package ca.neo.ui.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import ca.shu.ui.lib.util.Util;

/**
 * Filters files based on file extension.
 * 
 * @author Shu Wu
 */
public abstract class FileExtensionFilter extends FileFilter {

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
	 * @param extension
	 *            Extension
	 * @return Whether this type of extension is accepted
	 */
	public abstract boolean acceptExtension(String extension);

}