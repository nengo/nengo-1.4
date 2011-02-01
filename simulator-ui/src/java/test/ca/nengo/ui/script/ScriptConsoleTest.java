/*
 * Created on 11-Nov-07
 */
package ca.nengo.ui.script;

import ca.nengo.ui.script.ScriptConsole;
import junit.framework.TestCase;

/**
 * Unit tests for ScriptConsole. 
 * 
 * @author Bryan Tripp
 */
public class ScriptConsoleTest extends TestCase {

	public void testGetCallChain() {
		assertEquals("", 
				ScriptConsole.getCallChain(""));
		assertEquals("x", 
				ScriptConsole.getCallChain("x"));
		assertEquals("x.", 
				ScriptConsole.getCallChain("x."));
		assertEquals("x.getX", 
				ScriptConsole.getCallChain("x.getX"));
		assertEquals("x.getX(", 
				ScriptConsole.getCallChain("x.getX("));
		assertEquals("y", 
				ScriptConsole.getCallChain("x.getX(y"));
		assertEquals("y.getY", 
				ScriptConsole.getCallChain("x.getX(y.getY"));
		assertEquals("y.getY(foo).ba", 
				ScriptConsole.getCallChain("x.getX(y.getY(foo).ba"));
		assertEquals("y.getY(foo).ba", 
				ScriptConsole.getCallChain("x.getX(c, y.getY(foo).ba"));
		assertEquals("y.getY(foo, foo()).ba", 
				ScriptConsole.getCallChain("x.getX(c, y.getY(foo, foo()).ba"));
		assertEquals("y.getY(foo, foo(), foo()).ba", 
				ScriptConsole.getCallChain("x.getX(c, y.getY(foo, foo(), foo()).ba"));
		assertEquals("y.getY(foo, foo(), foo()).bar(bar())", 
				ScriptConsole.getCallChain("x.getX(c, y.getY(foo, foo(), foo()).bar(bar())"));
		assertEquals("y.getY(foo, foo(), foo()).bar(bar()=2)", 
				ScriptConsole.getCallChain("x.getX(c, s= y.getY(foo, foo(), foo()).bar(bar()=2)"));
	}

}
