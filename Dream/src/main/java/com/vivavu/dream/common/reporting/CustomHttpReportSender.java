package com.vivavu.dream.common.reporting;

import android.net.Uri;
import android.util.Log;

import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.user.User;
import com.vivavu.dream.util.StringUtils;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ACRAConstants;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.HttpSender;
import org.acra.sender.ReportSenderException;
import org.acra.util.HttpRequest;
import org.acra.util.JSONReportBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.acra.ACRA.LOG_TAG;

/**
 * Created by yuja on 2014-08-01.
 */
public class CustomHttpReportSender extends HttpSender {

	private final Uri mFormUri;
	private final Map<ReportField, String> mMapping;
	private final Method mMethod;
	private final Type mType;

	public CustomHttpReportSender(Method method, Type type, Map<ReportField, String> mapping) {
		super(method, type, mapping);
		mMethod = method;
		mFormUri = null;
		mMapping = mapping;
		mType = type;
	}

	public CustomHttpReportSender(Method method, Type type, String formUri, Map<ReportField, String> mapping) {
		super(method, type, formUri, mapping);
		mMethod = method;
		mFormUri = Uri.parse(formUri);
		mMapping = mapping;
		mType = type;
	}

	@Override
	public void send(CrashReportData report) throws ReportSenderException {
		try {
			URL reportUrl = mFormUri == null ? new URL(ACRA.getConfig().formUri()) : new URL(mFormUri.toString());
			Log.d(LOG_TAG, "Connect to " + reportUrl.toString());

			final String login = ACRAConfiguration.isNull(ACRA.getConfig().formUriBasicAuthLogin()) ? null : ACRA
					.getConfig().formUriBasicAuthLogin();
			final String password = ACRAConfiguration.isNull(ACRA.getConfig().formUriBasicAuthPassword()) ? null : ACRA
					.getConfig().formUriBasicAuthPassword();

			final HttpRequest request = new HttpRequest();
			request.setConnectionTimeOut(ACRA.getConfig().connectionTimeout());
			request.setSocketTimeOut(ACRA.getConfig().socketTimeout());
			request.setMaxNrRetries(ACRA.getConfig().maxNumberOfRequestRetries());
			request.setLogin(login);
			request.setPassword(password);
			request.setHeaders(ACRA.getConfig().getHttpHeaders());

			String reportAsString = "";

			// Generate report body depending on requested type
			switch (mType) {
				case JSON:
					JSONObject body = new JSONObject();
					User user = DreamApp.getInstance().getUser();
					body.put("email", user == null ? "not login or unknown" : user.getEmail() );
					body.put("type", "crash");
					JSONObject json = report.toJSON();
					String str = report.get(ReportField.STACK_TRACE);

					String subject = StringUtils.split(str, "[\r\n|:]", 0, "unknown");

					body.put("subject", subject);
					body.put("report", json);
					reportAsString = body.toString();
					break;
				case FORM:
				default:
					final Map<String, String> finalReport = remap(report);
					reportAsString = HttpRequest.getParamsAsFormString(finalReport);
					break;

			}

			// Adjust URL depending on method
			switch (mMethod) {
				case POST:
					break;
				case PUT:
					reportUrl = new URL(reportUrl.toString() + '/' + report.getProperty(ReportField.REPORT_ID));
					break;
				default:
					throw new UnsupportedOperationException("Unknown method: " + mMethod.name());
			}
			request.send(reportUrl, mMethod, reportAsString, mType);

		} catch (IOException e) {
			throw new ReportSenderException("Error while sending " + ACRA.getConfig().reportType()
					+ " report via Http " + mMethod.name(), e);
		} catch (JSONReportBuilder.JSONReportException e) {
			throw new ReportSenderException("Error while sending " + ACRA.getConfig().reportType()
					+ " report via Http " + mMethod.name(), e);
		} catch (JSONException e) {
			throw new ReportSenderException("Error while sending " + ACRA.getConfig().reportType()
					+ " report via Http " + mMethod.name(), e);
		}
	}

	private Map<String, String> remap(Map<ReportField, String> report) {

		ReportField[] fields = ACRA.getConfig().customReportContent();
		if (fields.length == 0) {
			fields = ACRAConstants.DEFAULT_REPORT_FIELDS;
		}

		final Map<String, String> finalReport = new HashMap<String, String>(report.size());
		for (ReportField field : fields) {
			if (mMapping == null || mMapping.get(field) == null) {
				finalReport.put(field.toString(), report.get(field));
			} else {
				finalReport.put(mMapping.get(field), report.get(field));
			}
		}
		return finalReport;
	}
}
