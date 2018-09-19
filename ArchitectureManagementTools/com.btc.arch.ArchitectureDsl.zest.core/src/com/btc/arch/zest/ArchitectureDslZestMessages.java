/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package com.btc.arch.zest;

import org.eclipse.osgi.util.NLS;

public class ArchitectureDslZestMessages extends NLS {
	public static final String BUNDLE_NAME = "com.btc.arch.zest.messages"; //$NON-NLS-1$
	public static String GraphCreatorInterpreter_0;
	public static String ArchitectureDslImport_1;
	public static String ArchitectureDslImport_2;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ArchitectureDslZestMessages.class);
	}

	private ArchitectureDslZestMessages() {
	}
}
