package com.payroll;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Init database
        DatabaseManager.initializeDatabase();

        // Launch UI on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Use system look and feel as base
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}

            PayrollUI ui = new PayrollUI();
            ui.setVisible(true);

            // Graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(DatabaseManager::closeConnection));
        });
    }
}
