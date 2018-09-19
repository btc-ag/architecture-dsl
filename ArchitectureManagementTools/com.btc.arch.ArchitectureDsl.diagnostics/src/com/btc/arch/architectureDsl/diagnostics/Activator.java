package com.btc.arch.architectureDsl.diagnostics;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.btc.arch.base.dependency.IDependencySource;
import com.btc.arch.diagnostics.api.IDiagnosticResultSourceRegistry;
import com.btc.commons.java.functional.UnmodifiableIterable;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static Activator instance;

	static BundleContext getContext() {
		return context;
	}

	private final List<IDependencySource> dependencySources;
	private final IDiagnosticResultSourceRegistry diagnosticResultSourceRegistry;

	public Activator() {
		this.dependencySources = new ArrayList<IDependencySource>();
		this.diagnosticResultSourceRegistry = new DiagnosticResultSourceRegistry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		Activator.instance = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		Activator.instance = null;
	}

	public static Activator getInstance() {
		return Activator.instance;
	}

	public void registerDependencySource(IDependencySource dependencySource) {
		this.dependencySources.add(dependencySource);
	}

	public void unregisterDependencySource(IDependencySource dependencySource) {
		this.dependencySources.remove(dependencySource);
	}

	public Iterable<IDependencySource> getDependencySources() {
		return new UnmodifiableIterable<IDependencySource>(dependencySources);
	}

	public IDiagnosticResultSourceRegistry getDiagnosticResultSourceRegistry() {
		return this.diagnosticResultSourceRegistry;
	}

}
