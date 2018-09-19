package com.btc.arch.diagnostics.api.service;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import com.btc.arch.diagnostics.api.DiagnosticsException;
import com.btc.arch.base.ConfigurationError;
import com.btc.arch.base.IContext;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;
import com.btc.commons.emf.diagnostics.ecore.DiagnosticsProcessor;

public interface IDiagnosticsProcessorServiceFactory {
	// TODO FIXME primaryContents/allContents are passed both here as well as in
	// DiagnosticsProcessor.diagnose... This may lead to inconsistencies, and
	// makes it impossible to wire differently filtered contents to different
	// parts of the diagnostics processor.

	DiagnosticsProcessor createDiagnosticsProcessor(
			final Collection<? extends EObject> primaryContents,
			final Collection<? extends EObject> allContents,
			final IContext contextParameters,
			final IDiagnosticsRegistry diagnosticsRegistry)
			throws DiagnosticsException, ConfigurationError;

}