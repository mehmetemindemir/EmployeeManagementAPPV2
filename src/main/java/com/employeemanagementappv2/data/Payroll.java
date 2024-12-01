package com.employeemanagementappv2.data;

public class Payroll {
    public Employee employee;
    public double bonus;
    public double overtimeHours;
    public double tax;
    public double netPay;

    public Payroll(Employee employee, double bonus, double overtimeHours, double tax, double netPay) {
        this.employee = employee;
        this.bonus = bonus;
        this.overtimeHours = overtimeHours;
        this.tax = tax;
        this.netPay = netPay;
    }
    public Payroll() {}

    public Employee getEmployee() {
        return employee;
    }

    public double getBonus() {
        return bonus;
    }

    public double getOvertimeHours() {
        return overtimeHours;
    }

    public double getTax() {
        return tax;
    }

    public double getNetPay() {
        return netPay;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public void setOvertimeHours(double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }

    @Override
    public String toString() {
        return "Employee: " + employee.getName() + ", Net Pay: " + netPay;
    }
}
