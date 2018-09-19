package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.btc.arch.ArchitectureDslStandaloneSetup;
import com.btc.arch.xtext.util.XtextResourceUtil;
import com.btc.commons.eclipse.ecore.ResourceUtil;
import com.btc.commons.eclipse.ecore.TwoPartResourceSet;

abstract public class ArchDslFileUsageTestBase {
	protected Resource resource;
	private IProject project;

	public ArchDslFileUsageTestBase() {
		super();
	}

	@BeforeClass
	public static void setUpClass() {
		ArchitectureDslStandaloneSetup.doSetup();
	}

	@Before
	public void setUp() throws CoreException, IOException {
		final String testDataFileName = getTestDataFileName();
		final String projectName = getTestProjectName();
		project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);
		assertFalse("Project already exists, run with clean workspace!!!",
				project.exists());
		final File projectDirectory = new File(getTestDataBaseDirectory(),
				projectName);
		final IProjectDescription description = ResourcesPlugin.getWorkspace()
				.newProjectDescription(projectName);
		description.setLocation(Path.fromOSString(projectDirectory
				.getAbsolutePath()));
		project.create(description, IResource.NONE, null);
		project.open(null);

		// final URI uri = URI.createPlatformResourceURI("/" + projectName + "/"
		// + testDataFileName, true);
		// resource = ArchitectureDslFileUtils.getOneShotResource(uri);
		resource = XtextResourceUtil.getResolvedXtextResource(
				project.getFile(testDataFileName), null);

		final ArchitectureDslResourceManager architectureDslResourceManager = new ArchitectureDslResourceManager(
				resource);
		assertEquals(
				"resource has errors: "
						+ architectureDslResourceManager.getErrors(), 0,
				architectureDslResourceManager.getErrors().size());

		// TODO warum ist getErrors leer, obwohl z.B. undefinierte Subdomänen
		// angegeben sind?
	}

	@After
	public void tearDown() throws CoreException {
		this.project.delete(false, false, null);
	}

	/**
	 * 
	 * @return Returns the path of the test data file relative to
	 *         com.btc.arch.ArchitectureDsl
	 */
	abstract protected String getTestDataFileName();

	abstract protected String getTestProjectName();

	protected File getTestDataBaseDirectory() {
		return new File("test/models");
	}

	protected Collection<? extends EObject> getAllContents() {
		return ResourceUtil.extractContents(resource.getResourceSet()
				.getResources());
	}
	// private Collection<? extends EObject> getAllContents() {
	// return ResourceUtil.extractContents(new TwoPartResourceSet(Collections
	// .singletonList(resource)).getAllResources());
	// }
}
