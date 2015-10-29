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
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, _simpleName);
  }
  
  public static <E extends Object> E createClassInstance(final Connection conn, final Class<E> clazz, final String PK_Column_Name, final Object keyValue) {
    try {
      E _xblockexpression = null;
      {
        final Statement stmt = conn.createStatement();
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("SELECT * FROM PUBLIC.");
        String _tableName = DBAccess.toTableName(clazz);
        _builder.append(_tableName, "");
        _builder.append(" WHERE ");
        _builder.append(PK_Column_Name, "");
        _builder.append(" = ");
        _builder.append(keyValue, "");
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
        String _simpleName = clazz.getSimpleName();
        String _upperCase = _simpleName.toUpperCase();
        final ResultSet foreignKeys = metaData.getImportedKeys(null, null, _upperCase);
        final HashMap<String, String> fkMap = CollectionLiterals.<String, String>newHashMap();
        while (foreignKeys.next()) {
          {
            String _string = foreignKeys.getString("FKCOLUMN_NAME");
            final String COLUMN_NAME = _string.toLowerCase();
            String _string_1 = foreignKeys.getString("PKCOLUMN_NAME");
            final String PK_COLUMN = _string_1.toLowerCase();
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
                String _upperCase_1 = _name.toUpperCase();
                final Object value = resultSet.getObject(_upperCase_1);
                String _name_1 = f.getName();
                String _lowerCase = _name_1.toLowerCase();
                final String pk_column = fkMap.get(_lowerCase);
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
