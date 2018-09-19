package com.btc.arch.base.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.btc.commons.java.EndInserter;
import com.btc.commons.java.Pair;

public class FileListDependencySource implements IDependencySource {

	private final Collection<IParseProblem> problems;
	private final IFileDependencyParserFactory fileDependencyParserFactory;
	private final Iterable<File> files;
	private Set<Pair<String, String>> extractedDependencies;
	private ArrayList<String> baseModuleNames;

	public FileListDependencySource(
			IFileDependencyParserFactory fileDependencyParserFactory,
			Iterable<File> files) {
		this.fileDependencyParserFactory = fileDependencyParserFactory;
		this.problems = new ArrayList<IParseProblem>();
		this.files = files;
	}

	@Override
	public Iterable<Pair<String, String>> getAllDependencies() {
		if (extractedDependencies == null)
			parseFiles();

		return extractedDependencies;
	}

	private void parseFiles() {
		this.extractedDependencies = new HashSet<Pair<String, String>>();
		this.baseModuleNames = new ArrayList<String>();
		final DependencyParserClosure fileClosure = new DependencyParserClosure(
				new EndInserter<Pair<String, String>>(extractedDependencies),
				new EndInserter<String>(baseModuleNames),
				this.fileDependencyParserFactory,
				new EndInserter<IParseProblem>(this.problems));
		for (File file : this.files) {
			fileClosure.process(file);
		}
		fileClosure.reportNonUniqueModuleNames();
	}

	@Override
	public Iterable<IParseProblem> getProblems() {
		return this.problems;
	}

	@Override
	public Iterable<String> getAllBaseModuleNames()
			throws DependencyParseException {
		if (baseModuleNames == null)
			parseFiles();

		return baseModuleNames;
	}
}
