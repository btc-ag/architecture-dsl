package com.btc.arch.base.dependency;

public interface IDependencyParser extends IDependencySource {

	String getRawModuleName() throws DependencyParseException;

}