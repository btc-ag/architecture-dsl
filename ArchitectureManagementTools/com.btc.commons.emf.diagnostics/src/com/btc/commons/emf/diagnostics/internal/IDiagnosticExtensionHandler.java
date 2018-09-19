package com.btc.commons.emf.diagnostics.internal;

import org.eclipse.core.runtime.IExtension;

public interface IDiagnosticExtensionHandler {

	void handleDiagnostic(IExtension extension, String fullId,
			String defaultSeverityString, String description,
			String subjectType, String documentationLink);

	void handleShortPrefix(IExtension extension, final String shortPrefix);

}