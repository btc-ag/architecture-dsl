package com.btc.arch.architectureDsl.util;

import com.btc.arch.architectureDsl.ArchitectureDslFactory;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleType;
import com.btc.commons.java.CollectionUtils;

public class DefaultModuleCreator implements IModuleCreator {

	@Override
	public Module createModule(String moduleName,
			Iterable<Module> moduleDependencies) {
		final Module module = ArchitectureDslFactory.eINSTANCE.createModule();
		module.setName(moduleName);
		// TODO make parametrisable or move to a context-specific class
		module.getType().add(ModuleType.FRAMEWORK);
		CollectionUtils.addAll(module.getUsedModules(), moduleDependencies);
		return module;
	}

}
