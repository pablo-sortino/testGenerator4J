package org.jicengine.element;

import java.util.ArrayList;
import java.util.List;

import org.jicengine.operation.Context;
import org.jicengine.operation.LocalContext;
import org.jicengine.operation.Operation;
import org.jicengine.operation.OperationException;

/**
 * <p>
 *
 * ElementImpl encapsulates the common properties and behaviour of an element.
 * </p> <h4> Common Element Structure </h4> <p>
 *
 * An Element consists of: </p>
 * <ul>
 *   <li> max 1 constructor together with * VariableElement-children as constructor
 *   parameters. </li>
 *   <li> * ActionElement-children </li>
 *   <li> max 1 action together with * VariableElement-children as action
 *   parameters. </li>
 * </ul>
 * <h4> Common processing procedures </h4>
 *
 * <p>
 * ElementImpl defines the common procedures for processing these
 * sub-components. The details of the processing can be tricky - an Element
 * might or might not have a constructor, it might or might not have an element,
 * etc.
 * </p>
 *
 * <h4> Lack of the exact runtime-behaviour </h4>
 * <p>
 * the runtime-behaviour of an Element is defined by interfaces VariableElement and
 * ActionElement. Whether an Element becomes an VariableElement or an ActionElement
 * depends on the state of the Element.
 * </p>
 * <p>
 *
 * ElementImpl can be initialized little-by-little, it is designed to be used
 * together with ElementCompiler. because of this approach, the
 * runtime-behaviour of the Element is known only when the Element is completely
 * initialized.
 * </p> <p>
 *
 * After an ElementImpl-object is completely initialized, its runtime-version
 * (VariableElement or ActionElement) can be obtained with method
 * toRuntimeElement(). </p>
 *
 * <h4> id binding </h4>
 * <p>
 * the object is added to the global-context only after it is created. this
 * way, the child-elements that are constructor parameters are processed before
 * the element has used its id-attribute. therefore, if a child has a same
 * id than the parent, the duplicate-id-exceptions will blame the wrong
 * element for the id - the parent.
 * </p>
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author    .timo
 */

public class ElementImpl extends AbstractElement {

	/**
	 * VariableElement-version of an ElementImpl.
	 *
	 * @author    timo
	 */
	public class VariableElementImpl implements VariableElement {
		public String getName()
		{
			return ElementImpl.this.getName();
		}

		public Location getLocation()
		{
				return ElementImpl.this.getLocation();
		}

		public boolean isExecuted(Context outerContext, Object parentInstance) throws ElementException
		{
			return ElementImpl.this.isExecuted(outerContext,parentInstance);
		}


		public Object getValue(Context context, Object parentInstance) throws ElementException
		{
			return ElementImpl.this.execute(context, parentInstance);
		}
    
    public Class getInstanceClass()
    {
      return ElementImpl.this.getInstanceClass();
    }

		public String toString()
		{
			return "<" + getName() + ">";
		}
	}

	/**
	 * ActionElement-version of an ElementImpl.
	 *
	 * @author    timo
	 */
	public class ActionElementImpl implements ActionElement {
		public String getName()
		{
			return ElementImpl.this.getName();
		}

		public Location getLocation()
		{
			return ElementImpl.this.getLocation();
		}

		public boolean isExecuted(Context outerContext, Object parentInstance) throws ElementException
		{
			return ElementImpl.this.isExecuted(outerContext,parentInstance);
		}

		public void execute(Context context, Object parentInstance) throws ElementException
		{
			ElementImpl.this.execute(context, parentInstance);
		}

		public String toString()
		{
				return "<" + getName() + ">";
		}
	}


	protected static final int CHILD_TYPE_CONSTRUCTOR_ARG = 0;
	protected static final int CHILD_TYPE_ACTION_ARG = 1;
	protected static final int CHILD_TYPE_ACTION = 2;
	protected static final int CHILD_TYPE_VARIABLE = 3;

	private Operation condition;

	private String[] variableNames;

	private Operation constructor;
	private Class instanceClass;

	private String overridableBy;

	private List childElements = new ArrayList();
	private List childElementTypes = new ArrayList();
	private int lastConstructorArgumentIndex = -1;

	private Operation action;

