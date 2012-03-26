package org.jicengine.element;
import org.jicengine.element.impl.CdataConverterInvocationOperation;
import org.jicengine.expression.ClassParser;
import org.jicengine.io.Resource;
import org.jicengine.operation.Operation;
import org.jicengine.operation.OperationException;
import org.jicengine.operation.StaticValue;
import org.jicengine.operation.Context;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.*;
import org.jicengine.util.StringConverter;
/**
 * <p> </p>
 *
 * <p> </p>
 *
 * <p> </p>
 *
 * <p> </p>
 *
 * todo: the exception types are not specific enough..
 *
 * @author timo laitinen
 */
public class CdataHandler {

  private static final String CDATA_TRUE = "true";
  private static final String CDATA_FALSE = "false";
  
  /**
   * 
   * 
   * @param cdata
   * @return
   */
  public static Class resolveInstanceClassFromCdata(String cdata)
  {
    Class instanceClass = null;
    
    char[] chars = cdata.toCharArray();

    if( chars.length == 0){
      instanceClass = String.class;
    }
    
    if( instanceClass == null){
      // not an empty string, try number
      instanceClass = getNumberClass(chars);
    }
    
    if( instanceClass == null){
      // not a number, try boolean
      if( cdata.equals(CDATA_TRUE)){
        instanceClass = Boolean.TYPE;
      }
      else if( cdata.equals(CDATA_FALSE)){
        instanceClass = Boolean.TYPE;
      }
    }

    if( instanceClass == null){
      // it must be a string
      instanceClass = String.class;
    }
    
    return instanceClass;
  }

  private static Class getNumberClass(char[] cdata)
  {
    int decimalIndex = -1;
    for( int i = 0 ; i < cdata.length ; i++ ) {
      if( Character.isDigit(cdata[i])){
        continue;
      }
      else if( cdata[i] == '.' && (decimalIndex == -1 && i > 0) ){
        // valid decimal index
        decimalIndex = i;
        continue;
      }
      else if( cdata[i] == '-' && i == 0){
        continue;
      }
      else {
        // illegal character, stop and return null.
        return null;
      }
    }
    
    if( decimalIndex == -1){
      return Integer.TYPE;
    }
    else {
      return Double.TYPE;
    }
  }
  
  private static Collection defaultTargetClasses;
  private static Map constructorFactories = new HashMap();
  
  static {
    defaultTargetClasses= new HashSet();
    defaultTargetClasses.addAll(Arrays.asList(new Object[]{
        String.class,
        Integer.TYPE,
        Integer.class,
        Double.class,
        Double.TYPE,
        Boolean.class,
        Boolean.TYPE,
        Long.class,
        Long.TYPE,
        Float.class,
        Float.TYPE,
        Short.class,
        Short.TYPE,        
        Resource.class,
        java.awt.Color.class,
        Font.class,
        java.awt.Dimension.class,
        java.awt.Point.class,
        java.net.URL.class,
        java.util.TimeZone.class,
        java.util.Locale.class,
        java.lang.Class.class
    }));
    
    constructorFactories.put(String.class, new StringConstructorFactory());
    constructorFactories.put(Integer.class, new IntConstructorFactory());
    constructorFactories.put(Integer.TYPE, new IntConstructorFactory());    
    constructorFactories.put(Double.class, new DoubleConstructorFactory());
    constructorFactories.put(Double.TYPE, new DoubleConstructorFactory());
    constructorFactories.put(Boolean.class, new BooleanConstructorFactory());
    constructorFactories.put(Boolean.TYPE, new BooleanConstructorFactory());
    constructorFactories.put(Character.class, new CharacterConstructorFactory());
    constructorFactories.put(Character.TYPE, new CharacterConstructorFactory());
    constructorFactories.put(Long.class, new LongConstructorFactory());    
    constructorFactories.put(Long.TYPE, new LongConstructorFactory());
    constructorFactories.put(Short.class, new ShortConstructorFactory());
    constructorFactories.put(Short.TYPE, new ShortConstructorFactory());
    constructorFactories.put(Float.class, new FloatConstructorFactory());
    constructorFactories.put(Float.TYPE, new FloatConstructorFactory());
    constructorFactories.put(BigDecimal.class, new BigDecimalConstructorFactory());
    constructorFactories.put(BigInteger.class, new BigIntegerConstructorFactory());
    constructorFactories.put(Resource.class, new ResourceConstructorFactory());
    constructorFactories.put(Color.class, new ColorConstructorFactory());
    constructorFactories.put(Font.class, new FontConstructorFactory());
    constructorFactories.put(Dimension.class, new DimensionConstructorFactory());
    constructorFactories.put(Point.class, new PointConstructorFactory());
    constructorFactories.put(URL.class, new UrlConstructorFactory());
    constructorFactories.put(TimeZone.class, new TimeZoneConstructorFactory());
    constructorFactories.put(Locale.class, new LocaleConstructorFactory());
    constructorFactories.put(Class.class, new ClassConstructorFactory());
  }
  
