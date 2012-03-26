package org.jicengine.element.impl;

import org.jicengine.element.*;
import org.jicengine.operation.*;
import java.util.*;
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
public class SwitchCompiler extends ElementCompiler {

	public SwitchCompiler(String name, Location location, String[] parameters) throws ElementException
	{
		super(name, location);

		getElement().setConstructor(new Chooser(parameters[0]));
	}

	/**
	 *
	 * @param child VariableElement
	 * @throws ElementException
	 * @return ActionElement
	 */
	protected ActionElement handleLooseVariableElement(final VariableElement child) throws ElementException
	{
		// check that the element has no if-attribute
		// handle the variable as a different case.

		// we give the element directly to the Chooser
		((Chooser)getElement().getConstructor()).addCase(child);

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

	public class Chooser implements Operation {
		private String variable;
		private List caseNames = new ArrayList();
		private List cases = new ArrayList();
		private VariableElement defaultCase;

		public Chooser(String variable)
		{
			this.variable = variable;
		}

		public void addCase(VariableElement child)
		{
			if( child.getName().equals("default") ){
				this.defaultCase = child;
			}
			else {
				this.cases.add(child);
				this.caseNames.add(child.getName());
			}
		}

		public boolean needsParameters()
		{
			return true;
		}

		public boolean needsParameter(String name)
		{
			return name.equals(this.variable);
		}

		public Object execute(Context context) throws OperationException
		{
			Object variableValue = new VariableValueOperation(this.variable).execute(context);

			String stringValue = String.valueOf(variableValue);

			VariableElement selected = null;
			for (int i = 0; i < this.caseNames.size(); i++) {
				String candidate = this.caseNames.get(i).toString();
				if( candidate.equals(stringValue) ){
					// match
					selected = (VariableElement) this.cases.get(i);
					break;
				}
			}

			if( selected == null ){
				if( this.defaultCase != null ){
					selected = this.defaultCase;
				}
				else {
					throw new OperationException("Case '" + stringValue + "' not found among " + this.caseNames);
				}
			}
			Context elementContext = ((LocalContext)context).getParent();
			try {
				Object result = selected.getValue(elementContext, null);
				return result;
			} catch (ElementException e){
				throw new OperationException(e.getMessage(), e);
			}
		}
	}

}
