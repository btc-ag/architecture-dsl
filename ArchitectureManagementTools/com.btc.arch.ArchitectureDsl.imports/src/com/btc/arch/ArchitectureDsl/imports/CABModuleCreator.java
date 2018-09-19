package com.btc.arch.ArchitectureDsl.imports;

import com.btc.arch.architectureDsl.ArchitectureDslFactory;
import com.btc.arch.architectureDsl.Language;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleType;
import com.btc.arch.architectureDsl.util.IModuleCreator;
import com.btc.commons.java.CollectionUtils;

public class CABModuleCreator implements IModuleCreator {

	private final Language defaultLanguage;

	public CABModuleCreator(Language defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.btc.arch.ArchitectureDsl.imports.IModuleCreator#createModule(java.
	 * lang.String, java.util.Collection)
	 */
	@Override
	public Module createModule(String moduleName,
			Iterable<Module> moduleDependencies) {
		// TODO here are rules that depend on the concrete Technology-specific
		// mapping!
		final Module module = ArchitectureDslFactory.eINSTANCE.createModule();
		module.setName(moduleName);
		CollectionUtils.addAll(module.getUsedModules(), moduleDependencies);
		if (module.getName().contains("NET"))
			module.setLanguage(Language.CSHARP);
		else if (module.getName().contains("CPP"))
			module.setLanguage(Language.CPP);
		else
			module.setLanguage(this.defaultLanguage);
		if (module.getName().contains(".API")
				|| module.getName().contains(".Api"))
			module.getType().add(ModuleType.INTERFACE);
		// TODO: Change or remove default?
		else
			module.getType().add(ModuleType.FRAMEWORK);
		return module;
	}

}
