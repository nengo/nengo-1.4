package ca.neo.ui.util;

import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import ca.neo.ui.NeoGraphics;
import ca.shu.ui.lib.util.UIEnvironment;

/**
 * File chooser used for NEO Model files.
 * 
 * @author Shu Wu
 */
public class NeoFileChooser {
	private static final long serialVersionUID = 1L;

	private FileFilter allFileFilter, networkFileFilter, ensembleFileFilter;

	/**
	 * Swing File Chooser component
	 */
	private JFileChooser fileChooser;

	public NeoFileChooser() {
		super();
		fileChooser = new JFileChooser();

		allFileFilter = new AllNeoFiles();
		networkFileFilter = new NetworkFileFilter();
		ensembleFileFilter = new EnsembleFileFilter();

		fileChooser.addChoosableFileFilter(allFileFilter);
		fileChooser.addChoosableFileFilter(networkFileFilter);
		fileChooser.addChoosableFileFilter(ensembleFileFilter);
		fileChooser.setFileFilter(allFileFilter);
	}

	/**
	 * @return Selected file
	 */
	public File getSelectedFile() {
		return fileChooser.getSelectedFile();
	}

	/**
	 * @param file
	 *            File to select
	 */
	public void setSelectedFile(File file) {
		fileChooser.setSelectedFile(file);
	}

	/**
	 * Shows a dialog for opening files
	 * 
	 * @return value returned by the Swing File Chooser
	 * @throws HeadlessException
	 */
	public int showOpenDialog() throws HeadlessException {
		fileChooser.setFileFilter(allFileFilter);
		return fileChooser.showOpenDialog(UIEnvironment.getInstance());
	}

	/**
	 * Shows a dialog for saving ensembles
	 * 
	 * @return value returned by Swing File Chooser
	 * @throws HeadlessException
	 */
	public int showSaveEnsembleDialog() throws HeadlessException {
		fileChooser.setFileFilter(ensembleFileFilter);
		return fileChooser.showSaveDialog(UIEnvironment.getInstance());
	}

	/**
	 * Shows a dialog for saving networks
	 * 
	 * @return value returned by Swing File Chooser
	 * @throws HeadlessException
	 */
	public int showSaveNetworkDialog() throws HeadlessException {
		fileChooser.setFileFilter(networkFileFilter);
		return fileChooser.showSaveDialog(UIEnvironment.getInstance());
	}
}

/**
 * File filter which allows all NEO files
 * 
 * @author Shu Wu
 */
class AllNeoFiles extends FileExtensionFilter {

	@Override
	public boolean acceptExtension(String str) {

		return (str.equals(NeoGraphics.ENSEMBLE_FILE_EXTENSION) || str
				.equals(NeoGraphics.NETWORK_FILE_EXTENSION));
	}

	@Override
	public String getDescription() {
		return "All Neo Files";
	}

}

/**
 * File filter which only allows Ensemble files
 * 
 * @author Shu Wu
 */
class EnsembleFileFilter extends FileExtensionFilter {

	@Override
	public boolean acceptExtension(String str) {
		return (str.equals(NeoGraphics.ENSEMBLE_FILE_EXTENSION));
	}

	@Override
	public String getDescription() {
		return "Neo Ensemble";
	}

}

/**
 * File filter which only allows Network files
 * 
 * @author Shu Wu
 */
class NetworkFileFilter extends FileExtensionFilter {

	@Override
	public boolean acceptExtension(String str) {
		return (str.equals(NeoGraphics.NETWORK_FILE_EXTENSION));
	}

	@Override
	public String getDescription() {
		return "Neo Network";
	}

}
