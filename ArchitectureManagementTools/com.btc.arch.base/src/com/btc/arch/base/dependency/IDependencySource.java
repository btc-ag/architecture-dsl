package com.btc.arch.base.dependency;

import com.btc.commons.java.Pair;

public interface IDependencySource {
	Iterable<String> getAllBaseModuleNames() throws DependencyParseException;

	Iterable<Pair<String, String>> getAllDependencies()
			throws DependencyParseException;

	Iterable<IParseProblem> getProblems();
}
