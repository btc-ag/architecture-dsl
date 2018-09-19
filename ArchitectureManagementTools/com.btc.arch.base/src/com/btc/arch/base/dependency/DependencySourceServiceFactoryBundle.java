package com.btc.arch.base.dependency;

import java.util.Collection;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.btc.commons.java.IFactory;

public class DependencySourceServiceFactoryBundle implements BundleActivator {
	private static final String DEPENDENCY_SOURCE_SERVICE_FACTORY_EXTENSION_POINT = "com.btc.arch.base.DependencySourceServiceFactory";
	private static DependencySourceServiceFactoryBundle instance = null;
	private Collection<DependencySourceServiceFactoryInfo> dependencySourceServiceFactoryInfos;

	@Override
	public void start(BundleContext context) throws Exception {
		DependencySourceServiceFactoryBundle.instance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		DependencySourceServiceFactoryBundle.instance = null;
	}

	public static DependencySourceServiceFactoryBundle getInstance() {
		return DependencySourceServiceFactoryBundle.instance;
	}

	public class DependencySourceServiceFactoryInfo {

		private final IFactory<IDependencySource> executableExtension;
		private final String programmingLanguage;
		private final String description;

		private DependencySourceServiceFactoryInfo(IConfigurationElement ice)
				throws CoreException {
			this((IFactory<IDependencySource>) ice
					.createExecutableExtension("class"), ice
					.getAttribute("programming_language"), ice
					.getAttribute("description"));
		}

		public DependencySourceServiceFactoryInfo(
				IFactory<IDependencySource> executableExtension,
				String programmingLanguage, String description) {
			this.executableExtension = executableExtension;
			this.programmingLanguage = programmingLanguage;
			this.description = description;
		}

		public String getProgrammingLanguage() {
			return programmingLanguage;
		}

		public IFactory<IDependencySource> getExecutableExtension() {
			return executableExtension;
		}

		public String getDescription() {
			return description;
		}
	}

	public Collection<DependencySourceServiceFactoryInfo> getDependencySourceServiceFactoryInfos() {
		if (this.dependencySourceServiceFactoryInfos == null) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry
					.getExtensionPoint(DEPENDENCY_SOURCE_SERVICE_FACTORY_EXTENSION_POINT);
			if (point != null) {
				IExtension[] extensions = point.getExtensions();
				this.dependencySourceServiceFactoryInfos = new Vector<DependencySourceServiceFactoryInfo>(
						extensions.length);
				for (int i = 0; i < extensions.length; i++) {
					IConfigurationElement[] configurationElements = extensions[i]
							.getConfigurationElements(); // get the information
															// about each
															// extension
					for (int j = 0; j < configurationElements.length; j++) {
						try {
							this.dependencySourceServiceFactoryInfos
									.add(new DependencySourceServiceFactoryInfo(
											configurationElements[j]));
						} catch (CoreException e) {
							// TODO log
							e.printStackTrace();
						} catch (ClassCastException e) {
							// TODO log
							e.printStackTrace();
						}
					}
				}
			} else {
				throw new RuntimeException("Extension point "
						+ DEPENDENCY_SOURCE_SERVICE_FACTORY_EXTENSION_POINT
						+ " does not exist");
			}
		}
		return this.dependencySourceServiceFactoryInfos;
	}

}
