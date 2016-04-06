package gov.nasa.worldwind.util;

import android.util.Log;

import java.text.MessageFormat;
import java.util.*;

/**
 * Messages
 *
 */
public class Messages {
    protected static final String MESSAGE_BUNDLE_NAME = Messages.class.getPackage().getName() + ".MessageStrings";
    public static final String LOGGING = "LOGGING";

    // Singleton, prevent public instantiation.
    protected Messages() {
    }

    /**
     * Retrieves a message from the World Wind message resource bundle.
     *
     * @param property the property identifying which message to retrieve.
     * @return The requested message.
     */
    public static String getMessage(String property) {
        try {
            return (String) ResourceBundle.getBundle(MESSAGE_BUNDLE_NAME, Locale.getDefault()).getObject(property);
        } catch (Exception e) {
            String msg = "Exception looking up message from bundle " + MESSAGE_BUNDLE_NAME;
            Log.e(LOGGING, msg, e);
            return msg;
        }
    }

    /**
     * Retrieves a message from the World Wind message resource bundle formatted with specified arguments. The arguments
     * are inserted into the message via {@link MessageFormat}.
     *
     * @param property the property identifying which message to retrieve.
     * @param args     the arguments referenced by the format string identified <code>property</code>.
     * @return The requested string formatted with the arguments.
     * @see MessageFormat
     */
    public static String getMessage(String property, Object... args) {
        String msg;

        try {
            msg = (String) ResourceBundle.getBundle(MESSAGE_BUNDLE_NAME, Locale.getDefault()).getObject(property);
        } catch (Exception e) {
            msg = "Exception looking up message from bundle " + MESSAGE_BUNDLE_NAME;
            Log.e(LOGGING, msg, e);
            return msg;
        }

        try {
            // TODO: This is no longer working with more than one arg in the message string, e.g., {1}
            return args == null ? msg : MessageFormat.format(msg, args);
        } catch (IllegalArgumentException e) {
            msg = "Message arguments do not match format string: " + property;
            Log.e(LOGGING, msg, e);
            return msg;
        }
    }
}
