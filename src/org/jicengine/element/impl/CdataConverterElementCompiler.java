package org.jicengine.element.impl;

import org.jicengine.element.ActionElement;
import org.jicengine.element.CdataHandler;
import org.jicengine.element.Element;
import org.jicengine.element.ElementCompiler;
import org.jicengine.element.ElementException;
import org.jicengine.element.Location;
import org.jicengine.element.VariableElement;
import org.jicengine.expression.ClassParser;
import org.jicengine.expression.FactoryInvocationParser;
import org.jicengine.operation.Context;

public class CdataConverterElementCompiler extends ElementCompiler {

  private Class targetClass;
  public CdataConverterElementCompiler(String name, Location location, String[] parameters) throws ElementException
  {
    super(name, location);
    
    try {
      Class targetClass = ClassParser.toClass((String)parameters[0]);
      this.targetClass = targetClass;
      
      //System.out.println(this.targetClass);
      
      if( CdataHandler.isDefaultCdataConversionType(targetClass)){
        throw new ElementException("User-defined CDATA conversion can not override default conversion for '" + parameters[0] + "'", getName(), getLocation());
      }
      
      getElement().setAction(new RegisterFactoryOperation(name,new String[]{Element.VARIABLE_NAME_CDATA},new Class[]{String.class}, toContextVariableName(targetClass)));      
      
    } catch (ClassNotFoundException e){
      throw new ElementException("Target class '" + parameters[0] + "' not found.", getName(), getLocation());
    }
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

    if( !org.jicengine.operation.ReflectionUtils.isAssignableFrom(this.targetClass, child.getInstanceClass()) ){
      throw new ElementException("Expected '" + child.getName() + "' to be '" + this.targetClass.getName() + "', was '" + child.getInstanceClass() + "'", getName(), getLocation());
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

  public static String toContextVariableName(Class targetClass)
  {
    return "cdata-converter::" + targetClass.getName();
  }

}
