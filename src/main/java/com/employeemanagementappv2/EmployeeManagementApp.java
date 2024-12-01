package com.employeemanagementappv2;

import com.employeemanagementappv2.data.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeManagementApp extends Application {

    private static final Map<String, String> users = new HashMap<>();
    private static final String EMPLOYEE_DATA_FILE = "employees.json";
    private static final String DEPARTMENT_DATA_FILE = "departments.json";
    private static final String PAYROLL_DATA_FILE = "payrolls.json";
    Alert alert=null;
    static {
        users.put("admin", "123");
    }
    private boolean authenticate(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employee Management System - Login");
        primaryStage.setResizable(false);
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(8);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        GridPane.setConstraints(usernameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        GridPane.setConstraints(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.length() < 3 || password.length() < 3) {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Username and password must be at least 3 characters long.");
                alert.showAndWait();
            } else if (authenticate(username, password)) {
                System.out.println("Login successful!");
                showMainScreen(primaryStage);
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid credentials! Please try again.");
                alert.showAndWait();
            }
        });

        gridPane.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton);
        StackPane root = new StackPane();
        root.getChildren().add(gridPane);
        Scene scene = new Scene(root, 300, 300);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }



    private void showMainScreen(Stage primaryStage) {
        primaryStage.setTitle("Employee Management System");

        TabPane tabPane = new TabPane();

        Tab employeeTab = new Tab("Employee");
        employeeTab.setContent(createEmployeeTabContent(primaryStage));
        employeeTab.setOnSelectionChanged(e -> {
            if (employeeTab.isSelected()) {
                employeeTab.setContent(createEmployeeTabContent(primaryStage));
            }
        });

        Tab payrollTab = new Tab("Payroll");
        payrollTab.setContent(createPayrollTabContent(primaryStage));
        payrollTab.setOnSelectionChanged(e -> {
            if (payrollTab.isSelected()) {
                payrollTab.setContent(createPayrollTabContent(primaryStage));
            }
        });

        Tab departmentTab = new Tab("Department");
        departmentTab.setContent(createDepartmentTabContent());
        departmentTab.setOnSelectionChanged(e -> {
            if (departmentTab.isSelected()) {
                departmentTab.setContent(createDepartmentTabContent());
            }
        });

        tabPane.getTabs().addAll(employeeTab, payrollTab, departmentTab);

        Scene mainScene = new Scene(tabPane, 800, 600);

        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();

    }
    private BorderPane createDepartmentTabContent() {
        ObservableList<Department> departments = FXCollections.observableArrayList(loadDepartments());
        ListView<Department> departmentListView = new ListView<>(departments);

        Button addButton = new Button("Add Department");
        Button removeButton = new Button("Remove Department");
        Button updateButton = new Button("Update Department");

        addButton.setOnAction(e -> showAddDepartmentScreen(new Stage(), departments));

        removeButton.setOnAction(e -> {
            Department selectedDepartment = departmentListView.getSelectionModel().getSelectedItem();
            if (selectedDepartment != null) {
                departments.remove(selectedDepartment);
                saveDepartments(departments);
            }
        });

        updateButton.setOnAction(e -> {
            Department selectedDepartment = departmentListView.getSelectionModel().getSelectedItem();
            if (selectedDepartment != null) {
                showUpdateDepartmentScreen(new Stage(), selectedDepartment, departments);
            }
        });


        VBox departmentBox = new VBox(10);
        departmentBox.setPadding(new Insets(10));
        departmentBox.getChildren().addAll(new Label("Departments:"), departmentListView);

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(addButton, removeButton, updateButton);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(departmentBox);
        borderPane.setBottom(buttonBox);

        return borderPane;
    }
    private BorderPane createPayrollTabContent(Stage primaryStage) {
        ObservableList<Employee> employees = FXCollections.observableArrayList(loadEmployees());
        ObservableList<Payroll> payrolls = FXCollections.observableArrayList(loadPayrolls());
        VBox payrollBox = new VBox(10);
        payrollBox.setPadding(new Insets(10));

        ListView<Payroll> payrollListView = new ListView<>(payrolls);

        Button addPayrollButton = new Button("Add Payroll");
        Button updatePayrollButton = new Button("Update Payroll");
        Button removePayrollButton = new Button("Remove Payroll");

        addPayrollButton.setOnAction(e -> showAddPayrollScreen(primaryStage, employees, payrollListView, payrolls));

        updatePayrollButton.setOnAction(e -> {
            Payroll selectedPayroll = payrollListView.getSelectionModel().getSelectedItem();
            if (selectedPayroll != null) {
                showUpdatePayrollScreen(primaryStage, selectedPayroll, payrolls);
            }
        });

        removePayrollButton.setOnAction(e -> {
            Payroll selectedPayroll = payrollListView.getSelectionModel().getSelectedItem();
            if (selectedPayroll != null) {
                payrolls.remove(selectedPayroll);
                savePayrolls(payrolls);
            }
        });


        VBox payrollsBox= new VBox(10);
        payrollsBox.setPadding(new Insets(10));
        payrollsBox.getChildren().addAll(new Label("Payroll Details:"), payrollListView);

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(addPayrollButton, removePayrollButton, updatePayrollButton);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(payrollsBox);
        borderPane.setBottom(buttonBox);

        return borderPane;
    }
    private BorderPane createEmployeeTabContent(Stage primaryStage) {
        ObservableList<Employee> employees = FXCollections.observableArrayList(loadEmployees());
        ObservableList<Department> departments = FXCollections.observableArrayList(loadDepartments());
        ListView<Employee> employeeListView = new ListView<>(employees);

        Button addButton = new Button("Add Employee");
        Button removeButton = new Button("Remove Employee");
        Button updateButton = new Button("Update Employee");

        addButton.setOnAction(e -> showAddEmployeeScreen(primaryStage, employees, departments));

        removeButton.setOnAction(e -> {
            Employee selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();
            if (selectedEmployee != null) {
                employees.remove(selectedEmployee);
                saveEmployees(employees);
            }
        });

        updateButton.setOnAction(e -> {
            Employee selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();
            if (selectedEmployee != null) {
                showUpdateEmployeeScreen(primaryStage, selectedEmployee, employees);
            }
        });

        // Layout for employee tab
        VBox employeeBox = new VBox(10);
        employeeBox.setPadding(new Insets(10));
        employeeBox.getChildren().addAll(new Label("Employees:"), employeeListView);

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(addButton, removeButton, updateButton);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(employeeBox);
        borderPane.setBottom(buttonBox);

        return borderPane;
    }

    private void showAddEmployeeScreen(Stage owner, ObservableList<Employee> employees, ObservableList<Department> departments) {
        Stage addEmployeeStage = new Stage();
        addEmployeeStage.setTitle("Add Employee");
        addEmployeeStage.initModality(Modality.WINDOW_MODAL);
        GridPane addEmployeeGrid = new GridPane();

        addEmployeeGrid.setPadding(new Insets(10, 10, 10, 10));
        addEmployeeGrid.setVgap(8);
        addEmployeeGrid.setHgap(10);

        Label nameLabel = new Label("Name:");
        GridPane.setConstraints(nameLabel, 0, 0);
        TextField nameField = new TextField();
        nameField.setPromptText("Enter employee name");
        GridPane.setConstraints(nameField, 1, 0);

        Label departmentLabel = new Label("Department:");
        GridPane.setConstraints(departmentLabel, 0, 1);
        ComboBox<Department> departmentComboBox = new ComboBox<>(departments);
        departmentComboBox.setPromptText("Select department");
        GridPane.setConstraints(departmentComboBox, 1, 1);

        Label hourlyRateLabel = new Label("Hourly Rate:");
        GridPane.setConstraints(hourlyRateLabel, 0, 2);
        TextField hourlyRateField = new TextField();
        hourlyRateField.setPromptText("Enter hourly rate");
        GridPane.setConstraints(hourlyRateField, 1, 2);

        Label yearlySalaryLabel = new Label("Yearly Salary:");
        GridPane.setConstraints(yearlySalaryLabel, 0, 3);
        TextField yearlySalaryField = new TextField();
        yearlySalaryField.setEditable(false);
        GridPane.setConstraints(yearlySalaryField, 1, 3);

        hourlyRateField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double hourlyRate = Double.parseDouble(newValue);
                double yearlySalary = hourlyRate * 40 * 52; // Assuming 40 hours per week, 52 weeks per year
                yearlySalaryField.setText(String.valueOf(yearlySalary));
            } catch (NumberFormatException ex) {
                yearlySalaryField.setText("");
            }
        });

        Button saveButton = new Button("Save");
        GridPane.setConstraints(saveButton, 1, 4);
        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            Department department = departmentComboBox.getValue();
            double hourlyRate = Double.parseDouble(hourlyRateField.getText());
            double yearlySalary = Double.parseDouble(yearlySalaryField.getText());
            Employee newEmployee = new Employee(name, department.getName(), hourlyRate, yearlySalary);
            employees.add(newEmployee);
            saveEmployees(employees);
            addEmployeeStage.close();
        });

        addEmployeeGrid.getChildren().addAll(nameLabel, nameField, departmentLabel, departmentComboBox, hourlyRateLabel, hourlyRateField, yearlySalaryLabel, yearlySalaryField, saveButton);


        Scene addEmployeeScene = new Scene(addEmployeeGrid, 400, 300);
        addEmployeeStage.setScene(addEmployeeScene);
        addEmployeeStage.showAndWait();
    }

    private void showUpdateEmployeeScreen(Stage owner, Employee employee, ObservableList<Employee> employees) {
        Stage updateEmployeeStage = new Stage();
        updateEmployeeStage.setTitle("Update Employee");
        updateEmployeeStage.initModality(Modality.WINDOW_MODAL);
        updateEmployeeStage.initOwner(owner);

        GridPane updateEmployeeGrid = new GridPane();
        updateEmployeeGrid.setPadding(new Insets(10, 10, 10, 10));
        updateEmployeeGrid.setVgap(8);
        updateEmployeeGrid.setHgap(10);

        Label nameLabel = new Label("Name:");
        GridPane.setConstraints(nameLabel, 0, 0);
        TextField nameField = new TextField(employee.getName());
        GridPane.setConstraints(nameField, 1, 0);

        Label departmentLabel = new Label("Department:");
        GridPane.setConstraints(departmentLabel, 0, 1);
        TextField departmentField = new TextField(employee.getDepartment());
        GridPane.setConstraints(departmentField, 1, 1);

        Label hourlyRateLabel = new Label("Hourly Rate:");
        GridPane.setConstraints(hourlyRateLabel, 0, 2);
        TextField hourlyRateField = new TextField(String.valueOf(employee.getHourlyRate()));
        GridPane.setConstraints(hourlyRateField, 1, 2);

        Label yearlySalaryLabel = new Label("Yearly Salary:");
        GridPane.setConstraints(yearlySalaryLabel, 0, 3);
        TextField yearlySalaryField = new TextField(String.valueOf(employee.getYearlySalary()));
        yearlySalaryField.setEditable(false);
        GridPane.setConstraints(yearlySalaryField, 1, 3);

        hourlyRateField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double hourlyRate = Double.parseDouble(newValue);
                double yearlySalary = hourlyRate * 40 * 52; // Assuming 40 hours per week, 52 weeks per year
                yearlySalaryField.setText(String.valueOf(yearlySalary));
            } catch (NumberFormatException ex) {
                yearlySalaryField.setText("");
            }
        });

        Button saveButton = new Button("Update");
        GridPane.setConstraints(saveButton, 1, 4);
        saveButton.setOnAction(e -> {
            employee.setName(nameField.getText());
            employee.setDepartment(departmentField.getText());
            employee.setHourlyRate(Double.parseDouble(hourlyRateField.getText()));
            employee.setYearlySalary(Double.parseDouble(yearlySalaryField.getText()));
            saveEmployees(employees);
            updateEmployeeStage.close();
        });

        updateEmployeeGrid.getChildren().addAll(nameLabel, nameField, departmentLabel, departmentField, hourlyRateLabel, hourlyRateField, yearlySalaryLabel, yearlySalaryField, saveButton);

        Scene updateEmployeeScene = new Scene(updateEmployeeGrid, 400, 300);
        updateEmployeeStage.setScene(updateEmployeeScene);
        updateEmployeeStage.centerOnScreen();

        updateEmployeeStage.showAndWait();
    }

    private void showAddDepartmentScreen(Stage owner, ObservableList<Department> departments) {
        Stage addDepartmentStage = new Stage();
        addDepartmentStage.setTitle("Add Department");
        addDepartmentStage.initModality(Modality.WINDOW_MODAL);
        addDepartmentStage.initOwner(owner);

        GridPane addDepartmentGrid = new GridPane();
        addDepartmentGrid.setPadding(new Insets(10, 10, 10, 10));
        addDepartmentGrid.setVgap(8);
        addDepartmentGrid.setHgap(10);

        Label codeLabel = new Label("Code:");
        GridPane.setConstraints(codeLabel, 0, 0);
        TextField codeField = new TextField();
        codeField.setPromptText("Enter department code");
        GridPane.setConstraints(codeField, 1, 0);

        Label nameLabel = new Label("Name:");
        GridPane.setConstraints(nameLabel, 0, 1);
        TextField nameField = new TextField();
        nameField.setPromptText("Enter department name");
        GridPane.setConstraints(nameField, 1, 1);

        Button saveButton = new Button("Save");
        GridPane.setConstraints(saveButton, 1, 2);
        saveButton.setOnAction(e -> {
            String code = codeField.getText();
            String name = nameField.getText();
            Department newDepartment = new Department(code, name);
            departments.add(newDepartment);
            saveDepartments(departments);
            addDepartmentStage.close();
        });

        addDepartmentGrid.getChildren().addAll(codeLabel, codeField, nameLabel, nameField, saveButton);

        Scene addDepartmentScene = new Scene(addDepartmentGrid, 300, 200);
        addDepartmentStage.setScene(addDepartmentScene);
        addDepartmentStage.showAndWait();
    }



    private void showAddPayrollScreen(Stage owner, ObservableList<Employee> employees, ListView<Payroll> payrollListView, ObservableList<Payroll> payrolls) {
        Stage addPayrollStage = new Stage();
        addPayrollStage.setTitle("Add Payroll");
        addPayrollStage.initModality(Modality.WINDOW_MODAL);
        addPayrollStage.initOwner(owner);

        // Layout for add payroll screen
        GridPane addPayrollGrid = new GridPane();
        addPayrollGrid.setPadding(new Insets(10, 10, 10, 10));
        addPayrollGrid.setVgap(8);
        addPayrollGrid.setHgap(10);

        Label employeeLabel = new Label("Employee:");
        GridPane.setConstraints(employeeLabel, 0, 0);
        ComboBox<Employee> employeeComboBox = new ComboBox<>(employees);
        employeeComboBox.setPromptText("Select employee");
        GridPane.setConstraints(employeeComboBox, 1, 0);

        Label bonusLabel = new Label("Bonus:");
        GridPane.setConstraints(bonusLabel, 0, 1);

        TextField bonusField = new TextField();
        bonusField.setPromptText("Enter bonus amount");
        GridPane.setConstraints(bonusField, 1, 1);

        Label overtimeLabel = new Label("Overtime Hours:");
        GridPane.setConstraints(overtimeLabel, 0, 2);
        TextField overtimeField = new TextField();
        overtimeField.setPromptText("Enter overtime hours");
        GridPane.setConstraints(overtimeField, 1, 2);

        Button saveButton = new Button("Save");
        GridPane.setConstraints(saveButton, 1, 3);

        saveButton.setOnAction(e -> {
            try {
                Employee selectedEmployee = employeeComboBox.getValue();
                if (selectedEmployee != null) {

                    double bonus = Double.parseDouble(bonusField.getText());
                    double overtimeHours = Double.parseDouble(overtimeField.getText());
                    double overtimePay = overtimeHours * selectedEmployee.getHourlyRate() * 1.5;
                    double grossPay = selectedEmployee.getYearlySalary() + bonus + overtimePay;
                    double tax = grossPay * 0.2;
                    double netPay = grossPay - tax;

                    Payroll newPayroll = new Payroll(selectedEmployee, bonus, overtimeHours, tax, netPay);

                    payrolls.add(newPayroll);
                    payrollListView.getItems().add(newPayroll);
                    savePayrolls(payrolls);
                    addPayrollStage.close();
                }else {
                    System.out.println("Please select an employee.");
                }
            }catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter valid numbers for bonus and overtime hours.");
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid input");
                alert.setContentText("Please enter valid numbers for bonus and overtime hours.");
                alert.showAndWait();
            }

        });

        addPayrollGrid.getChildren().addAll(employeeLabel, employeeComboBox, bonusLabel, bonusField, overtimeLabel, overtimeField, saveButton);

        Scene addPayrollScene = new Scene(addPayrollGrid, 400, 250);
        addPayrollStage.setScene(addPayrollScene);
        addPayrollStage.showAndWait();
    }

    private void showUpdateDepartmentScreen(Stage owner, Department department, ObservableList<Department> departments) {
        Stage updateDepartmentStage = new Stage();
        updateDepartmentStage.setTitle("Update Department");
        updateDepartmentStage.initModality(Modality.WINDOW_MODAL);
        updateDepartmentStage.initOwner(owner);

        GridPane updateDepartmentGrid = new GridPane();
        updateDepartmentGrid.setPadding(new Insets(10, 10, 10, 10));
        updateDepartmentGrid.setVgap(8);
        updateDepartmentGrid.setHgap(10);

        Label codeLabel = new Label("Code:");
        GridPane.setConstraints(codeLabel, 0, 0);
        TextField codeField = new TextField(department.getCode());
        GridPane.setConstraints(codeField, 1, 0);

        Label nameLabel = new Label("Name:");
        GridPane.setConstraints(nameLabel, 0, 1);
        TextField nameField = new TextField(department.getName());
        GridPane.setConstraints(nameField, 1, 1);

        Button saveButton = new Button("Update");
        GridPane.setConstraints(saveButton, 1, 2);
        saveButton.setOnAction(e -> {
            department.setCode(codeField.getText());
            department.setName(nameField.getText());
            saveDepartments(departments);
            updateDepartmentStage.close();
        });

        updateDepartmentGrid.getChildren().addAll(codeLabel, codeField, nameLabel, nameField, saveButton);

        Scene updateDepartmentScene = new Scene(updateDepartmentGrid, 300, 200);
        updateDepartmentStage.setScene(updateDepartmentScene);
        updateDepartmentStage.showAndWait();
    }
    private void showUpdatePayrollScreen(Stage owner, Payroll payroll, ObservableList<Payroll> payrolls) {
        Stage updatePayrollStage = new Stage();
        updatePayrollStage.setTitle("Update Payroll");
        updatePayrollStage.initModality(Modality.WINDOW_MODAL);
        updatePayrollStage.initOwner(owner);


        GridPane updatePayrollGrid = new GridPane();
        updatePayrollGrid.setPadding(new Insets(10, 10, 10, 10));
        updatePayrollGrid.setVgap(8);
        updatePayrollGrid.setHgap(10);

        Label employeeLabel = new Label("Employee:");
        GridPane.setConstraints(employeeLabel, 0, 0);
        TextField employeeField = new TextField(payroll.getEmployee().getName());
        employeeField.setEditable(false);
        GridPane.setConstraints(employeeField, 1, 0);

        Label bonusLabel = new Label("Bonus:");
        GridPane.setConstraints(bonusLabel, 0, 1);
        TextField bonusField = new TextField(String.valueOf(payroll.getBonus()));
        GridPane.setConstraints(bonusField, 1, 1);

        Label overtimeLabel = new Label("Overtime Hours:");
        GridPane.setConstraints(overtimeLabel, 0, 2);
        TextField overtimeField = new TextField(String.valueOf(payroll.getOvertimeHours()));
        GridPane.setConstraints(overtimeField, 1, 2);

        Button saveButton = new Button("Update");
        GridPane.setConstraints(saveButton, 1, 3);
        saveButton.setOnAction(e -> {
            try {
                double bonus = Double.parseDouble(bonusField.getText());
                double overtimeHours = Double.parseDouble(overtimeField.getText());
                double overtimePay = overtimeHours * payroll.getEmployee().getHourlyRate() * 1.5;
                double grossPay = payroll.getEmployee().getYearlySalary() + bonus + overtimePay;
                double tax = grossPay * 0.2;
                double netPay = grossPay - tax;

                payroll.setBonus(bonus);
                payroll.setOvertimeHours(overtimeHours);
                payroll.setTax(tax);
                payroll.setNetPay(netPay);

                savePayrolls(payrolls);
                updatePayrollStage.close();
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter valid numbers for bonus and overtime hours.");
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid input");
                alert.setContentText("Please enter valid numbers for bonus and overtime hours.");
                alert.showAndWait();
            }
        });
        updatePayrollGrid.getChildren().addAll(employeeLabel, employeeField, bonusLabel, bonusField, overtimeLabel, overtimeField, saveButton);

        Scene updatePayrollScene = new Scene(updatePayrollGrid, 400, 250);
        updatePayrollStage.setScene(updatePayrollScene);
        updatePayrollStage.showAndWait();
    }

    private List<Employee> loadEmployees() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(EMPLOYEE_DATA_FILE);
            if (file.exists()) {
                return mapper.readValue(file, new TypeReference<List<Employee>>() {});
            }
        } catch (IOException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error loading employees");
            alert.setContentText("Please try again later."+e.getMessage());
            alert.showAndWait();
            System.out.println(e.getMessage());

        }
        return FXCollections.observableArrayList();
    }
    private void saveEmployees(ObservableList<Employee> employees) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(EMPLOYEE_DATA_FILE), employees);
        } catch (IOException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error saving employees");
            alert.setContentText("Please try again later."+e.getMessage());
            alert.showAndWait();
        }
    }

    private List<Department> loadDepartments() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(DEPARTMENT_DATA_FILE);
            if (file.exists()) {
                return mapper.readValue(file, new TypeReference<List<Department>>() {});
            }
        } catch (IOException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error loading departments");
            alert.setContentText("Please try again later."+e.getMessage());
            alert.showAndWait();
        }
        return FXCollections.observableArrayList();
    }

    // Save departments to JSON file
    private void saveDepartments(ObservableList<Department> departments) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(DEPARTMENT_DATA_FILE), departments);
        } catch (IOException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error saving departments");
            alert.setContentText("Please try again later."+e.getMessage());
            alert.showAndWait();
        }
    }

    private List<Payroll> loadPayrolls() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(PAYROLL_DATA_FILE);
            if (file.exists()) {
                return mapper.readValue(file, new TypeReference<List<Payroll>>() {});
            }
        } catch (IOException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error loading payrolls");
            alert.setContentText("Please try again later."+e.getMessage());
            alert.showAndWait();
        }
        return FXCollections.observableArrayList();
    }

    // Save payrolls to JSON file
    private void savePayrolls(ObservableList<Payroll> payrolls) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(PAYROLL_DATA_FILE), payrolls);
        } catch (IOException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error saving payrolls");
            alert.setContentText("Please try again later."+e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}