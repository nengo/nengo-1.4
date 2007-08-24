package ca.neo.ui.models.nodes.connectors;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.neo.model.StructuralException;
import ca.neo.model.Termination;
import ca.neo.ui.configurable.ConfigException;
import ca.neo.ui.configurable.ConfigParam;
import ca.neo.ui.configurable.ConfigParamDescriptor;
import ca.neo.ui.configurable.managers.UserConfigurer;
import ca.neo.ui.configurable.targets.ConfigurableMatrix;
import ca.neo.ui.models.PNeoNode;
import ca.neo.ui.models.icons.ModelIcon;
import ca.neo.ui.models.tooltips.PropertyPart;
import ca.neo.ui.models.tooltips.TitlePart;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.neo.ui.util.NeoUtil;
import ca.neo.util.Configuration;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.objects.lines.ILineAcceptor;
import ca.shu.ui.lib.objects.lines.LineEnd;
import ca.shu.ui.lib.objects.lines.LineInIcon;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;

/**
 * Termination UI Object
 * 
 * @author Shu Wu
 * 
 */
public class PTermination extends PWidget implements ILineAcceptor {

	private static final long serialVersionUID = 1L;

	LineEnd lineEnd;

	PNeoNode nodeParent;

	public PTermination(PNeoNode nodeParent) {
		super(nodeParent);
		init();
	}

	public PTermination(PNeoNode nodeParent, Termination term) {
		super(nodeParent, term);
		setName(term.getName());
		init();

	}

	@Override
	public ConfigParamDescriptor[] getConfigSchema() {
		return null;
	}

	public LineEnd getLineEnd() {
		return lineEnd;
	}

	@Override
	public Termination getModel() {
		return (Termination) super.getModel();
	}

	public Termination getModelTermination() {
		return getModel();
	}

	@Override
	public String getTypeName() {
		return "Termination";
	}

	public float[][] getWeights() {
		return (float[][]) getModel().getConfiguration().getProperty(
				Termination.WEIGHTS);
	}

	public boolean setLineEnd(LineEnd lineEnd) {
		this.lineEnd = lineEnd;
		if (lineEnd != null) {
			addChild(lineEnd);
			this.lineEnd = lineEnd;

		}
		return true;
	}

	public void setWeights(float[][] newWeights) {
		try {
			getModel().getConfiguration().setProperty(Termination.WEIGHTS,
					newWeights);
			popupTransientMsg("Weights changed on Termination");
		} catch (StructuralException e) {
			Util.UserWarning("Could not modify weights: " + e.getMessage());
		}

	}

	private void init() {
		ModelIcon icon = new ModelIcon(this, new LineInIcon());
		icon.configureLabel(false);

		setIcon(icon);
	}

	@Override
	protected Object configureModel(ConfigParam configuredProperties)
			throws ConfigException {
		throw new NotImplementedException();
	}

	@Override
	protected PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();

		if (lineEnd != null) {
			menu.addAction(new RemoveConnectionAction("Remove connection"));
		}

		menu.addSection("Termination Model");
		menu.addAction(new EditWeightsAction("Edit weights"));

		return menu;
	}

	@Override
	protected TooltipBuilder constructTooltips() {
		TooltipBuilder tooltips = super.constructTooltips();

		tooltips.addPart(new PropertyPart("Dimensions", ""
				+ getModel().getDimensions()));

		tooltips.addPart(new TitlePart("Configuration"));
		Configuration config = getModel().getConfiguration();
		String[] configProperties = config.listPropertyNames();
		for (String element : configProperties) {
			Object propertyValue = config.getProperty(element);

			tooltips.addPart(new PropertyPart(element, NeoUtil
					.objToString(propertyValue)));
		}
		return tooltips;
	}

	class EditWeightsAction extends ReversableAction {
		private static final long serialVersionUID = 1L;

		float[][] oldWeights;

		public EditWeightsAction(String actionName) {
			super("Edit weights at Termination " + getName(), actionName);
		}

		@Override
		protected void action() throws ActionException {
			oldWeights = getWeights();

			ConfigurableMatrix matrixEditor = new ConfigurableMatrix(oldWeights);
			UserConfigurer config = new UserConfigurer(matrixEditor);
			try {
				config.configureAndWait();
				setWeights(matrixEditor.getMatrix());
			} catch (ConfigException e) {
				e.defaultHandledBehavior();
				throw new UserCancelledException();
			}

		}

		@Override
		protected void undo() throws ActionException {
			setWeights(oldWeights);
		}
	}

	class RemoveConnectionAction extends StandardAction {
		private static final long serialVersionUID = 1L;

		public RemoveConnectionAction(String actionName) {
			super("Remove connection from Termination", actionName);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void action() throws ActionException {
			lineEnd.destroy();
			lineEnd = null;
		}
	}
}
