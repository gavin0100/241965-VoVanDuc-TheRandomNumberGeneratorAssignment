package org.example;

import java.util.stream.IntStream;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        int baseNumber = generateBaseNumber();
        logger.log(Level.INFO, "Base number: {0}", String.valueOf(baseNumber));

        logger.log(Level.INFO, "Luhn algorithm");
        long randomNumberByLuhnAlgorithm = randomNumberGenerator(baseNumber, calculateCheckDigitByLuhnAlgorithm(baseNumber));
        logger.log(Level.INFO, "Random number: {0}", String.valueOf(randomNumberByLuhnAlgorithm));
        logger.log(Level.INFO, "Verification result: {0}", isValidLuhnNumber(randomNumberByLuhnAlgorithm));

        logger.log(Level.INFO, "Verhoeff algorithm");
        long randomNumberByVerhoeffAlgorithm = randomNumberGenerator(baseNumber, calculateCheckDigitByVerhoeffAlgorithm(baseNumber));
        logger.log(Level.INFO, "Random number: {0}", String.valueOf(randomNumberByVerhoeffAlgorithm));
        logger.log(Level.INFO, "Verification result: {0}", isValidVerhoeffNumber(randomNumberByVerhoeffAlgorithm));
    }

    public static long randomNumberGenerator(int baseNumber, int checkDigit){
        return Long.parseLong(baseNumber + String.format("%02d", checkDigit));
    }

    private static int generateBaseNumber() {
        return 10000000 + (int)(Math.random() * 90000000);
    }

    private static int calculateCheckDigitByLuhnAlgorithm(int baseNumber) {
        String numberStr = new StringBuilder(Integer.toString(baseNumber)).reverse().toString();
        int sum = IntStream.range(0, numberStr.length())
                .map(index -> {
                    int digit = Character.getNumericValue(numberStr.charAt(index));
                    if (index % 2 != 1){
                        digit *= 2;
                        if (digit > 9) digit -= 9;
                    }
                    return digit;
                })
                .sum();
        return (100 - (sum % 100)) % 100;
    }

    private static boolean isValidLuhnNumber(long uniqueNumber) {
        String uniqueNumberToString = String.valueOf(uniqueNumber);
        int baseNumber = Integer.parseInt(uniqueNumberToString.substring(0, 8));
        int originalCheckDigit = Integer.parseInt(uniqueNumberToString.substring(8, 10));
        int calculatedCheckDigit = calculateCheckDigitByLuhnAlgorithm(baseNumber);
        return originalCheckDigit == calculatedCheckDigit;
    }

    //============================= Verhoeff ==================================================

    // The multiplication table D
    private static final int[][] D = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
            {2, 3, 4, 0, 1, 7, 8, 9, 5, 6},
            {3, 4, 0, 1, 2, 8, 9, 5, 6, 7},
            {4, 0, 1, 2, 3, 9, 5, 6, 7, 8},
            {5, 9, 8, 7, 6, 0, 4, 3, 2, 1},
            {6, 5, 9, 8, 7, 1, 0, 4, 3, 2},
            {7, 6, 5, 9, 8, 2, 1, 0, 4, 3},
            {8, 7, 6, 5, 9, 3, 2, 1, 0, 4},
            {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}
    };

    // The permutation table P
    private static final int[][] P = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 5, 7, 6, 2, 8, 3, 0, 9, 4},
            {5, 8, 0, 3, 7, 9, 6, 1, 4, 2},
            {8, 9, 1, 6, 0, 4, 3, 5, 2, 7},
            {9, 4, 5, 3, 1, 2, 6, 8, 7, 0},
            {4, 2, 8, 6, 5, 7, 3, 9, 0, 1},
            {2, 7, 9, 3, 8, 0, 6, 4, 1, 5},
            {7, 0, 4, 6, 9, 1, 3, 2, 5, 8}
    };

    // The inverse table Inv
    private static final int[] Inv = {0, 4, 3, 2, 1, 5, 6, 7, 8, 9};

    public static int calculateCheckDigitByVerhoeffAlgorithm(int baseNumber) {

        String number = String.valueOf(baseNumber);
        if (!number.matches("\\d+")) {
            throw new IllegalArgumentException("Input must contain only digits");
        }

        // preprocessing data
        StringBuilder mappedNumber = new StringBuilder();
        for (int i = 0; i < number.length(); i++) {
            int digit = Character.getNumericValue(number.charAt(number.length() - 1 - i));
            int mappedDigit = P[((i + 1) % 8)][digit];
            mappedNumber.append(mappedDigit);
        }

        // Calculate first check digit
        int c = 0;
        for (int i = 0; i < mappedNumber.length(); i++) {
            int mappedDigit = Character.getNumericValue(mappedNumber.charAt(i));
            c = D[c][mappedDigit];
        }
        int firstDigit = Inv[c];

        // Add first check digit and calculate second check digit
        String numberWithFirst = number + firstDigit;

        // Combined preprocessing data and find check digit
        c = 0;
        for (int i = 0; i < numberWithFirst.length(); i++) {
            int digit = Character.getNumericValue(numberWithFirst.charAt(numberWithFirst.length() - 1 - i));
            c = D[c][P[((i + 1) % 8)][digit]];
        }
        int secondDigit = Inv[c];

        // Return both check digits as a string
        return Integer.parseInt(String.format("%d%d", firstDigit, secondDigit));
    }

    public static boolean isValidVerhoeffNumber(long number){
        String numberStr = String.valueOf(number);

        String baseNumberStr = numberStr.substring(0, numberStr.length() - 2);
        String checkDigitsStr = numberStr.substring(numberStr.length() - 2);
        int checkDigits = Integer.parseInt(checkDigitsStr);

        int calculatedCheckDigits = calculateCheckDigitByVerhoeffAlgorithm(Integer.parseInt(baseNumberStr));

        return checkDigits == calculatedCheckDigits;
    }
}