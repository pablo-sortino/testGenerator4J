package org.jicengine.element.impl;

import org.jicengine.expression.SyntaxException;
import org.jicengine.operation.Context;
import org.jicengine.operation.Operation;

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

public class ListElementCompiler extends CollectionElementCompiler {

	public ListElementCompiler(String name, Location location) throws ElementException
	{
		super(name, location);
	}

  /*
  public void elementInitialized() throws ElementException
  {
    super.elementInitialized();

    if( getConstructor() == null ){
      setConstructor("new java.util.ArrayList()");
    }
  } 
  */ 
  
  public void setInstanceClass(String className) throws ElementException
  {
    super.setInstanceClass(className);
    
    if( !List.class.isAssignableFrom(getElement().getInstanceClass())){
      throw new ElementException("Class '" + getElement().getInstanceClass().getName() + "' is not a List.",getElement().getName(), getLocation());
    }
  }
}
