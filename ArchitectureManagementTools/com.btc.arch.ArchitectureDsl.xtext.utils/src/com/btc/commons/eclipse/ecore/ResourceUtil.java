package com.btc.commons.eclipse.ecore;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IterationUtils;

public class ResourceUtil {

	public static Collection<? extends EObject> extractContents(
			Iterable<? extends Resource> resolvedResources) {
		return IterationUtils
				.materialize(IterationUtils
						.mapToIterablesAndChain(
								resolvedResources,
								new IMapFunctor<Resource, Iterable<? extends EObject>>() {
	
									@Override
									public Iterable<? extends EObject> mapItem(
											Resource resource) {
										return resource.getContents();
									}
								}));
	}

}
