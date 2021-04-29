package org.hinton_lang.Helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    /**
     * Checks whether a character matches a regular expression sequence.
     * 
     * @param c       The character to be tested.
     * @param pattern The regular expression.
     * @return True if the string matches the regular expression, false otherwise.
     */
    public static boolean Match(char c, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(Character.toString(c));
        return m.matches();
    }

    /**
     * Checks whether a string matches a regular expression sequence.
     * 
     * @param s       The string to be tested.
     * @param pattern The regular expression.
     * @return True if the string matches the regular expression, false otherwise.
     */
    public static boolean Match(String s, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        return m.matches();
    }
}
