package com.btc.arch.visualstudio;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class MSNamespaceContext implements NamespaceContext {
	@Override
	public String getNamespaceURI(String prefix) {
		if (prefix.equals("ms"))
			return "http://schemas.microsoft.com/developer/msbuild/2003";
		else
			return XMLConstants.NULL_NS_URI;
	}

	@Override
	public String getPrefix(String namespace) {
		if (namespace
				.equals("http://schemas.microsoft.com/developer/msbuild/2003"))
			return "ms";
		else
			return null;
	}

	@Override
	public Iterator<?> getPrefixes(String namespace) {
		throw new RuntimeException("Not implemented");
	}

}