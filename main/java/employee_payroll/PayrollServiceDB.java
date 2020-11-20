package employee_payroll;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PayrollServiceDB {
	public Connection getConnection() throws EmployeePayrollException {
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service?useSSL=false";
		String userName = "root";
		String password = "admin123";
		Connection connection;
		try {
			System.out.println("Connecting to database:" + jdbcURL);
			connection = DriverManager.getConnection(jdbcURL, userName, password);
			System.out.println("Connection is successful!" + connection);
			return connection;
		} catch (SQLException e) {
			throw new EmployeePayrollException("Unable to connect / Wrong Entry");
		}
	}

	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		while (driverList.hasMoreElements()) {
			Driver driverClass = (Driver) driverList.nextElement();
			System.out.println("Driver: " + driverClass.getClass().getName());
		}
	}

	public List<EmployeePayrollData> readData() throws EmployeePayrollException {
		String sql = "SELECT * FROM employee_payroll;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
			}
			connection.close();
		} catch (SQLException e) {
			throw new EmployeePayrollException("Unable to retrieve data from table.");
		}
		return employeePayrollList;
	}

	public List<EmployeePayrollData> getEmployeePayrollDataFromDB(String name) throws EmployeePayrollException {
		String sql = String.format("SELECT * FROM employee_payroll WHERE name='%s'", name);
		List<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String objectname = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate start = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, objectname, salary, start));
			}
			return employeePayrollList;
		} catch (SQLException e) {
			throw new EmployeePayrollException("Unable to get data from database");
		}
	}

	public int updateEmployeeDataUsingStatement(String name, double salary) throws EmployeePayrollException {
		String sql = String.format("UPDATE employee_payroll SET salary=%.2f WHERE name='%s'", salary, name);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowsAffected = statement.executeUpdate(sql);
			return rowsAffected;
		} catch (SQLException e) {
			throw new EmployeePayrollException("Unable To update data in database");
		}
	}
}
