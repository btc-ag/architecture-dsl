package com.btc.arch.base.dependency;

import java.io.File;
import java.io.IOException;

public interface IFileDependencyParserFactory {
	String[] getFileExtensions();

	IDependencyParser createParser(File sourceFile) throws IOException,
			DependencyParseException;

}