	public ElementImpl(String name, Location location)
	{
		super(name, location);
	}

	public void setConstructor(Operation operation) throws ElementException
	{
		if( this.constructor != null ){
			throw new ElementException("Constructor already set to '" + this.constructor + "', new constructor '" + operation + "' not accepted.",  getName(), getLocation());
		}
		this.constructor = operation;
	}

	/**
	 * Removes a pre-set constructor so that a new one can be set with
	 * <code>setConstructor</code>.
	 */
	public void deleteConstructor()
	{
		this.constructor = null;
	}

	public void setOverridableBy(String overridingId)
	{
		this.overridableBy = overridingId;
	}

	/**
	 *
	 * @throws ElementException if variables were already set.
	 */
	public void setVariableNames(String[] elementNames) throws ElementException
	{
		if( this.variableNames != null ){
			throw new ElementException("Variables already set.",  getName(), getLocation());
		}
		this.variableNames = elementNames;
	}

	public void setInstanceClass(Class instanceClass) throws ElementException
	{
		if( this.instanceClass != null ){
			throw new ElementException("Class already set to '" + this.instanceClass + "', new class '" + instanceClass + "' not accepted.",  getName(), getLocation());
		}
		this.instanceClass = instanceClass;
	}

	public void setAction(Operation operation) throws ElementException
	{
		if( this.action != null ){
			throw new ElementException("Action already set to '" + this.action + "', new action '" + operation + "' not accepted.",  getName(), getLocation());
		}
		this.action = operation;
	}

	public void setIf(Operation condition) throws ElementException
	{
		if( this.condition != null ){
			throw new ElementException("If condition already set to '" + this.condition + "'", getName(), getLocation());
		}
		this.condition = condition;
	}

	public Operation getIf()
	{
		return this.condition;
	}

	public Operation getConstructor()
	{
		return this.constructor;
	}

	public Class getInstanceClass()
	{
		return this.instanceClass;
	}

	public Operation getAction()
	{
		return this.action;
	}

	protected boolean isOverridden(Context context)
	{
		//return (this.overridableBy != null && findBuildContextFrom(context).getParameters().containsKey(this.overridableBy));
		return (this.overridableBy != null && context.hasObject(org.jicengine.expression.BuildParameterParser.PREFIX + this.overridableBy));
	}

	public boolean isElementVariable(String elementName)
	{
		if( this.variableNames != null ){
			for (int i = 0; i < this.variableNames.length; i++) {
				if( this.variableNames[i] != null && this.variableNames[i].equals(elementName) ){
					// match
					return true;
				}
			}
		}
		// no variables or no match.
		return false;
	}

	public boolean isConstructorVariable(String elementName)
	{
		return getConstructor() != null && getConstructor().needsParameter(elementName);
	}

	public boolean isActionVariable(String elementName)
	{
		return getAction() != null && getAction().needsParameter(elementName);
	}

	/**
	 * stores the information that a variable has been found and published to
	 * the context. this way we may later validate that all the necessary variables
	 * have been found.
	 *
	 * @param name String
	 */
	protected void variablePublished(String name)
	{
		if( this.variableNames != null ){
			for (int i = 0; i < this.variableNames.length; i++) {
				if( this.variableNames[i] != null && this.variableNames[i].equals(name) ){
					// match
					this.variableNames[i] = null;
				}
			}
		}
	}

	protected void verifyVariablesHaveBeenFound() throws ElementException
	{
		if( this.variableNames != null ){
			for (int i = 0; i < this.variableNames.length; i++) {
				if( this.variableNames[i] != null ){
					throw new ElementException("Element variable '" + this.variableNames[i] + "' not found.", getName(), "vars", getLocation());
				}
	 }
		}
	}

	/**
	 * @param context      the global context.
	 * @return                   null if this element can't be overridden at all or
	 *      is not overridden currently (overriding object not found).
	 * @throws ElementException  Description of the Exception
	 */
	private Object getOverridingObject(Context context) throws ElementException
	{
		Object overridingObject = null;

		if( isOverridden(context) ) {
			// this element is overridden..
			try {
				overridingObject = context.getObject(org.jicengine.expression.BuildParameterParser.PREFIX + this.overridableBy);
			} catch (org.jicengine.operation.ObjectNotFoundException e){
				throw new RuntimeException("Build parameter not found although it should", e);
			}
			// validate it
			validateInstance(overridingObject, true);
		}
		return overridingObject;
	}

