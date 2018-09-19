package com.btc.arch.architectureDsl.diagnostics.service;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.btc.arch.architectureDsl.ArchitectureDslPackage;
import com.btc.arch.architectureDsl.Domain;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.arch.base.ConfigurationError;
import com.btc.arch.base.IContext;
import com.btc.arch.diagnostics.api.DiagnosticsException;
import com.btc.arch.diagnostics.api.IDiagnosticResultSourceRegistry;
import com.btc.arch.diagnostics.api.service.IProcessingElementService;
import com.btc.commons.emf.EMFQueryUtils;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;
import com.btc.commons.java.CollectionUtils;
import com.btc.commons.java.Pair;
import com.btc.commons.java.StringUtils;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IUnaryPredicate;
import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.ToStringMapFunctor;

public class UniqueNamedModelElementsProcessingElementService implements
		IProcessingElementService {

	// TODO split into service and regular class

	@Override
	public void doRegistrations(
			final Collection<? extends EObject> primaryContents,
			final Collection<? extends EObject> allContents,
			final IContext contextParameters,
			final IDiagnosticsRegistry diagnosticsRegistry,
			final IDiagnosticResultSourceRegistry diagnosticResultSourceRegistry)
			throws DiagnosticsException, ConfigurationError {
		checkUnique(allContents);

	}

	private void checkUnique(final Collection<? extends EObject> allContents)
			throws DiagnosticsException {
		final Collection<EObject> duplicates = IterationUtils
				.materialize(getDuplicates(allContents));
		if (!duplicates.isEmpty()) {
			throw new DiagnosticsException(MessageFormat.format(
					"{0} duplicate model elements found:\n   {1}", duplicates
							.size(), StringUtils.join(IterationUtils.map(
							duplicates, new IMapFunctor<EObject, String>() {

								@Override
								public String mapItem(EObject obj) {
									return MessageFormat.format(
											"{0} from resource {1}", obj
													.toString(), obj
													.eResource() != null ? obj
													.eResource().getURI()
													: "<null>");
								}
							}), "\n   ")));
		}
	}

	private Iterable<EObject> getDuplicates(
			final Collection<? extends EObject> allContents) {
		final Iterable<Module> modules = (Iterable<Module>) EMFQueryUtils
				.getObjectsByType(allContents,
						ArchitectureDslPackage.Literals.MODULE);
		final Iterable<ModuleGroup> moduleGroups = (Iterable<ModuleGroup>) EMFQueryUtils
				.getObjectsByType(allContents,
						ArchitectureDslPackage.Literals.MODULE_GROUP);
		final Iterable<Domain> domains = (Iterable<Domain>) EMFQueryUtils
				.getObjectsByType(allContents,
						ArchitectureDslPackage.Literals.DOMAIN);
		return IterationUtils.chain(
				getDuplicates(modules, new IMapFunctor<Module, String>() {

					@Override
					public String mapItem(final Module obj) {
						return obj.getName();
					}
				}),
				getDuplicates(moduleGroups,
						new IMapFunctor<ModuleGroup, String>() {

							@Override
							public String mapItem(final ModuleGroup obj) {
								return obj.getName();
							}
						}),
				getDuplicates(domains, new IMapFunctor<Domain, String>() {

					@Override
					public String mapItem(final Domain obj) {
						return obj.getName();
					}
				}));
	}

	@SuppressWarnings("unchecked")
	private static <T extends EObject> Iterable<EObject> getDuplicates(
			final Iterable<T> objects,
			final IMapFunctor<T, String> keyMapFunctor) {
		return (Iterable<EObject>) IterationUtils.mapToIterablesAndChain(
				IterationUtils.filter(
						CollectionUtils.createSetValuedMapFromIndividuals(
								IterationUtils.map(objects,
										new IMapFunctor<T, Pair<String, T>>() {

											@Override
											public Pair<String, T> mapItem(
													final T obj) {
												return new Pair<String, T>(
														keyMapFunctor
																.mapItem(obj),
														obj);
											}
										})).entrySet(),
						new IUnaryPredicate<Map.Entry<String, Set<T>>>() {

							@Override
							public boolean evaluate(
									final Map.Entry<String, Set<T>> obj) {
								return obj.getValue().size() > 1;
							}

						}),
				new IMapFunctor<Map.Entry<String, Set<T>>, Iterable<T>>() {

					@Override
					public Iterable<T> mapItem(final Entry<String, Set<T>> obj) {
						return obj.getValue();
					}
				});
	}
}
