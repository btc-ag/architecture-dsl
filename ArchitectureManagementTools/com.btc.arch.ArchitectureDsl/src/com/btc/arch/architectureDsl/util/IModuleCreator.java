package com.btc.arch.architectureDsl.util;

import com.btc.arch.architectureDsl.Module;

public interface IModuleCreator {

	/**
	 * Creates a module given a name and its outgoing dependencies.
	 * 
	 * @param moduleName
	 *            may NOT be null or empty
	 * @param moduleDependencies
	 *            may not be null
	 * @return an instance of Module (never returns null)
	 * @throws IllegalArgumentException
	 *             if moduleName is empty
	 */
	public abstract Module createModule(String moduleName,
			Iterable<Module> moduleDependencies);

}