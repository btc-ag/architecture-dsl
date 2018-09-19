package com.btc.arch.base;

import java.util.Map;
import java.util.Map.Entry;

import com.btc.commons.java.StringUtils;
import com.btc.commons.java.functional.IUnaryPredicate;
import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.ToStringMapFunctor;

public class Context implements IContext {

	public static final String SEPARATOR = ".";

	private final Map<Object, Object> parameters;
	private final String prefix;

	public Context(Map<Object, Object> parameters) {
		this(parameters, null);
	}

	public Context(Map<Object, Object> parameters, String prefix) {
		this.parameters = parameters;
		this.prefix = prefix;
	}

	protected Context(Context parent, String subPrefix) {
		this.parameters = parent.getParameters();
		this.prefix = parent.getPrefix() != null ? parent.getPrefix()
				+ subPrefix + SEPARATOR : subPrefix + SEPARATOR;
	}

	@Override
	public IContext createSubContext(String subPrefix) {
		return new Context(this, subPrefix);
	}

	private String getPrefix() {
		return this.prefix;
	}

	private Map<Object, Object> getParameters() {
		return this.parameters;
	}

	@Override
	public String getParameter(String name) {
		String defaultResult = null;
		return getParameter(name, defaultResult);
	}

	@Override
	public String getParameter(String name, String defaultResult) {
		final Object parameter = parameters.get(getAbsoluteName(name));
		return parameter != null ? parameter.toString() : defaultResult;
	}

	@Override
	public String getAbsoluteName(String name) {
		return this.prefix != null ? this.prefix + name : name;
	}

	@Override
	public String toString() {
		return String.format("%s(parameters=%s, prefix=%s)", this.getClass()
				.getName(), StringUtils.join(IterationUtils.map(IterationUtils
				.filter(parameters.entrySet(),
						new IUnaryPredicate<Map.Entry<Object, Object>>() {

							@Override
							public boolean evaluate(Entry<Object, Object> obj) {
								return obj.getKey().toString()
										.startsWith(prefix);
							}
						}), new ToStringMapFunctor()), ", "), this.prefix);
	}

	@Override
	public boolean hasParameter(String name) {
		return parameters.containsKey(getAbsoluteName(name));
	}

	@Override
	public boolean hasAllParameters(String[] names) {
		for (String name : names) {
			if (!hasParameter(name))
				return false;
		}
		return true;
	}

}
