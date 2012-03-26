package org.jicengine.element.impl;

import org.jicengine.expression.SyntaxException;
import org.jicengine.operation.Operation;
import org.jicengine.operation.Context;
import org.jicengine.operation.OperationException;
import org.jicengine.element.*;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author    .timo
 */

public class MapCompiler extends ElementCompiler {

	public MapCompiler(String name, Location location)
	{
		super(name, location);
	}

  public void setInstanceClass(String className) throws ElementException
  {
    super.setInstanceClass(className);
    
    if( !Map.class.isAssignableFrom(getElement().getInstanceClass())){
      throw new ElementException("Class '" + getElement().getInstanceClass().getName() + "' is not a Map.",getElement().getName(), getLocation());
    }
  }  
  
	protected ActionElement handleLooseVariableElement(VariableElement child) throws org.jicengine.element.ElementException
	{
		final String childName = child.getName();

		// construct an Operation object corresponding expression:
		//   parent.put(childName,this)
		//
		Operation putToMapOperation = new Operation(){
			public boolean needsParameter(String name)
			{
				return (name.equals(Element.VARIABLE_NAME_PARENT_INSTANCE) || name.equals(Element.VARIABLE_NAME_ELEMENT_INSTANCE) );
			}
			public boolean needsParameters()
			{
				return true;
			}

			public Object execute(Context context) throws OperationException
			{
				Map parent = (Map) org.jicengine.operation.VariableValueOperation.lookup(Element.VARIABLE_NAME_PARENT_INSTANCE, context);
				Object elementInstance = org.jicengine.operation.VariableValueOperation.lookup(Element.VARIABLE_NAME_ELEMENT_INSTANCE, context);
				return parent.put(childName, elementInstance);
			}

		};

		return new WrapperActionElement(child,child.getLocation(),putToMapOperation);
	}
}
