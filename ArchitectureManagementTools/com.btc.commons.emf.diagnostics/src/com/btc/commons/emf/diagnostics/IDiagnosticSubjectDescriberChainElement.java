package com.btc.commons.emf.diagnostics;


public interface IDiagnosticSubjectDescriberChainElement extends
		IDiagnosticSubjectDescriber {
	void setChain(IDiagnosticSubjectDescriber chainHead,
			IDiagnosticSubjectDescriber chainNext);
}
