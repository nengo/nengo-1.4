package ca.neo.ui.util;

import java.security.InvalidParameterException;
import java.util.LinkedList;

import ca.neo.model.Node;

public class NengoClipboard {

	private Node selectedObj;

	public static interface ClipboardListener {
		public void clipboardChanged();
	}

	LinkedList<ClipboardListener> listeners = new LinkedList<ClipboardListener>();

	public void addClipboardListener(ClipboardListener listener) {
		listeners.add(listener);
	}

	public void removeClipboardListener(ClipboardListener listener) {
		if (!listeners.contains(listener)) {
			listeners.remove(listener);
		} else {
			throw new InvalidParameterException();
		}

	}

	public void setContents(Node node) {
		selectedObj = node;
		fireChanged();
	}

	private void fireChanged() {
		for (ClipboardListener listener : listeners) {
			listener.clipboardChanged();
		}
	}

	public Node getContents() {
		return selectedObj;
	}

}
