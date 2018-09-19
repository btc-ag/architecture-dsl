package com.btc.arch.architectureDsl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.btc.arch.architectureDsl.ArchitectureDslFactory;
import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.Module;
import com.btc.commons.java.IFactory;

public class ModelBuilder {
	private final Model model;
	private final IModuleCreator moduleCreator;
	private final List<Model> allModels;

	public ModelBuilder(IModuleCreator moduleCreator) {
		this(moduleCreator, ArchitectureDslFactory.eINSTANCE.createModel());
	}

	public ModelBuilder(IModuleCreator moduleCreator, Model model,
			Collection<Model> allModels) {
		this.moduleCreator = moduleCreator;
		this.model = model;
		this.allModels = new ArrayList<Model>(allModels.size() + 1);
		this.allModels.addAll(allModels);
	}

	public ModelBuilder(IModuleCreator moduleCreator, Model model) {
		this(moduleCreator, model, Collections.singletonList(model));
	}

	public Model toModel() {
		return model;
	}

	/**
	 * 
	 * The module dependencies are ignored if the node already exists. The
	 * client must therefore ensure that they are equivalent on multiple calls
	 * to getOrCreateModule.
	 * 
	 * @param moduleName
	 * @param alwaysUpdateDependencies
	 *            TODO
	 * @param moduleDependencies
	 * @return
	 */
	public Module getOrCreateModule(
			final String moduleName,
			final IFactory<? extends Collection<Module>> moduleDependencyFactory,
			boolean alwaysUpdateDependencies) {
		Module module = getModuleByName(moduleName);
		if (module == null) {
			module = moduleCreator.createModule(moduleName,
					moduleDependencyFactory.create());
			model.getModules().add(module);
		} else if (alwaysUpdateDependencies) {
			// TODO
			throw new UnsupportedOperationException(
					"Semantics need to be specified (replace or add dependencies? or check whether they are the same?)");
		}
		return module;
	}

	private Module getModuleByName(final String moduleName) {
		return ModelQueries.getModuleByName(allModels, moduleName);
	}

	public Module getOrCreateModule(final String moduleName) {
		Module module = getModuleByName(moduleName);
		if (module == null) {
			module = moduleCreator.createModule(moduleName,
					new ArrayList<Module>());
			model.getModules().add(module);
		}
		return module;
	}
}
