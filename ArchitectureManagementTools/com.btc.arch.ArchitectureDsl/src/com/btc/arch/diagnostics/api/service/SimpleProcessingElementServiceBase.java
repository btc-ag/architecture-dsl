package com.btc.arch.diagnostics.api.service;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import com.btc.arch.diagnostics.api.DiagnosticsException;
import com.btc.arch.diagnostics.api.IDiagnosticResultSourceRegistry;
import com.btc.arch.base.ConfigurationError;
import com.btc.arch.base.IContext;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;
import com.btc.commons.emf.diagnostics.ecore.EcoreDiagnosticSubjectDescriber;

public abstract class SimpleProcessingElementServiceBase implements
		IProcessingElementService {

	@Override
	public void doRegistrations(Collection<? extends EObject> primaryContents,
			Collection<? extends EObject> allContents,
			IContext contextParameters,
			IDiagnosticsRegistry diagnosticsRegistry,
			IDiagnosticResultSourceRegistry diagnosticResultSourceRegistry)
			throws DiagnosticsException, ConfigurationError {
		final IDiagnosticResultSource<Collection<? extends EObject>> diagnosticResultSource = createDiagnosticResultSource(
				contextParameters,
				diagnosticsRegistry,
				diagnosticsRegistry
						.createDiagnosticResultFactory(EcoreDiagnosticSubjectDescriber
								.getDefault()));
		if (diagnosticResultSource != null)
			diagnosticResultSourceRegistry
					.registerDiagnosticResultSource(diagnosticResultSource);
	}

	abstract protected IDiagnosticResultSource<Collection<? extends EObject>> createDiagnosticResultSource(
			IContext contextParameters,
			IDiagnosticsRegistry diagnosticsRegistry,
			IDiagnosticResultFactory diagnosticResultFactory)
			throws ConfigurationError;

}
