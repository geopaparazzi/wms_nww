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
package gov.nasa.worldwind.avlist;

import android.util.Log;

import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.util.*;

import java.beans.*;
import java.util.*;

/**
 * An implementation class for the {@link AVList} interface. Classes implementing <code>AVList</code> can subclass or
 * aggregate this class to provide default <code>AVList</code> functionality. This class maintains a hash table of
 * attribute-value pairs.
 * <p/>
 * This class implements a notification mechanism for attribute-value changes. The mechanism provides a means for
 * objects to observe attribute changes or queries for certain keys without explicitly monitoring all keys. See {@link
 * PropertyChangeSupport}.
 *
 * @author dcollins
 * @version $Id: AVListImpl.java 753 2012-09-06 02:40:57Z tgaskins $
 */
public class AVListImpl implements AVList
{
    // Identifies the property change support instance in the avlist
    protected static final String PROPERTY_CHANGE_SUPPORT = "avlist.PropertyChangeSupport";

    // To avoid unnecessary overhead, this object's hash map is created only if needed.
    protected Map<String, Object> avList;

    /** Creates an empty attribute-value list. */
    public AVListImpl()
    {
    }

    /**
     * Constructor enabling aggregation.
     *
     * @param sourceBean The bean to be given as the source for any events.
     */
    public AVListImpl(Object sourceBean)
    {
        if (sourceBean != null)
            this.setValue(PROPERTY_CHANGE_SUPPORT, new PropertyChangeSupport(sourceBean));
    }

