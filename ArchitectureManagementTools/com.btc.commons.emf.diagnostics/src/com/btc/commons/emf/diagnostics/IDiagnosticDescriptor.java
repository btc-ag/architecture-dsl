package com.btc.commons.emf.diagnostics;

import com.btc.commons.emf.diagnostics.IDiagnosticResultBase.Severity;

public interface IDiagnosticDescriptor extends
		Comparable<IDiagnosticDescriptor> {
	String getID();

	/**
	 * Get a dynamic, but short id. This may change depending between runs,
	 * especially when configuration changes.
	 * 
	 * @return
	 */
	String getDynamicID();

	String getDescription();

	String getSubjectType();

	/**
	 * 
	 * @return
	 */
	Severity getBaseSeverity();

	String getDocumentationLink();
}
