package org.jicengine.operation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author    .timo
 * @version   1.0
 */

public class ReflectionUtils {

  private static final String FIELD_CLASS = "class";  
  
	/**
	 *
	 * @author    timo
	 */
	protected static class NoSuchMethodException extends java.lang.NoSuchMethodException {
		public NoSuchMethodException(Class actorClass, String methodName)
		{
			super("Class '" + actorClass.getName() + "' doesn't have a method '" + methodName + "'.");
		}
	}

	/**
	 * Indicates that one or more methods with the right name were found but the
	 * method-parameters didn't match.
	 *
	 * @author    timo
	 */
	protected static class NoMethodWithSuchParametersException extends java.lang.NoSuchMethodException {
		public NoMethodWithSuchParametersException(Class actorClass, String methodName, Object[] arguments)
		{
			super("Class '" + actorClass.getName() + "' doesn't have a method '" + methodName + " accepting arguments (" + getArgumentTypeList(arguments) + ")");
		}
	}

	/**
	 *
	 *
	 * @author    timo
	 * @created   29. elokuuta 2004
	 */
	protected static class NoSuchConstructorException extends java.lang.NoSuchMethodException {
		public NoSuchConstructorException(Class actorClass, Object[] arguments)
		{
			super("Class '" + actorClass.getName() + "' doesn't have constructor accepting arguments (" + getArgumentTypeList(arguments) + ")");
		}
	}

	private static Map objectTypesToPrimitiveTypes = new HashMap();
	private static Map primitiveTypesToObjectTypes = new HashMap();

	private static final int ASSIGNABILITY_DIFFERENT_PARAMETER_COUNTS = -1;
	private static final int ASSIGNABILITY_NON_ASSIGNABLE_PARAMETERS = 0;
	private static final int ASSIGNABILITY_EXACT_MATCH = 1;

	/**
	 * <p>
	 * Mimics method <code>Class.isAssignableFrom</code>, but handles the
	 * conversions between primitives and primitive wrappers automatically.
	 * </p>
	 *
	 * @param class1 Class
	 * @param class2 Class
	 * @return boolean
	 */
	public static boolean isAssignableFrom(Class class1, Class class2)
	{
		if( class1.isAssignableFrom(class2) ){
			return true;
		}
		else {
			Class primitive = primitiveWrapperToPrimitiveType(class2);
			if( primitive != null ){
				return class1.isAssignableFrom(primitive);
			}
			else {
				return false;
			}
		}
	}


	public static void setFieldValue(Object instance, Class ownerClass, String fieldName, Object fieldValue) throws Exception
	{
		try {
			Field field = ownerClass.getField(fieldName);
			field.set(instance, fieldValue);

		} catch(NoSuchFieldException e) {
			// for better error-message
			throw new NoSuchFieldException("Field '" + fieldName + "' not found in class '" + ownerClass.getName() + "'.");
		}
	}

	/**
	 * @param instance  the instance whose field is referenced. may be null, if
	 * the field in question is a static field.
	 *
	 * @param ownerClass  the class that 'owns' the field.
	 * @param fieldName   the name of the field.
	 */
	public static Object getFieldValue(Object instance, Class ownerClass, String fieldName) throws Exception
	{
    if( instance == null && fieldName.equals(FIELD_CLASS) ){
      // this is not a real static field but an expression 
      // like 'java.lang.String.class'. 
      // reflection does not understand the pseudo-field
      // 'class', so we handle this situation here
      // by simply returning the class.
      return ownerClass;
    }
    else {
  		try {
  			Field field = ownerClass.getField(fieldName);
  			return field.get(instance);
  		} catch(NoSuchFieldException e) {
  			// for better error-message
  			throw new NoSuchFieldException("Field '" + fieldName + "' not found in class '" + ownerClass.getName() + "'.");
  		}
    }
	}

	protected static Class getActorClass(Object instanceOrClass)
	{
		if(instanceOrClass instanceof Class) {
			return (Class) instanceOrClass;
		}
		else {
			return instanceOrClass.getClass();
		}
	}

