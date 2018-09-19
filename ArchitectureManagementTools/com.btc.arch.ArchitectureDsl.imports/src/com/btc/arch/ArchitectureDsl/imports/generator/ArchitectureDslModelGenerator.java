package com.btc.arch.ArchitectureDsl.imports.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IPath;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.generator.ArchDslGenerator;
import com.btc.arch.generator.ArchDslGeneratorException;

public class ArchitectureDslModelGenerator {

	/**
	 * The separator of the elements of a module group or module name, which can
	 * be used in splitting module names and joining module name elements.
	 * 
	 * TODO this does belong somewhere else. If it is fixed, it should be
	 * defined in com.btc.arch.ArchitectureDsl. If this is context-dependent, it
	 * shouldn't be a constant here.
	 */
	private static final String MODULE_GROUP_SEP = ".";

	/**
	 * This method creates the textual representation of the given model.
	 * 
	 * It outputs one .archdsl file in which the textual representations of all
	 * modules that are listed in the moduleNamesList.
	 * 
	 * @param model
	 */
	public void generateTextualModelRepresentation(Model model,
			Collection<String> moduleNames, IPath targetDirectory) {
		String fileName = getCommonModulePrefix(moduleNames) + ".phys.archdsl";

		List<Object> params = new ArrayList<Object>();
		params.add(fileName);
		params.add(moduleNames);

		try {
			ArchDslGenerator
					.evaluateTemplate(
							"templates::ArchitectureDslTemplate::generateOneModuleDescriptionForAllModules",
							params, model.getModules(), targetDirectory);
		} catch (ArchDslGeneratorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The template according to this method is suited to the conventions of
	 * architecture descriptions for the CAB. Thus, the module description file
	 * will be generated to a path defined as follows:
	 * baseTargetDirectory\nameOfTheModule\build\ where nameOfTheModule is a
	 * subpath that is defined using the module name where each . is replaced by
	 * a \ to defined the path. It is assumed that the baseTargetDirectory is
	 * the main CAB folder. The template is currently only suited for generation
	 * on windows systems.
	 * 
	 * @param importedModel
	 * @param baseTargetDirectory
	 */
	public void generateTextualModelRepresentationForEachModule(
			Model importedModel, Collection<String> moduleNames,
			IPath baseTargetDirectory) {
		try {
			List<Object> params = new ArrayList<Object>();
			params.add(moduleNames);
			ArchDslGenerator
					.evaluateTemplate(
							"templates::ArchitectureDslTemplate::generateOneModuleDescriptionForEachModule",
							params, importedModel.getModules(),
							baseTargetDirectory);
		} catch (ArchDslGeneratorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * TODO this method should probably be moved to com.btc.arch.ArchitectureDsl
	 * 
	 * TODO this method should be documented and tested
	 * 
	 * @param moduleNames
	 * @return
	 */
	private String getCommonModulePrefix(Collection<String> moduleNames) {
		assert moduleNames != null;
		assert moduleNames.size() > 0;

		if (moduleNames.size() == 1)
			return moduleNames.iterator().next();

		final List<String> copyOfModuleNames = new ArrayList<String>(
				moduleNames);

		String prefix = copyOfModuleNames.remove(0);
		boolean foundCommonPrefix = false;
		while (!foundCommonPrefix) {
			foundCommonPrefix = true;
			prefix = prefix.substring(0, prefix.length() - 1);
			for (String string : copyOfModuleNames) {
				if (!string.startsWith(prefix)) {
					foundCommonPrefix = false;
				}
			}
		}
		if (prefix.endsWith(MODULE_GROUP_SEP))
			prefix = prefix.substring(0, prefix.length() - 1);
		return prefix;
	}

}
