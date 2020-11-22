package employee_payroll;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class EmployeePayrollServiceTest {
	// UC2
	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData;
		employeePayrollData = employeePayrollService.readEmployeePayrollData();
		Assert.assertEquals(3, employeePayrollData.size());
	}

	// UC3
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData;
		employeePayrollData = employeePayrollService.readEmployeePayrollData();
		employeePayrollService.updateEmployeeSalary("Terisa", 3000000.00);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
		Assert.assertTrue(result);
	}

	// UC4
	@Test
	public void givenEmployeePayroll_WhenUpdatedUsingPreparedStatement_ShouldSyncWithDB()
			throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData;
		employeePayrollData = employeePayrollService.readEmployeePayrollData();
		employeePayrollService.updateEmployeeSalary("Terisa", 3000000.00);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
		Assert.assertTrue(result);
	}

	// UC5
	@Test
	public void givenEmployeePayrollData_WhenRetrievedBasedOnStartDate_ShouldReturnResult()
			throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData();
		LocalDate startDate = LocalDate.parse("2018-01-01");
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> matchingRecords = employeePayrollService.getEmployeePayrollDataByStartDate(startDate,
				endDate);
		Assert.assertEquals(matchingRecords.get(0), employeePayrollService.getEmployeePayrollData("Bill"));
	}

	// UC6
	@Test
	public void givenEmployee_PerformedVariousOperations_ShouldGiveResult() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData();
		Map<String, Double> maxSalaryByGender = employeePayrollService.performOperationByGender("salary", "MAX");
		Assert.assertEquals(3000000.0, maxSalaryByGender.get("F"), 0.0);
	}

	// UC7
	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData();
		employeePayrollService.addEmployeeToPayroll("Mark", "M", 5000000.00, LocalDate.now());
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
		Assert.assertTrue(result);
	}

	// UC8
	@Test
	public void addingNewEmployee_WhenAdded_ShouldSyncWithDB() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData();
		employeePayrollService.addEmployeeToPayroll("Mark", "M", 5000000.00, LocalDate.now());
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
		Assert.assertTrue(result);
	}

	// UC9
	@Test
	public void givennewEmployeeDetails_addItInEveryTableToCompleteERDiagram_ShouldGiveResult()
			throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData();
		Date date = Date.valueOf("2020-11-19");
		boolean result;
		String[] departments = { "Marketing", "SalesAndBusiness" };
		int[] dept_id = { 01, 02 };
		EmployeePayrollData employeePayrollData = employeePayrollService.addNewEmployee(101, "Sippora", "F",
				"9988100232", "Ranchi", date, 4000000, "JHK", 11, departments, dept_id);
		boolean results = employeePayrollService.checkEmployeePayrollInSyncWithDB("Sippora");
		Assert.assertTrue(results);
	}

	// UC12
	@Test
	public void givenNameWhenDeletedShouldGetDeletedFromDatabase() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.deleteEmployee("Terisa");
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Sippora");
		Assert.assertFalse(result);
	}
}