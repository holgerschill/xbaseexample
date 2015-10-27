import java.util.Date;
import org.eclipse.eXXXtreme.tutorial.ITable;

@SuppressWarnings("all")
public class Person implements ITable {
  private Adress Adress;
  
  public Adress getAdress() {
    return this.Adress;
  }
  
  public void setAdress(final Adress Adress) {
    this.Adress = Adress;
  }
  
  private String Name;
  
  public String getName() {
    return this.Name;
  }
  
  public void setName(final String Name) {
    this.Name = Name;
  }
  
  private String Lastname;
  
  public String getLastname() {
    return this.Lastname;
  }
  
  public void setLastname(final String Lastname) {
    this.Lastname = Lastname;
  }
  
  private Date Dateofbirth;
  
  public Date getDateofbirth() {
    return this.Dateofbirth;
  }
  
  public void setDateofbirth(final Date Dateofbirth) {
    this.Dateofbirth = Dateofbirth;
  }
}
