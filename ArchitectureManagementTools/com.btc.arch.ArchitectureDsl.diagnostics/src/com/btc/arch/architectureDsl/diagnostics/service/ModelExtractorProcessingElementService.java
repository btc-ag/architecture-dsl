package com.btc.arch.architectureDsl.diagnostics.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.diagnostics.Activator;
import com.btc.arch.architectureDsl.diagnostics.ValidatingModuleCreator;
import com.btc.arch.architectureDsl.util.DefaultModuleCreator;
import com.btc.arch.architectureDsl.util.DependencySourceImporter;
import com.btc.arch.architectureDsl.util.ModelBuilder;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.arch.base.ConfigurationError;
import com.btc.arch.base.ContextHelper;
import com.btc.arch.base.IContext;
import com.btc.arch.base.dependency.DependencyParseException;
import com.btc.arch.base.dependency.IDependencySource;
import com.btc.arch.diagnostics.api.DiagnosticsException;
import com.btc.arch.diagnostics.api.IDiagnosticResultSourceRegistry;
import com.btc.arch.diagnostics.api.service.IProcessingElementService;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;

public class ModelExtractorProcessingElementService implements
		IProcessingElementService {
	private static final String USE_COMBINED_MODEL = "Combine";

	private static final String EXTERNAL_MODULE_GROUP_PREFIXES_PARAMETER = "ExternalModuleGroupPrefixes";
	private static final String EXTERNAL_MODULE_GROUP_PREFIXES_SEPARATOR = ",";

	private final Logger logger;

	public ModelExtractorProcessingElementService() {
		this.logger = Logger.getLogger(this.getClass());
	}

	@Deprecated
	public static boolean isUsingCombinedModel(final IContext contextParameters)
			throws ConfigurationError {
		// TODO this can be removed, since it is now possible to supply a list
		// of processing element services, the sole purpose of this flag was to
		// enable/disable this service
		return ContextHelper.getBooleanParameter(contextParameters,
				USE_COMBINED_MODEL, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.btc.arch.architectureDsl.diagnostics.service.IProcessingElementService
	 * #doRegistrations(java.util.Collection, java.util.Collection,
	 * com.btc.arch.base.IContext,
	 * com.btc.commons.emf.diagnostics.IDiagnosticsRegistry,
	 * com.btc.arch.architectureDsl.diagnostics.IDiagnosticResultSourceRegistry)
	 */
	@Override
	public void doRegistrations(
			final Collection<? extends EObject> primaryContents,
			final Collection<? extends EObject> allContents,
			final IContext contextParameters,
			final IDiagnosticsRegistry diagnosticsRegistry,
			final IDiagnosticResultSourceRegistry diagnosticResultSourceRegistry)
			throws DiagnosticsException, ConfigurationError {
		final Collection<String> externalModuleGroupPrefixes = getExternalModuleGroupPrefixes(contextParameters);
		final ValidatingModuleCreator validatingModuleCreator = new ValidatingModuleCreator(
				allContents, new DefaultModuleCreator(), diagnosticsRegistry,
				externalModuleGroupPrefixes);
		final List<Model> models;
		models = ModelQueries.findModels(primaryContents);
		if (models.isEmpty()) {
			throw new DiagnosticsException("No input models found!");
		}
		final DependencySourceImporter importer = new DependencySourceImporter();
		try {
			for (final IDependencySource dependencySource : Activator
					.getInstance().getDependencySources()) {
				importer.createModel(new ModelBuilder(validatingModuleCreator,
						models.get(0), ModelQueries.findModels(allContents)),
						dependencySource);
			}
		} catch (final DependencyParseException e) {
			throw new DiagnosticsException(e);
		}
		// very low priority to ensure that the import has happened before the
		// diagnostic is processed
		diagnosticResultSourceRegistry.registerDiagnosticResultSource(
				validatingModuleCreator.getDiagnosticResultSource(), -1000000);
	}

	private Collection<String> getExternalModuleGroupPrefixes(
			IContext contextParameters) {
		Collection<String> prefixCollection = new ArrayList<String>();
		String prefixesParameter = contextParameters
				.getParameter(EXTERNAL_MODULE_GROUP_PREFIXES_PARAMETER);
		if (prefixesParameter != null && !prefixesParameter.equals("")) {
			String[] prefixes = prefixesParameter
					.split(EXTERNAL_MODULE_GROUP_PREFIXES_SEPARATOR);
			for (String string : prefixes)
				prefixCollection.add(string);
		}
		this.logger.info("The configuration parameter "
				+ EXTERNAL_MODULE_GROUP_PREFIXES_PARAMETER
				+ " could not be found.");
		return prefixCollection;
	}
}
