package com.btc.arch.base.dependency;


public class ParseProblem implements IParseProblem {
	private final Location locationDescription;
	private final Exception cause;
	private final String explanation;

	public ParseProblem(Location locationDescription, String explanation,
			Exception cause) {
		this.locationDescription = locationDescription;
		this.cause = cause;
		this.explanation = explanation;
	}

	public ParseProblem(Location locationDescription, String explanation) {
		this(locationDescription, explanation, null);
	}

	public ParseProblem(Location locationDescription, Exception e) {
		this(locationDescription, e.toString(), e);
	}

	@Override
	public Location getLocationDescription() {
		return this.locationDescription;
	}

	@Override
	public String getExplanation() {
		return this.explanation;
	}

	@Override
	public Exception getCause() {
		return this.cause;
	}

	@Override
	public String toString() {
		return String.format("%s(location=%s, explanation=%s, cause=%s)", this
				.getClass().getName(), this.locationDescription,
				this.explanation, this.cause);
	}

}
