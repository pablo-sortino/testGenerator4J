package org.jicengine.operation;

/**
 * <p>
 * An executable unit similar to Operation except that the Factory receives
 * unnamed arguments, whereas an Operation retrieves its parameters from the
 * context by name.
 * </p>
 *
 *
 * @author timo laitinen
 */

public interface Factory {

	public Object invoke(Object[] arguments) throws OperationException;

}
