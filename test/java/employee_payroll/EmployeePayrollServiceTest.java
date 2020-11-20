package employee_payroll;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class EmployeePayrollServiceTest {

	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData;
		employeePayrollData = employeePayrollService.readEmployeePayrollData();
		Assert.assertEquals(3, employeePayrollData.size());

	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData;
		employeePayrollData = employeePayrollService.readEmployeePayrollData();
		employeePayrollService.updateEmployeeSalary("Terisa", 3000000.00);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
		Assert.assertTrue(result);
	}

}