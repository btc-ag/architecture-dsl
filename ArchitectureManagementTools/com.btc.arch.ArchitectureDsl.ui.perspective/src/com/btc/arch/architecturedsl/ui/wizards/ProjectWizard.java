package com.btc.arch.architecturedsl.ui.wizards;

import java.net.URI;

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import org.eclipse.xtext.ui.XtextProjectHelper;

public class ProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	private static final String PAGE_NAME = "Architecture Management Project Wizard";
	private static final String WIZARD_NAME = "Architecture Management Project Wizard"; 
	
	private IConfigurationElement configurationElement;
	
	private WizardNewProjectCreationPage pageOne;
	
	public ProjectWizard() {
		setWindowTitle(WIZARD_NAME); 
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean performFinish() {
		String name = pageOne.getProjectName();  
		URI location = null;  
		if (!pageOne.useDefaults()) {  
			location = pageOne.getLocationURI();  
		}
		createArchitectureManagementProject(name, location); 
		BasicNewProjectResourceWizard.updatePerspective(configurationElement);
		return true;  
	}

	private void createArchitectureManagementProject(String projectName, URI location) {
		IProject project = createBaseProject(projectName, location); 
		try {
			addXtextNature(project);
			// Only show .archdsl files
			project.createFilter( 
					IResourceFilterDescription.INCLUDE_ONLY | 
					IResourceFilterDescription.INHERITABLE |
					IResourceFilterDescription.FILES,
						new FileInfoMatcherDescription("org.eclipse.ui.ide.multiFilter",
								"1.0-name-matches-false-false-*.archdsl"),								
						IResource.BACKGROUND_REFRESH, 
						new NullProgressMonitor());
			// Exclude all src, etc, doc and include folders - It is assumed that the architecture 
			// description is stored in the build folders
			project.createFilter( 
					IResourceFilterDescription.EXCLUDE_ALL | 
					IResourceFilterDescription.INHERITABLE |
					IResourceFilterDescription.FOLDERS,
						new FileInfoMatcherDescription("org.eclipse.ui.ide.multiFilter",
								"1.0-name-matches-false-false-src"),								
						IResource.BACKGROUND_REFRESH, 
						new NullProgressMonitor());
			project.createFilter( 
					IResourceFilterDescription.EXCLUDE_ALL | 
					IResourceFilterDescription.INHERITABLE |
					IResourceFilterDescription.FOLDERS,
						new FileInfoMatcherDescription("org.eclipse.ui.ide.multiFilter",
								"1.0-name-matches-false-false-include"),								
						IResource.BACKGROUND_REFRESH, 
						new NullProgressMonitor());
			project.createFilter( 
					IResourceFilterDescription.EXCLUDE_ALL | 
					IResourceFilterDescription.INHERITABLE |
					IResourceFilterDescription.FOLDERS,
						new FileInfoMatcherDescription("org.eclipse.ui.ide.multiFilter",
								"1.0-name-matches-false-false-doc"),								
						IResource.BACKGROUND_REFRESH, 
						new NullProgressMonitor());
			project.createFilter( 
					IResourceFilterDescription.EXCLUDE_ALL | 
					IResourceFilterDescription.INHERITABLE |
					IResourceFilterDescription.FOLDERS,
						new FileInfoMatcherDescription("org.eclipse.ui.ide.multiFilter",
								"1.0-name-matches-false-false-etc"),								
						IResource.BACKGROUND_REFRESH, 
						new NullProgressMonitor());
			
			project.setDefaultCharset("UTF-8", new NullProgressMonitor());
			// Since the change of the default charset is not applied directly, 
			// I applied the following workaround
			// TODO: Check how to get rid of this workaround
			project.close(null);
			project.open(null);
			// The refresh can be necessary to update the Xtext references
			project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override 
	public void addPages() {  
		super.addPages();  
		pageOne = new WizardNewProjectCreationPage(PAGE_NAME);  
		pageOne.setTitle("Architecture Management Project");  
		pageOne.setDescription("Create new Architecture Management project.");  
		addPage(pageOne);  
	}
	
    /**    
      * @param location  
      * @param projectName  
      */ 
	private static IProject createBaseProject(String projectName, URI location) {  
		// it is acceptable to use the ResourcesPlugin class  
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);  

		if (!newProject.exists()) {  
			URI projectLocation = location;  
			IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());  
			if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {  
				projectLocation = null;  
			}
			desc.setLocationURI(projectLocation);  
			
			try {  
				newProject.create(desc, null);  
				if (!newProject.isOpen()) {  
					newProject.open(null);  
				}  
			} catch (CoreException e) {  
				e.printStackTrace();  
			}  
		}  
		return newProject;  
	}

	private static void addXtextNature(IProject project) throws CoreException {  
		if (!project.hasNature(XtextProjectHelper.NATURE_ID)) {  
			IProjectDescription description = project.getDescription();  
			String[] prevNatures = description.getNatureIds();  
			String[] newNatures = new String[prevNatures.length + 1];  
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);  
			newNatures[prevNatures.length] = XtextProjectHelper.NATURE_ID;  
			description.setNatureIds(newNatures);      
			IProgressMonitor monitor = null;  
			project.setDescription(description, monitor);  
		}  
	}  

	
	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		configurationElement = config; 
	}  

}
