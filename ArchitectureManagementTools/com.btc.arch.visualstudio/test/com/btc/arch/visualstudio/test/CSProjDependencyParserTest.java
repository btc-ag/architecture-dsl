package com.btc.arch.visualstudio.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;

import org.junit.Test;

import com.btc.arch.base.dependency.DependencyParseException;
import com.btc.arch.base.dependency.IParseProblem;
import com.btc.arch.visualstudio.CSProjDependencyParser;
import com.btc.arch.visualstudio.Messages;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IterationUtils;

public class CSProjDependencyParserTest {
	static {
		org.apache.log4j.BasicConfigurator.configure();
	}

	@Test
	public void testGetProjectReferences() throws DependencyParseException,
			IOException {
		CSProjDependencyParser csProjDependencyParser = CSProjDependencyParser
				.createFromURI(new File(
						"test/com/btc/arch/visualstudio/test/BTC.CAB.Identity.Authorisation.API.NET.csproj")
						.toURI());
		Collection<? extends Pair<String, String>> projectReferences = IterationUtils
				.materialize(csProjDependencyParser
						.getProjectReferencesResolved());
		assertArrayEquals(new Pair[] { new Pair<String, String>(
				"BTC.CAB.Identity.Authorisation.API.NET",
				"BTC.CAB.Identity.Base.API.NET") }, projectReferences.toArray());
	}

	@Test
	public void testGetAssemblyReferences() throws DependencyParseException,
			IOException {
		CSProjDependencyParser csProjDependencyParser = new CSProjDependencyParser(
				new FileInputStream(
						"test/com/btc/arch/visualstudio/test/BTC.AMM.AmmClient.csproj"));
		Collection<Pair<String, String>> projectReferences = IterationUtils
				.materialize(csProjDependencyParser.getAssemblyReferences());
		assertArrayEquals(new Pair[] {
				new Pair<String, String>("BTC.AMM.AmmClient",
						"BTC.AMM.AppFrame.SlControls"),
				new Pair<String, String>("BTC.AMM.AmmClient",
						"System.ComponentModel.DataAnnotations"),
				new Pair<String, String>("BTC.AMM.AmmClient",
						"System.Runtime.Serialization") },
				projectReferences.toArray());
	}

	@Test
	public void testGetRootNamespace() throws DependencyParseException,
			IOException {
		CSProjDependencyParser csProjDependencyParser = new CSProjDependencyParser(
				new FileInputStream(
						"test/com/btc/arch/visualstudio/test/BTC.AMM.AmmClient.csproj"));
		assertEquals("BTC.AMM.AmmClient",
				csProjDependencyParser.getRootNamespace());
	}

	@Test
	public void testRootNamespaceProblem() throws DependencyParseException,
			IOException {
		CSProjDependencyParser csProjDependencyParser = new CSProjDependencyParser(
				new FileInputStream(
						"test/com/btc/arch/visualstudio/test/BTC.AMM.AmmClient.RootNS.csproj"));
		assertEquals("OtherRootNamespace",
				csProjDependencyParser.getRootNamespace());
		Collection<IParseProblem> problems = IterationUtils
				.materialize(csProjDependencyParser.getProblems());
		assertEquals(1, problems.size());
		IParseProblem problem = problems.iterator().next();
		assertEquals(MessageFormat.format(
				Messages.CSProjDependencyParser_RootNamespaceDifferent,
				"OtherRootNamespace", "BTC.AMM.AmmClient"),
				problem.getExplanation());
	}
}
