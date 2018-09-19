package com.btc.arch;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.btc.arch.base.IServiceRegistry;
import com.btc.arch.diagnostics.api.service.IProcessingElementService;
import com.btc.arch.internal.EclipseProcessingElementExtensionRegistry;

public class ArchDslBundle implements BundleActivator {

	public static final String PROCESSING_ELEMENT_EXTENSION_POINT = "com.btc.arch.ArchitectureDsl.diagnosticProcessingElements";
	private EclipseProcessingElementExtensionRegistry processingElementExtensionRegistry;
	private static ArchDslBundle instance;

	@Override
	public void start(final BundleContext context) throws Exception {
		instance = this;
		final IExtensionRegistry extensionRegistry = Platform
				.getExtensionRegistry();
		this.processingElementExtensionRegistry = new EclipseProcessingElementExtensionRegistry(
				extensionRegistry.getExtensionPoint(
						ArchDslBundle.PROCESSING_ELEMENT_EXTENSION_POINT)
						.getExtensions());
		extensionRegistry.addListener(this.processingElementExtensionRegistry,
				PROCESSING_ELEMENT_EXTENSION_POINT);
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		instance = null;
		final IExtensionRegistry extensionRegistry = Platform
				.getExtensionRegistry();
		extensionRegistry
				.removeListener(this.processingElementExtensionRegistry);
		this.processingElementExtensionRegistry = null;
	}

	public static ArchDslBundle getInstance() {
		return instance;
	}

	public IServiceRegistry<IProcessingElementService> getProcessingElementExtensionRegistry() {
		return this.processingElementExtensionRegistry;
	}

}