	/**
	 * <p>
	 *
	 * Returns the runtime-version of this Element. Call this method after the
	 * Element is completely initialized. </p>
	 *
	 * @return                   either an instance of VariableElement or
	 *      ActionElement.
	 * @throws ElementException  Description of the Exception
	 */
	public Element toRuntimeElement() throws ElementException
	{
		if(this.action != null) {
			return new ActionElementImpl();
		}
		else if(this.constructor != null || this.overridableBy != null) {
			return new VariableElementImpl();
		}
		else {
			// no action and no value?
			throw new ElementException("Element without action nor value is not allowed.", getName(), getLocation());
		}
	}

	public String toString()
	{
		return "<" + getName() + ">";
	}

	/**
	 * @return true  if the child element is needed i.e. used by either the action
	 * or the constructor.
	 */
	public boolean isUsed(VariableElement child)
	{
		String name = child.getName();
		return isConstructorVariable(name) || isActionVariable(name) || isElementVariable(name);
	}

	/**
	 * adds a child element into this element.
	 *
	 * @throws ElementException if the child is a VariableElement that is not needed
	 * by the action nor the constructor. unsused child elements without a purpose
	 * (without action) are not allowed because.. their value would be created
	 * and the result would be ignored.
	 */
	public void addChildElement(Element child) throws ElementException
	{
		if( child instanceof ActionElement ){
			// ok.
			addChildElement(child, CHILD_TYPE_ACTION);
		}
		else if( child instanceof VariableElement ){
			VariableElement valueChild = (VariableElement) child;
			String childName = valueChild.getName();

			// the child may match only a single case.

			if( isElementVariable(childName) ){
				// this is a variable
				addChildElement(child, CHILD_TYPE_VARIABLE);
			}
			else if( isConstructorVariable(childName) ){
				// this is a constructor parameter.
				addChildElement(child, CHILD_TYPE_CONSTRUCTOR_ARG);
			}
			else if( isActionVariable(childName) ){
				// this is a parameter of the action
				addChildElement(child, CHILD_TYPE_ACTION_ARG);
			}
			else {
				// unused value element. we won't allow those here!
				throw new ElementException("The element " + child + " has no use.", child.getName(), child.getLocation());
			}
		}
		else {
			throw new RuntimeException("unknown child-element type: '" + child.getClass().getName() + "'");
		}
	}

	private void addChildElement(Element child, int type)
	{
		this.childElements.add(child);
		this.childElementTypes.add(new Integer(type));

		// check whether this child is needed in the constructor
		if( type == CHILD_TYPE_CONSTRUCTOR_ARG ||
			(type == CHILD_TYPE_VARIABLE && getConstructor() != null && getConstructor().needsParameter(child.getName()) )
			){
			// this is the currently last index.

			this.lastConstructorArgumentIndex = this.childElements.size()-1;
		}
	}

	/**
	 * <p>
	 * returns the instance of this element: by creating it with the constructor or
	 * by obtaining the overriding object.</p>
	 *
	 *
	 * @param constructorContext the context where the constructor is executed.
	 * @return                   Description of the Return Value
	 * @throws ElementException  Description of the Exception
	 */
	private Object obtainElementInstance(Context constructorContext, boolean isOverridden) throws ElementException
	{
		Object instance;
		if(isOverridden) {
			// we can find the overriding object also from the constructor context..
			instance = getOverridingObject(constructorContext);
		}
		else if(this.constructor != null) {
			instance = executeConstructor(constructorContext);
		}
		else if(this.overridableBy != null) {
			//
			throw new ElementException("Required overriding '" + this.overridableBy + "' not found.", getName(), getLocation());
		}
		else {
			throw new IllegalStateException("No constructor and no overriding - no instance available (" + getName() + " at " + getLocation() + ")");
		}

		return instance;
	}

	/**
	 * @param actionContext      Description of the Parameter
	 * @throws ElementException  Description of the Exception
	 */
	private void executeAction(Context actionContext) throws ElementException
	{
		if(this.action == null) {
			throw new IllegalStateException("Action is null");
		}

		try {
			this.action.execute(actionContext);
		} catch(OperationException e) {
			throw new ElementException(e, getName(), ElementCompiler.ATTR_NAME_ACTION,  getLocation());
		}
	}

