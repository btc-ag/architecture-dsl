package com.btc.arch.visualstudio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.btc.arch.base.dependency.DependencyParseException;
import com.btc.arch.base.dependency.IDependencyParser;
import com.btc.arch.base.dependency.IParseProblem;
import com.btc.arch.base.dependency.Location;
import com.btc.arch.base.dependency.ParseProblem;
import com.btc.commons.java.FileUtils;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.CachingMapFunctor;
import com.btc.commons.java.functional.DoubleChainMapFunctor;
import com.btc.commons.java.functional.IMapFunctor;
import com.btc.commons.java.functional.IterableChain;
import com.btc.commons.java.functional.IterationUtils;
import com.btc.commons.java.functional.PairSecondMapFunctor;

public class CSProjDependencyParser implements IDependencyParser {
	public static class BasenameResolver implements
			ProjectReferenceProxyResolver {

		private final Logger _logger;

		public BasenameResolver() {
			this._logger = Logger.getLogger(this.getClass());
		}

		@Override
		public String mapItem(final ProjectReferenceProxy proxy) {
			final String basename = FileUtils.basename(new File(proxy.getURI()
					.getPath()), CSPROJ_EXTENSION);
			this._logger.info(MessageFormat.format(
					Messages.CSProjDependencyParser_UsingBasename, basename,
					proxy.getURI()));
			return basename == null || basename.isEmpty() ? null : basename;
		}
	}

	public static class DefaultResolver implements
			ProjectReferenceProxyResolver {
		private static final String ERROR_MESSAGE = Messages.CSProjDependencyParser_CannotResolveTarget;
		private final Logger _logger;

		public DefaultResolver() {
			this._logger = Logger.getLogger(this.getClass());
		}

		@Override
		public String mapItem(final ProjectReferenceProxy proxy) {
			try {
				final String rawModuleName = CSProjDependencyParser
						.createFromURI(proxy.getURI()).getRawModuleName();
				return rawModuleName == null || rawModuleName.isEmpty() ? null
						: rawModuleName;
			} catch (final DependencyParseException e) {
				this._logger.debug(MessageFormat.format(ERROR_MESSAGE, proxy),
						e);
			} catch (final FileNotFoundException e) {
				this._logger.debug(MessageFormat.format("{0}: {1}",
						MessageFormat.format(ERROR_MESSAGE, proxy),
						e.getMessage()));
			} catch (final IOException e) {
				this._logger.debug(MessageFormat.format(ERROR_MESSAGE, proxy),
						e);
			}
			return null;
		}

	}

	final public static class ProjectReferenceProxy {

		private final URI uri;

		public ProjectReferenceProxy(final URI uri) {
			this.uri = uri.normalize();
		}

		public URI getURI() {
			return this.uri;
		}

		@Override
		public boolean equals(final Object other) {
			if (other instanceof ProjectReferenceProxy) {
				return getURI()
						.equals(((ProjectReferenceProxy) other).getURI());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return getURI().hashCode();
		}

		@Override
		public String toString() {
			return MessageFormat.format("ProjectReferenceProxy({0})", getURI()); //$NON-NLS-1$
		}

	}

	public interface ProjectReferenceProxyResolver extends
			IMapFunctor<ProjectReferenceProxy, String> {

	}

	public static final String CSPROJ_EXTENSION = ".csproj"; //$NON-NLS-1$
	private static final String ASSEMBLY_REFERENCE_NAME_SEP = ","; //$NON-NLS-1$
	private final URI _source;
	private final Document _doc;
	private final XPath _xpath;
	private final ArrayList<IParseProblem> _parseProblems;
	private final Logger _logger;

	/**
	 * @param uri
	 *            an URI which must be absolute
	 * @return
	 * @throws IOException
	 *             if the resource referenced by uri cannot be accessed or read
	 * @throws DependencyParseException
	 *             should not happen
	 * @throws IllegalArgumentException
	 *             if uri is not absolute
	 * @throws MalformedURLException
	 *             if no protocol handler for uri could be found
	 */
	public static CSProjDependencyParser createFromURI(final URI uri)
			throws IOException, DependencyParseException {
		return new CSProjDependencyParser(uri.toURL().openConnection()
				.getInputStream(), uri);
	}

	public CSProjDependencyParser(final InputStream stream)
			throws DependencyParseException {
		this(stream, null);
	}

