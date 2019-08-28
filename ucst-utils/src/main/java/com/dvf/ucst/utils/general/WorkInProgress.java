package com.dvf.ucst.utils.general;

/**
 * There is nothing here but docs. That's the best kind of interface. (I'm joking...
 * although it could certainly be said to be the purest kind).
 *
 * Implementations of this interface should have the following properties:
 * - are located in the same package as the immutable class they represent a draft of
 * - all fields are non-final and private
 * - provide package-private getters for all fields
 * - provide public setters that return the instance the getter was called off of
 * - unless necessary, should not provide or implement any constructors
 */
public interface WorkInProgress {

    /**
     * Consumers of [WorkInProgress] instances should throw this if they receive one
     * that is missing information that they require.
     */
    class ReceivedIncompleteWipException extends Exception {
        public ReceivedIncompleteWipException(final String message) {
            super(message);
        }
    }

}
