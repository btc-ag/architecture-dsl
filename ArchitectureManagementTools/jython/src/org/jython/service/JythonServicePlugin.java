/*******************************************************************************
 * Copyright (c) 2008 Guillermo Gonzalez.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.jython.service;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.python.core.PyException;
import org.python.core.PySystemState;
import org.python.util.InteractiveInterpreter;
import org.python.util.PythonInterpreter;

/**
 * 
 * @author Guillermo Gonzalez <guillo.gonzo@gmail.com>
 *
 */
public class JythonServicePlugin extends Plugin {
	
	public final static String PLUGIN_ID = "jython";  
	private static JythonServicePlugin plugin;
	
	private JythonInterpreterService service;
	
	
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		service = new JythonInterpreterService(context);
	}

	public void stop(BundleContext context) throws Exception {
		getLog().log(new Status(IStatus.INFO, PLUGIN_ID, "stopping Jython Service"));
		try {
			PySystemState.exit();	
		} 
		catch (PyException e) {
			if (!e.type.toString().startsWith("<type 'exceptions.SystemExit'>"))
				getLog().log(new Status(IStatus.WARNING, getClass().getPackage().getName(), e.toString()));
		}
		catch (Exception e) {
			getLog().log(new Status(IStatus.WARNING, getClass().getPackage().getName(), e.toString()));
		}
		super.stop(context);
	} 
	
	public static JythonServicePlugin getDefault() {
		return plugin;
	}
	
	public static PythonInterpreter getPythonInterpreter(String pythonPath) {
		return getDefault().getService().getPythonInterpreter(pythonPath);
	}
	
	public static PythonInterpreter getPythonInterpreter() {
		return getPythonInterpreter("");
	}
	
	public static InteractiveInterpreter getInteractiveInterpreter(String pythonPath) {
		return getDefault().getService().getInteractiveInterpreter(pythonPath);
	}
	
	public static InteractiveInterpreter getInteractiveInterpreter() {
		return getInteractiveInterpreter("");
	}
	
	private JythonInterpreterService getService() {
		return service;
	}

}
