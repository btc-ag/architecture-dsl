package com.btc.arch.architectureDsl.diagnostics.service;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.ArchitectureDslFactory;
import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.arch.base.ConfigurationError;
import com.btc.arch.base.IContext;
import com.btc.arch.diagnostics.api.DiagnosticsException;
import com.btc.arch.diagnostics.api.IDiagnosticResultSourceRegistry;
import com.btc.arch.diagnostics.api.service.IProcessingElementService;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;

public class FixCSharpProcessingElementService implements
		IProcessingElementService {
	// TODO this could be generalized...

	private static final String SYSTEM_MODULE_NAME = "System";
	private static final String CONFIG_KEY_SYSTEM_DOMAIN = "SystemDomain";
	private final Logger logger = Logger.getLogger(getClass());

	@Override
	public void doRegistrations(
			final Collection<? extends EObject> primaryContents,
			final Collection<? extends EObject> allContents,
			final IContext contextParameters,
			final IDiagnosticsRegistry diagnosticsRegistry,
			final IDiagnosticResultSourceRegistry diagnosticResultSourceRegistry)
			throws DiagnosticsException, ConfigurationError {
		final ModuleGroup systemModuleGroup = ArchitectureDslFactory.eINSTANCE
				.createModuleGroup();
		systemModuleGroup.setName(SYSTEM_MODULE_NAME);
		final String systemDomain = contextParameters
				.getParameter(CONFIG_KEY_SYSTEM_DOMAIN);
		systemModuleGroup.setDomain(ModelQueries.getDomainByName(allContents,
				systemDomain));
		getFirstModel(primaryContents).getModuleGroups().add(systemModuleGroup);
		this.logger.info(MessageFormat.format(
				"Added module {0} with domain {1} to model",
				SYSTEM_MODULE_NAME, systemDomain));
	}

	private static Model getFirstModel(
			final Collection<? extends EObject> primaryContents) {
		final List<Model> models;
		models = ModelQueries.findModels(primaryContents);
		final Model model = models.get(0);
		return model;
	}

}
