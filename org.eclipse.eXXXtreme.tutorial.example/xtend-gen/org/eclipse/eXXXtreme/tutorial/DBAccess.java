package org.eclipse.eXXXtreme.tutorial;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import org.eclipse.eXXXtreme.tutorial.ITable;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class DBAccess {
  public static <E extends ITable> List<E> loadTable(final Connection conn, final Class<E> clazz) {
    try {
      List<E> _xblockexpression = null;
      {
        final Statement stmt = conn.createStatement();
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("SELECT * FROM PUBLIC.");
        String _tableName = DBAccess.toTableName(clazz);
        _builder.append(_tableName, "");
        final ResultSet resultSet = stmt.executeQuery(_builder.toString());
        final List<E> result = CollectionLiterals.<E>newArrayList();
        while (resultSet.next()) {
          E _createClassInstance = DBAccess.<E>createClassInstance(conn, resultSet, clazz);
          result.add(_createClassInstance);
        }
        _xblockexpression = result;
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static String toTableName(final Class<?> clazz) {
    String _simpleName = clazz.getSimpleName();
    return DBAccess.toSQLName(_simpleName);
  }
  
  public static String toSQLName(final String javaName) {
    final String result = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, javaName);
    return result;
  }
  
  public static <E extends Object> E createClassInstance(final Connection conn, final Class<E> clazz, final String PK_Column_Name, final Object keyValue) {
    try {
      E _xblockexpression = null;
      {
        if ((keyValue == null)) {
          return null;
        }
        final Statement stmt = conn.createStatement();
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("SELECT * FROM PUBLIC.");
        String _tableName = DBAccess.toTableName(clazz);
        _builder.append(_tableName, "");
        _builder.append(" WHERE ");
        _builder.append(PK_Column_Name, "");
        _builder.append(" = \'");
        _builder.append(keyValue, "");
        _builder.append("\' ");
        final ResultSet resultSet = stmt.executeQuery(_builder.toString());
        resultSet.next();
        _xblockexpression = DBAccess.<E>createClassInstance(conn, resultSet, clazz);
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static <E extends Object> E createClassInstance(final Connection conn, final ResultSet resultSet, final Class<E> clazz) {
    try {
      E _xblockexpression = null;
      {
        final DatabaseMetaData metaData = conn.getMetaData();
        String _tableName = DBAccess.toTableName(clazz);
        final ResultSet foreignKeys = metaData.getImportedKeys(null, null, _tableName);
        final HashMap<String, String> fkMap = CollectionLiterals.<String, String>newHashMap();
        while (foreignKeys.next()) {
          {
            final String COLUMN_NAME = foreignKeys.getString("FKCOLUMN_NAME");
            final String PK_COLUMN = foreignKeys.getString("PKCOLUMN_NAME");
            fkMap.put(COLUMN_NAME, PK_COLUMN);
          }
        }
        E _newInstance = clazz.newInstance();
        final Procedure1<E> _function = (E it) -> {
          try {
            final Field[] declaredFields = clazz.getDeclaredFields();
            for (final Field f : declaredFields) {
              {
                f.setAccessible(true);
                String _name = f.getName();
                final String col = DBAccess.toSQLName(_name);
                final Object value = resultSet.getObject(col);
                final String pk_column = fkMap.get(col);
                boolean _equals = Objects.equal(pk_column, null);
                if (_equals) {
                  f.set(it, value);
                } else {
                  Class<?> _type = f.getType();
                  Object _createClassInstance = DBAccess.createClassInstance(conn, _type, pk_column, value);
                  f.set(it, _createClassInstance);
                }
              }
            }
          } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
          }
        };
        _xblockexpression = ObjectExtensions.<E>operator_doubleArrow(_newInstance, _function);
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static Connection getConnection(final String dbPath) {
    try {
      return DriverManager.getConnection(("jdbc:h2:" + dbPath), "sa", "");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
