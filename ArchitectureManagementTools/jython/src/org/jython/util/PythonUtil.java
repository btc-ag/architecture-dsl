package org.jython.util;

import org.python.util.PythonInterpreter;

public class PythonUtil {

	public static void addExtraSysPath(PythonInterpreter interpreter,
			String extraPythonPath) {
		if (extraPythonPath != null && !"".equals(extraPythonPath.trim())) {
			interpreter.exec("import sys");
			interpreter.exec("if not " + "'" + extraPythonPath + "'"
					+ " in sys.path: sys.path.insert(0, '" + extraPythonPath
					+ "')");
		}
	}

}
