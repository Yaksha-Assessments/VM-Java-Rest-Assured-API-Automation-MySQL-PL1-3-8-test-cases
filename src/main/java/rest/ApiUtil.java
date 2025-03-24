package rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiUtil {

	private static final String BASE_URL = "https://healthapp.yaksha.com/api";

	/**
	 * @Test1 This method retrieves and verifies the list of departments.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - The API response includes HTTP status code, status
	 *         message, and a list of departments in the "Results" field, containing
	 *         details such as DepartmentId and DepartmentName.
	 */
	public CustomResponse getAllDepartments(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send GET request and extract response
		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Extracting DepartmentId and DepartmentName
		List<Map<String, Object>> results = response.jsonPath().getList("Results");
		List<Object> departmentIds = results.stream().map(department -> department.get("DepartmentId"))
				.collect(Collectors.toList());
		List<Object> departmentNames = results.stream().map(department -> department.get("DepartmentName"))
				.collect(Collectors.toList());

		// Return custom response with extracted fields
		return new CustomResponse(response, statusCode, status, departmentIds, departmentNames);
	}

	/**
	 * @Test2 This method retrieves and verifies the list of items.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - The API response includes HTTP status code, status
	 *         message, and a list of items in the "Results" field, containing
	 *         details such as ItemId and ItemName.
	 */
	public CustomResponse getAllItems(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send GET request and extract response
		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Extracting ItemId and ItemName
		List<Map<String, Object>> results = response.jsonPath().getList("Results");
		List<Object> itemIds = results.stream().map(item -> item.get("ItemId")).collect(Collectors.toList());
		List<Object> itemNames = results.stream().map(item -> item.get("ItemName")).collect(Collectors.toList());

		// Return custom response with extracted fields
		return new CustomResponse(response, statusCode, status, itemIds, itemNames);
	}

	/**
	 * @Test3 This method retrieves and verifies the incentive summary report.
	 * 
	 * @param URL  - The API endpoint to which the GET request is sent.
	 * @param body - Optional
	 * @return CustomResponse - The API response includes HTTP status code, status
	 *         message, and a list of incentive summary details in the "JsonData"
	 *         field, containing details such as PrescriberName, PrescriberId,
	 *         DocTotalAmount, TDSAmount, and NetPayableAmount.
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	public CustomResponse getIncentiveSummaryReport(String URL, Object body)
			throws JsonMappingException, JsonProcessingException {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		System.out.println(URL);
		Response response = request.get(URL).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		System.out.println(response.prettyPrint());
		String status = response.jsonPath().getString("Status");

		// Extracting JsonData as a string
		String jsonDataString = response.jsonPath().getString("Results.JsonData");

		// Deserialize the stringified JSON into a List of Maps
		List<Map<String, Object>> incentiveResults = new ObjectMapper().readValue(jsonDataString,
				new TypeReference<List<Map<String, Object>>>() {
				});

		// Extract the required fields
		List<Object> prescriberIds = incentiveResults.stream().map(result -> result.get("PrescriberId"))
				.collect(Collectors.toList());
		List<Object> prescriberNames = incentiveResults.stream().map(result -> result.get("PrescriberName"))
				.collect(Collectors.toList());
		List<Object> docTotalAmounts = incentiveResults.stream().map(result -> result.get("DocTotalAmount"))
				.collect(Collectors.toList());
		List<Object> tdsAmounts = incentiveResults.stream().map(result -> result.get("TDSAmount"))
				.collect(Collectors.toList());
		List<Object> netPayableAmounts = incentiveResults.stream().map(result -> result.get("NetPayableAmount"))
				.collect(Collectors.toList());

		// Return custom response with extracted fields
		return new CustomResponse(response, statusCode, status, prescriberIds, prescriberNames, docTotalAmounts,
				tdsAmounts, netPayableAmounts);
	}

	/**
	 * @Test4 This method retrieves and verifies the incentive referral summary
	 *        report.
	 * 
	 * @param URL  - The API endpoint to which the GET request is sent.
	 * @param body - Optional
	 * @return CustomResponse - The API response includes HTTP status code, status
	 *         message, and a list of incentive referral summary details in the
	 *         "JsonData" field, containing details such as PrescriberName,
	 *         PrescriberId, DocTotalAmount, TDSAmount, and NetPayableAmount.
	 */
	public CustomResponse getIncReffSummReport(String URL, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send the GET request and extract the response
		Response response = request.get(URL).then().extract().response();
		System.out.println(response.prettyPrint());

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Get the JsonData as a String, which contains a JSON array
		String jsonDataString = response.jsonPath().getString("Results.JsonData");

		// Initialize variables to hold the individual field values
		List<Object> prescriberNames = new ArrayList<>();
		List<Object> prescriberIds = new ArrayList<>();
		List<Object> docTotalAmounts = new ArrayList<>();
		List<Object> tdsAmounts = new ArrayList<>();
		List<Object> netPayableAmounts = new ArrayList<>();

		// Parse the JsonData string into a List<Map<String, Object>> if it's not null
		if (jsonDataString != null && !jsonDataString.isEmpty()) {
			try {
				// Using ObjectMapper to parse the JSON string into a List of Maps
				ObjectMapper objectMapper = new ObjectMapper();
				List<Map<String, Object>> results = objectMapper.readValue(jsonDataString,
						new TypeReference<List<Map<String, Object>>>() {
						});

				// Extract the required fields into individual lists
				for (Map<String, Object> result : results) {
					prescriberNames.add((String) result.get("PrescriberName"));
					prescriberIds.add((Integer) result.get("PrescriberId"));
					docTotalAmounts.add((Double) result.get("DocTotalAmount"));
					tdsAmounts.add((Double) result.get("TDSAmount"));
					netPayableAmounts.add((Double) result.get("NetPayableAmount"));
				}
			} catch (Exception e) {
				// Handle parsing error
				System.out.println("Error parsing JsonData: " + e.getMessage());
			}
		}

		// Return a CustomResponse object with the individual fields
		return new CustomResponse(response, statusCode, status, prescriberNames, prescriberIds, docTotalAmounts,
				tdsAmounts, netPayableAmounts);
	}

	/**
	 * @Test5 This method fetches the hospital income incentive report for a
	 *        specified date range and service department from the API. It validates
	 *        the response status code, checks the "Status" field, and extracts
	 *        fields such as ServiceDepartmentId, ServiceDepartmentName, NetSales,
	 *        ReferralCommission, GrossIncome, OtherIncentive, and HospitalNetIncome
	 *        from the "Results" array.
	 *
	 * @param endpoint - The API endpoint for fetching the hospital income incentive
	 *                 report.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and details such as ServiceDepartmentId,
	 *         ServiceDepartmentName, NetSales, ReferralCommission, GrossIncome,
	 *         OtherIncentive, and HospitalNetIncome in the "Results" field.
	 */
	public CustomResponse getHospIncIncReport(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send the GET request and extract the response
		Response response = request.get(endpoint).then().extract().response();
		System.out.println(response.prettyPrint());

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Get the Results as a list of maps
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Initialize lists to hold the individual fields
		List<Object> serviceDepartmentIds = new ArrayList<>();
		List<Object> serviceDepartmentNames = new ArrayList<>();
		List<Object> netSales = new ArrayList<>();
		List<Object> referralCommissions = new ArrayList<>();
		List<Object> grossIncomes = new ArrayList<>();
		List<Object> otherIncentives = new ArrayList<>();
		List<Object> hospitalNetIncomes = new ArrayList<>();

		// Extract each record's fields from the results
		if (results != null && !results.isEmpty()) {
			for (Map<String, Object> result : results) {
				serviceDepartmentIds.add(String.valueOf(result.get("ServiceDepartmentId")));
				serviceDepartmentNames.add(String.valueOf(result.get("ServiceDepartmentName")));
				netSales.add(String.valueOf(result.get("NetSales")));
				referralCommissions.add(String.valueOf(result.get("ReferralCommission")));
				grossIncomes.add(String.valueOf(result.get("GrossIncome")));
				otherIncentives.add(String.valueOf(result.get("OtherIncentive")));
				hospitalNetIncomes.add(String.valueOf(result.get("HospitalNetIncome")));
			}
		}

		// Return a CustomResponse object with the individual fields
		return new CustomResponse(response, statusCode, status, serviceDepartmentIds, serviceDepartmentNames, netSales,
				referralCommissions, grossIncomes, otherIncentives, hospitalNetIncomes);
	}

	/**
	 * @Test6 This method fetches employee bill items for a specific employee from
	 *        the API. It validates the response status, checks the "Status" field,
	 *        and extracts fields like EmployeeIncentiveInfoId, EmployeeId,
	 *        FullName, TDSPercent, EmpTDSPercent, IsActive, and
	 *        EmployeeBillItemsMap for validation.
	 *
	 * @param endpoint - The API endpoint for fetching employee bill items.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and details such as EmployeeIncentiveInfoId,
	 *         EmployeeId, FullName, TDSPercent, EmpTDSPercent, IsActive, and
	 *         EmployeeBillItemsMap in the "Results" field.
	 */
	public CustomResponse getEmpBillItem(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();
		System.out.println(response.prettyPrint());

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		Map<String, Object> results = response.jsonPath().getMap("Results");

		// Extracting the relevant fields from the "Results"
		Object employeeIncentiveInfoId = String.valueOf(results.get("EmployeeIncentiveInfoId"));
		Object employeeId = String.valueOf(results.get("EmployeeId"));
		Object fullName = String.valueOf(results.get("FullName"));
		Object tdsPercent = String.valueOf(results.get("TDSPercent"));
		Object empTdsPercent = String.valueOf(results.get("EmpTDSPercent"));
		Object isActive = String.valueOf(results.get("IsActive"));
		List<Map<String, Object>> employeeBillItemsMap = (List<Map<String, Object>>) results
				.get("EmployeeBillItemsMap");

		// Return a CustomResponse object with extracted fields
		return new CustomResponse(response, statusCode, status, employeeIncentiveInfoId, employeeId, fullName,
				tdsPercent, empTdsPercent, isActive, employeeBillItemsMap);
	}

	/**
	 * @Test7 This method fetches inventory fiscal years from the API and validates
	 *        various fields. It checks the fiscal year ID, name, start date, end
	 *        date, and active status for each fiscal year.
	 *
	 * @param endpoint - The API endpoint for retrieving inventory fiscal years.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and details such as FiscalYearId, FiscalYearName,
	 *         StartDate, EndDate, and IsActive in the "Results" field.
	 */
	public CustomResponse getInvntryFiscalYrs(String endpoint, Object body) {
		// Set up the request
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send the request and get the response
		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extract required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Fetch results as a List of Maps (representing the fiscal years)
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Extract the necessary fields directly from the "Results" list
		List<Object> fiscalYearIds = results.stream().map(result -> result.get("FiscalYearId"))
				.collect(Collectors.toList());
		List<Object> fiscalYearNames = results.stream().map(result -> result.get("FiscalYearName"))
				.collect(Collectors.toList());
		List<Object> startDates = results.stream().map(result -> result.get("StartDate")).collect(Collectors.toList());
		List<Object> endDates = results.stream().map(result -> result.get("EndDate")).collect(Collectors.toList());
		List<Object> isActiveList = results.stream().map(result -> result.get("IsActive")).collect(Collectors.toList());

		// Return a CustomResponse object with all the necessary details
		return new CustomResponse(response, statusCode, status, fiscalYearIds, fiscalYearNames, startDates, endDates,
				isActiveList);
	}

	/**
	 * @Test8 This method activates inventory by retrieving store information from
	 *        the API. It fetches a list of stores and validates each store's ID,
	 *        name, and description.
	 *
	 * @param endpoint - The API endpoint for activating inventory.
	 * @param body     - Optional request body (null in this case).
	 *
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and a list of stores in the "Results" field.
	 */
	public CustomResponse getActInventory(String endpoint, Object body) {
		// Set up the request
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send the request and get the response
		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Fetch results as a List of Maps (representing the stores)
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Extract the necessary fields directly from the "Results" list
		List<Object> storeIds = results.stream().map(result -> result.get("StoreId")).collect(Collectors.toList());
		List<Object> names = results.stream().map(result -> result.get("Name")).collect(Collectors.toList());
		List<Object> storeDescriptions = results.stream().map(result -> result.get("StoreDescription"))
				.collect(Collectors.toList());

		// Return a CustomResponse object with the extracted fields
		return new CustomResponse(response, statusCode, status, storeIds, names, storeDescriptions);
	}

}