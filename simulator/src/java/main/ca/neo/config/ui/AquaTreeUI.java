/*
 * Created on 17-Mar-08
 */
package ca.neo.config.ui;

import java.awt.Rectangle;

import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

/**
 * Intended as a wrapper around the Mac look & feel AquaTreeUI, which doesn't 
 * properly expand tree cells when they change size.  
 * 
 * @author Bryan Tripp
 */
public class AquaTreeUI extends BasicTreeUI {

	private BasicTreeUI myUI;
	
	public AquaTreeUI(BasicTreeUI UI) {
		myUI = UI;
	}

	@Override
	public Rectangle getPathBounds(JTree tree, TreePath path) {
		Rectangle result = super.getPathBounds(tree, path);
		System.out.println("bounds: " + result);
		return result;
	}

	@Override
	public void updateSize() {
		super.updateSize();
	}
	
}
