package com.btc.arch.architectureDsl.diagnostics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.btc.arch.diagnostics.api.IDiagnosticResultSourceRegistry;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IterationUtils;

class DiagnosticResultSourceRegistry implements IDiagnosticResultSourceRegistry {
	private final List<Pair<Integer, IDiagnosticResultSource<Collection<? extends EObject>>>> diagnosticResultSources = new ArrayList<Pair<Integer, IDiagnosticResultSource<Collection<? extends EObject>>>>();
	private boolean diagnosticResultSourcesLocked = false;

	/* (non-Javadoc)
	 * @see com.btc.arch.architectureDsl.diagnostics.IDiagnosticResultSourceRegistry#registerDiagnosticResultSource(com.btc.commons.emf.diagnostics.IDiagnosticResultSource)
	 */
	@Override
	public void registerDiagnosticResultSource(
			IDiagnosticResultSource<Collection<? extends EObject>> diagnosticResultSource) {
		registerDiagnosticResultSource(diagnosticResultSource, 0);
	}

	/* (non-Javadoc)
	 * @see com.btc.arch.architectureDsl.diagnostics.IDiagnosticResultSourceRegistry#registerDiagnosticResultSource(com.btc.commons.emf.diagnostics.IDiagnosticResultSource, int)
	 */
	@Override
	public void registerDiagnosticResultSource(
			IDiagnosticResultSource<Collection<? extends EObject>> diagnosticResultSource,
			int priority) {
		if (!this.diagnosticResultSourcesLocked) {
			this.diagnosticResultSources
					.add(new Pair<Integer, IDiagnosticResultSource<Collection<? extends EObject>>>(
							priority, diagnosticResultSource));
		} else {
			// TODO implement unlocking after processing for interactive use
			throw new IllegalStateException(
					"Cannot add diagnostic result source after processing started");
		}
	}

	/* (non-Javadoc)
	 * @see com.btc.arch.architectureDsl.diagnostics.IDiagnosticResultSourceRegistry#getDiagnosticResultSources()
	 */
	@Override
	public Iterable<IDiagnosticResultSource<Collection<? extends EObject>>> getDiagnosticResultSources() {
		this.diagnosticResultSourcesLocked = true;
		Collections
				.sort(diagnosticResultSources,
						new Comparator<Pair<Integer, IDiagnosticResultSource<Collection<? extends EObject>>>>() {

							@Override
							public int compare(
									Pair<Integer, IDiagnosticResultSource<Collection<? extends EObject>>> o1,
									Pair<Integer, IDiagnosticResultSource<Collection<? extends EObject>>> o2) {
								return o1.getFirst().compareTo(o2.getFirst());
							}
						});
		return IterationUtils
				.map(diagnosticResultSources,
						new IMapFunctor<Pair<Integer, IDiagnosticResultSource<Collection<? extends EObject>>>, IDiagnosticResultSource<Collection<? extends EObject>>>() {

							@Override
							public IDiagnosticResultSource<Collection<? extends EObject>> mapItem(
									Pair<Integer, IDiagnosticResultSource<Collection<? extends EObject>>> obj) {
								return obj.getSecond();
							}
						});
	}

}