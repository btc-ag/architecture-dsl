package com.btc.manifestdependencyparser;

import java.io.File;
import java.io.IOException;

import com.btc.arch.base.dependency.DependencyParseException;
import com.btc.arch.base.dependency.IDependencyParser;
import com.btc.arch.base.dependency.IFileDependencyParserFactory;

public class ManifestDependencyParserFactory implements
		IFileDependencyParserFactory {
	@Override
	public String[] getFileExtensions() {
		return new String[] { ManifestDependencyParser.MANIFEST_EXTENSION };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.btc.arch.validation.IDependencyParserFactory#createParser(java.io
	 * .File)
	 */
	@Override
	public IDependencyParser createParser(File sourceFile)
			throws DependencyParseException, IOException {
		return new ManifestDependencyParser(sourceFile.toURI());
	}

}
