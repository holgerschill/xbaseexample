package org.eclipse.eXXXtreme.jvmmodel

import com.google.inject.Inject
import java.sql.Connection
import java.util.List
import org.eclipse.eXXXtreme.h2.StaticH2Access
import org.eclipse.eXXXtreme.tutorial.Model
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor.IPostIndexingInitializing
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder
import java.sql.SQLException

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
	
	@Inject extension StaticH2Access

	/**
	 * The dispatch method {@code infer} is called for each instance of the
	 * given element's type that is contained in a resource.
	 * 
	 * @param element
	 *            the model to create one or more
	 *            {@link JvmDeclaredType declared
	 *            types} from.
	 * @param acceptor
	 *            each created
	 *            {@link JvmDeclaredType type}
	 *            without a container should be passed to the acceptor in order
	 *            get attached to the current resource. The acceptor's
	 *            {@link IJvmDeclaredTypeAcceptor#accept(org.eclipse.xtext.common.types.JvmDeclaredType)
	 *            accept(..)} method takes the constructed empty type for the
	 *            pre-indexing phase. This one is further initialized in the
	 *            indexing phase using the closure you pass to the returned
	 *            {@link IPostIndexingInitializing#initializeLater(org.eclipse.xtext.xbase.lib.Procedures.Procedure1)
	 *            initializeLater(..)}.
	 * @param isPreIndexingPhase
	 *            whether the method is called in a pre-indexing phase, i.e.
	 *            when the global index is not yet fully updated. You must not
	 *            rely on linking using the index if isPreIndexingPhase is
	 *            <code>true</code>.
	 */
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
   				// TODO Try catch finally close
   				static = true
   				exceptions += typeRef(SQLException)
   				body = 
   				'''
				try{
					«FOR query : model.queries»
						«model.name».«query.name»(«model.name»._«query.name»);
					«ENDFOR»
				} catch (Exception e) {}
				finally {
					«model.name».conn.close();
				}
   				'''
   			]
			for(query : model.queries) {
				val tableType = typeRef(List,query.table)
				members+= query.toField("_" + query.name, tableType)[
					static = true
					initializer = '''loadTable(conn, «query.table».class)'''
				]
				members += query.toMethod(query.name, inferredType)[
					static = true
					parameters += query.toParameter("it",typeRef(List,query.table.cloneWithProxies))
					body = query.expression
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

