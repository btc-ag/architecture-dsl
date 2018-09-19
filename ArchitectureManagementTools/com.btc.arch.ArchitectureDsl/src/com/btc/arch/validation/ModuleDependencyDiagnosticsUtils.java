package com.btc.arch.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.util.ArchitectureDslNameMapper;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.arch.diagnostics.api.Dependency;
import com.btc.commons.emf.diagnostics.DiagnosticsUtils;
import com.btc.commons.emf.diagnostics.IDiagnosticResult;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IterationUtils;

public class ModuleDependencyDiagnosticsUtils {

	public static Iterable<Dependency> getAllDependencies(
			final Collection<? extends EObject> contents,
			final Iterable<IDiagnosticResultBase> allDiagnostics) {
		final Collection<IDiagnosticResult<Pair<Module, Module>>> illegalDependencies = IterationUtils
				.materialize(ModuleDependencyDiagnosticsUtils
						.filterModuleDependencyResults(allDiagnostics));
		final Iterable<Pair<Module, Module>> remainingDependencyPairs = ModuleDependencyDiagnosticsUtils
				.calcRemainingDependencyPairs(contents, illegalDependencies);
		final Collection<Dependency> allDependencies = new ArrayList<Dependency>();
		for (Pair<Module, Module> dependencyPair : remainingDependencyPairs) {
			allDependencies.add(new Dependency(dependencyPair.getFirst(),
					dependencyPair.getSecond(), true));
		}

		final Set<Pair<Module, Module>> dependenciesWithViolations = DiagnosticsUtils
				.getUniqueSubjects(illegalDependencies);
		for (Pair<Module, Module> dependencyPair : dependenciesWithViolations) {
			allDependencies.add(new Dependency(dependencyPair.getFirst(),
					dependencyPair.getSecond(), false));
		}
		return allDependencies;
	}

	private static Set<Pair<Module, Module>> calcRemainingDependencyPairs(
			final Collection<? extends EObject> contents,
			final Iterable<IDiagnosticResult<Pair<Module, Module>>> dependencyDiagnosticResults) {
		// TODO this could be done by filtering all dependencies
		final Set<Pair<Module, Module>> remainingDependencyPairs = IterationUtils
				.materialize(ModelQueries.getAllDependenciesAsPairs(contents),
						new HashSet<Pair<Module, Module>>());
		for (IDiagnosticResult<Pair<Module, Module>> dependencyDiagnosticResult : dependencyDiagnosticResults) {
			remainingDependencyPairs.remove(new Pair<Module, Module>(
					dependencyDiagnosticResult.getSubject().getFirst(),
					dependencyDiagnosticResult.getSubject().getSecond()));
		}
		return remainingDependencyPairs;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Iterable<IDiagnosticResult<Pair<Module, Module>>> filterModuleDependencyResults(
			final Iterable<IDiagnosticResultBase> diagnostics) {
		return (Iterable) DiagnosticsUtils.filterByType(diagnostics,
				"Module,Module");
	}

	/**
	 * TODO: This method should be moved somewhere else since it is not
	 * module-specific
	 * 
	 * @param input
	 * @return
	 */
	public static Iterable<Pair<String, String>> mapToString(
			final Iterable<IDiagnosticResultBase> input) {
		return IterationUtils.map(input,
				new IMapFunctor<IDiagnosticResultBase, Pair<String, String>>() {

					@Override
					public Pair<String, String> mapItem(
							IDiagnosticResultBase obj) {
						return new Pair<String, String>(
								ArchitectureDslNameMapper.getName(obj
										.getSubject()), obj.getExplanation());
					}
				});
	}

}
