package com.btc.commons.eclipse.core;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.btc.commons.java.FileUtils;

public final class EclipseFileUtils {
	/**
	 * @param url
	 *            The URL to resolve (can be workspace-relative)
	 * @return The file corresponding to the given URL
	 */
	// public static File resolve(final URL url) {
	// File resultFile = null;
	// URL resolved = url;
	// /*
	// * If we don't check the protocol here, the FileLocator throws a
	// * NullPointerException if the URL is a normal file URL.
	// */
	//		if (!url.getProtocol().equals("file")) { //$NON-NLS-1$
	// try {
	// resolved = FileLocator.resolve(resolved);
	// // TODO the returned URL may not be a file: URL!!!
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// try {
	// resultFile = new File(resolved.toURI());
	// } catch (URISyntaxException e) {
	// throw new AssertionError("Resolved URI has wrong syntax: "
	// + resolved);
	// }
	// return resultFile;
	// }

	/**
	 * @param ifile
	 *            The IFile to convert.
	 * @return The corresponding java.io.File object.
	 */
	public static File toJavaFile(IFile ifile) {
		File file = null;
		IPath location = ifile.getLocation();
		if (location != null)
			file = location.toFile();
		return file;
	}

	public static IPath toRelativePath(File path, File basePath) {
		return Path.fromOSString(FileUtils.toRelativePath(path, basePath));
	}
}
