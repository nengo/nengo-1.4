package ca.neo.ui.util;

import ca.shu.ui.lib.util.Util;

public class NeoUtil {
	public static String objToString(Object configValue) {

		if (Util.isArray(configValue)) {
			return Util.arrayToString(configValue);
		} else {
			return configValue.toString();
		}

	}
}
