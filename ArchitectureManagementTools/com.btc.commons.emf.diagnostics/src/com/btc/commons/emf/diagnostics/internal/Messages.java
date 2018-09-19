package com.btc.commons.emf.diagnostics.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.btc.commons.emf.diagnostics.internal.messages"; //$NON-NLS-1$
	public static String DiagnosticsRegistry_DiagnosticNotRegistered;
	public static String DiagnosticsRegistry_DuplicateShortPrefix;
	public static String DiagnosticsRegistry_RegisterDiagnostic;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
