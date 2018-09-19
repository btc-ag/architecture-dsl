package com.btc.arch.architectureDsl.diagnostics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.commons.emf.diagnostics.IDiagnosticDescriptor;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.java.Pair;

/**
 * The {@link DefaultModuleDependencyValidatorBase} base implementation of
 * {@link IDiagnosticResultSource} implements getAllIllegalDependencies by
 * iterating over all dependencies in the model and checking them individually.
 * This can be safely used as the basis for any validation which per se requires
 * iterating over all dependencies, but should not be used as a base class for
 * validations which could determine the set of all violations more efficiently.
 * 
 * This class applies the Template Method design pattern. It may be subclassed
 * by clients, but subclasses may not call any other method of the base class.
 * 
 * @author SIGIESEC
 * 
 */
public abstract class DefaultModuleDependencyValidatorBase implements
		IDiagnosticResultSource<Collection<? extends EObject>> {

	// TODO actually, the templated methods could be moved to a separate
	// interface, which would increase testability and understandability

	private final IDiagnosticResultFactory resultFactory;

	public DefaultModuleDependencyValidatorBase(
			IDiagnosticResultFactory resultFactory) {
		super();
		this.resultFactory = resultFactory;
	}

	/**
	 * This is a Templated Method.
	 * 
	 * May only be called if checkSingleDependency(source, target) returns
	 * false.
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	protected abstract String getViolationExplanation(Module source,
			Module target);

	/**
	 * This is a Templated Method.
	 * 
	 * @param source
	 * @param target
	 * @return true if dependency is legal according to the implemented rule,
	 *         false otherwise
	 */
	protected abstract boolean isSingleDependencyLegal(Module source,
			Module target);

	@Override
	public Iterable<IDiagnosticResultBase> diagnose(
			final Collection<? extends EObject> baseElements,
			final Collection<? extends EObject> allElements) {
		// return ModelQueries.getAllDependenciesIllegalByDomainModel(contents);
		final Collection<IDiagnosticResultBase> results = new ArrayList<IDiagnosticResultBase>();
		for (Pair<Module, Module> dependencyPair : ModelQueries
				.getAllDependenciesAsPairs(baseElements)) {
			final Module source = dependencyPair.getFirst();
			final Module target = dependencyPair.getSecond();
			if (!isSingleDependencyLegal(source, target)) {
				results.add(resultFactory.createDiagnosticResult(
						getDiagnosticsId(), dependencyPair,
						getViolationExplanation(source, target)));
			}
		}
		return results;
	}

	@Override
	public Iterable<IDiagnosticDescriptor> getDiagnosticDescriptors() {
		return Collections.emptyList();
	}

	abstract public String getDiagnosticsId();
}
