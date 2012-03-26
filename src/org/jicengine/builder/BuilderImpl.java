package org.jicengine.builder;
import javax.xml.parsers.*;
import org.xml.sax.SAXException;
import org.jicengine.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Iterator;
import org.jicengine.element.Element;
import org.jicengine.element.ElementException;
import org.jicengine.element.VariableElement;
import org.jicengine.element.ActionElement;
import org.jicengine.io.Resource;
import org.jicengine.io.UrlReadable;
import org.jicengine.operation.Context;
import org.jicengine.operation.SimpleContext;
/**
 *
 *
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

public class BuilderImpl implements Builder {

	private static boolean log = false;
	//Boolean.valueOf(System.getProperty("org.jicengine.Builder.log", "false")).booleanValue();

	public BuilderImpl() {
	}

	public Object build(Instructions instructions) throws IOException, SAXException, JICException
	{

		// parse
		long begin = System.currentTimeMillis();
		Element rootElement = parse(instructions);
		long parseTime = System.currentTimeMillis() - begin;

		// execute the elements and return the result.
		begin = System.currentTimeMillis();
		Object result = execute(rootElement, instructions);
		long execTime = System.currentTimeMillis() - begin;

		if( log ){
			System.out.println("[" + instructions.getFile() + "]:");
			System.out.println("  parse time : " + parseTime);
			System.out.println("  exec time  : " + execTime);
			System.out.println("  total time : " + (parseTime+execTime));
			System.out.println();
		}

		return result;
	}

	private SAXParser getSAXParser() throws JICException
	{
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setFeature("http://xml.org/sax/features/namespaces", true);
			factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
			SAXParser parser = factory.newSAXParser();
			return parser;
		} catch (Exception e){
			throw new JICException("Failed to obtain a SAX parser", e);
		}
	}

	private Element parse(Instructions instructions) throws IOException, SAXException, JICException
	{
		SAXParser parser = getSAXParser();
		Handler handler = new Handler();
		//XMLReader
    
		try {
			parser.parse(instructions.getFile().getInputStream(), handler, getSystemIdentifier(instructions));
			return handler.getResult();
		} catch (org.xml.sax.SAXException e){
			Exception cause = e.getException();
			if( cause != null ){
				if( cause instanceof JICException ){
					throw (JICException) cause;
				}
				else {
					throw new JICException(cause);
				}
			}
			else {
				// a SAX exception without a nested exception. throw it as is.
				throw e;
			}
		}
	}

  private static String getSystemIdentifier(Instructions instructions)
  {
    Resource resource = instructions.getFile();
    String systemIdentifier;
    if( resource instanceof UrlReadable){
      try {
        systemIdentifier = ((UrlReadable)resource).getUrl().toString();
      } catch (IOException e){
        System.out.println(new java.util.Date()+ " [JICE]: failed to create resource URL, using identifier '" + resource.getIdentifier() + "' as system identifier");
        systemIdentifier = resource.getIdentifier();
      }
    }
    else {
      systemIdentifier = resource.getIdentifier();
    }
    
    return systemIdentifier;
  }
  
	private Object execute(Element rootElement, Instructions buildInstructions) throws ElementException
	{
		Context rootContext = new SimpleContext("//");
		BuildContext jiceContext = new BuildContextImpl(buildInstructions);
		rootContext.addObject(BuildContext.VARIABLE_NAME, jiceContext);

		// reserve the variable names 'true' and 'false' to boolean values.
		rootContext.addObject("true", Boolean.TRUE);
		rootContext.addObject("false", Boolean.FALSE);

		Map buildParameters = buildInstructions.getParameters();
		for (Iterator it = buildParameters.keySet().iterator() ; it.hasNext();) {
			String key = it.next().toString();
			rootContext.addObject(org.jicengine.expression.BuildParameterParser.PREFIX + key, buildParameters.get(key));
		}

		if( rootElement.isExecuted(rootContext,null) ){

			if (rootElement instanceof VariableElement) {
				return ((VariableElement) rootElement).getValue(rootContext, null);
			} else if (rootElement instanceof ActionElement) {
				((ActionElement) rootElement).execute(rootContext, null);
				return null;
			} else {
				throw new IllegalStateException("unknown element type: " + rootElement);
			}
		}
		else {
			// element not executed. return null.
			return null;
		}
	}

	public static void main(String[] args) throws Exception
	{
		if( args.length == 0 ){
			System.out.println("give the org.jicengine-file as an argument.");
		}
		else {
			File file = new File(args[0]);
			System.out.println("-------------------");
			System.out.println("Processing file '" + file + "'");
			System.out.println("-------------------");
			java.util.Map parameters = new java.util.HashMap();
			//parameters.put("language.code", "fi");
			Object result = new BuilderImpl().build(new Instructions(new org.jicengine.io.FileResource(file),parameters));

			System.out.println("-------------------");
			System.out.println("Got object:");
			System.out.println("  " + result);
			System.out.println("  (" + result.getClass().getName() + ")");
			System.out.println("-------------------");
		}

		/*
		javax.swing.JFrame frame = new javax.swing.JFrame("Anne on taas kiva");
		frame.setLocation(300,300);
		frame.setSize(200,200);
		frame.show();
		*/
	}
}
