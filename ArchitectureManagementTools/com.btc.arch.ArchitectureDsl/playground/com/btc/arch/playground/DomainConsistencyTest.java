package com.btc.arch.playground;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.query.ocl.conditions.BooleanOCLCondition;
import org.eclipse.emf.query.statements.FROM;
import org.eclipse.emf.query.statements.IQueryResult;
import org.eclipse.emf.query.statements.SELECT;
import org.eclipse.emf.query.statements.WHERE;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.junit.Test;

import com.btc.arch.architectureDsl.ArchitectureDslPackage;
import com.btc.arch.architectureDsl.util.ArchitectureDslFileUtils;
import com.btc.arch.architectureDsl.util.ArchitectureDslResourceManager;

public class DomainConsistencyTest {
	@Test
	public void testSimpleQuery() throws ParserException {
		URI uri = URI
				.createFileURI("test/com/btc/arch/architectureDsl/util/DomainTest.archdsl");
		Resource resource = ArchitectureDslFileUtils.getOneShotResource(uri);

		ArchitectureDslResourceManager architectureDslResourceManager = new ArchitectureDslResourceManager(
				resource);
		assertEquals(
				"resource has errors: "
						+ architectureDslResourceManager.getErrors(), 0,
				architectureDslResourceManager.getErrors().size());

		EList<EObject> contents = architectureDslResourceManager.getResource()
				.getContents();

		// assertTrue("unexpected resource content: " + selectedEObjects,
		// 10 == selectedEObjects.size());

		Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> env = EcoreEnvironmentFactory.INSTANCE
				.createEnvironment();
		BooleanOCLCondition<EClassifier, EClass, EObject> condition;
		condition = new BooleanOCLCondition<EClassifier, EClass, EObject>(env,
				"true",
				// "self.books->collect(b : Book | b.category)->asSet()->size() > 2",
				ArchitectureDslPackage.Literals.MODULE);
		IQueryResult result = new SELECT(SELECT.UNBOUNDED, false, new FROM(
				contents), new WHERE(condition)).execute();

		assertEquals(4, result.size());

		/*
		 * OCL<EPackage, EClassifier, EOperation, EStructuralFeature,
		 * EEnumLiteral, EParameter, EObject, CallOperationAction,
		 * SendSignalAction, Constraint, EClass, EObject> ocl =
		 * org.eclipse.ocl.ecore.OCL.newInstance(); OCLHelper<EClassifier,
		 * EOperation, EStructuralFeature, Constraint> oclHelper =
		 * ocl.createOCLHelper(); OCLExpression<EClassifier> oclExpression =
		 * oclHelper.createQuery(
		 * "let effectiveModuleGroup : ModuleGroup = module.eRootContainer.eAllContents.typeSelect(ModuleGroup).select(moduleGroup|module.name.startsWith(moduleGroup.name)).sortBy(e|e.name.length).last();"
		 * +
		 * "effectiveModuleGroup.oclIsUndefined() ? list() : effectiveModuleGroup.domains"
		 * ); for (EObject eObject : result) { Object domains =
		 * ocl.evaluate(eObject, oclExpression);
		 * assertFalse(ocl.isInvalid(domains)); }
		 */
	}
}
