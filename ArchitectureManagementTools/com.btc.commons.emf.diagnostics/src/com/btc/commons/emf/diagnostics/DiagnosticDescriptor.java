package com.btc.commons.emf.diagnostics;

import com.btc.commons.emf.diagnostics.IDiagnosticResultBase.Severity;

public class DiagnosticDescriptor implements IDiagnosticDescriptor {

	private final String id;
	private final String dynamicId;
	private final Severity baseSeverity;
	private final String description;
	private final String subjectType;
	private final String documentationLink;

	public DiagnosticDescriptor(String id, String dynamicId, Severity severity,
			String description, String subjectType, String documentationLink) {
		this.id = id;
		this.dynamicId = dynamicId;
		this.baseSeverity = severity;
		this.description = description;
		this.subjectType = subjectType;
		this.documentationLink = documentationLink;
	}

	@Override
	public Severity getBaseSeverity() {
		return baseSeverity;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getSubjectType() {
		return subjectType;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public int compareTo(IDiagnosticDescriptor other) {
		return getID().compareTo(other.getID());
	}

	@Override
	public String getDynamicID() {
		return dynamicId;
	}

	@Override
	public String getDocumentationLink() {
		return documentationLink;
	}
}
