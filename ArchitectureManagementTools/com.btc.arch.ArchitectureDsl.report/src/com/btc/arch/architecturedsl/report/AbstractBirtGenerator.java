package com.btc.arch.architecturedsl.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Level;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.core.runtime.IPath;

import com.btc.arch.diagnostics.api.Dependency;
import com.btc.arch.generator.ArchDslGeneratorException;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.java.Pair;
import com.btc.commons.java.Triple;

public abstract class AbstractBirtGenerator {

	private static final String DEPENDENCIES_BIRT_CONTEXT_PARAMETER = "dependencies";
	private static final String DEPENDENCY_DIAGNOSTICS_BIRT_CONTEXT_PARAMETER = "dependencyDiagnostics";
	private static final String MODULEINFOS_BIRT_CONTEXT_PARAMETER = "moduleInfos";
	private static final String MODULEGROUPINFOS_BIRT_CONTEXT_PARAMETER = "moduleGroupInfos";
	private static final String DOMAININFOS_BIRT_CONTEXT_PARAMETER = "domainInfos";
	private static final String MODELINFOS_BIRT_CONTEXT_PARAMETER = "modelInfos";
	private static final String DEPENDENCY_HISTORY_BIRT_CONTEXT_PARAMETER = "dependencyHistory";
	private static final String REFERENCE_DATE = "referenceDate";
	private static final String NUMBER_OF_HIDDEN_VIOLATIONS = "numberOfHiddenViolations";
	private static final String UNRESOLVED_REFERENCES_STRINGS = "unresolvedReferencesStrings";
	private static final String SHOW_HIDDEN_RULE_VIOLATIONS_IN_HISTORY = "showHiddenRuleViolationsInHistory";

	public void birtGenerate(
			IPath targetDir,
			Iterable<Dependency> allDependencies,
			Iterable<IDiagnosticResultBase> allDependencyDiagnostics,
			Iterable<Pair<String, String>> moduleInfos,
			Iterable<Pair<String, String>> modelInfos,
			Iterable<DependencyHistoryEntry> dependencyHistoryEntries,
			Date referenceDate,
			int numberOfHiddenViolations,
			Iterable<Triple<String, String, String>> unresolvedReferencesStrings,
			Iterable<Pair<String, String>> moduleGroupInfos,
			Iterable<Pair<String, String>> domainInfos,
			boolean showHiddenRuleViolationsInHistory)
			throws ArchDslGeneratorException {
		try {
			final EngineConfig config = new EngineConfig();
			// delete the following line if using BIRT 3.7 or later
			// config.setEngineHome(
			// "C:\birt-runtime-2_1_0\birt-runtime-2_1_0\ReportEngine" );
			// config.setLogConfig("c:/temp", Level.FINE);

			// Platform.startup( config ); //If using RE API in Eclipse/RCP
			// application this is not needed.
			IReportEngineFactory factory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			IReportEngine engine = factory.createReportEngine(config);
			engine.changeLogLevel(Level.WARNING);

			// Open the report design
			InputStream rptDesign = org.eclipse.core.runtime.Platform
					.getBundle(Activator.BUNDLE_NAME)
					.getResource("InconsistenciesWithExplanation.rptdesign")
					.openStream();
			IReportRunnable design = engine.openReportDesign(rptDesign);

			// TODO has this any effect?
			org.eclipse.core.resources.ResourcesPlugin.getWorkspace();

			// Create task to run and render the report,
			IRunAndRenderTask task = engine.createRunAndRenderTask(design);
			// Set parent classloader for engine
			task.getAppContext().put(
					EngineConstants.APPCONTEXT_CLASSLOADER_KEY,
					getClass().getClassLoader());
			task.getAppContext().put(DEPENDENCIES_BIRT_CONTEXT_PARAMETER,
					allDependencies);
			task.getAppContext().put(
					DEPENDENCY_DIAGNOSTICS_BIRT_CONTEXT_PARAMETER,
					allDependencyDiagnostics);
			task.getAppContext().put(MODULEINFOS_BIRT_CONTEXT_PARAMETER,
					moduleInfos);
			task.getAppContext().put(MODULEGROUPINFOS_BIRT_CONTEXT_PARAMETER,
					moduleGroupInfos);
			task.getAppContext().put(DOMAININFOS_BIRT_CONTEXT_PARAMETER,
					domainInfos);
			task.getAppContext().put(MODELINFOS_BIRT_CONTEXT_PARAMETER,
					modelInfos);
			task.getAppContext().put(DEPENDENCY_HISTORY_BIRT_CONTEXT_PARAMETER,
					dependencyHistoryEntries);
			task.getAppContext().put(REFERENCE_DATE, referenceDate);

			// TODO: If all violations are hidden, the number of hidden
			// violations will not be shown in the report since the
			// numberOfHiddenViolations in the report is set in the data set
			// which represents the dependency diagnostics and which will then
			// be empty.
			task.getAppContext().put(NUMBER_OF_HIDDEN_VIOLATIONS,
					numberOfHiddenViolations);
			task.getAppContext().put(SHOW_HIDDEN_RULE_VIOLATIONS_IN_HISTORY,
					showHiddenRuleViolationsInHistory);
			task.getAppContext().put(UNRESOLVED_REFERENCES_STRINGS,
					unresolvedReferencesStrings);

			// Set parameter values and validate
			// task.setParameterValue("Top Percentage", (new Integer(3)));;
			// task.setParameterValue("Top Count", (new Integer(5)));
			// task.validateParameters();

			// Setup rendering to HTML
			IRenderOption options = getRenderOptions(targetDir);

			task.setRenderOption(options);
			// run and render report
			task.run();
			task.close();

			engine.destroy();
			// Platform.shutdown();
		} catch (EngineException e1) {
			throw new ArchDslGeneratorException(e1);
		} catch (IOException e) {
			throw new ArchDslGeneratorException(e);
		}
	}

	/**
	 * This is a Template Method called by the abstract base class.
	 * 
	 * @param targetDir
	 * @return
	 */
	abstract protected IRenderOption getRenderOptions(IPath targetDir);

}
