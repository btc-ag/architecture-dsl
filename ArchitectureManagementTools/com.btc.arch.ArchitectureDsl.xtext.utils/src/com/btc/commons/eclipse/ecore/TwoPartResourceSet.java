package com.btc.commons.eclipse.ecore;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.btc.commons.java.CollectionUtils;
import com.btc.commons.java.functional.IterationUtils;

final public class TwoPartResourceSet implements ITwoPartResourceSet {
	private final Iterable<Resource> primaryResources, auxiliaryResources;

	public TwoPartResourceSet(Iterable<? extends Resource> primaryResources,
			Iterable<? extends Resource> auxiliaryResources) {
		this.primaryResources = (Iterable<Resource>) primaryResources;
		this.auxiliaryResources = (Iterable<Resource>) auxiliaryResources;
	}

	public TwoPartResourceSet(ResourceSet resourceSet,
			Iterable<? extends Resource> primaryResources) {
		this(primaryResources, getRemaining(resourceSet, primaryResources));
	}

	private static Set<Resource> getRemaining(ResourceSet resourceSet,
			Iterable<? extends Resource> primaryResources) {
		final Set<Resource> calculatedAuxiliaryResources = new HashSet<Resource>(
				resourceSet.getResources());
		CollectionUtils.removeAll(calculatedAuxiliaryResources,
				primaryResources);
		return calculatedAuxiliaryResources;
	}

	public TwoPartResourceSet(Iterable<? extends Resource> primaryResources) {
		this(primaryResources.iterator().next().getResourceSet(),
				primaryResources);
	}

	@Override
	public Iterable<Resource> getPrimaryResources() {
		return this.primaryResources;
	}

	@Override
	public Iterable<Resource> getAuxiliaryResources() {
		return this.auxiliaryResources;
	}

	@Override
	public Iterable<Resource> getAllResources() {
		return IterationUtils.chain(getPrimaryResources(),
				getAuxiliaryResources());
	}

}