	/**
	 * executes the constructor and returns the resulting object. the returned
	 * object has been validated (with <code>validateInstance</code>).
	 *
	 * @param constructorContext      The context where the constructor is executed.
	 * @return                        The freshly created instance of this element.
	 * @throws ElementException       Description of the Exception
	 * @throws IllegalStateException  if this method was called although there is
	 * no constructor.
	 */
	private Object executeConstructor(Context constructorContext) throws ElementException
	{
		if(this.constructor == null) {
			throw new IllegalStateException("executeConstructor() called although constructor is null. (" + getName() + " at " + getLocation() + ")");
		}

		// now we can execute the constructor
		Object instance = null;
		try {
			instance = this.constructor.execute(constructorContext);
		} catch(OperationException e) {
			throw new ElementException(e, getName(), "instance", getLocation());
		}

		validateInstance(instance, false);
		return instance;
	}

	/**
	 * validates the instance. an exception is thrown, if the instance is not
	 * valid.
	 *
	 * currently, only the class of the instance is validated against the
	 * 'instanceClass'-property.
	 *
	 * @param isOverridingObject  for more better error messages..
	 *
	 * @throws Exception if the instance is not valid. since this method doesn't
	 * know where the instance came from, the caller must catch this exception
	 * and throw a new exception with a better error message.
	 */
	protected void validateInstance(Object instance, boolean isOverridingObject) throws ElementException
	{
		if( instance == null ){
			throw new ElementException("Element instance is null - null values not allowed.", getName(), getLocation());
		}

		if( this.instanceClass != null && !org.jicengine.operation.ReflectionUtils.isAssignableFrom(this.instanceClass, instance.getClass()) ){
			if( isOverridingObject ){
				throw new ElementException("The overriding object '" + this.overridableBy + "' was of type '" + instance.getClass().getName() + "', expected '" + this.instanceClass.getName() + "'", getName(), getLocation());
			}
			else {
				throw new ElementException("Expected the instance to be of type '" + this.instanceClass.getName() + "', was '" + instance.getClass().getName() + "'", getName(), getLocation());
			}
		}
	}

	public boolean isExecuted(Context outerContext, Object parentInstance) throws ElementException
	{
		if( this.condition == null ){
			return true;
		}
		else {
			Context ifContext = new LocalContext("<" + getName() + ">//if", outerContext);
			if( parentInstance != null ){
				// add the parent to the context
				ifContext.addObject(Element.VARIABLE_NAME_PARENT_INSTANCE, parentInstance);
			}

			try {
				Object result = this.condition.execute(ifContext);
				return toBoolean(result);
			} catch (OperationException e){
				throw new ElementException(e, getName(), "if", getLocation());
			}
		}
	}

	private boolean toBoolean(Object result)
	{
		if( result == null ){
			return false;
		}
		else if( result instanceof Boolean){
			return ((Boolean)result).booleanValue();
		}
		else {
			// some unknown object type..
			// interpreted as 'true' since it is not null..
			return true;
		}
	}

