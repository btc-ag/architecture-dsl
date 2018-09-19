package com.btc.arch.architecturedsl.report;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.diagnostics.UnresolvedReferenceDiagnostic;
import com.btc.arch.architectureDsl.diagnostics.service.DefaultDiagnosticsProcessorServiceFactory;
import com.btc.arch.architectureDsl.util.ArchitectureDslNameMapper;
import com.btc.arch.architectureDsl.util.ModelQueries;
import com.btc.arch.base.ConfigurationError;
import com.btc.arch.base.IContext;
import com.btc.arch.diagnostics.api.Dependency;
import com.btc.arch.diagnostics.api.DiagnosticsException;
import com.btc.arch.diagnostics.api.service.IDiagnosticsProcessorServiceFactory;
import com.btc.arch.generator.ArchDslGeneratorException;
import com.btc.arch.generator.IArchitectureDSLGenerator;
import com.btc.arch.validation.ModuleDependencyDiagnosticsUtils;
import com.btc.arch.xtext.util.XtextResourceUtil;
import com.btc.commons.emf.diagnostics.DiagnosticsBundle;
import com.btc.commons.emf.diagnostics.DiagnosticsUtils;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.emf.diagnostics.IDiagnosticsRegistry;
import com.btc.commons.emf.diagnostics.ecore.DiagnosticsProcessor;
import com.btc.commons.java.Pair;
import com.btc.commons.java.Triple;
import com.btc.commons.java.functional.IterationUtils;

class DiagnosticsProcessorServiceFactory implements
		IDiagnosticsProcessorServiceFactory {
	private final boolean modelValid;

	public DiagnosticsProcessorServiceFactory(final boolean modelValid) {
		this.modelValid = modelValid;
	}

	@Override
	public DiagnosticsProcessor createDiagnosticsProcessor(
			final Collection<? extends EObject> primaryContents,
			final Collection<? extends EObject> allContents,
			final IContext contextParameters,
			final IDiagnosticsRegistry diagnosticsRegistry)
			throws DiagnosticsException, ConfigurationError {
		final DiagnosticsProcessor processor;
		final IDiagnosticsProcessorServiceFactory diagnosticsProcessorFactory = new DefaultDiagnosticsProcessorServiceFactory();
		processor = diagnosticsProcessorFactory.createDiagnosticsProcessor(
				primaryContents, allContents, contextParameters,
				diagnosticsRegistry);
		if (!this.modelValid)
			processor
					.addDiagnosticsResultSource(new UnresolvedReferenceDiagnostic(
							diagnosticsRegistry));
		return processor;
	}

}

/**
 * @author NISTREEK
 * 
 */
public class ValidationReportGenerator implements IArchitectureDSLGenerator {
	private static final String DEPENDENCY_DIAGNOSTICS_FILENAME = "Dependency_Diagnostics.txt";

	private static final String DEPENDENCY_DIAGNOSTIC_FILE_SEPARATOR = ";";
	private static final String HIDDEN_DEPENDENCIES_SEPARATOR = ";";
	private static final String HIDDEN_DEPENDENCIES_LINE_SEPARATOR = ",";

	private static final String HIDDEN_ILLEGAL_DEPENDENCIES_PARAMETER = "Report.HiddenIllegalDependencies";
	private static final String REFERENCE_DATE_PARAMETER = "Report.ReferenceDate";
	private static final String NEW_DEPENDENCIES_OFFSET_PARAMETER = "Report.NewDependenciesOffset";
	private static final String SHOW_HIDDEN_RULE_VIOLATIONS_IN_HISTORY_PARAMETER = "Report.ShowHiddenRuleViolationsInHistory";
	private static final String FAIL_ON_NEW_ILLEGAL_DEPENDENCIES = "Report.FailOnNewIllegalDependencies";

	private static final String SUBJECT_TYPE_MODULE_MODULE = "Module,Module";
	private static final String SUBJECT_TYPE_MODEL = "Model";
	private static final String SUBJECT_TYPE_DOMAIN = "Domain";
	private static final String SUBJECT_TYPE_MODULEGROUP = "ModuleGroup";
	private static final String SUBJECT_TYPE_MODULE = "Module";

	private static final String ILLEGAL_DEPENDENCY_MESSAGE = "There were the following new illegal dependencies: \n";

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy/MM/dd");

	private static final Logger logger = Logger
			.getLogger(ValidationReportGenerator.class);

	public String outputMessage = "";

