package com.btc.commons.emf.diagnostics;

/**
 * TODO Support dynamic registration and unregistration of diagnostics, which
 * requires a notification mechanism for users of diagnostics descriptors.
 * 
 * @author SIGIESEC
 * 
 */
public interface IDiagnosticsRegistry {

	IDiagnosticDescriptor getDiagnosticDescriptor(String identifier);

	IDiagnosticResultFactory createDiagnosticResultFactory(
			IDiagnosticSubjectDescriber describer);

}
