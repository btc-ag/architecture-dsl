package com.btc.arch.zest;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.widgets.Composite;

public interface ZestGraphProviderFactory {
	ZestGraphProvider createGraphProvider(Resource resource, Composite parent);
}
