package com.btc.arch.architectureDsl.diagnostics;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.util.Triple;

import com.btc.arch.xtext.util.XtextResourceUtil;
import com.btc.commons.emf.diagnostics.IDiagnosticDescriptor;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;
import com.btc.commons.emf.diagnostics.ecore.EcoreDiagnosticSubjectDescriber;
import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.IMapFunctor;

public class UnresolvedReferenceDiagnostic implements
		IDiagnosticResultSource<Collection<? extends EObject>> {
	private final IDiagnosticsRegistry registry;
	private final IDiagnosticResultFactory diagnosticResultFactory;
	private static final String DIAGNOSTIC_ID = "com.btc.arch.ArchitectureDsl.diagnostics.unresolvedReference";

	public UnresolvedReferenceDiagnostic(IDiagnosticsRegistry registry) {
		this.registry = registry;
		this.diagnosticResultFactory = registry
				.createDiagnosticResultFactory(EcoreDiagnosticSubjectDescriber
						.getDefault());
	}

	@Override
	public Iterable<IDiagnosticResultBase> diagnose(
			final Collection<? extends EObject> baseElements,
			final Collection<? extends EObject> allElements) {
		final Iterable<Triple<EObject, EStructuralFeature, String>> unresolvedReferences = XtextResourceUtil
				.checkForUnresolvedReferences(allElements);
		return IterationUtils
				.map(unresolvedReferences,
						new IMapFunctor<Triple<EObject, EStructuralFeature, String>, IDiagnosticResultBase>() {

							@Override
							public IDiagnosticResultBase mapItem(
									Triple<EObject, EStructuralFeature, String> triple) {
								return diagnosticResultFactory
										.createDiagnosticResult(
												DIAGNOSTIC_ID,
												triple.getFirst(),
												MessageFormat
														.format("Unresolved reference: {0} -> {1}",
																triple.getSecond()
																		.getName(),
																triple.getThird()));
							}
						});

	}

	@Override
	public Iterable<IDiagnosticDescriptor> getDiagnosticDescriptors() {
		return Collections.singletonList(registry
				.getDiagnosticDescriptor(DIAGNOSTIC_ID));
	}

	@Override
	public String getDescription() {
		return "Analyzes an Architecture DSL model for unresolvable references";
	}

}
