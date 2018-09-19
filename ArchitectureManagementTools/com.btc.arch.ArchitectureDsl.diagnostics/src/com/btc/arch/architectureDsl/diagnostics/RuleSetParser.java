package com.btc.arch.architectureDsl.diagnostics;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IMapFunctor;

public class RuleSetParser<T> {

	private static final String SOURCE_TARGET_SEP = "->";
	private static final String RULE_SEP = ";";
	private static final String TYPE_SEP = ",";
	private final IMapFunctor<String, T> typeMapFunctor;

	public RuleSetParser(final IMapFunctor<String, T> typeMapFunctor) {
		this.typeMapFunctor = typeMapFunctor;
	}

	public Pair<T, T[]>[] parse(final String ruleSetString) {
		final List<Pair<T, T[]>> result = new ArrayList<Pair<T, T[]>>();
		final String[] rules = ruleSetString.split(Pattern.quote(RULE_SEP));
		for (final String rule : rules) {
			final String[] parts = rule.split(Pattern.quote(SOURCE_TARGET_SEP));
			if (parts.length != 2) {
				throw new IllegalArgumentException(
						MessageFormat.format(
								"Illegal rule {0} in rule set {1}", rule,
								ruleSetString));
			} else {
				final T srcType = this.typeMapFunctor.mapItem(parts[0]);
				if (srcType == null) {
					throw new IllegalArgumentException(MessageFormat.format(
							"Unknown source module type {0} in rule {1}",
							parts[0], rule));
				}
				final String[] targets = parts[1]
						.split(Pattern.quote(TYPE_SEP));
				final List<T> tgtTypes = new ArrayList<T>();
				for (final String target : targets) {
					final T tgtType = this.typeMapFunctor.mapItem(target);
					if (tgtType == null) {
						throw new IllegalArgumentException(
								MessageFormat
										.format("Unknown target module type {0} in rule {1}",
												target, rule));
					} else {
						tgtTypes.add(tgtType);
					}

				}
				result.add(new Pair<T, T[]>(srcType, (T[]) tgtTypes.toArray()));
			}
		}
		return result.toArray(new Pair[result.size()]);
	}

}