package com.btc.arch.architectureDsl.diagnostics.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.diagnostics.Activator;
import com.btc.arch.base.ConfigurationError;
import com.btc.arch.base.ContextHelper;
import com.btc.arch.base.IContext;
import com.btc.arch.base.IServiceRegistry;
import com.btc.arch.base.dependency.DependencySourceServiceFactoryBundle;
import com.btc.arch.base.dependency.DependencySourceServiceFactoryBundle.DependencySourceServiceFactoryInfo;
import com.btc.arch.base.dependency.IDependencySource;
import com.btc.arch.diagnostics.api.DiagnosticsException;
import com.btc.arch.diagnostics.api.IDiagnosticResultSourceRegistry;
import com.btc.arch.diagnostics.api.service.IDiagnosticsProcessorServiceFactory;
import com.btc.arch.diagnostics.api.service.IProcessingElementService;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;
import com.btc.commons.emf.diagnostics.ecore.DiagnosticsProcessor;
import com.btc.commons.java.IFactory;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IterationUtils;

public class DefaultDiagnosticsProcessorServiceFactory implements
		IDiagnosticsProcessorServiceFactory {
	// TODO separate into service factory which uses context and non-service
	// factory w/o context?

	public static final String EXTRACTION_SUBCONTEXT_NAME = "Extract";
	private final Logger logger = Logger.getLogger(getClass());

	public DefaultDiagnosticsProcessorServiceFactory() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.btc.arch.architectureDsl.diagnostics.IDiagnosticsProcessorServiceFactory
	 * #createValidModelDiagnosticsProcessor(java.util.Collection,
	 * java.util.Collection, com.btc.arch.base.IContext,
	 * com.btc.commons.emf.diagnostics.IDiagnosticsRegistry)
	 */
	@Override
	public DiagnosticsProcessor createDiagnosticsProcessor(
			final Collection<? extends EObject> primaryContents,
			final Collection<? extends EObject> allContents,
			final IContext contextParameters,
			final IDiagnosticsRegistry diagnosticsRegistry)
			throws DiagnosticsException, ConfigurationError {
		final IDependencySource dependencySource = getExtractionDependencySource(contextParameters
				.createSubContext(EXTRACTION_SUBCONTEXT_NAME));
		Activator.getInstance().registerDependencySource(dependencySource);

		final IDiagnosticResultSourceRegistry diagnosticResultSourceRegistry = Activator
				.getInstance().getDiagnosticResultSourceRegistry();
		registerDiagnosticResultSources(primaryContents, allContents,
				contextParameters, diagnosticsRegistry,
				diagnosticResultSourceRegistry);

		final DiagnosticsProcessor processor;
		processor = new DiagnosticsProcessor();
		for (final IDiagnosticResultSource<Collection<? extends EObject>> resultSource : diagnosticResultSourceRegistry
				.getDiagnosticResultSources()) {
			processor.addDiagnosticsResultSource(resultSource);
		}
		this.logger.info(MessageFormat.format(
				"Using effective diagnostics processor:\n{0}",
				processor.getDescription()));
		return processor;
	}

	private void registerDiagnosticResultSources(
			final Collection<? extends EObject> primaryContents,
			final Collection<? extends EObject> allContents,
			final IContext contextParameters,
			final IDiagnosticsRegistry diagnosticsRegistry,
			final IDiagnosticResultSourceRegistry diagnosticResultSourceRegistry)
			throws DiagnosticsException, ConfigurationError {
		final Pair<String, IContext>[] configuredServiceInfos = getConfiguredProcessingElementServiceIDs(contextParameters);
		final IServiceRegistry<IProcessingElementService> processingElementExtensionRegistry = com.btc.arch.ArchDslBundle
				.getInstance().getProcessingElementExtensionRegistry();
		final Set<IProcessingElementService> allServices = IterationUtils
				.materialize(
						processingElementExtensionRegistry.getAllServices(),
						new HashSet<IProcessingElementService>()), configuredServices = new HashSet<IProcessingElementService>();
		for (final Pair<String, IContext> configuredServiceInfo : configuredServiceInfos) {
			final IProcessingElementService service = processConfiguredService(
					primaryContents, allContents, diagnosticsRegistry,
					diagnosticResultSourceRegistry,
					processingElementExtensionRegistry,
					configuredServiceInfo.getFirst(),
					configuredServiceInfo.getSecond());
			if (service != null) {
				configuredServices.add(service);
			}
		}
		this.logger.info(MessageFormat.format(
				"{0} of {1} processing element services configured",
				configuredServices.size(), allServices.size()));
	}

	private IProcessingElementService processConfiguredService(
			final Collection<? extends EObject> primaryContents,
			final Collection<? extends EObject> allContents,
			final IDiagnosticsRegistry diagnosticsRegistry,
			final IDiagnosticResultSourceRegistry diagnosticResultSourceRegistry,
			final IServiceRegistry<IProcessingElementService> processingElementExtensionRegistry,
			final String serviceID, final IContext context)
			throws DiagnosticsException, ConfigurationError {
		final IProcessingElementService service = processingElementExtensionRegistry
				.getServiceByID(serviceID);
		if (service != null) {
			this.logger
					.debug(MessageFormat
							.format("Processing element service with id {0} (class {2}) and context {1}",
									serviceID, context, service.getClass()
											.getName()));
			service.doRegistrations(primaryContents, allContents, context,
					diagnosticsRegistry, diagnosticResultSourceRegistry);
		} else {
			this.logger
					.error(MessageFormat
							.format("Configured processing element service with id {0} not found",
									serviceID));
		}
		return service;
	}

	public final static String CONFIGURED_PROCESSING_ELEMENT_SERVICE_IDS = "ProcessingElements";

	private Pair<String, IContext>[] getConfiguredProcessingElementServiceIDs(
			final IContext contextParameters) throws ConfigurationError {
		final List<Pair<String, IContext>> result = new ArrayList<Pair<String, IContext>>();
		final String raw = contextParameters
				.getParameter(CONFIGURED_PROCESSING_ELEMENT_SERVICE_IDS);
		if (raw != null) {
			final String[] elements = raw.split(Pattern.quote(","));
			for (final String element : elements) {
				final String[] serviceSpecParts = element.split(Pattern
						.quote("="));
				switch (serviceSpecParts.length) {
				case 1:
					// service specification without individual short name,
					// using id as name
					result.add(new Pair<String, IContext>(serviceSpecParts[0],
							contextParameters
									.createSubContext(serviceSpecParts[0])));
					break;
				case 2:
					// service specification with individual short name
					result.add(new Pair<String, IContext>(serviceSpecParts[0],
							contextParameters
									.createSubContext(serviceSpecParts[1])));
					break;
				default:
					throw new ConfigurationError(MessageFormat.format(
							"Invalid processing element configuration: {0}",
							element));
				}
			}
			return result.toArray(new Pair[result.size()]);
		} else {
			throw new ConfigurationError(
					MessageFormat
							.format("No configured processing elements found, add missing key {0} in context {1}",
									CONFIGURED_PROCESSING_ELEMENT_SERVICE_IDS,
									contextParameters));
		}

	}

	private IDependencySource getExtractionDependencySource(
			final IContext subContext) throws ConfigurationError {
		String languageParameter = subContext.getParameter("Language");
		Iterable<DependencySourceServiceFactoryInfo> dependencySourceServiceFactoryInfos = DependencySourceServiceFactoryBundle
				.getInstance().getDependencySourceServiceFactoryInfos();
		IFactory<IDependencySource> dependencySourceServiceFactory = null;
		if (languageParameter == null)
			languageParameter = "NET";
		// TODO: It should be checked that there is only on
		// DependencySourceServiceFactoryInfo for the current languageParameter
		for (DependencySourceServiceFactoryInfo dependencySourceServiceFactoryInfo : dependencySourceServiceFactoryInfos) {
			if (dependencySourceServiceFactoryInfo.getProgrammingLanguage()
					.equals(languageParameter))
				dependencySourceServiceFactory = dependencySourceServiceFactoryInfo
						.getExecutableExtension();
		}
		if (dependencySourceServiceFactory == null)
			throw new ConfigurationError(
					MessageFormat
							.format("No DependencySourceServiceFactory can be found for language {0}",
									languageParameter));

		ContextHelper.bindContextIfNecessary(subContext,
				dependencySourceServiceFactory);
		final IDependencySource dependencySource = dependencySourceServiceFactory
				.create();
		ContextHelper.unbindContextIfNecessary(subContext,
				dependencySourceServiceFactory);
		return dependencySource;
	}
}
