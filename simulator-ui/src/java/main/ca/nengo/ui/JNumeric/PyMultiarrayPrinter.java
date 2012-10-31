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

// Inspired by the ArrayPrinter module of CPython written by Konrad Hinsen and
// modified by Jim Fulton.

/*
* Comments by D. Lemire.
* I don't understand any of this. It seems way too complicated.
* A simple call to "toString" is enough.
*/
class PrinterFormat {
	String format;
	int itemLength;
	PrinterFormat(String f, int il) {
		format = f;
		itemLength = il;
	}
}

abstract class FormatFunction {
	abstract String format(PyObject o);
}

class FormatInteger extends FormatFunction {
	PyString format;
	FormatInteger(String format) {
		this.format = Py.newString(format);
	}
	String format(PyObject o) {
		return ((PyObject)o).toString();
		//I don't understand this stuff
		//return format.__mod__(o).toString();	
	}
}

class FormatFloat extends FormatFunction {
	PyString format;
	boolean isComplex;
	String suffix;
	int type;
	FormatFloat(String format, boolean isComplex) {
		init(format, isComplex);
	}
	FormatFloat(String format) {
		init(format, false);
	}
	void init(String format, boolean isComplex) {
		char end = format.charAt(format.length()-1);
		if (end == '3') {
			type = 0;
			format = format.substring(0, format.length()-1); 
		}
		else if (end == 'f')
			type = 1;
		else
			type = 2;
		this.format = Py.newString(format);
		this.isComplex = isComplex;
		this.suffix = isComplex ? "j" : "";
	}
	String format(PyObject o) {
		return ((PyObject)o).toString();
		// I don't understand any of this stuff or why it is necessary!
		/*
		String s;
		switch (type) {
		case 0:
			s = format.__mod__(o).toString();
			int thirdNo = s.length()-3;
			char third = s.charAt(thirdNo);
			if (third == '+' || third == '-')
				return s.substring(0,thirdNo) + "0" + s.substring(thirdNo) + suffix;
			return s + suffix;
		case 1:
			s = format.__mod__(o).toString();
			if (true) {
				int dot = 0;
				while (dot < s.length() && s.charAt(dot) != '.')
					dot++;
				int zeros = s.length();
				while (zeros > dot+2 && s.charAt(zeros-1) == '0')
					zeros--;
				char pad[] = new char[s.length()-zeros];
				for (int i = 0; i < pad.length; i++)
					pad[i] = ' ';
				return s.substring(0,zeros) + suffix + new String(pad);
			}
			return s + suffix;
		default:
			return format.__mod__(o).toString() + suffix;
		}*/
	}
}

class FormatComplex extends FormatFunction {
	FormatFloat rFormat, iFormat;
	FormatComplex(String realFormat, String imagFormat) {
		rFormat = new FormatFloat(realFormat);
		iFormat = new FormatFloat(imagFormat, true);
	}
	String format(PyObject o) {
		// hacked this because existing routine is improper (returns 0+-0 sometimes)
		return ((PyComplex)o).toString();
		//return rFormat.format(Py.newFloat(((PyComplex)o).real)) + iFormat.format(Py.newFloat(((PyComplex)o).imag));
	}
}

class FormatObject extends FormatFunction {
	String format(PyObject o) {
		return o.toString() + " ";
	}
}


public class PyMultiarrayPrinter {

