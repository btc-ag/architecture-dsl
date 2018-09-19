package com.btc.arch.validation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.util.ArchDslFileUsageTestBase;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.arch.diagnostics.api.Dependency;
import com.btc.arch.validation.ModuleDependencyDiagnosticsUtils;
import com.btc.commons.emf.diagnostics.DummyDiagnosticsRegistry;
import com.btc.commons.emf.diagnostics.IDiagnosticDescriptor;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.emf.diagnostics.ecore.DiagnosticsProcessor;
import com.btc.commons.emf.diagnostics.ecore.EcoreDiagnosticSubjectDescriber;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IterationUtils;

public class ModuleDependencyValidationProcessorTest extends
		ArchDslFileUsageTestBase {
	private final class ModuleDependencyValidatorImplementation implements
			IDiagnosticResultSource<Collection<? extends EObject>> {
		private final DummyDiagnosticsRegistry registry;
		private final IDiagnosticResultFactory resultFactory;

		public ModuleDependencyValidatorImplementation(String name) {
			super();
			this.name = name;
			this.registry = new DummyDiagnosticsRegistry();
			this.resultFactory = this.registry
					.createDiagnosticResultFactory(EcoreDiagnosticSubjectDescriber
							.getDefault());
		}

		public ModuleDependencyValidatorImplementation() {
			this(null);
		}

		final private String name;

		@Override
		public Iterable<IDiagnosticResultBase> diagnose(
				final Collection<? extends EObject> baseElements,
				final Collection<? extends EObject> allElements) {
			final Iterator<Pair<Module, Module>> allDependenciesAsPairs = ModelQueries
					.getAllDependenciesAsPairs(baseElements).iterator();
			if (!allDependenciesAsPairs.hasNext()) {
				fail("Invalid test case");
			}
			final Pair<Module, Module> dependencyPair = allDependenciesAsPairs
					.next();
			final LinkedList<IDiagnosticResultBase> result = new LinkedList<IDiagnosticResultBase>();
			result.add(this.resultFactory.createDiagnosticResult(this.name,
					dependencyPair, "some explanation"));
			return result;
		}

		@Override
		public String getDescription() {
			return "Declares the first of the dependencies as an illegal one. For test purposes only.";
		}

		@Override
		public Iterable<IDiagnosticDescriptor> getDiagnosticDescriptors() {
			return Collections.emptyList();
		}
	}

	@Override
	protected String getTestDataFileName() {
		return "DomainTest.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "DomainTest";
	}

	private DiagnosticsProcessor createProcessorNoValidators() {
		IDiagnosticResultSource<Collection<? extends EObject>>[] validators = new IDiagnosticResultSource[] {};
		DiagnosticsProcessor processor = new DiagnosticsProcessor(validators);
		return processor;
	}

	@Test
	public void testGetAllDependenciesNoValidator() {
		DiagnosticsProcessor processor = createProcessorNoValidators();
		final EList<EObject> contents = resource.getContents();
		Collection<IDiagnosticResultBase> illegalDependencies = IterationUtils
				.materialize(processor.diagnose(contents, getAllContents()));
		Collection<Dependency> allDependencies = IterationUtils
				.materialize(ModuleDependencyDiagnosticsUtils
						.getAllDependencies(contents, illegalDependencies));
		assertEquals(4, allDependencies.size());
		// TODO check result details
	}

	@Test
	public void testGetIllegalDependenciesNoValidator() {
		DiagnosticsProcessor processor = createProcessorNoValidators();
		Collection<IDiagnosticResultBase> illegalDependencies = IterationUtils
				.materialize(processor.diagnose(resource.getContents(),
						getAllContents()));
		assertEquals(0, illegalDependencies.size());
	}

	private DiagnosticsProcessor createProcessorOneCustomValidator() {
		IDiagnosticResultSource<Collection<? extends EObject>>[] validators = new IDiagnosticResultSource[] { new ModuleDependencyValidatorImplementation() };
		DiagnosticsProcessor processor = new DiagnosticsProcessor(validators);
		return processor;
	}

	@Test
	public void testGetAllDependenciesOneCustomValidator() {
		final DiagnosticsProcessor processor = createProcessorOneCustomValidator();
		final EList<EObject> contents = resource.getContents();
		Collection<IDiagnosticResultBase> illegalDependencies = IterationUtils
				.materialize(processor.diagnose(contents, getAllContents()));
		Collection<Dependency> allDependencies = IterationUtils
				.materialize(ModuleDependencyDiagnosticsUtils
						.getAllDependencies(contents, illegalDependencies));
		assertEquals(4, allDependencies.size());
		// TODO check result details
	}

	@Test
	public void testGetIllegalDependenciesOneCustomValidator() {
		DiagnosticsProcessor processor = createProcessorOneCustomValidator();
		Collection<IDiagnosticResultBase> illegalDependencies = IterationUtils
				.materialize(processor.diagnose(resource.getContents(),
						getAllContents()));
		assertEquals(1, illegalDependencies.size());
		// TODO check result details
	}

	private DiagnosticsProcessor createProcessorTwoCustomValidators() {
		IDiagnosticResultSource<Collection<? extends EObject>>[] validators = new IDiagnosticResultSource[] {
				new ModuleDependencyValidatorImplementation("A"),
				new ModuleDependencyValidatorImplementation("B") };
		DiagnosticsProcessor processor = new DiagnosticsProcessor(validators);
		return processor;
	}

	@Test
	public void testGetAllDependenciesTwoCustomValidators() {
		DiagnosticsProcessor processor = createProcessorTwoCustomValidators();
		final EList<EObject> contents = resource.getContents();
		Collection<IDiagnosticResultBase> illegalDependencies = IterationUtils
				.materialize(processor.diagnose(contents, getAllContents()));
		Collection<Dependency> allDependencies = IterationUtils
				.materialize(ModuleDependencyDiagnosticsUtils
						.getAllDependencies(contents, illegalDependencies));
		assertEquals(4, allDependencies.size());
		// TODO check result details
	}

	@Test
	public void testGetIllegalDependenciesTwoCustomValidators() {
		DiagnosticsProcessor processor = createProcessorTwoCustomValidators();
		final EList<EObject> contents = resource.getContents();
		Collection<IDiagnosticResultBase> illegalDependencies = IterationUtils
				.materialize(processor.diagnose(contents, getAllContents()));
		assertEquals(2, illegalDependencies.size());
		// TODO check result details
	}

}
