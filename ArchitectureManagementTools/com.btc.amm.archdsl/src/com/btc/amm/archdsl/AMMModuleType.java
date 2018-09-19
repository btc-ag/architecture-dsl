package com.btc.amm.archdsl;

import java.util.Map;

import com.btc.arch.architectureDsl.diagnostics.RuleSetParser;
import com.btc.commons.java.CollectionUtils;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IMapFunctor;

public enum AMMModuleType {
	WebS, WinS, DTO, BLL_API, BLL, DAL, BO, Other;

	private static final RuleSetParser<AMMModuleType> RULE_SET_PARSER = new RuleSetParser<AMMModuleType>(
			new IMapFunctor<String, AMMModuleType>() {

				@Override
				public AMMModuleType mapItem(final String str) {
					return fromString(str);
				}
			});

	@SuppressWarnings("unchecked")
	private final static Pair<String, AMMModuleType>[] NAME_TO_ELEMENT_PAIRS = new Pair[] {
			new Pair<String, AMMModuleType>("WebS", AMMModuleType.WebS),
			new Pair<String, AMMModuleType>("WinS", AMMModuleType.WinS),
			new Pair<String, AMMModuleType>("DTO", AMMModuleType.DTO),
			new Pair<String, AMMModuleType>("BLL", AMMModuleType.BLL),
			new Pair<String, AMMModuleType>("BLL_API", AMMModuleType.BLL_API),
			new Pair<String, AMMModuleType>("DAL", AMMModuleType.DAL),
			new Pair<String, AMMModuleType>("BO", AMMModuleType.BO),
			new Pair<String, AMMModuleType>("Other", AMMModuleType.Other), };
	private final static Map<String, AMMModuleType> NAME_TO_ELEMENT_MAP = CollectionUtils
			.createMap(NAME_TO_ELEMENT_PAIRS);

	public static AMMModuleType fromString(final String str) {
		return NAME_TO_ELEMENT_MAP.get(str);
	}

	public static Pair<AMMModuleType, AMMModuleType[]>[] ruleSetFromString(
			final String ruleSetString) {
		return RULE_SET_PARSER.parse(ruleSetString);
	}
}
