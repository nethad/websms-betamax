/*
 * Copyright (C) 2010 Felix Bechstein
 * 
 * This file is part of WebSMS.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */
package de.ub0r.android.websms.connector.betamax2;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
//import android.util.Log;
import de.ub0r.android.websms.connector.common.BasicConnector;
import de.ub0r.android.websms.connector.common.ConnectorCommand;
import de.ub0r.android.websms.connector.common.ConnectorSpec;
import de.ub0r.android.websms.connector.common.ConnectorSpec.SubConnectorSpec;
import de.ub0r.android.websms.connector.common.Utils;
import de.ub0r.android.websms.connector.common.WebSMSException;

/**
 * AsyncTask to manage IO to betamax API.
 * 
 * @author flx
 */
public class ConnectorBetamax extends BasicConnector {

	/** Tag for debug output. */
	private static final String TAG = "WebSMS.betamax";
	/** SmsBug Gateway URL. */
	private static final String URL_SEND = "/myaccount/sendsms.php";
	/** SmsBug Gateway URL. */
	private static final String URL_BALANCE = "/myaccount/getbalance.php";

	private static final String PARAM_USERNAME = "username";
	private static final String PARAM_PASSWORD = "password";
	private static final String PARAM_SENDER = "from";
	private static final String PARAM_RECIPIENT = "to";
	private static final String PARAM_TEXT = "text";

	private String providerDomain;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ConnectorSpec initSpec(final Context context) {
		final String name = context.getString(R.string.connector_betamax_name);
		ConnectorSpec c = new ConnectorSpec(name);
		c.setAuthor(context.getString(R.string.connector_betamax_author));
		c.setBalance(null);
		c.setCapabilities(ConnectorSpec.CAPABILITIES_UPDATE
				| ConnectorSpec.CAPABILITIES_SEND
				| ConnectorSpec.CAPABILITIES_PREFS);
		// c.setValidCharacters(CharacterTable.getValidCharacters());
		c.addSubConnector(TAG, name, SubConnectorSpec.FEATURE_MULTIRECIPIENTS);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		providerDomain = preferences.getString(Preferences.PREFS_DOMAIN, "");
//		Log.d(TAG, "updateSpec() providerDomain = " + providerDomain);

		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ConnectorSpec updateSpec(final Context context,
			final ConnectorSpec connectorSpec) {
		final SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (p.getBoolean(Preferences.PREFS_ENABLED, false)) {
			if (p.getString(Preferences.PREFS_PASSWORD, "").length() > 0) {
				connectorSpec.setReady();
			} else {
				connectorSpec.setStatus(ConnectorSpec.STATUS_ENABLED);
			}
		} else {
			connectorSpec.setStatus(ConnectorSpec.STATUS_INACTIVE);
		}

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		providerDomain = preferences.getString(Preferences.PREFS_DOMAIN, "");
//		Log.d(TAG, "updateSpec() providerDomain = " + providerDomain);

		return connectorSpec;
	}

	@Override
	protected String getUrlSend(ArrayList<BasicNameValuePair> d) {
		StringBuilder url = new StringBuilder();
		url.append("https://").append(providerDomain).append(URL_SEND);
		return url.toString();
	}

	@Override
	protected String getUrlBalance(ArrayList<BasicNameValuePair> d) {
		StringBuilder url = new StringBuilder();
		url.append("https://").append(providerDomain).append(URL_BALANCE);
		return url.toString();
	}

	@Override
	protected String getParamUsername() {
		return PARAM_USERNAME;
	}

	@Override
	protected String getParamPassword() {
		return PARAM_PASSWORD;
	}

	@Override
	protected String getParamRecipients() {
		return PARAM_RECIPIENT;
	}

	@Override
	protected String getParamText() {
		return PARAM_TEXT;
	}

	@Override
	protected String getParamSender() {
		return PARAM_SENDER;
	}

	@Override
	protected String getUsername(Context context, ConnectorCommand command,
			ConnectorSpec cs) {
		String userName = readFromPreferences(context, Preferences.PREFS_USER);
//		Log.d(TAG, "getUsername() = " + userName);
		return userName;
	}

	@Override
	protected String getPassword(Context context, ConnectorCommand command,
			ConnectorSpec cs) {
		return readFromPreferences(context, Preferences.PREFS_PASSWORD);
	}

	@Override
	protected String getSender(Context context, ConnectorCommand command,
			ConnectorSpec cs) {
		String sender = Utils.getSender(context, command.getDefSender());
//		Log.d(TAG, "getSender() = " + sender);
		return sender;
	}

	@Override
	protected String getRecipients(ConnectorCommand command) {
		String recipientsNumbers = Utils.joinRecipientsNumbers(
				Utils.national2international(command.getDefPrefix(),
						command.getRecipients()), ";", true).replaceFirst("00",
				"+");
//		Log.d(TAG, "getRecipients() = " + recipientsNumbers);
		return recipientsNumbers;
	}

	private String readFromPreferences(Context context, String key) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getString(key, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean usePost() {
		return true;
	}

	@Override
	protected void doBootstrap(Context context, Intent intent)
			throws IOException {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		providerDomain = preferences.getString(Preferences.PREFS_DOMAIN, "");
		super.doBootstrap(context, intent);
	}

	@Override
	protected void parseResponse(Context context, ConnectorCommand command,
			ConnectorSpec cs, String htmlText) {

		final String text = command.getText();

		final boolean checkOnly = (text == null || text.length() == 0);
		if (checkOnly) {
			String[] lines = htmlText.split("\n");
			htmlText = null;
			for (String s : lines) {
				cs.setBalance(s.replace("| &#8364;", "\u20AC"));
			}
			return;
		}

		boolean resultIs1 = htmlText.contains("<result>1</result>");
		// boolean resultStringSuccess =
		// htmlText.contains("<resultstring>success</resultstring>");
		if (!resultIs1) {
//			Log.d(TAG,
//					"failed to send message via Betamax vendor, response following:");
//			Log.d(TAG, htmlText);
			throw new WebSMSException(context, R.string.error_server);
		}
	}
}
