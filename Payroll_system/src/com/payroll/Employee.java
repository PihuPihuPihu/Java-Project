package com.payroll;

public class Employee {
    private int id;
    private String name;
    private double salary;
    private double bonus;

    public Employee(int id, String name, double salary, double bonus) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.bonus = bonus;
    }

    // Getters
    public int getId()        { return id; }
    public String getName()   { return name; }
    public double getSalary() { return salary; }
    public double getBonus()  { return bonus; }

    // Setters
    public void setSalary(double salary) { this.salary = salary; }
    public void setBonus(double bonus)   { this.bonus = bonus; }
    public void setName(String name)     { this.name = name; }

    public double getTotalCompensation() {
        return salary + bonus;
    }

    @Override
    public String toString() {
        return name;
    }
}
