package com.btc.arch.xtext.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.linking.lazy.LazyLinker;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.parsetree.AbstractNode;
import org.eclipse.xtext.parsetree.LeafNode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.Triple;
import org.eclipse.xtext.util.Tuples;

import com.btc.commons.eclipse.core.EclipseResourceUtils;
import com.btc.commons.eclipse.ecore.ITwoPartResourceSet;
import com.btc.commons.eclipse.ecore.TwoPartResourceSet;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IUnaryClosure;
import com.btc.commons.java.functional.IterationUtils;

public class XtextResourceUtil {

	/**
	 * Precondition: The method xStandaloneSetup.doSetup() has to be called
	 * before this method. where xStandaloneSetup stands for the DSL specific
	 * StandaloneSetup class, as e.g. ArchitectureDslStandaloneSetup.
	 * 
	 * The method does not guarantee that all proxies of the resource and its
	 * containing resource set are resolved. Call
	 * {@link checkForUnresolvedReferences} to ensure this.
	 * 
	 * @param progressMonitor
	 *            TODO
	 * 
	 * @return
	 * @throws CoreException
	 * @throws IOException
	 */
	public static XtextResource getResolvedXtextResource(final IFile xtextFile,
			final IProgressMonitor progressMonitor) throws CoreException,
			IOException {
		final IProject project = xtextFile.getProject();
		// Open and refresh the project
		if (!project.isOpen()) {
			project.open(progressMonitor);
		}
		// if (!project.isSynchronized(IResource.DEPTH_INFINITE))
		project.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);

		// Get all resources of the project and put them in one resource set
		final XtextResourceSet resourceSet = new XtextResourceSet();
		final XtextResource xtextFileResource = getResource(xtextFile,
				resourceSet);

		resolveResource(xtextFileResource, project,
				xtextFile.getFileExtension(), progressMonitor);

