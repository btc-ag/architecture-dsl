package com.btc.arch.internal;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;

import com.btc.arch.base.IServiceRegistry;
import com.btc.arch.diagnostics.api.service.IProcessingElementService;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IterationUtils;

public class EclipseProcessingElementExtensionRegistry implements
		IRegistryEventListener, IServiceRegistry<IProcessingElementService> {

	// TODO handle conflicting short ids

	public interface IProcessingElementExtensionHandler {
		public void handleProcessingElement(String fullId, String shortId,
				String description, IProcessingElementService service);
	}

	private static class ProcessingElementDescriptor {
		private final String fullId;
		private final String shortId;
		private final String description;
		private final IProcessingElementService service;

		ProcessingElementDescriptor(final String fullId, final String shortId,
				final String description,
				final IProcessingElementService service) {
			this.fullId = fullId;
			this.shortId = shortId;
			this.description = description;
			this.service = service;
		}

		public String getFullId() {
			return this.fullId;
		}

		public String getShortId() {
			return this.shortId;
		}

		public String getDescription() {
			return this.description;
		}

		public IProcessingElementService getService() {
			return this.service;
		}
	}

	private static final Logger logger = Logger
			.getLogger(EclipseProcessingElementExtensionRegistry.class);
	private static final String CONFIG_ELEMENT_PROCESSING_ELEMENT = "processingElement"; //$NON-NLS-1$
	private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SHORT_ID = "short-id"; //$NON-NLS-1$
	private static final String ATTRIBUTE_DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$

	private final Map<String, ProcessingElementDescriptor> processingElementRegistry;

	public EclipseProcessingElementExtensionRegistry(
			final IExtension[] initialExtensions) {
		this.processingElementRegistry = new HashMap<String, ProcessingElementDescriptor>();
		added(initialExtensions);
	}

	@Override
	public void added(final IExtension[] extensions) {
		final IProcessingElementExtensionHandler handler = new IProcessingElementExtensionHandler() {

			@Override
			public void handleProcessingElement(final String fullId,
					final String shortId, final String description,
					final IProcessingElementService service) {
				logger.info(MessageFormat.format(
						"Registered processing element service with id {0}",
						fullId));

				EclipseProcessingElementExtensionRegistry.this.processingElementRegistry
						.put(fullId, new ProcessingElementDescriptor(fullId,
								shortId, description, service));
			}

		};
		processExtensions(extensions, handler);
	}

	protected void processExtensions(final IExtension[] extensions,
			final IProcessingElementExtensionHandler handler) {
		for (final IExtension extension : extensions) {
			final IConfigurationElement[] configurationElements = extension
					.getConfigurationElements();
			for (final IConfigurationElement configurationElement : configurationElements) {
				if (configurationElement.getName().equals(
						CONFIG_ELEMENT_PROCESSING_ELEMENT)) {
					final String fullId = configurationElement
							.getAttribute(ATTRIBUTE_ID);
					final String shortId = configurationElement
							.getAttribute(ATTRIBUTE_SHORT_ID);
					final String description = configurationElement
							.getAttribute(ATTRIBUTE_DESCRIPTION);

					try {
						final IProcessingElementService service = (IProcessingElementService) configurationElement
								.createExecutableExtension(ATTRIBUTE_CLASS);

						handler.handleProcessingElement(fullId, shortId,
								description, service);
					} catch (final CoreException e) {
						logger.error(
								MessageFormat
										.format("Cannot instantiate processing element with id {0}",
												fullId), e);
					}
				}
			}
		}
	}

	@Override
	public void removed(final IExtension[] extensions) {
		processExtensions(extensions, new IProcessingElementExtensionHandler() {

			@Override
			public void handleProcessingElement(final String fullId,
					final String shortId, final String description,
					final IProcessingElementService service) {
				EclipseProcessingElementExtensionRegistry.this.processingElementRegistry
						.remove(fullId);

			}
		});
	}

	@Override
	public void added(final IExtensionPoint[] extensionPoints) {
		// ignore
	}

	@Override
	public void removed(final IExtensionPoint[] extensionPoints) {
		// ignore
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.btc.arch.diagnostics.api.IServiceRegistry#getAllServices()
	 */
	@Override
	public Iterable<IProcessingElementService> getAllServices() {
		return IterationUtils
				.map(this.processingElementRegistry.values(),
						new IMapFunctor<ProcessingElementDescriptor, IProcessingElementService>() {

							@Override
							public IProcessingElementService mapItem(
									final ProcessingElementDescriptor obj) {
								return obj.getService();
							}
						});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.btc.arch.diagnostics.api.IServiceRegistry#getServiceByID(java.lang
	 * .String)
	 */
	@Override
	public IProcessingElementService getServiceByID(final String id) {
		final ProcessingElementDescriptor serviceByFullID = this.processingElementRegistry
				.get(id);
		if (serviceByFullID != null) {
			return serviceByFullID.getService();
		} else {
			for (final ProcessingElementDescriptor descriptor : this.processingElementRegistry
					.values()) {
				if (descriptor.getShortId() != null
						&& descriptor.getShortId().equals(id)) {
					return descriptor.getService();
				}
			}
		}
		return null;
	}

}
