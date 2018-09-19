package com.btc.arch.ArchitectureDsl.imports.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.service.application.ApplicationException;

import com.btc.arch.ArchitectureDsl.imports.ArchDslImportException;
import com.btc.arch.ArchitectureDsl.imports.CABModuleCreator;
import com.btc.arch.ArchitectureDsl.imports.DependsImporter;
import com.btc.arch.ArchitectureDsl.imports.FileDependencyParserImporter;
import com.btc.arch.ArchitectureDsl.imports.Importer;
import com.btc.arch.ArchitectureDsl.imports.JavaModuleCreator;
import com.btc.arch.ArchitectureDsl.imports.ManifestImporter;
import com.btc.arch.architectureDsl.Language;
import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.util.ModelBuilder;
import com.btc.arch.visualstudio.VSDependencyParserFactory;
import com.btc.arch.visualstudio.VcxprojDependencyParserFactory;
import com.btc.commons.java.FileUtils;

public class ArchitectureDslImporter implements IApplication {

	private static final String TARGET_DIRECTORY_OPTION = "targetDirectory";
	private static final String SOURCEPATH_OPTION = "sourcePath";
	private static final String OUTPUT_FILE_NUMBER_OPTION = "outputFileNumber";
	private static final String GROUP_PREFIXES_OPTION = "groupPrefixes";
	private static final String INVERSE_GROUP_PREFIXES_OPTION = "inverseGroupPrefixes";
	private static final String HELP_OPTION = "h";
	private static final String DEPENDS_OPTION = "d";
	private static final String MANIFEST_OPTION = "m";
	private static final String CSPROJ_OPTION = "c";
	private static final String VCXPROJ_OPTION = "v";

	private static IPath targetDirectory = null;
	private static File sourcePath = null;
	private static String outputFileNumber = null;
	private static String[] groupPrefixes = null;
	private static String[] inverseGroupPrefixes = null;

	private Importer importer;
	private ModelBuilder builder;

	/**
	 * @param args
	 * @throws CoreException
	 * @throws ApplicationException
	 */
	public void main(String[] args) throws CoreException, ApplicationException {
		Options options = defineCommandLineOptions();

		parseOptions(options, args);

		final Collection<File> sourceFiles = new ArrayList<File>();
		FileUtils.collectFilesOnFileTree(sourcePath,
				importer.getSupportedFileExtensions(), sourceFiles);
		if (sourceFiles.size() == 0) {
			System.out
					.print("No source files with extensions of the chosen importer (");
			for (String extension : importer.getSupportedFileExtensions()) {
				System.out.print(extension + " ");
			}
			System.out.println(") were found. No output files were generated. "
					+ "Please check the source path and the chosen importer.");
			System.exit(0);
		}

		Model importedModel = null;
		try {
			importedModel = importer.createModel(builder, sourceFiles);
		} catch (ArchDslImportException e) {
			System.out.println("An error occurred during import. Message: "
					+ e.getMessage());
			System.exit(1);
		}
		if (importedModel.getModules().size() == 0) {
			System.out
					.println("The imported model does not contain any modules. No output files were generated. "
							+ "Please check the source path and the chosen importer.");
			System.exit(0);
		}

		ArchitectureDslModelGenerator generator = new ArchitectureDslModelGenerator();

		Collection<String> moduleNames = new ArrayList<String>();
		for (Module module : importedModel.getModules()) {
			moduleNames.add(module.getName());
		}

		if (groupPrefixes != null) {
			for (String prefix : groupPrefixes) {
				Collection<String> prefixedModuleNames = getPrefixedModuleNames(
						moduleNames, prefix);
				if (prefixedModuleNames.size() > 0) {
					generator.generateTextualModelRepresentation(importedModel,
							prefixedModuleNames, targetDirectory);
					moduleNames.removeAll(prefixedModuleNames);
				}
			}
		}

		if (inverseGroupPrefixes != null) {
			for (String prefix : inverseGroupPrefixes) {
				Collection<String> notPrefixedModuleNames = new ArrayList<String>();
				notPrefixedModuleNames.addAll(moduleNames);
				notPrefixedModuleNames.removeAll(getPrefixedModuleNames(
						moduleNames, prefix));
				if (notPrefixedModuleNames.size() > 0) {
					generator.generateTextualModelRepresentation(importedModel,
							notPrefixedModuleNames, targetDirectory);
					moduleNames.removeAll(notPrefixedModuleNames);
				}
			}
		}

		generateModuleDescriptions(generator, importedModel, moduleNames);

		System.out.println("Import finished. Output written to "
				+ targetDirectory);
	}

	private Collection<String> getPrefixedModuleNames(
			Collection<String> moduleNames, String prefix) {
		Collection<String> prefixedModulesNames = new ArrayList<String>();
		for (String moduleName : moduleNames) {
			if (moduleName.startsWith(prefix))
				prefixedModulesNames.add(moduleName);
		}
		return prefixedModulesNames;
	}

	private void generateModuleDescriptions(
			ArchitectureDslModelGenerator generator, Model importedModel,
			Collection<String> moduleNames) {
		if (outputFileNumber.equals("multiple"))
			generator.generateTextualModelRepresentation(importedModel,
					moduleNames, targetDirectory);
		else if (outputFileNumber.equals("single"))
			generator.generateTextualModelRepresentationForEachModule(
					importedModel, moduleNames, targetDirectory);
		else
			System.out
					.println("Nothing was generated, since no valid outputFileNumber was given. Please "
							+ "choose \"single\" (one file for each module) or \"multiple\" (one file for all modules"
							+ "in the source path)");
	}

