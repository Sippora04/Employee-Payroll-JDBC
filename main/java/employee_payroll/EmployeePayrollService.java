package employee_payroll;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollService {
	public PayrollServiceDB payrollServiceDB;
	public List<EmployeePayrollData> employeePayrollList;

	public EmployeePayrollService() {
		super();
		this.payrollServiceDB = new PayrollServiceDB();
	}

	public List<EmployeePayrollData> readEmployeePayrollData() throws EmployeePayrollException {
		this.employeePayrollList = this.payrollServiceDB.readData();
		return this.employeePayrollList;
	}

	public void updateEmployeeSalary(String name, double salary) throws EmployeePayrollException {
		int result = new PayrollServiceDB().updateEmployeePayrollDataUsingPreparedStatement(name, salary);
		if (result == 0)
			return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.setSalary(salary);
	}

	public EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollObject -> employeePayrollObject.getName().equals(name)).findFirst().orElse(null);
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollDataList = new PayrollServiceDB().getEmployeePayrollDataFromDB(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

	public List<EmployeePayrollData> getEmployeePayrollDataByStartDate(LocalDate startDate, LocalDate endDate)
			throws EmployeePayrollException {
		return this.payrollServiceDB.getEmployeePayrollDataByStartingDate(startDate, endDate);
	}

	public Map<String, Double> performOperationByGender(String column, String operation)
			throws EmployeePayrollException {
		return this.payrollServiceDB.performAverageAndMinAndMaxOperations(column, operation);
	}

	public void addEmployeeToPayroll(String name, String gender, double salary, LocalDate startDate)
			throws EmployeePayrollException {
		employeePayrollList.add(payrollServiceDB.addEmployeeToPayroll(name, gender, salary, startDate));

	}

	public void addingEmployeeToPayroll(String name, String gender, double salary, LocalDate startDate)
			throws EmployeePayrollException {
		employeePayrollList.add(payrollServiceDB.addEmployeeToPayroll(name, gender, salary, startDate));

	}

	public void addEmployeeToPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList) {
		Map<Integer, Boolean> employeeAddditionStatus = new HashMap<>();
	}

	public EmployeePayrollData addNewEmployee(int id, String name, String gender, String phone_no, String address,
			Date date, double salary, String comp_name, int comp_id, String[] department, int[] dept_id)
			throws EmployeePayrollException {
		return PayrollServiceDB.getInstance().addNewEmployee(id, name, gender, phone_no, address, date, salary,
				comp_name, comp_id, department, dept_id);
	}

	public void deleteEmployee(String name) throws EmployeePayrollException {
		if (!this.checkEmployeePayrollInSyncWithDB(name))
			throw new EmployeePayrollException("Employee has not present");
		PayrollServiceDB.getInstance().deleteEmployee(name);
	}
}