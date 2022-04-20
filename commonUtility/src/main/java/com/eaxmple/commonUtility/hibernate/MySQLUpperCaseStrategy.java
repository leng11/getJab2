package com.eaxmple.commonUtility.hibernate;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class MySQLUpperCaseStrategy extends PhysicalNamingStrategyStandardImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String CAMEL_CASE_REGEX = "([a-z]+)([A-Z]+)";
	private static final String SNAKE_CASE_PATTERN = "$1\\_$2";

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment ccontext) {
		String tableName = name.getText().toUpperCase();
		return Identifier.toIdentifier(tableName);
	}
	
	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
		return formatIdentifier(super.toPhysicalColumnName(name, context));
	}
	
	private Identifier formatIdentifier(Identifier identifier) {
		if(null != identifier) {
			String name = identifier.getText();
			
			String formattedName = name.replaceAll(CAMEL_CASE_REGEX,
													SNAKE_CASE_PATTERN).toLowerCase();
			return !formattedName.equals(name) ?
					Identifier.toIdentifier(formattedName, identifier.isQuoted()) :
						identifier;
		} else {
			return null;
		}
	}
}
