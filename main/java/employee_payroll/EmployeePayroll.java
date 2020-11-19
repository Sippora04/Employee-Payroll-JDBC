package employee_payroll;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class EmployeePayroll {

	public static void main(String[] args) {
		String jdbcURL = "jdbc:mysql://localhost:33306/payroll_service1?useSSL=false";
		String userName = "root";
		String password = "admin123";
		Connection connection;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver Loaded.");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannout find the driver in the classpath ", e);
		}

		listDrivers();
		try {
			System.out.println("Connection to Database: " + jdbcURL);
			connection = DriverManager.getConnection(jdbcURL, userName, password);
			System.out.println("Connection is successful !!" + connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		while (driverList.hasMoreElements()) {
			Driver driverClass = (Driver) driverList.nextElement();
			System.out.println(" " + driverClass.getClass().getName());
		}
	}
}
