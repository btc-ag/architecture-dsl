package com.btc.arch.generator.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.btc.arch.base.IContext;
import com.btc.arch.generator.ArchDslGenerator;
import com.btc.arch.generator.ArchDslGeneratorException;
import com.btc.arch.generator.IArchitectureDSLGenerator;

public class ModuleGraphGenerator implements IArchitectureDSLGenerator {

	private static final String LIMITED_TO_PARAMETER = "limitedTo";
	private static final String TEMPLATE_PATH = "templates::GraphViz::main";
	private final String GENERATE_OUTPUT_MESSAGE = "Generated module graph to %s";

	private String outputMessage;

	public ModuleGraphGenerator() {
		outputMessage = "ModuleGraphGenerator did not generate anything.";
	}

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
		List<Object> params = new ArrayList<Object>();
		params.add(contextParameters.getParameter(LIMITED_TO_PARAMETER));
		ArchDslGenerator.evaluateTemplate(TEMPLATE_PATH, params, contents,
				targetDir);
		outputMessage = String.format(GENERATE_OUTPUT_MESSAGE,
				targetDir.toOSString());
	}

	@Override
	public Iterable<String> getRequiredParameters() {
		Collection<String> generatorParameters = new ArrayList<String>();
		generatorParameters.add(LIMITED_TO_PARAMETER);
		return generatorParameters;
	}

	@Override
	public String getOutputMessage() {
		return outputMessage;
	}
}
