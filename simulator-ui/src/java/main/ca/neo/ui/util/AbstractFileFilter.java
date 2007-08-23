package ca.neo.ui.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import ca.shu.ui.lib.util.Util;

public abstract class AbstractFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = Util.getExtension(f);
		if (extension != null) {

			if (acceptExtension(extension)) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	/**
	 * @param str
	 *            Extension
	 * @return Whether to accept it
	 */
	public abstract boolean acceptExtension(String str);

}