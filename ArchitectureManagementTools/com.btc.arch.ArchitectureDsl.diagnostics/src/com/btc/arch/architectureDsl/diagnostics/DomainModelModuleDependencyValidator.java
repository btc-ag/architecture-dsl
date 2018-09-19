package com.btc.arch.architectureDsl.diagnostics;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleType;
import com.btc.arch.architectureDsl.util.ModuleQueries;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.java.CollectionUtils;

public class DomainModelModuleDependencyValidator extends
		DefaultModuleDependencyValidatorBase {

	public DomainModelModuleDependencyValidator(
			IDiagnosticResultFactory resultFactory) {
		super(resultFactory);
	}

	private static final ModuleType[] TEST_MODULE_TYPES = new ModuleType[] {
			ModuleType.FRAMEWORK_TEST, ModuleType.IMPLEMENTATION_TEST,
			ModuleType.INTERFACE_TEST };
	private static final Logger logger = Logger
			.getLogger(DomainModelModuleDependencyValidator.class);

	@Override
	protected boolean isSingleDependencyLegal(final Module source,
			final Module target) {
		assert source != null;
		assert target != null;
		if (!ignoreDependency(source, target))
			return ModuleQueries.isDependencyAllowedByDomainModel(source,
					target);
		else
			return true;
	}

	private boolean ignoreDependency(Module source, Module target) {
		// TODO this could be lifted to DefaultModuleDependencyValidatorBase
		if (ignoreSource(source) || ignoreTarget(target)) {
			return true;
		} else
			return false;
	}

	private boolean ignoreTarget(Module target) {
		// TODO Does it make sense to make targets ignorable?
		return false;
	}

	private boolean ignoreSource(Module source) {
		// TODO ignore by type (preferred) or name
		// TODO make this configurable
		return CollectionUtils.containsAny(source.getType(),
				Arrays.asList(TEST_MODULE_TYPES));
	}

	@Override
	protected String getViolationExplanation(final Module source,
			final Module target) {
		final Domain effectiveTargetDomain = ModuleQueries
				.getEffectiveDomain(target);
		return effectiveTargetDomain == null ? MessageFormat
				.format("Actual target domain is unknown/unspecified, allowed target domains are {0}",
						getSortedDomainNames(ModuleQueries
								.getEffectiveAllowedTargetDomains(source)))
				: MessageFormat
						.format("No actual target domain is allowed (actual={0}, allowed={1})",
								effectiveTargetDomain.getName(),
								getSortedDomainNames(ModuleQueries
										.getEffectiveAllowedTargetDomains(source)));
	}

	@Override
	public String getDescription() {
		return "Checks model-specified module dependencies for conformance with the domain model, i.e. whether the domains of the source and target modules allow references of their elements";
	}

	private static SortedSet<String> getSortedDomainNames(
			final Iterable<Domain> domains) {
		final SortedSet<String> sortedDomainNames = new TreeSet<String>();
		for (Domain domain : domains) {
			if (!domain.eIsProxy()) {
				sortedDomainNames.add(domain.getName());
			} else {
				// TODO bekommt man den Namen der Domäne heraus?
				logger.warn(MessageFormat.format(
						"Ignoring unresolved domain {0}", domain));
			}
		}
		return sortedDomainNames;
	}

	@Override
	public String getDiagnosticsId() {
		return "com.btc.arch.ArchitectureDsl.domain";
	}

}
