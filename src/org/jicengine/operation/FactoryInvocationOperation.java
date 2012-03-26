package org.jicengine.operation;


/**
 * <p> 
 * Obtains a <code>Factory</code> instance from the context 
 * and executes it. 
 * </p>
 *
 *
 * @author timo laitinen
 */
public class FactoryInvocationOperation implements Operation {

	private String factoryName;
	private Operation[] parameters;

  /**
   * 
   * @param name  the name that the factory is stored into the context.
   * @param parameters the parameters given to the factory.
   */
	public FactoryInvocationOperation(String name, Operation[] parameters)
	{
		this.factoryName = name;
		this.parameters = parameters;
	}

	public boolean needsParameters()
	{
		return this.parameters.length > 0;
	}

	public boolean needsParameter(String name)
	{
		for (int i = 0; i < this.parameters.length; i++) {
			if( this.parameters[i].needsParameter(name) ){
				return true;
			}
		}
		return false;
	}

	public Object execute(Context context) throws OperationException
	{
		Factory factory = (Factory) context.getObject(this.factoryName);

		Object[] arguments = MethodInvocationOperation.evaluateParameters(this.parameters,context);

		Object result = factory.invoke(arguments);

		return result;
	}
}

