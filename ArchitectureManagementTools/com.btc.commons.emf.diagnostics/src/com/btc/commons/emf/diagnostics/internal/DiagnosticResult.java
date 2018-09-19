package com.btc.commons.emf.diagnostics.internal;

import java.util.Date;

import com.btc.commons.emf.diagnostics.IDiagnosticDescriptor;
import com.btc.commons.emf.diagnostics.IDiagnosticResult;
import com.btc.commons.emf.diagnostics.IDiagnosticResultBase;

public class DiagnosticResult<T> implements IDiagnosticResult<T> {
	private T subject;
	private String subjectType;
	private IDiagnosticDescriptor diagnostic;
	private String explanation;
	private int weight;
	private Date date;

	public DiagnosticResult(T subject, String subjectType,
			IDiagnosticDescriptor diagnostic, String explanation, int weight) {
		this.subject = subject;
		this.subjectType = subjectType;
		this.diagnostic = diagnostic;
		this.explanation = explanation;
		this.weight = weight;
		this.date = new Date();
	}

	public DiagnosticResult(T subject, String subjectType,
			IDiagnosticDescriptor diagnostic, String explanation) {
		this(subject, subjectType, diagnostic, explanation, 0);
	}

	public DiagnosticResult(T subject, String subjectType,
			IDiagnosticDescriptor diagnostic) {
		this(subject, subjectType, diagnostic, "", 0);
	}

	@Override
	public T getSubject() {
		return this.subject;
	}

	@Override
	public String getSubjectType() {
		return this.subjectType;
	}

	@Override
	public IDiagnosticDescriptor getDiagnostic() {
		return this.diagnostic;
	}

	@Override
	public String getExplanation() {
		return this.explanation;
	}

	@Override
	public int getWeight() {
		return this.weight;
	}

	@Override
	public int compareTo(IDiagnosticResultBase other) {
		int result;
		result = getSubjectType().compareTo(other.getSubjectType());
		if (result == 0) {
			result = getDiagnostic().compareTo(other.getDiagnostic());
			if (result == 0) {
				if (getSubject() instanceof Comparable)
					result = ((Comparable) getSubject()).compareTo(other
							.getSubject());
				else
					result = getSubject().toString()
							.compareTo(other.toString());
			}
		}
		return result;
	}

	@Override
	public Date getDate() {
		return this.date;
	}

	@Override
	public void setDate(Date date) {
		this.date = date;
	}

}
