package com.btc.arch.architecturedsl.report;

import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.core.runtime.IPath;

public class PDFBirtGenerator extends AbstractBirtGenerator {
	@Override
	protected IRenderOption getRenderOptions(IPath targetDir) {
		PDFRenderOption options = new PDFRenderOption();
		options.setOutputFileName(targetDir.append("report.pdf").toOSString());
		// options.setImageHandler(new HTMLServerImageHandler());
		options.setOutputFormat("pdf");
		// Setting this to true removes html and body tags
		return options;
	}

}
