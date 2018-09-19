package com.btc.arch.architectureDsl.util;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.btc.commons.java.StringUtils;

public class CABStyleModuleNameSeparationStrategy implements IModuleNameSeparationStrategy {
	private final static String MODULE_GROUP_SEPARATOR = ".";

	/* (non-Javadoc)
	 * @see com.btc.arch.architectureDsl.util.IModuleNameSeparationStrategy#getCompositeName(java.lang.Iterable)
	 */
	@Override
	public String toCompositeName(final Iterable<String> parentParts) {
		return StringUtils.join(parentParts, MODULE_GROUP_SEPARATOR);
	}

	/* (non-Javadoc)
	 * @see com.btc.arch.architectureDsl.util.IModuleNameSeparationStrategy#getCompositeName(java.lang.String[])
	 */
	@Override
	public String toCompositeName(final String[] parentParts) {
		return StringUtils.join(Arrays.asList(parentParts),
				MODULE_GROUP_SEPARATOR);
	}

	/* (non-Javadoc)
	 * @see com.btc.arch.architectureDsl.util.IModuleNameSeparationStrategy#getNameParts(java.lang.String)
	 */
	@Override
	public String[] toNameParts(final String moduleName) {
		return moduleName.split(Pattern.quote(MODULE_GROUP_SEPARATOR));
	}

}
