package com.btc.arch.ArchitectureDsl.imports;

import java.io.File;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.util.ModelBuilder;

public interface Importer {
	/**
	 * 
	 * @param sourceFiles
	 *            A collection of fully qualified file names.
	 * @return An architecture dsl model containing all modules specified in the
	 *         files and their usedModules dependencies.
	 */
	public Model createModel(ModelBuilder builder, Iterable<File> sourceFiles)
			throws ArchDslImportException;

	// TODO this should be changed to addToModel

	public String[] getSupportedFileExtensions();
}
