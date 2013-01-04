package ca.nengo.config.ui;

import ca.nengo.dynamics.impl.LTISystem;
import ca.nengo.dynamics.impl.SimpleLTISystem;
import org.junit.Test;

public class ConfigurationTreeCellEditorTest {
	@Test
	public void testNothing() {		
	}
	
	public static void main(String[] args) {
		NewConfigurableDialog.showDialog(null, LTISystem.class, SimpleLTISystem.class);
	}
}
