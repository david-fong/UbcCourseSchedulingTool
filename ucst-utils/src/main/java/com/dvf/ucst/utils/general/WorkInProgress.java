package com.dvf.ucst.utils.general;

/**
 * There is nothing here but docs.
 *
 * Implementations of this interface should have the following properties:
 * - are located in the same package as whoever will call the setters (assemble the WIP)
 * - all fields are non-final and private
 * - all fields have public getters
 * - all fields have package-private setters that return the instance the setter was called off of
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
