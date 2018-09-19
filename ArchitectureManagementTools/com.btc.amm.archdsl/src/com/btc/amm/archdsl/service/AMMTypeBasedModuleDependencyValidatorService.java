package com.btc.amm.archdsl.service;

import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import com.btc.amm.archdsl.AMMTypeBasedModuleDependencyValidator;
import com.btc.arch.base.ConfigurationError;
import com.btc.arch.base.IContext;
import com.btc.arch.diagnostics.api.service.SimpleProcessingElementServiceBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;

public class AMMTypeBasedModuleDependencyValidatorService extends
		SimpleProcessingElementServiceBase {

	private static final String PARAMETER_DIFFERENT_DOMAINS = "DifferentDomains";
	private static final String PARAMETER_SAME_DOMAIN = "SameDomain";

	@Override
	protected IDiagnosticResultSource<Collection<? extends EObject>> createDiagnosticResultSource(
			final IContext contextParameters,
			final IDiagnosticsRegistry diagnosticsRegistry,
			final IDiagnosticResultFactory diagnosticResultFactory)
			throws ConfigurationError {
		final String sameDomainRules = contextParameters
				.getParameter(PARAMETER_SAME_DOMAIN);
		final String differentDomainsRules = contextParameters
				.getParameter(PARAMETER_DIFFERENT_DOMAINS);
		if (sameDomainRules == null) {
			throw new ConfigurationError(MessageFormat.format(
					"Missing parameter {0} in context {1}",
					PARAMETER_SAME_DOMAIN, contextParameters));
		}
		if (differentDomainsRules == null) {
			throw new ConfigurationError(MessageFormat.format(
					"Missing parameter {0} in context {1}",
					PARAMETER_DIFFERENT_DOMAINS, contextParameters));
		}
		return new AMMTypeBasedModuleDependencyValidator(
				diagnosticResultFactory, sameDomainRules, differentDomainsRules);
	}

}
