package com.btc.arch.architecturedsl.report;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.eclipse.core.runtime.IPath;

import com.btc.arch.base.IContext;
import com.btc.arch.diagnostics.api.Dependency;
import com.btc.arch.generator.ArchDslGeneratorException;

public class DependencyHistoryHandler {

	private static final String DEPENDENCY_HISTORY_FILE_SEPARATOR = ",";
	private static final String DEPENDENCY_HISTORY_FILENAME = "Dependency_History.txt";
	private static final String DEPENDENCY_HISTORY_SIZE_PARAMETER = "Report.DependencyHistorySize";

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy/MM/dd");

	/**
	 * Reads the dependency history from the dependency history file and adds
	 * the current number of legal and illegal dependencies. If the file does
	 * not exist, a new file is created.
	 * 
	 * @param targetDir
	 *            The directory in which the history file is saved.
	 * @param allDependencies
	 * @param allDependencyDiagnostics
	 * @param contextParameters
	 *            The number of values contained in the dependency history.
	 *            Values smaller than 1 cause the return of the complete
	 *            history.
	 * @return An iteration of entries of the dependency history in chronological order. 
	 * TODO: Can this be ensured by the current implementation?
	 * 
	 * @throws ArchDslGeneratorException
	 */
	public static Collection<DependencyHistoryEntry> getDependencyHistory(
			final IPath targetDir, final Iterable<Dependency> allDependencies,
			final IContext contextParameters,
			final int numberOfHiddenRuleViolations)
			throws ArchDslGeneratorException {
		String dependencyHistorySizeParameter = contextParameters
				.getParameter(DEPENDENCY_HISTORY_SIZE_PARAMETER);
		int dependencyHistorySize = 0;
		if (dependencyHistorySizeParameter != null)
			dependencyHistorySize = Integer
					.parseInt(dependencyHistorySizeParameter);
		Collection<DependencyHistoryEntry> dependencyHistoryEntries;
		if (dependencyHistorySize > 0)
			dependencyHistoryEntries = new CircularFifoBuffer(
					dependencyHistorySize);
		else
			dependencyHistoryEntries = new ArrayList<DependencyHistoryEntry>();

		// TODO: Make filename configurable
		File dependencyHistoryFile = new File(targetDir.append(
				DEPENDENCY_HISTORY_FILENAME).toOSString());
		appendCurrentValuesToDependencyHistoryFile(dependencyHistoryFile,
				allDependencies, numberOfHiddenRuleViolations);
		readDependencyHistoryFromDependencyHistoryFile(dependencyHistoryFile,
				dependencyHistoryEntries);
		return dependencyHistoryEntries;
	}

	/**
	 * Reads the dependency history from the dependencyHistoryFile and adds it
	 * to the dependencyHistoryEntries collection.
	 * 
	 * @param dependencyHistoryFile
	 * @param dependencyHistoryEntries
	 * @throws IOException
	 * @throws ArchDslGeneratorException
	 */
	private static void readDependencyHistoryFromDependencyHistoryFile(
			final File dependencyHistoryFile,
			final Collection<DependencyHistoryEntry> dependencyHistoryEntries)
			throws ArchDslGeneratorException {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					dependencyHistoryFile));
			while (reader.ready()) {
				String[] entries = reader.readLine().split(
						DEPENDENCY_HISTORY_FILE_SEPARATOR);
				try {
					int numberOfHiddenDependencies = 0;
					if (entries.length == 4)
						numberOfHiddenDependencies = Integer
								.parseInt(entries[3]);
					dependencyHistoryEntries.add(new DependencyHistoryEntry(
							DATE_FORMAT.parse(entries[0]), Integer
									.parseInt(entries[1]), Integer
									.parseInt(entries[2]),
							numberOfHiddenDependencies));
				} catch (NumberFormatException e) {
					// TODO: Introduce
					// ArchDslGeneratorConfigurationException?
					throw new ArchDslGeneratorException(e);
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

	/**
	 * Adds the current number of legal and illegal dependencies to the
	 * dependencyHistoryFile.
	 * 
	 * @param dependencyHistoryFile
	 * @param allDependencies
	 *            Iteration containing all dependencies
	 * @param allDependencyDiagnostics
	 *            Collection containing the illegal dependencies
	 * @param numberOfHiddenRuleViolations
	 * @throws ArchDslGeneratorException
	 * @throws IOException
	 */
	private static void appendCurrentValuesToDependencyHistoryFile(
			final File dependencyHistoryFile,
			final Iterable<Dependency> allDependencies,
			final int numberOfHiddenRuleViolations)
			throws ArchDslGeneratorException {
		// Get numbers of legal and illegal dependencies
		int legalDependencyCount = 0;
		int illegalDependencyCount = 0;
		for (Dependency dependency : allDependencies) {
			if (dependency.isLegal())
				legalDependencyCount++;
			else
				illegalDependencyCount++;
		}

		// Write the current numbers to the dependencyHistoryFile
		try {
			Writer writer = new BufferedWriter(new FileWriter(
					dependencyHistoryFile, true));

			writer.append(DATE_FORMAT.format(new Date())
					+ DEPENDENCY_HISTORY_FILE_SEPARATOR + legalDependencyCount
					+ DEPENDENCY_HISTORY_FILE_SEPARATOR
					+ illegalDependencyCount
					+ DEPENDENCY_HISTORY_FILE_SEPARATOR
					+ numberOfHiddenRuleViolations + "\n");
			writer.flush();
		} catch (IOException e) {
			throw new ArchDslGeneratorException(e);
		}
	}
}
