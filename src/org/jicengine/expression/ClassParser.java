package org.jicengine.expression;

import org.jicengine.operation.StaticValue;
import java.util.Map;
import java.util.HashMap;
/**
 *
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public class ClassParser extends StaticValueParser {

	public static final char INNERCLASS_SEPARATOR = '$';
	public static final String ARRAY_BRACKETS = "[]";

	public static final ClassParser INSTANCE = new ClassParser();

	private static final Map primitives = new HashMap();
	static {
		primitives.put("int", Integer.TYPE);
		primitives.put("double", Double.TYPE);
		primitives.put("long", Long.TYPE);
		primitives.put("float", Float.TYPE);
		primitives.put("boolean", Boolean.TYPE);
		primitives.put("char", Character.TYPE);
		primitives.put("byte", Byte.TYPE);
    // shortcut for string defined here
    primitives.put("String", String.class);
    primitives.put("short", Short.TYPE);
	}

	private static final Map primitiveArrays = new HashMap();
	static {
		primitiveArrays.put("int[]", new int[0].getClass());
		primitiveArrays.put("double[]", new double[0].getClass());
		primitiveArrays.put("long[]", new long[0].getClass());
		primitiveArrays.put("float[]", new float[0].getClass());
		primitiveArrays.put("boolean[]", new boolean[0].getClass());
		primitiveArrays.put("char[]", new char[0].getClass());
		primitiveArrays.put("byte[]", new byte[0].getClass());
    primitiveArrays.put("short[]", new short[0].getClass());
    
		//   shortcut for string arrays defined here
    primitiveArrays.put("String[]", new String[0].getClass());
	}

	public Object parseValue(String expression) throws SyntaxException
	{
		char[] chars = expression.toCharArray();
		char character;
		if( Character.isLetter(chars[0]) ){
			for (int i = 1; i < chars.length; i++) {
				character = chars[i];
				if( character == OPERATION_SEPARATOR || Character.isLetterOrDigit(character) || character == '_' ||  character == INNERCLASS_SEPARATOR ){
					// ok.
				}
				else {
					return null;
				}
			}
			try {
				return toClass(expression);
			} catch (ClassNotFoundException e){
				throw new SyntaxException(e.toString());
			}
		}
		else {
			return null;
		}
	}

	public static Class toClass(String classExpression) throws ClassNotFoundException
	{
		Class parsedClass;
		if(classExpression.endsWith(ARRAY_BRACKETS)) {
			// it is an array

			Class primitiveArray = (Class) primitiveArrays.get(classExpression);
			if( primitiveArray != null ){
				parsedClass = primitiveArray;
			}
			else {
				// any object-array
				// transform the class to the format understood by Java.
				String elementClassName = classExpression.substring(0, classExpression.lastIndexOf("[]"));
				String parsedArrayClassName = "[L" + elementClassName + ";";
				parsedClass = toClass(parsedArrayClassName);
			}
		}
		else {
			// not an array
			Class primitive = (Class) primitives.get(classExpression);
			if( primitive != null ){
				parsedClass = primitive;
			}
			else {
				ClassLoader loader = ClassLoaderResolver.getClassLoader(ClassParser.class);
				parsedClass = Class.forName(classExpression, true, loader);
			}
		}
		return parsedClass;
	}
}
