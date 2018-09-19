package com.btc.arch.base.dependency;

import java.io.File;
import java.net.URI;
import java.text.MessageFormat;

public class Location implements IParseProblem.ILocation {
	public Location(String moduleName, URI uri, int line, int column) {
		this.uri = uri;
		this.moduleName = moduleName;
		this.line = line;
		this.column = column;
	}

	public Location(String moduleName, File path) {
		this(moduleName, path.toURI(), -1, -1);
	}

	public Location(String moduleName, URI uri) {
		this(moduleName, uri, -1, -1);
	}

	private final URI uri;
	private final String moduleName;
	private final int line, column;

	@Override
	public URI getURI() {
		return uri;
	}

	@Override
	public String getModuleName() {
		return moduleName;
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getColumn() {
		return column;
	}

	@Override
	public boolean hasPosition() {
		return line != -1;
	}

	@Override
	public String toString() {
		String positionString;
		if (hasPosition()) {
			positionString = new StringBuilder(", ").append(formatPosition())
					.toString();
		} else {
			positionString = "";
		}
		return MessageFormat.format("Module {0} (URI {1}{2})",
				getModuleName() != null ? getModuleName() : "<unknown>",
				getURI() != null ? getURI() : "<unknown>", positionString);
	}

	@Override
	public String formatPosition() {
		return MessageFormat.format(
				"line {0}{1}",
				getLine(),
				getColumn() != -1 ? MessageFormat.format(", column {0}",
						getColumn()) : "");
	}
}