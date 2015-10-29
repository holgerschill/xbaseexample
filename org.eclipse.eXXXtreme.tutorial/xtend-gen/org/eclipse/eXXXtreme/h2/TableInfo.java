package org.eclipse.eXXXtreme.h2;

import java.util.List;
import org.eclipse.eXXXtreme.h2.ColumnInfo;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class TableInfo {
  @Accessors
  private String name;
  
  @Accessors
  private List<ColumnInfo> columns;
  
  @Pure
  public String getName() {
    return this.name;
  }
  
  public void setName(final String name) {
    this.name = name;
  }
  
  @Pure
  public List<ColumnInfo> getColumns() {
    return this.columns;
  }
  
  public void setColumns(final List<ColumnInfo> columns) {
    this.columns = columns;
  }
}
