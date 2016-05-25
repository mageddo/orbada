package pl.mpak.usedb.core;

import pl.mpak.sky.SkyException;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.UseDBObject;

public class ParametrizedCommand extends UseDBObject {
  private static final long serialVersionUID = 2760793526568164758L;

  private String sqlText = "";
  protected String preparedSqlText = null;
  protected transient ParameterList parameterList = null;
  protected boolean paramCheck = true;
  protected Database database = null;
  protected boolean databaseSet = false;

  public ParametrizedCommand() {
    super();
    parameterList = new ParameterList();
  }
  
  public ParametrizedCommand(Database database) {
    this();
    setDatabase(database);
  }
  
  public void doUpdateSQLText() {
    
  }

  /**
   * Ustawienie polecenia SQL zamyka kursor jeœli by³ otwarty
   * Poleceni przygotowuje równie¿ listê parametrów
   * Mo¿na siê do nich odwo³aæ poprzez paramByName() lub getParameterList()
   * Przed ustawieniem SqlText mo¿na wywo³aæ setParseParameters(false) aby
   * parametry nie by³y przetwarzane
   * 
   * @param sqlText
   * @throws UseDBException 
   * @throws SkyException 
   */
  public void setSqlText(String sqlText) throws UseDBException {
    if (!this.sqlText.equals(sqlText)) {
      doUpdateSQLText();
      this.sqlText = sqlText;
      if (paramCheck) {
        preparedSqlText = parameterList.parseParameters(sqlText);
      }
      else {
        preparedSqlText = sqlText;
        parameterList.clear();
      }
    }
  }

  public String getSqlText() {
    return this.sqlText;
  }

  /**
   * Pozwala kontrolowaæ sprawdzanie parametrów w przekazywanym zapytaniu SQL
   * Sparsowane parametry pojawi¹ siê w ParameterList dostêpne równie¿ poprzez paramByName()
   *  
   * @see paramByName
   * @see getParameterList
   */
  public void setParamCheck(boolean paramCheck) {
    this.paramCheck = paramCheck;
  }

  public boolean getParamCheck() {
    return paramCheck;
  }

  /**
   * Pozwala uzyskac dostêp do parametru o podanej nazwie
   * Wywo³uje wyj¹tek jeœli parametr o podanej nazwie nie istnieje
   * Aby sprawdziæ czy podany parametr istnieje na liœcie nale¿y siê pos³u¿yæ getParamList().findParamByName();
   * 
   * @param name
   * @return
   * @throws UseDBException 
   * @throws SkyException
   */
  public Parameter paramByName(String name) throws UseDBException {
    return parameterList.paramByName(name);
  }
  
  public ParameterList getParameterList() {
    return parameterList;
  }
  
  public int getParameterCount() {
    return parameterList.parameterCount();
  }
  
  public Parameter getParameter(int index) {
    return parameterList.getParameter(index);
  }
  
  public void setDatabase(Database database) {
    if (this.database == null && database != null && !databaseSet) {
      database.statCreatedCommands++;
      databaseSet = true;
    }
    this.database = database;
  }

  public Database getDatabase() {
    return database;
  }

  /**
   * <p>Zwraca przygotowane do wykonania polecenie SQL 
   * @return
   * @see setSqlText
   */
  public String getPreparedSqlText() {
    return preparedSqlText;
  }
  
}
