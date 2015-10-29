package org.eclipse.eXXXtreme.jvmmodel;

import com.google.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.eclipse.eXXXtreme.h2.ColumnInfo;
import org.eclipse.eXXXtreme.h2.H2MetaDataAccess;
import org.eclipse.eXXXtreme.h2.TableInfo;
import org.eclipse.eXXXtreme.tutorial.Model;
import org.eclipse.eXXXtreme.tutorial.Query;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenationClient;
import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmMember;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer;
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * <p>Infers a JVM model from the source model.</p>
 * 
 * <p>The JVM model should contain all elements that would appear in the Java code
 * which is generated from the source model. Other models link against the JVM model rather than the source model.</p>
 */
@SuppressWarnings("all")
public class TutorialJvmModelInferrer extends AbstractModelInferrer {
  /**
   * convenience API to build and initialize JVM types and their members.
   */
  @Inject
  @Extension
  private JvmTypesBuilder _jvmTypesBuilder;
  
  @Inject
  @Extension
  private H2MetaDataAccess _h2MetaDataAccess;
  
  protected void _infer(final Model model, final IJvmDeclaredTypeAcceptor acceptor, final boolean isPreIndexingPhase) {
    final String path = this._h2MetaDataAccess.getProjectPath(model);
    String _name = model.getName();
    JvmGenericType _class = this._jvmTypesBuilder.toClass(model, _name);
    final Procedure1<JvmGenericType> _function = (JvmGenericType it) -> {
      EList<JvmTypeReference> _superTypes = it.getSuperTypes();
      JvmTypeReference _typeRef = this._typeReferenceBuilder.typeRef("org.eclipse.eXXXtreme.tutorial.DBAccess");
      this._jvmTypesBuilder.<JvmTypeReference>operator_add(_superTypes, _typeRef);
      EList<JvmMember> _members = it.getMembers();
      JvmTypeReference _typeRef_1 = this._typeReferenceBuilder.typeRef(Connection.class);
      final Procedure1<JvmField> _function_1 = (JvmField it_1) -> {
        it_1.setStatic(true);
        StringConcatenationClient _client = new StringConcatenationClient() {
          @Override
          protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
            _builder.append("getConnection(\"");
            String _h2Path = model.getH2Path();
            String _plus = ((path + "/") + _h2Path);
            _builder.append(_plus, "");
            _builder.append("\")");
          }
        };
        this._jvmTypesBuilder.setInitializer(it_1, _client);
      };
      JvmField _field = this._jvmTypesBuilder.toField(model, "conn", _typeRef_1, _function_1);
      this._jvmTypesBuilder.<JvmField>operator_add(_members, _field);
      EList<JvmMember> _members_1 = it.getMembers();
      JvmTypeReference _typeRef_2 = this._typeReferenceBuilder.typeRef(void.class);
      final Procedure1<JvmOperation> _function_2 = (JvmOperation it_1) -> {
        EList<JvmFormalParameter> _parameters = it_1.getParameters();
        JvmTypeReference _typeRef_3 = this._typeReferenceBuilder.typeRef(String.class);
        JvmTypeReference _addArrayTypeDimension = this._jvmTypesBuilder.addArrayTypeDimension(_typeRef_3);
        JvmFormalParameter _parameter = this._jvmTypesBuilder.toParameter(model, "args", _addArrayTypeDimension);
        this._jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters, _parameter);
        it_1.setStatic(true);
        it_1.setVarArgs(true);
        EList<JvmTypeReference> _exceptions = it_1.getExceptions();
        JvmTypeReference _typeRef_4 = this._typeReferenceBuilder.typeRef(SQLException.class);
        this._jvmTypesBuilder.<JvmTypeReference>operator_add(_exceptions, _typeRef_4);
        StringConcatenationClient _client = new StringConcatenationClient() {
          @Override
          protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
            _builder.append("try{");
            _builder.newLine();
            {
              EList<Query> _queries = model.getQueries();
              for(final Query query : _queries) {
                _builder.append("\t");
                String _name = model.getName();
                _builder.append(_name, "\t");
                _builder.append(".");
                String _name_1 = query.getName();
                _builder.append(_name_1, "\t");
                _builder.append("(");
                String _name_2 = model.getName();
                _builder.append(_name_2, "\t");
                _builder.append("._");
                String _name_3 = query.getName();
                _builder.append(_name_3, "\t");
                _builder.append("TableContent);");
                _builder.newLineIfNotEmpty();
              }
            }
            _builder.append("} catch (Exception e) {");
            _builder.newLine();
            _builder.append("\t");
            _builder.append("e.printStackTrace(); // superior exception handling");
            _builder.newLine();
            _builder.append("} finally {");
            _builder.newLine();
            _builder.append("\t");
            String _name_4 = model.getName();
            _builder.append(_name_4, "\t");
            _builder.append(".conn.close();");
            _builder.newLineIfNotEmpty();
            _builder.append("}");
            _builder.newLine();
          }
        };
        this._jvmTypesBuilder.setBody(it_1, _client);
      };
      JvmOperation _method = this._jvmTypesBuilder.toMethod(model, "main", _typeRef_2, _function_2);
      this._jvmTypesBuilder.<JvmOperation>operator_add(_members_1, _method);
      EList<Query> _queries = model.getQueries();
      for (final Query query : _queries) {
        {
          JvmTypeReference _table = query.getTable();
          final JvmTypeReference tableType = this._typeReferenceBuilder.typeRef(List.class, _table);
          EList<JvmMember> _members_2 = it.getMembers();
          String _name_1 = query.getName();
          String _plus = ("_" + _name_1);
          String _plus_1 = (_plus + "TableContent");
          final Procedure1<JvmField> _function_3 = (JvmField it_1) -> {
            it_1.setStatic(true);
            StringConcatenationClient _client = new StringConcatenationClient() {
              @Override
              protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
                _builder.append("loadTable(conn, ");
                JvmTypeReference _table = query.getTable();
                _builder.append(_table, "");
                _builder.append(".class)");
              }
            };
            this._jvmTypesBuilder.setInitializer(it_1, _client);
          };
          JvmField _field_1 = this._jvmTypesBuilder.toField(query, _plus_1, tableType, _function_3);
          this._jvmTypesBuilder.<JvmField>operator_add(_members_2, _field_1);
          EList<JvmMember> _members_3 = it.getMembers();
          String _name_2 = query.getName();
          JvmTypeReference _inferredType = this._jvmTypesBuilder.inferredType();
          final Procedure1<JvmOperation> _function_4 = (JvmOperation it_1) -> {
            it_1.setStatic(true);
            EList<JvmFormalParameter> _parameters = it_1.getParameters();
            JvmTypeReference _table_1 = query.getTable();
            JvmTypeReference _typeRef_3 = this._typeReferenceBuilder.typeRef(List.class, _table_1);
            JvmFormalParameter _parameter = this._jvmTypesBuilder.toParameter(query, "it", _typeRef_3);
            this._jvmTypesBuilder.<JvmFormalParameter>operator_add(_parameters, _parameter);
            XExpression _expression = query.getExpression();
            this._jvmTypesBuilder.setBody(it_1, _expression);
          };
          JvmOperation _method_1 = this._jvmTypesBuilder.toMethod(query, _name_2, _inferredType, _function_4);
          this._jvmTypesBuilder.<JvmOperation>operator_add(_members_3, _method_1);
          XExpression _expression = query.getExpression();
          final JvmTypeReference returnType = this._jvmTypesBuilder.inferredType(_expression);
          EList<JvmMember> _members_4 = it.getMembers();
          String _name_3 = query.getName();
          final Procedure1<JvmOperation> _function_5 = (JvmOperation it_1) -> {
            it_1.setStatic(true);
            StringConcatenationClient _client = new StringConcatenationClient() {
              @Override
              protected void appendTo(StringConcatenationClient.TargetStringConcatenation _builder) {
                _builder.append("return ");
                String _name = model.getName();
                _builder.append(_name, "");
                _builder.append(".");
                String _name_1 = query.getName();
                _builder.append(_name_1, "");
                _builder.append("(");
                String _name_2 = model.getName();
                _builder.append(_name_2, "");
                _builder.append("._");
                String _name_3 = query.getName();
                _builder.append(_name_3, "");
                _builder.append("TableContent);");
              }
            };
            this._jvmTypesBuilder.setBody(it_1, _client);
          };
          JvmOperation _method_2 = this._jvmTypesBuilder.toMethod(query, _name_3, returnType, _function_5);
          this._jvmTypesBuilder.<JvmOperation>operator_add(_members_4, _method_2);
        }
      }
    };
    acceptor.<JvmGenericType>accept(_class, _function);
    String _h2Path = model.getH2Path();
    String _plus = ((path + "/") + _h2Path);
    List<TableInfo> _tableInfos = this._h2MetaDataAccess.getTableInfos(_plus);
    for (final TableInfo tableInfo : _tableInfos) {
      String _name_1 = tableInfo.getName();
      JvmGenericType _class_1 = this._jvmTypesBuilder.toClass(model, _name_1);
      final Procedure1<JvmGenericType> _function_1 = (JvmGenericType it) -> {
        EList<JvmTypeReference> _superTypes = it.getSuperTypes();
        JvmTypeReference _typeRef = this._typeReferenceBuilder.typeRef("org.eclipse.eXXXtreme.tutorial.ITable");
        this._jvmTypesBuilder.<JvmTypeReference>operator_add(_superTypes, _typeRef);
        List<ColumnInfo> _columns = tableInfo.getColumns();
        for (final ColumnInfo column : _columns) {
          {
            String _name_2 = column.getName();
            final String columnName = _name_2.toLowerCase();
            EList<JvmMember> _members = it.getMembers();
            String _typeName = column.getTypeName();
            JvmTypeReference _typeRef_1 = this._typeReferenceBuilder.typeRef(_typeName);
            JvmField _field = this._jvmTypesBuilder.toField(model, columnName, _typeRef_1);
            this._jvmTypesBuilder.<JvmField>operator_add(_members, _field);
            EList<JvmMember> _members_1 = it.getMembers();
            String _typeName_1 = column.getTypeName();
            JvmTypeReference _typeRef_2 = this._typeReferenceBuilder.typeRef(_typeName_1);
            JvmOperation _getter = this._jvmTypesBuilder.toGetter(model, columnName, _typeRef_2);
            this._jvmTypesBuilder.<JvmOperation>operator_add(_members_1, _getter);
            EList<JvmMember> _members_2 = it.getMembers();
            String _typeName_2 = column.getTypeName();
            JvmTypeReference _typeRef_3 = this._typeReferenceBuilder.typeRef(_typeName_2);
            JvmOperation _setter = this._jvmTypesBuilder.toSetter(model, columnName, _typeRef_3);
            this._jvmTypesBuilder.<JvmOperation>operator_add(_members_2, _setter);
          }
        }
      };
      acceptor.<JvmGenericType>accept(_class_1, _function_1);
    }
  }
  
  public void infer(final EObject model, final IJvmDeclaredTypeAcceptor acceptor, final boolean isPreIndexingPhase) {
    if (model instanceof Model) {
      _infer((Model)model, acceptor, isPreIndexingPhase);
      return;
    } else if (model != null) {
      _infer(model, acceptor, isPreIndexingPhase);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(model, acceptor, isPreIndexingPhase).toString());
    }
  }
}
