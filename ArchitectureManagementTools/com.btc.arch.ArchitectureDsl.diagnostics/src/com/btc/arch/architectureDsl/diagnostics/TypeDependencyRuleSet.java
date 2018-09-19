package com.btc.arch.architectureDsl.diagnostics;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.btc.commons.java.CollectionUtils;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.MapLookupMapFunctor;

public class TypeDependencyRuleSet<T> {
	private final Map<T, Iterable<T>> typeDependencyRules;
	private final String ruleSetName;

	public TypeDependencyRuleSet(final String ruleSetName,
			final Pair<T, T[]>[] typeRules) {
		this(ruleSetName, CollectionUtils.createSetValuedMap(typeRules));
	}

	@SuppressWarnings("unchecked")
	public TypeDependencyRuleSet(final String ruleSetName,
			final Map<T, ? extends Iterable<T>> typeRules) {
		this.typeDependencyRules = (Map<T, Iterable<T>>) typeRules;
		this.ruleSetName = ruleSetName;
	}

	public boolean isTypeDependencyLegal(final Iterable<T> sourceTypes,
			final Iterable<T> targetTypes) {
		final Set<T> allowedTargetTypes = getAllowedTargetTypes(sourceTypes);

		return CollectionUtils.containsAny(allowedTargetTypes, targetTypes);
	}

	public boolean isDefined(final Iterable<T> sourceTypes) {
		return CollectionUtils.containsAny(this.typeDependencyRules.keySet(),
				sourceTypes);
	}

	public HashSet<T> getAllowedTargetTypes(final Iterable<T> sourceTypes) {
		return IterationUtils.materialize(IterationUtils
				.mapToIterablesAndChain(sourceTypes,
						new MapLookupMapFunctor<T, Iterable<T>>(
								this.typeDependencyRules)), new HashSet<T>());
	}

	/**
	 * Relaxes the rules in place.
	 * 
	 * @param additionalRules
	 * @return
	 */
	public TypeDependencyRuleSet<T> relax(final Pair<T, T[]>[] additionalRules) {
		// TODO change semantics auch that a new rule set is returned!
		for (final Pair<T, T[]> newAllowedDependencies : additionalRules) {
			if (this.typeDependencyRules.containsKey(newAllowedDependencies
					.getFirst())) {
				final Set<T> allowedTargetTypes = new HashSet<T>();
				CollectionUtils.addAll(allowedTargetTypes,
						this.typeDependencyRules.get(newAllowedDependencies
								.getFirst()));
				CollectionUtils.addAll(allowedTargetTypes,
						Arrays.asList(newAllowedDependencies.getSecond()));
			} else {
				this.typeDependencyRules.put(
						newAllowedDependencies.getFirst(),
						new HashSet<T>(Arrays.asList(newAllowedDependencies
								.getSecond())));
			}
		}
		return this;
	}

	public String getRuleSetName() {
		return this.ruleSetName;
	}

}
