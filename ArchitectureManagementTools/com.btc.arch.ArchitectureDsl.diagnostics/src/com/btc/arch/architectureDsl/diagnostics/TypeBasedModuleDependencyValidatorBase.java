package com.btc.arch.architectureDsl.diagnostics;

import java.text.MessageFormat;

import org.apache.log4j.Logger;

import com.btc.arch.architectureDsl.Module;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;

public abstract class TypeBasedModuleDependencyValidatorBase<T> extends
		DefaultModuleDependencyValidatorBase {

	private final Logger logger = Logger.getLogger(getClass());

	protected abstract Iterable<T> getTypes(final Module module);

	protected abstract TypeDependencyRuleSet<T> selectTypeDependencyRules(
			Module source, Module target);

	public TypeBasedModuleDependencyValidatorBase(
			IDiagnosticResultFactory resultFactory) {
		super(resultFactory);
	}

	@Override
	protected boolean isSingleDependencyLegal(final Module source,
			final Module target) {
		assert source != null;
		assert target != null;
		if (!ignoreDependency(source, target)) {
			final TypeDependencyRuleSet<T> typeDependencyRuleSet = selectTypeDependencyRules(
					source, target);
			final Iterable<T> sourceTypes = getTypes(source);
			if (typeDependencyRuleSet.isDefined(sourceTypes)) {
				return typeDependencyRuleSet.isTypeDependencyLegal(sourceTypes,
						getTypes(target));
			} else {
				logger.warn(MessageFormat
						.format("No allowed target dependency types defined in rule set {3} for source module type {0} while checking dependency {1},{2}",
								sourceTypes, source.getName(),
								target.getName(),
								typeDependencyRuleSet.getRuleSetName()));
				return true;
			}
		} else
			return true;
	}

	protected boolean ignoreDependency(Module source, Module target) {
		// TODO this could be lifted to DefaultModuleDependencyValidatorBase
		if (ignoreSource(source) || ignoreTarget(target)) {
			return true;
		} else
			return false;
	}

	protected boolean ignoreTarget(Module target) {
		// TODO Does it make sense to make targets ignorable?
		return false;
	}

	protected boolean ignoreSource(Module source) {
		// TODO ignore by type (preferred) or name
		// TODO make this configurable
		return false;
	}

	@Override
	protected String getViolationExplanation(final Module source,
			final Module target) {
		final TypeDependencyRuleSet<T> typeDependencyRuleSet = selectTypeDependencyRules(
				source, target);
		return MessageFormat
				.format("Actual target module type is {0}, but allowed dependency types for source module type {3} are {1} (rule set {2})",
						getTypes(target), typeDependencyRuleSet
								.getAllowedTargetTypes(getTypes(source)),
						typeDependencyRuleSet.getRuleSetName(),
						getTypes(source));
	}
}