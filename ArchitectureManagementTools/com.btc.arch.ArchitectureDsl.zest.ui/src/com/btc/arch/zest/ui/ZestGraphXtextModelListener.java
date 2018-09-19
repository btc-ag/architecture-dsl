package com.btc.arch.zest.ui;

import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.IXtextModelListener;

public class ZestGraphXtextModelListener implements IXtextModelListener {

	private IXtextDocument doc;

	public ZestGraphXtextModelListener(IXtextDocument doc) {
		this.doc = doc;
	}

	@Override
	public void modelChanged(XtextResource resource) {
		System.out.println("ModelListener " + this + " " + doc);
		// if (resource.getErrors().size() == 0) {
		// try {
		// // ZestGraphView.setForceUpdate(true);
		// ZestGraphView.asyncSetResource(resource);
		// // ZestGraphView.setForceUpdate(false);
		// } catch (CoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// } else {
		// System.out.println("SIZE " + resource.getErrors());
		// }
	}
}
