/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package com.btc.arch.architectureDsl.util;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Creation and access to the parsed object tree.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class ArchitectureDslResourceManager {

	private Resource resource;

	protected ArchitectureDslResourceManager() {
	}

	public ArchitectureDslResourceManager(Resource resource) {
		this.resource = resource;
	}

	/**
	 * @param architectureDslModelFile
	 *            The architecture dsl model file to parse
	 * @throws ArchDslException
	 */
	public ArchitectureDslResourceManager(final File architectureDslModelFile)
			throws ArchDslException {
		this.resource = ArchitectureDslFileUtils.loadResource(
				architectureDslModelFile, true);
	}

	/**
	 * @return The errors reported by the parser when parsing the given file
	 */
	public List<String> getErrors() {
		List<String> result = new ArrayList<String>();
		EList<Diagnostic> errors = resource.getErrors();
		Iterator<Diagnostic> i = errors.iterator();
		while (i.hasNext()) {
			Diagnostic next = i.next();
			result.add(MessageFormat.format(
					ArchitectureDslMessages.ArchitectureDslAst_0,
					next.getLine(), //$NON-NLS-1$
					next.getMessage()));
		}
		return result;
	}

	public TreeIterator<EObject> getContents() {
		return EcoreUtil.getAllProperContents(this.resource, false);
	}

	/**
	 * @return The loaded resource for the given architecture dsl model input
	 */
	public Resource getResource() {
		return this.resource;
	}

	@Override
	public String toString() {
		return String.format("%s with %s errors, resource: %s", //$NON-NLS-1$
				getClass().getSimpleName(), /* getModelName(), */getErrors()
						.size(), resource);
	}
}
