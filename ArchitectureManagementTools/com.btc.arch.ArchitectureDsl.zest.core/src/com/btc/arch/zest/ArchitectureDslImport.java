/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/

package com.btc.arch.zest;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;

import com.btc.arch.architectureDsl.util.ArchDslException;
import com.btc.arch.architectureDsl.util.ArchitectureDslResourceManager;

/**
 * Transformation of DOT files or strings to Zest Graph instances.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class ArchitectureDslImport implements ZestGraphProvider {
	private Resource architectureDslResource;
	private ArchitectureDslResourceManager architectureDslResourceManager;
	private final Composite parent;
	private final int style;

	/**
	 * CURRENTLY UNUSED
	 * 
	 * @param architectureDslResource
	 *            The architecture dsl model file to import
	 * @throws ArchDslException
	 */
	public ArchitectureDslImport(final Resource architectureDslResource,
			final Composite parent, final int style) throws ArchDslException {
		this.parent = parent;
		this.style = style;
		this.architectureDslResource = architectureDslResource;
		load();
	}

	// /**
	// * @param architectureDslModelFile
	// * The architecture dsl model file to import
	// * @throws ArchDslException
	// */
	// public ArchitectureDslImport(final IFile architectureDslModelFile,
	// final Composite parent, final int style) throws ArchDslException {
	// this.parent = parent;
	// this.style = style;
	// try {
	// this.architectureDslModel = EclipseFileUtils
	// .resolve(architectureDslModelFile.getLocationURI().toURL());
	// load();
	// } catch (MalformedURLException e) {
	// throw new ArchDslException(e);
	// }
	// }
	//
	// /**
	// * CURRENTLY UNUSED
	// *
	// * @param architectureDslModelString
	// * The architecture dsl model to import
	// * @throws ArchDslException
	// */
	// public ArchitectureDslImport(final String architectureDslModelString,
	// final Composite parent, final int style) throws ArchDslException {
	// this.parent = parent;
	// this.style = style;
	// init(architectureDslModelString);
	// }

	// private void init(final String architectureDslModelString)
	// throws ArchDslException {
	// if (architectureDslModelString == null
	// || architectureDslModelString.trim().length() == 0) {
	// throw new IllegalArgumentException(
	// ArchitectureDslZestMessages.ArchitectureDslImport_2 + ": "
	// + architectureDslModelString);
	// }
	// loadFrom(architectureDslModelString);
	// if (architectureDslResourceManager.getErrors().size() > 0) {
	// throw new ArchDslException(architectureDslResourceManager
	// .getErrors().toString());
	// }
	// }
	//
	// private void loadFrom(final String architectureDslModelString)
	// throws ArchDslException {
	// this.architectureDslModel = ArchitectureDslFileUtils
	// .writeTempFile(architectureDslModelString);
	// load();
	// }

	private void guardFaultyParse() {
		List<String> errors = this.architectureDslResourceManager.getErrors();
		if (errors.size() > 0) {
			throw new IllegalArgumentException(String.format(
					ArchitectureDslZestMessages.ArchitectureDslImport_1
							+ ": %s (%s)", architectureDslResource, //$NON-NLS-1$
					errors.toString()));
		}
	}

	private void load() throws ArchDslException {
		this.architectureDslResourceManager = new ArchitectureDslResourceManager(
				this.architectureDslResource);
	}

	/**
	 * @return The errors the parser reported when parsing the given DOT graph
	 */
	public List<String> getErrors() {
		return architectureDslResourceManager.getErrors();
	}

	@Override
	public String toString() {
		return String.format("%s of %s at %s", getClass().getSimpleName(), //$NON-NLS-1$
				architectureDslResourceManager, architectureDslResource);
	}

	@Override
	public Graph getGraph() {
		guardFaultyParse();
		/*
		 * TODO switch to a string as the member holding the architecture dsl
		 * model to avoid read-write here, and set that string as the resulting
		 * graph's data
		 */
		return new GraphCreatorInterpreter(parent, style,
				architectureDslResourceManager).getGraph();
	}
}
