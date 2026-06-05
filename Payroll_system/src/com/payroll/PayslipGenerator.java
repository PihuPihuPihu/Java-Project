package com.payroll;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PayslipGenerator {

    // Tax slabs (simplified Indian income tax - monthly)
    public static double calculateMonthlyTax(double annualSalary) {
        double tax = 0;
        if (annualSalary <= 250000) {
            tax = 0;
        } else if (annualSalary <= 500000) {
            tax = (annualSalary - 250000) * 0.05;
        } else if (annualSalary <= 1000000) {
            tax = 12500 + (annualSalary - 500000) * 0.20;
        } else {
            tax = 112500 + (annualSalary - 1000000) * 0.30;
        }
        return tax / 12; // monthly tax
    }

    public static double calculatePF(double monthlySalary) {
        // 12% of basic (simplified: 12% of salary)
        return monthlySalary * 0.12;
    }

    public static double calculateGratuity(double monthlySalary) {
        // Simplified: 4.81% of basic
        return monthlySalary * 0.0481;
    }

    public static String generatePayslip(Employee emp) {
        double monthlySalary = emp.getSalary() / 12.0;
        double monthlyBonus  = emp.getBonus() / 12.0;
        double grossMonthly  = monthlySalary + monthlyBonus;

        double pf           = calculatePF(monthlySalary);
        double tax          = calculateMonthlyTax(emp.getSalary());
        double gratuity     = calculateGratuity(monthlySalary);
        double totalDeduct  = pf + tax + gratuity;
        double netPay       = grossMonthly - totalDeduct;

        String month = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════╗\n");
        sb.append("║           PAYROLL MANAGEMENT SYSTEM              ║\n");
        sb.append("║                  — PAYSLIP —                     ║\n");
        sb.append("╠══════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Pay Period  : %-34s║\n", month));
        sb.append(String.format("║  Employee ID : %-34s║\n", emp.getId()));
        sb.append(String.format("║  Name        : %-34s║\n", emp.getName()));
        sb.append("╠══════════════════════════════════════════════════╣\n");
        sb.append("║            EARNINGS                              ║\n");
        sb.append("╠══════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Basic Salary     : %28s ║\n", fmt(monthlySalary)));
        sb.append(String.format("║  Monthly Bonus    : %28s ║\n", fmt(monthlyBonus)));
        sb.append(String.format("║  ─────────────────────────────────────────── ║\n"));
        sb.append(String.format("║  Gross Pay        : %28s ║\n", fmt(grossMonthly)));
        sb.append("╠══════════════════════════════════════════════════╣\n");
        sb.append("║            DEDUCTIONS                            ║\n");
        sb.append("╠══════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Provident Fund   : %28s ║\n", fmt(pf)));
        sb.append(String.format("║  Income Tax (TDS) : %28s ║\n", fmt(tax)));
        sb.append(String.format("║  Gratuity         : %28s ║\n", fmt(gratuity)));
        sb.append(String.format("║  ─────────────────────────────────────────── ║\n"));
        sb.append(String.format("║  Total Deductions : %28s ║\n", fmt(totalDeduct)));
        sb.append("╠══════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  NET PAY          : %28s ║\n", fmt(netPay)));
        sb.append("╚══════════════════════════════════════════════════╝\n");
        sb.append(String.format("\n  Annual CTC     : %-20s\n", fmt(emp.getTotalCompensation())));
        sb.append(String.format("  Annual Salary  : %-20s\n", fmt(emp.getSalary())));
        sb.append(String.format("  Annual Bonus   : %-20s\n", fmt(emp.getBonus())));

        return sb.toString();
    }

    private static String fmt(double val) {
        return String.format("₹ %,.2f", val);
    }
}
