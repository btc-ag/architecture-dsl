package com.btc.arch.ArchitectureDsl.imports;

public class ArchDslImportException extends Exception {

	/**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID = -4469725178282908543L;

	public ArchDslImportException(Exception exception) {
		super(exception);
	}

	public ArchDslImportException(String message) {
		super(message);
	}
}
