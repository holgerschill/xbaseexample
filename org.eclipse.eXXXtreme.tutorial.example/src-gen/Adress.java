import java.math.BigDecimal;
import org.eclipse.eXXXtreme.tutorial.ITable;

@SuppressWarnings("all")
public class Adress implements ITable {
  private BigDecimal Id;
  
  public BigDecimal getId() {
    return this.Id;
  }
  
  public void setId(final BigDecimal Id) {
    this.Id = Id;
  }
  
  private String City;
  
  public String getCity() {
    return this.City;
  }
  
  public void setCity(final String City) {
    this.City = City;
  }
}
