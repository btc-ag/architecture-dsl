package com.btc.arch.base;

public interface IContextDependentServiceFactory {

	void bindContext(IContext _context);

	void unbindContext(IContext _context);

	String[] getContextParameterNames();

}