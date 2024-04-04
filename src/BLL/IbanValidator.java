package BLL;

import Enums.ECountryCode;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class IbanValidator {

    private static final Map<Character, Integer> letterToDigitMap = createLetterToDigitMap();

    public static boolean validateIBAN(String iban) {

        iban = iban.replaceAll("\\s+", "").toUpperCase();
        if (iban.length() < 2 || iban.length() > 34) {
            return false;
        }
        String countryCode = iban.substring(0, 2);
        if (!validateCountryCode(countryCode)) {
            return false;
        }

        String ibanBody = iban.substring(4) + iban.substring(0,4);
        return isValidCheckDigits(ibanBody);
    }

    private static boolean isValidCheckDigits(String ibanBody) {
        StringBuilder result = new StringBuilder();
        for (Character c : ibanBody.toCharArray()) {
            if (Character.isLetter(c)) {
                HandleLetter(c, result);
                continue;
            }
            result.append(c);
        }

        BigInteger ibanNumber = new BigInteger(result.toString());
        BigInteger remainder = ibanNumber.mod(BigInteger.valueOf(97));
        int remainderInt = remainder.intValue();

        return remainderInt == 1;
    }

    private static void HandleLetter(Character c, StringBuilder result) {
        for (Character character : letterToDigitMap.keySet()) {
            if (c.equals(character)) {
                String letterToDigit = letterToDigitMap.get(c).toString();
                result.append(letterToDigit);
            }
        }
    }

    private static Map<Character, Integer> createLetterToDigitMap() {
        Map<Character, Integer> map = new HashMap<>();
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            map.put(ch, ch - 'A' + 10);
        }
        return map;
    }


    public static boolean validateCountryCode(String countryCode) {
        for (ECountryCode i : ECountryCode.values() ) {
            if (countryCode.equals(i.toString())) {
                return true;
            }
        }
        return false;
    }
}
