package com.employeemanagementappv2.data;

public class Employee {
    public String name;
    public String department;
    public double hourlyRate;
    public double yearlySalary;

    public Employee() {}

    public Employee(String name, String department, double hourlyRate, double yearlySalary) {
        this.name = name;
        this.department = department;
        this.hourlyRate = hourlyRate;
        this.yearlySalary = yearlySalary;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getYearlySalary() {
        return yearlySalary;
    }

    public void setYearlySalary(double yearlySalary) {
        this.yearlySalary = yearlySalary;
    }

    @Override
    public String toString() {
        return name + " - " + department + " - Hourly Rate: " + hourlyRate + " - Yearly Salary: " + yearlySalary;
    }
}
