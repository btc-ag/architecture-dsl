package com.btc.arch.xtext.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.btc.arch.xtext.util.messages"; //$NON-NLS-1$
	public static String XtextResourceUtil_InvalidResourceSet;
	public static String XtextResourceUtil_NotALazyLinkingResource;
	public static String XtextResourceUtil_UnknownObjectType;
	public static String XtextResourceUtil_UnknownResourceOrObjectType;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
