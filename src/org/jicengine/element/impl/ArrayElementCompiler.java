package org.jicengine.element.impl;

import org.jicengine.operation.OperationException;
import org.jicengine.operation.Context;
import org.jicengine.operation.Operation;
import org.jicengine.element.*;
import java.util.*;

/**
 * NOTE: currently, every value-element is handled as an array-element. what if
 * the element has an action that would need some of the child-value-elements? .
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author    .timo
 */

public class ArrayElementCompiler extends ElementCompiler {

	/**
	 * Description of the Class
	 *
	 * @author    timo
	 */
	public class ArrayConstructor implements Operation {
		private int size = 0;

		public void increaseSize()
		{
			this.size++;
		}

		public int getSize()
		{
			return this.size;
		}

		public boolean needsParameters()
		{
			return false;
		}

		public boolean needsParameter(String name)
		{
			return false;
		}

		public Object execute(Context context) throws OperationException
		{
			Class componentType = getElement().getInstanceClass().getComponentType();
			Object arrayObject = java.lang.reflect.Array.newInstance(componentType, this.size);
			return arrayObject;
		}

		public String toString()
		{
			return "{array creator}";
		}
	}



	private int index = 0;

	public ArrayElementCompiler(String name, Location location) throws ElementException
	{
		super(name, location);

		getElement().setConstructor(new ArrayConstructor());

	}

	public void elementInitialized() throws ElementException
	{
		super.elementInitialized();
		if( !getElement().getInstanceClass().isArray() ){
			throw new ElementException("Class '" + getElement().getInstanceClass().getName() + "' is not an array.", getName(), getLocation());
		}
	}

	protected ActionElement handleLooseVariableElement(VariableElement child) throws ElementException
	{
		// new element to be stored into the array!
		ArrayConstructor constructor = (ArrayConstructor) getElement().getConstructor();

		constructor.increaseSize();
		int size = constructor.getSize();
		int index = size-1;
		Operation action = new StoreToArrayOperation(index);
		return new WrapperActionElement(child, child.getLocation(), action);
	}

	public class StoreToArrayOperation implements Operation {
		private int index;
		public StoreToArrayOperation(int index)
		{
			this.index = index;
		}
		public boolean needsParameter(String name)
		{
			return name.equals(Element.VARIABLE_NAME_PARENT_INSTANCE) || name.equals(Element.VARIABLE_NAME_ELEMENT_INSTANCE);
		}
		public boolean needsParameters()
		{
			return true;
		}

		public Object execute(Context context) throws OperationException
		{
			Object array = context.getObject(Element.VARIABLE_NAME_PARENT_INSTANCE);
			Object element = context.getObject(Element.VARIABLE_NAME_ELEMENT_INSTANCE);

			try {
				java.lang.reflect.Array.set(array, this.index, element);
			} catch (IllegalArgumentException e){
				throw new OperationException("[array-position " + this.index + "]: failed to store value '" + element + " (class '" + element.getClass().getName() + "')", e);
			}
			return null;
		}

		public String toString()
		{
			return "java.lang.reflect.Array.set(" + Element.VARIABLE_NAME_PARENT_INSTANCE + ", " + this.index + ", " + Element.VARIABLE_NAME_ELEMENT_INSTANCE + ")";
		}
	}
}
