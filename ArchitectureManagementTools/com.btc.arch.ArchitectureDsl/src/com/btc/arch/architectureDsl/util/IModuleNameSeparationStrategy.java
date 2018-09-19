package com.btc.arch.architectureDsl.util;

public interface IModuleNameSeparationStrategy {

	String toCompositeName(final Iterable<String> parentParts);

	String toCompositeName(final String[] parentParts);

	String[] toNameParts(final String moduleName);

}