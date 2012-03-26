package org.jicengine;
import org.jicengine.io.Resource;
import java.util.Map;
import org.xml.sax.SAXException;
import java.io.IOException;

/**
 * <p>
 * The main class. Provides:
 * </p>
 * <ul>
 *  <li>Provides methods for processing JIC files.</li>
 *  <li>Contains the <code>main</code> method for using JIC Engine from the command-line.</li>
 *  <li>Acts as a factory for obtaining <code>Builder</code> instances.</li>
 * </ul>
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 *
 */

public class JICEngine {

	private static Builder INSTANCE = new org.jicengine.builder.BuilderImpl();

	/**
	 * Returns a Builder instance that is used for processing JIC files.
	 */
	public static Builder getBuilder()
	{
		return INSTANCE;
	}

	private JICEngine()
	{
	}

	/**
	 * <p>
	 * Processes a JIC file and returns the result. Use this for processing JIC
	 * files.
	 * </p>
	 *
	 * <p>
	 * An alternative way is to first obtain a <code>Builder</code> with
	 * <code>getBuilder</code> method and then use it to process a file.
	 * </p>
	 *
	 * <p>
	 * For details, see Builder class.
	 * </p>
	 *
	 * @see org.jicengine.Builder#build
	 */
	public static Object build(Instructions instructions) throws IOException, SAXException, JICException
	{
		return getBuilder().build(instructions);
	}

	/**
	 * @param buildParameters  may be null if there are no parameters.
	 */
	public static Object build(Resource jicFile, Map buildParameters) throws IOException, SAXException, JICException
	{
		return build(new Instructions(jicFile, buildParameters));
	}

	public static Object build(Resource jicFile) throws IOException, SAXException, JICException
	{
		return build(jicFile, null);
	}

	/**
	 * <p>
	 * command-line interface for processing a jic-file.
	 * </p>
	 * <p>format:
	 * <code>java org.jicengine.JICE -jic [jic-file-path] [-param name1 value1] [-param name2 value2] ..</code>
	 * </p>
	 */
	public static void main(String[] args) throws Exception
	{
		if( args.length == 0 ){
			throw new IllegalArgumentException("Usage: java org.jicengine.JICE -jic jic-file [-param name1 value1] [-param name2 value2] ..");
		}

		String jicFile = null;
		Map parameters = new java.util.HashMap();


		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if( arg.startsWith("-") ){
				// ok, this is what was expected
				String name = arg.substring(1);

				if( name.equals("jic") ){
					i++;
					jicFile = getArgument(args, i, "expected '-jic' to be followed by a path.");
				}
				else if( name.equals("param") ){
					i++;
					String paramName = getArgument(args, i, "expected '-param' to be followed by the param name.");
					i++;
					String paramValue = getArgument(args, i, "expected '-param " + paramName + " to be followed by the param value.");
					parameters.put(paramName, paramValue);
				}
				else {
					throw new IllegalArgumentException("unknown argument: " + name);
				}
			}
			else {
				throw new IllegalArgumentException("expected an argument-name starting with '-', got '" + arg + "'");
			}
		}

		if( jicFile == null ){
			throw new IllegalArgumentException("argument -jic [jic-file] not specified.");
		}

		long now = System.currentTimeMillis();
		Object result = build(new org.jicengine.io.FileResource(new java.io.File(jicFile)), parameters);
		long elapsed = System.currentTimeMillis() - now;
		System.out.println("[JICE]: '" + jicFile + "' processed in " + elapsed + " ms. result:");
		System.out.println("  " + result);
		if( result != null ){
			System.out.println("  [" + result.getClass().getName() + "]");
		}
	}

	private static String getArgument(String[] args, int index, String errorMessage)
	{
		try {
			return args[index];
		} catch (ArrayIndexOutOfBoundsException e){
			throw new IllegalArgumentException(errorMessage);
		}
	}
}
