
package com.btc.arch;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class ArchitectureDslStandaloneSetup extends ArchitectureDslStandaloneSetupGenerated{

	public static void doSetup() {
		new ArchitectureDslStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

