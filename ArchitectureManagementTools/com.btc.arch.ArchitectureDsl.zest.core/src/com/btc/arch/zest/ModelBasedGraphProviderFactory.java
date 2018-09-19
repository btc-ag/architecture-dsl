package com.btc.arch.zest;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.widgets.Composite;

public class ModelBasedGraphProviderFactory implements ZestGraphProviderFactory {

	@Override
	public ZestGraphProvider createGraphProvider(Resource resource,
			Composite parent) {
		return new ModelBasedGraphProvider(resource, parent);
	}

}
