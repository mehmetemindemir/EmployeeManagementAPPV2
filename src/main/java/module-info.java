module com.employeemanagementappv2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.employeemanagementappv2 to javafx.fxml;
    exports com.employeemanagementappv2;
}