package com.btc.arch.generator;

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

public class GeneratorBundle implements BundleActivator {
	private static final String GENERATOR_EXTENSION_POINT = "com.btc.arch.ArchitectureDsl.generator.archdsl_generator";
	private static GeneratorBundle instance = null;
	private Collection<Generator> generators;

	public void start(BundleContext context) throws Exception {
		GeneratorBundle.instance = this;
	}

	public void stop(BundleContext context) throws Exception {
		GeneratorBundle.instance = null;
	}

	public static GeneratorBundle getInstance() {
		return GeneratorBundle.instance;
	}

	public class Generator {

		private final String symbolicName;
		private final IArchitectureDSLGenerator executableExtension;
		private final String description;
		private final String parameter;

		private Generator(IConfigurationElement ice) throws CoreException {
			this(ice.getAttribute("symbolic_name"), (IArchitectureDSLGenerator) ice
					.createExecutableExtension("class"), ice
					.getAttribute("description"), ice.getAttribute("parameter"));
		}

		public Generator(String symbolicName,
				IArchitectureDSLGenerator executableExtension,
				String description, String parameter) {
			this.symbolicName = symbolicName;
			this.executableExtension = executableExtension;
			this.description = description;
			this.parameter = parameter;
		}

		public String getSymbolicName() {
			return symbolicName;
		}

		public IArchitectureDSLGenerator getExecutableExtension() {
			return executableExtension;
		}

		public String getDescription() {
			return description;
		}

		public String getParameter() {
			return parameter;
		}

	}

	public Collection<Generator> getGenerators() {
		if (this.generators == null) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry
					.getExtensionPoint(GENERATOR_EXTENSION_POINT);
			if (point != null) {
				IExtension[] extensions = point.getExtensions();
				this.generators = new Vector<Generator>(extensions.length);
				for (int i = 0; i < extensions.length; i++) {
					IConfigurationElement[] configurationElements = extensions[i]
							.getConfigurationElements(); // get the information
															// about each
															// extension
					for (int j = 0; j < configurationElements.length; j++) {
						try {
							this.generators.add(new Generator(
									configurationElements[j]));
						} catch (CoreException e) {
							// TODO log
							e.printStackTrace();
						}
						catch (ClassCastException e) {
							// TODO log
							e.printStackTrace();
						}
					}
				}
			} else {
				throw new RuntimeException("Extension point "+ GENERATOR_EXTENSION_POINT + " does not exist");
			}
		}
		return this.generators;
	}

}
