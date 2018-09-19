package com.btc.arch.generator;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.btc.arch.generator.messages"; //$NON-NLS-1$
	public static String ArchDslGenerator_UnresolvableReferences;
	public static String ArchDslGenerator_UnresolvableReferencesDetails;
	public static String ArchDslGenerator_Cause;
	public static String ArchDslGenerator_InvalidDirectory;
	public static String ArchDslGenerator_MissingParameters;
	public static String ArchDslGenerator_ProjectAlreadyExists;
	public static String ArchDslGenerator_ProjectCreationError;
	public static String ArchDslGenerator_PropertiesFileNotFound;
	public static String ArchDslGenerator_PropertiesFileUnreadable;
	public static String ArchDslGenerator_SourceFileNotFound;
	public static String ArchDslGenerator_SourceFileNotInProject;
	public static String ArchDslGenerator_TargetDirectoryNotFound;
	public static String ArchDslGeneratorFacade_ProjectExistsAtDifferentLocation;

	public static String ArchDslGeneratorFacade_ResourceErrors;
	public static String ArchDslGeneratorFacade_ResourceErrorsException;
	public static String ArchDslGeneratorFacade_ResourceWarnings;
	public static String ArchDslGeneratorFacade_MultipleObjectDefinition;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
