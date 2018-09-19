package com.btc.arch.architecturedsl.report;

import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.core.runtime.IPath;

public class PPTBirtGenerator extends AbstractBirtGenerator {
	@Override
	protected IRenderOption getRenderOptions(IPath targetDir) {
		RenderOption options = new RenderOption();
		options.setOutputFileName(targetDir.append("report.ppt").toOSString());
		// options.setImageHandler(new HTMLServerImageHandler());
		options.setOutputFormat("ppt");
		return options;
	}
}
