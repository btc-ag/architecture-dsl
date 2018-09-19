package com.btc.commons.emf.diagnostics;

import com.btc.commons.java.Pair;

public class DefaultDiagnosticSubjectDescriber implements
		IDiagnosticSubjectDescriberChainElement {
	private IDiagnosticSubjectDescriber chainHead;

	public DefaultDiagnosticSubjectDescriber() {
		this.chainHead = this;
	}

	@Override
	public void setChain(IDiagnosticSubjectDescriber chainHead,
			IDiagnosticSubjectDescriber chainNext) {
		this.chainHead = chainHead;
	}

	@Override
	public String describeSubjectType(Object subject) {
		if (subject instanceof Pair) {
			Pair<?, ?> pair = (Pair<?, ?>) subject;
			return String
					.format("%s,%s", this.chainHead.describeSubjectType(pair.getFirst()), //$NON-NLS-1$
							this.chainHead
									.describeSubjectType(pair.getSecond()));
		} else
			return subject.getClass().getName();
	}

}
