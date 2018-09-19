package com.btc.commons.emf.diagnostics;

public interface IDiagnosticResult<T> extends IDiagnosticResultBase {
	@Override
	T getSubject();
}
