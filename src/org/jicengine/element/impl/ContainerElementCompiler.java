package org.jicengine.element.impl;

import org.jicengine.element.*;
import org.jicengine.operation.Context;
import org.jicengine.operation.Operation;
import org.jicengine.operation.PushVariableOperation;
import org.jicengine.operation.EmptyOperation;
import java.util.*;

/**
 *
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public class ContainerElementCompiler extends ElementCompiler {

	public ContainerElementCompiler(String name, Location location)
	{
		super(name, location);
	}

	/**
	 * compiles the unused value element to an action element, that
	 * doesn't actually do anything than just creates the value..
	 */
	protected ActionElement handleLooseVariableElement(VariableElement child) throws org.jicengine.element.ElementException
	{
		return new WrapperActionElement(child, child.getLocation(), new PushVariableOperation(Element.VARIABLE_NAME_ELEMENT_INSTANCE, child.getName()));
	}

}
