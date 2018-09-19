package com.btc.arch.playground;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.diagnostics.ExtractedModuleDependencyValidator;
import com.btc.arch.architectureDsl.util.ArchDslFileUsageTestBase;
import com.btc.arch.base.dependency.DependencySourceServiceFactoryBundle;
import com.btc.arch.base.dependency.FileTreeDependencySource;
import com.btc.arch.base.dependency.IDependencySource;
import com.btc.arch.base.dependency.IFileDependencyParserFactory;
import com.btc.arch.base.dependency.DependencySourceServiceFactoryBundle.DependencySourceServiceFactoryInfo;
import com.btc.commons.emf.diagnostics.DummyDiagnosticsRegistry;
import com.btc.commons.emf.diagnostics.IDiagnosticResult;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;
import com.btc.commons.emf.diagnostics.IDiagnosticResultSource;
import com.btc.commons.java.IFactory;
import com.btc.commons.java.Pair;
import com.btc.commons.java.functional.IterationUtils;

public class ExtractedModuleDependencyValidatorTest extends
		ArchDslFileUsageTestBase {
	@Test
	public void testAMM() {
		Iterable<DependencySourceServiceFactoryInfo> dependencySourceServiceFactoryInfos = DependencySourceServiceFactoryBundle
				.getInstance().getDependencySourceServiceFactoryInfos();
		IFactory<IDependencySource> vsDependencySourceServiceFactory = null;
		for (DependencySourceServiceFactoryInfo dependencySourceServiceFactoryInfo : dependencySourceServiceFactoryInfos) {
			if (dependencySourceServiceFactoryInfo.getProgrammingLanguage()
					.equals("NET"))
				vsDependencySourceServiceFactory = dependencySourceServiceFactoryInfo
						.getExecutableExtension();
		}

		final IDiagnosticResultSource<Collection<? extends EObject>> extractedModuleDependencyValidator = new ExtractedModuleDependencyValidator(
				Collections.singletonList(new FileTreeDependencySource(
						(IFileDependencyParserFactory) vsDependencySourceServiceFactory,
						new File("D:/AMM/AMM/Server"))),
				new DummyDiagnosticsRegistry());
		final Collection<IDiagnosticResultBase> allIllegalDependencies = IterationUtils
				.materializeSorted(extractedModuleDependencyValidator.diagnose(
						this.resource.getContents(), getAllContents()));
		for (final IDiagnosticResultBase result : allIllegalDependencies) {
			final IDiagnosticResult<Pair<Module, Module>> illegalDependency = (IDiagnosticResult<Pair<Module, Module>>) result;
			System.out.println(String.format("%s -> %s", illegalDependency
					.getSubject().getFirst().getName(), illegalDependency
					.getSubject().getSecond().getName()));
		}
		System.out.println("Number of violations: "
				+ allIllegalDependencies.size());
	}

	@Override
	protected String getTestDataFileName() {
		return "AMM_Modules.phys.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "AMM";
	}

	@Override
	protected File getTestDataBaseDirectory() {
		return new File("../com.btc.arch.ArchitectureDsl.examples");
	}
}
