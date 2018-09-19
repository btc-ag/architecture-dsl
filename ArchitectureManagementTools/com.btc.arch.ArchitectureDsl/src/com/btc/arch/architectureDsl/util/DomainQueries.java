package com.btc.arch.architectureDsl.util;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.btc.arch.architectureDsl.Domain;
import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.IMapFunctor;

public class DomainQueries {

	private static final Logger logger = Logger
	.getLogger(DomainQueries.class);
	
	private static final class CollectingMapFunctor implements
			IMapFunctor<Domain, Iterable<Domain>> {
		private final Collection<Domain> resultDomains;
		private boolean changed = false;

		private CollectingMapFunctor(Collection<Domain> resultDomains) {
			this.resultDomains = resultDomains;
		}

		@Override
		public Iterable<Domain> mapItem(Domain domain) {
			changed |= resultDomains.add(domain);
			return domain.getSubdomains();
		}

		public boolean changed() {
			return this.changed;
		}

		public void reset() {
			this.changed = false;
		}
	}

	public static Collection<Domain> getSubdomainsRTClosure(final Iterable<Domain> domains) {
		Set<Domain> subDomains = new HashSet<Domain>();
		addAllSubdomains(domains, new CollectingMapFunctor(
				subDomains));
		return subDomains;
	}

	public static Collection<Domain> getAllSubdomains(final Domain domain) {
		Set<Domain> subDomains = new HashSet<Domain>();
		// TODO wie soll die Semantik sein?
		// Alternative wäre: return
		// addAllSubdomains(Collections.singletonList(domain), subDomains);
		addAllSubdomains(domain.getSubdomains(), new CollectingMapFunctor(
				subDomains));
		return subDomains;
	}

	private static void addAllSubdomains(
			final Iterable<Domain> inputDomains,
			final CollectingMapFunctor collectingMapFunctor) {
		collectingMapFunctor.reset();
		final Iterable<Domain> directSubdomains = IterationUtils
				.mapToIterablesAndChain(inputDomains, collectingMapFunctor);

		if (collectingMapFunctor.changed())
			addAllSubdomains(directSubdomains, collectingMapFunctor);
	}

	public static Domain getClosestSuperDomain(final Domain domain) {
		final Iterable<Domain> allDomains = IterationUtils
				.filterByClass(domain.eResource().getResourceSet()
						.getAllContents(), Domain.class);

		for (Domain currentDomain : allDomains) {
			if (currentDomain.getSubdomains().contains(domain))
				return currentDomain;
		}
		return null;
	}

	public static SortedSet<String> getSortedDomainNames(
			final Iterable<Domain> domains) {
		final SortedSet<String> sortedDomainNames = new TreeSet<String>();
		for (Domain domain : domains) {
			if (!domain.eIsProxy()) {
				sortedDomainNames.add(domain.getName());
			} else {
				// TODO bekommt man den Namen der DomÃ¤ne heraus?
				logger.warn(MessageFormat.format(
						"Ignoring unresolved domain {0}", domain));
			}
		}
		return sortedDomainNames;
	}
}
