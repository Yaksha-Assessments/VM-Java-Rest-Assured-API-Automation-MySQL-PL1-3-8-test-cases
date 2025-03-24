package testcases;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import coreUtilities.utils.FileOperations;
import rest.ApiUtil;
import rest.CustomResponse;

public class RestAssured_TestCases_PL1 {

	FileOperations fileOperations = new FileOperations();

	private final String EXCEL_FILE_PATH = "src/main/resources/config.xlsx"; // Path to the Excel file
	private final String SHEET_NAME = "PostData"; // Sheet name in the Excel file
	private final String FILEPATH = "src/main/java/rest/ApiUtil.java";
	ApiUtil apiUtil;

	public static int appointmentId;

	@Test(priority = 1, groups = { "PL1" }, description = "1. Send a GET request to Get All Departments\n"
			+ "2. Validate that all the DepartmentId and DepartmentName are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getAllDepartmentsTest() throws Exception {
		apiUtil = new ApiUtil();

		// Send GET request
		CustomResponse customResponse = apiUtil.getAllDepartments("/AssetReports/GetAllDepartments", null);

		// Validate the implementation of getAllDepartments
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getAllDepartments",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getAllDepartments must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getAllDepartments", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate that each department entry has non-null fields
		List<Object> departmentIds = customResponse.getPatientIds();
		List<Object> departmentNames = customResponse.getPatientCodes();

		Set<Object> uniqueDepartmentIds = new HashSet<>();
		Assert.assertFalse(departmentIds.isEmpty(), "DepartmentIds should not be empty.");
		for (int i = 0; i < departmentIds.size(); i++) {
			Assert.assertNotNull(departmentIds.get(i), "DepartmentId should not be null.");
			Assert.assertNotNull(departmentNames.get(i), "DepartmentName should not be null.");

			uniqueDepartmentIds.add(departmentIds.get(i));
		}

		// Validate uniqueness of DepartmentId
		Assert.assertEquals(uniqueDepartmentIds.size(), departmentIds.size(), "DepartmentId values should be unique.");

		System.out.println("All Departments Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 2, groups = { "PL1" }, description = "1. Send a GET request to Get All Items\n"
			+ "2. Validate that the Item Id and Item name are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getAllItemsTest() throws IOException {
		apiUtil = new ApiUtil();

		// Send GET request and receive response
		CustomResponse customResponse = apiUtil.getAllItems("/AssetReports/GetAllItems", null);

		// Validate the implementation of getAllItems
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getAllItems",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getAllItems must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getAllItems", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate that each item entry has non-null fields
		List<Object> itemIds = customResponse.getPatientIds();
		List<Object> itemNames = customResponse.getPatientCodes();

		Set<Object> uniqueItemIds = new HashSet<>();
		Assert.assertFalse(itemIds.isEmpty(), "ItemIds should not be empty.");
		for (int i = 0; i < itemIds.size(); i++) {
			Assert.assertNotNull(itemIds.get(i), "ItemId should not be null.");
			Assert.assertNotNull(itemNames.get(i), "ItemName should not be null.");

			uniqueItemIds.add(itemIds.get(i));
		}

		// Validate uniqueness of ItemId
		Assert.assertEquals(uniqueItemIds.size(), itemIds.size(), "ItemId values should be unique.");

		System.out.println("All Items Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 3, groups = { "PL1" }, description = "1. Send a GET request to Incentive Summary Report\n"
			+ "2. Validate that the Prescriber Id and Prescriber name are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getIncentiveSummary() throws Exception {
		// Initialize the ApiUtil object
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);

		apiUtil = new ApiUtil();

		String fromDate = searchResult.get("IncSummFromDate");
		String toDate = searchResult.get("IncSummToDate");
		String isRefferal = searchResult.get("IsRefferalOnly");

		// Fetch the response from the API
		CustomResponse customResponse = apiUtil
				.getIncentiveSummaryReport("https://healthapp.yaksha.com/BillingReports/INCTV_DocterSummary?FromDate="
						+ fromDate + "&ToDate=" + toDate + "&IsRefferalOnly=" + isRefferal, null);

		// Validate the response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200 OK.");

		// Validate the status field in the response
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate response fields using the shared method
		boolean isResponseValid = TestCodeValidator.validateResponseFields("getIncentiveSummaryReport", customResponse);
		Assert.assertTrue(isResponseValid, "Response validation failed.");

		// Print the entire API response for debugging
		System.out.println("Incentive Summary Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 4, groups = { "PL1" }, description = "1. Send a GET request to Incentive Referral Summary Report\n"
			+ "2. Validate that the Prescriber name, PrescriberId, DocTotalAmount, TDSAmount and NetPayableAmount are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getIncentiveReffSummary() throws Exception {
		// Initialize the ApiUtil object
		apiUtil = new ApiUtil();

		// Fetch search parameters from the Excel file
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);

		String IncFromDate = searchResult.get("IncFromDate");
		String IncToDate = searchResult.get("IncToDate");
		String isRefferal = searchResult.get("DocSumIsRefferalOnly");

		// Send GET request to fetch incentive referral summary report
		CustomResponse customResponse = apiUtil
				.getIncReffSummReport("https://healthapp.yaksha.com/BillingReports/INCTV_DocterSummary?FromDate="
						+ IncFromDate + "&ToDate=" + IncToDate + "&IsRefferalOnly=" + isRefferal, null);

		// Validate the implementation of getIncReffSummReport method
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getIncReffSummReport",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getIncentiveReffSummary must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getIncentiveReffSummary", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate the individual fields
		Assert.assertFalse(customResponse.getPrescriberNames().isEmpty(), "Prescriber Names should not be empty.");
		Assert.assertFalse(customResponse.getPrescriberIds().isEmpty(), "Prescriber Ids should not be empty.");
		Assert.assertFalse(customResponse.getDocTotalAmounts().isEmpty(), "DocTotalAmounts should not be empty.");
		Assert.assertFalse(customResponse.getTdsAmounts().isEmpty(), "TDS Amounts should not be empty.");
		Assert.assertFalse(customResponse.getNetPayableAmounts().isEmpty(), "Net Payable Amounts should not be empty.");

		// Iterate through each record and validate fields
		for (int i = 0; i < customResponse.getPrescriberNames().size(); i++) {
			Object prescriberName = customResponse.getPrescriberNames().get(i);
			Object prescriberId = customResponse.getPrescriberIds().get(i);
			Object docTotalAmount = customResponse.getDocTotalAmounts().get(i);
			Object tdsAmount = customResponse.getTdsAmounts().get(i);
			Object netPayableAmount = customResponse.getNetPayableAmounts().get(i);

			// Assert fields are not null
			Assert.assertNotNull(prescriberName, "The Prescriber Name is null.");
			Assert.assertNotNull(prescriberId, "The Prescriber ID is null.");
			Assert.assertNotNull(docTotalAmount, "The DocTotal Amount is null.");
			Assert.assertNotNull(tdsAmount, "The TDS Amount is null.");
			Assert.assertNotNull(netPayableAmount, "The Net Payable Amount is null.");

			// Print extracted fields for debugging
			System.out.println("PrescriberName: " + prescriberName);
			System.out.println("PrescriberId: " + prescriberId);
			System.out.println("DocTotalAmount: " + docTotalAmount);
			System.out.println("TDSAmount: " + tdsAmount);
			System.out.println("NetPayableAmount: " + netPayableAmount);
			System.out.println();
		}

		// Print full API response for debugging
		System.out.println("Full API Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 5, groups = { "PL1" }, description = "1. Send a GET request to Hospital Income Incentive Report\n"
			+ "2. Validate that the ServiceDepartmentName, ServiceDepartmentId, NetSales, ReferralCommission, GrossIncome, OtherIncentive, and HospitalNetIncome are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getHospitalIncomeIncReport() throws Exception {
		// Initialize the ApiUtil object
		apiUtil = new ApiUtil();

		// Fetch search parameters from the Excel file
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);

		String IncFromDate = searchResult.get("IncFromDate");
		String IncToDate = searchResult.get("IncToDate");
		String ServiceDepartments = searchResult.get("ServiceDepartments");

		// Send GET request to fetch hospital income incentive report
		CustomResponse hospitalIncomeResponse = apiUtil
				.getHospIncIncReport("https://healthapp.yaksha.com/Reporting/HospitalIncomeIncentiveReport?FromDate="
						+ IncFromDate + "&ToDate=" + IncToDate + "&ServiceDepartments=" + ServiceDepartments, null);

		// Validate the implementation of getHospIncIncReport method using RestAssured
		// methods
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getHospIncIncReport",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getHospIncIncReport must be implemented using Rest Assured methods only.");

		// Validate response structure using validateResponseFields
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getHospIncIncReport", hospitalIncomeResponse),
				"Must have all required fields in the response.");

