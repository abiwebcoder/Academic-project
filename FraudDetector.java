package com.fraud.util;

public class FraudDetector {
    public static final double FRAUD_THRESHOLD = 50000.00;

    /**
     * Determines whether a transaction is Fraud or Safe based on the transaction amount.
     * 
     * Rule:
     * - If amount > 50000 -> "Fraud"
     * - Otherwise -> "Safe"
     *
     * @param amount The transaction amount
     * @return "Fraud" or "Safe"
     */
    public static String evaluateTransaction(double amount) {
        if (amount > FRAUD_THRESHOLD) {
            return "Fraud";
        } else {
            return "Safe";
        }
    }
}
