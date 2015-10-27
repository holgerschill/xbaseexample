package org.eclipse.eXXXtreme.tutorial

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.List

class DBAccess {

	def static <E> List<E> loadTable(Connection conn, Class<? extends ITable> clazz) {
		val stmt = conn.createStatement()
		val resultSet = stmt.executeQuery("SELECT * FROM PUBLIC." + clazz.simpleName.toUpperCase)
		val List<E> result = newArrayList
		while(resultSet.next){
			result += <E>createClassInstance(conn,resultSet,clazz)
		}
		result
	}
	
	def static <E> E  createClassInstance(Connection conn, Class<?> clazz, String PK_Column_Name, Object keyValue){
		val stmt = conn.createStatement()
		val resultSet = stmt.executeQuery("SELECT * FROM PUBLIC." + clazz.simpleName.toUpperCase + " WHERE " + PK_Column_Name + " = " + keyValue)
		resultSet.next
		createClassInstance(conn,resultSet,clazz)
	}
	
	def static <E> E  createClassInstance(Connection conn,ResultSet resultSet,  Class<?> clazz){
		val metaData = conn.metaData
		val foreignKeys = metaData.getImportedKeys(null, null, clazz.simpleName.toUpperCase)
		val fkMap = <String, String>newHashMap()
		while (foreignKeys.next) {
			val COLUMN_NAME = foreignKeys.getString("FKCOLUMN_NAME").toLowerCase.toLowerCase
			val PK_COLUMN = foreignKeys.getString("PKCOLUMN_NAME").toLowerCase.toUpperCase
			fkMap.put(COLUMN_NAME, PK_COLUMN)
		}
		clazz.newInstance as E =>
			[
				val declaredFields = clazz.declaredFields
				for (f : declaredFields) {
					f.accessible = true
					val value = resultSet.getObject(f.name.toUpperCase)
					val pk_column = fkMap.get(f.name.toLowerCase)
					if (pk_column == null) {
						f.set(it, value)
					} else {
						f.set(it,createClassInstance(conn,f.type,pk_column, value))
					}

				}
			]
		
	}

	def static Connection getConnection(String dbPath) {
		DriverManager.getConnection("jdbc:h2:" + dbPath, "sa", "")
	}

}

interface ITable {
}