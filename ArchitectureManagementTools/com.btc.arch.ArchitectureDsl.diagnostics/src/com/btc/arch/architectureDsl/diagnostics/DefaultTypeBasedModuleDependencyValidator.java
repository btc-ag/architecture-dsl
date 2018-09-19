package com.btc.arch.architectureDsl.diagnostics;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleType;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.UnmodifiableIterable;

public class DefaultTypeBasedModuleDependencyValidator extends
		TypeBasedModuleDependencyValidatorBase<ModuleType> {
	public DefaultTypeBasedModuleDependencyValidator(
			IDiagnosticResultFactory resultFactory) {
		super(resultFactory);
	}

	// TODO these type dependencies are incomplete
	// TODO should probably be read from configuration (using RuleSetParser)
	@SuppressWarnings("unchecked")
	private static final Pair<ModuleType, ModuleType[]>[] ALLOWED_MODULE_TYPE_DEPENDENCIES = new Pair[] {
			new Pair<ModuleType, ModuleType[]>(ModuleType.INTERFACE,
					new ModuleType[] { ModuleType.INTERFACE,
							ModuleType.FRAMEWORK }),
			new Pair<ModuleType, ModuleType[]>(
					ModuleType.IMPLEMENTATION,
					new ModuleType[] { ModuleType.INTERFACE,
							ModuleType.ABSTRACT_IMPLEMENTATION,
							ModuleType.INTERFACE_UTILITY, ModuleType.FRAMEWORK }),
			new Pair<ModuleType, ModuleType[]>(
					ModuleType.ABSTRACT_IMPLEMENTATION,
					new ModuleType[] { ModuleType.INTERFACE,
							ModuleType.ABSTRACT_IMPLEMENTATION,
							ModuleType.INTERFACE_UTILITY, ModuleType.FRAMEWORK }),
			new Pair<ModuleType, ModuleType[]>(
					ModuleType.INTERFACE_UTILITY,
					new ModuleType[] { ModuleType.INTERFACE,
							ModuleType.INTERFACE_UTILITY, ModuleType.FRAMEWORK }),
			new Pair<ModuleType, ModuleType[]>(ModuleType.INTERFACE_TEST,
					new ModuleType[] { ModuleType.INTERFACE,
							ModuleType.FRAMEWORK }),
			new Pair<ModuleType, ModuleType[]>(ModuleType.IMPLEMENTATION_TEST,
					new ModuleType[] { ModuleType.INTERFACE,
							ModuleType.IMPLEMENTATION,
							ModuleType.ABSTRACT_IMPLEMENTATION,
							ModuleType.INTERFACE_UTILITY,
							ModuleType.INTERFACE_TEST, ModuleType.FRAMEWORK }),
			new Pair<ModuleType, ModuleType[]>(ModuleType.FRAMEWORK,
					new ModuleType[] { ModuleType.FRAMEWORK }) };
	private static final TypeDependencyRuleSet<ModuleType> DIFFERENT_MODULE_GROUP_RULE_SET = new TypeDependencyRuleSet<ModuleType>(
			"Regular", ALLOWED_MODULE_TYPE_DEPENDENCIES);

	// @SuppressWarnings("unchecked")
	// private static final Pair<ModuleType, ModuleType[]>[]
	// ALLOWED_MODULE_TYPE_DEPENDENCIES_SAME_MODULE_GROUP = new Pair[] { new
	// Pair<ModuleType, ModuleType[]>(
	// ModuleType.IMPLEMENTATION,
	// new ModuleType[] { ModuleType.IMPLEMENTATION }), };
	// private static final TypeDependencyRuleSet<ModuleType>
	// SAME_MODULE_GROUP_RULE_SET = new TypeDependencyRuleSet<ModuleType>(
	// "Same module group", ALLOWED_MODULE_TYPE_DEPENDENCIES)
	// .relax(ALLOWED_MODULE_TYPE_DEPENDENCIES_SAME_MODULE_GROUP);

	@Override
	protected TypeDependencyRuleSet<ModuleType> selectTypeDependencyRules(
			Module source, Module target) {
		// // TODO implement this using a Chain of Responsibility
		// final ModuleGroup sourceEffectiveModuleGroup = ModuleQueries
		// .getEffectiveModuleGroup(source);
		// if (sourceEffectiveModuleGroup != null
		// && sourceEffectiveModuleGroup.equals(ModuleQueries
		// .getEffectiveModuleGroup(target))) {
		// return SAME_MODULE_GROUP_RULE_SET;
		// } else {
		return DIFFERENT_MODULE_GROUP_RULE_SET;
		// }
	}

	@Override
	protected Iterable<ModuleType> getTypes(final Module module) {
		return new UnmodifiableIterable<ModuleType>(module.getType());
	}

	@Override
	public String getDescription() {
		return "Checks model-specified module dependencies for conformance with the module type rules";
	}

	@Override
	public String getDiagnosticsId() {
		return "com.btc.arch.ArchitectureDsl.moduletype";
	}
}
