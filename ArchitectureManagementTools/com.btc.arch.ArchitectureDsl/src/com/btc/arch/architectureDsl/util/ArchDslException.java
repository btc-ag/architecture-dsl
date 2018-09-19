package com.btc.arch.architectureDsl.util;

public class ArchDslException extends Exception {

	/**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID = 2703362282181199072L;

	public ArchDslException(Exception exception) {
		super(exception);
	}
	
	public ArchDslException(String message) {
		super(message);
	}
}
