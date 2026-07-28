package com.fraud.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Transaction {
    private int id;
    private String senderName;
    private String receiverName;
    private double amount;
    private Date transactionDate;
    private String status; // "Safe" or "Fraud"
    private Timestamp createdAt;

    public Transaction() {}

    public Transaction(int id, String senderName, String receiverName, double amount, Date transactionDate, String status, Timestamp createdAt) {
        this.id = id;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Transaction(String senderName, String receiverName, double amount, Date transactionDate, String status) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"senderName\":\"%s\",\"receiverName\":\"%s\",\"amount\":%.2f,\"transactionDate\":\"%s\",\"status\":\"%s\"}",
            id,
            escapeJson(senderName),
            escapeJson(receiverName),
            amount,
            transactionDate != null ? transactionDate.toString() : "",
            escapeJson(status)
        );
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", senderName='" + senderName + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                ", status='" + status + '\'' +
                '}';
    }
}
