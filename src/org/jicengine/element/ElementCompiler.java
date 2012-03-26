package org.jicengine.element;

import org.jicengine.operation.StaticValue;
import org.jicengine.operation.OperationException;
import org.jicengine.operation.VariableValueOperation;
import org.jicengine.operation.Operation;
import org.jicengine.operation.EmptyOperation;
import org.jicengine.expression.LJEParser;
import org.jicengine.expression.ClassParser;
import org.jicengine.expression.SyntaxException;
import java.util.*;

/**
 * <p>
 * A class that makes it possible to create Element-objects little by little, as
 * is needed if the JIC-file is parsed with a SAX-parser.
 * </p>
 * <p>
 * ElementCompiler parses String-typed attributes and other data to corresponding
 * objects. After all the data of an XML-element has been processed, the resulting
 * runtime-Element can be obtained with method <code>createElement()</code>.
 * </p>
 * <p>
 *
 * </p>
 *
 * <h4> Element Life cycle </h4>
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author    .timo
 */

public abstract class ElementCompiler {

	public static final String ATTR_NAME_ACTION = "action";
	public static final String ATTR_NAME_CLASS = "class";
	public static final String ATTR_NAME_VARIABLES = "vars";
	public static final String ATTR_NAME_CONSTRUCTOR_ARGUMENTS = "args";
	public static final String ATTR_NAME_TRACE = "trace";
	public static final String ATTR_NAME_TYPE = "type";
	public static final String ATTR_NAME_INSTANCE = "instance";
	public static final String ATTR_NAME_OVERRIDABLE_BY = "overridable-by";
	public static final String ATTR_NAME_IF = "if";

	public ElementCompiler()
	{
	}

	private ElementImpl element;
	private String constructorArguments;
	private boolean constructorDerivedFromClassInformation = false;


	public ElementCompiler(String name, Location location)
	{
		this.element = new ElementImpl(name, location);
	}

  public Element createElement() throws ElementException
  {
    return getElement().toRuntimeElement();
  }  
  
	public String getName()
	{
		return getElement().getName();
	}

	public Location getLocation()
	{
		return getElement().getLocation();
	}

	public void setOverridableBy(String overridingId)
	{
		getElement().setOverridableBy(overridingId);
	}

	public void setIf(String condition) throws ElementException
	{
		try {
			getElement().setIf(LJEParser.getInstance().parse(condition));
		} catch (SyntaxException e){
			throw new AttributeException("[if=\"" + condition + "\"]: " + e.getMessage(), e, getName(), getLocation());
		}
	}


	/**
	 *
	 */
	public void setVariables(String variableExpression) throws ElementException
	{
		StringTokenizer tokenizer = new StringTokenizer(variableExpression, ",");
		String[] variableNames = new String[tokenizer.countTokens()];
		int i = 0;
		while(tokenizer.hasMoreElements() ){
			variableNames[i] = tokenizer.nextToken().trim();
			i++;
		}
		setVariables(variableNames);
	}

	public void setVariables(String[] variableNames) throws ElementException
	{
		getElement().setVariableNames(variableNames);
	}

	public void setConstructorArguments(String argumentExpression)
	{
		this.constructorArguments = argumentExpression;
	}

	public void setConstructor(String expression) throws ElementException
	{
		try {
			getElement().setConstructor(LJEParser.getInstance().parse(expression));
		} catch (SyntaxException e){
			throw new AttributeException("[instance=\"" + expression + "\"]: " + e.getMessage(), e, getName(), getLocation());
		}
	}

	public Operation getConstructor()
	{
		return getElement().getConstructor();
	}

	public void setInstanceClass(String className) throws ElementException
	{
		try {
			getElement().setInstanceClass(ClassParser.INSTANCE.toClass(className));
		} catch(Exception e) {
			throw new AttributeException("[class=\"" + className + "\"]: " + e.getMessage(),e, getName(), getLocation());
		}
	}

