package com.btc.commons.java;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import com.btc.commons.java.functional.CollectionAdder;
import com.btc.commons.java.functional.IUnaryClosure;

public class FileUtils {

	private static final String HIDDEN_PATH_PREFIX = ".";
	private static final String EXTENSION_SEP = ".";

	/**
	 * Skips files and directories which are hidden (in the sense that their
	 * name starts with a ".").
	 * 
	 * @param file
	 * @param fileExtension
	 * @param closure
	 */
	public static void processFilesOnFileTree(File file, String fileExtension,
			IUnaryClosure<File> closure) {
		if (!file.isDirectory()) {
			throw new IllegalArgumentException(MessageFormat.format(
					"{0} is not a directory", file));
		}
		for (File member : Arrays.asList(file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return !name.startsWith(HIDDEN_PATH_PREFIX);
			}
		}))) {
			if (member.isFile()) {
				if (checkFileExtension(fileExtension, member)) {
					closure.process(member);
				}
			} else if (member.isDirectory()) {
				processFilesOnFileTree(member, fileExtension, closure);
			}
		}
	}

	public static void processFilesOnFileTree(File file,
			String[] fileExtensions, IUnaryClosure<File> closure) {
		// TODO this is suboptimal, call only once
		for (String fileExtension : Arrays.asList(fileExtensions)) {
			processFilesOnFileTree(file, fileExtension, closure);
		}
	}

	public static boolean checkFileExtension(String fileExtension, File file) {
		String actualFileExtension;
		if (!fileExtension.startsWith(EXTENSION_SEP))
			actualFileExtension = EXTENSION_SEP + fileExtension;
		else
			actualFileExtension = fileExtension;
		final String name = file.getName();
		return name.endsWith(actualFileExtension);
	}

	public static String getFileExtension(File obj) {
		final String name = obj.getName();
		int lastExtensionSep = name.lastIndexOf(EXTENSION_SEP);
		if (lastExtensionSep != -1) {
			return name.substring(lastExtensionSep);
		} else {
			return null;
		}
	}

	public static void collectFilesOnFileTree(final File startDir,
			final String fileExtension, final Collection<File> resultFiles) {
		processFilesOnFileTree(startDir, fileExtension,
				new CollectionAdder<File>(resultFiles));
	}

	public static void collectFilesOnFileTree(File startDir,
			String[] supportedFileExtensions, Collection<File> resultFiles) {
		// TODO only process tree once
		for (String fileExtension : Arrays.asList(supportedFileExtensions)) {
			collectFilesOnFileTree(startDir, fileExtension, resultFiles);
		}

	}

	public static String toRelativePath(File path, File basePath) {
		return basePath.toURI().relativize(path.toURI()).getPath();
	}

	public static boolean isFileContainedInDirectory(File directory, File file)
			throws IOException {
		return file.getCanonicalPath().startsWith(directory.getCanonicalPath());
	}

	public static String basename(final File file, final String extension) {
		return file.getName().split(Pattern.quote(extension))[0];
	}

}
