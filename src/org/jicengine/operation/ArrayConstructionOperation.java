package org.jicengine.operation;


/**
 *
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

public class ArrayConstructionOperation extends InvocationOperation {

	public ArrayConstructionOperation(String signature, Operation componentType, Operation length)
	{
		super(signature, componentType, new Operation[]{length});
	}

	protected Object execute(Object componentType, Object[] arguments) throws OperationException
	{
		try {
			return constructArray((Class) componentType, arguments[0]);
		} catch (RuntimeException e){
			throw e;
		} catch (Exception e){
			throw new OperationException(e.toString(), e);
		}
	}

	/**
	 * instantiates a class.
	 */
	private Object constructArray(Class componentType, Object size) throws Exception {
		int intSize;
		if( size instanceof Integer ){
			intSize = ((Integer)size).intValue();
		}
		else {
			throw new OperationException("Can't create array, expected the size argument '" + size + "' to be an integer, was '" + size.getClass().getName() + "'");
		}

		return java.lang.reflect.Array.newInstance(componentType, intSize);
	}

}