	@Override
	public void generate(final Collection<? extends EObject> primaryContents,
			final Collection<? extends EObject> allContents,
			final IPath targetDir, final String parameter,
			final IContext contextParameters, final boolean modelValid)
			throws ArchDslGeneratorException {
		// TODO validate partial model if invalid (let each diagnostics source
		// indicate if they support processing partially valid models)
		final IDiagnosticsRegistry diagnosticsRegistry = DiagnosticsBundle
				.getInstance().getDiagnosticsRegistry();
		final DiagnosticsProcessor processor;
		try {
			processor = new DiagnosticsProcessorServiceFactory(modelValid)
					.createDiagnosticsProcessor(primaryContents, allContents,
							contextParameters, diagnosticsRegistry);
		} catch (final DiagnosticsException e) {
			throw new ArchDslGeneratorException(e);
		} catch (final ConfigurationError e) {
			throw new ArchDslGeneratorException(e);
		}

		final Collection<IDiagnosticResultBase> allDiagnostics = IterationUtils
				.materialize(processor.diagnose(primaryContents, allContents));
		final Iterable<Dependency> allDependencies = ModuleDependencyDiagnosticsUtils
				.getAllDependencies(primaryContents, allDiagnostics);

		// Representations of specific diagnostic groups
		final Collection<IDiagnosticResultBase> allDependencyDiagnostics = IterationUtils
				.materialize(DiagnosticsUtils.filterByType(allDiagnostics,
						SUBJECT_TYPE_MODULE_MODULE));
		final Collection<Pair<String, String>> allModuleInfos = getDiagnosticInfosByType(
				allDiagnostics, SUBJECT_TYPE_MODULE);
		final Collection<Pair<String, String>> allModuleGroupInfos = getDiagnosticInfosByType(
				allDiagnostics, SUBJECT_TYPE_MODULEGROUP);
		final Collection<Pair<String, String>> allDomainInfos = getDiagnosticInfosByType(
				allDiagnostics, SUBJECT_TYPE_DOMAIN);
		final Collection<Pair<String, String>> allModelInfos = getDiagnosticInfosByType(
				allDiagnostics, SUBJECT_TYPE_MODEL);

		// Log result overview
		logger.info(MessageFormat
				.format("{3} total diagnostic results: {0} dependency, {1} module, {2} model, {4} domain, {5} module group",
						allDependencyDiagnostics.size(), allModuleInfos.size(),
						allModelInfos.size(), allDiagnostics.size(),
						allDomainInfos.size(), allModuleGroupInfos.size()));
		final int missingDiagnosticsCount = allDiagnostics.size()
				- (allModuleInfos.size() + allModelInfos.size()
						+ allDependencyDiagnostics.size()
						+ allDomainInfos.size() + allModuleGroupInfos.size());
		if (missingDiagnosticsCount > 0) {
			logger.warn(MessageFormat.format(
					"{0} diagnostic results skipped in report",
					missingDiagnosticsCount));
		}

		// Things to be done to enable the highlighting of current dependencies
		// in the report
		// TODO: Catch the exceptions of setDateOfDependencyDiagnostics,
		// writeDependencyDiagnosticsFile and getReferenceDate and add
		// a message to the report that the according features do not work
		// correctly?
		setDateOfDependencyDiagnostics(targetDir, allDependencyDiagnostics,
				allContents);
		writeDependencyDiagnosticsFile(targetDir, allDependencyDiagnostics);
		Date referenceDate = getReferenceDate(contextParameters);

		// Get unresolved references
		final Iterable<Triple<String, String, String>> unresolvedReferencesStrings = createUnresolvedReferencesStringRepresentation(allContents);

		boolean showHiddenRuleViolationsInHistory = true;
		if (contextParameters
				.getParameter(SHOW_HIDDEN_RULE_VIOLATIONS_IN_HISTORY_PARAMETER) != null)
			showHiddenRuleViolationsInHistory = Boolean
					.parseBoolean(contextParameters
							.getParameter(SHOW_HIDDEN_RULE_VIOLATIONS_IN_HISTORY_PARAMETER));

		// Remove hidden dependencies. The method changes
		// allDependencyDiagnostics! This should be considered in the
		// following steps.
		int numberOfHiddenViolations = removeHiddenDependencyDiagnostics(
				contextParameters, allDependencyDiagnostics, allContents);

		// Dependency history
		Collection<DependencyHistoryEntry> dependencyHistoryEntries = DependencyHistoryHandler
				.getDependencyHistory(targetDir, allDependencies,
						contextParameters, numberOfHiddenViolations);

		// Generate the report using the different BIRT generators
		for (final AbstractBirtGenerator generator : getGenerators()) {
			generator.birtGenerate(targetDir, allDependencies,
					allDependencyDiagnostics, allModuleInfos, allModelInfos,
					dependencyHistoryEntries, referenceDate,
					numberOfHiddenViolations, unresolvedReferencesStrings,
					allModuleGroupInfos, allDomainInfos,
					showHiddenRuleViolationsInHistory);
		}
		this.outputMessage = MessageFormat.format(
				"Generated validation report to {0}", targetDir.toOSString());

		// Throw an exception when new illegal dependencies occurred if the
		// respective parameter is set to true.
		// TODO: This is no optimal solution since the occurrence of new
		// dependencies is not an exceptional situation.
		failOnNewIllegalDependencies(contextParameters,
				allDependencyDiagnostics);
	}

