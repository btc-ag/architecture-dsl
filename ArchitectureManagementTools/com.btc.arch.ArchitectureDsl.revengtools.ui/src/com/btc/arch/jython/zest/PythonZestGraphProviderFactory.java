package com.btc.arch.jython.zest;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.btc.arch.architectureDsl.util.ArchitectureDslResourceManager;
import com.btc.arch.jython.RevEngToolsInterpreter;
import com.btc.arch.jython.emf.EMFPythonGraphProvider;
import com.btc.arch.zest.ZestGraphProvider;
import com.btc.arch.zest.ZestGraphProviderFactory;

public class PythonZestGraphProviderFactory implements ZestGraphProviderFactory {

	@Override
	public ZestGraphProvider createGraphProvider(Resource resource, Composite parent) {
		RevEngToolsInterpreter interpreter = new RevEngToolsInterpreter();

		return new PythonZestGraphProvider(
				parent,
				SWT.NONE,
				new EMFPythonGraphProvider(
						new ArchitectureDslResourceManager(resource),
						interpreter), interpreter, false);

	}

}
