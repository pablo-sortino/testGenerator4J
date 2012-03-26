package org.jicengine.element.impl;

import org.jicengine.expression.SyntaxException;
import org.jicengine.operation.Context;
import org.jicengine.operation.Operation;
import org.jicengine.operation.OperationException;

import org.jicengine.element.*;

import java.util.*;

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

public class CollectionElementCompiler extends ElementCompiler {

  private static Operation addToCollectionOperation = new AddToCollectionOperation();
  
  public CollectionElementCompiler(String name, Location location) throws ElementException
  {
    super(name, location);
  }

  public void setInstanceClass(String className) throws ElementException
  {
    super.setInstanceClass(className);
    
    if( !Collection.class.isAssignableFrom(getElement().getInstanceClass())){
      throw new ElementException("Class '" + getElement().getInstanceClass().getName() + "' is not a Collection.",getElement().getName(), getLocation());
    }
  }
  
  protected ActionElement handleLooseVariableElement(VariableElement child) throws ElementException
  {
    return new org.jicengine.element.WrapperActionElement(child, getLocation(), addToCollectionOperation);
  }

  static class AddToCollectionOperation implements Operation {
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
      Collection parent = (Collection) org.jicengine.operation.VariableValueOperation.lookup(Element.VARIABLE_NAME_PARENT_INSTANCE, context); 
      Object elementInstance = org.jicengine.operation.VariableValueOperation.lookup(Element.VARIABLE_NAME_ELEMENT_INSTANCE, context);
      return new Boolean(parent.add(elementInstance));
    }

  };  
}
