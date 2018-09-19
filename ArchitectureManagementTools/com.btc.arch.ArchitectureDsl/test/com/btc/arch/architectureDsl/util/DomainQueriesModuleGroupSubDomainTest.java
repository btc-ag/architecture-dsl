package com.btc.arch.architectureDsl.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.btc.arch.architectureDsl.ArchitectureDslPackage;
import com.btc.arch.architectureDsl.Domain;
import com.btc.commons.emf.EcoreUtils;

public class DomainQueriesModuleGroupSubDomainTest extends
		ArchDslFileUsageTestBase {

	@Test
	public void testGetAllSubdomains() {
		final String[] domainNames = new String[] { "DomainWithSubdomain1",
				"Subdomain2", "Subdomain2Subdomain" };
		final String[][] expectedAllSubdomainNames = new String[][] {
				new String[] { "Subdomain1", "Subdomain2",
						"Subdomain2Subdomain" },
				new String[] { "Subdomain2Subdomain" }, new String[] {}, };
		// TODO should the subdomain list be reflective? then add the relevant
		// domains
		performTestGetAllSubdomains(domainNames, expectedAllSubdomainNames);
	}

	private void performTestGetAllSubdomains(final String[] domainNames,
			final String[][] expectedAllSubdomainNames) {
		for (int i = 0; i < expectedAllSubdomainNames.length; ++i) {
			final Domain domain = ModelQueries.getDomainByName(
					resource.getContents(), domainNames[i]);
			final Collection<Domain> allSubDomains = DomainQueries
					.getAllSubdomains(domain);
			String[] actualAllSubdomainNames = EcoreUtils
					.getStringFeatureArray(allSubDomains,
							ArchitectureDslPackage.Literals.DOMAIN__NAME);
			Arrays.sort(actualAllSubdomainNames);
			assertEquals("test case " + domain.getName(),
					Arrays.asList(expectedAllSubdomainNames[i]),
					Arrays.asList(actualAllSubdomainNames));
		}

	}

	@Test
	public void testGetClosestSuperdomain() {
		final String[] domainNames = new String[] { "DomainWithSubdomain1",
				"Subdomain2", "Subdomain2Subdomain" };
		final String[] expectedSuperdomainNames = new String[] { null,
				"DomainWithSubdomain1", "Subdomain2", };
		performTestGetClosestSuperdomain(domainNames, expectedSuperdomainNames);
	}

	private void performTestGetClosestSuperdomain(final String[] domainNames,
			final String[] expectedSuperdomainNames) {
		for (int i = 0; i < expectedSuperdomainNames.length; ++i) {
			final Domain domain = ModelQueries.getDomainByName(
					resource.getContents(), domainNames[i]);
			final Domain superDomain = DomainQueries
					.getClosestSuperDomain(domain);
			assertEquals(expectedSuperdomainNames[i],
					superDomain != null ? superDomain.getName() : null);
		}

	}

	@Override
	protected String getTestDataFileName() {
		return "ModuleGroupSubDomainTest.archdsl";
	}

	@Override
	protected String getTestProjectName() {
		return "ModuleGroupSubDomainTest";
	}
}
