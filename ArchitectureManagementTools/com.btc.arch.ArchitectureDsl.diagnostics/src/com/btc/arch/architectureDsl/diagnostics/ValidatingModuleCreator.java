package com.btc.arch.architectureDsl.diagnostics;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.util.CABStyleModuleNameSeparationStrategy;
import com.btc.arch.architectureDsl.util.IModuleCreator;
import com.btc.arch.architectureDsl.util.IModuleNameSeparationStrategy;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.commons.emf.diagnostics.IDiagnosticDescriptor;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;
import com.btc.commons.emf.diagnostics.ecore.EcoreDiagnosticSubjectDescriber;
import com.btc.commons.java.StringUtils;
import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.ToStringMapFunctor;

public class ValidatingModuleCreator implements IModuleCreator {

	private class MyDiagnosticResultSource implements
			IDiagnosticResultSource<Collection<? extends EObject>> {

		@Override
		public Iterable<IDiagnosticResultBase> diagnose(
				final Collection<? extends EObject> baseElements,
				final Collection<? extends EObject> allElements) {
			// TODO this is not nice, the arguments are not used
			return ValidatingModuleCreator.this.diagnosticResults;
		}

		@Override
		public Iterable<IDiagnosticDescriptor> getDiagnosticDescriptors() {
			return Collections
					.singletonList(ValidatingModuleCreator.this.diagnosticsRegistry
							.getDiagnosticDescriptor(DIAGNOSTIC_ID));
		}

		@Override
		public String getDescription() {
			return "Delivers collected problems from importing modules into an ArchDSL model";
		}

	}

	private static final String DIAGNOSTIC_ID = "com.btc.arch.architectureDsl.report.moduleImport";

	private final Collection<IDiagnosticResultBase> diagnosticResults = new ArrayList<IDiagnosticResultBase>();
	private final Collection<? extends EObject> allContents;
	private final IModuleCreator moduleCreator;
	private final Logger logger;
	private final IDiagnosticsRegistry diagnosticsRegistry;
	private final Iterable<String> externalModuleGroupPrefixes;

	private final IDiagnosticResultFactory diagnosticResultFactory;

	private final IModuleNameSeparationStrategy moduleNameSeparationStrategy;

	public ValidatingModuleCreator(
			final Collection<? extends EObject> allContents,
			final IModuleCreator moduleCreator,
			final IDiagnosticsRegistry diagnosticsRegistry,
			final Collection<String> externalModuleGroupPrefixes) {
		this.allContents = allContents;
		this.moduleCreator = moduleCreator;
		this.diagnosticsRegistry = diagnosticsRegistry;
		this.diagnosticResultFactory = diagnosticsRegistry
				.createDiagnosticResultFactory(EcoreDiagnosticSubjectDescriber
						.getDefault());
		this.logger = Logger.getLogger(this.getClass());
		// TODO make this parametrisable
		this.moduleNameSeparationStrategy = new CABStyleModuleNameSeparationStrategy();
		this.externalModuleGroupPrefixes = externalModuleGroupPrefixes;
	}

	@Override
	public Module createModule(final String moduleName,
			final Iterable<Module> moduleDependencies) {
		if (moduleName == null || moduleName.length() == 0) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Module name is empty (dependencies {0})", StringUtils
							.alternative(StringUtils.join(IterationUtils.map(
									moduleDependencies,
									new ToStringMapFunctor()), ","), "None")));
		}
		final Module module = this.moduleCreator.createModule(moduleName,
				moduleDependencies);
		final String expectedParentModuleGroupName = getExpectedModuleGroupName(moduleName);
		if (expectedParentModuleGroupName != null) {
			if ((ModelQueries.getModuleGroupByName(this.allContents,
					expectedParentModuleGroupName) == null)
					&& (!startsWithExternalModuleGroupPrefix(expectedParentModuleGroupName))) {
				final String message = MessageFormat
						.format("Expected parent module group {0} for module {1} not found; domain association may be wrong.",
								expectedParentModuleGroupName, moduleName);
				this.logger.warn(message);
				this.diagnosticResults
						.add(this.diagnosticResultFactory
								.createDiagnosticResult(DIAGNOSTIC_ID, module,
										message));
			}
		} else {
			final String message = MessageFormat
					.format("Expected module group for module {0} could not be determined",
							moduleName);
			this.logger.warn(message);
			this.diagnosticResults.add(this.diagnosticResultFactory
					.createDiagnosticResult(DIAGNOSTIC_ID, module, message));

		}
		return module;
	}

	private boolean startsWithExternalModuleGroupPrefix(String moduleGroupName) {
		for (String prefix : this.externalModuleGroupPrefixes) {
			if (moduleGroupName.startsWith(prefix))
				return true;
		}
		return false;
	}

	private String getExpectedModuleGroupName(final String moduleName) {
		// TODO this is CAB-style-specific needs to be parametrisable
		if (ModelQueries.getModuleGroupByName(this.allContents, moduleName) != null) {
			return moduleName;
		} else {
			return getParentModuleGroupName(moduleName);
		}
	}

	private String getParentModuleGroupName(final String moduleName) {
		final String[] parts = this.moduleNameSeparationStrategy
				.toNameParts(moduleName);
		if (parts.length > 1) {
			final List<String> parentParts = Arrays.asList(parts).subList(0,
					parts.length - 1);
			return this.moduleNameSeparationStrategy
					.toCompositeName(parentParts);
		} else {
			return null;
		}
	}

	public IDiagnosticResultSource<Collection<? extends EObject>> getDiagnosticResultSource() {
		return new MyDiagnosticResultSource();
	}

}
