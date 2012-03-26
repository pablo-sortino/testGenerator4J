package org.jtestcase.core.asserter;

public class AssertFailureReason {

	public boolean isNotNull() {
		return isNotNull;
	}

	public void setNotNull(boolean isNotNull) {
		this.isNotNull = isNotNull;
	}

	public boolean isNotNullOnNullObject() {
		return isNotNullOnNullObject;
	}

	public void setNotNullOnNullObject(boolean isNotNullOnNullObject) {
		this.isNotNullOnNullObject = isNotNullOnNullObject;
	}

	public boolean isNullOnNonNullObject() {
		return isNullOnNonNullObject;
	}

	public void setNullOnNonNullObject(boolean isNullOnNonNullObject) {
		this.isNullOnNonNullObject = isNullOnNonNullObject;
	}

	public String getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}

	public String getTestValue() {
		return testValue;
	}

	public void setTestValue(String testValue) {
		this.testValue = testValue;
	}

	public boolean isAssertFailed() {
		return isAssertFailed;
	}

	public void setAssertFailed(boolean isAssertFailed) {
		this.isAssertFailed = isAssertFailed;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public String getTestCaseInstance() {
		return testCaseInstance;
	}

	public void setTestCaseInstance(String testCaseInstance) {
		this.testCaseInstance = testCaseInstance;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public AssertFailureReason() {
		super();
	}

	public String testCaseInstance = "";

	public String variable = "";

	public String testValue = "";

	public String expectedValue = "";

	public String criteria = "";

	public boolean isAssertFailed = false;

	public boolean isNotNullOnNullObject = false;

	public boolean isNullOnNonNullObject = false;
	
	public boolean isNotNull = false;
	
	public boolean noMatch = false;


	public boolean isNoMatch() {
		return noMatch;
	}

	public void setNoMatch(boolean noMatch) {
		this.noMatch = noMatch;
	}

	public String toString() {
		
		if(noMatch)
			return " no match ";

		if (isNotNull) 
			return " variable is not null";
		
		if (isNullOnNonNullObject)
			return "[" + " In test case : "
					+ " trying not to assert ISNULL for a null variable ]";
		if (isNullOnNonNullObject)
			return "[" + " In test case : "
					+ " trying to assert ISNULL for a not null variable ]";

		return "variable : " + variable + " criteria : " + criteria
				+ " testValue : " + testValue + " expectedValue : "
				+ expectedValue;
	}

}