	public CSProjDependencyParser(final InputStream stream, final URI source)
			throws DependencyParseException {
		this._doc = createDocument(stream);
		this._source = source;
		this._xpath = createXPath();
		this._parseProblems = new ArrayList<IParseProblem>();
		this._logger = Logger.getLogger(this.getClass());
		doBasicChecks();
	}

	private void doBasicChecks() {
		// TODO this should be moved outside this class into a diagnostic
		String rawModuleName = null;
		try {
			rawModuleName = getRawModuleName();
			if (!getAssemblyName().equals(getRootNamespace())) {
				this._parseProblems.add(new ParseProblem(new Location(
						rawModuleName, this._source), MessageFormat.format(
						Messages.CSProjDependencyParser_RootNamespaceDifferent,
						getRootNamespace(), getAssemblyName())));
			}
		} catch (final DependencyParseException e) {
			this._parseProblems.add(new ParseProblem(new Location(
					rawModuleName, this._source), e));
		}
	}

	public String getProjectUUID() throws DependencyParseException {
		return evaluateStringXPathExpression("/Project/PropertyGroup/ProjectGuid/text()"); //$NON-NLS-1$
	}

	public String getAssemblyName() throws DependencyParseException {
		return evaluateStringXPathExpression("/Project/PropertyGroup/AssemblyName/text()"); //$NON-NLS-1$
	}

	protected String evaluateStringXPathExpression(final String xPathExpression)
			throws DependencyParseException {
		try {
			final Document doc = getDocument();

			return (String) this._xpath.evaluate(xPathExpression, doc,
					XPathConstants.STRING);
		} catch (final XPathExpressionException e) {
			throw new AssertionError(MessageFormat.format(
					Messages.CSProjDependencyParser_UnexpectedException, e));
		}
	}

	/*
	 * ported from RevEngTools csharp.csproj_parser
	 */

	public Iterable<Pair<String, String>> getAssemblyReferences()
			throws DependencyParseException {
		final String assemblyName = getAssemblyName();
		final ArrayList<Pair<String, String>> resultList = new ArrayList<Pair<String, String>>();
		for (String assemblyReference : getXPathStrings("/Project/ItemGroup/Reference/@Include")) { //$NON-NLS-1$
			if (assemblyReference.contains(ASSEMBLY_REFERENCE_NAME_SEP)) {
				assemblyReference = assemblyReference.split(
						ASSEMBLY_REFERENCE_NAME_SEP, 2)[0];
			}
			if (assemblyReference.isEmpty()) {
				final String message = MessageFormat.format(
						"Empty assembly reference target in {0}", this._source);
				this._parseProblems.add(new ParseProblem(new Location(assemblyName,
						this._source), message));

				this._logger.warn(message);

			} else {
				resultList.add(new Pair<String, String>(assemblyName,
						assemblyReference));
			}
		}
		return resultList;
	}

	public Iterable<Pair<String, ProjectReferenceProxy>> getProjectReferences()
			throws DependencyParseException {
		final String assemblyName = getAssemblyName();
		final ArrayList<Pair<String, ProjectReferenceProxy>> resultList = new ArrayList<Pair<String, ProjectReferenceProxy>>();
		for (final String projectReference : getXPathStrings("/Project/ItemGroup/ProjectReference/@Include")) { //$NON-NLS-1$
			if (this._source == null) {
				throw new IllegalStateException(
						Messages.CSProjDependencyParser_NoSourceURI);
			}
			resultList
					.add(new Pair<String, ProjectReferenceProxy>(
							assemblyName,
							new ProjectReferenceProxy(
									this._source
											.resolve(relativePathToRelativeURI(projectReference)))));
		}
		return resultList;
	}

