package com.btc.commons.emf.diagnostics;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.btc.commons.emf.diagnostics.internal.DiagnosticsRegistry;

public class DiagnosticsBundle implements BundleActivator {
	public final static String BUNDLE_ID = "com.btc.commons.emf.diagnostics";
	public final static String DIAGNOSTICS_EXTENSION_POINT = BUNDLE_ID
			+ ".diagnostics";

	private static BundleContext context;
	private static DiagnosticsBundle instance;

	static BundleContext getContext() {
		return context;
	}

	public static DiagnosticsBundle getInstance() {
		return instance;
	}

	private DiagnosticsRegistry diagnosticsRegistry;

	public IDiagnosticsRegistry getDiagnosticsRegistry() {
		return this.diagnosticsRegistry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		DiagnosticsBundle.context = bundleContext;
		DiagnosticsBundle.instance = this;
		initializeDiagnosticsRegistry();
	}

	private void initializeDiagnosticsRegistry() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		this.diagnosticsRegistry = new DiagnosticsRegistry();
		extensionRegistry.addListener(this.diagnosticsRegistry,
				DIAGNOSTICS_EXTENSION_POINT);
	}

	private void uninitializeDiagnosticsRegistry() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		extensionRegistry.removeListener(this.diagnosticsRegistry);
		this.diagnosticsRegistry = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		uninitializeDiagnosticsRegistry();
		DiagnosticsBundle.instance = null;
		DiagnosticsBundle.context = null;
	}

}
