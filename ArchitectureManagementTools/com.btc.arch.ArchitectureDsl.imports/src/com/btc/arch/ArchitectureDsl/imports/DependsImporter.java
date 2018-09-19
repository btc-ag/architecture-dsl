package com.btc.arch.ArchitectureDsl.imports;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.util.ModelBuilder;
import com.btc.commons.java.IFactory;
import com.btc.commons.java.StringUtils;

/**
 * 
 * Platform restrictions: This will only work on Windows.
 * 
 */
public class DependsImporter implements Importer {

	public static class DependsImporterInternal {
		private final ModelBuilder modelBuilder;
		private final Logger logger;
		private final Iterable<File> sourceFiles;
		// TODO: Make independent of actual path separator
		private static final String PATH_SEPARATOR = "\\\\";

		public DependsImporterInternal(final ModelBuilder modelBuilder,
				final Iterable<File> sourceFiles) {
			this.modelBuilder = modelBuilder;
			this.sourceFiles = sourceFiles;
			this.logger = Logger.getLogger(this.getClass());
		}

		private void processDocument(final Document doc) {
			// TODO ensure that all source modules are created even if they do
			// not
			// have dependencies

			final Element mainNode = doc.getDocumentElement();
			final NodeList childNodes = mainNode.getChildNodes();

			for (int i = 0; i < childNodes.getLength(); i++) {
				final Node artifactNode = childNodes.item(i);
				if (artifactNode.getNodeName().equals("artifact")) {
					final String artifactName = artifactNode.getAttributes()
							.getNamedItem("path").getNodeValue();
					final String moduleName = extractModuleNameFromFileName(artifactName);
					this.modelBuilder.getOrCreateModule(moduleName,
							extractModuleDependenciesSometime(artifactNode),
							false);
				}
			}
		}

		/**
		 * This method removes the path information and the file extension from
		 * the filename. It assumes a three character file extension.
		 * 
		 * @param fullname
		 * @return
		 */
		private static String extractModuleNameFromFileName(
				final String fullname) {
			final String[] pathParts = fullname
					.split(DependsImporterInternal.PATH_SEPARATOR);
			final String filename = pathParts[pathParts.length - 1];
			// TODO what is 4???
			final String modulename = filename.substring(0,
					filename.length() - 4);
			return modulename;
		}

		private EList<Module> extractModuleDependencies(final Node artifactNode) {
			final EList<Module> moduleList = new BasicEList<Module>();
			final NodeList childNodes = artifactNode.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				final Node childNode = childNodes.item(i);
				if (childNode.getNodeName().equals("dependencies")) {
					final NodeList grandChildNodes = childNode.getChildNodes();
					for (int j = 0; j < grandChildNodes.getLength(); j++) {
						final Node grandChildNode = grandChildNodes.item(j);
						if (grandChildNode.getNodeName().equals("artifact")) {
							final String artifactName = grandChildNode
									.getAttributes().getNamedItem("path")
									.getNodeValue();
							final String moduleName = extractModuleNameFromFileName(artifactName);
							final Module module = this.modelBuilder
									.getOrCreateModule(
											moduleName,
											extractModuleDependenciesSometime(grandChildNode),
											false);
							moduleList.add(module);
						}

					}
				}
			}
			return moduleList;
		}

		private static List<String> createDependsCallArguments(
				final Iterable<File> sourceFiles) throws ArchDslImportException {
			final List<String> arguments = new LinkedList<String>();
			try {
				// TODO: previously only the relative path to the depends exe in
				// the plugin was given here, but this does not work anymore
				// (Did it ever work?). The following does work from within
				// Eclipse, but not from the exported product.
				URL url = FileLocator.resolve(FileLocator.find(
								Platform.getBundle("com.btc.arch.ArchitectureDsl.imports"),
								new Path("lib/BTC.CAB.Depends.EXE.exe"),
								Collections.EMPTY_MAP));
				arguments.add(url.getPath());
				arguments.add("-xml");
				arguments.add("-odt");
			} catch (IOException e) {
				throw new ArchDslImportException(e);
			}

			for (final File sourceFile : sourceFiles) {
				arguments.add(sourceFile.getAbsolutePath());
			}
			return arguments;
		}

		private void convertXMLToArchitectureDslModel(
				final List<String> arguments) throws ArchDslImportException {

			final DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();
			final InputSource is = new InputSource();
			try {
				ProcessBuilder builder = new ProcessBuilder(arguments);
				final Process process = builder.start();
				is.setByteStream(process.getInputStream());
				final DocumentBuilder db = dbf.newDocumentBuilder();
				final Document doc = db.parse(is);
				processDocument(doc);
			} catch (final ParserConfigurationException e) {
				this.logger.error("Error during import: " + e.getMessage(), e);
				throw new ArchDslImportException(e);
			} catch (final SAXException e) {
				this.logger.error("Error during import: " + e.getMessage(), e);
				throw new ArchDslImportException(e);
			} catch (final IOException e) {
				this.logger.error("Error during import: " + e.getMessage(), e);
				throw new ArchDslImportException(e);
			}
		}

		private IFactory<Collection<Module>> extractModuleDependenciesSometime(
				final Node artifactNode) {
			return new IFactory<Collection<Module>>() {

				@Override
				public Collection<Module> create() {
					return extractModuleDependencies(artifactNode);
				}

			};
		}

		public Model createModel() throws ArchDslImportException {
			this.logger.debug("Create Depends Call");
			final List<String> arguments = createDependsCallArguments(this.sourceFiles);
			this.logger.debug(StringUtils.join(arguments, " "));

			this.logger.debug("Convert Depends Result");
			convertXMLToArchitectureDslModel(arguments);

			final Model model = this.modelBuilder.toModel();
			return model;
		}
	}

	private static final String[] SUPPORTED_FILE_EXTENSIONS = { ".dll", ".exe" };

	/**
	 * This Importer creates module descriptions for all modules in the
	 * BTC.CAB.Depends.EXE.exe result for the given binaries. It will only
	 * produce module descriptions for the modules for which a binary exists.
	 * External dependencies of the binaries are not extracted by
	 * BTC.CAB.Depends.EXE.exe.
	 */
	public DependsImporter() {
	}

	/**
	 * This method runs BTC.CAB.Depends.EXE.exe on the given binary files and
	 * imports the results into an architecture dsl model. It is assumed that
	 * the filenames given in binaryFileNames end with .dll or .exe.
	 * 
	 * @param sourceFiles
	 * @return An architecture dsl model with the modules representing the given
	 *         fullyQualifiedSourceFileNames, the modules they use and all uses
	 *         dependencies.
	 */
	@Override
	public Model createModel(final ModelBuilder builder,
			final Iterable<File> sourceFiles) throws ArchDslImportException {
		final DependsImporterInternal data = new DependsImporterInternal(
				builder, sourceFiles);
		return data.createModel();
	}

	@Override
	public String[] getSupportedFileExtensions() {
		return SUPPORTED_FILE_EXTENSIONS;
	}

}