	private void parseOptions(Options options, String[] args) {
		// create the parser
		CommandLineParser parser = new GnuParser();

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);
			if (line.hasOption(TARGET_DIRECTORY_OPTION))
				targetDirectory = Path.fromOSString(line
						.getOptionValue(TARGET_DIRECTORY_OPTION));
			if (line.hasOption(SOURCEPATH_OPTION))
				sourcePath = new File(line.getOptionValue(SOURCEPATH_OPTION));
			if (line.hasOption(OUTPUT_FILE_NUMBER_OPTION))
				outputFileNumber = line
						.getOptionValue(OUTPUT_FILE_NUMBER_OPTION);
			if (line.hasOption(GROUP_PREFIXES_OPTION))
				groupPrefixes = line.getOptionValues(GROUP_PREFIXES_OPTION);
			if (line.hasOption(INVERSE_GROUP_PREFIXES_OPTION))
				inverseGroupPrefixes = line
						.getOptionValues(INVERSE_GROUP_PREFIXES_OPTION);
			if (line.hasOption(DEPENDS_OPTION)) {
				this.builder = new ModelBuilder(new CABModuleCreator(
						Language.CPP));
				this.importer = new DependsImporter();
			}
			if (line.hasOption(MANIFEST_OPTION)) {
				this.builder = new ModelBuilder(new JavaModuleCreator());
				this.importer = new ManifestImporter();
			}
			if (line.hasOption(CSPROJ_OPTION)) {
				final VSDependencyParserFactory parserFactory = new VSDependencyParserFactory();
				this.builder = new ModelBuilder(new CABModuleCreator(
						Language.CSHARP));

				this.importer = new FileDependencyParserImporter(parserFactory);
			}
			if (line.hasOption(VCXPROJ_OPTION)) {
				final VcxprojDependencyParserFactory parserFactory = new VcxprojDependencyParserFactory();
				this.builder = new ModelBuilder(new CABModuleCreator(
						Language.CPP));

				this.importer = new FileDependencyParserImporter(parserFactory);
			}
			if (line.hasOption(HELP_OPTION)) {
				printUsage(options);
				System.exit(0);
			}
		} catch (ParseException exp) {
			System.err.println("Command line parsing failed. Reason: "
					+ exp.getMessage());
			printUsage(options);
			System.exit(1);
		}
	}

	private static Options defineCommandLineOptions() {
		Options options = new Options();

		Option targetDirectoryOption = OptionBuilder
				.withArgName(TARGET_DIRECTORY_OPTION)
				.hasArg()
				.withDescription(
						"The base directory for the generation of the architecture descriptions (e.g. the main CAB directory).")
				.create("targetDirectory");
		Option sourcePathOption = OptionBuilder
				.withArgName(SOURCEPATH_OPTION)
				.hasArg()
				.withDescription(
						"Path from which the module descriptions shall be imported.")
				.create("sourcePath");
		Option outputFileNumber = OptionBuilder
				.withArgName(OUTPUT_FILE_NUMBER_OPTION)
				.hasArg()
				.withDescription(
						"Valid values are \"single\" (one file for each module) and \"multiple\" (one file for all modules that are chosen as binaries)")
				.create("outputFileNumber");
		Option groupPrefixesOption = OptionBuilder
				.withArgName(GROUP_PREFIXES_OPTION)
				.hasArgs()
				.withDescription(
						"For each prefix a file containing all modules which names start with the prefix is created.")
				.create("groupPrefixes");
		Option inverseGroupPrefixesOption = OptionBuilder
				.withArgName(INVERSE_GROUP_PREFIXES_OPTION)
				.hasArgs()
				.withDescription(
						"Creates a file containing all modules which names do not start with one of the prefixes.")
				.create("inverseGroupPrefixes");
		Option helpOption = new Option(HELP_OPTION, "help", false,
				"print this message");
		Option dependsOption = new Option(DEPENDS_OPTION, "depends", false,
				"use depends importer");
		Option manifestOption = new Option(MANIFEST_OPTION, "manifest", false,
				"use manifest importer");
		Option csprojOption = new Option(CSPROJ_OPTION, "csproj", false,
				"use .CSPROJ importer");
		Option vcxprojOption = new Option(VCXPROJ_OPTION, "vcxproj", false,
		"use .VCXPROJ importer");

		targetDirectoryOption.setRequired(true);
		sourcePathOption.setRequired(true);
		outputFileNumber.setRequired(true);

		options.addOption(targetDirectoryOption);
		options.addOption(sourcePathOption);
		options.addOption(groupPrefixesOption);
		options.addOption(inverseGroupPrefixesOption);
		options.addOption(outputFileNumber);
		options.addOption(helpOption);

		final OptionGroup importerGroup = new OptionGroup();
		importerGroup.addOption(dependsOption);
		importerGroup.addOption(manifestOption);
		importerGroup.addOption(csprojOption);
		importerGroup.addOption(vcxprojOption);
		importerGroup.setRequired(true);
		options.addOptionGroup(importerGroup);

		return options;
	}

	private static void printUsage(Options options) {
		// automatically generate the help statement
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar ArchitectureDslImporter.jar", options);
	}

	@Override
	public Object start(IApplicationContext context) throws Exception {
		context.applicationRunning();
		main((String[]) context.getArguments().get("application.args"));
		return EXIT_OK;
	}

	@Override
	public void stop() {
	}
}
