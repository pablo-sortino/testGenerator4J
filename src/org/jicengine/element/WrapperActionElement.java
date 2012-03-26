package org.jicengine.element;

import org.jicengine.operation.SimpleContext;
import org.jicengine.operation.OperationException;
import org.jicengine.operation.LocalContext;
import org.jicengine.operation.Context;
import org.jicengine.operation.Operation;
import org.jicengine.expression.*;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>
 * WrapperActionElement adds an action to a VariableElement.
 * </p>
 * <p>
 * Useful way for example transforming VariableElements as property-setters:
 * wrap them inside a WrapperActionElement and set the
 * action-operation to the set-property.
 * </p>
 *
 * <p>
 * Design-note: currently, there's know way to give the action any parameters
 * besides the implicit 'parent' and 'this' objects and global objects.
 * (and we shouldn't create new data that is not available in the JIC-file?)
 * of course, the action-Operation can always be constructor so that it
 * won't need other parameters..
 * </p>
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author    .timo
 */

public class WrapperActionElement extends AbstractElement implements ActionElement {

	private Operation action;
	private VariableElement variableElement;

	public WrapperActionElement(VariableElement variableElement, Location location,  Operation action)
	{
		super(variableElement.getName(), location);
		this.action = action;
		this.variableElement = variableElement;
	}

	public boolean isExecuted(Context outerContext, Object parentInstance) throws ElementException
	{
		return this.variableElement.isExecuted(outerContext,parentInstance);
	}

	public void execute(Context globalContext, Object parentInstance) throws ElementException
	{
		// create the context.
		Context actionParameterContext = new SimpleContext();

		if( parentInstance != null ){
			actionParameterContext.addObject(VARIABLE_NAME_PARENT_INSTANCE, parentInstance);
		}

		// create the instance here as part of the execution
		Object instance = this.variableElement.getValue(globalContext, parentInstance);

		// add the instance to the context, if necessary.
		if( this.action.needsParameter(Element.VARIABLE_NAME_ELEMENT_INSTANCE) ){
			actionParameterContext.addObject(Element.VARIABLE_NAME_ELEMENT_INSTANCE, instance);
		}

		// NOTE: the 'parent' element should already be in the outerContext.


		// context-hierarchy: action + global.
		Context actionContext = new LocalContext(
			actionParameterContext,
			globalContext);

		try {
			this.action.execute(actionContext);
		} catch (OperationException e){
			// this way the location information is added to the exception.
			throw new ElementException("Failed to execute action " + this.action, e, getName(), getLocation());
		}
	}
}
