package com.btc.arch.diagnostics.test;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.diagnostics.DomainModelModuleDependencyValidator;
import com.btc.arch.architectureDsl.util.ArchDslFileUsageTestBase;
import com.btc.commons.emf.diagnostics.DummyDiagnosticsRegistry;
import com.btc.commons.emf.diagnostics.IDiagnosticResult;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.emf.diagnostics.ecore.EcoreDiagnosticSubjectDescriber;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IterationUtils;

public class DomainModelModuleDependencyValidatorDomainTest extends
		ArchDslFileUsageTestBase {
	@Override
	protected String getTestDataFileName() {
		return "DomainTest.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "DomainTest";
	}

	@Test
	public void testGetAllInvalidDependencies() {
		final IDiagnosticResultSource<Collection<? extends EObject>> domainModelModuleDependencyValidator = new DomainModelModuleDependencyValidator(
				new DummyDiagnosticsRegistry()
						.createDiagnosticResultFactory(EcoreDiagnosticSubjectDescriber
								.getDefault()));
		final List<IDiagnosticResultBase> illegalDependencies = IterationUtils
				.materialize(domainModelModuleDependencyValidator.diagnose(
						this.resource.getContents(), getAllContents()),
						new ArrayList<IDiagnosticResultBase>());
		final String[] formattedDependencyModules = formatDependencyModulesSorted(illegalDependencies);
		assertArrayEquals(new String[] { "A.B.E.Bad,A.B.D.A",
				"NoDomain,A.B.D.A" }, formattedDependencyModules);
	}

	private String[] formatDependencyModulesSorted(
			final List<IDiagnosticResultBase> illegalDependencies) {
		final String[] formattedDependencyModules = formatDependencyModules(illegalDependencies);
		Arrays.sort(formattedDependencyModules);
		return formattedDependencyModules;
	}

	@SuppressWarnings("unchecked")
	private String[] formatDependencyModules(
			final List<IDiagnosticResultBase> illegalDependencies) {
		final String[] result = new String[illegalDependencies.size()];
		for (int i = 0; i < result.length; i++) {
			final IDiagnosticResult<Pair<Module, Module>> dependency;
			dependency = (IDiagnosticResult<Pair<Module, Module>>) illegalDependencies
					.get(i);
			result[i] = dependency.getSubject().getFirst().getName() + ","
					+ dependency.getSubject().getSecond().getName();
		}
		return result;
	}
}
