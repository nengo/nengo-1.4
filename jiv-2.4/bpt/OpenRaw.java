/*
 * Created on 15-Aug-07
 */
package bpt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This is to test opening a raw MNI file. 
 * 
 * @author Bryan Tripp
 */
public class OpenRaw {

	public static void main(String[] args) {
		File file = new File("data/t1_icbm_normal_1mm_pn3_rf20.rawb");		
		
		try {
			System.out.println(file.length());
			System.out.println(181*217*181);
			
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			
			long size = file.length();
			int max = 0;
			for (int i = 1; i < size; i++) {
//				System.out.println(in.read());
				int val = in.read();
				if (val > max) max = val;
			}
			System.out.println(max);
			
			in.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
