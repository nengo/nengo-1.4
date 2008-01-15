/*
 * Created on 15-Jan-08
 */
package ca.neo.config.impl;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import ca.neo.config.Configuration;
import ca.neo.config.ListProperty;
import ca.neo.model.StructuralException;

/**
 * TODO: handle generic list types
 * TODO: unit test with bean pattern, list, setter/getter/counter, inserter/remover/adder 
 * TODO: override isMutable() based on available methods
 * TODO: expose separate setters for method groups
 * 
 * @author Bryan Tripp
 */
public class ListPropertyImpl extends AbstractProperty implements ListProperty {

	private Object myTarget;
	
	private Method myGetter;
	private Method mySetter;	
	private Method myCountGetter;
	private Method myArrayGetter;
	private Method myArraySetter;
	private Method myListGetter;
	private Method myInserter;
	private Method myRemover;
	private Method myAdder;

	/**
	 * @param configuration Configuration to which this Property belongs
	 * @param name Parameter name
	 * @param c Parameter type
	 * @return Property or null if the necessary methods don't exist on the underlying class  
	 */
	public static ListProperty getListProperty(Configuration configuration, String name, Class type) {
		ListPropertyImpl result = null;
		Class targetClass = configuration.getConfigurable().getClass();
		
		String uname = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		String[] getterNames = new String[]{"get" + uname};
		String[] setterNames = new String[]{"set" +uname};
		String[] countGetterNames = new String[]{"getNum" + uname, "getNum" + uname + "s", "get" + uname + "Count"};
		String[] arrayGetterNames = new String[]{"get" + uname + "s", "get" + uname + "Array"};
		String[] arraySetterNames = new String[]{"set" + uname + "s", "set" + uname + "Array"};
		String[] listGetterNames = new String[]{"get" + uname + "s", "get" + uname + "List"};
		String[] inserterNames = new String[]{"insert" + uname};
		String[] adderNames = new String[]{"add" + uname};
		String[] removerNames = new String[]{"remove" + uname};
		
		Method getter = getMethod(targetClass, getterNames, new Class[]{Integer.TYPE}, type);
		Method setter = getMethod(targetClass, setterNames, new Class[]{Integer.TYPE, type}, null);
		Method countGetter = getMethod(targetClass, countGetterNames, new Class[0], Integer.TYPE);
		Method arrayGetter = getMethod(targetClass, arrayGetterNames, new Class[0], Array.newInstance(type, 0).getClass());
		Method arraySetter = getMethod(targetClass, arraySetterNames, new Class[]{Array.newInstance(type, 0).getClass()}, null);
		Method listGetter = getMethod(targetClass, listGetterNames, new Class[0], List.class);
		Method inserter = getMethod(targetClass, inserterNames, new Class[]{Integer.TYPE, type}, null);
		Method adder = getMethod(targetClass, adderNames, new Class[]{type}, null);
		Method remover = getMethod(targetClass, removerNames, new Class[]{Integer.TYPE}, null);
		if (remover == null) {
			remover = getMethod(targetClass, removerNames, new Class[]{Integer.TYPE}, type);
		}
		
		if (arrayGetter != null || (getter != null && countGetter != null)) { //OK, minimal method set exists
			boolean mutable = (setter != null || arraySetter != null);
			result = new ListPropertyImpl(configuration, name, type, mutable);
			result.setAccessors(getter, setter, countGetter, arrayGetter, arraySetter, listGetter, inserter, remover, adder);
		}
		
		return result;
	}
	
	//looks for defined method; returns null if no match (no exception thrown)
	private static Method getMethod(Class c, String[] names, Class[] argTypes, Class returnType) {
		Method result = null;
		
		Method[] methods = c.getMethods();
		for (int i = 0; i < methods.length && result == null; i++) {
			for (int j = 0; j < names.length; j++) {
				if (methods[i].getName().equals(names[j]) 
						&& typesCompatible(methods[i].getParameterTypes(), argTypes) 
						&& ((returnType == null && methods[i].getReturnType() == null) || methods[i].getReturnType().equals(returnType))) {
					result = methods[i];
				}
			}
		}
		
		return result;
	}
	
	//checks that two lists of classes are the same
	private static boolean typesCompatible(Class[] a, Class[] b) {
		boolean match = a.length == b.length;
		
		for (int i = 0; i < a.length && match; i++) {
			if (!a[i].equals(b[i])) {
				match = false;
			}
		}
		
		return match;
	}
	
