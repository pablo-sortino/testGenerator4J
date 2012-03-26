package org.jicengine.expression;

import java.util.List;
import java.util.ArrayList;
/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */
public final class Utils {
	public static char METHOD_ARGS_START = '(';
	public static char METHOD_ARGS_END = ')';


	/**
	 * 'anything.anything(something, else)' -> ['something','else'].
	 */
	public static String[] extractArguments(String expression)
	{
		return extractArguments(expression, 0, expression.length());
	}

	/**
	 *
	 * @param expression String 'anything.anything(something, else)'
	 * @param fromIndex int
	 * @param endIndex int
	 * @return String[]  {'something','else'}
	 */
	public static String[] extractArguments(String expression, int fromIndex, int endIndex)
	{
		char[] chars = expression.toCharArray();
		char character;
		int paramStart = -1;
		int paramEnd = -1;

		// find the first '('
		for (int i = fromIndex; i < endIndex; i++) {
			character = chars[i];
			if( character == METHOD_ARGS_START ){
				// got it.
				paramStart = i;
				break;
			}
		}

		// find the last ')'
		for (int i = endIndex-1; fromIndex <= i ; i--) {
			character = chars[i];
			if( character == METHOD_ARGS_END){
				paramEnd = i;
				break;
			}
		}

		if( paramStart < 0 && paramEnd < 0 ){
			return new String[0];
		}
		else {
			try {
				return parseArgumentlist(expression.substring(paramStart+1,paramEnd));
			} catch (StringIndexOutOfBoundsException e){
				throw new IllegalArgumentException("didn't find correct argument-part from string '" + expression + "' at [" + fromIndex + ".." + endIndex + "]");
			}
		}
	}

	public static String toParameterList(String[] parameters)
	{
		String list = "";
		for (int i = 0; i < parameters.length; i++) {
			list += parameters[i];
			if( i+1 < parameters.length ){
				list += ",";
			}
		}
		return list;

	}

	/**
	 *
	 * @param list String  'arg1,arg2,arg3'
	 * @return String[]   {'arg1','arg2','arg3'}
	 */
	public static String[] parseArgumentlist(String list)
	{
		if( list.indexOf("'") == -1 ){
			java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(list,",");
			String[] params = new String[tokenizer.countTokens()];
			for (int i = 0; i < params.length; i++) {
				params[i] = tokenizer.nextToken().trim();
			}
			return params;
		}
		else {
			boolean inString = false;
			java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(list,",'",true);
			List params = new ArrayList();
			String token;
			while( tokenizer.hasMoreElements() ){
				token = tokenizer.nextToken(",'");
				if( token.equals("'") ){
					try {
						token = tokenizer.nextToken("'");
						tokenizer.nextToken("'");
						params.add("'" + token + "'");
					} catch (java.util.NoSuchElementException e){
						throw new IllegalArgumentException("unclosed string in parameter-list: " + list);
					}
				}
				else if( token.equals(",") ){
					// nothing..
				}
				else {
					params.add(token.trim());
				}
			}

			return (String[]) params.toArray(new String[0]);
		}


	}
}
