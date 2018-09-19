package com.btc.arch.architecturedsl.report;

import org.eclipse.birt.report.engine.api.script.IDataSetRow;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.api.script.ScriptException;
import org.eclipse.birt.report.engine.api.script.eventadapter.DataSetEventAdapter;
import org.eclipse.birt.report.engine.api.script.instance.IDataSetInstance;

public class ModuleInfoDataSetEventHandler extends DataSetEventAdapter {

	@Override
	public void beforeOpen(IDataSetInstance dataSet,
			IReportContext reportContext) throws ScriptException {
		// TODO Auto-generated method stub
		super.beforeOpen(dataSet, reportContext);
	}

	@Override
	public void onFetch(IDataSetInstance dataSet, IDataSetRow row,
			IReportContext reportContext) throws ScriptException {
		reportContext.getGlobalVariable("");
		// TODO Auto-generated method stub
		super.onFetch(dataSet, row, reportContext);
	}

	@Override
	public void beforeClose(IDataSetInstance dataSet,
			IReportContext reportContext) throws ScriptException {
		reportContext.deleteGlobalVariable("count");
	}

}
