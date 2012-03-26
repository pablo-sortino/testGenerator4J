package org.jicengine.builder;

import org.jicengine.operation.Context;
import org.jicengine.operation.SimpleContext;
import org.jicengine.operation.BeanUtils;
import org.jicengine.operation.OperationException;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import java.util.*;
import org.jicengine.element.*;
import org.jicengine.element.impl.*;
import org.jicengine.expression.*;
import org.jicengine.Instructions;
import org.jicengine.BuildContext;

/**
 *
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 *
 * @author    .timo
 */

public class Handler extends DefaultHandler {

//	public static final String NAMESPACE_URI_JICE_1_0 = "http://www.jicengine.org/jic/1.0";
	public static final String NAMESPACE_URI_JICE_2_0 = "http://www.jicengine.org/jic/2.0";
  public static final String NAMESPACE_URI_JICE_2_1 = "http://www.jicengine.org/jic/2.1";  

	private Stack elementCompilers = new Stack();
	private Element rootElement;

  private String jiceNameSpace;
	private String jiceNamespacePrefix = "";
  boolean syntaxBasedCdataConversionSupport = false;
	private boolean defaultNamespaceUsed = true;

	StringBuffer cdataContent = new StringBuffer();
	Locator locator;

	public Handler()
	{
	}

	public Element getResult() throws ElementException
	{
		if( this.rootElement == null ){
			throw new IllegalStateException("root element is null.");
		}

		return this.rootElement;
	}

	public void setDocumentLocator(Locator locator)
	{
		this.locator = locator;
	}

	public void startDocument() throws org.xml.sax.SAXException
	{
	}

	public void endDocument() throws org.xml.sax.SAXException
	{
	}

	public void startPrefixMapping(String prefix, String uri) throws org.xml.sax.SAXException
	{
		if( uri.equals(NAMESPACE_URI_JICE_2_0) || uri.equals(NAMESPACE_URI_JICE_2_1) ){
			// a valid JICE namespace prefix has been specified.
      this.jiceNameSpace = uri;
			this.jiceNamespacePrefix = prefix;
			if( this.jiceNamespacePrefix == null || this.jiceNamespacePrefix.length() == 0){
				this.defaultNamespaceUsed = true;
			}
			else {
				this.defaultNamespaceUsed = false;
			}
      
      if( uri.equals(NAMESPACE_URI_JICE_2_1)){
        syntaxBasedCdataConversionSupport = true;
      }  
		}
	}

	public void endPrefixMapping(String prefix) throws org.xml.sax.SAXException
	{
		// should we do something if the current org.jicengine-namespace-prefix-mapping ends?
		// or are we informed with a startPrefixMapping()-call of the future
		// prefixes?
	}

	/**
	 * @param jiceAttributeName the local name of a JICE-attribute e.g. 'action'.
	 */
	protected String getAttribute(Attributes attributes, String jiceAttributeName)
	{
		String value = attributes.getValue(jiceAttributeName);
		if( value == null ){
			// ok, lets try to specify the namespace also.
			value = attributes.getValue(this.jiceNameSpace, jiceAttributeName);
		}
		return value;
	}

	public void startElement(String nameSpaceUri, String localName, String qName, Attributes attributes) throws org.xml.sax.SAXException
	{
		try {
			doStartElement(nameSpaceUri,localName,qName,attributes);
		} catch (Exception e){
			throw new org.xml.sax.SAXException(e);
		}
	}

	protected void doStartElement(String nameSpaceUri, String localName, String qName, Attributes attributes) throws Exception, org.xml.sax.SAXException
	{
		Location location;
		if( this.locator != null ){
			location = new Location(this.locator.getLineNumber(), locator.getSystemId(), this.elementCompilers.size());
		}
		else {
			location = new Location(-1, "?", 0);
		}

		if( !nameSpaceUri.equals(NAMESPACE_URI_JICE_2_0) && !nameSpaceUri.equals(NAMESPACE_URI_JICE_2_1)){
			throw new ElementException("Element '" + localName +"' is in illegal namespace '" + nameSpaceUri + "'. only '" + NAMESPACE_URI_JICE_2_0 + "' and '" + NAMESPACE_URI_JICE_2_1 + "' supported.", localName, location);
		}
		final ElementCompiler current;

    Type type;
		String typeExpression = getAttribute(attributes, ElementCompiler.ATTR_NAME_TYPE);
		if( typeExpression != null ){
      type = Type.parse(typeExpression);
    }
    else {
			type = DefaultJiceTypes.getDefaultType();
		}

		current = DefaultJiceTypes.getTypeManager().createCompiler(localName, location, type);

		// ATTRIBUTE HANDLING

		for (int i = 0; i < attributes.getLength(); i++) {

      String attrName = attributes.getLocalName(i);
      String attrValue = attributes.getValue(i);
      String attrUri = attributes.getURI(i);

			if( attrName.length() == 0 ){
				// attribute 'xmlns'. ignore
				continue;
			}
			else if( attrUri.length() == 0 && !attributes.getQName(i).startsWith("xmlns") ){
        // attribute has no namespace 
        // -> since we are inside a JIC element,
        // the attribute is a JIC attribute 
				setAttribute(attrName, attrValue, current);
			}
			else if( attrUri.equals(NAMESPACE_URI_JICE_2_0) || attrUri.equals(NAMESPACE_URI_JICE_2_1)){
        // attribute belongs to JICE namespace
        // -> definitely a JIC attribute
				setAttribute(attrName, attrValue, current);
			}
			else {
				//System.out.println("ignored: " + attrName + "=\"" + attrValue + "\"");
        // attribute belongs to some unknown namespace
        // -> just ignore it
        continue;
			}
		}


		// tell the element that it's attributes have been set
		current.elementInitialized();

		this.elementCompilers.push(current);
	}

