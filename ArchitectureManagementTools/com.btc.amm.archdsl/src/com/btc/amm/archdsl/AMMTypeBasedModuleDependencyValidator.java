package com.btc.amm.archdsl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.diagnostics.TypeBasedModuleDependencyValidatorBase;
import com.btc.arch.architectureDsl.diagnostics.TypeDependencyRuleSet;
import com.btc.arch.architectureDsl.util.CABStyleModuleNameSeparationStrategy;
import com.btc.arch.architectureDsl.util.IModuleNameSeparationStrategy;
import com.btc.arch.architectureDsl.util.ModuleQueries;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.java.CollectionUtils;
import com.btc.commons.java.Pair;

public class AMMTypeBasedModuleDependencyValidator extends
		TypeBasedModuleDependencyValidatorBase<AMMModuleType> {
	private final TypeDependencyRuleSet<AMMModuleType> sameDomainRules;
	private final TypeDependencyRuleSet<AMMModuleType> differentDomainsRules;

	public AMMTypeBasedModuleDependencyValidator(
			final IDiagnosticResultFactory diagnosticResultFactory,
			final String sameDomainRules, final String differentDomainsRules) {
		super(diagnosticResultFactory);
		this.moduleNameSeparationStrategy = new CABStyleModuleNameSeparationStrategy();
		this.sameDomainRules = new TypeDependencyRuleSet<AMMModuleType>(
				"Same Domain", AMMModuleType.ruleSetFromString(sameDomainRules));
		this.differentDomainsRules = new TypeDependencyRuleSet<AMMModuleType>(
				"Different Domains",
				AMMModuleType.ruleSetFromString(differentDomainsRules));
	}

	private final IModuleNameSeparationStrategy moduleNameSeparationStrategy;

	@Override
	protected Iterable<AMMModuleType> getTypes(final Module module) {
		final AMMModuleType type = getType(module);
		// temporarily return BLL_API + BLL for BLL
		if (type == AMMModuleType.BLL) {
			return Arrays.asList(AMMModuleType.BLL, AMMModuleType.BLL_API);
		} else {
			return Collections.singletonList(type);
		}
	}

	@SuppressWarnings("unchecked")
	private final static Pair<String, AMMModuleType>[] SIMPLE_NAME_TO_TYPE_MAP_PAIRS = new Pair[] {
			new Pair<String, AMMModuleType>("WebS", AMMModuleType.WebS),
			new Pair<String, AMMModuleType>("WinS", AMMModuleType.WinS),
			new Pair<String, AMMModuleType>("DTO", AMMModuleType.DTO),
			new Pair<String, AMMModuleType>("BLL", AMMModuleType.BLL),
			new Pair<String, AMMModuleType>("BLL_API", AMMModuleType.BLL_API),
			new Pair<String, AMMModuleType>("DAL", AMMModuleType.DAL),
			new Pair<String, AMMModuleType>("BO", AMMModuleType.BO), };
	private final static Map<String, AMMModuleType> SIMPLE_NAME_TO_TYPE_MAP = CollectionUtils
			.createMap(SIMPLE_NAME_TO_TYPE_MAP_PAIRS);

	private AMMModuleType getType(final Module module) {
		final String[] nameParts = this.moduleNameSeparationStrategy
				.toNameParts(module.getName());
		final String lastPart = nameParts[nameParts.length - 1];
		final AMMModuleType ammModuleType = SIMPLE_NAME_TO_TYPE_MAP
				.get(lastPart);
		return ammModuleType != null ? ammModuleType : AMMModuleType.Other;
	}

	@Override
	protected TypeDependencyRuleSet<AMMModuleType> selectTypeDependencyRules(
			Module source, Module target) {
		// TODO implement this using a Chain of Responsibility
		final Domain sourceEffectiveDomain = ModuleQueries
				.getEffectiveDomain(source);
		if (sourceEffectiveDomain != null
				&& sourceEffectiveDomain.equals(ModuleQueries
						.getEffectiveDomain(target))) {
			return this.sameDomainRules;
		} else {
			return this.differentDomainsRules;
		}
	}

	@Override
	public String getDescription() {
		return "Checks model-specified module dependencies for conformance with the AMM module type rules";
	}

	@Override
	public String getDiagnosticsId() {
		return "com.btc.amm.archdsl.ammModuleType";
	}

	@Override
	protected boolean ignoreSource(final Module source) {
		return getType(source) == AMMModuleType.Other
				|| source.getName().startsWith("BTC.AMM.Test");
	}

}
