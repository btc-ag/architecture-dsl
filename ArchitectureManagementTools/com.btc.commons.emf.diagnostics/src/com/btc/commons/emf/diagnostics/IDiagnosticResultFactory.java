package com.btc.commons.emf.diagnostics;

public interface IDiagnosticResultFactory {
	IDiagnosticResultBase createDiagnosticResult(String diagnostic,
			Object subject, String explanation);

}
