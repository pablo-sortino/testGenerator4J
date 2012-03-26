package org.jicengine.operation;

/**
 * An executable operation.
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 */

public interface Operation {

	/**
	 * So clients may query whether this operation needs
	 * any parameters at all.
	 */
	public boolean needsParameters();

	/**
	 * <p>
	 * So clients may query if this operation needs a particular
	 * parameter. a return value 'true' means that in order to execute this
	 * parameter, a parameter with the given name must exist in the context.
	 * </p>
	 * <p>
	 * if the parmeter in question is an optional parameter, a true must be
	 * returned.
	 * </p>
	 */
	public boolean needsParameter(String name);

	/**
	 * <p>
	 * executes the operation in a given context. objects in the context
	 * might be used in evaluation, or operation could produce
	 * more objects into the context.
	 * </p>
	 * <p>
	 * NOTE: operations must be re-executable.
	 * </p>
	 */
	public Object execute(Context context) throws OperationException;

}
