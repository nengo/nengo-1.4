/*
 * Created on 7-Dec-07
 */
package ca.neo.io;

import java.io.File;
import java.io.IOException;

import ca.neo.model.StructuralException;
import ca.neo.model.impl.MockConfigurable;

/**
 * Functional tests for ConfigurableIO. 
 * 
 * @author Bryan Tripp
 */
public class ConfigurableIOFunctional {

	public static void main(String[] args) {
		try {
			MockConfigurable c = new MockConfigurable(MockConfigurable.getConstructionTemplate());
			ConfigurableIO.save(c, new File("testsave.xml"));
		} catch (StructuralException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
