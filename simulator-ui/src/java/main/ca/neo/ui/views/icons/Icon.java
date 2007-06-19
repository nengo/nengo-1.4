package ca.neo.ui.views.icons;

import ca.sw.graphics.basics.GDefaults;
import ca.sw.graphics.basics.GText;
import ca.sw.graphics.world.INamedObject;
import ca.sw.graphics.world.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

public class Icon extends WorldObjectImpl implements INamedObject {

	private static final long serialVersionUID = 1L;

	PNode innerNode;
	
	String iconName;

	public Icon(PNode innerNode, String iconName) {
		super();
		this.iconName = iconName;
		this.innerNode = innerNode;
		getLayoutManager().setLeftPadding(0);
		getLayoutManager().setVerticalPadding(0);
		
		addToLayout(innerNode);
		addToLayout(new GText(getName()));
		
		
		setBounds(getFullBounds());
		
		this.setChildrenPickable(false);
	}

	
	public String getName() {
		return iconName;
	}
	
	

	@Override
	public WorldObjectImpl getTooltipObject() {
		// TODO Auto-generated method stub
		return new TextTag(this);
	}
	
}

class TextTag extends WorldObjectImpl {

	public TextTag(Icon icon) {
		super();
		PText tag = new PText(icon.getName() + " Icon");
		tag.setTextPaint(GDefaults.FOREGROUND_COLOR);
		tag.setFont(GDefaults.LARGE_FONT);
		
//		this.setDraggable(false);
		addToLayout(tag);
//		this.setChildrenPickable(false);
//		addChild(tag );
		
//		this.setBounds(getFullBounds());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	
	
	
}
