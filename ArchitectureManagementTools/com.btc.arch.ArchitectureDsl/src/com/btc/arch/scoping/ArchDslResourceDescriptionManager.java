package com.btc.arch.scoping;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionManager;

public class ArchDslResourceDescriptionManager extends
		DefaultResourceDescriptionManager {

	@Override
	protected IResourceDescription internalGetResourceDescription(
			Resource resource, IQualifiedNameProvider nameProvider) {
		return new ArchDslDefaultResourceDescriptionDecorator(new DefaultResourceDescription(
				resource, nameProvider));
	}
}
