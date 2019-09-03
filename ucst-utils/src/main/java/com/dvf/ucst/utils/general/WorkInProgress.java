package com.dvf.ucst.utils.general;

/**
 * Ain't nobody here but us docs.
 *
 * Implementations of this interface should have the following properties:
 * - are located in the same package as whoever will call the setters (assemble the WIP)
 * - all fields are non-final and private
 * - all fields have public getters, collections must be wrapped as unmodifiable
 * - getters should throw a [IncompleteWipException] if a property is known to be incomplete
 * - all fields have package-private setters that return the instance the setter was called off of
 * - unless necessary, should not provide or implement any constructors
 */
public interface WorkInProgress {

    /**
     * Consumers of [WorkInProgress] instances should throw this if they receive one
     * that is missing information that they require.
     */
    class IncompleteWipException extends Exception {
        public IncompleteWipException(final String message) {
            super(message);
        }

        /**
         * @param wip the [WorkInProgress] considered to be missing the property
         *     bound to a declared field of its own by the name [fieldName].
         * @param fieldName The name of the declared field of [wip] representing
         *     a property that is currently considered to be missing. Must be the
         *     exact String used for the declared field.
         * @return an [IncompleteWipException] describing this incident.
         */
        public static IncompleteWipException missingProperty(final WorkInProgress wip, final String fieldName) {
            try {
                return new IncompleteWipException(String.format("The [%s]"
                        + " \"%s\" is currently missing its \"%s\" property",
                        WorkInProgress.class,
                        wip,
                        wip.getClass().getDeclaredField(fieldName).getName() // absolutely unnecessary.
                ));
            } catch (final NoSuchFieldException e) {
                throw new RuntimeException("A String literal for a field name in WIP getter is wrong", e);
            }
        }
    }

}
