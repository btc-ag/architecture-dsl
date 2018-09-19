package com.btc.arch.generator.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.base.Context;
import com.btc.arch.generator.ArchDslGeneratorException;
import com.btc.arch.xtext.util.XtextResourceUtil;

public abstract class ArchDslGeneratorDelegate extends ActionDelegate implements
		IObjectActionDelegate {

	private Map<Object, Object> propertyMap;
	private IFile xtextFile;

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		TreeSelection ts = (TreeSelection) selection;
		xtextFile = (IFile) ts.getPaths()[0].getLastSegment();

		try {
			Map<QualifiedName, Object> projectPropertyMap = xtextFile
					.getProject().getPersistentProperties();
			this.propertyMap = new HashMap<Object, Object>();
			for (QualifiedName qualifiedNameKey : projectPropertyMap.keySet()) {
				if (!qualifiedNameKey.getQualifier().isEmpty()) {
					this.propertyMap.put(qualifiedNameKey.toString(),
							projectPropertyMap.get(qualifiedNameKey));
				} else
					this.propertyMap.put(qualifiedNameKey.getLocalName(),
							projectPropertyMap.get(qualifiedNameKey));
			}

		} catch (CoreException e) {
			showMessage(e.getMessage(), true);
		}
	}

	protected void showMessage(String message, boolean error) {
		MessageBox mb;
		if (error)
			mb = new MessageBox(new Shell(), SWT.ICON_ERROR);
		else
			mb = new MessageBox(new Shell());
		mb.setText("Architecture DSL Generator");
		mb.setMessage(message);
		mb.open();
	}

	protected Model getModel() throws ArchDslGeneratorException {
		try {
			// TODO use progress monitor here
			Resource resource = XtextResourceUtil.getResolvedXtextResource(
					xtextFile, null);
			Model model = (Model) resource.getContents().get(0);
			return model;
		} catch (IOException e) {
			throw new ArchDslGeneratorException(e);
		} catch (CoreException e) {
			throw new ArchDslGeneratorException(e);
		}
	}

	public Resource.Factory.Registry getResourceFactoryRegistry() {
		Resource.Factory.Registry resourceFactoryRegistry = new ResourceFactoryRegistryImpl() {
			@Override
			protected Resource.Factory delegatedGetFactory(URI uri,
					String contentTypeIdentifier) {
				return convert(getFactory(uri,
						Resource.Factory.Registry.INSTANCE
								.getProtocolToFactoryMap(),
						Resource.Factory.Registry.INSTANCE
								.getExtensionToFactoryMap(),
						Resource.Factory.Registry.INSTANCE
								.getContentTypeToFactoryMap(),
						contentTypeIdentifier, false));
			}

			@Override
			protected URIConverter getURIConverter() {
				return new ExtensibleURIConverterImpl();
			}

			@Override
			protected Map<?, ?> getContentDescriptionOptions() {
				return new HashMap<Object, Object>();
			}
		};
		return resourceFactoryRegistry;
	}

	protected Context getContext() {
		return new Context(propertyMap);
	}
}
