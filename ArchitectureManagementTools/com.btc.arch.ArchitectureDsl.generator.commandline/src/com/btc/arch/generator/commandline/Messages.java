package com.btc.arch.generator.commandline;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.btc.arch.generator.commandline.messages"; //$NON-NLS-1$
	public static String ArchDslCommandLineGenerator_OptionHelp;
	public static String ArchDslCommandLineGenerator_ErrorsBeforeGeneration;
	public static String ArchDslCommandLineGenerator_GeneratorFailure;
	public static String ArchDslCommandLineGenerator_NoGeneratorSuccessful;

	public static String ArchDslCommandLineGenerator_OptionGeneratorProperties;
	public static String ArchDslCommandLineGenerator_OptionProjectPath;
	public static String ArchDslCommandLineGenerator_OptionSourceFile;
	public static String ArchDslCommandLineGenerator_SourceFileResourceNull;
	public static String ArchDslCommandLineGenerator_UnexpectedExceptionDuringInitialization;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
