package io.github.evalexp.util;

/**
 * StringUtil for String operation
 */
public class StringUtil {
    /**
     * judge the string has length
     * @param str string
     * @return has length?
     */
    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    /**
     * judge the string has length
     * @param str string
     * @return has length?
     */
    public static boolean hasLength(String str) {
        return (str != null && !str.isEmpty());
    }

    /**
     * make first letter to upper
     * @param str string
     * @return capitalized string
     */
    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    /**
     * make first letter to lower
     * @param str string
     * @return uncapitalize string
     */
    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    /**
     * change first letter case
     * @param str string
     * @param capitalize true to capitalize
     * @return converted string
     */
    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (!hasLength(str)) {
            return str;
        }

        char baseChar = str.charAt(0);
        char updatedChar;
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        }
        else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return str;
        }

        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars, 0, chars.length);
    }
}
