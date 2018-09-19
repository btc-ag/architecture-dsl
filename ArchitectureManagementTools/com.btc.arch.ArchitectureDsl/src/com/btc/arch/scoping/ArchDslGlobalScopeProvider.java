/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.btc.arch.scoping;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.ResourceSetReferencingResourceSet;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.AbstractExportedObjectsAwareScopeProvider;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.btc.arch.xtext.util.XtextResourceUtil;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * A {@link IGlobalScopeProvider} which puts all elements in the project on the
 * scope.
 * 
 * This is a clone of ResourceSetGlobalScopeProvider in which only the
 * ResourceSet taken has been changed.
 * 
 * @author Sven Efftinge - Author of ResourceSetGlobalScopeProvider
 * @author Niels Streekmann - Adjustment of the chosen ResourceSet.
 */
public class ArchDslGlobalScopeProvider extends
		AbstractExportedObjectsAwareScopeProvider implements
		IGlobalScopeProvider {

	private static final Logger logger = Logger
			.getLogger(ArchDslGlobalScopeProvider.class);

	@Override
	public IScope getScope(EObject context, EReference reference) {
		try {
			IScope parent = IScope.NULLSCOPE;
			if (context.eResource() == null
					|| context.eResource().getResourceSet() == null)
				return parent;

			XtextResourceUtil.resolveResource(
					(XtextResource) context.eResource(), "archdsl", null);

			final ResourceSet resourceSet = context.eResource()
					.getResourceSet();

			if (resourceSet instanceof ResourceSetReferencingResourceSet) {
				ResourceSetReferencingResourceSet set = (ResourceSetReferencingResourceSet) resourceSet;
				Iterable<ResourceSet> referencedSets = Iterables.reverse(set
						.getReferencedResourceSets());
				for (ResourceSet referencedSet : referencedSets) {
					parent = createScopeWithQualifiedNames(parent, context,
							reference, referencedSet);
				}
			}
			return createScopeWithQualifiedNames(parent, context, reference,
					resourceSet);

		} catch (CoreException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return IScope.NULLSCOPE;
	}

	protected IScope createScopeWithQualifiedNames(final IScope parent,
			final EObject context, final EReference reference,
			ResourceSet resourceSet) {
		Iterable<IResourceDescription> descriptions = Iterables.transform(
				resourceSet.getResources(),
				new Function<Resource, IResourceDescription>() {
					@Override
					public IResourceDescription apply(Resource from) {
						return getResourceDescription(from);
					}
				});
		final Iterable<IResourceDescription> filteredDescriptions = Iterables
				.filter(descriptions, Predicates.notNull());
		Iterable<Iterable<IEObjectDescription>> objectDescriptionsIter = Iterables
				.transform(
						filteredDescriptions,
						new Function<IResourceDescription, Iterable<IEObjectDescription>>() {
							@Override
							public Iterable<IEObjectDescription> apply(
									IResourceDescription from) {
								return from.getExportedObjects(reference
										.getEReferenceType());
							}
						});
		Iterable<IEObjectDescription> objectDescriptions = Iterables
				.concat(objectDescriptionsIter);
		return new SimpleScope(parent, objectDescriptions) {
			@Override
			public IEObjectDescription getContentByName(String name) {
				IEObjectDescription result = null;
				for (IResourceDescription description : filteredDescriptions) {
					Iterable<IEObjectDescription> objects = description
							.getExportedObjects(reference.getEReferenceType(),
									name);
					Iterator<IEObjectDescription> iter = objects.iterator();
					if (iter.hasNext()) {
						if (result != null)
							return getOuterScope().getContentByName(name);
						result = iter.next();
						if (iter.hasNext())
							return getOuterScope().getContentByName(name);
					}
				}
				if (result != null)
					return result;
				return getOuterScope().getContentByName(name);
			}
		};
	}

}
