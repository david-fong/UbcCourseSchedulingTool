package com.dvf.ucst.utils.general;

/**
 * Ain't nobody here but us docs.
 *
 *
 * Implementations of this interface should have the following properties:
 *
 * - are located in the same package as whoever will call the setters (assemble the WIP)
 * - unless necessary, should not provide or implement any constructors
 *
 * - all fields are non-final and private
 * - no primitive fields - box them with their respective objects
 * - fields may have default values as long is it is clearly documented in the class doc
 *
 * - all fields have public getters, collections must be wrapped as unmodifiable (even if redundant)
 * - getters should throw a [IncompleteWipException] if a property is known to be incomplete
 * - if a getter's field has a default value, it may choose to force the setter to use the
 *       default value if the input would qualify as invalid (Ex. null), and forgo the exception
 *       in the getter.
 *
 * - all fields have package-private setters that return the instance the setter was called off of
 * - unless absolutely necessary, setters must wrap input collections as unmodifiable before assigning to fields
 * - setters may perform validation and throw exceptions as long as the validation does not
 *       depend on other fields with setters (to prevent user of setters from having to write
 *       ugly code that performs the same validation externally).
 *
 * @param <I> The implementation's own type.
 */
public interface WorkInProgress<I extends WorkInProgress<I>> {

    /**
     * @return a deep copy of the calling instance.
     */
    I copy();

    /**
     * Consumers of [WorkInProgress] instances should throw this if they receive one
     * that is missing information that they require.
     */
    class IncompleteWipException extends Exception {
        public IncompleteWipException(final String message) {
            super(message);
        }

        public IncompleteWipException(final String message, Exception e) {
            super(message, e);
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
