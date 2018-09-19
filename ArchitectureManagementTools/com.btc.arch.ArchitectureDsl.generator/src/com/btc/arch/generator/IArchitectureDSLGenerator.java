package com.btc.arch.generator;

import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.btc.arch.base.IContext;

public interface IArchitectureDSLGenerator {
	void generate(Collection<? extends EObject> primaryContents,
			Collection<? extends EObject> allContents, IPath targetDir,
			String parameter, IContext contextParameters, boolean modelValid)
			throws ArchDslGeneratorException;

	Iterable<String> getRequiredParameters();

	String getOutputMessage();
}
