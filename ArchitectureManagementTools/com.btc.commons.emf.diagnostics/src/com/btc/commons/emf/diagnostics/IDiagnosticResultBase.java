package com.btc.commons.emf.diagnostics;

import java.util.Date;

public interface IDiagnosticResultBase extends
		Comparable<IDiagnosticResultBase> {
	public enum Severity {
		UNCHECKED, OK, INFO, WARNING, ERROR, FATAL
	}

	Object getSubject();

	/**
	 * Returns the name of the model element type, or a comma-separated list of
	 * model element type of the subject, e.g. "Module" or "Module,Module".
	 * 
	 * TODO evtl. eine eigene Klasse hierfür definieren?
	 * 
	 * @return
	 */
	String getSubjectType();

	IDiagnosticDescriptor getDiagnostic();

	String getExplanation();

	int getWeight();

	Date getDate();

	void setDate(Date date);

}