	private URI relativePathToRelativeURI(final String path) {
		// TODO is this safe?
		return URI.create(path.replace("\\", "/")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getRootNamespace() throws DependencyParseException {
		return evaluateStringXPathExpression("/Project/PropertyGroup/RootNamespace/text()"); //$NON-NLS-1$
	}

	protected Iterable<String> getXPathStrings(final String xIncludes)
			throws DependencyParseException {
		try {
			final ArrayList<String> result = new ArrayList<String>();
			final Document doc = getDocument();

			final NodeList nodeList = (NodeList) this._xpath.evaluate(xIncludes,
					doc, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); ++i) {
				final Node item = nodeList.item(i);
				result.add(item.getTextContent());
			}
			return result;
		} catch (final XPathExpressionException e) {
			throw new AssertionError(MessageFormat.format(
					Messages.CSProjDependencyParser_UnexpectedException, e));
		}
	}

	private static XPath createXPath() {
		final XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new MSNamespaceContext());
		return xpath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.btc.arch.visualstudio.IDependencyParser#getAllDependencies()
	 */
	@Override
	public Iterable<Pair<String, String>> getAllDependencies()
			throws DependencyParseException {
		final IterableChain<Pair<String, String>> iterableChain = new IterableChain<Pair<String, String>>();
		iterableChain.addIterable(getAssemblyReferences());
		iterableChain.addIterable(getProjectReferencesResolved());
		return iterableChain;
	}

	public Iterable<? extends Pair<String, String>> getProjectReferencesResolved()
			throws DependencyParseException {
		return IterationUtils.mapOptional(getProjectReferences(),
				getProjectReferenceResolver());
	}

	private IMapFunctor<? super Pair<String, ProjectReferenceProxy>, ? extends Pair<String, String>> getProjectReferenceResolver() {
		final String rawModuleName = getRawModuleNameSafe();
		final DoubleChainMapFunctor.FailureHandler<ProjectReferenceProxy, String> failureHandler = new DoubleChainMapFunctor.FailureHandler<ProjectReferenceProxy, String>() {
			private final String errorMessage = Messages.CSProjDependencyParser_CannotResolveReference;
			private final String warningMessage = Messages.CSProjDependencyParser_CannotResolveTargetButCanGuess;

			@Override
			public void handlePrimaryFailure(final ProjectReferenceProxy proxy,
					final String resolvedName) {
				if (resolvedName != null) {
					CSProjDependencyParser.this._parseProblems.add(new ParseProblem(new Location(
							rawModuleName, CSProjDependencyParser.this._source), MessageFormat.format(
							this.warningMessage, proxy.getURI(), resolvedName)));
				} else {
					final String message = MessageFormat.format(this.errorMessage,
							rawModuleName, proxy.getURI());
					CSProjDependencyParser.this._parseProblems.add(new ParseProblem(new Location(
							rawModuleName, CSProjDependencyParser.this._source), message));

					CSProjDependencyParser.this._logger.warn(message);

				}
			}
		};
		// TODO instead of basic base name resolver use a resolver that checks
		// if the resulting module name conforms to given rules (e.g. contains
		// at least one ".")
		final IMapFunctor<ProjectReferenceProxy, String> resolver = new CachingMapFunctor<ProjectReferenceProxy, String>(
				new DoubleChainMapFunctor<ProjectReferenceProxy, String>(
						failureHandler, Arrays.asList(new DefaultResolver()),
						Arrays.asList(new BasenameResolver())));
		return new PairSecondMapFunctor<String, ProjectReferenceProxy, String>(
				resolver);
	}

	protected Document getDocument() {
		return this._doc;
	}

	private Document createDocument(final InputStream stream)
			throws DependencyParseException {
		final DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (final ParserConfigurationException e) {
			throw new AssertionError(MessageFormat.format(
					Messages.CSProjDependencyParser_UnexpectedException, e));
		}
		assert db.isNamespaceAware();
		Document doc;
		try {
			doc = db.parse(stream);
		} catch (final SAXException e) {
			throw new DependencyParseException(MessageFormat.format(
					Messages.CSProjDependencyParser_ParseError, this._source),
					e);
		} catch (final IOException e) {
			throw new DependencyParseException(MessageFormat.format(
					Messages.CSProjDependencyParser_ParseError, this._source),
					e);
		}
		return doc;
	}

	@Override
	public Iterable<IParseProblem> getProblems() {
		return this._parseProblems;
	}

	@Override
	public String getRawModuleName() throws DependencyParseException {
		return getAssemblyName();
	}

	private String getRawModuleNameSafe() {
		String rawModuleName = null;
		try {
			rawModuleName = getRawModuleName();
		} catch (final DependencyParseException e) {
			this._logger.debug(e);
		}
		return rawModuleName;
	}

	@Override
	public Iterable<String> getAllBaseModuleNames()
			throws DependencyParseException {
		return Collections.singletonList(getRawModuleName());
	}

}
