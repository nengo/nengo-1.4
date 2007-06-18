package ca.neo.ui.views.objects.proxies;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ca.neo.model.Node;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.plot.Plotter;
import ca.neo.ui.views.objects.properties.PTInt;
import ca.neo.ui.views.objects.properties.PTString;
import ca.neo.ui.views.objects.properties.PropertySchema;

public class PEnsemble extends ProxyNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static String pDim = "Dimensions";

	static String pName = "Name";

	static String pNumOfNeurons = "Number of Neurons";

	static String pStorageName = "Storage Name";

	static PropertySchema[] metaProperties = { new PTString(pName),
			new PTInt(pNumOfNeurons), new PTInt(pDim),
			new PTString(pStorageName)

	};

	String name = "Unamed Ensemble";

	String storageName = "Unamed Ensemble";

	@Override
	public JPopupMenu createPopupMenu() {
		// TODO Auto-generated method stub
		JPopupMenu menu = super.createPopupMenu();
		JMenuItem item;
		item = new JMenuItem(new AbstractAction("Plot") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Plotter.plot((NEFEnsemble) getProxy());
			}
		});
		menu.add(item);

		item = new JMenuItem(new AbstractAction("Plot X") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Plotter.plot((NEFEnsemble) getProxy(), NEFEnsemble.X);
			}
		});
		menu.add(item);

		return menu;

	}

	@Override
	public PropertySchema[] getMetaProperties() {
		// TODO Auto-generated method stub
		return metaProperties;
	}

	// @Override
	// public void initProxy0() {
	// // TODO Auto-generated method stub
	// super.initProxy0();
	//
	// proxy.getOrigins();
	//
	// }

	@Override
	protected Node createProxy() {
		// TODO Auto-generated method stub
		try {
			NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();

			String name = (String) getProperty(pName);
			Integer numOfNeurons = (Integer) getProperty(pNumOfNeurons);
			Integer dimensions = (Integer) getProperty(pDim);
			String storageName = (String) getProperty(pStorageName);

			return ef.make(name, numOfNeurons, dimensions, storageName, false);
		} catch (StructuralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
