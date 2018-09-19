package com.btc.arch.generator.commandline;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.btc.arch.ArchitectureDslStandaloneSetup;
import com.btc.arch.generator.ArchDslGeneratorException;
import com.btc.arch.generator.ArchDslGeneratorFacade;
import com.btc.arch.generator.GeneratorBundle;
import com.btc.arch.generator.GeneratorBundle.Generator;
import com.btc.arch.generator.IArchitectureDSLGenerator;
import com.btc.arch.generator.LoggerUtils;
import com.btc.commons.java.EndInserter;

public class ArchDslCommandLineGenerator implements IApplication {

	private File projectPath;
	private final List<File> sourceFiles = new ArrayList<File>();
	private final List<File> propertiesFiles = new ArrayList<File>();
	private final List<IArchitectureDSLGenerator> activeGenerators = new LinkedList<IArchitectureDSLGenerator>();

	private static final String ARCH_DSL_GENERATOR_NAME = "ArchDslGenerator"; //$NON-NLS-1$
	private static final String PROPERTIES_FILE_OPTION = "properties-file"; //$NON-NLS-1$
	private static final String SOURCE_FILE_OPTION = "source-file"; //$NON-NLS-1$
	private static final String PROJECT_PATH_OPTION = "project-path"; //$NON-NLS-1$
	private static final String HELP_OPTION = "h"; //$NON-NLS-1$

	private boolean help = false;

	private static final Logger logger = Logger
			.getLogger(ArchDslCommandLineGenerator.class);
	private static final String FILE_LIST_SEP = ",";

	@Override
	public Object start(final IApplicationContext context) throws Exception {
		// TODO do this by configuration or cmd line flag
		Logger.getRootLogger().setLevel(Level.DEBUG);
		// TODO ist das überhaupt notwendig, wenn das ganze per OSGi gestartet
		// wird?
		ArchitectureDslStandaloneSetup.doSetup();
		final List<String> errors = new ArrayList<String>();

		final Options options = defineCommandLineOptions();

		parseOptions(
				options,
				(String[]) context.getArguments().get(
						IApplicationContext.APPLICATION_ARGS));

		if (this.propertiesFiles.size() == 0
				|| this.sourceFiles.size() == 0 || this.projectPath == null
				|| this.help) {
			printUsage(options);

			return this.help ? IApplication.EXIT_OK : 1;
		}
		ArchDslGeneratorFacade data = null;

		try {
			data = new ArchDslGeneratorFacade(this.activeGenerators,
					this.projectPath, this.propertiesFiles, this.sourceFiles,
					new EndInserter<String>(errors));
		} catch (final Exception e) {
			final String message = MessageFormat
					.format(Messages.ArchDslCommandLineGenerator_UnexpectedExceptionDuringInitialization,
							e.getMessage());
			errors.add(message);
			LoggerUtils.logExtendedInfo(logger, Level.ERROR, message, e);
		}
		if (errors.isEmpty() && data != null
				&& data.getPrimaryContents() == null) {
			errors.add(Messages.ArchDslCommandLineGenerator_SourceFileResourceNull);
		}

		if (errors.size() > 0) {
			for (final String error : errors) {
				System.err.println(error);
			}
			System.err
					.println(Messages.ArchDslCommandLineGenerator_ErrorsBeforeGeneration);
			return 1;
		}

		try {
			@SuppressWarnings("null")
			// cannot be null because in this case an error would have led to
			// system exit
			final Collection<String> messages = data.generate();
			if (messages.isEmpty()) {
				System.err
						.println(Messages.ArchDslCommandLineGenerator_NoGeneratorSuccessful);
				return 1;
			} else {
				for (final String message : messages) {
					System.out.println(message);
				}
			}
		} catch (final ArchDslGeneratorException e) {
			final String message = MessageFormat.format(
					Messages.ArchDslCommandLineGenerator_GeneratorFailure,
					e.getMessage());
			LoggerUtils.logExtendedInfo(logger, Level.ERROR, message, e);
			System.err.println(message);
			return 1;
		}
		// System.in.read();
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
	}

	private void parseOptions(final Options options, final String[] args) {
		// create the parser
		final CommandLineParser parser = new GnuParser();
		try {
			// parse the command line arguments
			final CommandLine line = parser.parse(options, args);
			String propertiesFileNames = null;
			String sourceFileNames = null;
			if (line.hasOption(PROPERTIES_FILE_OPTION)) {
				propertiesFileNames = line
						.getOptionValue(PROPERTIES_FILE_OPTION);
			}
			if (line.hasOption(SOURCE_FILE_OPTION)) {
				sourceFileNames = line.getOptionValue(SOURCE_FILE_OPTION);
			}
			if (line.hasOption(PROJECT_PATH_OPTION)) {
				this.projectPath = new File(
						line.getOptionValue(PROJECT_PATH_OPTION));
			}
			if (line.hasOption(HELP_OPTION)) {
				this.help = true;
			}

			for (final Generator g : getGenerators()) {
				if (line.hasOption(g.getSymbolicName())) {
					this.activeGenerators.add(g.getExecutableExtension());
				}
			}
			if (propertiesFileNames != null) {
				for (final String propertiesFileName : propertiesFileNames
						.split(FILE_LIST_SEP)) {
					this.propertiesFiles.add(new File(this.projectPath,
							propertiesFileName));
				}
			}
			if (sourceFileNames != null) {
				for (final String sourceFileName : sourceFileNames
						.split(FILE_LIST_SEP)) {
					this.sourceFiles.add(new File(this.projectPath,
							sourceFileName));
				}
			}
		} catch (final ParseException exp) {
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		}
	}

	@SuppressWarnings("static-access")
	private Options defineCommandLineOptions() {
		final Options options = new Options();

		// Add common options
		options.addOption(OptionBuilder
				.withLongOpt(PROPERTIES_FILE_OPTION)
				.hasArg()
				.withArgName(PROPERTIES_FILE_OPTION)
				.withDescription(
						Messages.ArchDslCommandLineGenerator_OptionGeneratorProperties)
				.isRequired().create('P'));
		options.addOption(OptionBuilder
				.hasArg()
				.withArgName(SOURCE_FILE_OPTION)
				.withDescription(
						Messages.ArchDslCommandLineGenerator_OptionSourceFile)
				.isRequired().withLongOpt(SOURCE_FILE_OPTION).create());
		options.addOption(OptionBuilder
				.hasArg()
				.withArgName(PROJECT_PATH_OPTION)
				.withDescription(
						Messages.ArchDslCommandLineGenerator_OptionProjectPath)
				.withLongOpt(PROJECT_PATH_OPTION).isRequired().create());
		options.addOption(new Option(HELP_OPTION, "help", false, //$NON-NLS-1$
				Messages.ArchDslCommandLineGenerator_OptionHelp));

		// Add internal generator options
		final OptionGroup og = new OptionGroup();

		// Add external generator options
		for (final Generator g : getGenerators()) {
			og.addOption(OptionBuilder.withLongOpt(g.getSymbolicName())
					.withDescription(g.getDescription()).create());
		}
		og.setRequired(true);

		options.addOptionGroup(og);

		return options;
	}

	private Collection<Generator> getGenerators() {
		return GeneratorBundle.getInstance().getGenerators();
	}

	private void printUsage(final Options options) {
		// automatically generate the help statement
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(ARCH_DSL_GENERATOR_NAME, options);
	}
}
