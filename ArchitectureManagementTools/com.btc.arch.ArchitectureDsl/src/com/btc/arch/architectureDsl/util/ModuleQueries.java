package com.btc.arch.architectureDsl.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.commons.emf.EcoreUtils;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.InfiniteIterable;
import com.btc.commons.java.functional.IterableChain;
import com.btc.commons.java.functional.IterationUtils;

public class ModuleQueries {

	public static ModuleGroup getEffectiveModuleGroup(final Module module) {
		assert module != null;
		assert module.eResource() != null;

		if (module.getModuleGroup() != null) {
			return module.getModuleGroup();
		}

		ModuleGroup effectiveModuleGroup = null;

		final Iterable<ModuleGroup> moduleGroups = IterationUtils
				.filterByClass(EcoreUtils.getResourceSetContents(module),
						ModuleGroup.class);
		for (final ModuleGroup currentModuleGroup : moduleGroups) {
			if (module.getName().startsWith(currentModuleGroup.getName())
					&& (effectiveModuleGroup == null || currentModuleGroup
							.getName().length() > effectiveModuleGroup
							.getName().length())) {
				effectiveModuleGroup = currentModuleGroup;
			}
		}
		return effectiveModuleGroup;
	}

	public static ModuleGroup getVersionedEffectiveModuleGroup(
			final Module module) {
		assert module != null;
		assert module.eResource() != null;

		if (module.getModuleGroup() != null) {
			return module.getModuleGroup();
		}

		ModuleGroup versionedEffectiveModuleGroup = null;

		final Iterable<ModuleGroup> moduleGroups = IterationUtils
				.filterByClass(EcoreUtils.getResourceSetContents(module),
						ModuleGroup.class);
		for (final ModuleGroup currentModuleGroup : moduleGroups) {
			if (currentModuleGroup.getVersion() != null // TODO: Refactor: This
														// line is the only
														// difference to
														// getEffectiveModuleGroup
					&& module.getName()
							.startsWith(currentModuleGroup.getName())
					&& (versionedEffectiveModuleGroup == null || currentModuleGroup
							.getName().length() > versionedEffectiveModuleGroup
							.getName().length())) {
				versionedEffectiveModuleGroup = currentModuleGroup;
			}
		}
		return versionedEffectiveModuleGroup;
	}

	public static boolean isDependencyAllowedByDomainModel(final Module from,
			final Module to) {
		assert from != null;
		assert to != null;
		assert from.eResource() != null;
		assert to.eResource() != null;

		final Collection<Domain> allowedTargetDomainsRTClosure = DomainQueries
				.getSubdomainsRTClosure(getEffectiveAllowedTargetDomains(from));
		return allowedTargetDomainsRTClosure.contains(getEffectiveDomain(to));
	}

	public static Iterable<Module> getAllDependencyTargets(final Module module) {
		assert module != null;

		final IterableChain<Module> dependencyTargets = new IterableChain<Module>();
		dependencyTargets.addIterable(module.getUsedModules());
		dependencyTargets.addIterable(module.getImplementedModules());
		dependencyTargets.addIterable(module.getTestedModule());
		return dependencyTargets;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Iterable<Domain> getEffectiveAllowedTargetDomains(
			final Module module) {
		assert module != null;
		assert module.eResource() != null;

		final ModuleGroup effectiveModuleGroup = ModuleQueries
				.getEffectiveModuleGroup(module);
		return effectiveModuleGroup != null ? ModuleGroupQueries
				.getEffectiveAllowedTargetDomains(effectiveModuleGroup)
				: (Iterable) Collections.emptyList();
	}

	public static Iterable<Module> getIllegalDependencyTargets(
			final Module module) {
		assert module != null;
		assert module.eResource() != null;

		final Set<Module> illegalDependencyTargets = new HashSet<Module>();
		for (final Module dependency : getAllDependencyTargets(module)) {
			if (!isDependencyAllowedByDomainModel(module, dependency)) {
				illegalDependencyTargets.add(dependency);
			}
		}
		return illegalDependencyTargets;
	}

	@Deprecated
	public static Iterable<String> getIllegalDependencyTargetNames(
			final Module module) {
		assert module != null;
		assert module.eResource() != null;

		final Set<String> illegalDependencyNames = new HashSet<String>();
		for (final Module dependency : getIllegalDependencyTargets(module)) {
			illegalDependencyNames.add(dependency.getName());
		}
		return illegalDependencyNames;
	}

	public static Domain getEffectiveDomain(final Module module) {
		assert module != null;
		assert module.eResource() != null;

		Domain effectiveDomain = null;
		final ModuleGroup effectiveModuleGroup = getEffectiveModuleGroup(module);
		if (effectiveModuleGroup != null) {
			effectiveDomain = ModuleGroupQueries
					.getEffectiveDomain(effectiveModuleGroup);
		}
		return effectiveDomain;
	}

	public static String getEffectiveMaintainer(final Module module) {
		assert module != null;

		if (module.getMaintainer() != null
				&& !module.getMaintainer().equals("")) {
			return module.getMaintainer();
		}
		return ModuleGroupQueries
				.getEffectiveMaintainer(getEffectiveModuleGroup(module));
	}

	public static Iterable<Pair<Module, Module>> getAllDependenciesAsPairs(
			final Module module) {
		assert module != null;
		// final Collection<Pair<Module, Module>> dependencyPairs = new
		// Vector<Pair<Module, Module>>();
		// for (Module targetModule : getAllDependencyTargets(module)) {
		// dependencyPairs.add(new Pair<Module, Module>(module, targetModule));
		// }
		// return dependencyPairs;
		return IterationUtils.zip(new InfiniteIterable<Module>(module),
				getAllDependencyTargets(module));
	}

	public static boolean isEffectiveDomainLeafDomain(final Module module) {
		final Domain domain = ModuleQueries.getEffectiveDomain(module);
		if (domain.getSubdomains().size() > 0)
			return false;
		return true;
	}
}
