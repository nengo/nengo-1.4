/**
* JNumeric - a Jython port of Numerical Java
* Current Maintainer: Daniel Lemire, Ph.D.
* (c) 1998, 1999 Timothy Hochberg, tim.hochberg@ieee.org
*
* Free software under the Python license, see http://www.python.org
* Home page: http://jnumerical.sourceforge.net
*
*/

package ca.nengo.ui.JNumeric;
import org.python.core.*;
import java.lang.*;
import java.util.*;
 
// There are faster ways to do much of this, but I'm writing the simplest possible FFT.

public class JN_FFT extends PyObject {

	static PyMultiarray _fft(PyObject o, boolean inverse) {
		PyMultiarray a = PyMultiarray.asarray(o, 'D');
		if (PyMultiarray.shapeOf(a).length != 1)
			throw Py.ValueError("FFT only available for 1D arrays");
		int N = a.__len__();
		// Compute log2(N)
		int log2N = 0;
		while ((1 << log2N) < N)
			++log2N;
		if ((1 << log2N) != N)
			throw Py.ValueError("array length is not a power of two");
		// 'bit reverse' a.
		int [] factors = new int[log2N];
		int [] perms = new int[log2N];
		for (int i = 0; i < log2N; i++) {
			factors[i] = 2;
			perms[i] = log2N - i - 1;
		}
		// Copy the array both to make it continuous and so that we don't overwrite.
		a = PyMultiarray.transpose(PyMultiarray.reshape(a, factors), perms);
		a = PyMultiarray.array(PyMultiarray.reshape(a, new int [] {N}));
		// Grab data out of array and operate on it directly
		double [] data = (double [])a.data;
		// Core of FFT algorithm
		double signedTwoPi = (inverse ? 1 : -1) * 2*Math.PI;
		int twoN = 2*N;
		int step = 4;
		while (step <= twoN) {
			int halfStep = step / 2;
			double theta0 = signedTwoPi / step;
			for (int start = 0; start < halfStep; start += 2) {
				double cosTheta = Math.cos(start*theta0);
				double sinTheta = Math.sin(start*theta0);
				for (int jR = start; jR < twoN; jR += step) { 
					int kR = jR + halfStep;
					int jI = jR+1, kI = kR+1;
					double tempR = cosTheta*data[kR] - sinTheta*data[kI];
					double tempI = sinTheta*data[kR] + cosTheta*data[kI];
					data[kR] = data[jR] - tempR;
					data[kI] = data[jI] - tempI;
					data[jR] += tempR;
					data[jI] += tempI;
				}
			}
			step *= 2;
		}
		if (inverse) // Use inverseN ? XXX
			for (int i = 0; i < twoN; i+=2) {
				data[i] /= N;
				data[i+1] /= N;
			}
		return a;
	}

	static public PyMultiarray fft(PyObject o) {
		return _fft(o, false);
	}

	static public PyMultiarray inverse_fft(PyObject o) {
		return _fft(o, true);
	}
}
