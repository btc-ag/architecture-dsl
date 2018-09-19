package com.btc.arch.generator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xpand2.XpandExecutionContextImpl;
import org.eclipse.xpand2.XpandFacade;
import org.eclipse.xpand2.output.Outlet;
import org.eclipse.xpand2.output.OutputImpl;
import org.eclipse.xtend.type.impl.java.JavaBeansMetaModel;

import com.btc.commons.java.PropertyManager;
import com.btc.commons.java.PropertyManager.PropertyException;

public class ArchDslGenerator {

	private static PropertyManager propertyManager;

	@Deprecated
	public ArchDslGenerator(File propertiesFile)
			throws ArchDslGeneratorException {
		try {
			propertyManager = new PropertyManager();
			propertyManager.setProperties(new FileReader(propertiesFile),
					propertiesFile.getName());
		} catch (IOException e) {
			throw new ArchDslGeneratorException(MessageFormat.format(
					Messages.ArchDslGenerator_PropertiesFileNotFound,
					propertiesFile));
		}
	}

	public ArchDslGenerator(Map<String, Object> propertyMap)
			throws ArchDslGeneratorException {
		propertyManager = new PropertyManager();
		propertyManager.setProperties(propertyMap);
	}

	public static void evaluateTemplate(String definitionName,
			List<Object> params, EObject source, IPath targetDirectory)
			throws ArchDslGeneratorException {
		evaluateTemplate(definitionName, params,
				Collections.singletonList(source), targetDirectory);
	}

	public static void evaluateTemplate(String definitionName,
			List<Object> params, Collection<? extends EObject> sourceObjects,
			IPath targetDirectory) throws ArchDslGeneratorException {
		OutputImpl output = new OutputImpl();
		Outlet outlet = new Outlet(targetDirectory.toOSString());
		output.addOutlet(outlet);
		XpandExecutionContextImpl execCtx = new XpandExecutionContextImpl(
				output, null);

		execCtx.registerMetaModel(new JavaBeansMetaModel());
		XpandFacade facade = XpandFacade.create(execCtx);
		facade.evaluate2(definitionName, sourceObjects, params);
	}

	public IPath getTargetDir() throws ArchDslGeneratorException {
		String targetDirectory;
		try {
			targetDirectory = (String) propertyManager.getProperty("targetDir"); //$NON-NLS-1$
		} catch (PropertyException e) {
			throw new ArchDslGeneratorException(e);
		}
		return Path.fromOSString(targetDirectory);
	}
}
