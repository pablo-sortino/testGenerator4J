package org.jicengine.expression;

/**
 *
 *
 *
 *
 * @author .timo
 *
 */

public class ClassLoaderResolver {

	private ClassLoaderResolver() {
	}

	/**
	 * <p>
	 * Resolves the &quot;correct&quot; classloader to use i.e. chooses
	 * between the current classloader or the context classloader.
	 * </p>
	 * <p>
	 * the logic is based on the JavaWorld article 'Find a way out of the ClassLoader
	 * maze' by Vladimir Roubtsov.
	 * </p>
	 *
	 * @param callerClass  the class of the object that wants to use the
	 *                     ClassLoader.
	 */
	public static ClassLoader getClassLoader(Class callerClass)
	{
		ClassLoader callerLoader = callerClass.getClassLoader ();
		ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();

		ClassLoader result;

		// if 'callerLoader' and 'contextLoader' are in a parent-child
		// relationship, always choose the child:
		if( isChild (contextLoader, callerLoader) ){
			result = callerLoader;
		}
		else if (isChild (callerLoader, contextLoader)){
			result = contextLoader;
		}
		else {
			// just a guess - context should be a better choice, in most cases.
			result = contextLoader;
		}

		/*
		// getSystemClassLoader fails in applets due to security permissions
		// didn't have time to fix. now JICE can't be deployed as an extension

		ClassLoader systemLoader = ClassLoader.getSystemClassLoader ();

		// precaution for when deployed as a bootstrap or extension class:
		if( isChild (result, systemLoader)){
			result = systemLoader;
		}
		*/

		return result;
	}

	/**
	 * Returns true if 'loader2' is a delegation child of 'loader1' [or if
	 * 'loader1'=='loader2']. Of course, this works only for classloaders that
	 * set their parent pointers correctly. 'null' is interpreted as the
	 * primordial loader [i.e., everybody's parent].
	 */
	private static boolean isChild(ClassLoader loader1, ClassLoader loader2)
	{
		if (loader1 == loader2){
			return true;
		}
		else if (loader2 == null){
			return false;
		}
		else if (loader1 == null){
			return true;
		}
		else {

			for( ; loader2 != null; loader2 = loader2.getParent() ){
				if (loader2 == loader1){
					return true;
				}
			}

			return false;
		}
	}
}