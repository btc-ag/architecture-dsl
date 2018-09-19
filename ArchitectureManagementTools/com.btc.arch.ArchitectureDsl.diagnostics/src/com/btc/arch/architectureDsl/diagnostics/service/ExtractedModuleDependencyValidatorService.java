package com.btc.arch.architectureDsl.diagnostics.service;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.diagnostics.Activator;
import com.btc.arch.architectureDsl.diagnostics.ExtractedModuleDependencyValidator;
import com.btc.arch.base.ConfigurationError;
import com.btc.arch.base.IContext;
import com.btc.arch.diagnostics.api.service.SimpleProcessingElementServiceBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;

public class ExtractedModuleDependencyValidatorService extends
		SimpleProcessingElementServiceBase {

	@Override
	protected IDiagnosticResultSource<Collection<? extends EObject>> createDiagnosticResultSource(
			IContext contextParameters,
			IDiagnosticsRegistry diagnosticsRegistry,
			IDiagnosticResultFactory diagnosticResultFactory)
			throws ConfigurationError {
		if (!ModelExtractorProcessingElementService
				.isUsingCombinedModel(contextParameters)) {
			return new ExtractedModuleDependencyValidator(Activator
					.getInstance().getDependencySources(), diagnosticsRegistry);

		} else
			return null;
	}

}
