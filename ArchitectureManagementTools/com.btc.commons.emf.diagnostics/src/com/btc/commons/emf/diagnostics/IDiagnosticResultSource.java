package com.btc.commons.emf.diagnostics;

public interface IDiagnosticResultSource<BaseT> {
	Iterable<IDiagnosticResultBase> diagnose(BaseT baseElements, BaseT allElements);

	/**
	 * Returns the list of diagnostic descriptors for which this diagnostic
	 * result source produces results.
	 * 
	 * @return
	 */
	Iterable<IDiagnosticDescriptor> getDiagnosticDescriptors();

	/**
	 * Describe the diagnostic result source (rather than its invididual
	 * diagnostics.
	 * 
	 * TODO specify this in an extension point and supply via an
	 * IDiagnosticResultSourceDescriptor
	 * 
	 * @return
	 */
	String getDescription();
}
