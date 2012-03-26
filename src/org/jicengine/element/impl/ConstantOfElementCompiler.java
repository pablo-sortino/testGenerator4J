package org.jicengine.element.impl;

import java.util.Arrays;

import org.jicengine.element.ActionElement;
import org.jicengine.element.Element;
import org.jicengine.element.ElementCompiler;
import org.jicengine.element.ElementException;
import org.jicengine.element.Location;
import org.jicengine.element.VariableElement;
import org.jicengine.expression.ClassParser;
import org.jicengine.operation.Context;
import org.jicengine.operation.Operation;
import org.jicengine.operation.OperationException;
import org.jicengine.operation.ReflectionUtils;

public class ConstantOfElementCompiler extends ElementCompiler {

  public ConstantOfElementCompiler(String name, Location location, String[] parameters) throws ElementException
  {
    super(name, location);
    
    if( parameters.length == 1){
      try {
        Class ownerClass = ClassParser.toClass(parameters[0]);
        getElement().setConstructor(new ConstantOfConstructor(ownerClass));
      } catch (ClassNotFoundException e){
        throw new ElementException("Owner class not found",e, getName(), getLocation());
      }
    }
    else {
      throw new ElementException("Expected 1 parameter, got " + parameters.length + " parameters: " + Arrays.asList(parameters), getName(),getLocation());
    }
  }
  
  protected ActionElement handleLooseVariableElement(VariableElement child) throws ElementException
  {
    throw new ElementException("Child elements not allowed", getName(), getLocation());
  }

  public class ConstantOfConstructor implements Operation {
    
    private Class ownerClass;
    
    public ConstantOfConstructor(Class ownerClass)
    {
      this.ownerClass = ownerClass;
    }
    
    public Object execute(Context context) throws OperationException
    {
      try {
        String fieldName = (String) context.getObject(Element.VARIABLE_NAME_CDATA);
        return getConstantValue(this.ownerClass, fieldName);
      } catch (Exception e){
        throw new OperationException(e.getMessage(), e);
      }
    }
    
    public boolean needsParameter(String name)
    {
      return name.equals(Element.VARIABLE_NAME_CDATA);
    }
    
    public boolean needsParameters()
    {
      return true;
    }
  }
  
  public static Object getConstantValue(Class ownerClass, String fieldName) throws Exception
  {
    return ReflectionUtils.getFieldValue(null, ownerClass, fieldName);
  }
}
