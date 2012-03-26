package org.jicengine.operation;

/**
 * <p> </p>
 *
 * <p> </p>
 *
 * <p> </p>
 *
 * <p> </p>
 *
 * @author timo laitinen
 */
public class NegationOperation implements Operation {
	private Operation operation;
	public NegationOperation(Operation operation)
	{
		this.operation = operation;
	}

	/**
	 * <p> executes the operation in a given context.
	 *
	 * @param context Context
	 * @throws OperationException
	 * @return Object
	 */
	public Object execute(Context context) throws OperationException
	{
		Object result = this.operation.execute(context);
		if( result == null ){
			// null means true to here.
			return Boolean.TRUE;
		}
		else if( result instanceof Boolean ){
			// negate the boolean
			return new Boolean(!((Boolean)result).booleanValue());
		}
		else {
			// non-null, non-boolean means false
			return Boolean.FALSE;
		}

	}

	/**
	 * <p> So clients may query if this operation needs a particular parameter.
	 *
	 * @param name String
	 * @return boolean
	 */
	public boolean needsParameter(String name)
	{
		return this.operation.needsParameter(name);
	}

	/**
	 * So clients may query whether this operation needs any parameters at all.
	 *
	 * @return boolean
	 */
	public boolean needsParameters()
	{
		return this.operation.needsParameters();
	}
}
