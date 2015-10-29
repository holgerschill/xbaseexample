package org.eclipse.eXXXtreme.h2;

import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class ColumnInfo {
  @Accessors
  private String name;
  
  @Accessors
  private String typeName;
  
  @Pure
  public String getName() {
    return this.name;
  }
  
  public void setName(final String name) {
    this.name = name;
  }
  
  @Pure
  public String getTypeName() {
    return this.typeName;
  }
  
  public void setTypeName(final String typeName) {
    this.typeName = typeName;
  }
}
