package com.btc.arch.generator.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.arch.architectureDsl.ReleaseUnit;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.arch.base.IContext;
import com.btc.arch.generator.ArchDslGenerator;
import com.btc.arch.generator.ArchDslGeneratorException;
import com.btc.arch.generator.IArchitectureDSLGenerator;
import com.btc.commons.java.functional.IterationUtils;

public class CgxmlGenerator implements IArchitectureDSLGenerator {

	private static final String TARGET_MODULE_GROUP_PARAMETER = "targetModuleGroup";
	private static final String TEMPLATE_PATH = "templates::Cgxml::main";
	private final String GENERATE_OUTPUT_MESSAGE = "Generated Cgxml to %s";

	private String outputMessage;

	public CgxmlGenerator() {
		outputMessage = "CgxmlGenerator did not generate anything.";
	}

	/**
	 * The core asset profile is generate for the module group defined in the
	 * context parameter targetModuleGroup or for all module groups that are
	 * tagged as release units in allContents if targetModuleGroup is not
	 * specified. All modules groups that have are version are considered to be
	 * a release unit.
	 */
	@Override
	public void generate(Collection<? extends EObject> contents,
			Collection<? extends EObject> allContents, IPath targetDir,
			String parameter, IContext contextParameters, boolean modelValid)
			throws ArchDslGeneratorException {
		if (!modelValid) {
			// TODO check whether relevant parts are valid
			throw new ArchDslGeneratorException(
					"Cannot generate on an invalid model!");
		}
		Iterable<ReleaseUnit> releaseUnits = ModelQueries.getAllReleaseUnits(contents);
		
//		List<ModuleGroup> releaseUnitModuleGroups = new ArrayList<ModuleGroup>();
//		String targetModuleGroupName = contextParameters
//				.getParameter(TARGET_MODULE_GROUP_PARAMETER);
//		// TODO: The template has to be changed to enable the generation of more
//		// than one profile at once.
//		// if (targetModuleGroupName != null &&
//		// !targetModuleGroupName.equals("")) {
//		ModuleGroup targetModuleGroup = null;
//		for (ModuleGroup moduleGroup : ModelQueries
//				.getAllModuleGroups(contents)) {
//			if (moduleGroup.getName().equals(targetModuleGroupName))
//				targetModuleGroup = moduleGroup;
//		}
//		if (targetModuleGroup == null)
//			throw new ArchDslGeneratorException(
//					"No matching module group for name "
//							+ targetModuleGroupName + ".");
//		releaseUnitModuleGroups.add(targetModuleGroup);

		if (IterationUtils.count(releaseUnits) > 0) {
			ArchDslGenerator.evaluateTemplate(TEMPLATE_PATH, null,
					new ArrayList<ReleaseUnit>(IterationUtils.materialize(releaseUnits)), targetDir);
			outputMessage = String.format(GENERATE_OUTPUT_MESSAGE,
					targetDir.toOSString());
		} else {
			outputMessage = String
					.format("No core asset profile was generated "
							+ "since no release unit module group was found.");
		}
	}

	@Override
	public Iterable<String> getRequiredParameters() {
		Collection<String> generatorParameters = new ArrayList<String>();
		generatorParameters.add(TARGET_MODULE_GROUP_PARAMETER);
		return generatorParameters;
	}

	@Override
	public String getOutputMessage() {
		return outputMessage;
	}
}
