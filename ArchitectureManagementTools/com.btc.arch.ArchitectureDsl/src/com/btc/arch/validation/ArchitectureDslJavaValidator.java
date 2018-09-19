package com.btc.arch.validation;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;

import com.btc.arch.architectureDsl.ArchitectureDslPackage;
import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.arch.architectureDsl.util.ModuleGroupQueries;
import com.btc.arch.architectureDsl.util.ModuleQueries;
import com.btc.commons.eclipse.ecore.ResourceUtil;
import com.btc.commons.emf.diagnostics.DiagnosticsBundle;
import com.btc.commons.emf.diagnostics.DiagnosticsUtils;
import com.btc.commons.emf.diagnostics.DummyDiagnosticsRegistry;
import com.btc.commons.emf.diagnostics.IDiagnosticResult;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;
import com.btc.commons.emf.diagnostics.ecore.DiagnosticsProcessor;
import com.btc.commons.emf.diagnostics.ecore.EcoreDiagnosticSubjectDescriber;
import com.btc.commons.java.Pair;

final public class ArchitectureDslJavaValidator extends
		AbstractArchitectureDslJavaValidator {

	private final DiagnosticsProcessor processor;

	public ArchitectureDslJavaValidator() {
		final DiagnosticsBundle instance = DiagnosticsBundle.getInstance();
		final IDiagnosticsRegistry diagnosticsRegistry;
		if (instance != null) {
			diagnosticsRegistry = instance.getDiagnosticsRegistry();
		} else {
			// TODO warn... this is just for standalone tests to be runnable
			diagnosticsRegistry = new DummyDiagnosticsRegistry();
		}
		final IDiagnosticResultFactory diagnosticResultFactory = diagnosticsRegistry
				.createDiagnosticResultFactory(EcoreDiagnosticSubjectDescriber
						.getDefault());
		// TODO FIXME this must be changed to use the registered extensions etc.
		this.processor = new DiagnosticsProcessor(
				new IDiagnosticResultSource[] {

				});
	}

	@Check
	public void checkModuleGroup(final Module module) {
		if (ModuleQueries.getEffectiveModuleGroup(module) == null) {
			warning("Module does not have a module group.",
					ArchitectureDslPackage.MODULE__MODULE_GROUP);
		}
	}

	@Check
	public void checkDomain(final Module module) {
		if (ModuleQueries.getEffectiveDomain(module) == null) {
			error("Module does not have a domain.",
					ArchitectureDslPackage.MODULE);
		}
	}

	// TODO: Check that a domain has only one super domain.

	/*
	 * Rule R1.1
	 */
	@Check
	public void checkValidSpecifiedDomain(final ModuleGroup moduleGroup) {
		final Collection<Domain> invalidDomains = ModuleGroupQueries
				.getInvalidSpecifiedDomains(moduleGroup);
		if (invalidDomains.size() > 0) {
			String invalidDomainString = "";
			for (final Domain domain : invalidDomains) {
				invalidDomainString += domain.getName() + ", ";
			}
			error("Rule R1.1: The specified domain of a module group has to be a (transitive) subdomain of the effective domain of its super module group or this domain itself. The following domains infringe this rule: "
					+ invalidDomainString.substring(0,
							invalidDomainString.length() - 2),
					ArchitectureDslPackage.MODULE_GROUP__DOMAIN);
		}
	}

	/*
	 * Rule 1.7
	 */
	@Check
	public void checkOnlyLeafDomain(final Module module) {
		if (!ModuleQueries.isEffectiveDomainLeafDomain(module)) {
			error("Rule 1.7: The effective domain of a module should be a leaf of the domain tree. The following effective domain infringes this rule: "
					+ ModuleQueries.getEffectiveDomain(module),
					ArchitectureDslPackage.MODULE);
		}
	}

	// The check is not necessary anymore since this is no longer possible.
	// @Check
	// public void checkMultipleDomains(final Module module) {
	// if (ModuleQueries.getEffectiveDomains(module).size() > 1) {
	// warning("Module has multiple domains: ",
	// ArchitectureDslPackage.MODULE);
	// }
	// }

	@Check(CheckType.NORMAL)
	public void checkModuleDependencies(final Module module) {
		final Iterable<IDiagnosticResultBase> diagnostics = this.processor
				.diagnose(
						Collections.singletonList(module),
						ResourceUtil.extractContents(module.eResource()
								.getResourceSet().getResources()));
		final Iterable<IDiagnosticResult<Pair<Module, Module>>> illegalDependencies = ModuleDependencyDiagnosticsUtils
				.filterModuleDependencyResults(diagnostics);
		for (final IDiagnosticResult<Pair<Module, Module>> dependency : illegalDependencies) {
			warning(dependency.toString(), ArchitectureDslPackage.MODULE);
		}
		final Iterable<IDiagnosticResult<Module>> moduleDiagnostics = DiagnosticsUtils
				.filterByType(diagnostics, "Module", Module.class);
		for (final IDiagnosticResult<Module> dependency : moduleDiagnostics) {
			warning(dependency.toString(), ArchitectureDslPackage.MODULE);
		}
	}

	@Check
	public void checkModuleName(final Module module) {
		if (!module.getName().contains(".")) {
			warning("Module name should contain at least one '.'",
					ArchitectureDslPackage.MODULE__NAME);
		}
	}
}