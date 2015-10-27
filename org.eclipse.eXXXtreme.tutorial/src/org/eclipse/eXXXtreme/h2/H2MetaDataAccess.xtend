package org.eclipse.eXXXtreme.h2

import java.math.BigDecimal
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Date
import java.util.List
import org.eclipse.eXXXtreme.tutorial.Model
import org.eclipse.jdt.core.IJavaProject
import org.eclipse.jdt.internal.core.JavaProject
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtext.resource.XtextResourceSet

class H2MetaDataAccess {

	def getTableInfos(String dbPath) {
		Class.forName("org.h2.Driver");
		val conn = DriverManager.getConnection("jdbc:h2:" + dbPath, "sa", "")
		val m = conn.getMetaData()
		val rs = m.getTables(null, "PUBLIC", "%", null)
		val List<TableInfo> tables = newArrayList()
		while (rs.next()) {
			val tableName = rs.getString("TABLE_NAME")
			tables += new TableInfo => [
				name = tableName.toLowerCase.toFirstUpper
				columns = newArrayList()
				val DatabaseMetaData metadata = conn.getMetaData()
				val foreignKeys = metadata.getImportedKeys(null, null, tableName)
				val foreignTable = newHashSet()
				while (foreignKeys.next) {
					columns += new ColumnInfo =>[
						val fielName = foreignKeys.getString("FKCOLUMN_NAME").toLowerCase.toFirstUpper
						name = fielName
						foreignTable += fielName
						typeName =  foreignKeys.getString("PKTABLE_NAME").toLowerCase.toFirstUpper
					]
				}
				val ResultSet resultSet = metadata.getColumns(null, null, tableName, null)
				while (resultSet.next()) {
					val fieldName = resultSet.getString("COLUMN_NAME").toLowerCase.toFirstUpper
					if(!foreignTable.exists[it == fieldName])
					columns += new ColumnInfo => [
						name = fieldName
						typeName = switch (resultSet.getString("TYPE_NAME")) {
							case "VARCHAR": String.typeName
							case "DECIMAL": BigDecimal.typeName
							case "DATE": Date.typeName
							default: String.typeName
						}

					]
				}
			]
		}
		conn.close();
		tables
	}

	def getProjectPath(Model model) {
		val xtextResourceSet = model.eResource.resourceSet as XtextResourceSet
		val classpathURIContext = xtextResourceSet.classpathURIContext as JavaProject
		val javaproject = classpathURIContext.getAdapter(IJavaProject)
		val project = javaproject.project
		project.location.toPortableString
	}
}

class TableInfo {
	@Accessors
	String name
	@Accessors
	List<ColumnInfo> columns
}

class ColumnInfo {
	@Accessors
	String name
	@Accessors
	String typeName
}