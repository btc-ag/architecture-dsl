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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import com.btc.arch.ArchitectureDslStandaloneSetup;
import com.google.inject.Injector;

/**
 * Static helper methods for working with files.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class ArchitectureDslFileUtils {
	public static final String EXTENSION = ".archdsl";

	private ArchitectureDslFileUtils() {/* Enforce non-instantiability */
	}

	/**
	 * @param text
	 *            The string to write out to a file
	 * @param destination
	 *            The file to write the string to
	 * @return The file containing the given string
	 * @throws ArchDslException
	 */
	public static File write(final String text, final File destination)
			throws ArchDslException {
		try {
			final FileWriter writer = new FileWriter(destination);
			writer.write(text);
			writer.flush();
			writer.close();
			return destination;
		} catch (IOException e) {
			throw new ArchDslException(e);
		}
	}

	/**
	 * @param file
	 *            The file to read into a string
	 * @return The string containing the contents of the given file
	 * @throws ArchDslException
	 */
	public static String read(final File file) throws ArchDslException {
		final StringBuilder builder = new StringBuilder();
		try {
			Scanner s = new Scanner(file);
			while (s.hasNextLine()) {
				builder.append(s.nextLine()).append("\n"); //$NON-NLS-1$
			}
		} catch (FileNotFoundException e) {
			throw new ArchDslException(e);
		}
		return builder.toString();
	}

	/**
	 * @param text
	 *            The string to write out to a temp file
	 * @return The temp file containing the given string
	 * @throws ArchDslException
	 */
	public static File writeTempFile(final String text) throws ArchDslException {
		try {
			return write(text,
					File.createTempFile("architectureDsl", EXTENSION)); //$NON-NLS-1$//$NON-NLS-2$
		} catch (IOException e) {
			throw new ArchDslException(e);
		}
	}

	/**
	 * Loads the resource from the given file and adds all imported models of
	 * the models in the resource.
	 * 
	 * The method can not be generalised since the doSetup method is only
	 * defined for concrete StandaloneSetups and not in a more abstract
	 * interface or superclass.
	 * 
	 * @param file
	 *            The file to read the resource from
	 * @return The resource in the given file including all imported models.
	 * @throws ArchDslException
	 */
	public static Resource loadResource(final File file,
			boolean loadImportedModels) throws ArchDslException {
		// The following line is only needed if the resource is loaded using a
		// platform:/resource uri
		// and else prevents the loading of a resource in a setup outside of
		// eclipse
		// new StandaloneSetup().setPlatformUri("..");
		ArchitectureDslStandaloneSetup.doSetup();
		final ResourceSet set = new ResourceSetImpl();
		final Resource resource = set.getResource(
				URI.createURI(file.toURI().toString()), true);
		if (!resource.isLoaded()) {
			try {
				resource.load(Collections.EMPTY_MAP);
			} catch (IOException e) {
				throw new ArchDslException(e);
			}
		}

		if (!resource.getErrors().isEmpty()) {
			String message = "";
			for (Diagnostic diagnostic : resource.getErrors()) {
				message += "Error in line " + diagnostic.getLine() + " of "
						+ file.getAbsolutePath() + ": "
						+ diagnostic.getMessage() + "\n";
			}
			throw new ArchDslException(message);
		}

		// if (loadImportedModels){
		// // In the standard case of an Xtext file res.getContents will return
		// a
		// // list containing only one EObject. The loop is chosen here to also
		// // allow for non-standard cases.
		// EList<EObject> resourceContents = new BasicEList<EObject>();
		// resourceContents.addAll(resource.getContents());
		// for (EObject model : resourceContents) {
		// loadImportedModels(resource, (Model) model);
		// }
		// }
		return resource;
	}

	// private static void loadImportedModels(Resource res, Model model) throws
	// ArchDslException {
	// for (Import imp : model.getImports()) {
	// try {
	// String urlString = "file:/"
	// + model.eResource().getURI().toFileString()
	// + imp.getImportURI();
	// urlString = urlString.replaceFirst(model.eResource().getURI()
	// .lastSegment(), "");
	// File file = EclipseFileUtils
	// .resolve(new URL(urlString));
	// Resource resource = ArchitectureDslFileUtils.loadResource(file, true);
	// // It is assumed that each imported file only contains one model
	// Model importedModel = (Model) resource.getContents().get(0);
	//
	// res.getContents().add(importedModel);
	//
	// } catch (MalformedURLException e) {
	// throw new ArchDslException(e);
	// }
	// }
	// }

	public static Resource getOneShotResource(URI uri) {
		// TODO this does more or less the same as loadResource, but does not
		// depend on ResourceSetImpl
		final Injector injector = new ArchitectureDslStandaloneSetup()
				.createInjectorAndDoEMFRegistration();
		final ResourceSet resourceSet = injector.getInstance(ResourceSet.class);

		final Resource resource = resourceSet.getResource(uri, true);
		return resource;
	}
}
