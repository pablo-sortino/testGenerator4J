package org.jicengine.element;

import org.jicengine.expression.Utils;

/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 */

public abstract class Type {

	String name;
	String[] parameters;
	String signature;

	protected Type(String name, String[] parameters)
	{
		this.name = name;
		this.parameters = parameters;
		this.signature = name + "/" + parameters.length;
	}

	public String getName()
	{
		return this.name;
	}

	public String getSignature()
	{
		return this.signature;
	}

	public String[] getParameters()
	{
		return this.parameters;
	}

	public static Type parse(String typeExpression)
	{
		char[] chars = typeExpression.toCharArray();
		char character;
		int paramStart = -1;

		// find first '('
		for (int i = 0; i < chars.length; i++) {
			character = chars[i];
			if( character == Utils.METHOD_ARGS_START){
				paramStart = i;
				break;
			}
		}

		String typeName;
		String[] parameters;


		if( paramStart < 0 ){
			typeName = typeExpression;
			parameters = new String[0];
		}
		else {
			typeName = typeExpression.substring(0,paramStart);
			parameters = Utils.extractArguments(typeExpression,paramStart,typeExpression.length());
		}

		return new SimpleType(typeName, parameters);
	}

	public String toString()
	{
		return this.signature;
	}
}
