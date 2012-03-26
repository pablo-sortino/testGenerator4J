package org.jicengine.operation;

/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public class PushVariableOperation implements Operation {

	String localContextName;
	String parentContextName;

	public PushVariableOperation(String localContextName, String parentContextName)
	{
		this.parentContextName = parentContextName;
		this.localContextName = localContextName;
	}

	public boolean needsParameters()
	{
		return true;
	}

	public boolean needsParameter(String name)
	{
		return (this.localContextName.equals(name));
	}

	public Object execute(Context context) throws OperationException
	{
		Context parentContext;
		try {
			parentContext = ((LocalContext)context).getParent();
		} catch (ClassCastException e){
			throw new RuntimeException("Expected " + LocalContext.class.getName() + ", was " + context.getClass().getName());
		}

		try {
			Object value = context.getObject(this.localContextName);

			parentContext.addObject(this.parentContextName, value);
			return null;
		} catch (ObjectNotFoundException e){
			throw new OperationException(e.getMessage(), e);
		} catch (DuplicateNameException e2){
			throw new OperationException(e2.getMessage(), e2);
		}
	}
}
