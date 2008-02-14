package ca.neo.ui.models.nodes;

import ca.neo.model.impl.FunctionInput;
import ca.neo.ui.actions.PlotFunctionNodeAction;
import ca.neo.ui.models.UINeoNode;
import ca.neo.ui.models.icons.FunctionInputIcon;
import ca.neo.ui.models.tooltips.TooltipBuilder;
import ca.shu.ui.lib.util.menus.PopupMenuBuilder;

/**
 * UI Wrapper of FunctionInput
 * 
 * @author Shu Wu
 */
public class UIFunctionInput extends UINeoNode {

	private static final String typeName = "Function Input";
	private static final long serialVersionUID = 1L;

	public UIFunctionInput(FunctionInput model) {
		super(model);
		init();
	}

	private void init() {
		setIcon(new FunctionInputIcon(this));
		showAllOrigins();
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);
		// MenuBuilder plotMenu = menu.createSubMenu("Plot");
		menu.addSection("Function");

		menu.addAction(new PlotFunctionNodeAction(getName(), "Plot function", getModel()));
	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);

		tooltips.addProperty("Dimensions", "" + getModel().getFunctions().length);
	}

	@Override
	public FunctionInput getModel() {

		return (FunctionInput) super.getModel();
	}

	@Override
	public String getTypeName() {
		return typeName;
	}
}
