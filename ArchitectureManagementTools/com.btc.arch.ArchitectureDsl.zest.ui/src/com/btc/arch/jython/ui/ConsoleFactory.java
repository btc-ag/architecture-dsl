package com.btc.arch.jython.ui;

//import java.util.logging.Logger;
//
//import org.eclipse.ui.console.ConsolePlugin;
//import org.eclipse.ui.console.IConsole;
//import org.eclipse.ui.console.IConsoleFactory;
//import org.eclipse.ui.console.IConsoleManager;
//import org.eclipse.ui.console.IOConsole;
//import org.python.core.PySystemState;
//import org.python.util.InteractiveConsole;
//
//class InterpreterThread extends Thread {
//	private InteractiveConsole interpreter = null;
//
//	public InterpreterThread(IOConsole console) {
//		this.interpreter = new InteractiveConsole();
//		// this.interpreter.
//		this.interpreter.setIn(console.getInputStream());
//		this.interpreter.setOut(console.newOutputStream());
//		this.interpreter.setErr(console.newOutputStream());
//	}
//
//	@Override
//	public void run() {
//		interpreter.interact();
//		Logger logger = Logger.getLogger("com.btc.arch.jython");
//		logger.info("Jython interpreter interaction finished");
//	}
//}
//
//public class ConsoleFactory implements IConsoleFactory {
//	private IOConsole console = null;
//
//	@Override
//	public void openConsole() {
//		if (this.console == null) {
//			this.console = new IOConsole("Jython Console", "jython", null,
//					"utf8", true);
//			IConsoleManager manager = ConsolePlugin.getDefault()
//					.getConsoleManager();
//			// IConsole[] existing = manager.getConsoles();
//			// boolean exists = false;
//			// for (int i = 0; i if(console == existing )
//			// exists = true;
//			// }
//			// if(! exists)
//			manager.addConsoles(new IConsole[] { this.console });
//
//			manager.showConsoleView(console);
//
//			PySystemState.initialize();
//
//			new InterpreterThread(console).start();
//		}
//
//	}
// }
