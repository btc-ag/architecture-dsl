package com.btc.arch.architecturedsl.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DependencyHistoryEntry {
	private static final String DATE_FORMAT = "dd.MM.";
	private final Date date;
	private final int legal;
	private final int illegal;
	private final int hiddenRuleViolations;

	public DependencyHistoryEntry(Date date, int legal, int illegal,
			int hiddenRuleViolations) {
		this.date = date;
		this.legal = legal;
		this.illegal = illegal;
		this.hiddenRuleViolations = hiddenRuleViolations;
	}

	public String getDateString() {
		// TODO: Make date format changeable?
		DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
		return dateFormatter.format(date);
	}

	public int getLegal() {
		return legal;
	}

	public int getIllegal() {
		return illegal;
	}

	public int getHiddenRuleViolations() {
		return hiddenRuleViolations;
	}
}
