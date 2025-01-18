package dev.keith.fileDataBase;

import dev.keith.event.IEvent;
import dev.keith.event.Parameters;

/**
 * A very simple IEvent inheritor.
 */
public class Event implements IEvent<Event.Type> {
    private final Type type;
    private final String message;
    private final Parameters parameters;

    /**
     * The default constructor
     * @param s message
     * @param type type
     * @param parameters parameters
     */
    public Event(String s, Type type, Parameters parameters) {
        this.message = s;
        this.type = type;
        this.parameters = parameters;
    }

    /**
     * get the message
     * @return message
     */
    @Override
    public String message() {
        return message;
    }

    /**
     * get the type of the event
     * @return type
     */
    @Override
    public Type type() {
        return type;
    }

    /**
     * get the parameters of the event
     * @return parameters
     */
    @Override
    public Parameters parameters() {
        return parameters;
    }

    /**
     * The default type that this event used.
     */
    public enum Type implements IEvent.Type {
        /**
         * Read
         */
        READ("read"),
        /**
         * Write
         */
        WRITE("write"),
        /**
         * Clear
         */
        CLEAR("clear"),
        /**
         * Delete
         */
        DELETE("delete")
        ;
        private final String message;

        Type(String message) {
            this.message = message;
        }

        /**
         * Get the message
         * @return message
         */
        @Override
        public String message() {
            return message;
        }
    }
}
