package ca.neo.ui.models.proxies;

import ca.neo.model.Origin;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.icons.IconWrapper;
import ca.shu.ui.lib.objects.lines.LineEndWell;
import edu.umd.cs.piccolo.PNode;

/**
 * Proxy UI Object for a Origin model
 * 
 * @author Shu Wu
 * 
 */
public class POrigin extends PModel {

	private static final long serialVersionUID = 1L;

	public POrigin(Origin origin) {
		super();
		setModel(origin);

		setIcon(new OriginIcon(this));
		
		this.setDraggable(false);
		
	}

	public Origin getOrigin() {
		return (Origin) getModel();
	}

	static final String typeName = "Origin";

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
	}

	@Override
	public String getName() {

		return getOrigin().getName();
	}

}

class OriginIcon extends IconWrapper {

	public OriginIcon(PModel parent) {
		super(parent, new LineEndWell());

		configureLabel(false);
		// TODO Auto-generated constructor stub
	}

}
