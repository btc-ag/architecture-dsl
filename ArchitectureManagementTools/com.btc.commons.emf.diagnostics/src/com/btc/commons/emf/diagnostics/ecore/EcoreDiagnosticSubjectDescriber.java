package com.btc.commons.emf.diagnostics.ecore;

import org.eclipse.emf.ecore.EObject;

import com.btc.commons.emf.diagnostics.DefaultDiagnosticSubjectDescriber;
import com.btc.commons.emf.diagnostics.DiagnosticSubjectDescriberChain;
import com.btc.commons.emf.diagnostics.IDiagnosticSubjectDescriber;
import com.btc.commons.emf.diagnostics.IDiagnosticSubjectDescriberChainElement;

/**
 * 
 * TODO this could be reused outside the ArchitectureDSL context.
 * 
 * @author SIGIESEC
 * 
 */
public class EcoreDiagnosticSubjectDescriber implements
		IDiagnosticSubjectDescriberChainElement {
	private static IDiagnosticSubjectDescriber defaultDescriber = new DiagnosticSubjectDescriberChain(
			new IDiagnosticSubjectDescriberChainElement[] {
					new EcoreDiagnosticSubjectDescriber(),
					new DefaultDiagnosticSubjectDescriber() });
	private IDiagnosticSubjectDescriber chainNext;

	private EcoreDiagnosticSubjectDescriber() {
	}

	@Override
	public void setChain(final IDiagnosticSubjectDescriber chainHead,
			final IDiagnosticSubjectDescriber chainNext) {
		this.chainNext = chainNext;
	}

	@Override
	public String describeSubjectType(final Object subject) {
		if (subject instanceof EObject) {
			final EObject eObject = (EObject) subject;
			return eObject.eClass().getName();
		} else {
			if (chainNext != null)
				return chainNext.describeSubjectType(subject);
			else
				return null;
		}

	}

	public static IDiagnosticSubjectDescriber getDefault() {
		return defaultDescriber;
	}
}
