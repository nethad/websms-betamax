package de.ub0r.android.websms.connector.betamax2;

import java.io.IOException;

import org.apache.http.HttpResponse;

import android.util.Log;

import de.ub0r.android.websms.connector.common.Utils;

public class BalanceChecks {
	
	private static final String USERAGENT = // .
	"curl/7.21.3 (x86_64-pc-linux-gnu) libcurl/7.21.3 OpenSSL/0.9.8o zlib/1.2.3.4 libidn/1.18";
	
	private static final String TAG = "WebSMS.betamax";
	
	private String lastBalanceCheckUrl;
	private boolean balanceCheckSupported;
	private boolean balanceAlreadyChecked = false;
	
//	public boolean isBalanceAlreadyChecked() {
//		return balanceAlreadyChecked;
//	}
	
	public boolean isBalanceSupported(String balanceUrl, String encoding) {
		Log.d(TAG, String.format("isBalanceSupported(%s,encoding)", balanceUrl));
		if (lastBalanceCheckUrl == null || !lastBalanceCheckUrl.equals(balanceUrl)) {
			lastBalanceCheckUrl = balanceUrl;
			doBalanceCheck(balanceUrl, encoding);
		}
		Log.d(TAG, String.format("balanceCheckSupported = %s", balanceCheckSupported));
		return balanceCheckSupported;
	}
	
	private void doBalanceCheck(String balanceUrl, String encoding) {
		Log.d(TAG, String.format("BalanceChecks.doBalanceCheck(%s,encoding)",balanceUrl));
		try {
			HttpResponse response = Utils.getHttpClient(balanceUrl, null, null, USERAGENT, null,
					encoding, true);
			int statusCode = response.getStatusLine().getStatusCode();
			Log.d(TAG, String.format("balance check status code: %d",statusCode));
			if (String.valueOf(statusCode).startsWith("2")) {
				balanceCheckSupported = true;
			} else {
				balanceCheckSupported = false;
			}
		} catch (IOException e) {
			balanceCheckSupported = false;
		}
	}

}
