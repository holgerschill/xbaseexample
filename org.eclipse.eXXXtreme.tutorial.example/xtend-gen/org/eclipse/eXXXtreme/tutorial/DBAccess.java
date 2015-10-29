package org.eclipse.eXXXtreme.tutorial;

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
        String _simpleName = clazz.getSimpleName();
        String _upperCase = _simpleName.toUpperCase();
        String _plus = ("SELECT * FROM PUBLIC." + _upperCase);
        final ResultSet resultSet = stmt.executeQuery(_plus);
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
  
  public static <E extends Object> E createClassInstance(final Connection conn, final Class<E> clazz, final String PK_Column_Name, final Object keyValue) {
    try {
      E _xblockexpression = null;
      {
        final Statement stmt = conn.createStatement();
        String _simpleName = clazz.getSimpleName();
        String _upperCase = _simpleName.toUpperCase();
        String _plus = ("SELECT * FROM PUBLIC." + _upperCase);
        String _plus_1 = (_plus + " WHERE ");
        String _plus_2 = (_plus_1 + PK_Column_Name);
        String _plus_3 = (_plus_2 + " = ");
        String _plus_4 = (_plus_3 + keyValue);
        final ResultSet resultSet = stmt.executeQuery(_plus_4);
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
            String _lowerCase = _string.toLowerCase();
            final String COLUMN_NAME = _lowerCase.toLowerCase();
            String _string_1 = foreignKeys.getString("PKCOLUMN_NAME");
            String _lowerCase_1 = _string_1.toLowerCase();
            final String PK_COLUMN = _lowerCase_1.toUpperCase();
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
