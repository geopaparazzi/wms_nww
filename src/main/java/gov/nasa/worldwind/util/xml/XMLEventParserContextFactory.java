/*
 * Copyright (C) 2012 DreamHammer.com
 */

package gov.nasa.worldwind.util.xml;

import android.util.Log;

import gov.nasa.worldwind.util.Messages;

import javax.xml.XMLConstants;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author tag
 * @version $Id: XMLEventParserContextFactory.java 771 2012-09-14 19:30:10Z tgaskins $
 */
public class XMLEventParserContextFactory
{
    /** Holds the mime types and the associated prototype parser. */
    protected static class ParserTableEntry
    {
        /** The mime types for which the associated parser should be used. */
        protected List<String> mimeTypes = new ArrayList<String>();
        /**
         * A prototype parser able to construct a copy of itself. The copy typically shares the prototype's parser table
         * and may also share other internal fields.
         */
        XMLEventParserContext prototypeParser;

        /**
         * Construct an instance for a specified list of mime types and a specified prototype parser.
         *
         * @param mimeTypes        the list of mime types for which to use the specified prototype parser context.
         * @param prototypeContext the prototype parser context to use for the specified mime types. This parser
         *                         context's class must provide a copy constructor, a constructor that takes an instance
         *                         of its class as its only argument.
         *
         * @throws IllegalArgumentException if the mime type list is null or empty or the prototype context is null or
         *                                  has no copy constructor.
         */
        public ParserTableEntry(String[] mimeTypes, XMLEventParserContext prototypeContext)
        {
            for (String mimeType : mimeTypes)
            {
                this.mimeTypes.add(mimeType);
            }

            this.prototypeParser = prototypeContext;

            // Ensure the prototype has a copy constructor
            try
            {
                prototypeContext.getClass().getConstructor(prototypeContext.getClass());
            }
            catch (NoSuchMethodException e)
            {
                String message = Messages.getMessage("XML.NoCopyConstructor");
                Log.e("NWW_ANDROID", message);
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * The list of registered parser contexts. A copy-on-write list is used to avoid the need to explicitly synchronize
     * access.
     */
    protected static List<ParserTableEntry> parsers = new CopyOnWriteArrayList<ParserTableEntry>();

    static
    {
//        // Register a KML parser context for the default KML namespace and one for the empty namespace.
//        String[] mimeTypes = new String[] {KMLConstants.KML_MIME_TYPE, KMLConstants.KMZ_MIME_TYPE};
//        parsers.add(new ParserTableEntry(mimeTypes, new KMLParserContext(KMLConstants.KML_NAMESPACE)));
//        parsers.add(new ParserTableEntry(mimeTypes, new KMLParserContext(XMLConstants.NULL_NS_URI)));
    }

    /**
     * Appends a specified prototype parser context to the list of those already registered.
     *
     * @param mimeTypes        the list of mime types for which to use the specified prototype parser context.
     * @param prototypeContext the prototype parser context to use for the specified mime types. This parser context's
     *                         class must provide a copy constructor, a constructor that takes an instance of its class
     *                         as its only argument.
     *
     * @throws IllegalArgumentException if the mime type list is null or empty or the prototype context is null or has
     *                                  no copy constructor.
     */
    public static void addParserContext(String[] mimeTypes, XMLEventParserContext prototypeContext)
    {
        if (mimeTypes == null || mimeTypes.length == 0)
        {
            String message = Messages.getMessage("nullValue.MimeTypeListIsNull");
            Log.e("NWW_ANDROID", message);
            throw new IllegalArgumentException(message);
        }

        if (prototypeContext == null)
        {
            String message = Messages.getMessage("nullValue.ParserContextIsNull");
            Log.e("NWW_ANDROID", message);
            throw new IllegalArgumentException(message);
        }

        parsers.add(new ParserTableEntry(mimeTypes, prototypeContext));
    }

    /**
     * Prepends a specified prototype parser context to the list of those already registered. Because the new entry is
     * prepended to the list of registered parsers, it will be the first match for the specified mime types.
     *
     * @param mimeTypes        the list of mime types for which to use the specified prototype parser context.
     * @param prototypeContext the prototype parser context to use for the specified mime types. This parser context's
     *                         class must provide a copy constructor, a constructor that takes an instance of its class
     *                         as its only argument.
     *
     * @throws IllegalArgumentException if the mime type list is null or empty or the prototype context is null or has
     *                                  no copy constructor.
     */
    public static void prependParserContext(String[] mimeTypes, XMLEventParserContext prototypeContext)
    {
        if (mimeTypes == null || mimeTypes.length == 0)
        {
            String message = Messages.getMessage("nullValue.MimeTypeListIsNull");
            Log.e("NWW_ANDROID", message);
            throw new IllegalArgumentException(message);
        }

        if (prototypeContext == null)
        {
            String message = Messages.getMessage("nullValue.ParserContextIsNull");
            Log.e("NWW_ANDROID", message);
            throw new IllegalArgumentException(message);
        }

        parsers.add(0, new ParserTableEntry(mimeTypes, prototypeContext));
    }

    /**
     * Constructs and returns a parser context for a specified mime type and namespace. The list of registered parser
     * contexts is searched from first to last. A parser context is constructed from the first entry matching the
     * specified mime type and namespace. Null is returned if no match is found.
     * <p/>
     * Note that the empty namespace, {@link XMLConstants#NULL_NS_URI} does not match any other namespace. In order for
     * a parser context with the empty namespace to be returned, one with the empty namespace must be registered.
     *
     * @param mimeType         the mime type for which to construct a parser.
     * @param defaultNamespace a namespace qualifying the parser context to return. May be null, in which case a parser
     *                         context for the specified mime type and an empty namespace, {@link
     *                         XMLConstants#NULL_NS_URI}, is searched for.
     *
     * @return a new parser context constructed from the prototype context registered for the specified mime type and
     *         having the specified default namespace.
     *
     * @throws IllegalArgumentException if the specified mime type is null.
     */
    public static XMLEventParserContext createParserContext(String mimeType, String defaultNamespace)
    {
        if (mimeType == null)
        {
            String message = Messages.getMessage("nullValue.MimeTypeIsNull");
            Log.e("NWW_ANDROID", message);
            throw new IllegalArgumentException(message);
        }

        for (ParserTableEntry entry : parsers)
        {
            for (String entryMimeType : entry.mimeTypes)
            {
                if (entryMimeType.equals(mimeType))
                {
                    String ns = entry.prototypeParser.getDefaultNamespaceURI();
                    ns = ns != null ? ns : XMLConstants.NULL_NS_URI;
                    defaultNamespace = defaultNamespace != null ? defaultNamespace : XMLConstants.NULL_NS_URI;

                    if (ns.equals(defaultNamespace))
                        try
                        {
                            return createInstanceFromPrototype(entry.prototypeParser);
                        }
                        catch (Exception e)
                        {
                            String message = Messages.getMessage("XML.ExceptionCreatingParserContext", e.getMessage());
                            Log.w("NWW_ANDROID", message);
                            // continue on to subsequent entries
                        }
                }
            }
        }

        return null;
    }

    /**
     * Constructs a new parser context given a prototype parser context.
     *
     * @param prototype the prototype parser context. This parser context's class must provide a copy constructor, a
     *                  constructor that takes an instance of the class as its only argument.
     *
     * @return the new parser context.
     *
     * @throws Exception if an exception occurs while attempting to construct the new context.
     */
    protected static XMLEventParserContext createInstanceFromPrototype(XMLEventParserContext prototype)
        throws Exception
    {
        Constructor<? extends XMLEventParserContext> constructor;
        constructor = prototype.getClass().getConstructor(prototype.getClass());

        return constructor.newInstance(prototype);
    }
}
