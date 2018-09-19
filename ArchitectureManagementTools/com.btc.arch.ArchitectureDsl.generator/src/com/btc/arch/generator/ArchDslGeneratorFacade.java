package com.btc.arch.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.util.Triple;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.util.ArchitectureDslNameMapper;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.arch.base.Context;
import com.btc.arch.base.IContext;
import com.btc.arch.xtext.util.XtextResourceUtil;
import com.btc.commons.eclipse.core.EclipseFileUtils;
import com.btc.commons.eclipse.ecore.ITwoPartResourceSet;
import com.btc.commons.eclipse.ecore.ResourceUtil;
import com.btc.commons.emf.EcoreUtils;
import com.btc.commons.java.FileUtils;
import com.btc.commons.java.IInserter;
import com.btc.commons.java.Pair;
import com.btc.commons.java.StringUtils;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.ToStringMapFunctor;

/**
 * 
 * TODO do not pass properties file, but properties or IContext
 * 
 * TODO use this in the generator UI as well
 * 
 * @author SIGIESEC
 * 
 */
public class ArchDslGeneratorFacade {
	private final File projectPath;
	private final Iterable<File> sourceFiles;
	private final Iterable<File> propertiesFiles;
	private final Iterable<IArchitectureDSLGenerator> activeGenerators;
	private Properties properties;
	private IPath targetDirectory;
	private final Collection<? extends EObject> primaryContents;
	private final Collection<? extends EObject> allContents;
	private static final Logger logger = Logger
			.getLogger(ArchDslGeneratorFacade.class.getName());
	private static final String NEW_LINE = System.getProperty("line.separator");

	public ArchDslGeneratorFacade(
			final Iterable<IArchitectureDSLGenerator> activeGenerators,
			final File projectPath, final Iterable<File> propertiesFiles,
			final Iterable<File> sourceFiles, final IInserter<String> errors)
			throws ArchDslGeneratorException, IOException {
		this.activeGenerators = activeGenerators;
		this.projectPath = projectPath;
		this.propertiesFiles = propertiesFiles;
		this.sourceFiles = sourceFiles;
		final Pair<Collection<? extends EObject>, Collection<? extends EObject>> contents = checkAndInitialize(errors);
		if (contents != null) {
			this.primaryContents = contents.getFirst();
			this.allContents = contents.getSecond();
		} else {
			this.primaryContents = null;
			this.allContents = null;
		}
	}

	private Pair<Collection<? extends EObject>, Collection<? extends EObject>> checkAndInitialize(
			final IInserter<String> errors) throws ArchDslGeneratorException,
			IOException {
		// check properties file
		initializeProperties(errors);

		// check source file
		for (final File sourceFile : this.sourceFiles) {
			if (!sourceFile.isFile()) {
				errors.add(MessageFormat.format(
						Messages.ArchDslGenerator_SourceFileNotFound,
						sourceFile));
			}
		}

		// initialise amd check targetDirectory
		@SuppressWarnings({ "unchecked", "rawtypes" })
		final ArchDslGenerator archDslGenerator = new ArchDslGenerator(
				(Map) this.properties);
		this.targetDirectory = archDslGenerator.getTargetDir();
		final File targetDir = new File(this.targetDirectory.toOSString());
		if (!targetDir.isDirectory()) {
			errors.add(MessageFormat.format(
					Messages.ArchDslGenerator_TargetDirectoryNotFound,
					targetDir));
		}

		// check project path
		if (!this.projectPath.isDirectory()) {
			errors.add(MessageFormat.format(
					Messages.ArchDslGenerator_InvalidDirectory,
					this.projectPath));
		} else {
			if (checkAllSourceFiles(errors)) {
				try {
					final IProject project = getOrCreateProject(errors);
					if (project != null) {
						final Iterable<IFile> sourceIFiles = IterationUtils
								.map(this.sourceFiles,
										new IMapFunctor<File, IFile>() {

											@Override
											public IFile mapItem(
													final File sourceFile) {
												return project
														.getFile(EclipseFileUtils
																.toRelativePath(
																		sourceFile,
																		ArchDslGeneratorFacade.this.projectPath));
											}
										});

						final ITwoPartResourceSet resolvedXtextResources = XtextResourceUtil
								.getResolvedXtextResources(sourceIFiles, null);
						return new Pair<Collection<? extends EObject>, Collection<? extends EObject>>(
								ResourceUtil.extractContents(resolvedXtextResources
										.getPrimaryResources()),
								ResourceUtil
										.extractContents(resolvedXtextResources
												.getAllResources()));
					}
				} catch (final CoreException e) {
					for (Object object : e.getStackTrace()) {
						System.out.println(object);
					}
					final String message = MessageFormat.format(
							Messages.ArchDslGenerator_ProjectCreationError,
							e.getMessage());
					errors.add(message);
					LoggerUtils
							.logExtendedInfo(logger, Level.ERROR, message, e);
				} catch (final IOException e) {
					final String message = MessageFormat.format(
							Messages.ArchDslGenerator_ProjectCreationError,
							e.getMessage());
					errors.add(message);
					LoggerUtils
							.logExtendedInfo(logger, Level.ERROR, message, e);
				}
			}
		}
		return null;
	}

