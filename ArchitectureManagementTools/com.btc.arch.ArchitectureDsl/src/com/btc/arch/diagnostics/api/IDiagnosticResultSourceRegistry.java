package com.btc.arch.diagnostics.api;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;

public interface IDiagnosticResultSourceRegistry {

	void registerDiagnosticResultSource(
			IDiagnosticResultSource<Collection<? extends EObject>> diagnosticResultSource);

	void registerDiagnosticResultSource(
			IDiagnosticResultSource<Collection<? extends EObject>> diagnosticResultSource,
			int priority);

	Iterable<IDiagnosticResultSource<Collection<? extends EObject>>> getDiagnosticResultSources();

}