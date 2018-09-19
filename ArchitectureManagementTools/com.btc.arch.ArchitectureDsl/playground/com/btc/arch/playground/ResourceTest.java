package com.btc.arch.playground;

import static org.junit.Assert.assertNotNull;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ocl.ParserException;
import org.eclipse.xtext.resource.XtextResource;
import org.junit.Test;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.util.ArchitectureDslFileUtils;
import com.btc.arch.architectureDsl.util.ModelQueries;

public class ResourceTest {
	@Test
	public void testViaOneShotResource() throws ParserException {
		URI uri = URI
				.createFileURI("test/com/btc/arch/architectureDsl/util/DomainTest.archdsl");
		Resource resource = ArchitectureDslFileUtils.getOneShotResource(uri);

		doTest(resource);
	}

	protected void doTest(Resource resource) {
		Module module = ModelQueries.getModuleByName(resource.getContents(),
				"A.B.D.A");
		assertNotNull(module.eResource());
		Module module2 = ModelQueries.getModuleByName(resource.getContents(),
				"A.B.C.Y");
		assertNotNull(module2.eResource());
	}
}
