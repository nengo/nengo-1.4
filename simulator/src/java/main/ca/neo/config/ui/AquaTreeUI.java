/*
 * Created on 17-Mar-08
 */
package ca.neo.config.ui;

import javax.swing.plaf.basic.BasicTreeUI;

/**
 * To be used in place of the Mac look & feel AquaTreeUI, which seems not to  
 * respect differences in tree cell size, or to expand tree cells when they change size.  
 * 
 * @author Bryan Tripp
 */
public class AquaTreeUI extends BasicTreeUI {

	@Override
	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
	}

	@Override
	protected int getRowHeight() {
		return -1;
	}
	
}
