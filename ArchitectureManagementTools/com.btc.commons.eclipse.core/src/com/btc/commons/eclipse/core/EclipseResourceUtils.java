package com.btc.commons.eclipse.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.btc.commons.java.functional.IUnaryClosure;

public final class EclipseResourceUtils {

	public static void processFilesOnResourceTree(final IContainer container,
			final String fileExtension, IUnaryClosure<IFile> closure)
			throws CoreException {
		for (IResource member : container.members()) {
			if (member instanceof IFile) {
				final IFile file = (IFile) member;
				if (checkFileExtension(fileExtension, file)) {
					closure.process(file);
				}
			} else if (member instanceof IFolder) {
				// TODO parametrise the folders to skip
				if (!member.getName().startsWith("."))
					processFilesOnResourceTree((IFolder) member, fileExtension,
							closure);
			}
		}
	}

	public static boolean checkFileExtension(String fileExtension, IFile file) {
		return (file.getFileExtension() == null && (fileExtension == null || fileExtension
				.length() == 0))
				|| (file.getFileExtension() != null && file.getFileExtension()
						.equals(fileExtension));
	}

}