	/**
	 * <p>
	 * Resolves the 'parameter assignability level' that a set of parameters
	 * has against a set of parameter types.
	 * </p>
	 * <p>
	 * The assignability level is returned as a int value that follows a little
	 * peculiar scheme:
	 * <ul>
	 *  <li><b> -1 </b> - no match. even the number of parameters doesn't match. </li>
	 *  <li><b> 0 </b> - no match. the number of parameter matches, but at least
	 *  one of the parameters has a wrong type. </li>
	 *  <li><b> 1 </b> - exact match. the parameter types match the runtime
	 *  classes of the parameter objects exactly. </li>
	 *  <li><b> 2,3,4,.. </b> - the parameters match. the parameters are assignable
	 *  to the expected parameter types, but the expected types are superclasses
	 *  or interfaces of the actual types of the parameter objects. the greater
	 *  the number, the further away the parameter objects are. </li>
	 * </ul>
	 * <p>
	 * THEREFORE: a positive assignability level that is closer to 1 is better
	 * than a positive value that is further away from it. 1 is the best. values
	 * that are 0 or negative mean that the parameters are incompatible.
	 * </p>
	 *
	 * @param parameterTypes  declared parameter types of a method/constructor.
	 * @param parameters      runtime parameters given to the method/constructor,
	 *                        whose types are checked agains the declared types,
	 *                        in order to find out the correct method/constructor
	 *                        to call.
	 *
	 * @return                The parameterAssignabilityLevel value
	 */
	private static int getParameterAssignabilityLevel(Class[] parameterTypes, Object[] parameters)
	{
		if(parameterTypes.length == parameters.length) {
			// the number of parameters matches. search further

			// exactly assignable by default
			int assignability = ASSIGNABILITY_EXACT_MATCH;

			for(int i = 0; i < parameterTypes.length; i++) {
				Class parameterType = parameterTypes[i];
				Object parameter = parameters[i];
				if(parameter == null) {
					// we can't resolve the type of a null-parameter!
					throw new IllegalArgumentException("parameter " + (i + 1) + " was null.");
				}

				Class candidateType = parameter.getClass();

				if(parameterType.equals(candidateType)) {
					// ok, an exact match.
					assignability *= ASSIGNABILITY_EXACT_MATCH;
				}
				else if(parameterType.isAssignableFrom(candidateType)) {
					// not an exact match but a match anyways
					// TODO: calculate the distance between these two classes.
					assignability *= 2;
				}
				else if(parameterType.isPrimitive()) {
					// we have still hope: lets do the primitive conversion.
					Class correspondingPrimitive = primitiveWrapperToPrimitiveType(candidateType);

					// if the required parameter type is a primitive, we can't simply
					// test for equals instead of the assignable thing. int = int,
					// there are no subclasses!
					if(correspondingPrimitive != null && correspondingPrimitive.equals(parameterType)) {
						// this is considered as an exact match.
						assignability *= ASSIGNABILITY_EXACT_MATCH;
					}
					else {
						// no match, stop the search.
						assignability *= ASSIGNABILITY_NON_ASSIGNABLE_PARAMETERS;
						break;
					}
				}
				else {
					// no exact match, no assignable match and no match after primitive
					// conversions. certainly no match!
					assignability *= ASSIGNABILITY_NON_ASSIGNABLE_PARAMETERS;
					break;
				}
			}

			return assignability;
		}
		else {
			// even the number of parameters isn't exact. the worst case
			return ASSIGNABILITY_DIFFERENT_PARAMETER_COUNTS;
		}
	}


