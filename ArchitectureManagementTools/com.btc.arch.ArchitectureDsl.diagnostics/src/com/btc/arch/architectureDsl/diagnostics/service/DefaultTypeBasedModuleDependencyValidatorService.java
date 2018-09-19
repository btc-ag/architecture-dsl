package com.btc.arch.architectureDsl.diagnostics.service;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.diagnostics.DefaultTypeBasedModuleDependencyValidator;
import com.btc.arch.base.ConfigurationError;
import com.btc.arch.base.IContext;
import com.btc.arch.diagnostics.api.service.SimpleProcessingElementServiceBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;

public class DefaultTypeBasedModuleDependencyValidatorService extends
		SimpleProcessingElementServiceBase {

	@Override
	protected IDiagnosticResultSource<Collection<? extends EObject>> createDiagnosticResultSource(
			IContext contextParameters,
			IDiagnosticsRegistry diagnosticsRegistry,
			IDiagnosticResultFactory diagnosticResultFactory)
			throws ConfigurationError {
		return new DefaultTypeBasedModuleDependencyValidator(
				diagnosticResultFactory);
	}

}