		// Validate the response status code
		Assert.assertEquals(hospitalIncomeResponse.getStatusCode(), 200, "Status code should be 200 OK.");

		// Validate the "Status" field in the response
		String status = hospitalIncomeResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Extract individual fields from the response
		List<Object> serviceDepartmentIds = hospitalIncomeResponse.getServiceDepartmentIds();
		List<Object> serviceDepartmentNames = hospitalIncomeResponse.getServiceDepartmentNames();
		List<Object> netSales = hospitalIncomeResponse.getNetSales();
		List<Object> referralCommissions = hospitalIncomeResponse.getReferralCommissions();
		List<Object> grossIncomes = hospitalIncomeResponse.getGrossIncomes();
		List<Object> otherIncentives = hospitalIncomeResponse.getOtherIncentives();
		List<Object> hospitalNetIncomes = hospitalIncomeResponse.getHospitalNetIncomes();

		// Assert that results are not null
		Assert.assertNotNull(serviceDepartmentIds, "ServiceDepartmentIds field should not be null.");
		Assert.assertNotNull(serviceDepartmentNames, "ServiceDepartmentNames field should not be null.");
		Assert.assertNotNull(netSales, "NetSales field should not be null.");
		Assert.assertNotNull(referralCommissions, "ReferralCommissions field should not be null.");
		Assert.assertNotNull(grossIncomes, "GrossIncomes field should not be null.");
		Assert.assertNotNull(otherIncentives, "OtherIncentives field should not be null.");
		Assert.assertNotNull(hospitalNetIncomes, "HospitalNetIncomes field should not be null.");

