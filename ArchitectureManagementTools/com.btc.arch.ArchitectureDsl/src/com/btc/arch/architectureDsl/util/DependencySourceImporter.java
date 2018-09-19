package com.btc.arch.architectureDsl.util;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.base.dependency.DependencyParseException;
import com.btc.arch.base.dependency.IDependencySource;
import com.btc.commons.java.Pair;

public class DependencySourceImporter {
	public Model createModel(final ModelBuilder builder,
			final IDependencySource dependencySource)
			throws DependencyParseException {
		createBaseModules(builder, dependencySource);
		createDependencies(builder, dependencySource);

		return builder.toModel();
	}

	private void createDependencies(final ModelBuilder builder,
			final IDependencySource dependencySource)
			throws DependencyParseException {
		for (final Pair<String, String> dependency : dependencySource
				.getAllDependencies()) {
			final String sourceModuleName = dependency.getFirst();
			final String targetModuleName = dependency.getSecond();
			processDependency(builder, sourceModuleName, targetModuleName);
		}
	}

	private void createBaseModules(final ModelBuilder builder,
			final IDependencySource dependencySource)
			throws DependencyParseException {
		for (final String moduleName : dependencySource.getAllBaseModuleNames()) {
			builder.getOrCreateModule(moduleName);
		}
	}

	private void processDependency(final ModelBuilder builder,
			final String sourceModuleName, final String targetModuleName) {
		final Module sourceModule = builder.getOrCreateModule(sourceModuleName);
		final Module targetModule = builder.getOrCreateModule(targetModuleName);
		sourceModule.getUsedModules().add(targetModule);
	}

}