package com.btc.arch.visualstudio;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.btc.arch.visualstudio.messages"; //$NON-NLS-1$
	public static String CSProjDependencyParser_CannotResolveTarget;
	public static String CSProjDependencyParser_CannotResolveReference;
	public static String CSProjDependencyParser_CannotResolveTargetButCanGuess;
	public static String CSProjDependencyParser_NoSourceURI;
	public static String CSProjDependencyParser_ParseError;
	public static String CSProjDependencyParser_RootNamespaceDifferent;
	public static String CSProjDependencyParser_UnexpectedException;
	public static String CSProjDependencyParser_UsingBasename;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
