package com.btc.arch.architectureDsl.diagnostics.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.util.DefaultModuleCreator;
import com.btc.arch.architectureDsl.util.DependencyPairUtils;
import com.btc.arch.architectureDsl.util.ModelBuilder;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.arch.base.dependency.DependencyParseException;
import com.btc.arch.base.dependency.IDependencySource;
import com.btc.arch.diagnostics.api.Dependency;
import com.btc.commons.java.CollectionUtils;
import com.btc.commons.java.Pair;
import com.btc.commons.java.StringUtils;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IterationUtils;

/**
 * The purpose of this class is to manage the lifecycle of the nameToModuleMap.
 * 
 * @author SIGIESEC
 * 
 */
public class ExtractedModuleDependencyValidatorInternal {
	private final class NameToModuleMapFunctor implements
			IMapFunctor<String, Module> {
		@Override
		public Module mapItem(String obj) {
			return lookupOrCreateModule(obj);
		}
	}

	private final Iterable<IDependencySource> extractedDependencySources;
	private final Map<String, Module> nameToModuleMap;
	private final ModelBuilder modelBuilder;
	private final Set<Pair<String, String>> additionalDependencyPairs;
	private final String commonModuleNamePrefix;
	private final Set<String> ignoredTargetModules;
	private final Set<String> unspecifiedSourceModules;
	private final Set<String> extractedSourceModules;
	private final Set<Module> specifiedModules;
	private final Logger logger;

	public ExtractedModuleDependencyValidatorInternal(
			final Collection<? extends EObject> baseElements,
			Collection<? extends EObject> allElements,
			Iterable<IDependencySource> extractedDependencySources) {
		// TODO lock the model against modification?

		this.extractedDependencySources = extractedDependencySources;

		final Collection<Pair<Module, Module>> specifiedDependencies = IterationUtils
				.materialize(ModelQueries
						.getAllDependenciesAsPairs(baseElements));
		this.additionalDependencyPairs = getAdditionalDependencyPairs(specifiedDependencies);
		this.ignoredTargetModules = new HashSet<String>();
		this.unspecifiedSourceModules = new HashSet<String>();
		this.extractedSourceModules = new HashSet<String>();

		this.modelBuilder = new ModelBuilder(new DefaultModuleCreator());
		this.specifiedModules = IterationUtils.materialize(
				ModelQueries.getAllModules(allElements), new HashSet<Module>());
		this.nameToModuleMap = createNameToModuleMap(specifiedDependencies);
		this.commonModuleNamePrefix = StringUtils
				.findCommonPrefix(this.nameToModuleMap.keySet().toArray(
						new String[this.nameToModuleMap.keySet().size()]));
		logger = Logger.getLogger(getClass().getName());
	}

	public boolean hasIgnoredTargetModules() {
		return !this.ignoredTargetModules.isEmpty();
	}

	public Iterable<Module> getIgnoredTargetModules() {
		return IterationUtils.map(this.ignoredTargetModules,
				new NameToModuleMapFunctor());
	}

	public Iterable<Module> getUnspecifiedSourceModules() {
		return IterationUtils.map(this.unspecifiedSourceModules,
				new NameToModuleMapFunctor());
	}

	public Iterable<Module> getMissingSourceModules() {
		final Set<Module> modules = new HashSet<Module>();
		CollectionUtils.addAll(modules, this.specifiedModules);
		CollectionUtils.removeAll(modules, IterationUtils.map(
				this.extractedSourceModules, new NameToModuleMapFunctor()));
		return modules;
	}

	private boolean ignoreIllegalTarget(String moduleName) {
		// TODO parametrise as a regular expression!
		// return moduleName.startsWith("System.") ||
		// moduleName.equals("system")
		// || moduleName.equals("mscorlib");

		final boolean ignore = !moduleName
				.startsWith(this.commonModuleNamePrefix);
		if (ignore) {
			this.ignoredTargetModules.add(moduleName);
		}
		return ignore;
	}

	protected Map<String, Module> createNameToModuleMap(
			Collection<Pair<Module, Module>> dependencyPairs) {
		final Map<String, Module> pNameToModuleMap = new HashMap<String, Module>();
		for (Module module : this.specifiedModules) {
			addToMap(pNameToModuleMap, module);
		}
		for (Pair<Module, Module> dependencyPair : dependencyPairs) {
			addToMap(pNameToModuleMap, dependencyPair.getFirst());
			addToMap(pNameToModuleMap, dependencyPair.getSecond());
		}
		return pNameToModuleMap;
	}

	protected void addToMap(final Map<String, Module> pNameToModuleMap,
			final Module module) {
		if (module.getName() != null) // module has no name if it is a proxy
										// object
			CollectionUtils.addIfNotExists(pNameToModuleMap, module.getName(),
					module);
		else {
			logger.debug("Module " + module + " has no name");
		}
	}

	private Module lookupOrCreateModule(String moduleName) {
		if (nameToModuleMap.containsKey(moduleName)) {
			return nameToModuleMap.get(moduleName);
		} else {
			final Module module = modelBuilder.getOrCreateModule(moduleName);
			nameToModuleMap.put(moduleName, module);
			return module;
		}

	}

	public Iterable<Dependency> getAllIllegalDependencies() {
		final Collection<Dependency> additionalDependencies = new ArrayList<Dependency>();
		for (Pair<String, String> dependencyPair : additionalDependencyPairs) {
			final String sourceModuleName = dependencyPair.getFirst();
			if (nameToModuleMap.containsKey(sourceModuleName)) {
				final String targetModuleName = dependencyPair.getSecond();
				if (!ignoreIllegalTarget(targetModuleName)) {
					final Module sourceModule = lookupOrCreateModule(sourceModuleName);
					final Module targetModule = lookupOrCreateModule(targetModuleName);
					if (this.specifiedModules.contains(sourceModule)) {
						// TODO make it configurable whether to output
						// violations for unknown source modules?
						additionalDependencies.add(new Dependency(sourceModule,
								targetModule, false));
					}
				}
				extractedSourceModules.add(sourceModuleName);
			} else {
				unspecifiedSourceModules.add(sourceModuleName);
			}
		}
		return additionalDependencies;
	}

	protected Set<Pair<String, String>> getAdditionalDependencyPairs(
			final Collection<Pair<Module, Module>> specifiedDependencies) {
		final Set<Pair<String, String>> dependencies = getExtractedDependencies();
		CollectionUtils.removeAll(dependencies,
				DependencyPairUtils.mapToNames(specifiedDependencies));
		return dependencies;
	}

	protected Set<Pair<String, String>> getExtractedDependencies() {
		return IterationUtils
				.materialize(
						IterationUtils
								.mapToIterablesAndChain(
										extractedDependencySources,
										new IMapFunctor<IDependencySource, Iterable<Pair<String, String>>>() {

											@Override
											public Iterable<Pair<String, String>> mapItem(
													IDependencySource extractedDependencySource) {
												try {
													return extractedDependencySource
															.getAllDependencies();
												} catch (DependencyParseException e) {
													throw new AssertionError(
															"Expected no exception: "
																	+ e);
												}
											}

										}), new HashSet<Pair<String, String>>());
	}
}