package ca.nengo.ui.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import ca.shu.ui.lib.util.Util;

public class NengoConfigManager {

	public static final String NENGO_CONFIG_FILE = "NengoGraphics.config";
	public static final String USER_CONFIG_FILE = "User.config";
	public static final String USER_CONFIG_COMMENTS = "This file persists user preferences for Nengo Graphics";

	private static Properties nengoConfig;
	private static Properties userConfig;

	public enum UserProperties {
		ModelWorkingLocation,
		PlotterDefaultTauFilter,
		PlotterDefaultSubSampling
	}

	public static Properties getNengoConfig() {
		if (nengoConfig == null) {
			nengoConfig = loadConfig(NENGO_CONFIG_FILE);
		}

		return nengoConfig;
	}

	private static Properties getUserConfig() {
		if (userConfig == null) {
			userConfig = loadConfig(USER_CONFIG_FILE);
		}

		return userConfig;
	}

	public static String getUserProperty(UserProperties property) {
		return getUserConfig().getProperty(property.toString());
	}

	public static void saveUserProperty(UserProperties property, String value) {
		getUserConfig().setProperty(property.toString(), value);
	}

	public static void saveUserConfig() {
		if (userConfig != null) {
			try {
				FileOutputStream fos = new FileOutputStream(USER_CONFIG_FILE);
				try {
					userConfig.store(fos, USER_CONFIG_COMMENTS);
					fos.close();
				} finally {
					fos.close();
				}
			} catch (IOException e) {
				Util.debugMsg("Problem saving config file: " + e.getMessage());
			}
		}
	}

	private static Properties loadConfig(String name) {
		try {
			FileInputStream fis = new FileInputStream(name);
			try {

				Properties props = new Properties();
				props.load(fis);
				return props;
			} finally {
				fis.close();
			}
		} catch (IOException e) {
			Util.debugMsg("Problem loading config file: " + e.getMessage());
		}

		return new Properties();
	}

}
