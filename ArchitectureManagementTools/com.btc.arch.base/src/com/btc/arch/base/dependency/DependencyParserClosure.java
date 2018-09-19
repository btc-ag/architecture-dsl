package com.btc.arch.base.dependency;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btc.commons.java.CollectionUtils;
import com.btc.commons.java.IInserter;
import com.btc.commons.java.Pair;
import com.btc.commons.java.StringUtils;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IUnaryClosure;
import com.btc.commons.java.functional.IterationUtils;

class DependencyParserClosure implements IUnaryClosure<File> {
	/**
	 * 
	 */
	private final IInserter<Pair<String, String>> extractedDependencies;
	private final IFileDependencyParserFactory fileDependencyParserFactory;
	private final IInserter<IParseProblem> problems;
	private final Map<String, List<File>> moduleNameToFileMap;
	private final IInserter<String> baseModuleNames;

	DependencyParserClosure(
			final IInserter<Pair<String, String>> extractedDependencies,
			final IInserter<String> baseModuleNames,
			final IFileDependencyParserFactory fileDependencyParserFactory,
			final IInserter<IParseProblem> problems) {
		this.extractedDependencies = extractedDependencies;
		this.baseModuleNames = baseModuleNames;
		this.fileDependencyParserFactory = fileDependencyParserFactory;
		this.problems = problems;
		this.moduleNameToFileMap = new HashMap<String, List<File>>();
	}

	@Override
	public void process(final File sourceFile) {
		String rawModuleName = null;
		try {
			final IDependencyParser dependencyParser = this.fileDependencyParserFactory
					.createParser(sourceFile);
			rawModuleName = dependencyParser.getRawModuleName();
			addFile(rawModuleName, sourceFile);
			this.baseModuleNames.add(rawModuleName);
			this.extractedDependencies.addAll(dependencyParser
					.getAllDependencies());
			this.problems.addAll(dependencyParser.getProblems());
		} catch (final FileNotFoundException e) {
			this.problems.add(new ParseProblem(new Location(rawModuleName,
					sourceFile), e.getLocalizedMessage()));
		} catch (final IOException e) {
			this.problems.add(new ParseProblem(new Location(rawModuleName,
					sourceFile), e));
		} catch (final DependencyParseException e) {
			this.problems.add(new ParseProblem(new Location(rawModuleName,
					sourceFile), e));
		}
	}

	private void addFile(final String rawModuleName, final File sourceFile) {
		final List<File> files = CollectionUtils.getOrCreateValueList(
				this.moduleNameToFileMap, rawModuleName);
		files.add(sourceFile);
	}

	public void reportNonUniqueModuleNames() {
		for (final String moduleName : this.moduleNameToFileMap.keySet()) {
			final List<File> files = this.moduleNameToFileMap.get(moduleName);
			if (files.size() > 1) {
				final Iterable<String> fileNames = IterationUtils.map(files,
						new IMapFunctor<File, String>() {

							@Override
							public String mapItem(final File obj) {
								return obj.toURI().toASCIIString();
							}
						});
				this.problems
						.add(new ParseProblem(
								new Location(moduleName, (URI) null),
								MessageFormat
										.format("Module name is not unique, it is used by the following project definitions:\n{0}",
												StringUtils.join(fileNames,
														",\n"))));
			}
		}
	}
}
