package com.btc.arch.visualstudio;

import java.io.File;
import java.io.IOException;

import com.btc.arch.base.dependency.DependencyParseException;
import com.btc.arch.base.dependency.IDependencyParser;
import com.btc.arch.base.dependency.IFileDependencyParserFactory;

public class VSDependencyParserFactory implements IFileDependencyParserFactory {
	@Override
	public String[] getFileExtensions() {
		return new String[] { CSProjDependencyParser.CSPROJ_EXTENSION };
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
		return CSProjDependencyParser.createFromURI(sourceFile.toURI());
	}

}