    public synchronized Object getValue(String key)
    {
        if (key == null)
        {
            String msg = Messages.getMessage("nullValue.KeyIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }

        if (this.hasAvList())
            return this.avList.get(key);

        return null;
    }

    public synchronized String getStringValue(String key)
    {
        if (key == null)
        {
            String msg = Messages.getMessage("nullValue.KeyIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalStateException(msg);
        }
        try
        {
            return (String) this.getValue(key);
        }
        catch (ClassCastException e)
        {
            String msg = Messages.getMessage("generic.ValueForKeyIsNotAString", key, this.getValue(key));
            Log.e("NWW_ANDROID", msg);
            throw new WWRuntimeException(msg, e);
        }
    }

    public synchronized Object setValue(String key, Object value)
    {
        if (key == null)
        {
            String msg = Messages.getMessage("nullValue.KeyIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }

        return this.avList(true).put(key, value);
    }

    public synchronized Collection<Object> getValues()
    {
        return this.hasAvList() ? this.avList.values() : this.createAvList().values();
    }

    public synchronized AVList setValues(AVList list)
    {
        if (list == null)
        {
            String msg = Messages.getMessage("nullValue.ListIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }

        Set<Map.Entry<String, Object>> entries = list.getEntries();
        for (Map.Entry<String, Object> entry : entries)
        {
            this.setValue(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public synchronized Set<Map.Entry<String, Object>> getEntries()
    {
        return this.hasAvList() ? this.avList.entrySet() : this.createAvList().entrySet();
    }

    public synchronized boolean hasKey(String key)
    {
        if (key == null)
        {
            String msg = Messages.getMessage("nullValue.KeyIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }

        return this.hasAvList() && this.avList.containsKey(key);
    }

    public synchronized Object removeKey(String key)
    {
        if (key == null)
        {
            String msg = Messages.getMessage("nullValue.KeyIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }

        return this.hasKey(key) ? this.avList.remove(key) : null;
    }

    public synchronized AVList copy()
    {
        AVListImpl clone = new AVListImpl();

        if (this.avList != null)
        {
            clone.createAvList();
            clone.avList.putAll(this.avList);
        }

        return clone;
    }

    public synchronized AVList clearList()
    {
        if (this.hasAvList())
            this.avList.clear();

        return this;
    }

    protected boolean hasAvList()
    {
        return this.avList != null;
    }

    protected Map<String, Object> createAvList()
    {
        if (!this.hasAvList())
        {
            // The map type used must accept null values. java.util.concurrent.ConcurrentHashMap does not.
            this.avList = new HashMap<String, Object>();
        }

        return this.avList;
    }

    protected Map<String, Object> avList(boolean createIfNone)
    {
        if (createIfNone && !this.hasAvList())
            this.createAvList();

        return this.avList;
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        if (propertyName == null)
        {
            String msg = Messages.getMessage("nullValue.PropertyNameIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }

        if (listener == null)
        {
            String msg = Messages.getMessage("nullValue.ListenerIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }

        this.getChangeSupport().addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        if (propertyName == null)
        {
            String msg = Messages.getMessage("nullValue.PropertyNameIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }
        if (listener == null)
        {
            String msg = Messages.getMessage("nullValue.ListenerIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }

        this.getChangeSupport().removePropertyChangeListener(propertyName, listener);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener)
    {
        if (listener == null)
        {
            String msg = Messages.getMessage("nullValue.ListenerIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }

        this.getChangeSupport().addPropertyChangeListener(listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener)
    {
        if (listener == null)
        {
            String msg = Messages.getMessage("nullValue.ListenerIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }

        this.getChangeSupport().removePropertyChangeListener(listener);
    }

    public synchronized void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        if (propertyName == null)
        {
            String msg = Messages.getMessage("nullValue.PropertyNameIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }

        this.getChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
    }

    public synchronized void firePropertyChange(PropertyChangeEvent event)
    {
        if (event == null)
        {
            String msg = Messages.getMessage("nullValue.EventIsNull");
            Log.e("NWW_ANDROID", msg);
            throw new IllegalArgumentException(msg);
        }

        this.getChangeSupport().firePropertyChange(event);
    }

    protected synchronized PropertyChangeSupport getChangeSupport()
    {
        Object pcs = this.getValue(PROPERTY_CHANGE_SUPPORT);
        if (pcs == null || !(pcs instanceof PropertyChangeSupport))
        {
            pcs = new PropertyChangeSupport(this);
            this.setValue(PROPERTY_CHANGE_SUPPORT, pcs);
        }

        return (PropertyChangeSupport) pcs;
    }

    ///////////////////////////////////////
    // Static AVList utilities.
    ///////////////////////////////////////

    public static String getStringValue(AVList avList, String key, String defaultValue)
    {
        String v = getStringValue(avList, key);
        return v != null ? v : defaultValue;
    }

    public static String getStringValue(AVList avList, String key)
    {
        try
        {
            return avList.getStringValue(key);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static Integer getIntegerValue(AVList avList, String key)
    {
        Object o = avList.getValue(key);
        if (o == null)
            return null;

        if (o instanceof Integer)
            return (Integer) o;

        if (!(o instanceof String))
            return null;

        try
        {
            return Integer.parseInt((String) o);
        }
        catch (NumberFormatException e)
        {
            Log.e("NWW_ANDROID", Messages.getMessage("generic.ConversionError", o));
            return null;
        }
    }

    public static Integer getIntegerValue(AVList avList, String key, Integer defaultValue)
    {
        Integer v = getIntegerValue(avList, key);
        return v != null ? v : defaultValue;
    }

    public static Long getLongValue(AVList avList, String key, Long defaultValue)
    {
        Long v = getLongValue(avList, key);
        return v != null ? v : defaultValue;
    }

    public static Long getLongValue(AVList avList, String key)
    {
        Object o = avList.getValue(key);
        if (o == null)
            return null;

        if (o instanceof Long)
            return (Long) o;

        String v = getStringValue(avList, key);
        if (v == null)
            return null;

        try
        {
            return Long.parseLong(v);
        }
        catch (NumberFormatException e)
        {
            Log.e("NWW_ANDROID", "Configuration.ConversionError" + v, e);
            return null;
        }
    }

    public static Double getDoubleValue(AVList avList, String key)
    {
        Object o = avList.getValue(key);
        if (o == null)
            return null;

        if (o instanceof Double)
            return (Double) o;

        String v = getStringValue(avList, key);
        if (v == null)
            return null;

        try
        {
            return Double.parseDouble(v);
        }
        catch (NumberFormatException e)
        {
            Log.e("NWW_ANDROID", "Configuration.ConversionError" + v, e);
            return null;
        }
    }
}