	/**
	 * Invokes a method.
	 *
	 * @param methodName                        the name of the method, like
	 *                                          'addLayer' or 'setName'
	 * @param actor                             Description of the Parameter
	 * @param arguments                        Description of the Parameter
	 * @return                                  an Object, if the invoked method
	 *      returned something. null if the methods return-type is void.
	 * @throws NoSuchMethodException            if no matching method was found
	 * @throws IllegalAccessException           see Method.invoke()
	 * @throws IllegalArgumentException         see Method.invoke()
	 * @throws InvocationTargetException        if the invoked method throwed an
	 *      Exception
	 */
	public static Object invokeMethod(Object actor, String methodName, Object[] arguments) throws java.lang.NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(actor == null) {
			throw new NullPointerException("when calling method '" + methodName + "' (with " + arguments.length + " arguments)");
		}
		else {
			return invokeMethod(actor, actor.getClass(), methodName, arguments);
		}
	}

	public static Object invokeStaticMethod(Class actorClass, String methodName, Object[] arguments) throws java.lang.NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(actorClass == null) {
			throw new NullPointerException("when calling static method '" + methodName + "' (with " + arguments.length + " arguments)");
		}
		else {
			return invokeMethod(null, actorClass, methodName, arguments);
		}
	}

	public static Object instantiate(Class instantiatedClass, Object[] arguments) throws java.lang.NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		// find the constructor.
		Constructor constructor = findConstructor(instantiatedClass, arguments);

		// instantiate object
		try {
			return constructor.newInstance(arguments);
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("Problems instantiating with constructor '" + constructor + "'");
		}
	}

	private static Constructor findConstructor(Class instantiatedClass, Object[] arguments) throws java.lang.NoSuchMethodException, InstantiationException, IllegalAccessException
	{
		Constructor[] constructors = instantiatedClass.getConstructors();
		Constructor match = null;
		int parameterAssignability = ASSIGNABILITY_NON_ASSIGNABLE_PARAMETERS;

		for(int i = 0; i < constructors.length; i++) {
			Constructor candidate = constructors[i];
			Class[] parameterTypes = candidate.getParameterTypes();

			int candidateParameterAssignability = getParameterAssignabilityLevel(parameterTypes, arguments);

			if(candidateParameterAssignability > ASSIGNABILITY_NON_ASSIGNABLE_PARAMETERS) {
				// parameters a assignable. find out how assignable

				if(candidateParameterAssignability == ASSIGNABILITY_EXACT_MATCH) {
					// the parameters match exactly. we can stop the seach here!
					match = candidate;
					parameterAssignability = candidateParameterAssignability;
					break;
				}
				else if(parameterAssignability < ASSIGNABILITY_EXACT_MATCH || candidateParameterAssignability < parameterAssignability) {
					// not an exact match but a better match than our previous match
					// (if had one).
					// this is the best candidate so far, but we have to continue the
					// search.
					match = candidate;
					parameterAssignability = candidateParameterAssignability;
					continue;
				}
			}
		}

		if(match == null) {
			throw new NoSuchConstructorException(instantiatedClass, arguments);
		}
		else {
			return match;
		}
	}

	/**
	 * @param objectClass  Description of the Parameter
	 * @return             the Class representing a primitive: if the argument
	 *                     Class is java.lang.Integer, returns Integer.TYPe, etc.
	 *                     null if the argument class is not a wrapper.
	 */
	protected static Class primitiveWrapperToPrimitiveType(Class objectClass)
	{
		return (Class) objectTypesToPrimitiveTypes.get(objectClass);
	}

	/**
	 * @param primitiveType  Description of the Parameter
	 * @return               null if the argument class is not a primitive type.
	 */
	protected static Class primitiveTypeToWrapperType(Class primitiveType)
	{
		return (Class) primitiveTypesToObjectTypes.get(primitiveType);
	}

	/**
	 *
	 * @param arguments Object[]
	 * @return String types in format [arg1 class], [arg2 class], etc.
	 */
	protected static String getArgumentTypeList(Object[] arguments)
	{
		String paramString = "";
		for(int i = 0; i < arguments.length; i++) {
			if(arguments[i] == null) {
				paramString += "" + arguments[i];
			}
			else {
				// should array and primitive types be handled separately?
				paramString += arguments[i].getClass().getName();
			}

			if((i + 1) < arguments.length) {
				paramString += ",";
			}
		}
		return paramString;
	}

	/**
	 * a method for invoking both instance methods and static methods dynamically.
	 *
	 * @param actor                             the instance whose method is
	 *      invoked. null, if a static method is in question. NOTE: the null means
	 *      that the method must be static. filter unintentional null-values away
	 *      before calling this method!
	 * @param actorClass                        the class that owns the invoked
	 *      method. this should be the class of the actor.
	 * @param methodName                        name of the method
	 * @param parameters                        parameters as Object[]
	 * @return                                  Description of the Return Value
	 * @throws java.lang.NoSuchMethodException  Description of the Exception
	 * @throws IllegalAccessException           Description of the Exception
	 * @throws IllegalArgumentException         Description of the Exception
	 * @throws InvocationTargetException        Description of the Exception
	 */
	private static Object invokeMethod(Object actor, Class actorClass, String methodName, Object[] arguments) throws java.lang.NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		// some validity checks
		if(methodName == null) {
			throw new IllegalArgumentException("Can't invoke a method: method name was null");
		}
		if(arguments == null) {
			throw new IllegalArgumentException("Can't invoke a method: arguments[] was null");
		}

		if(!Modifier.isPublic(actorClass.getModifiers())) {
			// problem: a non-public class. we have no permissions to read it.
			// this support has to be implemented later.
			throw new UnsupportedOperationException("Class '" + actorClass.getName() + "' is private or protected. only public classes are supported currently.");
		}

		Method method;

		// we try first the method Class.getMethod().
		// it works only if the types of the parameters match exactly the
		// declared parameter types of a method in the owner class.
		// but it is fast - its worth trying although it might result in
		// exception.
		//
		// note: we could use some heuristics for deciding whether it is better
		// to try getMethod() or manual search first.
		try {
			method = actorClass.getMethod(methodName, getTypes(arguments));
		} catch (java.lang.NoSuchMethodException e){
			method = findMethod(actorClass, methodName, arguments);
		}

		// call the method and return the result.
		// note: a better error message for situations where the method was supposed
		// to be static but wasn't? now, only a nullpointer is thrown..
		return method.invoke(actor, arguments);
	}

	private static Method findMethod(Class actorClass, String methodName, Object[] arguments) throws java.lang.NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method[] methods = actorClass.getMethods();
		Method match = null;
		int parameterAssignability = ASSIGNABILITY_DIFFERENT_PARAMETER_COUNTS;
		boolean foundMethodWithTheSameName = false;

		for(int i = 0; i < methods.length; i++) {
			Method candidate = methods[i];
			if(candidate.getName().equals(methodName)) {
				// good, the name matches..
				foundMethodWithTheSameName = true;
				Class[] parameterTypes = candidate.getParameterTypes();

				int candidateParameterAssignability = getParameterAssignabilityLevel(parameterTypes, arguments);

				if(candidateParameterAssignability > ASSIGNABILITY_NON_ASSIGNABLE_PARAMETERS) {
					// parameters a assignable. find out how assignable

					if(candidateParameterAssignability == ASSIGNABILITY_EXACT_MATCH) {
						// the parameters match exactly. we can stop the seach here!
						match = candidate;
						parameterAssignability = candidateParameterAssignability;
						break;
					}
					else if(parameterAssignability < ASSIGNABILITY_EXACT_MATCH || candidateParameterAssignability < parameterAssignability) {
						// not an exact match but a better match than our previous match
						// (if had one).
						// this is the best candidate so far, but we have to continue the
						// search.

						// NOTE: 16.5.2005: is the test correct? shouldn't it use '>'
						// instead of '<'..

						match = candidate;
						parameterAssignability = candidateParameterAssignability;
						continue;
					}
				}
			}
		}

		if(match == null) {
			// no method found.
			if(foundMethodWithTheSameName) {
				throw new NoMethodWithSuchParametersException(actorClass, methodName, arguments);
			}
			else {
				throw new ReflectionUtils.NoSuchMethodException(actorClass, methodName);
			}
		}
		else {
			return match;
		}
	}

	protected static Class[] getTypes(Object[] parameters)
	{
		Class[] types = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			types[i] = parameters[i].getClass();
		}
		return types;
	}

	static {
		objectTypesToPrimitiveTypes.put(Double.class, Double.TYPE);
		objectTypesToPrimitiveTypes.put(Integer.class, Integer.TYPE);
		objectTypesToPrimitiveTypes.put(Long.class, Long.TYPE);
		objectTypesToPrimitiveTypes.put(Character.class, Character.TYPE);
		objectTypesToPrimitiveTypes.put(Boolean.class, Boolean.TYPE);
		objectTypesToPrimitiveTypes.put(Byte.class, Byte.TYPE);
    objectTypesToPrimitiveTypes.put(Float.class, Float.TYPE);
    objectTypesToPrimitiveTypes.put(Short.class, Short.TYPE);

		primitiveTypesToObjectTypes.put(Double.TYPE, Double.class);
		primitiveTypesToObjectTypes.put(Integer.TYPE, Integer.class);
		primitiveTypesToObjectTypes.put(Long.TYPE, Long.class);
		primitiveTypesToObjectTypes.put(Character.TYPE, Character.class);
		primitiveTypesToObjectTypes.put(Boolean.TYPE, Boolean.class);
		primitiveTypesToObjectTypes.put(Byte.TYPE, Byte.class);
    primitiveTypesToObjectTypes.put(Float.TYPE, Float.class);
    primitiveTypesToObjectTypes.put(Short.TYPE, Short.class);

	}
}
