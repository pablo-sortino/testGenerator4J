package org.jicengine.element;

import org.jicengine.operation.Context;
import org.jicengine.expression.*;

/**
 * <p>
 * VariableElements are elements that both have a value and let the clients/users
 * decide what to do with the value. The client/user of a VariableElement is typically
 * the parent element.
 * </p>
 * <p>
 * The latter aspect is an important one. A VariableElement doesn't actually know
 * what to do with the value, it can only create the value. It is the responsibility
 * of the client/user to use the value for something.
 * </p>
 * <p>
 * VariableElements are usually used for producing parameters of various operations,
 * like the constructor and action operation of an element.
 * </p>
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 */

public interface VariableElement extends Element {

	/**
	 * <p>
	 * Creates and returns the value of this element.
	 * </p>
	 * <p>
	 * The client/user of this element will call this method. currently, the method
	 * is called only once. No caching of the result is needed. However, the
	 * object should be able to be executed multiple times.
	 * </p>
	 * <p>
	 * in general, the creation of the value consists of the following steps:
	 * </p>
	 *
	 * <ol>
	 *  <li> the instance of the element is created first (with parameters)</li>
	 *  <li> if the element has other ActionElements as children, they are executed next </li>
	 *  <li> the instance is returned. </li>
	 * </ol>
	 *
	 * @param globalContext a Context where the instances marked with the 'id'-attribute
	 *                      are put.
	 *
	 * @param parentInstance the instance of the parent element. NOTE: null-value means
	 *                       that the value of the parent is not available (not yet
	 *                       or never), not that the instance of the parent is null!
	 *
	 * @return  the value of the element. the operation can return null!
	 * (although that is not currently supported by other parts ozf the framework)
	 * @throws ElementException  if any part of the execution of the action fails.
	 */
	public Object getValue(Context globalContext, Object parentInstance) throws ElementException;

   public Class getInstanceClass();  
}
