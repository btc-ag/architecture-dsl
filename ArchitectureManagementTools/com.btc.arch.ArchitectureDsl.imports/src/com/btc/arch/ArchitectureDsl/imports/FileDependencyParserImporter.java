package com.btc.arch.ArchitectureDsl.imports;

import java.io.File;
import java.text.MessageFormat;

import org.apache.log4j.Logger;

import com.btc.arch.architectureDsl.Model;
import com.btc.arch.architectureDsl.util.DependencySourceImporter;
import com.btc.arch.architectureDsl.util.ModelBuilder;
import com.btc.arch.base.dependency.DependencyParseException;
import com.btc.arch.base.dependency.FileListDependencySource;
import com.btc.arch.base.dependency.IDependencySource;
import com.btc.arch.base.dependency.IFileDependencyParserFactory;
import com.btc.arch.base.dependency.IParseProblem;
import com.btc.commons.java.StringUtils;
import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.ToStringMapFunctor;

public class FileDependencyParserImporter implements Importer {
	// TODO split into two classes, the second version of createModel does not
	// use the parser factory!

	private final IFileDependencyParserFactory parserFactory;
	private final Logger logger;

	public FileDependencyParserImporter(
			IFileDependencyParserFactory parserFactory) {
		this.parserFactory = parserFactory;
		this.logger = Logger.getLogger(this.getClass());
	}

	@Override
	public Model createModel(final ModelBuilder builder,
			final Iterable<File> sourceFiles) {

		final IDependencySource dependencySource = new FileListDependencySource(
				this.parserFactory, sourceFiles);
		try {
			final Model model = new DependencySourceImporter().createModel(
					builder, dependencySource);
			return model;
		} catch (DependencyParseException e) {
			this.logger.error("Error during import", e);
			return null;
		} finally {
			processProblems(dependencySource);
		}
	}

	private void processProblems(IDependencySource dependencySource) {
		final Iterable<IParseProblem> problems = dependencySource.getProblems();
		if (problems.iterator().hasNext()) {
			final Iterable<String> problemDescriptions = IterationUtils.map(
					problems, new ToStringMapFunctor());
			this.logger.warn(MessageFormat.format(
					"Problems during import\n{0}",
					StringUtils.join(problemDescriptions, "\n")));
		}
	}

	@Override
	public String[] getSupportedFileExtensions() {
		return parserFactory.getFileExtensions();
	}

}
