package com.btc.arch.architectureDsl.diagnostics;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.ArchitectureDslFactory;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.diagnostics.internal.ExtractedModuleDependencyValidatorInternal;
import com.btc.arch.architectureDsl.util.ArchitectureDslNameMapper;
import com.btc.arch.base.dependency.IDependencySource;
import com.btc.arch.diagnostics.api.Dependency;
import com.btc.commons.emf.diagnostics.IDiagnosticDescriptor;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;
import com.btc.commons.emf.diagnostics.ecore.EcoreDiagnosticSubjectDescriber;
import com.btc.commons.java.EndInserter;
import com.btc.commons.java.IInserter;
import com.btc.commons.java.Pair;
import com.btc.commons.java.StringUtils;
import com.btc.commons.java.functional.IterationUtils;

public class ExtractedModuleDependencyValidator implements
		IDiagnosticResultSource<Collection<? extends EObject>> {
	static final Logger logger = Logger
			.getLogger(ExtractedModuleDependencyValidator.class);

	private static final String DIAGNOSTIC_ID_MISSING = "com.btc.arch.ArchitectureDsl.diagnostics.missing";
	private static final String DIAGNOSTIC_ID_UNSPECIFIED = "com.btc.arch.ArchitectureDsl.diagnostics.unspecified";
	private static final String DIAGNOSTIC_ID_IGNORED = "com.btc.arch.ArchitectureDsl.diagnostics.ignoredTargets";
	private static final String DIAGNOSTIC_ID_EXTRA_DEPENDENCY = "com.btc.arch.ArchitectureDsl.diagnostics.extraDependencies";

	private final Iterable<IDependencySource> extractedDependencySources;
	private final IDiagnosticsRegistry diagnosticsRegistry;
	private final IDiagnosticResultFactory diagnosticResultFactory;

	public ExtractedModuleDependencyValidator(
			Iterable<? extends IDependencySource> dependencySources,
			IDiagnosticsRegistry diagnosticsRegistry) {
		this.extractedDependencySources = (Iterable<IDependencySource>) dependencySources;
		this.diagnosticsRegistry = diagnosticsRegistry;
		this.diagnosticResultFactory = diagnosticsRegistry
				.createDiagnosticResultFactory(EcoreDiagnosticSubjectDescriber
						.getDefault());
	}

	@Override
	public String getDescription() {
		return "Compares the extracted dependencies against the specified dependencies in the model, and reports any deviations";
	}

	@Override
	public Iterable<IDiagnosticDescriptor> getDiagnosticDescriptors() {
		List<IDiagnosticDescriptor> diagnosticDescriptors = new ArrayList<IDiagnosticDescriptor>(
				3);
		diagnosticDescriptors.add(this.diagnosticsRegistry
				.getDiagnosticDescriptor(DIAGNOSTIC_ID_EXTRA_DEPENDENCY));
		diagnosticDescriptors.add(this.diagnosticsRegistry
				.getDiagnosticDescriptor(DIAGNOSTIC_ID_IGNORED));
		diagnosticDescriptors.add(this.diagnosticsRegistry
				.getDiagnosticDescriptor(DIAGNOSTIC_ID_MISSING));
		diagnosticDescriptors.add(this.diagnosticsRegistry
				.getDiagnosticDescriptor(DIAGNOSTIC_ID_UNSPECIFIED));
		return diagnosticDescriptors;
	}

	@Override
	public Iterable<IDiagnosticResultBase> diagnose(
			final Collection<? extends EObject> baseElements,
			final Collection<? extends EObject> allElements) {
		final ExtractedModuleDependencyValidatorInternal internalProcessor = new ExtractedModuleDependencyValidatorInternal(
				baseElements, allElements, extractedDependencySources);

		final Iterable<Dependency> allIllegalDependencies = internalProcessor
				.getAllIllegalDependencies();
		final Collection<IDiagnosticResultBase> diagnosticResults = new ArrayList<IDiagnosticResultBase>();
		final IInserter<IDiagnosticResultBase> diagnosticsInserter = new EndInserter<IDiagnosticResultBase>(
				diagnosticResults);
		addModuleDiagnostics(diagnosticsInserter, internalProcessor);
		addModuleDependencyDiagnostics(diagnosticsInserter,
				allIllegalDependencies);
		return diagnosticResults;
	}

	private void addModuleDependencyDiagnostics(
			IInserter<IDiagnosticResultBase> diagnosticsInserter,
			Iterable<Dependency> allIllegalDependencies) {
		// TODO this could be done without creating intermediate Dependency
		// objects
		for (Dependency dependency : allIllegalDependencies) {
			diagnosticsInserter.add(this.diagnosticResultFactory
					.createDiagnosticResult(DIAGNOSTIC_ID_EXTRA_DEPENDENCY,
							new Pair<Module, Module>(dependency.getSource(),
									dependency.getTarget()), ""));
		}

	}

	private void addModuleDiagnostics(
			IInserter<IDiagnosticResultBase> diagnosticsInserter,
			ExtractedModuleDependencyValidatorInternal internalProcessor) {
		for (Module m : internalProcessor.getMissingSourceModules()) {
			// TODO use relative URI
			logger.debug(MessageFormat
					.format("Module {0} specified in Architecture DSL resource {1}, but no project definition file (.csproj, etc.) found",
							m.getName(), m.eResource() != null ? m.eResource()
									.getURI() : "<none>"));
			diagnosticsInserter
					.add(diagnosticResultFactory
							.createDiagnosticResult(
									DIAGNOSTIC_ID_MISSING,
									m,
									"Module specified in Architecture DSL, but no project definition file (.csproj, etc.) found"));
		}
		for (Module m : internalProcessor.getUnspecifiedSourceModules()) {
			// TODO it would be nice to include the path of the project
			// definition
			diagnosticsInserter
					.add(diagnosticResultFactory
							.createDiagnosticResult(
									DIAGNOSTIC_ID_UNSPECIFIED,
									m,
									"Module not specified in Architecture DSL, but project definition file (.csproj, etc.) found"));
		}
		if (internalProcessor.hasIgnoredTargetModules()) {
			Iterable<Module> ignored = internalProcessor
					.getIgnoredTargetModules();
			// TODO use correct model (or not the model at all)
			diagnosticsInserter
					.add(diagnosticResultFactory.createDiagnosticResult(
							DIAGNOSTIC_ID_IGNORED,
							ArchitectureDslFactory.eINSTANCE.createModel(),
							MessageFormat
									.format("These modules were ignored as least once as extracted dependency targets in Rule {1}: {0}",
											StringUtils.join(
													IterationUtils
															.materializeSorted(IterationUtils
																	.map(ignored,
																			new ArchitectureDslNameMapper())),
													", "),
											diagnosticsRegistry
													.getDiagnosticDescriptor(
															DIAGNOSTIC_ID_EXTRA_DEPENDENCY)
													.getDynamicID())));
		}
	}

}
