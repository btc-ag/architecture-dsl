package com.btc.arch.ArchitectureDsl.imports;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.util.ModelBuilder;

public class ManifestImporter implements Importer {

	private static final String[] SUPPORTED_FILE_EXTENSIONS = { ".MF" };

	static class ManifestLiterals {
		private static final String MANIFEST_LIST_SEP = ",";
		private static final String MANIFEST_PARAMETER_SEP = ";";
		private static final Name BUNDLE_SYMBOLICNAME = new Name(
				"Bundle-SymbolicName");
		private static final Name REQUIRE_BUNDLE = new Name("Require-Bundle");
	}

	private final Logger logger;

	public ManifestImporter() {
		this.logger = Logger.getLogger(this.getClass());
	}

	@Override
	public Model createModel(ModelBuilder builder, Iterable<File> sourceFiles) {
		for (final File manifestFile : sourceFiles) {
			try {
				final Manifest manifest = new Manifest(new FileInputStream(
						manifestFile));
				// Get or create Module
				final String moduleName = ((String) manifest
						.getMainAttributes().get(
								ManifestLiterals.BUNDLE_SYMBOLICNAME))
						.split(ManifestLiterals.MANIFEST_PARAMETER_SEP)[0];

				// TODO: use moduleDependencyFactory here instead of the code
				// below?
				final Module currentModule = builder
						.getOrCreateModule(moduleName);

				// Add uses
				final String requiredBundlesString = (String) manifest
						.getMainAttributes().get(
								ManifestLiterals.REQUIRE_BUNDLE);
				if (requiredBundlesString != null) {
					final String[] requiredBundles = requiredBundlesString
							.split(ManifestLiterals.MANIFEST_LIST_SEP);
					for (String requiredBundleName : requiredBundles) {
						requiredBundleName = requiredBundleName
								.split(ManifestLiterals.MANIFEST_PARAMETER_SEP)[0];
						final Module usedModule = builder
								.getOrCreateModule(requiredBundleName);
						currentModule.getUsedModules().add(usedModule);
					}
				}
			} catch (IOException e) {
				this.logger.error(MessageFormat.format(
						"Manifest file {0} cannot be read", manifestFile), e);
			}
		}
		return builder.toModel();
	}

	@Override
	public String[] getSupportedFileExtensions() {
		return SUPPORTED_FILE_EXTENSIONS;
	}
}
