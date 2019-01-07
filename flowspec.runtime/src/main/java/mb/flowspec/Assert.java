package mb.flowspec;

import org.junit.ComparisonFailure;

/**
 * Functionality copied from {@link org.junit.Assert} and added {@link Assert#assertInstanceOf(Object, Class)}
 * Copied so we don't have a dependency on junit in normal code, which would look strange.
 * Copied from junit 4.12, which is distributed under the Eclipse Public License v1.0. 
 * All methods except {@link Assert#assertInstanceOf(Object, Class)} are copyright of the JUnit team.
 */
public class Assert {
    /**
     * Asserts that two objects are equal. If they are not, an {@link AssertionError} is thrown with the given message.
     * If <code>expected</code> and <code>actual</code> are <code>null</code>, they are considered equal.
     *
     * @param message
     *            the identifying message for the {@link AssertionError} (<code>null</code> okay)
     * @param expected
     *            expected value
     * @param actual
     *            actual value
     */
    static public void assertEquals(String message, Object expected, Object actual) {
        if(equalsRegardingNull(expected, actual)) {
            return;
        } else if(expected instanceof String && actual instanceof String) {
            String cleanMessage = message == null ? "" : message;
            throw new ComparisonFailure(cleanMessage, (String) expected, (String) actual);
        } else {
            failNotEquals(message, expected, actual);
        }
    }

    private static boolean equalsRegardingNull(Object expected, Object actual) {
        if(expected == null) {
            return actual == null;
        }

        return isEquals(expected, actual);
    }

    private static boolean isEquals(Object expected, Object actual) {
        return expected.equals(actual);
    }

    /**
     * Asserts that two objects are equal. If they are not, an
     * {@link AssertionError} without a message is thrown. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param expected expected value
     * @param actual the value to check against <code>expected</code>
     */
    static public void assertEquals(Object expected, Object actual) {
        assertEquals(null, expected, actual);
    }

    static private void failNotEquals(String message, Object expected, Object actual) {
        fail(format(message, expected, actual));
    }

    static String format(String message, Object expected, Object actual) {
        String formatted = "";
        if(message != null && !message.equals("")) {
            formatted = message + " ";
        }
        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        if(expectedString.equals(actualString)) {
            return formatted + "expected: " + formatClassAndValue(expected, expectedString) + " but was: "
                + formatClassAndValue(actual, actualString);
        } else {
            return formatted + "expected:<" + expectedString + "> but was:<" + actualString + ">";
        }
    }

    private static String formatClassAndValue(Object value, String valueString) {
        String className = value == null ? "null" : value.getClass().getName();
        return className + "<" + valueString + ">";
    }

    /**
     * Asserts that two longs are equal. If they are not, an
     * {@link AssertionError} is thrown.
     *
     * @param expected expected long value.
     * @param actual actual long value
     */
    static public void assertEquals(long expected, long actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two longs are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     * okay)
     * @param expected long expected value.
     * @param actual long actual value
     */
    static public void assertEquals(String message, long expected, long actual) {
        if (expected != actual) {
            failNotEquals(message, Long.valueOf(expected), Long.valueOf(actual));
        }
    }

    /**
     * Fails a test with the given message.
     *
     * @param message
     *            the identifying message for the {@link AssertionError} (<code>null</code> okay)
     * @see AssertionError
     */
    static public void fail(String message) {
        if(message == null) {
            throw new AssertionError();
        }
        throw new AssertionError(message);
    }

    /**
     * Asserts that two longs are equal. If they are not, an {@link AssertionError} is thrown.
     *
     * @param expected
     *            expected long value.
     * @param actual
     *            actual long value
     */
    static public <T> void assertInstanceOf(T object, Class<? extends T> clazz) {
        if(clazz.isInstance(object)) {
            return;
        }
        fail(object.getClass().getName() + " is not instance of " + clazz.getName());
    }
}