	/**
	 * Throws an exception when new illegal dependencies occurred if the
	 * respective parameter is not null and set to true.
	 * 
	 * Throws an ArchDslGeneratorException if the there are illegal dependencies
	 * with a date newer than the date given in the configuration or if the date
	 * in the configuration can not be parsed.
	 * 
	 * @param contextParameters
	 * @param dependencyDiagnostics
	 *            a list of dependency diagnostics
	 * @throws ArchDslGeneratorException
	 */
	private void failOnNewIllegalDependencies(IContext contextParameters,
			Iterable<IDiagnosticResultBase> dependencyDiagnostics)
			throws ArchDslGeneratorException {
		String failOnNewIllegalDependenciesParameter = contextParameters
				.getParameter(FAIL_ON_NEW_ILLEGAL_DEPENDENCIES);
		if (failOnNewIllegalDependenciesParameter != null) {
			Collection<IDiagnosticResultBase> newDependencyResults;
			try {
				newDependencyResults = IterationUtils
						.materialize(DiagnosticsUtils.filterByDateAfterOrEqual(
								dependencyDiagnostics,
								DATE_FORMAT
										.parse(failOnNewIllegalDependenciesParameter)));
			} catch (ParseException e) {
				throw new ArchDslGeneratorException(e);
			}
			if (newDependencyResults.size() > 0){
				String dependencyList = "";
				for (IDiagnosticResultBase result : newDependencyResults) {
					dependencyList += DATE_FORMAT.format(result.getDate())+";"+result.getDiagnostic().getID()+";";
					if (result.getSubjectType().equals(SUBJECT_TYPE_MODULE_MODULE))
							dependencyList += result.getSubjectType()+";"+((Pair<Module, Module>)result.getSubject()).getFirst().getName()+";"+((Pair<Module, Module>)result.getSubject()).getSecond().getName();
					dependencyList += "\n";
				}
				throw new ArchDslGeneratorException(ILLEGAL_DEPENDENCY_MESSAGE+dependencyList);
			}
		}
	}

	/**
	 * Returns a representation as a pair of strings for all diagnostics for the
	 * given subject type
	 * 
	 * @param allDiagnostics
	 * @param subjectType
	 * @return
	 */
	private Collection<Pair<String, String>> getDiagnosticInfosByType(
			Collection<IDiagnosticResultBase> allDiagnostics, String subjectType) {
		return IterationUtils.materialize(ModuleDependencyDiagnosticsUtils
				.mapToString(DiagnosticsUtils.filterByType(allDiagnostics,
						subjectType)));
	}

	/**
	 * Creates an iterable containing string tuple representations of all
	 * unresolved references in the given contents.
	 * 
	 * @param allContents
	 * @return
	 */
	private Iterable<Triple<String, String, String>> createUnresolvedReferencesStringRepresentation(
			Collection<? extends EObject> allContents) {
		final Collection<org.eclipse.xtext.util.Triple<EObject, EStructuralFeature, String>> unresolvedReferences = XtextResourceUtil
				.checkForUnresolvedReferences(allContents);
		final Collection<Triple<String, String, String>> unresolvedReferencesStrings = new ArrayList<Triple<String, String, String>>();
		for (org.eclipse.xtext.util.Triple<EObject, EStructuralFeature, String> triple : unresolvedReferences) {
			Triple<String, String, String> stringTriple = new Triple<String, String, String>(
					ArchitectureDslNameMapper.getName(triple.getFirst()),
					triple.getSecond().getName(), triple.getThird());
			unresolvedReferencesStrings.add(stringTriple);
		}
		return unresolvedReferencesStrings;
	}