	protected void setAttribute(String name, String value, ElementCompiler elementCompiler) throws Exception
	{
		if( name.equals(ElementCompiler.ATTR_NAME_CLASS) ){
			elementCompiler.setInstanceClass(value);
		}
		else if( name.equals(ElementCompiler.ATTR_NAME_TYPE) ){
			// nothing..
		}
		else if( name.equals(ElementCompiler.ATTR_NAME_INSTANCE) ){
			elementCompiler.setConstructor(value);
		}
		else if( name.equals(ElementCompiler.ATTR_NAME_ACTION) ){
			elementCompiler.setAction(value);
		}
		else if( name.equals(ElementCompiler.ATTR_NAME_CONSTRUCTOR_ARGUMENTS) ){
			elementCompiler.setConstructorArguments(value);
		}
		else if( name.equals(ElementCompiler.ATTR_NAME_VARIABLES) ){
			elementCompiler.setVariables(value);
		}
		else if( name.equals(ElementCompiler.ATTR_NAME_IF) ){
			elementCompiler.setIf(value);
		}
		else if( name.equals(ElementCompiler.ATTR_NAME_OVERRIDABLE_BY) ){
			elementCompiler.setOverridableBy(value);
		}
		else {
			throw new ElementException("Unsupported attribute: " + name + "=" + value, elementCompiler.getName(), elementCompiler.getLocation());
		}
	}

	public void endElement(String namespaceUri, String localName, String qName) throws org.xml.sax.SAXException
	{
		try {
			doEndElement(namespaceUri,localName, qName);
		} catch (ElementException e){
			throw new org.xml.sax.SAXException(e);
		} catch (RuntimeException e2){
			throw new org.xml.sax.SAXException(new org.jicengine.JICException("Unexpected runtime exception at line " + this.locator.getLineNumber(), e2));
		} catch (Exception e3) {
			throw new org.xml.sax.SAXException(e3);
		}
	}

	protected void doEndElement(String namespaceUri, String localName, String qName) throws Exception, org.xml.sax.SAXException
	{
		ElementCompiler current = (ElementCompiler) this.elementCompilers.pop();

		// cdata of the element
		//
		// NOTE: because we clear the buffer here, there can't be nested
		// cdata-blocks. Consider the following situation:
		// --------
		// <parent>
		//  some text as CData
		//  <child>text of the child-element</child>
		// </parent>
		// --------
		// in that case, the child will receive all the cdata,also
		// the cdata of the parent, and the parent won't receive any
		// cdata at all (the child has consumed it all).
		String cdata = cdataContent.toString().trim();
		if( cdata.length() > 0 ){
			current.setCData(cdata, this.syntaxBasedCdataConversionSupport);
		}
		cdataContent = new StringBuffer();

		// element processed.. let the compiler create the element.

		Element element = current.createElement();

		if( !this.elementCompilers.isEmpty() ){
			ElementCompiler parent = (ElementCompiler) this.elementCompilers.peek();

			// let the parent handle the element
			parent.handleChildElement(element);
		}
		else {
			// no parents: this is the root element?
			this.rootElement = element;
		}

	}

	public void characters(char[] cdata, int start, int length) throws org.xml.sax.SAXException
	{
		this.cdataContent.append(cdata, start, length);
	}

	public void ignorableWhitespace(char[] parm1, int parm2, int parm3) throws org.xml.sax.SAXException
	{
	}

	public void processingInstruction(String parm1, String parm2) throws org.xml.sax.SAXException
	{
	}

	public void skippedEntity(String parm1) throws org.xml.sax.SAXException
	{
	}
}