	/**
	 * <p>
	 * for setting the action as an expression.
	 * </p>
	 * <p>
	 * enhancements:
	 * </p>
	 * <ul>
	 *  <li>empty string is mapped into an EmptyOperation</li>
	 *  <li>
	 *    expressions of type <code>setMethod(value)</code> and
	 *  <code>add(component)</code> are automatically madded to expressions
	 *  <code>parent.setMethod(value)</code> and
	 *  <code>parent.add(component)</code>.
	 *    </li>
	 * </ul>
	 */
	public void setAction(String expression) throws ElementException
	{
		if(expression.length() == 0) {
			getElement().setAction(EmptyOperation.INSTANCE);
		}
		else {
			String preparedExpression;
			int paramStart = expression.indexOf("(");
			int commaIndex = -1;

			if( paramStart != -1 ){
				commaIndex = expression.substring(0,paramStart).indexOf(".");
			}

			// the existence of '(' implies that the action is indeed a method call
			// (what else?)
			// the lack of '.' implies that the actor of the method call is missing.
			// therefore we add the implicit actor parent into the expression.
			if(paramStart != -1 && commaIndex == -1) {
				preparedExpression = Element.VARIABLE_NAME_PARENT_INSTANCE + "." + expression;
			}
			else {
				preparedExpression = expression;
			}

			try {
				getElement().setAction(LJEParser.getInstance().parse(preparedExpression));
			} catch (SyntaxException e){
				throw new AttributeException("[action=\"" + expression + "\"]: " + e.getMessage(), e, getName(), getLocation());
			}
		}
	}


	/**
	 * if this element has a constructor, the cdata is added to the element-context
	 * with the name 'cdata'. (cdata might be ignored if the constructor doesn't
	 * use cdata) if there is no constructor, cdata becomes the value of this
	 * element.
	 *
	 * @param cdata              Description of the Parameter
   * @param syntaxBasedCdataConversionsSupported
	 * @throws ElementException  Description of the Exception
	 */
	public void setCData(String cdata, boolean syntaxBasedCdataConversionsSupported) throws ElementException
	{
		if( getElement().getConstructor() == null || this.constructorDerivedFromClassInformation ){
			// there is no real constructor yet.
			// in this case the element instance is derived from the CDATA.
			// obtain a constructor that converts the CDATA-string into the required
			// object.
      
      if( getElement().getInstanceClass() == null ){
        // the class needs to be specified.
        
        Class instanceClass;
        
        if( syntaxBasedCdataConversionsSupported){
          // we therefore resolve the class by examining the syntax of the CDATA
          instanceClass = CdataHandler.resolveInstanceClassFromCdata(cdata);
        }
        else {
          // JICE 2.0 behaviour:  
          instanceClass = String.class;
        }
        
        // setting the instance class is enough:
        // the CDATA will now be handled as a normal
        // CDATA conversion.
        getElement().setInstanceClass(instanceClass);
      }

			Operation constructor;
			try {
				constructor = CdataHandler.getClassBasedCdataConversionConstructor(getElement().getInstanceClass(), cdata);
			} catch (Exception ex) {
				throw new ElementException(ex, getName(), getLocation());
			}
			getElement().deleteConstructor();
			getElement().setConstructor(constructor);

			// note: we don't save the CDATA anywhere here. we assume that
			// the constructor obtained from CdataHandler has stored the CDATA.
     
		}
		else {
			// a constructor has been specified explicitly.

			if(	getElement().isConstructorVariable(Element.VARIABLE_NAME_CDATA) ){
				// ok, the constructor consumes the CDATA.

				// we create a virtual element that makes it possible to handle the
				// CDATA like any other child element..
				VariableElement virtualElement = new StaticValueElement(Element.VARIABLE_NAME_CDATA, getLocation(), cdata);
				handleChildElement(virtualElement);
			}
			else {
				// the constructor doesn't use the CDATA. It is therefore illegal!
				throw new ElementException("Illegal CDATA: CDATA must be used in the constructor (variable 'cdata').", getName(), getLocation());
			}
		}
	}

