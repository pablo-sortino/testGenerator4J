package org.jicengine.builder;

import org.jicengine.operation.ReflectionUtils;

import org.jicengine.element.*;

import java.lang.reflect.InvocationTargetException;
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

public class TypeManagerImpl implements TypeManager {

	private Map typeNamesToClassesMap;

	public TypeManagerImpl(Map elements)
	{
		this.typeNamesToClassesMap = elements;
	}

	public ElementCompiler createCompiler(String elementName, Location location, Type elementType) throws ElementException {
		Class elementClass = (Class) this.typeNamesToClassesMap.get(elementType.getName());
		if( elementClass == null ){
			throw new AttributeException("Type named '" + elementType.getName() + "' not supported. use " + this.typeNamesToClassesMap.keySet(), elementName, location);
		}

		String[] typeParameters = elementType.getParameters();

		Object[] parameters;
		if( typeParameters.length > 0  ){
			parameters = new Object[3];
			parameters[0] = elementName;
			parameters[1] = location;
			parameters[2] = typeParameters;
		}
		else {
			parameters = new Object[2];
			parameters[0] = elementName;
			parameters[1] = location;
		}

		try {
			ElementCompiler element = (ElementCompiler) org.jicengine.operation.ReflectionUtils.instantiate(elementClass, parameters);
			return element;
    } catch (InvocationTargetException e1){
      Throwable targetException = e1.getTargetException();
      if( targetException instanceof ElementException){
        throw (ElementException) targetException;
      }
      else {
        throw new RuntimeException("Failed to create element of type " + elementType.getSignature(),e1);        
      }
		} catch (Exception e2){
			throw new RuntimeException("Failed to create element of type " + elementType.getSignature(),e2);
		}
	}
}
