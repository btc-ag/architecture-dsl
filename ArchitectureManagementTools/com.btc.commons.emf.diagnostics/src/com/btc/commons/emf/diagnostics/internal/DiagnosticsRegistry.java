package com.btc.commons.emf.diagnostics.internal;

import java.text.MessageFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import com.btc.commons.emf.diagnostics.DiagnosticDescriptor;
import com.btc.commons.emf.diagnostics.DiagnosticsBundle;
import com.btc.commons.emf.diagnostics.IDiagnosticDescriptor;
import com.btc.commons.emf.diagnostics.IDiagnosticResult;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase.Severity;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticSubjectDescriber;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;

public class DiagnosticsRegistry implements IRegistryEventListener,
		IDiagnosticsRegistry {
	private static final Logger logger = Logger
			.getLogger(DiagnosticsRegistry.class);
	private static final String CONFIG_ELEMENT_DIAGNOSTIC = "diagnostic"; //$NON-NLS-1$
	private static final String CONFIG_ELEMENT_SHORT_PREFIX = "short-prefix"; //$NON-NLS-1$

	private final HashMap<String, IDiagnosticDescriptor> diagnosticsRegistry;
	private final HashMap<String, String> shortPrefixes;
	private int count = 1;

	public DiagnosticsRegistry() {
		this.diagnosticsRegistry = new HashMap<String, IDiagnosticDescriptor>();
		this.shortPrefixes = new HashMap<String, String>();
		added(Platform
				.getExtensionRegistry()
				.getExtensionPoint(
						DiagnosticsBundle.DIAGNOSTICS_EXTENSION_POINT)
				.getExtensions());
	}

	@Override
	public void added(IExtension[] extensions) {
		final IDiagnosticExtensionHandler handler = new IDiagnosticExtensionHandler() {

			@Override
			public void handleDiagnostic(IExtension extension, String fullId,
					String defaultSeverityString, String description,
					String subjectType, String documentationLink) {
				logger.info(MessageFormat
						.format(Messages.DiagnosticsRegistry_RegisterDiagnostic,
								fullId));

				IDiagnosticResult.Severity defaultSeverity;
				if (defaultSeverityString.equals("INFO")) { //$NON-NLS-1$
					defaultSeverity = IDiagnosticResult.Severity.INFO;
				} else if (defaultSeverityString.equals("WARNING")) { //$NON-NLS-1$
					defaultSeverity = IDiagnosticResult.Severity.WARNING;
				} else if (defaultSeverityString.equals("ERROR")) { //$NON-NLS-1$
					defaultSeverity = IDiagnosticResult.Severity.ERROR;
				} else if (defaultSeverityString.equals("FATAL")) { //$NON-NLS-1$
					defaultSeverity = IDiagnosticResult.Severity.FATAL;
				} else {
					defaultSeverity = IDiagnosticResult.Severity.UNCHECKED;
				}
				// TODO override with severity from configuration
				diagnosticsRegistry.put(fullId, new DiagnosticDescriptor(
						fullId, "R" + count++, defaultSeverity, description,
						subjectType, documentationLink));
			}

			@Override
			public void handleShortPrefix(IExtension extension,
					final String shortPrefix) {
				if (shortPrefixes.containsKey(shortPrefix)) {
					final String message = MessageFormat.format(
							Messages.DiagnosticsRegistry_DuplicateShortPrefix,
							shortPrefix, shortPrefixes.get(shortPrefix),
							extension.getNamespaceIdentifier());
					Platform.getLog(
							Platform.getBundle(DiagnosticsBundle.BUNDLE_ID))
							.log(new Status(IStatus.WARNING,
									DiagnosticsBundle.BUNDLE_ID, message));
				}
				shortPrefixes.put(shortPrefix,
						extension.getNamespaceIdentifier());
			}
		};
		processDiagnosticExtensions(extensions, handler);
	}

	protected void processDiagnosticExtensions(IExtension[] extensions,
			IDiagnosticExtensionHandler handler) {
		for (IExtension extension : extensions) {
			final IConfigurationElement[] configurationElements = extension
					.getConfigurationElements();
			for (IConfigurationElement configurationElement : configurationElements) {
				if (configurationElement.getName().equals(
						CONFIG_ELEMENT_SHORT_PREFIX)) {
					final String shortPrefix = configurationElement
							.getAttribute("value"); //$NON-NLS-1$
					handler.handleShortPrefix(extension, shortPrefix);
				}
				if (configurationElement.getName().equals(
						CONFIG_ELEMENT_DIAGNOSTIC)) {
					final String fullId = configurationElement
							.getAttribute("id"); //$NON-NLS-1$ 
					final String defaultSeverityString = configurationElement
							.getAttribute("default-severity"); //$NON-NLS-1$
					final String description = configurationElement
							.getAttribute("description"); //$NON-NLS-1$
					final String subjectType = configurationElement
							.getAttribute("subject-type"); //$NON-NLS-1$
					final String documentationLink = configurationElement
							.getAttribute("documentation-link"); //$NON-NLS-1$
					handler.handleDiagnostic(extension, fullId,
							defaultSeverityString, description, subjectType,
							documentationLink);
				}
			}
		}
	}

	@Override
	public void removed(IExtension[] extensions) {
		processDiagnosticExtensions(extensions,
				new IDiagnosticExtensionHandler() {

					@Override
					public void handleShortPrefix(IExtension extension,
							String shortPrefix) {
						shortPrefixes.remove(shortPrefix);
					}

					@Override
					public void handleDiagnostic(IExtension extension,
							String fullId, String defaultSeverityString,
							String description, String subjectType,
							String documentationLink) {
						diagnosticsRegistry.remove(fullId);
					}
				});
	}

	@Override
	public void added(IExtensionPoint[] extensionPoints) {
		// ignore
	}

	@Override
	public void removed(IExtensionPoint[] extensionPoints) {
		// ignore
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.btc.commons.emf.diagnostics.internal.IDiagnosticsRegistry#
	 * getDiagnosticDescriptor(java.lang.String)
	 */
	@Override
	public IDiagnosticDescriptor getDiagnosticDescriptor(String identifier) {
		if (this.diagnosticsRegistry.containsKey(identifier)) {
			return this.diagnosticsRegistry.get(identifier);
		} else {
			// TODO try short prefixes
		}
		return null;
	}

	public boolean isDiagnosticActive(String identifier) {
		return getDiagnosticDescriptor(identifier).getBaseSeverity() != Severity.UNCHECKED;
	}

	@Override
	public IDiagnosticResultFactory createDiagnosticResultFactory(
			IDiagnosticSubjectDescriber describer) {
		return new DiagnosticResultFactory(this, describer);
	}
}
