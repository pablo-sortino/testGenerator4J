package org.jicengine.element.impl;


import org.jicengine.element.Element;
import org.jicengine.expression.FactoryInvocationParser;
import org.jicengine.operation.Context;
import org.jicengine.operation.Factory;
import org.jicengine.operation.FactoryInvocationOperation;
import org.jicengine.operation.MethodInvocationOperation;
import org.jicengine.operation.ObjectNotFoundException;
import org.jicengine.operation.Operation;
import org.jicengine.operation.OperationException;
import org.jicengine.operation.StaticValue;
import org.jicengine.operation.VariableValueOperation;

/**
 * 
 * @author timo
 *
 */
public class CdataConverterInvocationOperation implements Operation {

  private Class targetClass; 
  private String cdata;
  
  public CdataConverterInvocationOperation(Class targetClass, String cdata)
  {
    this.targetClass = targetClass;
    this.cdata = cdata;
  }
  
  public boolean needsParameters()
  {
    return true;
  }

  public boolean needsParameter(String name)
  {
    return name.equals(Element.VARIABLE_NAME_CDATA);
  }

  public Object execute(Context context) throws OperationException
  {
     
    String contextVariableName = CdataConverterElementCompiler.toContextVariableName(this.targetClass);
    
    try {
      Factory factory = (Factory) context.getObject(contextVariableName);

      Object result = factory.invoke(new Object[]{this.cdata});
      
      return result;
    } catch (ObjectNotFoundException e){
      throw new OperationException("CDATA conversion for class '" + this.targetClass.getName() + "' not available.");
    }
    
  }

}
