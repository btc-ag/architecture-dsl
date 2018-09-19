package com.btc.arch.base.dependency;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;

import com.btc.arch.base.IContext;
import com.btc.arch.base.IContextDependentServiceFactory;

/**
 * 
 * Unbinding the parser factory and context is optional. The methods are
 * provided to ease the shutdown process (and is required to make the
 * ServiceFactory usable as an OSGi service component).
 * 
 * Caution: Unbinding does not remove the parser factory from previously created
 * dependency sources.
 * 
 * @author SIGIESEC
 * 
 */
public class FileTreeDependencySourceServiceFactory implements
		IContextDependentServiceFactory {
	public static final String PARAMETER_PROJECT_FILE_BASE_PATH = "ProjectFileBasePath";

	private IFileDependencyParserFactory fileDependencyParserFactory;
	private IContext contextParameters;

	public void bindParserFactory(
			IFileDependencyParserFactory _fileDependencyParserFactory) {
		this.fileDependencyParserFactory = _fileDependencyParserFactory;
	}

	public void unbindParserFactory(
			@SuppressWarnings("unused") IFileDependencyParserFactory _fileDependencyParserFactory) {
		this.fileDependencyParserFactory = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.btc.arch.base.dependency.IContextDependentServiceFactory#bindContext
	 * (com.btc.arch.base.IContext)
	 */
	@Override
	public void bindContext(IContext _context) {
		this.contextParameters = _context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.btc.arch.base.dependency.IContextDependentServiceFactory#unbindContext
	 * (com.btc.arch.base.IContext)
	 */
	@Override
	public void unbindContext(IContext _context) {
		this.contextParameters = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.btc.arch.base.dependency.IContextDependentServiceFactory#
	 * getContextParameterNames()
	 */
	@Override
	public String[] getContextParameterNames() {
		// TODO distinguish between required and optional parameter (groups)
		return new String[] { PARAMETER_PROJECT_FILE_BASE_PATH };
	}

	public IDependencySource createDependencySource() {
		if (this.fileDependencyParserFactory == null
				|| this.contextParameters == null) {
			throw new IllegalStateException(MessageFormat.format(
					"Not all required dependencies have been bound on {0}",
					this.toString()));
		}
		if (!this.contextParameters
				.hasAllParameters(getContextParameterNames())) {
			throw new IllegalArgumentException(
					MessageFormat
							.format("Bound context {0} is missing some of the required parameters {1}",
									contextParameters,
									Arrays.asList(getContextParameterNames())));
		}

		String startDirName = this.contextParameters
				.getParameter(PARAMETER_PROJECT_FILE_BASE_PATH);
		File startDir = new File(startDirName);
		return new FileTreeDependencySource(this.fileDependencyParserFactory,
				startDir);
	}

	@Override
	public String toString() {
		return String
				.format("%s(fileDependencyParserFactory=%s, context=%s)",
						this.getClass().getName(),
						this.fileDependencyParserFactory != null ? this.fileDependencyParserFactory
								: "<unbound>",
						this.contextParameters != null ? this.contextParameters
								: "<unbound>");
	}
}
