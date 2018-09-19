package com.btc.arch.ArchitectureDsl.imports;

import com.btc.arch.architectureDsl.ArchitectureDslFactory;
import com.btc.arch.architectureDsl.Language;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleType;
import com.btc.arch.architectureDsl.util.IModuleCreator;

public class JavaModuleCreator implements IModuleCreator {

	@Override
	public Module createModule(String moduleName,
			Iterable<Module> moduleDependencies) {
		final Module module = ArchitectureDslFactory.eINSTANCE.createModule();
		module.setName(moduleName);
		module.setLanguage(Language.JAVA);
		if (module.getName().toUpperCase().contains(".API")) // TODO shouldn't
																// this be
																// endsWith
																// instead of
																// contains?
			module.getType().add(ModuleType.INTERFACE);
		// TODO: Change or remove default?
		else
			module.getType().add(ModuleType.FRAMEWORK);
		return module;
	}

}