	public static String array2string(PyMultiarray a, int maxLineWidth, int precision, 
		                              boolean suppressSmall, String separator, boolean arrayOutput) {
		int [] shape = PyMultiarray.shapeOf(a);
		if (shape.length == 0) 
			// Scalar
			return a.__getitem__(Py.EmptyTuple).toString();
		int size = shape[0];
		for (int i = 1; i < shape.length; i++)
			size *= shape[i];
		if (size == 0) { 
			// Zero size array
			PyInteger [] pyShape = new PyInteger[a.dimensions.length];
			for (int i = 0; i < a.dimensions.length; i++)
				pyShape[i] = Py.newInteger(a.dimensions[i]);
			return Py.newString("zeros(%s, '%s')").__mod__(new PyTuple(
				new PyObject [] {new PyTuple(pyShape), Py.newString(a.typecode())})).toString();
		}
		// "Vanilla" array
		PyMultiarray data = PyMultiarray.reshape(a, new int [] {-1});
		char type = a.typecode();
		int itemsPerLine = shape[shape.length-1];
		int itemLength;
		FormatFunction format;
		switch (type) {
		case 'b': case '1': case 's': case 'i': case 'l': // Integer type
			PrinterFormat intFormat = _integerFormat(data);
			itemLength = intFormat.itemLength;
			format = new FormatInteger(intFormat.format);
			break;
		case 'f': case 'd': // Floating point type
			PrinterFormat floatFormat = _floatFormat(data, precision, suppressSmall, false);
			itemLength = floatFormat.itemLength;
			format = new FormatFloat(floatFormat.format);
			break;
		case 'F': case 'D': // Complex type
			PrinterFormat realFormat = _floatFormat(data.getReal(), precision, suppressSmall, false);
			PrinterFormat imagFormat = _floatFormat(data.getImag(), precision, suppressSmall, true);
			itemLength = realFormat.itemLength + imagFormat.itemLength + 3;
			format = new FormatComplex(realFormat.format, imagFormat.format);
			break;
		case 'O': // Object
			PrinterFormat objectFormat = _objectFormat(data);
			itemLength = objectFormat.itemLength;
			format = new FormatObject();
			break;
		default:
			throw Py.ValueError("unprintable array");
		}
		itemLength += separator.length();
		int lineWidth = itemLength*itemsPerLine - 1; // 1 was final_spaces
		int [] lineFormat;
		if (lineWidth > maxLineWidth) {
			int indent = (itemLength == 6) ? 8 : 6;
			int itemsFirst = (maxLineWidth+1) / itemLength;
			if (itemsFirst < 1) 
				itemsFirst = 1;
			int itemsContinuation = (maxLineWidth + 1 - indent) / itemLength;
			if (itemsContinuation < 1)
				itemsContinuation = 1;
			int lineWidth1 = itemLength*itemsFirst - 1;
			int lineWidth2 = itemLength*itemsContinuation + indent - 1;
			lineWidth = (lineWidth1 > lineWidth2) ? lineWidth1 : lineWidth2;
			int numberOfLines = 1 + (itemsPerLine - itemsFirst + itemsContinuation - 1) / itemsContinuation;
			lineFormat = new int[] {numberOfLines, itemsFirst, itemsContinuation, indent, lineWidth};
		}
		else
			lineFormat = new int[] {1, itemsPerLine, 0, 0, lineWidth};
		String rawString = _arrayToString(a, format, shape.length, lineFormat, separator, arrayOutput ? 6 : 0, false);
		if (arrayOutput)
			if (type == 'i' || type == 'd' || type == 'D')
				return "array(" + rawString + ")";
			else
				return "array(" + rawString + ",'" + type +"')";
		else
			return rawString;
	}

	static PrinterFormat _integerFormat(PyMultiarray a) {
		int maxValLength = Umath.maximum.reduce(a).toString().length();
		int minValLength = Umath.minimum.reduce(a).toString().length();
		int maxStrLength = (maxValLength > minValLength) ? maxValLength : minValLength;
		String format = "%" + maxStrLength + "d";
		return new PrinterFormat(format, maxStrLength);
	}

