package com.btc.arch.playground;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.query.conditions.eobjects.EObjectCondition;
import org.eclipse.emf.query.conditions.eobjects.EObjectSource;
import org.eclipse.emf.query.conditions.eobjects.IEObjectSource;
import org.eclipse.emf.query.conditions.eobjects.structuralfeatures.EObjectAttributeValueCondition;
import org.eclipse.emf.query.conditions.strings.StringAdapter;
import org.eclipse.emf.query.conditions.strings.StringCondition;
import org.eclipse.emf.query.statements.FROM;
import org.eclipse.emf.query.statements.IQueryResult;
import org.eclipse.emf.query.statements.SELECT;
import org.eclipse.emf.query.statements.WHERE;
import org.junit.Test;

import com.btc.arch.architectureDsl.ArchitectureDslPackage;
import com.btc.arch.architectureDsl.util.ArchitectureDslFileUtils;
import com.btc.arch.architectureDsl.util.ArchitectureDslResourceManager;

public class EMFQueryTest {
	class StringPrefixCondition extends StringCondition {

		private final String prefix;

		public StringPrefixCondition(StringAdapter adapter, String prefix) {
			super(adapter);
			// TODO Auto-generated constructor stub
			this.prefix = prefix;
		}

		@Override
		public boolean isSatisfied(String str) {
			return str.startsWith(prefix);
		}
	}

	@Test
	public void testSimpleQuery() {
		URI uri = URI
				.createFileURI("test/com/btc/arch/architectureDsl/util/DomainTest.archdsl");
		Resource resource = ArchitectureDslFileUtils.getOneShotResource(uri);

		ArchitectureDslResourceManager architectureDslResourceManager = new ArchitectureDslResourceManager(
				resource);

		IEObjectSource selectedEObjects = new EObjectSource(
				architectureDslResourceManager.getResource().getContents());

		EObjectCondition condition = new EObjectAttributeValueCondition(
				ArchitectureDslPackage.eINSTANCE.getBuildingBlock_Name(),
				new StringPrefixCondition(StringAdapter.DEFAULT, "BTC.CAB"));
		IQueryResult result = new SELECT(new FROM(selectedEObjects), new WHERE(
				condition)).execute();

		assertEquals(4, result.size());

		condition = new EObjectAttributeValueCondition(
				ArchitectureDslPackage.eINSTANCE.getBuildingBlock_Name(),
				new StringPrefixCondition(StringAdapter.DEFAULT, "BTC.COP"));
		result = new SELECT(new FROM(selectedEObjects), new WHERE(condition))
				.execute();

		assertEquals(0, result.size());
	}
}
