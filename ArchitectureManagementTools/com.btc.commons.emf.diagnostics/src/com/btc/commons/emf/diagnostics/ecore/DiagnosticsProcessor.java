package com.btc.commons.emf.diagnostics.ecore;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import com.btc.commons.emf.diagnostics.IDiagnosticDescriptor;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.java.StringUtils;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IterableChain;
import com.btc.commons.java.functional.IterationUtils;

public class DiagnosticsProcessor implements
		IDiagnosticResultSource<Collection<? extends EObject>> {
	private final Collection<IDiagnosticResultSource<Collection<? extends EObject>>> diagnosticsResultSources;

	@SuppressWarnings("unchecked")
	public DiagnosticsProcessor() {
		this(new IDiagnosticResultSource[] {});
	}

	public DiagnosticsProcessor(
			final IDiagnosticResultSource<Collection<? extends EObject>>[] validators) {
		this.diagnosticsResultSources = new ArrayList<IDiagnosticResultSource<Collection<? extends EObject>>>(
				Arrays.asList(validators));
	}

	public void addDiagnosticsResultSource(
			final IDiagnosticResultSource<Collection<? extends EObject>> diagnosticsResultSource) {
		this.diagnosticsResultSources.add(diagnosticsResultSource);
	}

	@Override
	public Iterable<IDiagnosticResultBase> diagnose(
			final Collection<? extends EObject> baseElements,
			final Collection<? extends EObject> allElements) {
		return IterationUtils
				.mapToIterablesAndChain(
						this.diagnosticsResultSources,
						new IMapFunctor<IDiagnosticResultSource<Collection<? extends EObject>>, Iterable<IDiagnosticResultBase>>() {
							@Override
							public Iterable<IDiagnosticResultBase> mapItem(
									final IDiagnosticResultSource<Collection<? extends EObject>> validator) {
								return validator.diagnose(baseElements,
										allElements);
							}

						});
	}

	@Override
	public Iterable<IDiagnosticDescriptor> getDiagnosticDescriptors() {
		final IterableChain<IDiagnosticDescriptor> iterableChain = new IterableChain<IDiagnosticDescriptor>();
		for (final IDiagnosticResultSource<Collection<? extends EObject>> src : this.diagnosticsResultSources) {
			iterableChain.addIterable(src.getDiagnosticDescriptors());
		}
		return iterableChain;
	}

	@Override
	public String getDescription() {
		return MessageFormat.format("{0} containing\n{1}",
				getClass().getName(), StringUtils.join(IterationUtils.map(
						this.diagnosticsResultSources,
						new IMapFunctor<IDiagnosticResultSource<?>, String>() {

							@Override
							public String mapItem(
									final IDiagnosticResultSource<?> obj) {
								return MessageFormat.format("class {0}: {1}",
										obj.getClass().getName(),
										obj.getDescription());
							}
						}), "\n"));
	}
}
