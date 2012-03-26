package org.jicengine.element.impl;

import org.jicengine.operation.OperationException;
import org.jicengine.operation.Context;
import org.jicengine.operation.Operation;
import org.jicengine.element.*;

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

public class BeanElementCompiler extends ElementCompiler {

	public BeanElementCompiler(String name, Location location)
	{
		super(name, location);
	}

	/**
	 * transforms the child into a property setter action..
	 */
	protected ActionElement handleLooseVariableElement(final VariableElement child) throws ElementException
	{
		if( this.getElement().getConstructor() == null){
			// the element has no constructor
			// -> the element has no instance
			// -> we can't set a property without a bean instance.
			throw new ElementException("Unused child element: <" + child.getName() + ">", getName(), getLocation());
		}
		try {
			Operation setPropertyAction = org.jicengine.expression.LJEParser.getInstance().parse(getSetPropertyActionExpression(child.getName()) );

			return new WrapperActionElement(child, child.getLocation(), setPropertyAction);

		} catch (org.jicengine.expression.SyntaxException e){
			throw new ElementException(e,getName(), getLocation());
		}
	}

	/**
	 * 'childElement' -> parent.setChildElement(this)
	 */
	public static String getSetPropertyActionExpression(String propertyName)
	{
		return Element.VARIABLE_NAME_PARENT_INSTANCE + ".set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1) + "(" + Element.VARIABLE_NAME_ELEMENT_INSTANCE + ")";
	}
}
