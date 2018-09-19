package com.btc.arch.scoping;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.ignorecase.IIgnoreCaseResourceDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescription;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.ModuleGroup;
import com.btc.arch.architectureDsl.util.ModuleQueries;
import com.btc.arch.xtext.util.XtextResourceUtil;

public class ArchDslDefaultResourceDescriptionDecorator implements
		IIgnoreCaseResourceDescription {

	private static final Logger logger = Logger
			.getLogger(ArchDslDefaultResourceDescriptionDecorator.class);
	private final DefaultResourceDescription decoratee;

	public ArchDslDefaultResourceDescriptionDecorator(
			DefaultResourceDescription defaultResourceDescription) {
		this.decoratee = defaultResourceDescription;
	}

	@Override
	public Iterable<IReferenceDescription> getReferenceDescriptions() {
		// If a file does not have explicit xtext references, the
		// GlobalScopeProvider is not executed. So the resource has to
		// be resolved here in order to set the implicit references.
		if (this.decoratee.getResource().getResourceSet().getResources().size() == 1) {
			try {
				// TODO "archdsl" is not a valid file extension. A file
				// extension by definition starts with a "."
				// use ArchitectureDslFileUtils.EXTENSION
				XtextResourceUtil.resolveResource(
						(XtextResource) this.decoratee.getResource(),
						"archdsl", null);
			} catch (CoreException e) {
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

		// Makes the implicit module group references explicit so that
		// they are recognized by the DefaultResourceDescription.
		// TODO FIXME the model should not be changed...
		final Model model = (Model) this.decoratee.getResource().getContents()
				.get(0);
		for (Module module : model.getModules()) {
			ModuleGroup effectiveModuleGroup = ModuleQueries
					.getEffectiveModuleGroup(module);
			if (effectiveModuleGroup != null)
				module.setModuleGroup(effectiveModuleGroup);
		}
		return this.decoratee.getReferenceDescriptions();
	}

	@Override
	public Iterable<IEObjectDescription> getExportedObjects(EClass clazz,
			String name) {
		return decoratee.getExportedObjects(clazz, name);
	}

	@Override
	public Iterable<IEObjectDescription> getExportedObjectsIgnoreCase(
			EClass clazz, String name) {
		return decoratee.getExportedObjectsIgnoreCase(clazz, name);
	}

	@Override
	public Iterable<IEObjectDescription> getExportedObjects(EClass clazz) {
		return decoratee.getExportedObjects(clazz);
	}

	@Override
	public Iterable<IEObjectDescription> getExportedObjectsForEObject(
			EObject object) {
		return decoratee.getExportedObjectsForEObject(object);
	}

	@Override
	public Iterable<IEObjectDescription> getExportedObjects() {
		return decoratee.getExportedObjects();
	}

	@Override
	public Iterable<String> getImportedNames() {
		return decoratee.getImportedNames();
	}

	@Override
	public URI getURI() {
		return decoratee.getURI();
	}

	@Override
	public String toString() {
		return decoratee.toString();
	}
}