	/**
	 * <p>
	 * executes this element. this includes:
	 * </p>
	 * <ul>
	 *  <li> processing of all child elements, both value and action </li>
	 *  <li> executing the constructor (if there is one)</li>
	 *  <li> validating the instace (if there is one) </li>
	 *  <li> handling the overriding-issues: instance not created and only
	 *  action-parameter child-elements are executed if this element is overridden.</li>
	 *  <li> handling the id-attribute: the instance is added to the context, if
	 *  necessary.
	 *  <li> executing the action (if there is one) </li>
	 * </ul>
	 *
	 * @return  the instance of this element, if any, or null. the caller of
	 * this method should know whether the returned value is usable or not.
	 * if this element has an action, the returned value should not be used!!
	 * and note: the returned value won't be the result of the action. it will
	 * be the result of the constructor.
	 */
	protected Object execute(Context outerContext, Object parentInstance) throws ElementException
	{
		Context elementContext = new LocalContext("<" + getName() + ">", outerContext);

		// the constructor and action have contexes of their own.
		Context constructorContext = new LocalContext("<" + getName() + ">//instance", elementContext);
		Context actionContext = new LocalContext("<" + getName() + ">//action", elementContext);

		if( parentInstance != null ){
			// make the parent instance available to both of these contexes
			constructorContext.addObject(VARIABLE_NAME_PARENT_INSTANCE, parentInstance);
			actionContext.addObject(VARIABLE_NAME_PARENT_INSTANCE, parentInstance);
		}

		boolean isOverridden = isOverridden(outerContext);

		// -----------------------
		// execution
		// -----------------------
		// 1. child elements that come BEFORE the element instance is created
		//    = action elements | variables | constructor arguments
		//
		// 2. create the element instance
		//
		// 3. execute rest of the children
		//    = action elements | variables
		//
		// 4. execute the action of this element
		// -----------------------

		// assertion: lastConstructorArgumentIndex <= childElements.size()!!

		// first set: child-elements that are executed before the instance is created.
		for (int i = 0; i <= this.lastConstructorArgumentIndex; i++) {
			Element child = (Element) childElements.get(i);
			executeChild(child, ((Integer)childElementTypes.get(i)).intValue(), elementContext, null, constructorContext, actionContext, isOverridden);
		}

		Object instance = null;
		if( this.constructor != null || this.overridableBy != null) {
			// we have an instance.
			// obtain the instance - it is either created by the constructor or
			// it is the overriding object.
			instance = obtainElementInstance(constructorContext, isOverridden);
		}

		// execute rest of the children
		for (int i = (this.lastConstructorArgumentIndex+1) ; i < childElements.size()  ; i++) {
			Element child = (Element) childElements.get(i);
			// the constructor params have no importance any more
			// should we make sure somehow that the constructorParams won't get 'abused'?
			executeChild(child, ((Integer)this.childElementTypes.get(i)).intValue(), elementContext, instance, constructorContext, actionContext, isOverridden);
		}

		//
		verifyVariablesHaveBeenFound();

		if( this.action != null ){

			if( instance != null ){
				actionContext.addObject(Element.VARIABLE_NAME_ELEMENT_INSTANCE, instance);
			}
			executeAction(actionContext);
		}

		// done.
		return instance;
	}

	/**
	 *
	 * @param child        Element to be executed
	 * @param type         whether the child is a constructor argument, action argument, etc.
	 * @param elementContext  the context of this element, holding variables.
	 * @param instance Object the instance of this element, delivered to the child elements. may be null.
	 * @param constructorContext     the constructor arguments are added to this context.
	 * @param actionContext Context  the action arguments are added to this context.
	 * @param isOverridden boolean whether this element (not the child!) is overridden or not
	 * @throws ElementException if the execution of a child results in an error.
	 */
	private void executeChild(Element child, int type, Context elementContext, Object instance, Context constructorContext, Context actionContext, boolean isOverridden) throws ElementException
	{
		if( isOverridden ){
			// only action arguments are executed when this element is overridden
			if( type == CHILD_TYPE_ACTION_ARG ){
				// NOTE: we don't evaluate the if-attribute before it is absolutely
				// necessary.
				if( child.isExecuted(elementContext, instance) ){
					Object value = ((VariableElement) child).getValue(elementContext, instance);
					actionContext.addObject(child.getName(), value);
				}
			}
		}
		else if( child.isExecuted(elementContext, instance) ){
			// the child element is executed
			if( child instanceof VariableElement ){
				// get the value
				Object value = ((VariableElement)child).getValue(elementContext,instance);

				// choose the context for the value
				if( type == CHILD_TYPE_CONSTRUCTOR_ARG ){
					constructorContext.addObject(child.getName(), value);
				}
				else if( type == CHILD_TYPE_VARIABLE ){
					elementContext.addObject(child.getName(), value);
					variablePublished(child.getName());
				}
				else if( type == CHILD_TYPE_ACTION_ARG ){
					actionContext.addObject(child.getName(), value);
				}
				else {
					throw new RuntimeException("illegal variable element type.");
				}
			}
			else {
				// action element
				((ActionElement)child).execute(elementContext, instance);
			}
		}
		else {
			// child not executed.
		}
	}
}
