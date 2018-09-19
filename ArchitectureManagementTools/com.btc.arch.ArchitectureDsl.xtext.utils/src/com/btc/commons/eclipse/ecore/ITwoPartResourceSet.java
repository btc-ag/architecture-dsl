package com.btc.commons.eclipse.ecore;

import org.eclipse.emf.ecore.resource.Resource;

public interface ITwoPartResourceSet {
	Iterable<Resource> getPrimaryResources();

	Iterable<Resource> getAuxiliaryResources();

	Iterable<Resource> getAllResources();
}
