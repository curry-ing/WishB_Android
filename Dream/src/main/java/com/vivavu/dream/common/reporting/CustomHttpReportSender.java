package com.vivavu.dream.common.reporting;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.HttpSender;
import org.acra.sender.ReportSenderException;

import java.util.Map;

/**
 * Created by yuja on 2014-08-01.
 */
public class CustomHttpReportSender extends HttpSender {
	public CustomHttpReportSender(Method method, Type type, Map<ReportField, String> mapping) {
		super(method, type, mapping);
	}

	public CustomHttpReportSender(Method method, Type type, String formUri, Map<ReportField, String> mapping) {
		super(method, type, formUri, mapping);
	}

	@Override
	public void send(CrashReportData report) throws ReportSenderException {
		ACRA.getErrorReporter();
		super.send(report);
	}
}
