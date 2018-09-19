package com.btc.arch.diagnostics.api;

public class DiagnosticsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1415964636702274439L;

	public DiagnosticsException(Exception exception) {
		super(exception);
	}

	public DiagnosticsException(String message) {
		super(message);
	}

	public DiagnosticsException(String message, Exception exception) {
		super(message, exception);
	}

}
