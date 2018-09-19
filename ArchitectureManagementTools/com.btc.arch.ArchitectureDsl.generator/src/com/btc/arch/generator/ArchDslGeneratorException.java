package com.btc.arch.generator;

public class ArchDslGeneratorException extends Exception {

	public ArchDslGeneratorException (Exception exception){
		super(exception);
	}
	
	public ArchDslGeneratorException (String message){
		super(message);
	}
	
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -788933012815844508L;

}
