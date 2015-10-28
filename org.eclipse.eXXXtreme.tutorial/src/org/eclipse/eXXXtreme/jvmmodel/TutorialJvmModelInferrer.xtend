package org.eclipse.eXXXtreme.jvmmodel

import com.google.inject.Inject
import java.sql.Connection
import java.sql.SQLException
import java.util.List
import org.eclipse.eXXXtreme.h2.H2MetaDataAccess
import org.eclipse.eXXXtreme.tutorial.Model
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder

/**
 * <p>Infers a JVM model from the source model.</p> 
 *
 * <p>The JVM model should contain all elements that would appear in the Java code 
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>     
 */
class TutorialJvmModelInferrer extends AbstractModelInferrer {

    /**
     * convenience API to build and initialize JVM types and their members.
     */
	@Inject extension JvmTypesBuilder
	
	@Inject extension H2MetaDataAccess


   	def dispatch void infer(Model model, IJvmDeclaredTypeAcceptor acceptor, boolean isPreIndexingPhase) {
   		val path = getProjectPath(model)
   		acceptor.accept(model.toClass(model.name)) [
   			superTypes += typeRef("org.eclipse.eXXXtreme.tutorial.DBAccess")
   			members += model.toField("conn", typeRef(Connection))[
   				static = true
   				initializer = '''getConnection("«path + "/" + model.h2Path»")'''
   			]
   			members += model.toMethod("main",typeRef(void))[
   				parameters += model.toParameter("args",typeRef(String).addArrayTypeDimension)
   				static = true
   				varArgs = true
   				exceptions += typeRef(SQLException)
   				body = 
   				'''
				try{
					«FOR query : model.queries»
						«model.name».«query.name»(«model.name»._«query.name»TableContent);
					«ENDFOR»
				} catch (Exception e) {}
				finally {
					«model.name».conn.close();
				}
   				'''
   			]
			for(query : model.queries) {
				val tableType = typeRef(List,query.table)
				members+= query.toField("_" + query.name+ "TableContent", tableType)[
					static = true
					initializer = '''loadTable(conn, «query.table».class)'''
				]
				members += query.toMethod(query.name, inferredType)[
					static = true
					parameters += query.toParameter("it",typeRef(List,query.table.cloneWithProxies))
					body = query.expression
				]
				val returnType = inferredType(query.expression)
				members+= query.toMethod(query.name, returnType) [
					static = true
					body = '''return «model.name».«query.name»(«model.name»._«query.name»TableContent);'''
				]
				
			}
   		]
		
   		for(tableInfo : getTableInfos(path + "/" + model.h2Path)){
   			acceptor.accept(model.toClass(tableInfo.name))[
   				superTypes += typeRef("org.eclipse.eXXXtreme.tutorial.ITable")
   				for(column : tableInfo.columns){
   					members += model.toField(column.name,typeRef(column.typeName))
   					members += model.toGetter(column.name,typeRef(column.typeName))
   					members += model.toSetter(column.name,typeRef(column.typeName))
   				}
   			]
   		}
   	}
	
	

	
}

