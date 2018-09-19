package com.btc.arch.architecturedsl.ui.perspective;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ArchDslExplorerTreeContentProvider implements ITreeContentProvider {
	// final Logger log =
	// LoggerFactory.getLogger(ExplorerTreeContentProvider.class);

	ExplorerTreeRoot treeRoot = new ExplorerTreeRoot();

	public ArchDslExplorerTreeContentProvider() {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		this.treeRoot.setValidState(false);

		if (inputElement instanceof File) {
			File inputFile = (File) inputElement;

			try {
				this.treeRoot.root = inputFile;

				this.treeRoot.setValidState(true);
			} catch (Exception ioe) {
				this.treeRoot.setValidState(false);
			}

			ExplorerTreeRoot[] array = new ExplorerTreeRoot[] { this.treeRoot };
			return (array);
		}

		String[] ups = new String[] { "Do not use the "
				+ this.getClass().toString() + " this way" };
		return ups;
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof ExplorerTreeRoot) {
			ExplorerTreeRoot obj = (ExplorerTreeRoot) parent;

			if (obj.isValid()) {
				List<Object> content = new ArrayList<Object>();

				List<File> fileList = rootfilter(obj.root);
				content.addAll(fileList);

				content.addAll(obj.specialFileGroups);

				content.addAll(obj.specialFileNodes);

				return content.toArray();
			}
		}

		if (parent instanceof ExplorerTreeSpecialFileGroup) {
			ExplorerTreeSpecialFileGroup obj = (ExplorerTreeSpecialFileGroup) parent;

			return obj.specialFileNodes.toArray();
		}

		if (parent instanceof File) {
			File file = (File) parent;
			return this.filter(file).toArray();
		}

		return null;
	}

	public List<File> rootfilter(File parent) {
		List<File> list = new ArrayList<File>();

		File[] files = parent.listFiles();
		for (File f : files) {
			if (f.getName().toLowerCase().equals(".svn")) {
				continue;
			}

			if (f.getName().toLowerCase().equals("extern")) {
				continue;
			}

			if (hasImportantFiles(f)) {
				list.add(f);
			}
		}

		return list;
	}

	public List<File> filter(File parent) {
		List<File> list = new ArrayList<File>();

		File[] files = parent.listFiles();
		for (File f : files) {
			if (f.getName().toLowerCase().equals(".svn")) {
				continue;
			}

			if (hasImportantFiles(f)) {
				list.add(f);
			}
		}

		return list;
	}

	private boolean hasImportantFiles(File file) {
		boolean rc = false;

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				if (f.getName().toLowerCase().equals(".svn")) {
					continue;
				}

				rc = hasImportantFiles(f);

				if (rc) {
					break;
				}
			}
		} else {
			if (file.getName().endsWith(".archdsl"))
				rc = true;
		}

		return rc;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof ExplorerTreeRoot) {
			return null;
		}

		if (element instanceof ExplorerTreeSpecialFileGroup) {
			return this.treeRoot;
		}

		if (element instanceof ExplorerTreeSpecialFileNode) {
			ExplorerTreeSpecialFileNode obj = (ExplorerTreeSpecialFileNode) element;

			if (null == obj.group) {
				return this.treeRoot;
			} else {
				return obj.group;
			}
		}

		if (element instanceof File) {
			File file = (File) element;
			return file.getParentFile();
		}

		return null;
	}

	@Override
	public boolean hasChildren(Object parent) {
		if (parent instanceof ExplorerTreeRoot) {
			ExplorerTreeRoot obj = (ExplorerTreeRoot) parent;
			if (obj.isValid()) {
				return true;
			} else {
				return false;
			}
		}

		if (parent instanceof ExplorerTreeSpecialFileGroup) {
			// ExplorerTreeSpecialFileGroup obj = (ExplorerTreeSpecialFileGroup)
			// parent;
			return true;
		}

		if (parent instanceof ExplorerTreeSpecialFileNode) {
			// ExplorerTreeSpecialFileNode obj = (ExplorerTreeSpecialFileNode)
			// parent;
			return false;
		}

		if (parent instanceof File) {
			File file = (File) parent;
			return file.isDirectory();
		}

		return false;
	}

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}
}
