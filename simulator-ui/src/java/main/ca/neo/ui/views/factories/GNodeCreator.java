package ca.neo.ui.views.factories;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ca.neo.ui.views.icons.Icon;
import ca.neo.ui.views.objects.ProxyObject;
import ca.neo.ui.views.symbol.Symbol;
import ca.sw.graphics.nodes.WorldObject;
import edu.umd.cs.piccolo.PNode;

public class GNodeCreator implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Class repClass;

	Class iconClass;

	Class symbolClass;

	public GNodeCreator(Class iconClass, Class repClass) {
		this.iconClass = iconClass;
		this.repClass = repClass;
	}

	public ProxyObject createNode() {
		try {
			ProxyObject obj = (ProxyObject) (repClass.newInstance());
			obj.setIcon((Icon)(iconClass.newInstance()));
			return obj;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public WorldObject createIcon() {
		try {
//			Symbol symbol = new Symbol(this);
			
			
			return new Symbol((Icon) (iconClass.newInstance()), this);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
