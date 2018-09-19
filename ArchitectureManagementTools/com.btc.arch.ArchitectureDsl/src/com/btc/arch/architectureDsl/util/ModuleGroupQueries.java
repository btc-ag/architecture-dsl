package com.btc.arch.architectureDsl.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.commons.java.functional.IterationUtils;

public class ModuleGroupQueries {

	public static ModuleGroup getParentModuleGroup(final ModuleGroup moduleGroup) {
		assert moduleGroup != null;
		ModuleGroup parentModuleGroup = null;
		final Iterable<ModuleGroup> moduleGroups = IterationUtils
				.filterByClass(moduleGroup.eResource().getResourceSet()
						.getAllContents(), ModuleGroup.class);
		for (ModuleGroup currentModuleGroup : moduleGroups) {
			if (moduleGroup.getName().startsWith(currentModuleGroup.getName())
					&& (!moduleGroup.getName().equals(
							currentModuleGroup.getName()))
					&& (parentModuleGroup == null || (currentModuleGroup
							.getName().length() > parentModuleGroup.getName()
							.length()))) {
				parentModuleGroup = currentModuleGroup;
			}
		}
		return parentModuleGroup;
	}

	public static Domain getEffectiveDomain(final ModuleGroup moduleGroup) {
		assert moduleGroup != null;
		if (moduleGroup.getDomain() != null)
			return moduleGroup.getDomain();

		final ModuleGroup parentModuleGroup = getParentModuleGroup(moduleGroup);
		if (parentModuleGroup != null)
			return getEffectiveDomain(parentModuleGroup);
		return null;
	}

	public static Collection<Domain> getEffectiveAllowedTargetDomains(
			final ModuleGroup moduleGroup) {
		assert moduleGroup != null;
		final Set<Domain> allowedTargetDomains = new HashSet<Domain>();
		final ModuleGroup parentModuleGroup = getParentModuleGroup(moduleGroup);
		if (parentModuleGroup != null) {
			allowedTargetDomains
					.addAll(getEffectiveAllowedTargetDomains(parentModuleGroup));
			// TODO: Should the domain of the parentModuleGroup really be not
			// allowed as target domain of the module group? If so, state the
			// reason here.
			allowedTargetDomains.remove(getEffectiveDomain(parentModuleGroup));
		}
		// The allowed target domains of the current moduleGroup are inserted
		// after the allowed target domains of the parentModuleGroup so that the
		// domain of the parentModuleGroup can be defined as allowed target
		// domains of the current moduleGroup. If this is not done, the effect
		// that an explicitly allowed target domain is not allowed anymore is
		// not comprehensible.
		allowedTargetDomains.addAll(moduleGroup.getAllowedTargetDomains());
		if (getEffectiveDomain(moduleGroup) != null)
			allowedTargetDomains.add(getEffectiveDomain(moduleGroup));
		return allowedTargetDomains;
	}

	public static Collection<Domain> getInvalidSpecifiedDomains(
			ModuleGroup moduleGroup) {
		Set<Domain> invalidDomains = new HashSet<Domain>();
		if (moduleGroup.getDomain() != null) {
			Domain effectiveSuperModuleGroupDomain = ModuleGroupQueries
					.getEffectiveDomain(ModuleGroupQueries
							.getParentModuleGroup(moduleGroup));

			Set<Domain> allDomainsAndSubdomains = new HashSet<Domain>();
			allDomainsAndSubdomains.add(effectiveSuperModuleGroupDomain);
			allDomainsAndSubdomains.addAll(DomainQueries
					.getAllSubdomains(effectiveSuperModuleGroupDomain));

			invalidDomains.add(moduleGroup.getDomain());
			invalidDomains.removeAll(allDomainsAndSubdomains);
		}
		return invalidDomains;
	}

	public static Collection<Module> getEffectivelyContainedModules(
			ModuleGroup moduleGroup) {
		Set<Module> modules = new HashSet<Module>();
		final Iterable<Module> allModules = IterationUtils.filterByClass(
				moduleGroup.eResource().getResourceSet().getAllContents(),
				Module.class);
		for (Module module : allModules) {
			if (ModuleQueries.getEffectiveModuleGroup(module).equals(
					moduleGroup))
				modules.add(module);
		}
		return modules;
	}

	public static String getEffectiveMaintainer(ModuleGroup moduleGroup) {
		assert moduleGroup != null;

		if (moduleGroup.getMaintainer() != null
				&& !moduleGroup.getMaintainer().equals(""))
			return moduleGroup.getMaintainer();
		return getEffectiveMaintainer(getParentModuleGroup(moduleGroup));
	}

	public static Collection<ModuleGroup> getAllVersionedTargetModuleGroups(
			ModuleGroup moduleGroup) {
		assert moduleGroup != null;

		Set<ModuleGroup> targetModuleGroups = new HashSet<ModuleGroup>();
		for (Module module : getEffectivelyContainedModules(moduleGroup)) {
			for (Module targetModule : ModuleQueries
					.getAllDependencyTargets(module)) {
				targetModuleGroups.add(ModuleQueries
						.getVersionedEffectiveModuleGroup(targetModule));
			}
		}
		targetModuleGroups.remove(moduleGroup);

		return targetModuleGroups;
	}
}
