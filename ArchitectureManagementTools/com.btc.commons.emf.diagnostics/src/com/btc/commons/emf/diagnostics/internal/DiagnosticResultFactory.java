package com.btc.commons.emf.diagnostics.internal;

import java.text.MessageFormat;

import org.apache.log4j.Logger;

import com.btc.commons.emf.diagnostics.IDiagnosticDescriptor;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticSubjectDescriber;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;

public class DiagnosticResultFactory implements IDiagnosticResultFactory {
	private final IDiagnosticsRegistry registry;
	private final IDiagnosticSubjectDescriber describer;
	private static final Logger logger = Logger
			.getLogger(DiagnosticResultFactory.class);

	public DiagnosticResultFactory(final IDiagnosticsRegistry registry,
			final IDiagnosticSubjectDescriber describer) {
		this.registry = registry;
		this.describer = describer;
	}

	@Override
	public IDiagnosticResultBase createDiagnosticResult(String diagnostic,
			Object subject, String explanation) {
		final IDiagnosticDescriptor diagnosticDescriptor = registry
				.getDiagnosticDescriptor(diagnostic);
		if (diagnosticDescriptor == null) {
			logger.warn(MessageFormat.format(
					Messages.DiagnosticsRegistry_DiagnosticNotRegistered,
					diagnostic));
			// TODO introduce strict checking mode, which throws an exception in
			// this case
		}
		return new DiagnosticResult(subject,
				describer.describeSubjectType(subject), diagnosticDescriptor,
				explanation);
	}

}
