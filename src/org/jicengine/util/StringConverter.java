package org.jicengine.util;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.*;

/**
 * <p>
 * Converts various kinds of String values to corresponding Java objects.
 * </p>
 *
 * @author timo laitinen
 */
public class StringConverter {

		/**
		 * <p>
		 * Accepts 4 different kinds of values:
		 * </p>
		 * <ul>
		 *   <li>RGB-value as hexadecimals: <code>0099FF</code>, etc.</li>
		 *   <li>RGB-value as integers in range 0-255: <code>0,127,255</code>, etc.</li>
		 *   <li>RGBa-value as hexadecimals: <code>0099FF66</code>, etc.</li>
		 *   <li>RGBa-value as integers in range 0-255: <code>0,127,255,90</code>, etc.</li>
		 * </ul>
		 *
		 * @param colorSpec String
		 * @return Color
		 * @throws IllegalArgumentException
		 */
		public static Color toColor(String colorSpec) throws IllegalArgumentException
	{
		try {
			int red;
			int green;
			int blue;
			int alpha = 255;

			if( colorSpec.indexOf(",") != -1 ){
				// a color as numbers
				StringTokenizer tokenizer = new StringTokenizer(colorSpec, ",");
				red = Integer.parseInt(tokenizer.nextToken());
				green = Integer.parseInt(tokenizer.nextToken());
				blue = Integer.parseInt(tokenizer.nextToken());
				if( tokenizer.hasMoreTokens()){
					alpha = Integer.parseInt(tokenizer.nextToken());
				}
			}
			else {
				// a color as a hex
				red = Integer.parseInt(colorSpec.substring(0,2),16);
				green = Integer.parseInt(colorSpec.substring(2,4),16);
				blue = Integer.parseInt(colorSpec.substring(4,6),16);
				if( colorSpec.length() == 8 ){
					alpha = Integer.parseInt(colorSpec.substring(6),16);
				}
			}
			return new Color(red, green, blue, alpha);
		} catch (Exception e){
			throw new IllegalArgumentException("Failed to parse '" + colorSpec + "' to a java.awt.Color : " + e);
		}
	}

	/**
	 *
	 * @param spec String in format <code><var>width</var> x <var>height</var></code>,
	 * where <code>width</code> and <code>height</code> are integers.
	 *
	 * @return Dimension
	 * @throws IllegalArgumentException
	 */
	public static Dimension toDimension(String spec) throws IllegalArgumentException
	{
		try {
			int separatorIndex = spec.indexOf("x");
			String width = spec.substring(0, separatorIndex).trim();
			String height = spec.substring(separatorIndex + 1).trim();
			return new java.awt.Dimension(Integer.parseInt(width), Integer.parseInt(height));
		} catch (RuntimeException e){
			throw new IllegalArgumentException("Can't parse '" + spec + "' to java.awt.Dimension. Expected format 'width x height'.");
		}
	}

	/**
	 *
	 * @param spec String in format <code>(x,y)</code>, where <code>x</code> and
	 * <code>y</code> are integers.
	 *
	 * @return corresponding Point
	 * @throws IllegalArgumentException
	 */
	public static Point toPoint(String spec) throws IllegalArgumentException
	{
		try {
			int separatorIndex = spec.indexOf(",");
			String x = spec.substring(spec.indexOf("(") + 1, separatorIndex).trim();
			String y = spec.substring(separatorIndex + 1, spec.indexOf(")")).trim();
			return new java.awt.Point(Integer.parseInt(x), Integer.parseInt(y));
		} catch (Exception e){
			throw new IllegalArgumentException("Can't parse '" + spec + "' to java.awt.Point. Expected format '(x,y)'.");
		}
	}

	/**
	 *
	 * @param spec String in format <code><var>language</var>_<var>country</var></code>
	 * or <code><var>language</var>_<var>country</var>_<var>variant</var></code>,
	 * where <code>language</code>, <code>country</code> and <code>variant</code>
	 * are specified by <code>java.util.Locale</code> class.
	 *
	 * @return Locale
	 * @throws IllegalArgumentException
	 */
	public static Locale toLocale(String spec) throws IllegalArgumentException
	{
		String[] params = spec.split("_");

		if( params.length == 2){
			return new java.util.Locale(params[0], params[1]);
		}
		else if( params.length == 3){
			return new java.util.Locale(params[0], params[1], params[2]);
		}
		else {
			throw new IllegalArgumentException("Can't parse '" + spec + "' to java.util.Locale. Expected format 'lang_country' or 'lang_country_variant'");
		}
	}
}
