package org.jicengine.element.impl;

import org.jicengine.element.VariableElement;
import org.jicengine.operation.Context;
import org.jicengine.operation.DuplicateNameException;
import org.jicengine.operation.Factory;
import org.jicengine.operation.LocalContext;
import org.jicengine.operation.Operation;
import org.jicengine.operation.OperationException;

/**
 * <p>
 * Registers a Factory object i.e. stores it in the correct context with the
 * prefix 'jic::'.
 * </p>
 *
 * @author timo laitinen
 */
public class RegisterFactoryOperation implements Operation {
	private String factoryName;
	private String[] parameterNames;
	private Class[] parameterTypes;
	private VariableElement factoryElement;

  private String contextVariableName;
  
	public RegisterFactoryOperation(String factoryName, String[] parameterNames, Class[] parameterTypes, String contextVariableName)
	{
		this.factoryName = factoryName;
		this.parameterNames = parameterNames;
		this.parameterTypes = parameterTypes;
    this.contextVariableName = contextVariableName;
	}
  
	public void setFactoryElement(VariableElement factoryElement)
	{
		this.factoryElement = factoryElement;
	}

	public VariableElement getFactoryElement()
	{
		return this.factoryElement;
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
		// we got the action context of the current element as a parameter

    // we want the element context of the parent element.
		// (why?)
		Context elementContext = ((LocalContext)context).getParent();

		// the factory-element is executed in a replica of the element context
		// (because the original element context may get modified before the
		// factory is used)
		Context executionContext = elementContext.replicate();

		// the factory is stored into the element context of the parent
		Context parentElementContext = ((LocalContext)elementContext).getParent();


		Factory factory = new ElementExecutionFactory(this.factoryName, this.parameterNames, this.parameterTypes, getFactoryElement(), executionContext);

		// store the factory to the root-context:
    try {
      parentElementContext.addObject(this.contextVariableName, factory);
    } catch (DuplicateNameException e){
      throw new OperationException("Duplicate factory definition: '" + this.factoryName + "' ('" + this.contextVariableName + "')");
    }

		return null;
	}
}