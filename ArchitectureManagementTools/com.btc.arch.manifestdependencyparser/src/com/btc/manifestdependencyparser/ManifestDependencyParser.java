package com.btc.manifestdependencyparser;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

import com.btc.arch.base.dependency.DependencyParseException;
import com.btc.arch.base.dependency.IDependencyParser;
import com.btc.arch.base.dependency.IParseProblem;
import com.btc.commons.java.Pair;

public class ManifestDependencyParser implements IDependencyParser {

	public static final String MANIFEST_EXTENSION = ".MF"; //$NON-NLS-1$

	// TODO: ManifestLiterals is also contained in
	// com.btc.arch.ArchitectureDsl.imports.ManifestImporter. This should be
	// refactored.
	static class ManifestLiterals {
		private static final String MANIFEST_LIST_SEP = ",";
		private static final String MANIFEST_PARAMETER_SEP = ";";
		private static final Name BUNDLE_SYMBOLICNAME = new Name(
				"Bundle-SymbolicName");
		private static final Name REQUIRE_BUNDLE = new Name("Require-Bundle");
	}

	private final String bundleName;
	private final Collection<Pair<String, String>> dependencies;

	public ManifestDependencyParser(URI uri) throws IOException,
			DependencyParseException {
		Manifest manifest = new Manifest(uri.toURL().openConnection()
				.getInputStream());

		bundleName = ((String) manifest.getMainAttributes().get(
				ManifestLiterals.BUNDLE_SYMBOLICNAME))
				.split(ManifestLiterals.MANIFEST_PARAMETER_SEP)[0];

		// Add required bundles
		dependencies = new ArrayList<Pair<String, String>>();
		final String requiredBundlesString = (String) manifest
				.getMainAttributes().get(ManifestLiterals.REQUIRE_BUNDLE);
		if (requiredBundlesString != null) {
			final String[] requiredBundles = requiredBundlesString
					.split(ManifestLiterals.MANIFEST_LIST_SEP);
			for (String requiredBundleName : requiredBundles) {
				requiredBundleName = requiredBundleName
						.split(ManifestLiterals.MANIFEST_PARAMETER_SEP)[0];
				dependencies.add(new Pair<String, String>(bundleName,
						requiredBundleName));
			}
		}
	}

	@Override
	public Iterable<String> getAllBaseModuleNames()
			throws DependencyParseException {
		// Taken from CSProjDependencyParser
		return Collections.singletonList(getRawModuleName());
	}

	@Override
	public Iterable<Pair<String, String>> getAllDependencies()
			throws DependencyParseException {
		return dependencies;
	}

	@Override
	public Iterable<IParseProblem> getProblems() {
		return new ArrayList<IParseProblem>();
	}

	@Override
	public String getRawModuleName() throws DependencyParseException {
		return bundleName;
	}

}