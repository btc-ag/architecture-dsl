package com.btc.arch.visualstudio.service;

import org.apache.log4j.Logger;

import com.btc.arch.base.IContext;
import com.btc.arch.base.IContextDependentServiceFactory;
import com.btc.arch.base.dependency.FileTreeDependencySourceServiceFactory;
import com.btc.arch.base.dependency.IDependencySource;
import com.btc.arch.visualstudio.VSDependencyParserFactory;
import com.btc.commons.java.IFactory;

public class VSDependencySourceServiceFactory implements
		IContextDependentServiceFactory, IFactory<IDependencySource> {

	private IContext context;
	private final Logger logger = Logger.getLogger(getClass());

	@Override
	public void bindContext(final IContext _context) {
		this.context = _context;
	}

	@Override
	public void unbindContext(final IContext _context) {
		this.context = null;
	}

	@Override
	public String[] getContextParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDependencySource create() {

		final FileTreeDependencySourceServiceFactory serviceFactory = new FileTreeDependencySourceServiceFactory();
		final IDependencySource dependencySource;
		if (this.context.hasAllParameters(serviceFactory.getContextParameterNames())) {
			serviceFactory.bindContext(this.context);
			// TODO this should not depend on a concrete parser factory, but use
			// some key
			final VSDependencyParserFactory vsDependencyParserFactory = new VSDependencyParserFactory();
			serviceFactory.bindParserFactory(vsDependencyParserFactory);
			dependencySource = serviceFactory.createDependencySource();
			serviceFactory.unbindParserFactory(vsDependencyParserFactory);
			serviceFactory.unbindContext(this.context);
		} else {
			this.logger.info("Skipping extracted parameter validation, context lacks some required parameters");
			dependencySource = null;
		}
		return dependencySource;

	}
}