		// Assert that lists are not empty
		Assert.assertFalse(serviceDepartmentIds.isEmpty(), "ServiceDepartmentIds list should not be empty.");
		Assert.assertFalse(serviceDepartmentNames.isEmpty(), "ServiceDepartmentNames list should not be empty.");
		Assert.assertFalse(netSales.isEmpty(), "NetSales list should not be empty.");
		Assert.assertFalse(referralCommissions.isEmpty(), "ReferralCommissions list should not be empty.");
		Assert.assertFalse(grossIncomes.isEmpty(), "GrossIncomes list should not be empty.");
		Assert.assertFalse(otherIncentives.isEmpty(), "OtherIncentives list should not be empty.");
		Assert.assertFalse(hospitalNetIncomes.isEmpty(), "HospitalNetIncomes list should not be empty.");

		// Iterate through each record and validate fields
		for (int i = 0; i < serviceDepartmentIds.size(); i++) {
			// Extract values from each list
			Object serviceDepartmentId = serviceDepartmentIds.get(i);
			Object serviceDepartmentName = serviceDepartmentNames.get(i);
			Object netSale = netSales.get(i);
			Object referralCommission = referralCommissions.get(i);
			Object grossIncome = grossIncomes.get(i);
			Object otherIncentive = otherIncentives.get(i);
			Object hospitalNetIncome = hospitalNetIncomes.get(i);

			// Assert fields are not null
			Assert.assertNotNull(serviceDepartmentId, "The ServiceDepartmentId is null.");
			Assert.assertNotNull(serviceDepartmentName, "The ServiceDepartmentName is null.");
			Assert.assertNotNull(netSale, "The NetSales is null.");
			Assert.assertNotNull(referralCommission, "The ReferralCommission is null.");
			Assert.assertNotNull(grossIncome, "The GrossIncome is null.");
			Assert.assertNotNull(otherIncentive, "The OtherIncentive is null.");
			Assert.assertNotNull(hospitalNetIncome, "The HospitalNetIncome is null.");

			// Print extracted fields for debugging
			System.out.println("ServiceDepartmentId: " + serviceDepartmentId);
			System.out.println("ServiceDepartmentName: " + serviceDepartmentName);
			System.out.println("NetSales: " + netSale);
			System.out.println("ReferralCommission: " + referralCommission);
			System.out.println("GrossIncome: " + grossIncome);
			System.out.println("OtherIncentive: " + otherIncentive);
			System.out.println("HospitalNetIncome: " + hospitalNetIncome);
			System.out.println();
		}

