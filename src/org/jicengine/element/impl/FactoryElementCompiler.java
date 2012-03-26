package org.jicengine.element.impl;

import org.jicengine.element.*;
import org.jicengine.expression.FactoryInvocationParser;
import org.jicengine.operation.*;

/**
 * <p> </p>
 *
 * <p> </p>
 *
 * <p> </p>
 *
 * <p> </p>
 *
 * @author timo laitinen
 */
public class FactoryElementCompiler extends ElementCompiler {

	public FactoryElementCompiler(String name, Location location) throws ElementException
	{
		this(name, location, new String[0]);
	}


	public FactoryElementCompiler(String name, Location location, String[] parameters) throws ElementException
	{
		super(name, location);

		String[] parameterNames = new String[parameters.length];
		Class[] parameterTypes = new Class[parameters.length];

		for (int i = 0; i < parameters.length; i++) {
			try {
				String paramDef = parameters[i].trim();
				int spaceIndex = paramDef.indexOf(" ");
				parameterTypes[i] = org.jicengine.expression.ClassParser.toClass(paramDef.substring(0, spaceIndex));
				parameterNames[i] = paramDef.substring(spaceIndex + 1).trim();
			} catch (Exception e){
				throw new ElementException("Problems with parameter definition '" + parameters[i] + "'. Expected format '[class] [name]'", getName(), getLocation());
			}
		}
		getElement().setAction(new RegisterFactoryOperation(name,parameterNames,parameterTypes, FactoryInvocationParser.FACTORY_NAME_PREFIX + name));
	}

	/**
	 *
	 * @param child VariableElement
	 * @throws ElementException
	 * @return ActionElement
	 */
	protected ActionElement handleLooseVariableElement(final VariableElement child) throws ElementException
	{
		if( ((RegisterFactoryOperation)getElement().getAction()).getFactoryElement() != null ){
			throw new ElementException("Illegal child " + child + ". Only single element is allowed inside element of type 'factory'.", getName(), getLocation());
		}

		((RegisterFactoryOperation)getElement().getAction()).setFactoryElement(child);

		// return a dummy action element in return..
		return new ActionElement(){
			public void execute(Context context, Object parentInstance)
			{
			}
			public String getName()
			{
				return child.getName();
			}
			public Location getLocation()
			{
				return child.getLocation();
			}
			public boolean isExecuted(Context outerContext, Object parentInstance)
			{
				return false;
			}
		};

	}


  /*
	private static Context getRootContext(Context context)
	{
		if( context instanceof LocalContext){
			return getRootContext(((LocalContext)context).getParent());
		}
		else {
			return context;
		}
	}
  */
}
