package com.btc.arch.ArchitectureDsl.imports;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.btc.arch.visualstudio.VSDependencyParserFactory;
import com.btc.arch.visualstudio.VcxprojDependencyParserFactory;
import com.btc.commons.java.FileUtils;
import com.btc.commons.java.StringUtils;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IUnaryPredicate;
import com.btc.commons.java.functional.IterationUtils;

public class ImporterServiceFactory {
	private final Map<String, Importer> registeredImporters;
	private final Logger logger;

	public ImporterServiceFactory() {
		this.logger = Logger.getLogger(this.getClass());
		this.registeredImporters = new HashMap<String, Importer>();
		initializeImporters();
	}

	private boolean checkImporter(final Importer importer,
			final Iterable<String> extensions) {
		return IterationUtils.all(extensions, new IUnaryPredicate<String>() {

			@Override
			public boolean evaluate(String extension) {
				return Arrays.asList(importer.getSupportedFileExtensions())
						.contains(extension);
			}
		});
	}

	public Importer createImporterByName(final String name) {
		return registeredImporters.get(name);
	}

	public Importer createImporterForExtensions(
			final Iterable<String> extensions) {
		final String extensionNames = StringUtils.join(extensions, ",");
		this.logger.debug(MessageFormat.format(
				"Searching importer for extensions {0}", extensionNames));
		for (Importer importer : registeredImporters.values()) {
			if (checkImporter(importer, extensions)) {
				this.logger.debug(MessageFormat.format("Selected importer {0}",
						importer));
				return importer;
			}
		}
		this.logger.error(MessageFormat.format(
				"No importer found that supports ALL extensions {0}",
				extensionNames));
		return null;
	}

	public Importer createImporterForFileList(final Iterable<File> inputFiles) {
		final Set<String> extensions = IterationUtils.materialize(
				IterationUtils.map(inputFiles, new IMapFunctor<File, String>() {

					@Override
					public String mapItem(File obj) {
						return FileUtils.getFileExtension(obj);
					}
				}), new HashSet<String>());
		return createImporterForExtensions(extensions);
	}

	public Iterable<String> getRegisteredImporterNames() {
		return registeredImporters.keySet();
	}

	private void initializeImporters() {
		// TODO register importers via extension point
		registeredImporters.put("depends", new DependsImporter());
		registeredImporters.put("jar-manifest", new ManifestImporter());
		registeredImporters.put("csproj", new FileDependencyParserImporter(
				new VSDependencyParserFactory()));
		registeredImporters.put("vcxproj", new FileDependencyParserImporter(
				new VcxprojDependencyParserFactory()));
	}
}