		// Print the entire API response
		System.out.println("Full API Response:");
		hospitalIncomeResponse.getResponse().prettyPrint();
	}

	@Test(priority = 6, groups = { "PL1" }, description = "1. Send a GET request to Incentive Employee Bill Items\n"
			+ "2. Validate that the EmployeeIncentiveInfoId, EmployeeId, FullName, TDSPercent, EmpTDSPercent, IsActive are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getIncenEmpBillItems() throws Exception {
		apiUtil = new ApiUtil();

		// Fetch the employeeId from the Excel file
		Map<String, String> searchResult = fileOperations.readExcelPOI(EXCEL_FILE_PATH, SHEET_NAME);
		String employeeId = searchResult.get("employeeId");

		// Send GET request to fetch employee bill items
		CustomResponse customResponse = apiUtil.getEmpBillItem("/Incentive/EmployeeBillItems?employeeId=" + employeeId,
				null);

		// Validate the implementation of getEmpBillItem method using Rest Assured
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getEmpBillItem",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getIncenEmpBillItems must be implemented using Rest Assured methods only.");

		// Validate the response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200 OK.");

		// Validate the status field in the response
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Extract and validate each field directly from the "result" map
		Object employeeIncentiveInfoId = customResponse.getEmployeeIncentiveInfoId();
		Object employeeIdFromResponse = customResponse.getEmployeeId();
		Object fullName = customResponse.getFullName();
		Object tdsPercent = customResponse.getTdsPercent();
		Object empTdsPercent = customResponse.getEmpTdsPercent();
		Object isActive = customResponse.getIsActive();
		List<Map<String, Object>> employeeBillItemsMap = customResponse.getEmployeeBillItemsMap();

		// Assert fields are not null
		Assert.assertNotNull(employeeIncentiveInfoId, "EmployeeIncentiveInfoId should not be null.");
		Assert.assertNotNull(employeeIdFromResponse, "EmployeeId should not be null.");
		Assert.assertNotNull(fullName, "FullName should not be null.");
		Assert.assertNotNull(tdsPercent, "TDSPercent should not be null.");
		Assert.assertNotNull(empTdsPercent, "EmpTDSPercent should not be null.");
		Assert.assertNotNull(isActive, "IsActive should not be null.");
		Assert.assertNotNull(employeeBillItemsMap, "EmployeeBillItemsMap should not be null.");

		// Additional validation: check if EmployeeBillItemsMap is empty (which is
		// allowed, as per the response structure)
		if (employeeBillItemsMap != null && employeeBillItemsMap.isEmpty()) {
			System.out.println("EmployeeBillItemsMap is empty, which is allowed.");
		}

		// Print extracted fields for debugging
		System.out.println("EmployeeIncentiveInfoId: " + employeeIncentiveInfoId);
		System.out.println("EmployeeId: " + employeeIdFromResponse);
		System.out.println("FullName: " + fullName);
		System.out.println("TDSPercent: " + tdsPercent);
		System.out.println("EmpTDSPercent: " + empTdsPercent);
		System.out.println("IsActive: " + isActive);
		System.out.println("EmployeeBillItemsMap: " + employeeBillItemsMap);
		System.out.println();

		// Print the entire API response for debugging
		System.out.println("Employee Bill Items Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 7, groups = { "PL1" }, description = "1. Send a GET request to Inventory Fiscal Years\n"
			+ "2. Validate that the FiscalYearId, FiscalYearName are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getInventoryFiscalYears() throws IOException {
		apiUtil = new ApiUtil();

		// Fetch the response from the API
		CustomResponse customResponse = apiUtil.getInvntryFiscalYrs("/Inventory/InventoryFiscalYears", null);

		// Validate the implementation of getInvntryFiscalYrs
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getInvntryFiscalYrs",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getInvntryFiscalYrs must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getInvntryFiscalYrs", customResponse),
				"Must have all required fields in the response.");

		// Validate the response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate each fiscal year field directly from the extracted lists
		List<Object> fiscalYearIds = customResponse.getPrescriberIds();
		List<Object> fiscalYearNames = customResponse.getPrescriberNames();
		List<Object> startDates = customResponse.getDocTotalAmounts();
		List<Object> endDates = customResponse.getTdsAmounts();
		List<Object> isActiveList = customResponse.getNetPayableAmounts();

		// Validate that none of the fields are null
		for (int i = 0; i < fiscalYearIds.size(); i++) {
			Assert.assertNotNull(fiscalYearIds.get(i), "FiscalYearId at index " + i + " should not be null.");
			Assert.assertNotNull(fiscalYearNames.get(i), "FiscalYearName at index " + i + " should not be null.");
			Assert.assertNotNull(startDates.get(i), "StartDate at index " + i + " should not be null.");
			Assert.assertNotNull(endDates.get(i), "EndDate at index " + i + " should not be null.");
			Assert.assertNotNull(isActiveList.get(i), "IsActive at index " + i + " should not be null.");
		}

		// Print the extracted fields for debugging
		System.out.println("FiscalYearId: " + fiscalYearIds);
		System.out.println("FiscalYearName: " + fiscalYearNames);
		System.out.println("StartDate: " + startDates);
		System.out.println("EndDate: " + endDates);
		System.out.println("IsActive: " + isActiveList);
	}

	@Test(priority = 8, groups = { "PL1" }, description = "1. Send a GET request to Activate Inventory\n"
			+ "2. Validate that the StoreId, Name, and StoreDescription are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void activateInventory() throws IOException {
		apiUtil = new ApiUtil();

		// Fetch the response from the API
		CustomResponse customResponse = apiUtil.getActInventory("/ActivateInventory/", null);

		// Validate the implementation of getActInventory
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getActInventory",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getActInventory must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getActInventory", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Loop through each store and validate the necessary fields
		List<Object> storeIds = customResponse.getItemIds();
		List<Object> names = customResponse.getItemNames();
		List<Object> storeDescriptions = customResponse.getGenericNames();

		// Validate that none of the fields are null
		for (int i = 0; i < storeIds.size(); i++) {
			Assert.assertNotNull(storeIds.get(i), "StoreId at index " + i + " should not be null.");
			Assert.assertNotNull(names.get(i), "Name at index " + i + " should not be null.");
			Assert.assertNotNull(storeDescriptions.get(i), "StoreDescription at index " + i + " should not be null.");
		}

		// Print the extracted fields for debugging
		System.out.println("StoreIds: " + storeIds);
		System.out.println("Names: " + names);
		System.out.println("StoreDescriptions: " + storeDescriptions);
	}

}
