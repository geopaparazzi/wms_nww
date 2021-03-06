/*
 * Geopaparazzi - Digital field mapping on Android based devices
 * Copyright (C) 2016  HydroloGIS (www.hydrologis.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gov.nasa.worldwind.util.xml;

import android.util.Log;

import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.util.Messages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

/**
 * Edited By: Nicola Dorigatti, Trilogis
 * 
 * @author tag
 * @version $Id: BasicXMLEventParserContext.java 771 2012-09-14 19:30:10Z tgaskins $
 */
public class BasicXMLEventParserContext extends AVListImpl implements XMLEventParserContext {
	/** The parser name of the default double parser. */
	public static QName DOUBLE = new QName("Double");
	/** The parser name of the default integer parser. */
	public static QName INTEGER = new QName("Integer");
	/** The parser name of the default string parser. */
	public static QName STRING = new QName("String");
	/** The parser name of the default boolean parser. */
	public static QName BOOLEAN = new QName("Boolean");
	/** The parser name of the default boolean integer parser. */
	public static QName BOOLEAN_INTEGER = new QName("BooleanInteger");
	/** The parser name of the unrecognized-element parser. */
	public static QName UNRECOGNIZED = new QName(UNRECOGNIZED_ELEMENT_PARSER);

	protected XMLEventReader reader;
	protected StringXMLEventParser stringParser;
	protected DoubleXMLEventParser doubleParser;
	protected IntegerXMLEventParser integerParser;
	protected BooleanXMLEventParser booleanParser;
	protected BooleanIntegerXMLEventParser booleanIntegerParser;
	protected String defaultNamespaceURI = XMLConstants.NULL_NS_URI;
	protected XMLParserNotificationListener notificationListener;
	protected ConcurrentHashMap<String, Object> idTable = new ConcurrentHashMap<String, Object>();

	protected ConcurrentHashMap<QName, XMLEventParser> parsers = new ConcurrentHashMap<QName, XMLEventParser>();

	/** Construct an instance. Invokes {@link #initializeParsers()} and {@link #initialize()}. */
	public BasicXMLEventParserContext() {
		this.initializeParsers();
		this.initialize();
	}

	/**
	 * Construct an instance for a specified event reader. Invokes {@link #initializeParsers()} and {@link #initialize()}.
	 * 
	 * @param eventReader
	 *            the event reader to use for XML parsing.
	 */
	public BasicXMLEventParserContext(XMLEventReader eventReader) {
		this.reader = eventReader;

		this.initializeParsers();
		this.initialize();
	}

	/**
	 * Construct an instance for a specified event reader and default namespace. Invokes {@link #initializeParsers()} and {@link #initialize()}.
	 * 
	 * @param eventReader
	 *            the event reader to use for XML parsing.
	 * @param defaultNamespace
	 *            the namespace URI of the default namespace.
	 */
	public BasicXMLEventParserContext(XMLEventReader eventReader, String defaultNamespace) {
		this.reader = eventReader;
		this.setDefaultNamespaceURI(defaultNamespace);

		this.initializeParsers();
		this.initialize();
	}

	public BasicXMLEventParserContext(BasicXMLEventParserContext ctx) {
		this.parsers = ctx.parsers;
		this.setDefaultNamespaceURI(ctx.getDefaultNamespaceURI());
		this.initialize();
	}

	protected void initialize() {
		this.initializeDefaultNotificationListener();
	}

