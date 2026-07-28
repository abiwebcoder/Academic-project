package com.fraud;

import com.fraud.dao.DBConnection;
import com.fraud.dao.TransactionDAO;
import com.fraud.model.Transaction;
import com.fraud.server.SimpleHttpServer;
import com.fraud.util.FraudDetector;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        printBanner();

        // 1. Check Database Connectivity
        System.out.println("🔍 Testing MySQL Database Connection...");
        boolean isConnected = DBConnection.testConnection();
        if (isConnected) {
            System.out.println("✅ Database connection established successfully! Connected to 'frauddb'.");
        } else {
            System.err.println("⚠️ Warning: Could not connect to MySQL 'frauddb'.");
            System.err.println("👉 Please ensure MySQL service is running on localhost:3306 and 'frauddb.sql' has been executed.");
            System.err.println("👉 Default Credentials - User: root, Password: password (Configure in DBConnection.java if needed).");
        }

        // 2. Start HTTP Server for Web Interface
        SimpleHttpServer server = new SimpleHttpServer(PORT);
        try {
            server.start();
            System.out.println("\n🌐 Web Dashboard URL: http://localhost:" + PORT + "/login.html");
            System.out.println("🔑 Default Login Credentials -> Username: admin | Password: password123");
        } catch (Exception e) {
            System.err.println("❌ Failed to start HTTP Server on port " + PORT + ": " + e.getMessage());
        }

        // 3. Command Line Interface (CLI) Options
        System.out.println("\n==================================================");
        System.out.println("  CLI MODE ACTIVE - Type numbers below or interact via Browser");
        System.out.println("==================================================");
        
        Scanner scanner = new Scanner(System.in);
        TransactionDAO dao = new TransactionDAO();

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. View All Transactions");
            System.out.println("2. Add New Transaction");
            System.out.println("3. Search Transactions by Sender");
            System.out.println("4. Filter Transactions by Status (Fraud/Safe)");
            System.out.println("5. Delete Transaction");
            System.out.println("6. View Dashboard Statistics");
            System.out.println("7. Exit Server & CLI");
            System.out.print("Select an option (1-7): ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    displayTransactions(dao.getAllTransactions());
                    break;
                case "2":
                    addNewTransactionCLI(scanner, dao);
                    break;
                case "3":
                    System.out.print("Enter Sender Name to search: ");
                    String sender = scanner.nextLine().trim();
                    displayTransactions(dao.searchBySender(sender));
                    break;
                case "4":
                    System.out.print("Enter status to filter (Fraud/Safe): ");
                    String status = scanner.nextLine().trim();
                    displayTransactions(dao.filterByStatus(status));
                    break;
                case "5":
                    System.out.print("Enter Transaction ID to delete: ");
                    try {
                        int deleteId = Integer.parseInt(scanner.nextLine().trim());
                        if (dao.deleteTransaction(deleteId)) {
                            System.out.println("✅ Transaction #" + deleteId + " deleted successfully!");
                        } else {
                            System.out.println("❌ Transaction ID not found or deletion failed.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid ID format.");
                    }
                    break;
                case "6":
                    Map<String, Integer> stats = dao.getStats();
                    System.out.println("\n📊 --- DASHBOARD SUMMARY STATISTICS ---");
                    System.out.println("Total Transactions : " + stats.get("total"));
                    System.out.println("Fraud Transactions : " + stats.get("fraud"));
                    System.out.println("Safe Transactions  : " + stats.get("safe"));
                    break;
                case "7":
                    System.out.println("Shutting down Digital Payment Server... Goodbye!");
                    server.stop();
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("❌ Invalid selection. Please enter a number between 1 and 7.");
                    break;
            }
        }
    }

    private static void addNewTransactionCLI(Scanner scanner, TransactionDAO dao) {
        try {
            System.out.print("Enter Sender Name: ");
            String sender = scanner.nextLine().trim();

            System.out.print("Enter Receiver Name: ");
            String receiver = scanner.nextLine().trim();

            System.out.print("Enter Amount (e.g. 55000.00): ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter Transaction Date (YYYY-MM-DD) [Leave blank for Today]: ");
            String dateInput = scanner.nextLine().trim();
            Date date;
            if (dateInput.isEmpty()) {
                date = new Date(System.currentTimeMillis());
            } else {
                date = Date.valueOf(dateInput);
            }

            String status = FraudDetector.evaluateTransaction(amount);
            Transaction t = new Transaction(sender, receiver, amount, date, status);

            boolean success = dao.addTransaction(t);
            if (success) {
                System.out.println("✅ Transaction created successfully! ID: #" + t.getId() + " | Status: " + t.getStatus());
                if ("Fraud".equals(t.getStatus())) {
                    System.out.println("🚨 WARNING: Transaction flagged as FRAUD (Amount > 50,000)");
                }
            } else {
                System.out.println("❌ Failed to save transaction to database.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error processing input: " + e.getMessage());
        }
    }

    private static void displayTransactions(List<Transaction> list) {
        if (list.isEmpty()) {
            System.out.println("ℹ️ No transactions found.");
            return;
        }
        System.out.println("\n-----------------------------------------------------------------------------------------");
        System.out.printf("%-6s | %-18s | %-18s | %-12s | %-12s | %-8s\n", "ID", "Sender Name", "Receiver Name", "Amount ($)", "Date", "Status");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Transaction t : list) {
            System.out.printf("%-6d | %-18s | %-18s | %-12.2f | %-12s | %-8s\n",
                    t.getId(), t.getSenderName(), t.getReceiverName(), t.getAmount(), t.getTransactionDate(), t.getStatus());
        }
        System.out.println("-----------------------------------------------------------------------------------------");
    }

    private static void printBanner() {
        System.out.println("===============================================================");
        System.out.println("   💳 DIGITAL PAYMENT FRAUD DETECTION SYSTEM (PURE JAVA + JDBC)");
        System.out.println("===============================================================");
    }
}
