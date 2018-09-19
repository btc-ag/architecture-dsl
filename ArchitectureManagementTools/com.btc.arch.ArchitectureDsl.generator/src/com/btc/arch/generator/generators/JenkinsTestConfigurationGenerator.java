package com.btc.arch.generator.generators;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.btc.arch.base.IContext;
import com.btc.arch.generator.ArchDslGenerator;
import com.btc.arch.generator.ArchDslGeneratorException;
import com.btc.arch.generator.IArchitectureDSLGenerator;

public class JenkinsTestConfigurationGenerator implements
		IArchitectureDSLGenerator {

	private static final String TEMPLATE_PATH = "templates::JenkinsConfig::main";
	private final String GENERATE_OUTPUT_MESSAGE = "Generated Jenkins test configuration to %s";

	private String outputMessage;

	public JenkinsTestConfigurationGenerator() {
		outputMessage = "JenkinsTestConfigurationGenerator did not generate anything.";
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
		ArchDslGenerator.evaluateTemplate(TEMPLATE_PATH, null, contents,
				targetDir);
		outputMessage = String.format(GENERATE_OUTPUT_MESSAGE,
				targetDir.toOSString());
	}

	@Override
	public Iterable<String> getRequiredParameters() {
		return new ArrayList<String>();
	}

	@Override
	public String getOutputMessage() {
		return outputMessage;
	}
}