		return xtextFileResource;
	}

	private static ResourceSet lastResolvedResourceSet;

	public static void resolveResource(final XtextResource resource,
			final String fileExtension, final IProgressMonitor progressMonitor)
			throws CoreException, IOException {

		// For performance reasons the resolution of the resource set
		// is only done if the currentResolvedResource Set does not
		// equal the resource set of the given resource.
		if (lastResolvedResourceSet == null
				|| !lastResolvedResourceSet.equals(resource.getResourceSet())) {

			final Path path = new Path(resource.getURI().device(), resource
					.getURI().path().replaceFirst("resource", ""));
			final IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(path);

			resolveResource(resource, file.getProject(), fileExtension,
					progressMonitor);

			lastResolvedResourceSet = resource.getResourceSet();
		}
	}

	private static void resolveResource(final XtextResource resource,
			final IProject project, final String fileExtension,
			final IProgressMonitor progressMonitor) throws CoreException,
			IOException {
		addAllResourcesToResourceSet(resource, project, fileExtension);
		assertResourceSetConsistency(resource.getResourceSet());
		loadResource(resource, progressMonitor);
	}

	public static ITwoPartResourceSet getResolvedXtextResources(
			final Iterable<IFile> sourceIFiles, IProgressMonitor progressMonitor)
			throws CoreException, IOException {
		final Collection<XtextResource> resources = new ArrayList<XtextResource>();

		final XtextResourceSet resourceSet = (XtextResourceSet) getResolvedXtextResource(
				sourceIFiles.iterator().next(), progressMonitor)
				.getResourceSet();
		for (final IFile file : sourceIFiles) {
			final XtextResource resource = getResource(file, resourceSet);
			loadResource(resource, progressMonitor);
			resources.add(resource);
		}
		return new TwoPartResourceSet(resources);
	}

	/**
	 * 
	 * @param contents
	 *            An xtext resource
	 * @return An empty collection if there are no unresolved references.
	 *         Otherwise a collection containing Triples with information about
	 *         the unresolved references. In the Triples the first entry is the
	 *         source element of the unresolved reference, the second entry
	 *         marks the affected Feature and the third entry contains the text
	 *         of the reference in the xtext document.
	 */
	public static Collection<Triple<EObject, EStructuralFeature, String>> checkForUnresolvedReferences(
			final Collection<? extends EObject> contents) {
		final Collection<Triple<EObject, EStructuralFeature, String>> unresolvedReferenceCollection = new ArrayList<Triple<EObject, EStructuralFeature, String>>();

		final Map<EObject, Collection<Setting>> unresolvedReferences = EcoreUtil.UnresolvedProxyCrossReferencer
				.find(contents);
		for (final EObject eObject : unresolvedReferences.keySet()) {
			for (final Setting setting : unresolvedReferences.get(eObject)) {
				final Object settingValue = setting.get(false);
				if (settingValue instanceof EObjectResolvingEList) {
					final EObjectResolvingEList<EObject> list = (EObjectResolvingEList<EObject>) settingValue;
					for (final EObject object : list) {
						checkSingleObject(setting.getEObject().eResource(),
								unresolvedReferenceCollection, setting, object);
					}
				} else if (settingValue instanceof EObject) {
					checkSingleObject(setting.getEObject().eResource(),
							unresolvedReferenceCollection, setting,
							(EObject) settingValue);
				} else {
					Logger.getLogger(XtextResourceUtil.class.getName())
							.info(MessageFormat
									.format(Messages.XtextResourceUtil_UnknownObjectType,
											settingValue));
				}
			}
		}
		return unresolvedReferenceCollection;
	}

	protected static void checkSingleObject(
			final Resource resource,
			final Collection<Triple<EObject, EStructuralFeature, String>> unresolvedReferenceCollection,
			final Setting setting, final EObject object) {
		if (object.eIsProxy()) {
			if (object instanceof InternalEObject
					&& resource instanceof XtextResource) {
				final InternalEObject basicEObject = (InternalEObject) object;
				final XtextResource xtextResource = (XtextResource) resource;
				final LazyLinker linker = (LazyLinker) xtextResource
						.getLinker();
				final Triple<EObject, EReference, AbstractNode> triple = linker
						.getEncoder().decode(xtextResource,
								basicEObject.eProxyURI().toString());
				final LeafNode node = (LeafNode) triple.getThird();
				final Triple<EObject, EStructuralFeature, String> unresolvedReference = Tuples
						.create(triple.getFirst(),
								setting.getEStructuralFeature(), node.getText());
				unresolvedReferenceCollection.add(unresolvedReference);
			} else {
				final Triple<EObject, EStructuralFeature, String> unresolvedReference = Tuples
						.create(object,
								null,
								Messages.XtextResourceUtil_UnknownResourceOrObjectType);
				unresolvedReferenceCollection.add(unresolvedReference);

			}
		}
	}

	/**
	 * Ensures that all resources in the given resource set are associated with
	 * that resource set.
	 * 
	 * @param resourceSet
	 * @throws IllegalArgumentException
	 *             if any resource has a different resource set
	 */
	private static void assertResourceSetConsistency(
			final ResourceSet resourceSet) {
		final StringBuilder problems = new StringBuilder();
		for (final Resource resource : resourceSet.getResources()) {
			if (resource.getResourceSet() != resourceSet) {
				problems.append(MessageFormat.format("{0} (resourceSet={1})\n", //$NON-NLS-1$
						resource.toString(), resource.getResourceSet()));
			}
		}
		if (problems.length() > 0) {
			throw new AssertionError(MessageFormat.format(
					Messages.XtextResourceUtil_InvalidResourceSet, problems));
		}
	}

	protected static void updateResourceSettings(final Resource resource) {
		if (resource instanceof LazyLinkingResource) {
			((LazyLinkingResource) resource).setEagerLinking(true);
		} else {
			throw new IllegalArgumentException(MessageFormat.format(
					Messages.XtextResourceUtil_NotALazyLinkingResource,
					resource));
		}
	}

	private static XtextResource getResource(final IFile file,
			final ResourceSet resourceSet) {
		final XtextResource resource = (XtextResource) resourceSet.getResource(
				createURI(file), true);
		updateResourceSettings(resource);
		return resource;
	}

	private static URI createURI(final IFile file) {
		return URI.createPlatformResourceURI(file.getFullPath().toString(),
				true);
	}

	private static void loadResource(final Resource resource,
			final IProgressMonitor progressMonitor) throws IOException {
		// resource.load(Collections.singletonMap(XtextResource.OPTION_ENCODING,
		// "UTF-8"));
		// resource.load(Collections.singletonMap(XtextResource.OPTION_RESOLVE_ALL,
		// Boolean.TRUE));
		resource.load(null); // TODO pass progressMonitor
	}

	private static void addAllResourcesToResourceSet(final Resource resource,
			final IContainer container, final String fileExtension)
			throws CoreException {
		final ResourceSet resourceSet = resource.getResourceSet();
		final IUnaryClosure<IFile> unaryClosure = new IUnaryClosure<IFile>() {

			@Override
			public void process(final IFile file) {
				// Das if-Statement sorgt dafür, dass nicht zwei Resources mit
				// unterschiedlichen URIs zur gleichen Datei im ResourceSet
				// enthalten sind.
				// TODO: Gibt es bessere Möglichkeiten zu prüfen, ob die
				// Resource aus dem file entstanden ist?
				// TODO funktioniert das so überhaupt? das eine ist ein Pfad,
				// das andere eine (absolute) URI, die können doch niemals
				// gleich sein...
				if (!file.getFullPath().toString()
						.equals("/" + resource.getURI().toString())) {
					getResource(file, resourceSet);
					// TODO the resource is created but not added to resourceSet
					// according to the name of the
					// called method AND according to the documentation of
					// resourceSet.getResource, but actually
					// it is added to the ResourceSet. This might change in
					// future versions...
				}
			}
		};
		EclipseResourceUtils.processFilesOnResourceTree(container,
				fileExtension, unaryClosure);
	}

	public static Collection<? extends EObject> extractContents(
			Iterable<? extends Resource> resolvedResources) {
		return IterationUtils
				.materialize(IterationUtils
						.mapToIterablesAndChain(
								resolvedResources,
								new IMapFunctor<Resource, Iterable<? extends EObject>>() {

									@Override
									public Iterable<? extends EObject> mapItem(
											Resource resource) {
										return resource.getContents();
									}
								}));
	}
}
