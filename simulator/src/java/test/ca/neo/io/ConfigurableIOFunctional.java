/*
 * Created on 7-Dec-07
 */
package ca.neo.io;

import java.io.File;
import java.io.IOException;

import ca.neo.model.Configurable;
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
//			String jed = "java.endorsed.dirs";
//			File libDir = new File("lib");
//			System.setProperty(jed, System.getProperty(jed) + File.pathSeparator + libDir.getAbsolutePath());
			System.out.println(System.getProperty("java.endorsed.dirs"));
			File file = new File("testsave.xml");
			MockConfigurable c = new MockConfigurable(MockConfigurable.getConstructionTemplate());
			ConfigurableIO.save(c, file);
			
			Configurable loaded = ConfigurableIO.load(file);
			System.out.println(loaded.getClass().getName());
		} catch (StructuralException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