  /**
   * <p>
   * Called by handler when the start-tag of the element has been processed -
   * Element has been created and all attributes have been set, but no CDATA nor
   * child-elements have been processed.
   * </p>
   * <p>
   * This is a good spot for verifying that
   * the state of the element is valid - the attributes have been used properly,
   * attributes not set by user can be set to their default values, etc.
   * </p>
   *
   * @throws ElementException  Description of the Exception
   */
  public void elementInitialized() throws ElementException
  {
    // do some validity checks concerning the use of the 'args' attribute.
    if(this.constructorArguments != null) {
      if(getElement().getInstanceClass() == null) {
        throw new AttributeException("Attribute '" + ATTR_NAME_CONSTRUCTOR_ARGUMENTS + "' must be specified together with attribute '" + ATTR_NAME_CLASS + "'", getName(), getLocation());
      }
      if(getElement().getConstructor() != null) {
        throw new AttributeException("Attributes '" + ATTR_NAME_INSTANCE + "' and '" + ATTR_NAME_CONSTRUCTOR_ARGUMENTS + "' can't be used together", getName(), getLocation());
      }
    }

    if( getElement().getConstructor() != null ){
      // the element has a constructor
      // -> it has an object
      // -> the class of object must be specified!
      if( getElement().getInstanceClass() == null ){
        throw new AttributeException("The attribute '" + ATTR_NAME_CLASS + "' must be specified.", getName(), getLocation());
      }
    }


    deriveConstructorFromClassInformation();
  }  
  
	/**
	 * Used for notifying this element about a child-element
	 *
	 * @param child              a child with no action, can have a value or not.
	 * @throws ElementException  Description of the Exception
	 */
	public void handleChildElement(Element child) throws ElementException
	{
		if( child instanceof VariableElement && !getElement().isUsed((VariableElement)child) ){
			// this value element has no use.. yet.
			// we let the subclasses to decide the purpose of this child.
			//
			ActionElement actionElement = handleLooseVariableElement((VariableElement)child);

			// now the child is an action element and we can add it.
			getElement().addChildElement(actionElement);
		}
		else {
			getElement().addChildElement(child);
		}
	}

  protected ElementImpl getElement()
  {
    return this.element;
  }  
  
	/**
	 *
	 * for subclasses!
	 */
	protected abstract ActionElement handleLooseVariableElement(VariableElement child) throws ElementException;



	public String toString()
	{
		return "<" + getName() + ">";
	}

	/**
	 * <p>
	 * create the implicit constructor from 'class' and 'args' attributes,
	 * if possible. if the element already has a constructor, nothing is done.
	 * </p>
	 *
	 *
	 * @throws ElementException  Description of the Exception
	 */
	private void deriveConstructorFromClassInformation() throws ElementException
	{
		if( getElement().getConstructor() == null && getElement().getInstanceClass() != null ){
			// we use the instanceClass-information for constructing the implicit constructor.

			String constructorExpression;
			if(this.constructorArguments == null) {
				// empty constructor
				constructorExpression = "new " + getElement().getInstanceClass().getName() + "()";
				// we mark that the constructor wasn't explicitly set in the code
				// but was an implicit constructor derived from the attributes.
				// we need this information when handling cdata..
				this.constructorDerivedFromClassInformation = true;
			}
			else {
				// we have some parameters.
				constructorExpression = "new " + getElement().getInstanceClass().getName() + "(" + this.constructorArguments + ")";
				this.constructorDerivedFromClassInformation = false;
			}

			// TODO: don't create a string to be parsed - create a ready Operation-object

			try {
				setConstructor(constructorExpression);

			} catch (ElementException e){
				throw new ElementException("Problems creating implicit constructor.", e, getName(), getLocation());
			}
		}
	}

}