	protected void initializeDefaultNotificationListener() {
		this.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent propEvent) {
				XMLParserNotification notification = (XMLParserNotification) propEvent;

				if (notificationListener != null) {
					notificationListener.notify(notification);
					return;
				}

				String msg;
				if (notification.getEvent() != null) {
					msg = Messages.getMessage(notification.getMessage(), notification.getEvent().toString(), notification.getEvent().getLineNumber());
				} else {
					msg = Messages.getMessage(notification.getMessage(), "", "");
				}

				if (notification.getPropertyName().equals(XMLParserNotification.EXCEPTION)) Log.w("NWW_ANDROID", msg);
				else if (notification.getPropertyName().equals(XMLParserNotification.UNRECOGNIZED)) Log.w("NWW_ANDROID", msg);
			}
		});
	}

	/**
	 * Initializes the parser table with the default parsers for the strings, integers, etc., qualified for the default
	 * namespace.
	 */
	protected void initializeParsers() {
		this.parsers.put(STRING, new StringXMLEventParser());
		this.parsers.put(DOUBLE, new DoubleXMLEventParser());
		this.parsers.put(INTEGER, new IntegerXMLEventParser());
		this.parsers.put(BOOLEAN, new BooleanXMLEventParser());
		this.parsers.put(BOOLEAN_INTEGER, new BooleanIntegerXMLEventParser());
		this.parsers.put(UNRECOGNIZED, new UnrecognizedXMLEventParser(null));
	}

	/**
	 * Add string parsers for a list of element types and qualified for a specified namespace.
	 * 
	 * @param namespace
	 *            the namespace URI.
	 * @param stringFields
	 *            the string parsers.
	 */
	protected void addStringParsers(String namespace, String[] stringFields) {
		StringXMLEventParser stringParser = this.getStringParser();
		for (String s : stringFields) {
			this.parsers.put(new QName(namespace, s), stringParser);
		}
	}

	/**
	 * Add double parsers for a list of element types and qualified for a specified namespace.
	 * 
	 * @param namespace
	 *            the namespace URI.
	 * @param doubleFields
	 *            the string parsers.
	 */
	protected void addDoubleParsers(String namespace, String[] doubleFields) {
		DoubleXMLEventParser doubleParser = this.getDoubleParser();
		for (String s : doubleFields) {
			this.parsers.put(new QName(namespace, s), doubleParser);
		}
	}

	/**
	 * Add integer parsers for a list of element types and qualified for a specified namespace.
	 * 
	 * @param namespace
	 *            the namespace URI.
	 * @param integerFields
	 *            the string parsers.
	 */
	protected void addIntegerParsers(String namespace, String[] integerFields) {
		IntegerXMLEventParser integerParser = this.getIntegerParser();
		for (String s : integerFields) {
			this.parsers.put(new QName(namespace, s), integerParser);
		}
	}

	/**
	 * Add boolean parsers for a list of element types and qualified for a specified namespace.
	 * 
	 * @param namespace
	 *            the namespace URI.
	 * @param booleanFields
	 *            the string parsers.
	 */
	protected void addBooleanParsers(String namespace, String[] booleanFields) {
		BooleanXMLEventParser booleanParser = this.getBooleanParser();
		for (String s : booleanFields) {
			this.parsers.put(new QName(namespace, s), booleanParser);
		}
	}

	/**
	 * Add boolean integer parsers for a list of element types and qualified for a specified namespace.
	 * 
	 * @param namespace
	 *            the namespace URI.
	 * @param booleanIntegerFields
	 *            the string parser.
	 */
	protected void addBooleanIntegerParsers(String namespace, String[] booleanIntegerFields) {
		BooleanIntegerXMLEventParser booleanIntegerParser = this.getBooleanIntegerParser();
		for (String s : booleanIntegerFields) {
			this.parsers.put(new QName(namespace, s), booleanIntegerParser);
		}
	}

	/**
	 * Returns the event reader used by this instance.
	 * 
	 * @return the instance's event reader.
	 */
	public XMLEventReader getEventReader() {
		return this.reader;
	}

	/**
	 * Specify the event reader for the parser context to use to parse XML.
	 * 
	 * @param reader
	 *            the event reader to use.
	 */
	public void setEventReader(XMLEventReader reader) {
		if (reader == null) {
			String message = Messages.getMessage("nullValue.EventIsNull"); // TODO
			Log.e("NWW_ANDROID", message);
			throw new IllegalArgumentException(message);
		}

		this.reader = reader;
	}

	public String getDefaultNamespaceURI() {
		return defaultNamespaceURI;
	}

	public void setDefaultNamespaceURI(String defaultNamespaceURI) {
		this.defaultNamespaceURI = defaultNamespaceURI;
	}

	public void setNotificationListener(XMLParserNotificationListener listener) {
		this.notificationListener = listener;
	}

	public Map<String, Object> getIdTable() {
		return this.idTable;
	}

	public void addId(String id, Object o) {
		if (id != null) this.getIdTable().put(id, o);
	}

	public XMLEvent nextEvent() throws XMLParserException {
		XMLEvent event = this.getEventReader().nextEvent();
		while (event != null) {
			if (event.isCharacters() && event.isWhiteSpace()) {
				event = this.getEventReader().nextEvent();
				continue;
			}

			return event;
		}

		return null;
	}

	public XMLEventParser allocate(XMLEvent event, XMLEventParser defaultParser) {
		return this.getParser(event, defaultParser);
	}

	public XMLEventParser allocate(XMLEvent event) {
		return this.getParser(event, null);
	}

	public XMLEventParser getParser(XMLEvent event) {
		return this.getParser(event, null);
	}

	protected XMLEventParser getParser(XMLEvent event, XMLEventParser defaultParser) {
		if (event == null) {
			String message = Messages.getMessage("nullValue.EventIsNull");
			Log.e("NWW_ANDROID", message);
			throw new IllegalArgumentException(message);
		}

		QName elementName = event.getName();
		if (elementName == null) return null;

		XMLEventParser parser = this.getParser(elementName);

		return parser != null ? parser : defaultParser;
	}

	public StringXMLEventParser getStringParser() {
		if (this.stringParser == null) this.stringParser = (StringXMLEventParser) this.getParser(STRING);

		return this.stringParser;
	}

	public DoubleXMLEventParser getDoubleParser() {
		if (this.doubleParser == null) this.doubleParser = (DoubleXMLEventParser) this.getParser(DOUBLE);

		return this.doubleParser;
	}

	public IntegerXMLEventParser getIntegerParser() {
		if (this.integerParser == null) this.integerParser = (IntegerXMLEventParser) this.getParser(INTEGER);

		return this.integerParser;
	}

	public BooleanXMLEventParser getBooleanParser() {
		if (this.booleanParser == null) this.booleanParser = (BooleanXMLEventParser) this.getParser(BOOLEAN);

		return this.booleanParser;
	}

	public BooleanIntegerXMLEventParser getBooleanIntegerParser() {
		if (this.booleanIntegerParser == null) this.booleanIntegerParser = (BooleanIntegerXMLEventParser) this.getParser(BOOLEAN_INTEGER);

		return this.booleanIntegerParser;
	}

	/**
	 * Returns a parser to handle unrecognized elements. The default unrecognized event parser is {@link gov.nasa.worldwind.util.xml.UnrecognizedXMLEventParser}
	 * , and may be replaced by calling {@link #registerParser(QName, XMLEventParser)} and specifying {@link #UNRECOGNIZED} as the parser
	 * name.
	 * 
	 * @return a parser to handle unrecognized elements.
	 */
	public XMLEventParser getUnrecognizedElementParser() {
		return this.getParser(UNRECOGNIZED);
	}

	public String getCharacters(XMLEvent event) {
		if (event == null) {
			String message = Messages.getMessage("nullValue.EventIsNull");
			Log.e("NWW_ANDROID", message);
			throw new IllegalArgumentException(message);
		}

		return event.isCharacters() ? event.getData() : null;
	}

	public boolean isSameName(QName qa, QName qb) {
		if (qa.equals(qb)) return true;

		if (!qa.getLocalPart().equals(qb.getLocalPart())) return false;

		if (qa.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) return qb.getNamespaceURI().equals(this.getDefaultNamespaceURI());

		if (qb.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) return qa.getNamespaceURI().equals(this.getDefaultNamespaceURI());

		return false;
	}

	public boolean isSameAttributeName(QName qa, QName qb) {
		return qa != null && qb != null && qa.getLocalPart() != null && qa.getLocalPart().equals(qb.getLocalPart());
	}

	public boolean isStartElement(XMLEvent event, QName elementName) {
		if (event == null) {
			String message = Messages.getMessage("nullValue.EventIsNull");
			Log.e("NWW_ANDROID", message);
			throw new IllegalArgumentException(message);
		}

		if (elementName == null) {
			String message = Messages.getMessage("nullValue.ElementNameIsNull");
			Log.e("NWW_ANDROID", message);
			throw new IllegalArgumentException(message);
		}

		return (event.isStartElement() && this.isSameName(event.getName(), elementName));
	}

	public boolean isEndElement(XMLEvent event, XMLEvent startElement) {
		if (event == null || startElement == null) {
			String message = Messages.getMessage("nullValue.EventIsNull");
			Log.e("NWW_ANDROID", message);
			throw new IllegalArgumentException(message);
		}

		return isEndElementEvent(event, startElement);
	}

	public static boolean isEndElementEvent(XMLEvent event, XMLEvent startElement) {
		if (event == null || startElement == null) {
			String message = Messages.getMessage("nullValue.EventIsNull");
			Log.e("NWW_ANDROID", message);
			throw new IllegalArgumentException(message);
		}

		return (event.isEndElement() && event.getName().equals(startElement.getName()));
	}

	public void registerParser(QName elementName, XMLEventParser parser) {
		if (parser == null) {
			String message = Messages.getMessage("nullValue.ParserIsNull");
			Log.e("NWW_ANDROID", message);
			throw new IllegalArgumentException(message);
		}

		if (elementName == null) {
			String message = Messages.getMessage("nullValue.ElementNameIsNull");
			Log.e("NWW_ANDROID", message);
			throw new IllegalArgumentException(message);
		}

		this.parsers.put(elementName, parser);
	}

	public XMLEventParser getParser(QName name) {
		if (name == null) {
			String message = Messages.getMessage("nullValue.ElementNameIsNull");
			Log.e("NWW_ANDROID", message);
			throw new IllegalArgumentException(message);
		}

		XMLEventParser factoryParser = this.parsers.get(name);
		if (factoryParser == null) {
			// Try alternate forms that assume a default namespace in either the input name or the table key.
			if (isNullNamespace(name.getNamespaceURI())) {
				// input name has no namespace but table key has the default namespace
				QName altName = new QName(this.getDefaultNamespaceURI(), name.getLocalPart());
				factoryParser = this.parsers.get(altName);
			} else if (this.isDefaultNamespace(name.getNamespaceURI())) {
				// input name has the default namespace but table name has no namespace
				QName altName = new QName(name.getLocalPart());
				factoryParser = this.parsers.get(altName);
			}
		}

		try {
			if (factoryParser == null) return null;

			return factoryParser.newInstance();
		} catch (Exception e) {
			String message = Messages.getMessage("XML.ParserCreationException", name);
			Log.w("NWW_ANDROID", message, e);
			return null;
		}
	}

	protected static boolean isNullNamespace(String namespaceURI) {
		return namespaceURI == null || XMLConstants.NULL_NS_URI.equals(namespaceURI);
	}

	public boolean isDefaultNamespace(String namespaceURI) {
		return this.getDefaultNamespaceURI() != null && this.getDefaultNamespaceURI().equals(namespaceURI);
	}

	@Deprecated
	public void resolveInternalReferences(String referenceName, String fieldName, AbstractXMLEventParser parser) {
		if (parser == null || !parser.hasFields()) return;

		Map<String, Object> newFields = null;

		for (Map.Entry<String, Object> p : parser.getFields().getEntries()) {
			String key = p.getKey();
			if (key == null || key.equals("id")) continue;

			Object v = p.getValue();
			if (v == null) continue;

			if (v instanceof String) {
				String value = (String) v;

				if (value.startsWith("#") && key.endsWith(referenceName)) {
					Object o = this.getIdTable().get(value.substring(1, value.length()));
					if (/* o instanceof KMLStyle && */!parser.hasField(fieldName)) {
						if (newFields == null) newFields = new HashMap<String, Object>();
						newFields.put(fieldName, o);
					}
				}
			}
		}

		if (newFields != null) parser.setFields(newFields);
	}
}
