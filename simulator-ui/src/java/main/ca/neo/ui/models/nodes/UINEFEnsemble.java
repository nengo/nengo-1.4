package ca.neo.ui.models.nodes;

import ca.neo.model.Node;
import ca.neo.model.Origin;
import ca.neo.model.StructuralException;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.model.nef.NEFEnsembleFactory;
import ca.neo.model.nef.impl.DecodedOrigin;
import ca.neo.model.nef.impl.NEFEnsembleFactoryImpl;
import ca.neo.plot.Plotter;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.descriptors.CInt;
import ca.neo.ui.configurable.descriptors.CString;
import ca.neo.ui.configurable.managers.UserTemplateConfigurer;
import ca.neo.ui.models.nodes.widgets.UIDecodedTermination;
import ca.neo.ui.models.nodes.widgets.UITermination;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.exceptions.ActionException;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;

/**
 * A UI object for NEFEnsemble
 * 
 * @author Shu Wu
 */
public class UINEFEnsemble extends UIEnsemble {
	private static final long serialVersionUID = 1L;

	static final ConfigParamDescriptor pDim = new CInt("Dimensions");

	static final ConfigParamDescriptor pName = new CString("Name");

	static final ConfigParamDescriptor pNumOfNeurons = new CInt(
			"Number of Neurons");

	static final ConfigParamDescriptor pStorageName = new CString(
			"Storage Name");

	static final String typeName = "NEFEnsemble";

	/**
	 * Config descriptors
	 */
	static final ConfigParamDescriptor[] zConfig = { pName, pNumOfNeurons,
			pDim, pStorageName };

	public UINEFEnsemble() {
		super();
		init();

	}

	public UINEFEnsemble(NEFEnsemble model) {
		super(model);
		init();
	}

	private void init() {

	}

	@Override
	protected Node configureModel(ConfigParam prop) {
		try {

			NEFEnsembleFactory ef = new NEFEnsembleFactoryImpl();

			String name = (String) prop.getProperty(pName);

			Integer numOfNeurons = (Integer) prop.getProperty(pNumOfNeurons);
			Integer dimensions = (Integer) prop.getProperty(pDim);
			String storageName = (String) prop.getProperty(pStorageName);

			NEFEnsemble ensemble = ef.make(name, numOfNeurons, dimensions,
					storageName, false);

			return ensemble;
		} catch (StructuralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected PopupMenuBuilder constructMenu() {

		PopupMenuBuilder menu = super.constructMenu();

		MenuBuilder plotMenu = menu.createSubMenu("Plot");

		plotMenu.addAction(new StandardAction("Activities") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void action() {
				Plotter.plot(getModel());
			}

		});

		Origin[] origins = getModel().getOrigins();

		for (Origin element : origins) {
			if (element instanceof DecodedOrigin) {
				plotMenu.addAction(new PlotDecodedOriginDistortion(
						"Decoded Origin (Distortion): " + element.getName(),
						element.getName()));
			}
		}

		// Termination
		menu.addAction(new AddDecodedTerminationAction());
		return menu;
	}

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = super.constructTooltips();
		tooltips.addPart(new PropertyPart("# Dimension", ""
				+ getModel().getDimension()));

		return tooltips;
	}

	/**
	 * Adds a decoded termination to the UI and Ensemble Model The UI is used to
	 * configure it
	 * 
	 * @return PTermination created, null if not
	 */
	public UITermination createDecodedTermintation() {
		UIDecodedTermination termUI = new UIDecodedTermination(this);

		try {
			UserTemplateConfigurer config = new UserTemplateConfigurer(termUI);
			config.configureAndWait();

			addWidget(termUI);
			return termUI;

		} catch (ConfigException e) {
			e.defaultHandledBehavior();
		}

		return null;
	}

	@Override
	public ConfigParamDescriptor[] getConfigSchema() {
		return zConfig;
	}

	@Override
	public NEFEnsemble getModel() {
		return (NEFEnsemble) super.getModel();
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Action for adding a decoded termination
	 * 
	 * @author Shu Wu
	 */
	class AddDecodedTerminationAction extends ReversableAction {

		private static final long serialVersionUID = 1L;

		UITermination addedTermination;

		public AddDecodedTerminationAction() {
			super("Add decoded termination");
		}

		@Override
		protected void action() throws ActionException {
			UITermination term = createDecodedTermintation();
			if (term == null)
				throw new UserCancelledException();
			else
				addedTermination = term;
		}

		@Override
		protected void undo() throws ActionException {
			addedTermination.destroy();

		}

	}

	/**
	 * Action for plotting a decoded origin
	 * 
	 * @author Shu Wu
	 */
	class PlotDecodedOriginDistortion extends StandardAction {
		private static final long serialVersionUID = 1L;
		String decodedOriginName;

		public PlotDecodedOriginDistortion(String actionName,
				String decodedOriginName) {
			super("Plot decoded origin distortion", actionName);
			this.decodedOriginName = decodedOriginName;
		}

		@Override
		protected void action() throws ActionException {
			if (getModel().getDimension() > 1) {
				Util
						.UserWarning("Distortion cannot be plotted for multi-dimensional NEFEnsemble");
			} else
				Plotter.plot(getModel(), decodedOriginName);

		}

	}

}
