package ca.neo.ui.models.icons;

import ca.neo.ui.style.Style;
import ca.neo.ui.views.objects.properties.INamedObject;
import ca.shu.ui.lib.objects.GText;
import ca.shu.ui.lib.world.impl.WorldObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

public class Icon extends WorldObject implements INamedObject {

	private static final long serialVersionUID = 1L;

	PNode innerNode;
	
	String iconName;

	public Icon(PNode innerNode, String iconName) {
		this(innerNode, iconName, 1);
	}
	
	public Icon(PNode innerNode, String iconName, float scale) {
		super();
		this.iconName = iconName;
		this.innerNode = innerNode;
		this.innerNode.setScale(scale);
		
		getLayoutManager().setLeftPadding(0);
		getLayoutManager().setVerticalPadding(0);
		
		addToLayout(innerNode);
		addToLayout(new GText(getName()));
		
		
//		setBounds(getFullBounds());
		
		this.setChildrenPickable(false);
	}

	
	public String getName() {
		return iconName;
	}
	
	

	@Override
	public WorldObject getTooltipObject() {
		// TODO Auto-generated method stub
		return new TextTag(this);
	}
	
}

class TextTag extends WorldObject {

	public TextTag(Icon icon) {
		super();
		PText tag = new PText(icon.getName() + " Icon");
		tag.setTextPaint(Style.FOREGROUND_COLOR);
		tag.setFont(Style.FONT_LARGE);
		
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
