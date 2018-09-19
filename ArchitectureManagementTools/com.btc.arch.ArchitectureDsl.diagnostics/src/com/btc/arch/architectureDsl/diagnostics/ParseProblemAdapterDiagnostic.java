package com.btc.arch.architectureDsl.diagnostics;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.ArchitectureDslFactory;
import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.arch.base.dependency.IDependencySource;
import com.btc.arch.base.dependency.IParseProblem;
import com.btc.arch.base.dependency.IParseProblem.ILocation;
import com.btc.commons.emf.diagnostics.IDiagnosticDescriptor;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultFactory;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IterationUtils;

public class ParseProblemAdapterDiagnostic implements
		IDiagnosticResultSource<Collection<? extends EObject>> {

	public final String DIAGNOSTIC_ID = "com.btc.arch.ArchitectureDsl.report.dependencyParseProblem";

	private class ParseProblemAdapter implements
			IMapFunctor<IParseProblem, IDiagnosticResultBase> {

		private final Collection<Model> models;

		public ParseProblemAdapter(Collection<Model> models) {
			this.models = models;
		}

		@Override
		public IDiagnosticResultBase mapItem(IParseProblem parseProblem) {
			final ILocation locationDescription = parseProblem
					.getLocationDescription();
			final EObject subject = getSubject(locationDescription);

			final String positionText = locationDescription.hasPosition() ? MessageFormat
					.format("({0})", locationDescription.formatPosition()) : "";
			final String locationText = locationDescription.getURI() != null ? MessageFormat
					.format(" at {0}{1}",
							formatURI(locationDescription.getURI()),
							positionText) : "";
			return diagnosticResultFactory.createDiagnosticResult(
					DIAGNOSTIC_ID, subject, MessageFormat.format(
							"Dependency parser problem{0}:\n{1}{2}",
							locationText,
							parseProblem.getExplanation(),
							parseProblem.getCause() != null ? MessageFormat
									.format(" ({0})", parseProblem.getCause())
									: ""));
		}

		private String formatURI(final URI uri) {
			// TODO The URI is only valid on the server, it should be mapped to
			// an URI usable by a client!
			// Options are:
			// * Link into workspace (but this may not be the current revision)
			// * Link into SVN including the repository revision, such as
			// http://esvn.e-konzern.de/repo/btc-project-epm/!svn/ver/116520/a/b/c
			// * Do not link, but show only path to project root

			return uri.toString();
		}

		private EObject getSubject(ILocation location) {
			final Model firstModel = models.iterator().next();
			EObject subject = firstModel;
			if (location != null) {
				final String moduleName = location.getModuleName();
				if (moduleName != null) {
					subject = ModelQueries.getModuleByName(models, moduleName);
					if (subject == null) {
						final Module module = ArchitectureDslFactory.eINSTANCE
								.createModule();
						module.setName(moduleName);
						firstModel.getModules().add(module);
						subject = module;
					}
				}
			}
			return subject;
		}
	}

	private final Iterable<IDependencySource> dependencySources;
	private final IDiagnosticResultFactory diagnosticResultFactory;
	private final IDiagnosticsRegistry diagnosticsRegistry;

	public ParseProblemAdapterDiagnostic(
			Iterable<IDependencySource> dependencySources,
			IDiagnosticsRegistry diagnosticsRegistry,
			IDiagnosticResultFactory diagnosticResultFactory) {
		this.dependencySources = dependencySources;
		this.diagnosticsRegistry = diagnosticsRegistry;
		this.diagnosticResultFactory = diagnosticResultFactory;
	}

	@Override
	public Iterable<IDiagnosticResultBase> diagnose(
			final Collection<? extends EObject> baseElements,
			final Collection<? extends EObject> allElements) {

		return IterationUtils.map(IterationUtils.mapToIterablesAndChain(
				dependencySources,
				new IMapFunctor<IDependencySource, Iterable<IParseProblem>>() {

					@Override
					public Iterable<IParseProblem> mapItem(
							IDependencySource dependencySource) {
						return dependencySource.getProblems();
					}
				}),
				new ParseProblemAdapter(ModelQueries.findModels(allElements)));
	}

	@Override
	public Iterable<IDiagnosticDescriptor> getDiagnosticDescriptors() {
		return Arrays.asList(diagnosticsRegistry
				.getDiagnosticDescriptor(DIAGNOSTIC_ID));
	}

	@Override
	public String getDescription() {
		return "Diagnoses problems during dependency parsing";
	}

}
