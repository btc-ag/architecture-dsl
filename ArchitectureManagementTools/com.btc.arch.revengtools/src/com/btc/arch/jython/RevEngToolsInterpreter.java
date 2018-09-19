package com.btc.arch.jython;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.jython.service.JythonInterpreterService;
import org.jython.service.JythonServicePlugin;
import org.jython.util.PythonUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.python.core.PyCode;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

class OSGIUtil {
	public static boolean runningInContainer() {
		return Platform.isRunning();
	}

	public static PythonInterpreter getDefaultInterpreter() {
		if (OSGIUtil.runningInContainer()) {
			try {
				Platform.getBundle(
						JythonInterpreterService.JYTHON_SERVICE_BUNDLE).start();
				Platform.getBundle(
						JythonInterpreterService.JYTHON_SERVICE_BUNDLE)
						.loadClass("org.jython.service.JythonServicePlugin");
				return JythonServicePlugin.getPythonInterpreter();
			} catch (ClassNotFoundException e) {
				Logger.getLogger(OSGIUtil.class.getPackage().getName()).log(
						Level.WARNING, "Jython not correctly configured", e);
			} catch (BundleException e) {
				Logger.getLogger(OSGIUtil.class.getPackage().getName()).log(
						Level.WARNING, "Jython not correctly configured", e);
			}
		}
		return new PythonInterpreter();
	}
}

public class RevEngToolsInterpreter {
	// TODO it would be nice if there was an IPythonInterpreter interface, which
	// could be implemented by this class
	private static final String REVENGTOOLS_PROPERTIES = "revengtools.properties";
	private static final String REVENGTOOLS_CONFIG = getProperties()
			.getProperty("REVENGTOOLS_CONFIG");
	private static final String REVENGTOOLS_BASEDIR = getProperties()
			.getProperty("REVENGTOOLS_BASEDIR");
	private boolean configured = false;
	private PythonInterpreter wrappee;

	public RevEngToolsInterpreter() {
		this(OSGIUtil.getDefaultInterpreter());
	}

	public RevEngToolsInterpreter(PythonInterpreter wrappee) {
		this.wrappee = wrappee;
		PythonUtil.addExtraSysPath(wrappee, REVENGTOOLS_BASEDIR);
		// PythonUtil.addExtraSysPath(wrappee, getJythonLibDir());
		if (getJythonBaseDir() != null)
			exec("sys.prefix = \"" + getJythonBaseDir() + "\"");
		Logger.getLogger(getClass().getPackage().getName()).info(
				"sys.path (before site) = " + get("path", List.class));
		exec("import site");
		Logger.getLogger(getClass().getPackage().getName()).info(
				"sys.path (after site) = " + get("path", List.class));
		exec("import os");
		exec("os.environ['CONFIG'] = '" + REVENGTOOLS_CONFIG + "'");
		exec("os.environ['FLAVORS'] = 'eclipse'");
	}

	private String getJythonBaseDir() {
		Bundle bundle = Platform.getBundle("jython");
		if (bundle != null)
			// return bundle.getResource("/").getFile();
			return "D:\\PRINS-Analyse\\EclipseWorkspaces\\ArchitekturDSL\\jython";
		else
			return null;
	}

	public String getBaseDir() {
		return REVENGTOOLS_BASEDIR;
	}

	public void execfile_RevEngTools(String script) {
		execfile(REVENGTOOLS_BASEDIR + File.separator + script);
	}

	public void doConfigure() {
		if (!configured) {
			exec("from commons.configurator import Configurator");
			exec("Configurator().default()");
			configured = true;
		}
	}

	private static Properties getProperties() {
		Properties properties = new Properties();
		try {
			properties.load(RevEngToolsInterpreter.class
					.getResourceAsStream(REVENGTOOLS_PROPERTIES));
		} catch (IOException e) {
			Logger.getLogger(
					RevEngToolsInterpreter.class.getPackage().getName()).log(
					Level.SEVERE,
					"Internal error when loading properties "
							+ REVENGTOOLS_PROPERTIES, e);
		}
		return properties;
	}

	public static boolean checkConfiguration() {
		try {
			RevEngToolsInterpreter interpreter = new RevEngToolsInterpreter();
			return checkConfiguration(interpreter);
		} catch (PyException e) {
			Logger.getLogger(
					RevEngToolsInterpreter.class.getPackage().getName()).log(
					Level.WARNING, "RevEngTools not correctly configured", e);
			return false;
		}
	}

	public static boolean checkConfiguration(RevEngToolsInterpreter interpreter) {
		try {
			interpreter.doConfigure();
			return true;
		} catch (PyException e) {
			Logger.getLogger(
					RevEngToolsInterpreter.class.getPackage().getName()).log(
					Level.WARNING, "RevEngTools not correctly configured", e);
			return false;
		}
	}

	public void cleanup() {
		wrappee.cleanup();
	}

	public PyCode compile(Reader reader, String filename) {
		return wrappee.compile(reader, filename);
	}

	public PyCode compile(Reader reader) {
		return wrappee.compile(reader);
	}

	public PyCode compile(String script, String filename) {
		return wrappee.compile(script, filename);
	}

	public PyCode compile(String script) {
		return wrappee.compile(script);
	}

	public boolean equals(Object obj) {
		return wrappee.equals(obj);
	}

	public PyObject eval(PyObject code) {
		return wrappee.eval(code);
	}

	public PyObject eval(String s) {
		return wrappee.eval(s);
	}

	public void exec(PyObject code) {
		wrappee.exec(code);
	}

	public void exec(String s) {
		wrappee.exec(s);
	}

	public void execfile(InputStream s, String name) {
		wrappee.execfile(s, name);
	}

	public void execfile(InputStream s) {
		wrappee.execfile(s);
	}

	public void execfile(String filename) {
		wrappee.execfile(filename);
	}

	public <T> T get(String name, Class<T> javaclass) {
		return wrappee.get(name, javaclass);
	}

	public PyObject get(String name) {
		return wrappee.get(name);
	}

	public PyObject getLocals() {
		return wrappee.getLocals();
	}

	public PySystemState getSystemState() {
		return wrappee.getSystemState();
	}

	public int hashCode() {
		return wrappee.hashCode();
	}

	public void set(String name, Object value) {
		wrappee.set(name, value);
	}

	public void set(String name, PyObject value) {
		wrappee.set(name, value);
	}

	public void setErr(OutputStream outStream) {
		wrappee.setErr(outStream);
	}

	public void setErr(PyObject outStream) {
		wrappee.setErr(outStream);
	}

	public void setErr(Writer outStream) {
		wrappee.setErr(outStream);
	}

	public void setIn(InputStream inStream) {
		wrappee.setIn(inStream);
	}

	public void setIn(PyObject inStream) {
		wrappee.setIn(inStream);
	}

	public void setIn(Reader inStream) {
		wrappee.setIn(inStream);
	}

	public void setLocals(PyObject d) {
		wrappee.setLocals(d);
	}

	public void setOut(OutputStream outStream) {
		wrappee.setOut(outStream);
	}

	public void setOut(PyObject outStream) {
		wrappee.setOut(outStream);
	}

	public void setOut(Writer outStream) {
		wrappee.setOut(outStream);
	}

	public String toString() {
		return wrappee.toString();
	}
}
