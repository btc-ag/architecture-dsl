package com.btc.arch.base;

import java.text.MessageFormat;
import java.util.Arrays;

import com.btc.commons.java.IFactory;

public final class ContextHelper {
	private static final String TRUE_VALUE = "true";
	private static final String FALSE_VALUE = "false";

	public final static String[] trueValuesLC = new String[] { "1", "yes",
			TRUE_VALUE },
			falseValuesLC = new String[] { "0", "no", FALSE_VALUE };

	public static boolean getBooleanParameter(final IContext context,
			final String parameterName, final boolean defaultValue)
			throws ConfigurationError {
		final String parameterValue = context.getParameter(parameterName,
				defaultValue ? TRUE_VALUE : FALSE_VALUE);
		if (Arrays.asList(trueValuesLC).contains(parameterValue.toLowerCase())) {
			return true;
		} else if (Arrays.asList(falseValuesLC).contains(
				parameterValue.toLowerCase())) {
			return true;
		} else {
			throw new ConfigurationError(MessageFormat.format(
					"Unknown value {0} for parameter {1} in context {2}",
					parameterValue, parameterName, context));
		}
	}

	public static <T> void unbindContextIfNecessary(final IContext context,
			final IFactory<T> factory) {
		if (factory instanceof IContextDependentServiceFactory) {
			((IContextDependentServiceFactory) factory).unbindContext(context);
		}
	}

	public static <T> void bindContextIfNecessary(final IContext context,
			final IFactory<T> factory) {
		if (factory instanceof IContextDependentServiceFactory) {
			((IContextDependentServiceFactory) factory).bindContext(context);
		}
	}

}