	private ListPropertyImpl(Configuration configuration, String name, Class c, boolean mutable) {
		super(configuration, name, c, mutable);
		myTarget = configuration.getConfigurable();
	}

	private void setAccessors(Method getter, Method setter, Method countGetter, Method arrayGetter, Method arraySetter,  
			Method listGetter, Method inserter, Method remover, Method adder) {
		myGetter = getter;
		mySetter = setter;
		myCountGetter = countGetter;
		myArrayGetter = arrayGetter;
		myArraySetter = arraySetter;
		myListGetter = listGetter;
		myInserter = inserter;
		myRemover = remover;
		myAdder = adder;
	}
	
	/**
	 * @see ca.neo.config.ListProperty#getDefaultValue()
	 */
	public Object getDefaultValue() {
		try {
			return getNumValues() > 0 ? getValue(0) : null;
		} catch (StructuralException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see ca.neo.config.ListProperty#getNumValues()
	 */
	public int getNumValues() {
		int result = -1;
		
		try {
			if (myCountGetter != null) {
				result = ((Integer) myCountGetter.invoke(myTarget, new Object[0])).intValue();
			} else if (myArrayGetter != null) { 
				Object array = myArrayGetter.invoke(myTarget, new Object[0]);
				result = Array.getLength(array);
			} else if (myListGetter != null) {
				result = getList(myTarget, myListGetter).size();
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		
		return result;
	}

	/**
	 * @see ca.neo.config.ListProperty#getValue(int)
	 */
	public Object getValue(int index) throws StructuralException {
		Object result = null;
		
		try {
			if (myGetter != null) {
				result = myGetter.invoke(myTarget, new Object[]{new Integer(index)});
			} else if (myArrayGetter != null) {
				Object array = myArrayGetter.invoke(myTarget, new Object[0]);
				result = Array.get(array, index);
			} else if (myListGetter != null) {
				result = getList(myTarget, myListGetter).get(index);
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		
		return result;
	}

	/**
	 * @see ca.neo.config.ListProperty#setValue(int, java.lang.Object)
	 */
	public void setValue(int index, Object value) throws StructuralException {
		try {
			if (mySetter != null) {
				mySetter.invoke(myTarget, new Object[]{new Integer(index), value});
			} else if (myArrayGetter != null && myArraySetter != null) {
				Object array = myArrayGetter.invoke(myTarget, new Object[0]);
				Array.set(array, index, value);
				myArraySetter.invoke(myTarget, new Object[]{array});
			} else if (myListGetter != null) {
				getList(myTarget, myListGetter).set(index, value);
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}		
	}

	/**
	 * @see ca.neo.config.ListProperty#addValue(java.lang.Object)
	 */
	public void addValue(Object value) throws StructuralException {
		try {
			if (myAdder != null) {
				myAdder.invoke(myTarget, new Object[]{value});
			} else if (myInserter != null) {
				int index = getNumValues();
				myInserter.invoke(myTarget, new Object[]{new Integer(index), value});
			} else if (myListGetter != null) {
				getList(myTarget, myListGetter).add(value);
			} else {
				throw new StructuralException("There is no method available for adding a value");
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see ca.neo.config.ListProperty#insert(int, java.lang.Object)
	 */
	public void insert(int index, Object value) throws StructuralException {
		try {
			if (myInserter != null) {
				myInserter.invoke(myTarget, new Object[]{new Integer(index), value});
			} else if (myListGetter != null) {
				getList(myTarget, myListGetter).add(index, value);
			} else {
				throw new StructuralException("There is no method available for inserting a value");
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see ca.neo.config.ListProperty#remove(int)
	 */
	public void remove(int index) throws StructuralException {
		try {
			if (myRemover != null) {
				myRemover.invoke(myTarget, new Object[]{new Integer(index)});
			} else if (myListGetter != null) {
				getList(myTarget, myListGetter).remove(index);
			} else {
				throw new StructuralException("There is no method available for removing a value");
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}		
	}

	private static List getList(Object target, Method listGetter) {
		try {
			return (List) listGetter.invoke(target, new Object[0]);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see ca.neo.config.Property#isFixedCardinality()
	 */
	public boolean isFixedCardinality() {
		return (myListGetter != null 
				|| (myInserter != null && myRemover != null));
	}

}
