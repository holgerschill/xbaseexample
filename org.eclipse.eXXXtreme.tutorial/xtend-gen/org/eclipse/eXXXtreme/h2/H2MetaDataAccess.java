package org.eclipse.eXXXtreme.h2;

import com.google.common.base.Objects;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.eXXXtreme.h2.ColumnInfo;
import org.eclipse.eXXXtreme.h2.TableInfo;
import org.eclipse.eXXXtreme.tutorial.Model;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class H2MetaDataAccess {
  public List<TableInfo> getTableInfos(final String dbPath) {
    try {
      List<TableInfo> _xblockexpression = null;
      {
        Class.forName("org.h2.Driver");
        final Connection conn = DriverManager.getConnection(("jdbc:h2:" + dbPath), "sa", "");
        final DatabaseMetaData m = conn.getMetaData();
        final ResultSet rs = m.getTables(null, "PUBLIC", "%", null);
        final List<TableInfo> tables = CollectionLiterals.<TableInfo>newArrayList();
        while (rs.next()) {
          {
            final String tableName = rs.getString("TABLE_NAME");
            TableInfo _tableInfo = new TableInfo();
            final Procedure1<TableInfo> _function = (TableInfo it) -> {
              try {
                String _lowerCase = tableName.toLowerCase();
                String _firstUpper = StringExtensions.toFirstUpper(_lowerCase);
                it.setName(_firstUpper);
                ArrayList<ColumnInfo> _newArrayList = CollectionLiterals.<ColumnInfo>newArrayList();
                it.setColumns(_newArrayList);
                final DatabaseMetaData metadata = conn.getMetaData();
                final ResultSet foreignKeys = metadata.getImportedKeys(null, null, tableName);
                final HashSet<String> foreignTable = CollectionLiterals.<String>newHashSet();
                while (foreignKeys.next()) {
                  List<ColumnInfo> _columns = it.getColumns();
                  ColumnInfo _columnInfo = new ColumnInfo();
                  final Procedure1<ColumnInfo> _function_1 = (ColumnInfo it_1) -> {
                    try {
                      String _string = foreignKeys.getString("FKCOLUMN_NAME");
                      String _lowerCase_1 = _string.toLowerCase();
                      final String fielName = StringExtensions.toFirstUpper(_lowerCase_1);
                      it_1.setName(fielName);
                      foreignTable.add(fielName);
                      String _string_1 = foreignKeys.getString("PKTABLE_NAME");
                      String _lowerCase_2 = _string_1.toLowerCase();
                      String _firstUpper_1 = StringExtensions.toFirstUpper(_lowerCase_2);
                      it_1.setTypeName(_firstUpper_1);
                    } catch (Throwable _e) {
                      throw Exceptions.sneakyThrow(_e);
                    }
                  };
                  ColumnInfo _doubleArrow = ObjectExtensions.<ColumnInfo>operator_doubleArrow(_columnInfo, _function_1);
                  _columns.add(_doubleArrow);
                }
                final ResultSet resultSet = metadata.getColumns(null, null, tableName, null);
                while (resultSet.next()) {
                  {
                    String _string = resultSet.getString("COLUMN_NAME");
                    String _lowerCase_1 = _string.toLowerCase();
                    final String fieldName = StringExtensions.toFirstUpper(_lowerCase_1);
                    final Function1<String, Boolean> _function_1 = (String it_1) -> {
                      return Boolean.valueOf(Objects.equal(it_1, fieldName));
                    };
                    boolean _exists = IterableExtensions.<String>exists(foreignTable, _function_1);
                    boolean _not = (!_exists);
                    if (_not) {
                      List<ColumnInfo> _columns = it.getColumns();
                      ColumnInfo _columnInfo = new ColumnInfo();
                      final Procedure1<ColumnInfo> _function_2 = (ColumnInfo it_1) -> {
                        try {
                          it_1.setName(fieldName);
                          String _switchResult = null;
                          String _string_1 = resultSet.getString("TYPE_NAME");
                          switch (_string_1) {
                            case "VARCHAR":
                              _switchResult = String.class.getTypeName();
                              break;
                            case "DECIMAL":
                              _switchResult = BigDecimal.class.getTypeName();
                              break;
                            case "DATE":
                              _switchResult = Date.class.getTypeName();
                              break;
                            default:
                              _switchResult = String.class.getTypeName();
                              break;
                          }
                          it_1.setTypeName(_switchResult);
                        } catch (Throwable _e) {
                          throw Exceptions.sneakyThrow(_e);
                        }
                      };
                      ColumnInfo _doubleArrow = ObjectExtensions.<ColumnInfo>operator_doubleArrow(_columnInfo, _function_2);
                      _columns.add(_doubleArrow);
                    }
                  }
                }
              } catch (Throwable _e) {
                throw Exceptions.sneakyThrow(_e);
              }
            };
            TableInfo _doubleArrow = ObjectExtensions.<TableInfo>operator_doubleArrow(_tableInfo, _function);
            tables.add(_doubleArrow);
          }
        }
        conn.close();
        _xblockexpression = tables;
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public String getProjectPath(final Model model) {
    String _xblockexpression = null;
    {
      Resource _eResource = model.eResource();
      ResourceSet _resourceSet = _eResource.getResourceSet();
      final XtextResourceSet xtextResourceSet = ((XtextResourceSet) _resourceSet);
      Object _classpathURIContext = xtextResourceSet.getClasspathURIContext();
      final JavaProject classpathURIContext = ((JavaProject) _classpathURIContext);
      final IJavaProject javaproject = classpathURIContext.<IJavaProject>getAdapter(IJavaProject.class);
      final IProject project = javaproject.getProject();
      IPath _location = project.getLocation();
      _xblockexpression = _location.toPortableString();
    }
    return _xblockexpression;
  }
}