  public static boolean isDefaultCdataConversionType(Class targetClass)
  {
    return defaultTargetClasses.contains(targetClass.getName());
  }
  
	/**
	 * Returns a constructor that converts the CDATA value into 
   * the instance of the proper class i.e. the class stated
   * in the class attribute.
   *
	 * @param targetClass Class  may be null.
	 * @param cdata String
	 * @return Operation
	 * @throws Exception if the CDATA can not be converted to an instance of the particular
   * class.
	 */
	public static Operation getClassBasedCdataConversionConstructor(Class targetClass, String cdata) throws Exception
	{
		if( targetClass == null ){
      throw new IllegalArgumentException("Target class required in CDATA conversions");
		}
    
    ConstructorFactory factory = (ConstructorFactory) constructorFactories.get(targetClass);
    
    if( factory != null){
      return factory.getConstructor(cdata);
    }
    else {
      // no match found, but the JIC file may have an 
      // user-defined CDATA conversion for the class:
      return new CdataConverterInvocationOperation(targetClass, cdata);
		}
	}

  static interface ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException; 
  }
  
  static class StringConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
    {
      return new StaticValue(stringValue);
    }
  }  
  
  static class ClassConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
    {
      try {
        return new StaticValue(ClassParser.toClass(stringValue));
      } catch (ClassNotFoundException e){
        throw new OperationException("Failed to load class '" + stringValue + "'");
      }
    }
  }
  
  
	static class IntConstructorFactory implements ConstructorFactory  {

		public Operation getConstructor(String stringValue) throws OperationException
		{
			try {
        return new StaticValue(Integer.valueOf(stringValue));
			} catch (NumberFormatException e){
				throw new OperationException("Failed to convert '" + stringValue + "' to int");
			}
		}
	}

  static class DoubleConstructorFactory implements ConstructorFactory {

    public Operation getConstructor(String stringValue) throws OperationException
		{
			try {
				return new StaticValue(Double.valueOf(stringValue));
			} catch (NumberFormatException e){
				throw new OperationException("Failed to convert '" + stringValue + "' to double.");
			}
		}
	}


	static class BooleanConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
		{
			try {
				return new StaticValue(Boolean.valueOf(stringValue));
			} catch (NumberFormatException e){
				throw new OperationException("Failed to convert '" + stringValue + "' to boolean.");
			}
		}
	}

  static class CharacterConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
    {
      if( stringValue.length() == 1){
        return new StaticValue(new Character(stringValue.charAt(0)));
      }
      else {
        throw new OperationException("Too many characters in '" + stringValue + "'");        
      }
    }
  }  
  
	static class LongConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
		{
			try {
				return new StaticValue(Long.valueOf(stringValue));
			} catch (NumberFormatException e){
				throw new OperationException("Failed to convert '" + stringValue + "' to long.");
			}
		}
	}

  static class ShortConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
    {
      try {
        return new StaticValue(Short.valueOf(stringValue));
      } catch (NumberFormatException e){
        throw new OperationException("Failed to convert '" + stringValue + "' to short.");
      }
    }
  }  

	static class FloatConstructorFactory implements ConstructorFactory {

    public Operation getConstructor(String stringValue) throws OperationException
		{
			try {
        return new StaticValue(Float.valueOf(stringValue));
			} catch (NumberFormatException e) {
				throw new OperationException("Failed to convert '" + stringValue + "' to float.");
			}
		}
	}

  static class BigDecimalConstructorFactory implements ConstructorFactory {

    public Operation getConstructor(String stringValue) throws OperationException
    {
      try {
        return new StaticValue(new BigDecimal(stringValue));
      } catch (NumberFormatException e) {
        throw new OperationException("Failed to convert '" + stringValue + "' to BigDecimal",e);
      }
    }
  }
  
  static class BigIntegerConstructorFactory implements ConstructorFactory {
    private BigInteger value;
    public Operation getConstructor(String stringValue) throws OperationException
    {
      try {
        return new StaticValue(new BigInteger(stringValue));
      } catch (NumberFormatException e) {
        throw new OperationException("Failed to convert '" + stringValue + "' to BigInteger",e);
      }
    }
  }  
  
	static class ColorConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
		{
			try {
        return new StaticValue(StringConverter.toColor(stringValue));
			} catch (IllegalArgumentException e) {
				throw new OperationException(e.getMessage(), e);
			}
		}
	}

	static class FontConstructorFactory implements ConstructorFactory {
   public Operation getConstructor(String stringValue) throws OperationException
   {
     return new FontConstructor(stringValue);
   } 
  }
  
	static class FontConstructor implements Operation {
		private String stringValue;

    /**
     * the Font is not created in the constructor, because.. I guess
     * fonts can be installed to the system at any time? so we must not be
     * hasty..
     *
     * @param stringValue String
     */    
		public FontConstructor(String stringValue)
		{
			this.stringValue = stringValue;
		}

		public boolean needsParameters()
		{
			return false;
		}
		public boolean needsParameter(String name)
		{
				return false;
		}
		public Object execute(Context context) throws OperationException
		{
			return java.awt.Font.decode(this.stringValue);
		}
	}

	static class UrlConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
		{
			try {
        return new StaticValue(new java.net.URL(stringValue));
			} catch (MalformedURLException ex) {
				throw new OperationException(ex.getMessage(), ex);
			}
		}
	}

  static class DimensionConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
    {
      return new DimensionConstructor(stringValue);
    }
  }
  
	static class DimensionConstructor implements Operation {
		private String stringValue;
		/**
		 * Dimension is not created in the constructor because
		 * Dimensions are modifiable!
		 *
		 * @param stringValue String
		 */
		public DimensionConstructor(String stringValue)
		{
			this.stringValue = stringValue;
		}
		public boolean needsParameters()
		{
			return false;
		}
		public boolean needsParameter(String name)
		{
				return false;
		}
		public Object execute(Context context) throws OperationException
		{
			try {
				return StringConverter.toDimension(this.stringValue);
			} catch (IllegalArgumentException e){
				throw new OperationException(e.getMessage(), e);
			}
		}
	}

  static class PointConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
    {
      return new PointConstructor(stringValue);
    }
  }
    
	static class PointConstructor implements Operation {
		private String stringValue;
		/**
		 * Point IS NOT created in the constructor because Points
		 * are modifiable in Java..
		 *
		 * @param stringValue String
		 */
		public PointConstructor(String stringValue)
		{
			this.stringValue = stringValue;
		}
		public boolean needsParameters()
		{
			return false;
		}
		public boolean needsParameter(String name)
		{
				return false;
		}
		public Object execute(Context context) throws OperationException
		{
			try {
				return StringConverter.toPoint(this.stringValue);
			} catch (IllegalArgumentException e){
				throw new OperationException(e.getMessage(), e);
			}
		}
	}

  static class LocaleConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
    {
      return new LocaleConstructor(stringValue);
    }
  }
  
	static class LocaleConstructor implements Operation {
		private String stringValue;
		public LocaleConstructor(String stringValue)
		{
			this.stringValue = stringValue;
		}
		public boolean needsParameters()
		{
			return false;
		}
		public boolean needsParameter(String name)
		{
				return false;
		}
		public Object execute(Context context) throws OperationException
		{
			try {
				return StringConverter.toLocale(this.stringValue);
			} catch (IllegalArgumentException e){
				throw new OperationException(e.getMessage(), e);
			}
		}
	}

  static class TimeZoneConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
    {
      return new TimeZoneConstructor(stringValue);
    }
  }
  
	static class TimeZoneConstructor implements Operation {
		private String stringValue;
		public TimeZoneConstructor(String stringValue)
		{
			this.stringValue = stringValue;
		}
		public boolean needsParameters()
		{
			return false;
		}
		public boolean needsParameter(String name)
		{
			return false;
		}
		public Object execute(Context context) throws OperationException
		{
			return java.util.TimeZone.getTimeZone(this.stringValue);
		}
	}

  static class ResourceConstructorFactory implements ConstructorFactory {
    public Operation getConstructor(String stringValue) throws OperationException
    {
      return new ResourceConstructor(stringValue);
    }
  }
	static class ResourceConstructor implements Operation {
		private String stringValue;
		public ResourceConstructor(String stringValue)
		{
			this.stringValue = stringValue;
		}
		public boolean needsParameters()
		{
			return false;
		}

		public boolean needsParameter(String name)
		{
				return false;
		}
		public Object execute(Context context) throws OperationException
		{
			org.jicengine.BuildContext buildContext = (org.jicengine.BuildContext) context.getObject(org.jicengine.BuildContext.VARIABLE_NAME);

			try {
				return buildContext.getCurrentFile().getResource(this.stringValue);
			} catch (IOException ex) {
				throw new OperationException(ex.getMessage(), ex);
			}
		}
	}

}
