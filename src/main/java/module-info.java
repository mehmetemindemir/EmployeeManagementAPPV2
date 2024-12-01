module com.employeemanagementappv2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    opens com.employeemanagementappv2.data to com.fasterxml.jackson.databind,javafx.fxml;
    exports com.employeemanagementappv2;
}