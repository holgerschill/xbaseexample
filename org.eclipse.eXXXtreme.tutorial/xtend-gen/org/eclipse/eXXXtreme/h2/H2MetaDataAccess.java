package org.eclipse.eXXXtreme.h2;

import com.google.common.base.CaseFormat;
import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.eXXXtreme.h2.ColumnInfo;
import org.eclipse.eXXXtreme.h2.TableInfo;
import org.eclipse.eXXXtreme.tutorial.Model;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.h2.Driver;

@SuppressWarnings("all")
public class H2MetaDataAccess {
  /**
   * Read the table information from the database at the given absolute location
   */
  public List<TableInfo> getTableInfos(final String dbPath) {
    try {
      List<TableInfo> _xblockexpression = null;
      {
        File _file = new File((dbPath + ".mv.db"));
        boolean _exists = _file.exists();
        boolean _not = (!_exists);
        if (_not) {
          return CollectionLiterals.<TableInfo>emptyList();
        }
        Driver.class.getSimpleName();
        final List<TableInfo> tables = CollectionLiterals.<TableInfo>newArrayList();
        final Connection conn = DriverManager.getConnection(("jdbc:h2:" + dbPath), "sa", "");
        try {
          final DatabaseMetaData metadata = conn.getMetaData();
          final ResultSet rs = metadata.getTables(null, "PUBLIC", "%", null);
          while (rs.next()) {
            {
              final String tableName = rs.getString("TABLE_NAME");
              TableInfo _tableInfo = new TableInfo();
              final Procedure1<TableInfo> _function = (TableInfo it) -> {
                try {
                  String _camelCase = this.toCamelCase(tableName);
                  it.setName(_camelCase);
                  ArrayList<ColumnInfo> _newArrayList = CollectionLiterals.<ColumnInfo>newArrayList();
                  it.setColumns(_newArrayList);
                  final ResultSet foreignKeys = metadata.getImportedKeys(null, null, tableName);
                  final HashMap<String, String> fkToTypeName = CollectionLiterals.<String, String>newHashMap();
                  while (foreignKeys.next()) {
                    {
                      String _string = foreignKeys.getString("FKCOLUMN_NAME");
                      final String fieldName = this.toCamelCase(_string);
                      String _string_1 = foreignKeys.getString("PKTABLE_NAME");
                      final String typeName = this.toCamelCase(_string_1);
                      fkToTypeName.put(fieldName, typeName);
                    }
                  }
                  final ResultSet resultSet = metadata.getColumns(null, null, tableName, null);
                  while (resultSet.next()) {
                    {
                      String _string = resultSet.getString("COLUMN_NAME");
                      final String fieldName = this.toCamelCase(_string);
                      List<ColumnInfo> _columns = it.getColumns();
                      ColumnInfo _columnInfo = new ColumnInfo();
                      final Procedure1<ColumnInfo> _function_1 = (ColumnInfo it_1) -> {
                        try {
                          it_1.setName(fieldName);
                          String _elvis = null;
                          String _get = fkToTypeName.get(fieldName);
                          if (_get != null) {
                            _elvis = _get;
                          } else {
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
                            _elvis = _switchResult;
                          }
                          it_1.setTypeName(_elvis);
                        } catch (Throwable _e) {
                          throw Exceptions.sneakyThrow(_e);
                        }
                      };
                      ColumnInfo _doubleArrow = ObjectExtensions.<ColumnInfo>operator_doubleArrow(_columnInfo, _function_1);
                      _columns.add(_doubleArrow);
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
        } finally {
          conn.close();
        }
        _xblockexpression = tables;
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public String toCamelCase(final String name) {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
  }
  
  public String getProjectPath(final Model model) {
    Resource _eResource = model.eResource();
    ResourceSet _resourceSet = _eResource.getResourceSet();
    final XtextResourceSet xtextResourceSet = ((XtextResourceSet) _resourceSet);
    Object _classpathURIContext = xtextResourceSet.getClasspathURIContext();
    final IJavaProject javaProject = ((IJavaProject) _classpathURIContext);
    if ((javaProject != null)) {
      final IProject project = javaProject.getProject();
      URI _locationURI = project.getLocationURI();
      return _locationURI.getPath();
    } else {
      return "./";
    }
  }
}
