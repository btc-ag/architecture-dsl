package com.btc.commons.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

public class PropertyManager {

	private Properties properties;
	private String propertiesFileName;

	public Object getProperty(String propertyKey) throws PropertyException {
		assert propertyKey != null;

		Object property = null;
		if (properties != null) {
			property = properties.get(propertyKey);
		}

		if (property == null) {
			String message = "The property '" + propertyKey
					+ "' was not set in ";
			if (this.propertiesFileName == null)
				message += "the properties.";
			else
				message += propertiesFileName + ".";
			throw new PropertyException(message);
		}
		return property;
	}

	public void setProperties(Reader propertiesReader, String sourceDescription)
			throws IOException {
		checkSetPropertiesOnlyOnce();
		this.propertiesFileName = sourceDescription;
		BufferedReader bufferedReader = new BufferedReader(propertiesReader);
		this.properties = new Properties();
		this.properties.load(bufferedReader);
	}

	public void checkSetPropertiesOnlyOnce() {
		if (this.properties != null) {
			throw new IllegalStateException(
					"Properties have already been set on this property manager");
		}
	}

	public void setProperties(Map<String, Object> propertyMap) {
		checkSetPropertiesOnlyOnce();
		this.properties = new Properties();
		this.properties.putAll(propertyMap);
	}

	public class PropertyException extends Exception {
		public PropertyException(String message) {
			super(message);
		}

		/**
		 * Generated serialVersionUID
		 */
		private static final long serialVersionUID = 7790831303342102703L;
	}
}