	static PrinterFormat _floatFormat(PyMultiarray a, int precision, boolean suppressSmall, boolean sign) {
		// Initialize values to defaults.
		boolean expFormat = false;
		// Only concerned with abs(a).
		a = (PyMultiarray)a.__abs__();
		// Find the min and max magnitudes.
		double maxVal = Py.py2double(Umath.maximum.reduce(a));
		double minVal = 0;
		if (maxVal > 0) {
			minVal = maxVal;
			for (int i = 0; i < a.__len__(); i++) {
				double temp = Py.py2double(a.get(i));
				if (temp != 0 && temp < minVal)
					minVal = temp;
			}
			if (maxVal >= 1e12)
				expFormat = true;
			if (!suppressSmall && (minVal < .0001 || maxVal / minVal > 1000.))
				expFormat = true;
		}
		if (expFormat) {
			boolean largeExp = (0 < minVal && minVal < 1e-99) || (maxVal >= 1e100);
			int maxStrLen = 8 + precision + (largeExp ? 1 : 0);
			StringBuffer format = new StringBuffer(sign ? "%+" : "%");
			format.append(maxStrLen + "." + precision + "e");
			if (largeExp)
				format.append("3");
			return new PrinterFormat(format.toString(), maxStrLen);
		}
		else {
			StringBuffer format = new StringBuffer("%." + precision + "f"); 
			int digitPrecision = 0;
			for (int i = 0; i < a.__len__(); i++) {
				int trialDp = _digits(Py.py2double(a.get(i)), precision, format.toString());
				digitPrecision = (trialDp > digitPrecision) ? trialDp : digitPrecision;
			}
			precision = (digitPrecision < precision) ? digitPrecision : precision;
			int maxStrLen = Py.newFloat(maxVal).__int__().__str__().__len__() + precision + 2;
			format = new StringBuffer(sign ? "%#+" : "%#");
			format.append(maxStrLen + "." + precision + "f");
			return new PrinterFormat(format.toString(), maxStrLen);
		}
	}

	static PrinterFormat _objectFormat(PyMultiarray a) {
		int itemLength = 0;
		for (int i = 0; i < a.__len__(); i++) {
			int trialLength = a.get(i).toString().length();
			itemLength = (itemLength > trialLength) ? itemLength : trialLength;
		}
		return new PrinterFormat("", itemLength);
	}




	static int _digits(double x, int precision, String format) {
		String s = Py.newString(format).__mod__(Py.newFloat(x)).toString();
		int zeros = s.length();
		while (s.charAt(zeros-1) == '0') zeros--;
		return precision - s.length() + zeros;
	}

	static String _arrayToString(PyMultiarray a, FormatFunction format, int rank, int [] lineFormat, 
						  String separator, int baseIndent, boolean indentFirst) {
		StringBuffer s = new StringBuffer();
		int aLength = a.__len__();
		if (rank == 1) {
			String s0 = "[";
			int items = lineFormat[1];
			int indent = indentFirst ? baseIndent : 0;
			int index = 0;
			for (int j = 0; j < lineFormat[0]; j++) {
				for (int k = 0; k < indent; k++)
					s.append(' ');
				s.append(s0);
				for (int i = 0; i < items; i++) {
					s.append(format.format(a.get(index)) + separator);
					index++;
					if (index == aLength) 
						break;
				}
				if (s.charAt(s.length()-1) == ' ')
					s.setCharAt(s.length()-1, '\n');
				else
					s.append('\n');
				items = lineFormat[2];
				indent = lineFormat[3] + baseIndent;
				s0 = "";
			}
			s.setLength(s.length()-separator.length());
			s.append("]\n");
		}
		else { // rank >= 2
			if (indentFirst)
				for (int k = 0; k < baseIndent; k++)
					s.append(" ");
			s.append('[');
			String subSep = (separator.length() > 1) ? (separator.substring(0, separator.length()-1)+'\n') : "\n";
			for (int i = 0; i < aLength-1; i++) {
				s.append(_arrayToString((PyMultiarray)a.get(i), format, rank-1, lineFormat, separator, 
					                    baseIndent+1, (i!=0)));
				s.append(subSep);
			}
			s.append(_arrayToString((PyMultiarray)a.get(aLength-1), format, rank-1, lineFormat, separator, 
				                    baseIndent+1, true));
			s.append("]\n");
		}
		s.setLength(s.length()-1);
		return s.toString();
	}

}
