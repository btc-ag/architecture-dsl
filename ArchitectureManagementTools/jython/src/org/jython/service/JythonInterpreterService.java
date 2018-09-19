/*******************************************************************************
 * Copyright (c) 2008 Guillermo Gonzalez.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.jython.service;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.jython.util.PythonUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.python.core.Py;
import org.python.core.PySystemState;
import org.python.util.InteractiveInterpreter;
import org.python.util.PythonInterpreter;

/**
 * @author Guillermo Gonzalez <guillo.gonzo@gmail.com>
 * 
 */
public class JythonInterpreterService {

	public static final String JYTHON_SERVICE_BUNDLE = JythonServicePlugin.PLUGIN_ID;
	public static final String JYTHON_JAR_BUNDLE = JYTHON_SERVICE_BUNDLE;

	private final IPath HOME;
	private final IPath LIB;
	private BundleContext context = null;
	private AllBundleClassLoader allBundleClassLoader = null;
	private Properties postProperties = null;

	public JythonInterpreterService(BundleContext context) {
		super();
		this.context = context;
		HOME = new Path("/");
		LIB = new Path("Lib");
		setEnvironment();
	}

	private void setEnvironment() {
		Bundle jythonLibBundle = getJythonJarBundle(context);

		// initialize the Jython runtime
		postProperties = new Properties();
		Properties preProperties = System.getProperties();
		try {
			String lib = FileLocator.resolve(
					FileLocator.findEntries(jythonLibBundle, LIB)[0]).getPath();
			String jythonPath = (String) preProperties.get("JYTHONPATH");
			jythonPath = jythonPath != null ? jythonPath + ":" : "";
			preProperties.put("JYTHONPATH", jythonPath + LIB + ":" + lib
					+ "site-packages");
			String home = FileLocator.toFileURL(
					FileLocator.findEntries(jythonLibBundle, HOME)[0])
					.getPath();
			postProperties.put("python.home", home);
		} catch (IOException e) {
			JythonServicePlugin
					.getDefault()
					.getLog()
					.log(new Status(IStatus.ERROR, JythonServicePlugin.class
							.getPackage().getName(), e.getMessage(), e));
		}
		postProperties.put("python.cachedir", JythonServicePlugin.getDefault()
				.getStateLocation().makeAbsolute().append("cachedir")
				.toPortableString());
		// don't respect java accessibility, so that we can access protected
		// members on subclasses
		// postProperties.put("python.security.respectJavaAccessibility",
		// "false");
		postProperties.put("python.environment", "shell"); // before was None
		postProperties.put("python.packages.paths",
				"java.class.path, sun.boot.class.path");
		postProperties.put("python.packages.directories", "java.ext.dirs");
		if (JythonServicePlugin.getDefault().isDebugging()) {
			postProperties.put("python.verbose", "debug");
		}

		try {
			allBundleClassLoader = new AllBundleClassLoader(
					context.getBundles(),
					JythonInterpreterService.class.getClassLoader());
			PySystemState.initialize(preProperties, postProperties,
					new String[0], allBundleClassLoader);
			PySystemState sys = Py.getSystemState();
			sys.setClassLoader(allBundleClassLoader);
			String[] packageNames = allBundleClassLoader.getPackageNames();
			for (int i = 0; i < packageNames.length; ++i) {
				PySystemState.add_package(packageNames[i]);
			}
		} catch (Exception e) {
			JythonServicePlugin
					.getDefault()
					.getLog()
					.log(new Status(IStatus.ERROR, JythonServicePlugin.class
							.getPackage().getName(), e.getMessage(), e));
			throw new RuntimeException(e);
		}
	}

	private Bundle getJythonJarBundle(BundleContext context) {
		Bundle jythonLibBundle = null;
		for (Bundle bundle : context.getBundles()) {
			if (JYTHON_JAR_BUNDLE.equals(bundle.getSymbolicName().trim())) {
				jythonLibBundle = bundle;
				break;
			}
		}
		return jythonLibBundle;
	}

	public PythonInterpreter getPythonInterpreter(String extraPythonPath) {
		PythonInterpreter interpreter = new PythonInterpreter();
		PythonUtil.addExtraSysPath(interpreter, extraPythonPath);
		return interpreter;
	}

	public InteractiveInterpreter getInteractiveInterpreter(
			String extraPythonPath) {
		InteractiveInterpreter interpreter = new InteractiveInterpreter();
		PythonUtil.addExtraSysPath(interpreter, extraPythonPath);
		return interpreter;
	}

}
