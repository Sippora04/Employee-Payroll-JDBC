package employee_payroll;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class PayrollServiceDB {
	private static PayrollServiceDB employeePayrollServiceDB;
	private PreparedStatement preparedStatementForUpdation;
	private PreparedStatement employeePayrollDataStatement;
	private int rowAffected;

	public PayrollServiceDB() {
	}

	public static PayrollServiceDB getInstance() {
		if (employeePayrollServiceDB == null) {
			employeePayrollServiceDB = new PayrollServiceDB();
		}
		return employeePayrollServiceDB;
	}

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
			System.out.println("Driver:" + driverClass.getClass().getName());
		}
	}

	public List<EmployeePayrollData> readData() throws EmployeePayrollException {
		String sql = "SELECT * FROM employee_payroll;";
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			return this.getEmployeePayrollListFromResultset(resultSet);
		} catch (SQLException e) {
			throw new EmployeePayrollException("Unable to retrieve data from table.");
		}
	}

	private List<EmployeePayrollData> getEmployeePayrollListFromResultset(ResultSet resultSet)
			throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String objectname = resultSet.getString("name");
				String gender = resultSet.getString("gender");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, objectname, gender, salary, startDate));
			}
			return employeePayrollList;
		} catch (SQLException e) {
			throw new EmployeePayrollException("Unable to use the result set.");
		}
	}

	public List<EmployeePayrollData> getEmployeePayrollDataFromDB(String name) throws EmployeePayrollException {
		if (this.employeePayrollDataStatement == null) {
			this.prepareStatementForEmployeePayrollDataRetrieval();
		}
		try (Connection connection = this.getConnection()) {
			this.employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			return this.getEmployeePayrollListFromResultset(resultSet);
		} catch (SQLException e) {
			throw new EmployeePayrollException("Unable to read data");
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

	public int updateEmployeePayrollDataUsingPreparedStatement(String name, double salary)
			throws EmployeePayrollException {
		if (this.preparedStatementForUpdation == null) {
			this.prepareStatementForEmployeePayroll();
		}
		try {
			preparedStatementForUpdation.setDouble(1, salary);
			preparedStatementForUpdation.setString(2, name);
			int rowsAffected = preparedStatementForUpdation.executeUpdate();
			return rowsAffected;
		} catch (SQLException e) {
			throw new EmployeePayrollException("Unable to use prepared statement");
		}
	}

	private void prepareStatementForEmployeePayroll() throws EmployeePayrollException {
		try {
			Connection connection = this.getConnection();
			String sql = "UPDATE employee_payroll SET salary=? WHERE name=?";
			this.preparedStatementForUpdation = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new EmployeePayrollException("Unable to prepare statement");
		}
	}

	private void prepareStatementForEmployeePayrollDataRetrieval() throws EmployeePayrollException {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name=?";
			this.employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new EmployeePayrollException("Unable to create prepare statement");
		}
	}

	public List<EmployeePayrollData> getEmployeePayrollDataByStartingDate(LocalDate startDate, LocalDate endDate)
			throws EmployeePayrollException {
		String sql = String.format(
				"SELECT * FROM employee_payroll WHERE start BETWEEN cast('%s' as date) and cast('%s' as date);",
				startDate.toString(), endDate.toString());
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			return this.getEmployeePayrollListFromResultset(resultSet);
		} catch (SQLException e) {
			throw new EmployeePayrollException("Connection Failed.");
		}
	}

	public Map<String, Double> performAverageAndMinAndMaxOperations(String column, String operation)
			throws EmployeePayrollException {
		String sql = String.format("SELECT gender , %s(%s) FROM employee_payroll GROUP BY gender;", operation, column);
		Map<String, Double> mapValues = new HashMap<>();
		try (Connection connection = this.getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				mapValues.put(resultSet.getString(1), resultSet.getDouble(2));
			}
		} catch (SQLException e) {
			throw new EmployeePayrollException("Connection Failed.");
		}
		return mapValues;
	}

	public EmployeePayrollData addEmployeeToPayroll(String name, String gender, double salary, LocalDate startDate)
			throws EmployeePayrollException {
		int employeeId = -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format(
				"INSERT INTO employee_payroll(name,gender,salary,start) " + "VALUES ( '%s', '%s', %s, '%s' )", name,
				gender, salary, Date.valueOf(startDate));
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowsAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowsAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(employeeId, name, gender, salary, startDate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollData;
	}

	public EmployeePayrollData addingEmployeeToPayroll(String name, String gender, double salary, LocalDate startDate)
			throws EmployeePayrollException {
		int employeeId = -1;
		Connection connection = null;
		EmployeePayrollData employeePayrollData = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO employee_payroll(name,gender,salary,start) " + "VALUES ( '%s', '%s', %s, '%s' )", name,
					gender, salary, Date.valueOf(startDate));
			int rowsAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowsAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
				return employeePayrollData;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String sql = String.format("INSERT INTO payroll_details "
					+ "( emp_id, basic_pay, deductions, taxable_pay, tax , net_pay) VALUES "
					+ "(%s, %s, %s, %s, %s, %s)", employeeId, salary, deductions, taxablePay, tax, netPay);
			int rowsAffected = statement.executeUpdate(sql);
			if (rowsAffected == 1) {
				employeePayrollData = new EmployeePayrollData(employeeId, name, gender, salary, startDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return employeePayrollData;
	}

	public EmployeePayrollData addNewEmployee(int id, String name, String gender, String phone_no, String address,
			Date date, double salary, String comp_name, int comp_id, String[] department, int[] dept_id)
			throws EmployeePayrollException {
		int employeeId = 0;
		EmployeePayrollData employeePayrollData = null;
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			String sql_forcompany = String.format("INSERT INTO company values ('%s', '%s')", comp_id, comp_name);
			statement.executeUpdate(sql_forcompany);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			Statement statement = connection.createStatement();
			for (int i = 0; i < dept_id.length; i++) {
				String sql_department = String.format("INSERT INTO department values ('%s','%s')", dept_id[i],
						department[i]);
				statement.executeUpdate(sql_department);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			Statement statement_employee = connection.createStatement();
			String sql = String.format("INSERT INTO employee values ('%s','%s','%s','%s')");
			int rowAffected = statement_employee.executeUpdate(sql, statement_employee.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EmployeePayrollException("Insertion error");
			}
			return employeePayrollData;
		}
		double deductions = salary * 0.2;
		double taxable_pay = salary - deductions;
		double tax = taxable_pay * 0.1;
		double net_pay = taxable_pay - tax;
		String sql_salary = String.format("INSERT INTO payroll values (%s,%s,%s,%s,%s,%s)", id, salary, deductions,
				taxable_pay, tax, net_pay);
		try {
			Statement statement_salary = connection.createStatement();
			int rowAffected = statement_salary.executeUpdate(sql_salary, statement_salary.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement_salary.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(2);
				employeePayrollData = new EmployeePayrollData(employeeId, name, salary);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EmployeePayrollException("Insertion error");
			}
		}
		try {
			Statement statement = connection.createStatement();
			for (int i = 0; i < dept_id.length; i++) {
				String sql_emp_department = String.format("INSERT INTO Employee_Department values (%s,%s)", id,
						dept_id[i]);
				statement.executeUpdate(sql_emp_department);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					throw new EmployeePayrollException("Connection not closed");
				}
		}
		return employeePayrollData;
	}
}