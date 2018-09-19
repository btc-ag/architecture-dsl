package com.btc.arch.base.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.btc.commons.java.EndInserter;
import com.btc.commons.java.FileUtils;
import com.btc.commons.java.Pair;

public class FileTreeDependencySource implements IDependencySource {

	private final Collection<IParseProblem> problems;
	private final IFileDependencyParserFactory fileDependencyParserFactory;
	private final File startDir;
	private List<String> baseModuleNames;
	private Set<Pair<String, String>> extractedDependencies;

	public FileTreeDependencySource(
			IFileDependencyParserFactory fileDependencyParserFactory,
			File startDir) {
		this.fileDependencyParserFactory = fileDependencyParserFactory;
		this.problems = new ArrayList<IParseProblem>();
		this.startDir = startDir;
	}

	@Override
	public Iterable<Pair<String, String>> getAllDependencies() {
		if (extractedDependencies == null)
			processFiles();
		return extractedDependencies;
	}

	private void processFiles() {
		this.extractedDependencies = new HashSet<Pair<String, String>>();
		this.baseModuleNames = new ArrayList<String>();
		final DependencyParserClosure closure = new DependencyParserClosure(
				new EndInserter<Pair<String, String>>(extractedDependencies),
				new EndInserter<String>(baseModuleNames),
				this.fileDependencyParserFactory,
				new EndInserter<IParseProblem>(this.problems));
		FileUtils.processFilesOnFileTree(startDir,
				fileDependencyParserFactory.getFileExtensions(), closure);
		closure.reportNonUniqueModuleNames();
	}

	@Override
	public Iterable<IParseProblem> getProblems() {
		return this.problems;
	}

	@Override
	public Iterable<String> getAllBaseModuleNames()
			throws DependencyParseException {
		if (baseModuleNames == null)
			processFiles();
		return baseModuleNames;
	}
}
