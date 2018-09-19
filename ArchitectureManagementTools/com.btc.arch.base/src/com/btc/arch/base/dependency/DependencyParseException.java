package com.btc.arch.base.dependency;


public class DependencyParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1683871146610838508L;

	public DependencyParseException(Exception e) {
		super(e);
	}

	public DependencyParseException(String string, Exception e) {
		super(string, e);
	}

}
