package org.jicengine.builder;
import org.jicengine.element.Type;
import org.jicengine.element.ElementCompiler;
import org.jicengine.element.ElementException;
import org.jicengine.element.Location;
/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public interface TypeManager {

	public ElementCompiler createCompiler(String elementName, Location location, Type elementType) throws ElementException;
}
