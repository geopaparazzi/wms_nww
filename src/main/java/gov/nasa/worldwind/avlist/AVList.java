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

import java.beans.*;
import java.util.*;

/**
 * An interface for managing an attribute-value pair collection.
 *
 * @author dcollins
 * @version $Id: AVList.java 733 2012-09-02 17:15:09Z dcollins $
 */
public interface AVList
{
    /**
     * Returns the value for a specified key.
     *
     * @param key the attribute name. May not be <code>null</code>.
     *
     * @return the attribute value if one exists in the collection, otherwise <code>null</code>.
     *
     * @throws NullPointerException if <code>key</code> is <code>null</code>.
     */
    Object getValue(String key);

    /**
     * Returns the value for a specified key. The value must be a {@link String}.
     *
     * @param key the attribute name. May not be <code>null</code>.
     *
     * @return the attribute value if one exists in the collection, otherwise <code>null</code>.
     *
     * @throws NullPointerException if <code>key</code> is <code>null</code>.
     * @throws gov.nasa.worldwind.exception.WWRuntimeException
     *                              if the value in the collection is not a <code>String</code> type.
     */
    String getStringValue(String key);

    /**
     * Adds a key/value pair to the list. Replaces an existing key/value pair if the list already contains the key.
     *
     * @param key   the attribute name. May not be <code>null</code>.
     * @param value the attribute value. May be <code>null</code>, in which case any existing value for the key is
     *              removed from the collection.
     *
     * @return previous value associated with specified key, or null  if there was no mapping for key. A null return can
     *         also indicate that the map previously associated null  with the specified key, if the implementation
     *         supports null values.
     *
     * @throws NullPointerException if <code>key</code> is <code>null</code>.
     */
    Object setValue(String key, Object value);

    Collection<Object> getValues();

    /**
     * Adds the contents of another attribute-value list to the list. Replaces an existing key/value pair if the list
     * already contains the key.
     *
     * @param avList the list to copy. May not be <code>null</code>.
     *
     * @return <code>this</code>, a self reference.
     *
     * @throws NullPointerException if <code>avList</code> is <code>null</code>.
     */
    AVList setValues(AVList avList);

    Set<Map.Entry<String, Object>> getEntries();

    /**
     * Indicates whether a key is in the collection.
     *
     * @param key the attribute name. May not be <code>null</code>.
     *
     * @return <code>true</code> if the key exists in the collection, otherwise <code>false</code>.
     *
     * @throws NullPointerException if <code>key</code> is <code>null</code>.
     */
    boolean hasKey(String key);

    /**
     * Removes a specified key from the collection if the key exists, otherwise returns without affecting the
     * collection.
     *
     * @param key the attribute name. May not be <code>null</code>.
     *
     * @return previous value associated with specified key, or null  if there was no mapping for key.
     *
     * @throws NullPointerException if <code>key</code> is <code>null</code>.
     */
    Object removeKey(String key);

    /**
     * Returns a shallow copy of this <code>AVList</code> instance: the keys and values themselves are not cloned.
     *
     * @return a shallow copy of this <code>AVList</code>.
     */
    AVList copy();

    AVList clearList();

    /**
     * Adds a property change listener for the specified key.
     *
     * @param propertyName the key to associate the listener with.
     * @param listener     the listener to associate with the key.
     *
     * @throws IllegalArgumentException if either <code>propertyName</code> or <code>listener</code> is null
     * @see PropertyChangeSupport
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Removes a property change listener associated with the specified key.
     *
     * @param propertyName the key associated with the change listener.
     * @param listener     the listener to remove.
     *
     * @throws IllegalArgumentException if either <code>propertyName</code> or <code>listener</code> is null
     * @see PropertyChangeSupport
     */
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Adds the specified all-property property change listener that will be called for all list changes.
     *
     * @param listener the listener to call.
     *
     * @throws IllegalArgumentException if <code>listener</code> is null
     * @see PropertyChangeSupport
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes the specified all-property property change listener.
     *
     * @param listener the listener to remove.
     *
     * @throws IllegalArgumentException if <code>listener</code> is null
     * @see PropertyChangeSupport
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Calls all property change listeners associated with the specified key. No listeners are called if
     * <code>odValue</code> and <code>newValue</code> are equal and non-null.
     *
     * @param propertyName the key
     * @param oldValue     the value associated with the key before the even causing the firing.
     * @param newValue     the new value associated with the key.
     *
     * @throws IllegalArgumentException if <code>propertyName</code> is null
     * @see PropertyChangeSupport
     */
    void firePropertyChange(String propertyName, Object oldValue, Object newValue);

    /**
     * Calls all registered property change listeners with the specified property change event.
     *
     * @param event the property change event.
     *
     * @throws IllegalArgumentException if <code>propertyChangeEvent</code> is null
     * @see PropertyChangeSupport
     */
    void firePropertyChange(PropertyChangeEvent event);
}

