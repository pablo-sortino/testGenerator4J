package org.jicengine.element.impl;

import org.jicengine.operation.*;
import org.jicengine.element.VariableElement;
import org.jicengine.element.ElementException;


/**
 * <p>
 * A <code>Factory</code> that operates by executing an element in a stored
 * context.
 * </p>
 *
 *
 * @author timo laitinen
 */
public class ElementExecutionFactory implements Factory {

	private String name;
	private VariableElement result;
	private Context elementContext;
	private String[] parameterNames;
	private Class[] parameterTypes;

	public ElementExecutionFactory(String name, String[] parameterNames, Class[] parameterTypes, VariableElement result, Context elementContext)
	{
		this.name = name;
		this.parameterNames = parameterNames;
		this.parameterTypes = parameterTypes;
		this.result = result;
		this.elementContext = elementContext;
	}

	public Object invoke(Object[] arguments) throws OperationException
	{
		if( arguments.length != this.parameterNames.length ){
			throw new OperationException("Factory '" + this.name + "' requires " + this.parameterNames.length + " arguments, got " + arguments.length);
		}

		Context executionContext = new LocalContext(this.name, this.elementContext);

		for (int i = 0; i < arguments.length; i++) {
			if( !org.jicengine.operation.ReflectionUtils.isAssignableFrom(parameterTypes[i], arguments[i].getClass()) ){
				String expected = "";
				String received = "";
				for (int j = 0; j < this.parameterTypes.length; j++) {
					expected += this.parameterTypes[j].getName();
					received += arguments[j].getClass().getName();
					if( j+1 < this.parameterTypes.length ){
						expected += ", ";
						received += ", ";
					}
				}
				throw new OperationException(this.name + " expected (" + expected + "), got (" + received + ")");
			}
			executionContext.addObject(parameterNames[i],arguments[i]);
		}

		Object resultObject = null;
		try {
			resultObject = this.result.getValue(executionContext, null);
		} catch (ElementException ex) {
			throw new OperationException(ex.getMessage(),ex);
		}
		return resultObject;
	}
  
  public String toString()
  {
    return "factory '" + this.name + "'";
  }
}
