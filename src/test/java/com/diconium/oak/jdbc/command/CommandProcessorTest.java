package com.diconium.oak.jdbc.command;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CommandProcessorTest {

	@Test
	public void getContainerNameFromSqlQueryWithQueryEndingWithSemicolonReturnsCorrectName() throws Exception {
		String query = "select * from expected;";

		assertEquals("expected", new CommandProcessor().getContainerNameFromSqlQuery(query));
	}
	
	@Test
	public void getContainerNameFromSqlQueryWithQueryEndingWithoutSemicolonReturnsCorrectName() throws Exception {
		String query = "select * from expected";

		assertEquals("expected", new CommandProcessor().getContainerNameFromSqlQuery(query));
	}
	
	@Test
	public void getContainerNameFromSqlQueryWithQueryContainingMultipleColumnsEndingWithSemicolonReturnsCorrectName() throws Exception {
		String query = "select expectedId,expectedName from expected;";

		assertEquals("expected", new CommandProcessor().getContainerNameFromSqlQuery(query));
	}
	
	@Test
	public void getContainerNameFromSqlQueryWithQueryContainingMultipleColumnsEndingWithoutSemicolonReturnsCorrectName() throws Exception {
		String query = "select expectedId,expectedName from expected";

		assertEquals("expected", new CommandProcessor().getContainerNameFromSqlQuery(query));
	}
	
	@Test
	public void getContainerNameFromSqlQueryWithQueryContainingWhereClauseEndingWithSemicolonReturnsCorrectName() throws Exception {
		String query = "select * from expected where expectedId = '123';";

		assertEquals("expected", new CommandProcessor().getContainerNameFromSqlQuery(query));
	}
	
	@Test
	public void getContainerNameFromSqlQueryWithQueryContainingWhereClauseEndingWithoutSemicolonReturnsCorrectName() throws Exception {
		String query = "select * from expected where expectedId = '123'";

		assertEquals("expected", new CommandProcessor().getContainerNameFromSqlQuery(query));
	}
	
	@Test
	public void getContainerNameFromSqlQueryWithQueryContainingMultipleColumsAndWhereClauseEndingWithSemicolonReturnsCorrectName() throws Exception {
		String query = "select expectedId,expectedName,expectedSalary from expected where expectedId = '123';";

		assertEquals("expected", new CommandProcessor().getContainerNameFromSqlQuery(query));
	}
	
	@Test
	public void getContainerNameFromSqlQueryWithQueryContainingMultipleColumsAndWhereClauseEndingWithoutSemicolonReturnsCorrectName() throws Exception {
		String query = "select expectedId,expectedName,expectedSalary from expected where expectedId = '123'";

		assertEquals("expected", new CommandProcessor().getContainerNameFromSqlQuery(query));
	}

}
