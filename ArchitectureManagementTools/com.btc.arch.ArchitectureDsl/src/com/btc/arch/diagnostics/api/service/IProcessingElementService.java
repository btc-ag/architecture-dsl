package com.btc.arch.diagnostics.api.service;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import com.btc.arch.diagnostics.api.DiagnosticsException;
import com.btc.arch.diagnostics.api.IDiagnosticResultSourceRegistry;
import com.btc.arch.base.ConfigurationError;
import com.btc.arch.base.IContext;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;

public interface IProcessingElementService {

	void doRegistrations(final Collection<? extends EObject> primaryContents,
			final Collection<? extends EObject> allContents,
			final IContext contextParameters,
			final IDiagnosticsRegistry diagnosticsRegistry,
			IDiagnosticResultSourceRegistry diagnosticResultSourceRegistry)
			throws DiagnosticsException, ConfigurationError;

}