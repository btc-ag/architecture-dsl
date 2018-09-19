package com.btc.arch.validation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.btc.arch.architectureDsl.ArchitectureDslFactory;
import com.btc.arch.architectureDsl.Module;
import com.btc.commons.emf.diagnostics.IDiagnosticSubjectDescriber;
import com.btc.commons.emf.diagnostics.ecore.EcoreDiagnosticSubjectDescriber;
import com.btc.commons.java.Pair;

public class EcoreDiagnosticSubjectDescriberTest {
	@Test
	public void testPlainEObject() {
		IDiagnosticSubjectDescriber describer = EcoreDiagnosticSubjectDescriber
				.getDefault();
		assertEquals("Module",
				describer.describeSubjectType(ArchitectureDslFactory.eINSTANCE
						.createModule()));
	}

	@Test
	public void testEObjectPair() {
		IDiagnosticSubjectDescriber describer = EcoreDiagnosticSubjectDescriber
				.getDefault();
		assertEquals("Module,Module",
				describer.describeSubjectType(new Pair<Module, Module>(
						ArchitectureDslFactory.eINSTANCE.createModule(),
						ArchitectureDslFactory.eINSTANCE.createModule())));
	}
}
