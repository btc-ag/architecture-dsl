package com.btc.arch.base;

public interface IContext {
	String getParameter(String name);

	boolean hasParameter(String name);

	boolean hasAllParameters(String[] name);

	String getAbsoluteName(String name);

	IContext createSubContext(String subPrefix);

	String getParameter(String name, String defaultResult);
}
