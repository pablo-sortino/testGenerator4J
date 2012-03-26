package org.jicengine.element;


import org.jicengine.operation.*;
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

public class StaticValueElement extends AbstractElement implements VariableElement {
	private Object value;

	public StaticValueElement(String name, Location location, Object value)
	{
		super(name, location);
		this.value = value;
	}

	public Object getValue(Context context, Object parentInstance)
	{
		return this.value;
	}

	public boolean isExecuted(Context context, Object parentInstance)
	{
		return true;
	}
  
  public Class getInstanceClass()
  {
    return this.value.getClass();
  }
}
