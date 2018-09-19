package com.btc.arch.base.dependency;

import java.net.URI;

public interface IParseProblem {

	public interface ILocation {
		public URI getURI();

		public String getModuleName();

		public int getLine();

		public int getColumn();

		public boolean hasPosition();

		public String formatPosition();

	}

	ILocation getLocationDescription();

	String getExplanation();

	Exception getCause();
}
