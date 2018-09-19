package com.btc.arch.diagnostics.api;

import com.btc.arch.architectureDsl.Module;

final public class Dependency implements Comparable<Dependency> {
	// TODO Soll es (auch hier) eine Schwere (Severity) einer Regelverletzung
	// geben? Wäre
	// vermutlich ganz sinnvoll.

	private final Module source, target;
	private final State state;

	public enum State {
		LEGAL, ILLEGAL_BY_CUSTOM_RULE
	};

	public Dependency(final Module source, final Module target,
			final boolean isLegal) {
		this.source = source;
		this.target = target;
		this.state = isLegal ? State.LEGAL : State.ILLEGAL_BY_CUSTOM_RULE;
	}

	public String getTargetName() {
		return target.getName();
	}

	public String getSourceName() {
		return source.getName();
	}

	public Module getTarget() {
		return target;
	}

	public Module getSource() {
		return source;
	}

	public boolean isLegal() {
		return state == State.LEGAL;
	}

	@Deprecated
	public String getStateDescription() {
		switch (state) {
		case LEGAL:
			return "Legal dependency";
			// case UNCHECKED:
			// return "Not checked for legality";
		case ILLEGAL_BY_CUSTOM_RULE:
			return "Illegal dependency";
		}
		return "Unknown state (implementation error)";
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		s.append(getSource() + " -> " + getTarget() + ": "
				+ getStateDescription());

		return s.toString();
	}

	@Override
	public int compareTo(final Dependency o2) {
		final int sourceCmp = this.getSource().getName()
				.compareTo(o2.getSource().getName());
		if (sourceCmp == 0) {
			final int targetCmp = this.getTarget().getName()
					.compareTo(o2.getTarget().getName());
			if (targetCmp == 0)
				return new Boolean(isLegal()).compareTo(o2.isLegal());
			else
				return targetCmp;
		} else
			return sourceCmp;
	}

}
