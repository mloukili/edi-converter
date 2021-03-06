package com.beesphere.edi.reader.impls;

import java.util.List;
import java.util.ArrayList;

public class EDIUtils {

    /**
     * Splits a String by delimiter as long as delimiter does not follow an escape sequence.
     * The split method follows the same behavior as the method splitPreserveAllTokens(String, String)
     * in {@link org.apache.commons.lang.StringUtils}.
     *
     * @param value the string to split, may be null.
     * @param delimiter the delimiter sequence. A null delimiter splits on whitespace.
     * @param escape the escape sequence. A null escape is allowed,  and result will be consistent with the splitPreserveAllTokens method.   
     * @return an array of split edi-sequences, null if null string input.
     */
    public static String[] split(String value, String delimiter, String escape) {

        // A null input string returns null
        if (value == null) {
            return null;
        }

        // Empty input string returns empty array
        if (value.length() == 0) {
            return new String[0];
        }

        // Empty delimiter splits on whitespace.
        if (delimiter == null) {
            delimiter = " ";
        }

        List<String> tokens = new ArrayList<String>();

        int escapeIndex = 0;
        int delimiterIndex = 0;
        boolean foundEscape = false;
        StringBuilder escapeContent = new StringBuilder();
        StringBuilder delimiterContent = new StringBuilder();
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char tmp = value.charAt(i);

            // If character equals current escape character or start of a new escape sequence.
            if (escape != null && ( tmp == escape.charAt(0) || tmp == escape.charAt(escapeIndex) )) {

                // If starting from the beginning of a new escape sequence.
                if (tmp == escape.charAt(0)) {
                    token.append(escapeContent);
                    escapeIndex = 0;
                    escapeContent = new StringBuilder();
                }

                // If we haven't found the whole escape seguence.
                if ( escapeIndex < escape.length() -1 ) {
                    escapeIndex++;
                    if (foundEscape) {
                        token.append(escapeContent);
                        escapeContent = new StringBuilder();
                    }
                    foundEscape = false;

                    // If we have found the whole escape seguence.
                } else {
                    if (foundEscape) {
                        token.append(escapeContent);
                    }
                    foundEscape = true;
                    escapeIndex = 0;
                }

                escapeContent.append(tmp);

                // If character equals current delimiter or start of a new delimiter sequence.
            } else if (tmp == delimiter.charAt(delimiterIndex) || tmp == delimiter.charAt(0)) {

                // If starting from the beginning of a new delimiter sequence.
                if ( tmp == delimiter.charAt(0) ) {
                    token.append(delimiterContent);
                    delimiterIndex = 0;
                    delimiterContent = new StringBuilder();
                }

                delimiterContent.append(tmp);
                // If we haven't found the whole delimiter sequence.
                if ( delimiterIndex < delimiter.length() -1 ) {
                    delimiterIndex++;
                    // If we have found the whole delimiter sequence.
                } else {
                    if (foundEscape) {
                        token.append(delimiterContent);
                        escapeContent = new StringBuilder();
                    } else {
                        tokens.add(token.toString());
                        token = new StringBuilder();
                    }
                    delimiterIndex = 0;
                    delimiterContent = new StringBuilder();
                }
                // If Character doesn't match current delimiter or escape character.
            } else {
                // Append and reset escape sequence if it exists.
                token.append(escapeContent);
                foundEscape = false;
                escapeContent = new StringBuilder();

                // Append and reset delimiter sequence if it exists.
                token.append(delimiterContent);
                delimiterContent = new StringBuilder();

                // Append the current character.
                token.append(value.charAt(i));
            }
        }

        tokens.add(token.toString());

        return tokens.toArray(new String[tokens.size()]);
    }

    public static void main(String[] args) {
        String[] test = EDIUtils.split("ATS+hep:iee+hai??+kai=haikai+slut", "+", "?");
        String[] expected = new String[]{"ATS", "hep:iee", "hai?+kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel1");
        }

        test = EDIUtils.split("ATS+hep:iee+hai?#?#+kai=haikai+slut", "+", "?#");
        expected = new String[]{"ATS", "hep:iee", "hai?#+kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel2");
        }

        test = EDIUtils.split("ATS+#hep:iee+#hai?#?#+#kai=haikai+#slut", "+#", "?#");
        expected = new String[]{"ATS", "hep:iee", "hai?#+#kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel3");
        }

        test = EDIUtils.split("ATS+#hep:iee+#hai??+#kai=haikai+#slut", "+#", "?");
        expected = new String[]{"ATS", "hep:iee", "hai?+#kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel4");
        }

        test = EDIUtils.split("ATS+#hep:iee+#hai??+#kai=haikai+#slut", "+#", null);
        expected = new String[]{"ATS", "hep:iee", "hai??", "kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel5");
        }

        // Test restarting escape sequence within escape sequence.
        test = EDIUtils.split("ATS+hep:iee+hai??#+kai=haikai+slut", "+", "?#");
        expected = new String[]{"ATS", "hep:iee", "hai?+kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel6");
        }

        // Test restarting delimiter sequence within delimiter sequence.
        test = EDIUtils.split("ATS++#hep:iee+#hai?+#kai=haikai+#slut", "+#", "?");
        expected = new String[]{"ATS+", "hep:iee", "hai+#kai=haikai", "slut"};
        if (!equal(test,expected)) {
            System.out.println("Fel7");
        }


        /**************************************************************************
         * Testcases defined by split
         * ************************************************************************/
        if (EDIUtils.split(null, "*", null) != null) {
            System.out.println("Fel8");
        }

        if (!equal(EDIUtils.split("", null, null), new String[0])) {
            System.out.println("Fel8");
        }

        if (!equal(EDIUtils.split("abc def", null, null), new String[]{"abc", "def"})) {
            System.out.println("Fel8");
        }

        if (!equal(EDIUtils.split("abc def", " ", null), new String[]{"abc", "def"})) {
            System.out.println("Fel8");
        }

        if (!equal(EDIUtils.split("abc  def", " ", null), new String[]{"abc", "", "def"})) {
            System.out.println("Fel8");
        }

        if (!equal(EDIUtils.split("ab:cd:ef", ":", null), new String[]{"ab", "cd", "ef"})) {
            System.out.println("Fel8");
        }

        if (!equal(EDIUtils.split("ab:cd:ef:", ":", null), new String[]{"ab", "cd", "ef", ""})) {
            System.out.println("Fel8");
        }

        if (!equal(EDIUtils.split("ab:cd:ef::", ":", null), new String[]{"ab", "cd", "ef", "", ""})) {
            System.out.println("Fel8");
        }

        if (!equal(EDIUtils.split(":cd:ef", ":", null), new String[]{"", "cd", "ef"})) {
            System.out.println("Fel8");
        }

        if (!equal(EDIUtils.split("::cd:ef", ":", null), new String[]{"", "", "cd", "ef"})) {
            System.out.println("Fel8");
        }

        if (!equal(EDIUtils.split(":cd:ef:", ":", null), new String[]{"", "cd", "ef", ""})) {
            System.out.println("Fel8");
        }

        System.out.println("");
    }

    private static boolean equal(String[] test, String[] expected) {
        if (test.length != expected.length) {
            return false;
        }

        for (int i = 0; i < test.length; i++) {
            if (!test[i].equals(expected[i])) {
                return false;
            }
        }
        return true;
    }
}
