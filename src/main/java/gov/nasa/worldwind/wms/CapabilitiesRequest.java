/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.wms;

import android.util.Log;

import gov.nasa.worldwind.util.Messages;

import java.net.*;

/**
 * @author tag
 * @version $Id: CapabilitiesRequest.java 733 2012-09-02 17:15:09Z dcollins $
 */
public final class CapabilitiesRequest extends Request
{
    /** Construct an OGC GetCapabilities request using the default service. */
    public CapabilitiesRequest()
    {
    }

    /**
     * Constructs a request for the default service, WMS, and a specified server.
     *
     * @param uri the address of the web service.
     *
     * @throws IllegalArgumentException if the uri is null.
     * @throws URISyntaxException       if the web service address is not a valid URI.
     */

    public CapabilitiesRequest(URI uri) throws URISyntaxException
    {
        super(uri, null);

        if (uri == null)
        {
            String message = Messages.getMessage("nullValue.URIIsNull");
            Log.e("NWW_ANDROID", message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Constructs a request for a specified service at a specified server.
     *
     * @param uri     the address of the web service.
     * @param service the service name. Common names are WMS, WFS, WCS, etc.
     *
     * @throws IllegalArgumentException if the uri or service name is null.
     * @throws URISyntaxException       if the web service address is not a valid URI.
     */
    public CapabilitiesRequest(URI uri, String service) throws URISyntaxException
    {
        super(uri, service);

        if (uri == null)
        {
            String message = Messages.getMessage("nullValue.URIIsNull");
            Log.e("NWW_ANDROID", message);
            throw new IllegalArgumentException(message);
        }

        if (service == null)
        {
            String message = Messages.getMessage("nullValue.WMSServiceNameIsNull");
            Log.e("NWW_ANDROID", message);
            throw new IllegalArgumentException(message);
        }
    }

    protected void initialize(String service)
    {
        super.initialize(service);
        this.setParam("REQUEST", "GetCapabilities");
        this.setParam("VERSION", "1.3.0");
    }
}
