package com.btc.commons.emf.diagnostics;


public class DiagnosticSubjectDescriberChain implements
		IDiagnosticSubjectDescriber {

	private final IDiagnosticSubjectDescriberChainElement[] diagnosticSubjectDescribers;

	public DiagnosticSubjectDescriberChain(
			IDiagnosticSubjectDescriberChainElement[] diagnosticSubjectDescribers) {
		this.diagnosticSubjectDescribers = diagnosticSubjectDescribers;
		initializeChain();
	}

	private void initializeChain() {
		for (int i = 0; i < diagnosticSubjectDescribers.length - 1; i++) {
			diagnosticSubjectDescribers[i].setChain(this,
					diagnosticSubjectDescribers[i + 1]);
		}
		diagnosticSubjectDescribers[diagnosticSubjectDescribers.length - 1]
				.setChain(this, null);
	}

	@Override
	public String describeSubjectType(Object subject) {
		return this.diagnosticSubjectDescribers[0].describeSubjectType(subject);
	}

}
