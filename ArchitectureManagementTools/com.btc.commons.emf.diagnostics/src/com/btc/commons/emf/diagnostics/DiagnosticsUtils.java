package com.btc.commons.emf.diagnostics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IUnaryPredicate;

public class DiagnosticsUtils {

	// public static <T> Iterable<IDiagnosticResultBase> diagnoseAll(
	// final IDiagnosticResultSource<T> diagnosticResultSource,
	// final Set<T> resources) {
	// return IterationUtils.mapToIterablesAndChain(resources,
	// new IMapFunctor<T, Iterable<IDiagnosticResultBase>>() {
	//
	// @Override
	// public Iterable<IDiagnosticResultBase> mapItem(T obj) {
	// return diagnosticResultSource.diagnose(obj, baseElements);
	// }
	// });
	// }

	public static <T> Set<T> getUniqueSubjects(
			final Collection<IDiagnosticResult<T>> results) {
		// TODO move to DiagnosticResultUtils
		return IterationUtils.materialize(IterationUtils.map(results,
				new IMapFunctor<IDiagnosticResult<T>, T>() {

					@Override
					public T mapItem(IDiagnosticResult<T> obj) {
						return obj.getSubject();
					}

				}), new HashSet<T>());
	}

	public static <T, T2 extends IDiagnosticResultBase> Iterable<IDiagnosticResult<T>> filterByType(
			final Iterable<T2> diagnosticResults, final String typeString,
			@SuppressWarnings("unused") final Class<T> resultClass) {
		return IterationUtils.map(filterByType(diagnosticResults, typeString),
				new IMapFunctor<IDiagnosticResultBase, IDiagnosticResult<T>>() {

					@SuppressWarnings("unchecked")
					@Override
					public IDiagnosticResult<T> mapItem(
							IDiagnosticResultBase obj) {
						return (IDiagnosticResult<T>) obj;
					}
				});
	}

	public static <T extends IDiagnosticResultBase> Iterable<IDiagnosticResultBase> filterByType(
			final Iterable<T> diagnosticResults, final String typeString) {
		return IterationUtils.filter(diagnosticResults,
				new IUnaryPredicate<IDiagnosticResultBase>() {

					@Override
					public boolean evaluate(IDiagnosticResultBase obj) {

						return obj.getSubjectType().equals(typeString);
					}
				});
	}

	/**
	 * Filters the iteration by the date of the IDiagnosticResultBase. The
	 * dateFormat defines the degree of detail in the comparison of the date
	 * (same year, month, day, hour, ...)
	 * 
	 * @param <T>
	 * @param diagnosticResults
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static <T extends IDiagnosticResultBase> Iterable<IDiagnosticResultBase> filterByDate(
			final Iterable<T> diagnosticResults, final Date date,
			final DateFormat dateFormat) {
		return IterationUtils.filter(diagnosticResults,
				new IUnaryPredicate<IDiagnosticResultBase>() {

					@Override
					public boolean evaluate(IDiagnosticResultBase obj) {
						return dateFormat.format(obj.getDate()).equals(
								dateFormat.format(date));
					}
				});
	}

	/**
	 * Filters the iteration by the date of the IDiagnosticResultBase. All
	 * IDiagnosticResultBase objects with a date after or equal to the given
	 * date are returned.
	 * 
	 * @param <T>
	 * @param diagnosticResults
	 * @param date
	 * @return
	 */
	public static <T extends IDiagnosticResultBase> Iterable<IDiagnosticResultBase> filterByDateAfterOrEqual(
			final Iterable<T> diagnosticResults, final Date date) {
		return IterationUtils.filter(diagnosticResults,
				new IUnaryPredicate<IDiagnosticResultBase>() {

					@Override
					public boolean evaluate(IDiagnosticResultBase obj) {
						return obj.getDate().compareTo(date) >= 0;
					}
				});
	}
}
