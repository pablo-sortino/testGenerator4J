package org.jicengine.element;

import org.jicengine.operation.Context;
import org.jicengine.operation.OperationException;


/**
 * <p>
 * Executable/runtime instantiation of a JIC-element. JIC-processing is based
 * on Elements.
 * </p>
 *
 * <p>
 * Design-note: getLocation() was removed: it is needed mostly internally by
 * the element when it throws exeptions. this is a public interface.
 * </p>
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public interface Element {

	/**
	 * Name of a variable that refers to the CDATA section of the element.
	 */
	public static final String VARIABLE_NAME_CDATA = "cdata";

	/**
	 * Name of a variable that refers to the instance of the parent element.
	 */
	public static final String VARIABLE_NAME_PARENT_INSTANCE = "parent";

	/**
	 * Name of a variable that refers to the instance of this element.
	 */
	public static final String VARIABLE_NAME_ELEMENT_INSTANCE = "this";

	/**
	 * The name of this element.
	 *
	 * @return String
	 */
	public String getName();

	/**
	 * The location of this element in the JIC file.
	 *
	 * @return Location
	 */
	public Location getLocation();

	/**
	 * For testing whether this element should be executed or not. The execution
	 * methods in subinterfaces VariableElement and ActionElement should be called
	 * only if this returns true.
	 *
	 * @param outerContext Context
	 * @return boolean
	 * @throws OperationException
	 */
	public boolean isExecuted(Context outerContext, Object parentInstance) throws ElementException;

}
