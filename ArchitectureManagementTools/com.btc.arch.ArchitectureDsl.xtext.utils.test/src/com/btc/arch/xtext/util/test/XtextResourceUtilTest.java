package com.btc.arch.xtext.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.Triple;
import org.eclipse.xtext.util.Tuples;
import org.junit.Test;

import com.btc.arch.ArchitectureDslStandaloneSetup;
import com.btc.arch.architectureDsl.ArchitectureDslPackage;
import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.arch.xtext.util.XtextResourceUtil;
import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.IMapFunctor;

public class XtextResourceUtilTest {
	@Test
	public void testGetResolvedXtextResource() throws Exception {
		Resource resource = createTestResource("model/BTC.CAB.Commons.Core.phys.archdsl");

		Model model = (Model) resource.getContents().get(0);
		Module module = model.getModules().get(0);
		assertEquals("BTC.CAB.Commons.Core", module.getName());
		assertFalse("Module " + module + " is a proxy", module.getModuleGroup()
				.eIsProxy());
		assertEquals("ProductDevelopment", module.getModuleGroup().getDomain()
				.getName());
	}

	@Test
	public void testGetResolvedXtextResourceFail() throws Exception {
		Resource resource = createTestResourceUnresolved("model/BTC.CAB.Commons.Core.phys.archdsl");

		Model model = (Model) resource.getContents().get(0);
		Module module = model.getModules().get(0);
		assertTrue("Module " + module + " is not a proxy", module
				.getModuleGroup().eIsProxy());
	}

	@Test
	public void testCheckForUnresolvedReferencesOK() throws Exception {
		Resource resource = createTestResource("model/BTC.CAB.Commons.Core.phys.archdsl");
		final Collection<Triple<EObject, EStructuralFeature, String>> unresolvedReferences = XtextResourceUtil
				.checkForUnresolvedReferences(resource.getContents());
		assertEquals("Unresolved references: " + unresolvedReferences, 0,
				unresolvedReferences.size());
	}

	@Test
	public void testCheckForUnresolvedReferencesFail() throws Exception {
		Resource resource = createTestResourceUnresolved("model/BTC.CAB.Commons.Core.phys.archdsl");
		final Collection<Triple<EObject, EStructuralFeature, String>> unresolvedReferences = XtextResourceUtil
				.checkForUnresolvedReferences(resource.getContents());
		assertEquals("Unresolved references: " + unresolvedReferences, 1,
				unresolvedReferences.size());

		Module module = ((Model) resource.getContents().get(0)).getModules()
				.get(0);
		assertEquals("BTC.CAB.Commons.Core", module.getName());
		assertEquals(
				Tuples.create(module,
						ArchitectureDslPackage.Literals.MODULE__MODULE_GROUP,
						"BTC.CAB"), unresolvedReferences.toArray()[0]);

	}

	protected Resource createTestResource(String filename)
			throws CoreException, IOException {
		final IFile xtextFile = getTestFile(filename);

		ArchitectureDslStandaloneSetup.doSetup();
		return XtextResourceUtil.getResolvedXtextResource(xtextFile, null);
	}

	protected Iterable<Resource> createTestResources(String[] filenames)
			throws CoreException, IOException {
		Iterable<IFile> xtextFiles = IterationUtils.map(
				Arrays.asList(filenames), new IMapFunctor<String, IFile>() {

					@Override
					public IFile mapItem(String filename) {
						try {
							return getTestFile(filename);
						} catch (CoreException e) {
							e.printStackTrace();
							return null;
						}
					}
				});

		ArchitectureDslStandaloneSetup.doSetup();
		return XtextResourceUtil.getResolvedXtextResources(xtextFiles, null)
				.getAllResources();
	}

	protected Resource createTestResourceUnresolved(String filename)
			throws CoreException, IOException {
		IFile xtextFile = getTestFile(filename);

		ArchitectureDslStandaloneSetup.doSetup();
		XtextResourceSet resourceSet = new XtextResourceSet();
		Resource resource = resourceSet.createResource(URI
				.createPlatformResourceURI(xtextFile.getFullPath().toString(),
						true));
		((LazyLinkingResource) resource).setEagerLinking(true);
		resource.load(null);
		return resource;

	}

	protected IFile getTestFile(String filename) throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject("XtextResourceUtilTest");
		if (!project.exists()) {
			project.create(null);
			project.open(null);
		}

		IFile xtextFile = project.getFile(filename);
		return xtextFile;
	}

	@Test
	public void testViaXtextUtils() throws CoreException, IOException {
		Resource resource = createTestResource("model/BTC.CAB.Commons.Core.phys.archdsl");

		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"BTC.CAB.Commons.Core");
		assertNotNull(module.eResource());
		Module module2 = ModelQueries.getModuleByName(resource.getContents(),
				"NoDeps");
		assertNotNull(module2.eResource());
	}

	@Test
	public void testMultipleFilesResolution() throws CoreException, IOException {
		final Iterable<Resource> resources = createTestResources(new String[] {
				"model/BTC.CAB.Commons.Core.phys.archdsl",
				"model/BTC.CAB.phys.archdsl" });
		final Collection<? extends EObject> contents = XtextResourceUtil
				.extractContents(resources);
		final Collection<Triple<EObject, EStructuralFeature, String>> unresolvedReferences = XtextResourceUtil
				.checkForUnresolvedReferences(contents);
		assertEquals("Unresolved references: " + unresolvedReferences, 0,
				unresolvedReferences.size());
	}

	@Test
	public void testMultipleFilesHomogeneity() throws CoreException,
			IOException {
		final Iterable<Resource> resources = createTestResources(new String[] {
				"model/BTC.CAB.Commons.Core.phys.archdsl",
				"model/BTC.CAB.phys.archdsl" });
		final Collection<? extends EObject> contents = XtextResourceUtil
				.extractContents(resources);
		System.out.println(contents);
		Module module = ModelQueries.getModuleByName(contents,
				"BTC.CAB.Commons.Core");
		assertNotNull(module);
		ModuleGroup groupFromModule = module.getModuleGroup();
		ModuleGroup groupFromContents = ModelQueries.getModuleGroupByName(
				contents, "BTC.CAB");
		assertEquals(groupFromContents.eResource().getURI(), groupFromModule
				.eResource().getURI());
		assertEquals(groupFromContents.eResource(), groupFromModule.eResource());
		assertEquals(groupFromContents, groupFromModule);
	}

	@Test
	public void testMultipleFilesHomogeneity2() throws CoreException,
			IOException {
		final Iterable<Resource> resources = createTestResources(new String[] {
				"model/BTC.CAB.phys.archdsl",
				"model/BTC.CAB.Commons.Core.phys.archdsl" });
		final Collection<? extends EObject> contents = XtextResourceUtil
				.extractContents(resources);
		System.out.println(contents);
		Module module = ModelQueries.getModuleByName(contents,
				"BTC.CAB.Commons.Core");
		assertNotNull(module);
		ModuleGroup groupFromModule = module.getModuleGroup();
		ModuleGroup groupFromContents = ModelQueries.getModuleGroupByName(
				contents, "BTC.CAB");
		assertEquals(groupFromContents.eResource().getURI(), groupFromModule
				.eResource().getURI());
		assertEquals(groupFromContents.eResource(), groupFromModule.eResource());
		assertEquals(groupFromContents, groupFromModule);
	}
}
