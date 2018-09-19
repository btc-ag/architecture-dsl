package com.btc.arch.architecturedsl.report;

import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.core.runtime.IPath;

public class HTMLBirtGenerator extends AbstractBirtGenerator {
	@Override
	protected IRenderOption getRenderOptions(IPath targetDir) {
		HTMLRenderOption options = new HTMLRenderOption();
		options.setOutputFileName(targetDir.append("report.html").toOSString());
		options.setImageHandler(new HTMLServerImageHandler());
		options.setImageDirectory(targetDir.toOSString());
		options.setBaseImageURL(".");
		options.setOutputFormat("html");
		// Setting this to true removes html and body tags
		options.setEmbeddable(false);
		return options;
	}

}
