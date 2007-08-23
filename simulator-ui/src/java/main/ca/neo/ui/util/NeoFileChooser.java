package ca.neo.ui.util;

import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import ca.neo.ui.NeoGraphics;
import ca.shu.ui.lib.util.UIEnvironment;

public class NeoFileChooser {
	JFileChooser fileChooser;

	private static final long serialVersionUID = 1L;

	public NeoFileChooser() {
		super();
		init();
	}

	FileFilter allFileFilter, networkFileFilter, ensembleFileFilter;

	private void init() {
		fileChooser = new JFileChooser();

		allFileFilter = new AllNeoFiles();
		networkFileFilter = new NetworkFileFilter();
		ensembleFileFilter = new EnsembleFileFilter();

		fileChooser.addChoosableFileFilter(allFileFilter);
		fileChooser.addChoosableFileFilter(networkFileFilter);
		fileChooser.addChoosableFileFilter(ensembleFileFilter);
		fileChooser.setFileFilter(allFileFilter);
	}

	public int showOpenDialog() throws HeadlessException {
		fileChooser.setFileFilter(allFileFilter);
		return fileChooser.showOpenDialog(UIEnvironment.getInstance());
	}

	public int showSaveNetworkDialog() throws HeadlessException {
		fileChooser.setFileFilter(networkFileFilter);
		return fileChooser.showSaveDialog(UIEnvironment.getInstance());
	}

	public int showSaveEnsembleDialog() throws HeadlessException {
		fileChooser.setFileFilter(ensembleFileFilter);
		return fileChooser.showSaveDialog(UIEnvironment.getInstance());
	}

	public File getSelectedFile() {
		return fileChooser.getSelectedFile();
	}

	public void setSelectedFile(File file) {
		fileChooser.setSelectedFile(file);
	}
}

class AllNeoFiles extends AbstractFileFilter {

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

class EnsembleFileFilter extends AbstractFileFilter {

	@Override
	public boolean acceptExtension(String str) {
		return (str.equals(NeoGraphics.ENSEMBLE_FILE_EXTENSION));
	}

	@Override
	public String getDescription() {
		return "Neo Ensemble";
	}

}

class NetworkFileFilter extends AbstractFileFilter {

	@Override
	public boolean acceptExtension(String str) {
		return (str.equals(NeoGraphics.NETWORK_FILE_EXTENSION));
	}

	@Override
	public String getDescription() {
		return "Neo Network";
	}

}