	/**
	 * 
	 * @param allDependencyDiagnostics
	 * @return The number of removed diagnostics
	 * @throws ArchDslGeneratorException
	 */
	private int removeHiddenDependencyDiagnostics(IContext contextParameters,
			Collection<IDiagnosticResultBase> allDependencyDiagnostics,
			final Collection<? extends EObject> modelContents)
			throws ArchDslGeneratorException {
		Collection<IDiagnosticResultBase> hiddenDependencyDiagnostics = new ArrayList<IDiagnosticResultBase>();
		String hiddenIllegalDependencies = contextParameters
				.getParameter(HIDDEN_ILLEGAL_DEPENDENCIES_PARAMETER);
		if (hiddenIllegalDependencies != null) {
			String[] hiddenDependencyLines = hiddenIllegalDependencies
					.split(HIDDEN_DEPENDENCIES_LINE_SEPARATOR);
			for (String hiddenDependencyLine : hiddenDependencyLines) {
				String[] lineElements = hiddenDependencyLine
						.split(HIDDEN_DEPENDENCIES_SEPARATOR);
				try {
					if (DATE_FORMAT.parse(lineElements[0]).after(new Date())) {
						Iterable<Pair<Module, Module>> modulePairs = findModulePairs(
								modelContents, lineElements[2], lineElements[3]);
						for (Pair<Module, Module> modulePair : modulePairs) {
							Collection<IDiagnosticResultBase> diagnosticResults = getDiagnosticResults(
									allDependencyDiagnostics, lineElements[1],
									SUBJECT_TYPE_MODULE_MODULE, modulePair);
							hiddenDependencyDiagnostics
									.addAll(diagnosticResults);
						}
					} else {
						logger.info("The following dependency is not hidden anymore since it reached the expiration date: "
								+ hiddenDependencyLine);
					}
				} catch (ParseException e) {
					throw new ArchDslGeneratorException(e);
				}
				allDependencyDiagnostics.removeAll(hiddenDependencyDiagnostics);
			}
			return hiddenDependencyDiagnostics.size();
		}
		return 0;
	}

	/**
	 * Returns the referenceDate from the report configuration or the current
	 * date if no date has been configured. The configuration parameter
	 * REFERENCE_DATE_PARAMETER is preferred to
	 * NEW_DEPENDENCIES_OFFSET_PARAMETER in order to determine the reference
	 * date.
	 * 
	 * @param contextParameters
	 * @return
	 * @throws ArchDslGeneratorException
	 */
	private Date getReferenceDate(IContext contextParameters)
			throws ArchDslGeneratorException {
		Date referenceDate = null;
		if (contextParameters.getParameter(REFERENCE_DATE_PARAMETER) != null) {
			try {
				referenceDate = DATE_FORMAT.parse(contextParameters
						.getParameter(REFERENCE_DATE_PARAMETER));
			} catch (ParseException e) {
				// TODO: Introduce ArchDslGeneratorConfigurationException?
				throw new ArchDslGeneratorException(e);
			}
		} else if (contextParameters
				.getParameter(NEW_DEPENDENCIES_OFFSET_PARAMETER) != null) {
			long offset = Long.parseLong(contextParameters
					.getParameter(NEW_DEPENDENCIES_OFFSET_PARAMETER));
			long referenceDateMs = System.currentTimeMillis()
					- (offset * 24 * 60 * 60 * 1000);
			referenceDate = new Date(referenceDateMs);
		} else
			referenceDate = new Date();
		return referenceDate;
	}