	private boolean checkAllSourceFiles(final IInserter<String> errors)
			throws IOException {
		boolean result = true;
		for (final File sourceFile : this.sourceFiles) {
			if (!FileUtils.isFileContainedInDirectory(this.projectPath,
					sourceFile)) {
				errors.add(MessageFormat.format(
						Messages.ArchDslGenerator_SourceFileNotInProject,
						sourceFile, this.projectPath));
				result = false;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param errors
	 * @return a valid project, or null if an error occurred
	 * @throws CoreException
	 *             if a CoreException occurred during project creation
	 */
	private IProject getOrCreateProject(final IInserter<String> errors)
			throws CoreException {

		final IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(this.projectPath.getName());
		if (!project.exists()) {
			final IProjectDescription description = ResourcesPlugin
					.getWorkspace().newProjectDescription(
							this.projectPath.getName());
			description.setLocation(Path.fromOSString(this.projectPath
					.getAbsolutePath()));

			try {
				project.create(description, IResource.NONE, null);
			} catch (CoreException e) {
				// Add further information to the log, since the message of the
				// CoreException is not detailed enough.
				MultiStatus status = new MultiStatus(
						ResourcesPlugin.PI_RESOURCES,
						IResourceStatus.INVALID_VALUE, "", null);
				status.merge(ResourcesPlugin.getWorkspace().validateName(
						description.getName(), IResource.PROJECT));
				URI location = description.getLocationURI();
				status.merge(ResourcesPlugin.getWorkspace()
						.validateProjectLocationURI(project, location));
				for (IStatus childStatus : status.getChildren()) {
					LoggerUtils.logExtendedInfo(logger, Level.ERROR,
							childStatus.getMessage(), e);
				}
				throw e;
			}
		} else {
			final URI existingProjectLocation = project.getDescription()
					.getLocationURI().normalize();
			final URI newProjectLocation = removeTrailingSlash(this.projectPath
					.toURI().normalize());
			if (!existingProjectLocation.toString().toLowerCase()
					.equals(newProjectLocation.toString().toLowerCase())) {
				errors.add(MessageFormat
						.format(Messages.ArchDslGeneratorFacade_ProjectExistsAtDifferentLocation,
								existingProjectLocation, newProjectLocation));
				return null;
			}

			logger.info(MessageFormat.format(
					Messages.ArchDslGenerator_ProjectAlreadyExists,
					this.projectPath.getName()));
		}

		return project;
	}

	private void initializeProperties(final IInserter<String> errors) {
		this.properties = new Properties();
		for (final File propertiesFile : this.propertiesFiles) {
			if (!propertiesFile.isFile()) {
				errors.add(MessageFormat.format(
						Messages.ArchDslGenerator_PropertiesFileNotFound,
						propertiesFile));
			} else {
				try {
					this.properties.load(new FileInputStream(propertiesFile));
				} catch (final IOException e) {
					final String message = MessageFormat.format(
							Messages.ArchDslGenerator_PropertiesFileUnreadable,
							propertiesFile);
					LoggerUtils
							.logExtendedInfo(logger, Level.ERROR, message, e);
					errors.add(message);
				}
			}
		}
	}

	private URI removeTrailingSlash(final URI uri) {
		final String slashSuffix = "/"; //$NON-NLS-1$
		final String uristring = uri.toString();
		if (uristring.endsWith(slashSuffix)) {
			try {
				return new URI(uristring.substring(0, uristring.length()
						- slashSuffix.length()));
			} catch (final URISyntaxException e) {
				throw new AssertionError();
			}
		} else {
			return uri;
		}
	}

	public Collection<String> generate() throws ArchDslGeneratorException {
		checkParseProblems(this.getAllContents());
		checkMultipleObjectDefinitions(this.getAllContents());
		final boolean modelValid = EcoreUtils.areAllElementsResolved(this
				.getAllContents());
		// TODO: Throw Exception if model is invalid?
		if (!modelValid) {
			logUnresolvedReferenceInfo(this.getAllContents());
		}
		final Collection<String> outputMessages = new ArrayList<String>();

		for (final IArchitectureDSLGenerator g : this.activeGenerators) {
			final IContext propertyContext = new Context(this.properties);
			final Collection<String> missingParameters = getMissingParameters(
					propertyContext, g);
			if (missingParameters.isEmpty()) {
				g.generate(this.getPrimaryContents(), this.getAllContents(),
						this.targetDirectory, null, propertyContext, modelValid);
				outputMessages.add(g.getOutputMessage());
			} else {
				final String message = MessageFormat.format(
						Messages.ArchDslGenerator_MissingParameters, g
								.getClass().getName(), StringUtils.join(
								missingParameters, ", ")); //$NON-NLS-1$
				logger.error(message);
			}
		}
		return outputMessages;
	}

	private void checkMultipleObjectDefinitions(
			final Collection<? extends EObject> contents)
			throws ArchDslGeneratorException {
		Map<EClass, Map<String, EObject>> eObjectsClassMap = new HashMap<EClass, Map<String, EObject>>();
		Map<String, Collection<EObject>> duplicateObjects = new HashMap<String, Collection<EObject>>();
		for (final EObject eObject : ModelQueries.getAllModelContents(contents)) {
			EStructuralFeature nameFeature = eObject.eClass()
					.getEStructuralFeature("name");
			if (nameFeature != null) {
				String eObjectName = (String) eObject.eGet(nameFeature);
				EClass eClass = eObject.eClass();
				Map<String, EObject> nameMap = eObjectsClassMap.get(eClass);
				if (nameMap == null) {
					nameMap = new HashMap<String, EObject>();
					nameMap.put(eObjectName, eObject);
					eObjectsClassMap.put(eClass, nameMap);
				} else {
					EObject eObject2 = nameMap.get(eObjectName);
					if (eObject2 == null)
						nameMap.put(eObjectName, eObject);
					else {
						collectDuplicateObjectDefinitions(duplicateObjects,
								eObjectName, eObject, eObject2);
					}
				}
			}
		}

		if (!duplicateObjects.keySet().isEmpty()) {
			List<String> messages = new ArrayList<String>();
			for (String duplicateName : duplicateObjects.keySet()) {
				List<org.eclipse.emf.common.util.URI> uris = new ArrayList<org.eclipse.emf.common.util.URI>();
				for (EObject eObject : duplicateObjects.get(duplicateName)) {
					uris.add(eObject.eResource().getURI());
				}
				String message = MessageFormat
						.format(Messages.ArchDslGeneratorFacade_MultipleObjectDefinition,
								duplicateName, StringUtils.join(IterationUtils
										.map(uris, new ToStringMapFunctor()),
										NEW_LINE));
				// ArchDslGeneratorFacade.logger.warn(message);
				messages.add(message);
			}

			throw new ArchDslGeneratorException(StringUtils.join(
					IterationUtils.map(messages, new ToStringMapFunctor()),
					NEW_LINE)); //$NON-NLS-1$
		}
	}

	private void collectDuplicateObjectDefinitions(
			Map<String, Collection<EObject>> duplicateObjects,
			String eObjectName, EObject eObject1, EObject eObject2) {
		Collection<EObject> duplicatesWithName = duplicateObjects
				.get(eObjectName);

		if (duplicatesWithName == null) {
			duplicatesWithName = new ArrayList<EObject>();
			duplicateObjects.put(eObjectName, duplicatesWithName);
		}

		if (!duplicatesWithName.contains(eObject1))
			duplicatesWithName.add(eObject1);
		if (!duplicatesWithName.contains(eObject2))
			duplicatesWithName.add(eObject2);
	}

	private void checkParseProblems(final Collection<? extends EObject> contents)
			throws ArchDslGeneratorException {
		final List<Resource> hasErrors = new ArrayList<Resource>();
		for (final Model model : ModelQueries.findModels(contents)) {
			final EList<Diagnostic> warnings = model.eResource().getWarnings();
			if (!warnings.isEmpty()) {
				ArchDslGeneratorFacade.logger.warn(MessageFormat.format(
						Messages.ArchDslGeneratorFacade_ResourceWarnings, model
								.eResource(), StringUtils.join(IterationUtils
								.map(warnings, new ToStringMapFunctor()),
								NEW_LINE)));
			}
			final EList<Diagnostic> errors = model.eResource().getErrors();
			if (!errors.isEmpty()) {
				ArchDslGeneratorFacade.logger.error(MessageFormat.format(
						Messages.ArchDslGeneratorFacade_ResourceErrors, model
								.eResource(), StringUtils.join(IterationUtils
								.map(errors, new ToStringMapFunctor()),
								NEW_LINE)));
				hasErrors.add(model.eResource());
			}
		}
		if (!hasErrors.isEmpty()) {
			throw new ArchDslGeneratorException(MessageFormat.format(
					Messages.ArchDslGeneratorFacade_ResourceErrorsException,
					StringUtils.join(IterationUtils.map(hasErrors,
							new ToStringMapFunctor()), ", "))); //$NON-NLS-1$
		}
	}

	private void logUnresolvedReferenceInfo(
			final Collection<? extends EObject> contents) {
		final Collection<Triple<EObject, EStructuralFeature, String>> unresolvedReferences = XtextResourceUtil
				.checkForUnresolvedReferences(contents);
		if (!unresolvedReferences.isEmpty()) {
			final StringBuilder builder = new StringBuilder();
			builder.append(Messages.ArchDslGenerator_UnresolvableReferencesDetails);
			for (final Triple<EObject, EStructuralFeature, String> triple : unresolvedReferences) {
				final EObject object = triple.getFirst();
				builder.append(MessageFormat.format("   {0} [ {1}: {2} ]\n", //$NON-NLS-1$
						ArchitectureDslNameMapper.getName(object), triple
								.getSecond().getName(), triple.getThird()));
			}
			logger.warn(Messages.ArchDslGenerator_UnresolvableReferences);
			logger.debug(builder.toString());
		}
	}

	private Collection<String> getMissingParameters(
			final IContext propertyContext, final IArchitectureDSLGenerator g) {
		final Collection<String> missingParameters = new ArrayList<String>();
		for (final String requiredParameter : g.getRequiredParameters()) {
			if (propertyContext.getParameter(requiredParameter) == null) {
				missingParameters.add(requiredParameter);
			}
		}
		return missingParameters;
	}

	public Collection<? extends EObject> getPrimaryContents() {
		return this.primaryContents;
	}

	public Collection<? extends EObject> getAllContents() {
		return this.allContents;
	}

}
