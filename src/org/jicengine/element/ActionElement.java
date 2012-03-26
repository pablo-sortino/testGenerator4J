package org.jicengine.element;

import org.jicengine.operation.Context;

/**
 * <p>
 * ActionElements can execute an action on request. The request is made by
 * the client or user of the ActionElement, which is usually the parent element.
 * </p>
 * <p>
 * Typically, ActionElement modify global objects or the instance of the parent
 * element by setting the values of some properties or by calling some methods.
 * ActionElements might also call some static methods.
 * </p>
 * <p>
 * Design-note: ActionElements don't return any value. the ActionElement-interface
 * is otherwise similar to the ValueElement-interface. The two interfaces could
 * have been put under a common interface, but in that case the action should
 * have returned value. it was essential that action-elements don't return
 * a value. a null-value could have been used for signalling "no value", but
 * that approach works only if we don't use null-values otherwise..
 * </p>
 *
 * <p>
 * Elements with the 'action'-attribute are compiled into ActionElements. However,
 * also elements without the attribute may end up as ActionElements.
 * </p>
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public interface ActionElement  extends Element {

	/**
	 * <p>
	 * Executes the action of this element. The operation doesn't return any
	 * value to its caller, although it can internally produce a value.
	 * </p>
	 * <p>
	 * The client/user of this element will call this method. currently, the method
	 * is called only once. However, the action should be able to be executed
	 * multiple times.
	 * </p>
	 * <p>
	 * in general, the execution consists of the following steps:
	 * </p>
	 *
	 * <ol>
	 *  <li> if the element has an instance, it is created first (with parameters)</li>
	 *  <li> if the element has other ActionElements as children, they are executed next </li>
	 *  <li> finally, the actual action is executed (with parameters) </li>
	 * </ol>
	 *
	 * @param globalContext a Context where the instances marked with the 'id'-attribute
	 *                      are put.
	 *
	 * @param parentInstance the instance of the parent element. NOTE: null-value means
	 *                       that the value of the parent is not available (not yet
	 *                       or never), not that the instance of the parent is null!
	 *
	 * @throws ElementException  if any part of the execution of the action fails.
	 */
	public void execute(Context globalContext, Object parentInstance) throws ElementException;
}