	/**
	 * Sets the date of all dependency diagnostics in allDependencyDiagnostics
	 * that are contained in the dependency diagnostics file.
	 * 
	 * TODO: Die Lösung, dass das Datum mit im diagnostic result gespeichert
	 * wird ist eigentlich nicht so wirklich schön und sollte evtl. durch eine
	 * bessere Alternative ersetzt werden.
	 * 
	 * @param targetDir
	 * @param allDependencyDiagnostics
	 * @param modelContents
	 * @return
	 * @throws ArchDslGeneratorException
	 */
	private void setDateOfDependencyDiagnostics(final IPath targetDir,
			Collection<IDiagnosticResultBase> allDependencyDiagnostics,
			final Collection<? extends EObject> modelContents)
			throws ArchDslGeneratorException {
		final File dependencyDiagnosticsFile = new File(targetDir.append(
				DEPENDENCY_DIAGNOSTICS_FILENAME).toOSString());
		if (dependencyDiagnosticsFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(
						dependencyDiagnosticsFile));
				while (reader.ready()) {
					String[] entries = reader.readLine().split(
							DEPENDENCY_DIAGNOSTIC_FILE_SEPARATOR);
					Object subject = null;
					String subjectType = entries[2];
					// TODO: Implement support for the other subject types?
					if (subjectType.equals(SUBJECT_TYPE_MODULE_MODULE)) {
						subject = findModulePair(modelContents, entries[3],
								entries[4]);
						if (subject == null)
							continue;
					}
					// The first element of the iteration is chosen since it is
					// assumed that entries[1] != *
					IDiagnosticResultBase diagnosticResult = null;
					Iterable<IDiagnosticResultBase> diagnosticResults = getDiagnosticResults(
							allDependencyDiagnostics, entries[1], subjectType,
							subject);
					if (diagnosticResults.iterator().hasNext())
						diagnosticResult = diagnosticResults.iterator().next();
					if (diagnosticResult != null)
						try {
							diagnosticResult.setDate(DATE_FORMAT
									.parse(entries[0]));
						} catch (ParseException e) {
							// TODO: Introduce
							// ArchDslGeneratorConfigurationException?
							throw new ArchDslGeneratorException(e);
						}
				}
			} catch (IOException e) {
				throw new ArchDslGeneratorException(e);
			}
		}
	}

	/**
	 * Searches for the modules in the modelContents which names match the given
	 * regular expressions and returns the corresponding module pairs in an
	 * Iterable<Pair<Module, Module>>.
	 * 
	 * If no module can not be found which matches regex1 or regex2 an empty
	 * Iterable<Pair<Module, Module>> is returned.
	 * 
	 * @param modelContents
	 * @param regex1
	 * @param regex2
	 * @return Iterable<Pair<Module, Module>> with the modules for the given
	 *         module names or empty Iterable<Pair<Module, Module>> if one of
	 *         the regular expressions can not be matched
	 * @throws ArchDslGeneratorException
	 */
	private Iterable<Pair<Module, Module>> findModulePairs(
			final Collection<? extends EObject> modelContents, String regex1,
			String regex2) {
		Collection<Pair<Module, Module>> modulePairs = new ArrayList<Pair<Module, Module>>();
		Iterable<Module> modules1 = ModelQueries.getModulesByRegEx(
				modelContents, regex1);
		Iterable<Module> modules2 = ModelQueries.getModulesByRegEx(
				modelContents, regex2);
		if (!modules1.iterator().hasNext()) {
			logger.info("Modules with a name matching " + regex1
					+ " could not be found.");
			return modulePairs;
		}
		if (!modules2.iterator().hasNext()) {
			logger.info("Modules with a name matching " + regex2
					+ " could not be found.");
			return modulePairs;
		}
		for (Module module1 : modules1) {
			for (Module module2 : modules2) {
				modulePairs.add(new Pair<Module, Module>(module1, module2));
			}
		}
		return modulePairs;
	}

	/**
	 * Searches for the modules given by their names in the modelContents and
	 * returns a Pair<Module, Module>. If no module can not be found which
	 * matches moduleName1 or moduleName2 null is returned.
	 * 
	 * @param modelContents
	 * @param moduleName1
	 * @param moduleName2
	 * @return Pair<Module, Module> with the modules for the given module names
	 *         or null if one of the modules can not be found
	 */
	private Pair<Module, Module> findModulePair(
			final Collection<? extends EObject> modelContents,
			String moduleName1, String moduleName2) {
		// TODO: Throw exception if one of the modules does not
		// exist
		Module module1 = ModelQueries.getModuleByName(modelContents,
				moduleName1);
		Module module2 = ModelQueries.getModuleByName(modelContents,
				moduleName2);

		if (module1 == null) {
			logger.info("Modules with a name matching " + moduleName1
					+ " could not be found.");
			return null;
		}
		if (module2 == null) {
			logger.info("Modules with a name matching " + moduleName2
					+ " could not be found.");
			return null;
		}

		return new Pair<Module, Module>(module1, module2);
	}

	/**
	 * Returns the IDiagnosticResultBase with the given properties
	 * (diagnosticID, subjectType, subject) from the dependencyDiagnostics
	 * iteration. Returns null if no IDiagnosticResultBase with the given
	 * properties could be found.
	 * 
	 * If diagnosticID != * the returned iteration will contain at most one
	 * entry.
	 * 
	 * @param allDependencyDiagnostics
	 * @param diagnosticID
	 *            The id of the diagnostic or * if the diagnosticID does not
	 *            matter.
	 * @param subjectType
	 * @param subject
	 * @return
	 */
	private Collection<IDiagnosticResultBase> getDiagnosticResults(
			Iterable<IDiagnosticResultBase> dependencyDiagnostics,
			String diagnosticID, String subjectType, Object subject) {
		Collection<IDiagnosticResultBase> diagnosticResults = new ArrayList<IDiagnosticResultBase>();
		for (IDiagnosticResultBase diagnosticResult : dependencyDiagnostics) {
			if (diagnosticID.equals("*")
					|| diagnosticResult.getDiagnostic().getID()
							.equals(diagnosticID)) {
				if (diagnosticResult.getDiagnostic().getSubjectType()
						.equals(subjectType)) {
					// TODO: Implement support for the other subject types?
					if (diagnosticResult.getSubjectType().equals(
							SUBJECT_TYPE_MODULE_MODULE)) {
						Pair<Module, Module> diagnosticResultSubject = (Pair<Module, Module>) diagnosticResult
								.getSubject();
						Pair<Module, Module> modulePair = (Pair<Module, Module>) subject;
						if (diagnosticResultSubject.getFirst().getName()
								.equals(modulePair.getFirst().getName())
								&& diagnosticResultSubject
										.getSecond()
										.getName()
										.equals(modulePair.getSecond()
												.getName()))
							diagnosticResults.add(diagnosticResult);
					}
				}
			}
		}
		return diagnosticResults;
	}

	/**
	 * Writes a file which containing all current dependency diagnostics.
	 * 
	 * @param targetDir
	 * @param allDependencyDiagnostics
	 * @throws ArchDslGeneratorException
	 */
	private void writeDependencyDiagnosticsFile(final IPath targetDir,
			final Collection<IDiagnosticResultBase> allDependencyDiagnostics)
			throws ArchDslGeneratorException {
		File dependencyDiagnosticsFile = new File(targetDir.append(
				DEPENDENCY_DIAGNOSTICS_FILENAME).toOSString());
		try {
			Writer writer = new BufferedWriter(new FileWriter(
					dependencyDiagnosticsFile));

			for (IDiagnosticResultBase iDiagnosticResultBase : allDependencyDiagnostics) {
				// Append current dependency values to
				// knownIllegalDependenciesFile
				writer.append(DATE_FORMAT.format(iDiagnosticResultBase
						.getDate())
						+ DEPENDENCY_DIAGNOSTIC_FILE_SEPARATOR
						+ iDiagnosticResultBase.getDiagnostic().getID()
						+ DEPENDENCY_DIAGNOSTIC_FILE_SEPARATOR
						+ iDiagnosticResultBase.getSubjectType()
						+ DEPENDENCY_DIAGNOSTIC_FILE_SEPARATOR);
				// TODO: Implement support for the other subject types
				if (iDiagnosticResultBase.getSubjectType().equals(
						SUBJECT_TYPE_MODULE_MODULE)) {
					Pair<Module, Module> pair = (Pair<Module, Module>) iDiagnosticResultBase
							.getSubject();
					writer.append(pair.getFirst().getName()
							+ DEPENDENCY_DIAGNOSTIC_FILE_SEPARATOR
							+ pair.getSecond().getName());
				}
				writer.append("\n");
			}
			writer.flush();
		} catch (IOException e) {
			throw new ArchDslGeneratorException(e);
		}
	}

	@Override
	public Iterable<String> getRequiredParameters() {
		return new ArrayList<String>();
	}

	@Override
	public String getOutputMessage() {
		return this.outputMessage;
	}

	private List<AbstractBirtGenerator> getGenerators() {
		final List<AbstractBirtGenerator> generators = new ArrayList<AbstractBirtGenerator>();
		if (Platform.getBundle("org.eclipse.birt.report.engine.emitter.html") != null) {
			generators.add(new HTMLBirtGenerator());
		}
		if (Platform.getBundle("org.eclipse.birt.report.engine.emitter.pdf") != null) {
			generators.add(new PDFBirtGenerator());
		}
		if (Platform.getBundle("org.eclipse.birt.report.engine.emitter.ppt") != null) {
			generators.add(new PPTBirtGenerator());
		}
		return generators;
	}

}
