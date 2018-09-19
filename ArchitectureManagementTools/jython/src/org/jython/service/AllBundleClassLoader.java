/*******************************************************************************
 * Copyright (c) 2008 Guillermo Gonzalez.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.jython.service;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;

/**
 * @author Guillermo Gonzalez <guillo.gonzo@gmail.com>
 *
 * Classloader that knows about all the bundles...
 */
public class AllBundleClassLoader extends ClassLoader {

	private Bundle[] bundles;
	
	public AllBundleClassLoader(Bundle[] bundles, ClassLoader parent) {
		super(parent);
		this.bundles = bundles;
		setPackageNames(bundles);
	}

	public Class loadClass(String className) throws ClassNotFoundException {
		//setPyClasspath(bundles);
		try {
			return super.loadClass(className);
		} catch (ClassNotFoundException e) {
			// Look for the class from the bundles.
			for (int i = 0; i < bundles.length; ++i) {
				try {
                    if(bundles[i].getState() == Bundle.ACTIVE){ 
                        return bundles[i].loadClass(className);
                    }
				} catch (Throwable e2) {
				}
			}
			// Didn't find the class anywhere, rethrow e.
			throw e;
		}
	}
	
	
	/**
	 * The package names the bundles provide
	 */
	private String[] packageNames;
	
	/**
	 * Set the package names available given the bundles that we can access
	 */
	private void setPackageNames(Bundle[] bundles) {
		List<String> names = new ArrayList<String>();
		for (int i = 0; i < bundles.length; ++i) {
			String packages = (String) bundles[i].getHeaders().get("Provide-Package");
			if (packages != null) {
				String[] pnames = packages.split(",");
				for (int j = 0; j < pnames.length; ++j) {
					names.add(pnames[j].trim());
				}
			}
			packages = (String) bundles[i].getHeaders().get("Export-Package");
			if (packages != null) {
				String[] pnames = packages.split(",");
				for (int j = 0; j < pnames.length; ++j) {
					names.add(pnames[j].trim());
				}
			}
		}
		packageNames = (String[]) names.toArray(new String[names.size()]);
	}
	
	/**
	 * @return the package names available for the passed bundles
	 */
	public String[] getPackageNames() {
		return packageNames;
	}
}
