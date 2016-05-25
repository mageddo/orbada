package pl.mpak.sky.gui.swing.syntax;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;

import pl.mpak.sky.gui.swing.SwingUtil;

public class SQLSyntaxDocument extends SyntaxDocument {
  private static final long serialVersionUID = 9222327549103537186L;

  public final static int KEYWORD = 1;
  public final static int COMMENT = 2;
  public final static int OPERATOR = 3;
  public final static int TABLE = 4;
  public final static int QUOTED_NAME = 5;
  public final static int STRING = 6;
  public final static int ERROR = 7;
  public final static int SQL_FUNCTION = 8;
  public final static int USER_FUNCTION = 9;
  public final static int DATA_TYPE = 10;
  public final static int HINT = 11;
  public final static int IDENTIFIER = 12;
  public final static int NUMERIC = 13;
  public final static int SYSTEM_OBJECT = 14;
  public final static int COMMAND_PARAMETER = 15;
  public final static int DOCUMENTATION = 16;
  public final static int DOCUMENTATION_KEYWORD = 17;
  public final static int DOCUMENTATION_HTML_TAG = 18;
  public final static int BRACKET = 19;
  public final static int SPECJAL_CHAR = 20;
  public final static int LOCAL_IDENTIFIER = 21;
  
  public final static int[] blankAllStyles = new int[] {NONE, COMMENT, DOCUMENTATION, DOCUMENTATION_KEYWORD, DOCUMENTATION_HTML_TAG};
  public final static int[] documentationStyles = new int[] {DOCUMENTATION, DOCUMENTATION_KEYWORD, DOCUMENTATION_HTML_TAG};
  public final static int[] commentStyles = new int[] {COMMENT, HINT, DOCUMENTATION, DOCUMENTATION_KEYWORD, DOCUMENTATION_HTML_TAG};
  
  private final static String operators = "!+*%&#^!@~<>=|:";
  
  public SQLSyntaxDocument() {
    super();
    setIgnoreTokentForImmediateSnippet(new int[] {STRING, QUOTED_NAME, HINT, COMMENT, DOCUMENTATION, DOCUMENTATION_KEYWORD, DOCUMENTATION_HTML_TAG});
    addStyle(KEYWORD, new SyntaxStyle("Key word", Color.BLUE));
    addStyle(COMMENT, new SyntaxStyle("Comment", Color.GRAY));
    addStyle(OPERATOR, new SyntaxStyle("Operator", Color.BLUE));
    addStyle(TABLE, new SyntaxStyle("Table", SwingUtil.Color.NAVY));
    addStyle(QUOTED_NAME, new SyntaxStyle("Quoted name", SwingUtil.Color.GREEN));
    addStyle(STRING, new SyntaxStyle("String", SwingUtil.Color.GREEN));
    addStyle(ERROR, new SyntaxStyle("Error", Color.RED));
    addStyle(SQL_FUNCTION, new SyntaxStyle("Sql function", new Color(128, 128, 0)));
    addStyle(USER_FUNCTION, new SyntaxStyle("User function", new Color(128, 0, 0)));
    addStyle(DATA_TYPE, new SyntaxStyle("Data type", new Color(0, 98, 98)));
    addStyle(HINT, new SyntaxStyle("Hint", SwingUtil.Color.DARKORANGE));
    addStyle(IDENTIFIER, new SyntaxStyle("Identifier", Color.BLACK));
    addStyle(NUMERIC, new SyntaxStyle("Numeric", SwingUtil.Color.FUCHSIA));
    addStyle(SYSTEM_OBJECT, new SyntaxStyle("System object", new Color(128, 0, 0)));
    addStyle(COMMAND_PARAMETER, new SyntaxStyle("Command parameter", new Color(68, 0, 128)));
    addStyle(DOCUMENTATION, new SyntaxStyle("Documentation", new Color(63, 97, 159)));
    addStyle(DOCUMENTATION_KEYWORD, new SyntaxStyle("Documentation key word", new Color(63, 97, 159), true, false));
    addStyle(DOCUMENTATION_HTML_TAG, new SyntaxStyle("Documentation html tag", new Color(127, 127, 159)));
    addStyle(BRACKET, new SyntaxStyle("Brackets", Color.BLACK));
    addStyle(SPECJAL_CHAR, new SyntaxStyle("Special Char", Color.BLACK));
    addStyle(LOCAL_IDENTIFIER, new SyntaxStyle("Local identifier", SwingUtil.Color.NAVY));
    
    setIgnoreCase(true);
  }
  
  public void defaultKeyWords() {
    addSingleKeyWord("ABORT", KEYWORD);
    addSingleKeyWord("ACCESS", KEYWORD);
    addSingleKeyWord("ADD", KEYWORD);
    addSingleKeyWord("AFTER", KEYWORD);
    addSingleKeyWord("ALTER", KEYWORD);
    addSingleKeyWord("ARRAY", KEYWORD);
    addSingleKeyWord("ARRAY_LEN", KEYWORD);
    addSingleKeyWord("AND", KEYWORD);
    addSingleKeyWord("AS", KEYWORD);
    addSingleKeyWord("ASC", KEYWORD);
    addSingleKeyWord("ASSERT", KEYWORD);
    addSingleKeyWord("ASSIGN", KEYWORD);
    addSingleKeyWord("AT", KEYWORD);
    addSingleKeyWord("AUDIT", KEYWORD);
    addSingleKeyWord("AUTHORIZATION", KEYWORD);
    addSingleKeyWord("BASE_TABLE", KEYWORD);
    addSingleKeyWord("BEGIN", KEYWORD);
    addSingleKeyWord("BEFORE", KEYWORD);
    addSingleKeyWord("BODY", KEYWORD);
    addSingleKeyWord("CASCADE", KEYWORD);
    addSingleKeyWord("CASE", KEYWORD);
    addSingleKeyWord("CHAR", KEYWORD);
    addSingleKeyWord("CHAR_BASE", KEYWORD);
    addSingleKeyWord("CHECK", KEYWORD);
    addSingleKeyWord("CLOSE", KEYWORD);
    addSingleKeyWord("CLUSTER", KEYWORD);
    addSingleKeyWord("CLUSTERS", KEYWORD);
    addSingleKeyWord("COLAUTH", KEYWORD);
    addSingleKeyWord("COLUMN", KEYWORD);
    addSingleKeyWord("COMMENT", KEYWORD);
    addSingleKeyWord("COMMIT", KEYWORD);
    addSingleKeyWord("COMPRESS", KEYWORD);
    addSingleKeyWord("CONSTANT", KEYWORD);
    addSingleKeyWord("CONSTRAINT", KEYWORD);
    addSingleKeyWord("CREATE", KEYWORD);
    addSingleKeyWord("CURRENT", KEYWORD);
    addSingleKeyWord("CURRVAL", KEYWORD);
    addSingleKeyWord("CURSOR", KEYWORD);
    addSingleKeyWord("DATABASE", KEYWORD);
    addSingleKeyWord("DATA_BASE", KEYWORD);
    addSingleKeyWord("DATE", KEYWORD);
    addSingleKeyWord("DBA", KEYWORD);
    addSingleKeyWord("DEBUGOFF", KEYWORD);
    addSingleKeyWord("DEBUGON", KEYWORD);
    addSingleKeyWord("DECLARE", KEYWORD);
    addSingleKeyWord("DEFAULT", KEYWORD);
    addSingleKeyWord("DEFINITION", KEYWORD);
    addSingleKeyWord("DELAY", KEYWORD);
    addSingleKeyWord("DELETE", KEYWORD);
    addSingleKeyWord("DESC", KEYWORD);
    addSingleKeyWord("DIGITS", KEYWORD);
    addSingleKeyWord("DISPOSE", KEYWORD);
    addSingleKeyWord("DISTINCT", KEYWORD);
    addSingleKeyWord("DO", KEYWORD);
    addSingleKeyWord("DROP", KEYWORD);
    addSingleKeyWord("DUMP", KEYWORD);
    addSingleKeyWord("ELSE", KEYWORD);
    addSingleKeyWord("EACH", KEYWORD);
    addSingleKeyWord("ELSIF", KEYWORD);
    addSingleKeyWord("END", KEYWORD);
    addSingleKeyWord("ENTRY", KEYWORD);
    addSingleKeyWord("EXCEPTION", KEYWORD);
    addSingleKeyWord("EXCEPTION_INIT", KEYWORD);
    addSingleKeyWord("EXCLUSIVE", KEYWORD);
    addSingleKeyWord("EXIT", KEYWORD);
    addSingleKeyWord("FALSE", KEYWORD);
    addSingleKeyWord("FETCH", KEYWORD);
    addSingleKeyWord("FILE", KEYWORD);
    addSingleKeyWord("FOR", KEYWORD);
    addSingleKeyWord("FOREIGN", KEYWORD);
    addSingleKeyWord("FORM", KEYWORD);
    addSingleKeyWord("FROM", KEYWORD);
    addSingleKeyWord("FUNCTION", KEYWORD);
    addSingleKeyWord("GENERIC", KEYWORD);
    addSingleKeyWord("GOTO", KEYWORD);
    addSingleKeyWord("GRANT", KEYWORD);
    addSingleKeyWord("GREATEST", KEYWORD);
    addSingleKeyWord("GROUP", KEYWORD);
    addSingleKeyWord("HAVING", KEYWORD);
    addSingleKeyWord("IDENTIFIED", KEYWORD);
    addSingleKeyWord("IDENTITYCOL", KEYWORD);
    addSingleKeyWord("IF", KEYWORD);
    addSingleKeyWord("IMMEDIATE", KEYWORD);
    addSingleKeyWord("INCREMENT", KEYWORD);
    addSingleKeyWord("INDEX", KEYWORD);
    addSingleKeyWord("INDEXES", KEYWORD);
    addSingleKeyWord("INDICATOR", KEYWORD);
    addSingleKeyWord("INITIAL", KEYWORD);
    addSingleKeyWord("INSERT", KEYWORD);
    addSingleKeyWord("INTERFACE", KEYWORD);
    addSingleKeyWord("INTO", KEYWORD);
    addSingleKeyWord("IS", KEYWORD);
    addSingleKeyWord("KEY", KEYWORD);
    addSingleKeyWord("LEAST", KEYWORD);
    addSingleKeyWord("LEVEL", KEYWORD);
    addSingleKeyWord("LIMITED", KEYWORD);
    addSingleKeyWord("LOCK", KEYWORD);
    addSingleKeyWord("LONG", KEYWORD);
    addSingleKeyWord("LOOP", KEYWORD);
    addSingleKeyWord("MAXEXTENTS", KEYWORD);
    addSingleKeyWord("MINUS", KEYWORD);
    addSingleKeyWord("MLSLABEL", KEYWORD);
    addSingleKeyWord("MOD", KEYWORD);
    addSingleKeyWord("MORE", KEYWORD);
    addSingleKeyWord("NEW", KEYWORD);
    addSingleKeyWord("NEXTVAL", KEYWORD);
    addSingleKeyWord("NOAUDIT", KEYWORD);
    addSingleKeyWord("NOCOMPRESS", KEYWORD);
    addSingleKeyWord("NOWAIT", KEYWORD);
    addSingleKeyWord("NULL", KEYWORD);
    addSingleKeyWord("NUMBER_BASE", KEYWORD);
    addSingleKeyWord("OF", KEYWORD);
    addSingleKeyWord("OFFLINE", KEYWORD);
    addSingleKeyWord("ON", KEYWORD);
    addSingleKeyWord("OFF", KEYWORD);
    addSingleKeyWord("OLD", KEYWORD);
    addSingleKeyWord("ONLINE", KEYWORD);
    addSingleKeyWord("OPEN", KEYWORD);
    addSingleKeyWord("OPTION", KEYWORD);
    addSingleKeyWord("ORDER", KEYWORD);
    addSingleKeyWord("OUT", KEYWORD);
    addSingleKeyWord("PACKAGE", KEYWORD);
    addSingleKeyWord("PARTITION", KEYWORD);
    addSingleKeyWord("PCTFREE", KEYWORD);
    addSingleKeyWord("PRAGMA", KEYWORD);
    addSingleKeyWord("PRIVATE", KEYWORD);
    addSingleKeyWord("PRIVILEGES", KEYWORD);
    addSingleKeyWord("PROCEDURE", KEYWORD);
    addSingleKeyWord("PUBLIC", KEYWORD);
    addSingleKeyWord("QUOTED_IDENTIFIER", KEYWORD);
    addSingleKeyWord("RAISE", KEYWORD);
    addSingleKeyWord("RANGE", KEYWORD);
    addSingleKeyWord("RECORD", KEYWORD);
    addSingleKeyWord("REF", KEYWORD);
    addSingleKeyWord("RELEASE", KEYWORD);
    addSingleKeyWord("RENAME", KEYWORD);
    addSingleKeyWord("REFERENCES", KEYWORD);
    addSingleKeyWord("REFERENCING", KEYWORD);
    addSingleKeyWord("RESOURCE", KEYWORD);
    addSingleKeyWord("RETURN", KEYWORD);
    addSingleKeyWord("REVERSE", KEYWORD);
    addSingleKeyWord("REVOKE", KEYWORD);
    addSingleKeyWord("ROLLBACK", KEYWORD);
    addSingleKeyWord("ROW", KEYWORD);
    addSingleKeyWord("ROWLABEL", KEYWORD);
    addSingleKeyWord("ROWNUM", KEYWORD);
    addSingleKeyWord("ROWS", KEYWORD);
    addSingleKeyWord("ROWTYPE", KEYWORD);
    addSingleKeyWord("RUN", KEYWORD);
    addSingleKeyWord("SAVEPOINT", KEYWORD);
    addSingleKeyWord("SCHEMA", KEYWORD);
    addSingleKeyWord("SELECT", KEYWORD);
    addSingleKeyWord("SEPERATE", KEYWORD);
    addSingleKeyWord("SESSION", KEYWORD);
    addSingleKeyWord("SET", KEYWORD);
    addSingleKeyWord("SHARE", KEYWORD);
    addSingleKeyWord("SPACE", KEYWORD);
    addSingleKeyWord("SQL", KEYWORD);
    addSingleKeyWord("SQLCODE", KEYWORD);
    addSingleKeyWord("SQLERRM", KEYWORD);
    addSingleKeyWord("STATEMENT", KEYWORD);
    addSingleKeyWord("STDDEV", KEYWORD);
    addSingleKeyWord("SUBTYPE", KEYWORD);
    addSingleKeyWord("SUCCESSFULL", KEYWORD);
    addSingleKeyWord("SYNONYM", KEYWORD);
    addSingleKeyWord("SYSDATE", KEYWORD);
    addSingleKeyWord("TABAUTH", KEYWORD);
    addSingleKeyWord("TABLE", KEYWORD);
    addSingleKeyWord("TABLES", KEYWORD);
    addSingleKeyWord("TASK", KEYWORD);
    addSingleKeyWord("TERMINATE", KEYWORD);
    addSingleKeyWord("THEN", KEYWORD);
    addSingleKeyWord("TO", KEYWORD);
    addSingleKeyWord("TRIGGER", KEYWORD);
    addSingleKeyWord("TRUE", KEYWORD);
    addSingleKeyWord("TYPE", KEYWORD);
    addSingleKeyWord("UID", KEYWORD);
    addSingleKeyWord("UNION", KEYWORD);
    addSingleKeyWord("UNIQUE", KEYWORD);
    addSingleKeyWord("UPDATE", KEYWORD);
    addSingleKeyWord("UPDATETEXT", KEYWORD);
    addSingleKeyWord("USE", KEYWORD);
    addSingleKeyWord("USER", KEYWORD);
    addSingleKeyWord("USING", KEYWORD);
    addSingleKeyWord("VALIDATE", KEYWORD);
    addSingleKeyWord("VALUES", KEYWORD);
    addSingleKeyWord("VARIANCE", KEYWORD);
    addSingleKeyWord("VIEW", KEYWORD);
    addSingleKeyWord("VIEWS", KEYWORD);
    addSingleKeyWord("WHEN", KEYWORD);
    addSingleKeyWord("WHENEVER", KEYWORD);
    addSingleKeyWord("WHERE", KEYWORD);
    addSingleKeyWord("WHILE", KEYWORD);
    addSingleKeyWord("WITH", KEYWORD);
    addSingleKeyWord("WORK", KEYWORD);
    addSingleKeyWord("WRITE", KEYWORD);
    addSingleKeyWord("XOR", KEYWORD);
    addSingleKeyWord("TRANSACTION", KEYWORD);
    addSingleKeyWord("READ", KEYWORD);
    addSingleKeyWord("ALL", KEYWORD);
    addSingleKeyWord("AND", KEYWORD);
    addSingleKeyWord("ANY", KEYWORD);
    addSingleKeyWord("BETWEEN", KEYWORD);
    addSingleKeyWord("BY", KEYWORD);
    addSingleKeyWord("CONNECT", KEYWORD);
    addSingleKeyWord("EXISTS", KEYWORD);
    addSingleKeyWord("IN", KEYWORD);
    addSingleKeyWord("INTERSECT", KEYWORD);
    addSingleKeyWord("LIKE", KEYWORD);
    addSingleKeyWord("NOT", KEYWORD);
    addSingleKeyWord("NULL", KEYWORD);
    addSingleKeyWord("OR", KEYWORD);
    addSingleKeyWord("START", KEYWORD);
    addSingleKeyWord("UNION", KEYWORD);
    addSingleKeyWord("WITH", KEYWORD);
    addSingleKeyWord("LEFT", KEYWORD);
    addSingleKeyWord("RIGHT", KEYWORD);
    addSingleKeyWord("OUTER", KEYWORD);
    addSingleKeyWord("INNER", KEYWORD);
    addSingleKeyWord("FULL", KEYWORD);
    addSingleKeyWord("JOIN", KEYWORD);
  }
  
  public void defaultDocumentationKeywords() {
    addSingleKeyWord("@AUTHOR", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@SEE", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@TODO", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@PARAM", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@RETURN", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@SQL_PARAM", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@TASK", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@LINK", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@VALUE", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@DEPRECED", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@EXCEPTION", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@THROWS", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@VERSION", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@DOC", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@SERIAL", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@CREATE", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@CREATED", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@MODIFY", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@MODIFIED", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@FUNCTION", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@PROCEDURE", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@METHOD", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@PACKAGE", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@BODY", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@BUG", DOCUMENTATION_KEYWORD);
    addSingleKeyWord("@FIX", DOCUMENTATION_KEYWORD);
  }
  
  public void defaultSqlFunctions() {
    addSingleKeyWord("ABS", SQL_FUNCTION);
    addSingleKeyWord("ACOS", SQL_FUNCTION);
    addSingleKeyWord("ADD_MONTHS", SQL_FUNCTION);
    addSingleKeyWord("ASCII", SQL_FUNCTION);
    addSingleKeyWord("ASIN", SQL_FUNCTION);
    addSingleKeyWord("ATAN", SQL_FUNCTION);
    addSingleKeyWord("ATAN2", SQL_FUNCTION);
    addSingleKeyWord("AVG", SQL_FUNCTION);
    addSingleKeyWord("CEIL", SQL_FUNCTION);
    addSingleKeyWord("CHARTOROWID", SQL_FUNCTION);
    addSingleKeyWord("CHR", SQL_FUNCTION);
    addSingleKeyWord("CONCAT", SQL_FUNCTION);
    addSingleKeyWord("CONVERT", SQL_FUNCTION);
    addSingleKeyWord("COS", SQL_FUNCTION);
    addSingleKeyWord("COSH", SQL_FUNCTION);
    addSingleKeyWord("COUNT", SQL_FUNCTION);
    addSingleKeyWord("DECODE", SQL_FUNCTION);
    addSingleKeyWord("DEFINE", SQL_FUNCTION);
    addSingleKeyWord("FLOOR", SQL_FUNCTION);
    addSingleKeyWord("HEXTORAW", SQL_FUNCTION);
    addSingleKeyWord("INITCAP", SQL_FUNCTION);
    addSingleKeyWord("INSTR", SQL_FUNCTION);
    addSingleKeyWord("INSTRB", SQL_FUNCTION);
    addSingleKeyWord("LAST_DAY", SQL_FUNCTION);
    addSingleKeyWord("LENGTH", SQL_FUNCTION);
    addSingleKeyWord("LENGTHB", SQL_FUNCTION);
    addSingleKeyWord("LN", SQL_FUNCTION);
    addSingleKeyWord("LOG", SQL_FUNCTION);
    addSingleKeyWord("LOWER", SQL_FUNCTION);
    addSingleKeyWord("LPAD", SQL_FUNCTION);
    addSingleKeyWord("LTRIM", SQL_FUNCTION);
    addSingleKeyWord("MOD", SQL_FUNCTION);
    addSingleKeyWord("MONTHS_BETWEEN", SQL_FUNCTION);
    addSingleKeyWord("MAX", SQL_FUNCTION);
    addSingleKeyWord("MIN", SQL_FUNCTION);
    addSingleKeyWord("NEW_TIME", SQL_FUNCTION);
    addSingleKeyWord("NEXT_DAY", SQL_FUNCTION);
    addSingleKeyWord("NLSSORT", SQL_FUNCTION);
    addSingleKeyWord("NSL_INITCAP", SQL_FUNCTION);
    addSingleKeyWord("NLS_LOWER", SQL_FUNCTION);
    addSingleKeyWord("NLS_UPPER", SQL_FUNCTION);
    addSingleKeyWord("NVL", SQL_FUNCTION);
    addSingleKeyWord("POWER", SQL_FUNCTION);
    addSingleKeyWord("RAWTOHEX", SQL_FUNCTION);
    addSingleKeyWord("REPLACE", SQL_FUNCTION);
    addSingleKeyWord("ROUND", SQL_FUNCTION);
    addSingleKeyWord("ROWIDTOCHAR", SQL_FUNCTION);
    addSingleKeyWord("RPAD", SQL_FUNCTION);
    addSingleKeyWord("RTRIM", SQL_FUNCTION);
    addSingleKeyWord("SIGN", SQL_FUNCTION);
    addSingleKeyWord("SOUNDEX", SQL_FUNCTION);
    addSingleKeyWord("SIN", SQL_FUNCTION);
    addSingleKeyWord("SINH", SQL_FUNCTION);
    addSingleKeyWord("SQRT", SQL_FUNCTION);
    addSingleKeyWord("SUBSTR", SQL_FUNCTION);
    addSingleKeyWord("SUBSTRB", SQL_FUNCTION);
    addSingleKeyWord("TAN", SQL_FUNCTION);
    addSingleKeyWord("TANH", SQL_FUNCTION);
    addSingleKeyWord("TO_CHAR", SQL_FUNCTION);
    addSingleKeyWord("TO_DATE", SQL_FUNCTION);
    addSingleKeyWord("TO_MULTIBYTE", SQL_FUNCTION);
    addSingleKeyWord("TO_NUMBER", SQL_FUNCTION);
    addSingleKeyWord("TO_SINGLE_BYTE", SQL_FUNCTION);
    addSingleKeyWord("TRANSLATE", SQL_FUNCTION);
    addSingleKeyWord("TRUNC", SQL_FUNCTION);
    addSingleKeyWord("UPPER", SQL_FUNCTION);
    addSingleKeyWord("SYSDATE", SQL_FUNCTION);
    addSingleKeyWord("SUM", SQL_FUNCTION);
  }
  
  public void defaultDataTypes() {
    addSingleKeyWord("binary", DATA_TYPE);
    addSingleKeyWord("bit", DATA_TYPE);
    addSingleKeyWord("blob", DATA_TYPE);
    addSingleKeyWord("boolean", DATA_TYPE);
    addSingleKeyWord("char", DATA_TYPE);
    addSingleKeyWord("character", DATA_TYPE);
    addSingleKeyWord("clob", DATA_TYPE);
    addSingleKeyWord("DATE", DATA_TYPE);
    addSingleKeyWord("datetime", DATA_TYPE);
    addSingleKeyWord("decimal", DATA_TYPE);
    addSingleKeyWord("DOUBLE", DATA_TYPE);
    addSingleKeyWord("PRECISION", DATA_TYPE);
    addSingleKeyWord("float", DATA_TYPE);
    addSingleKeyWord("image", DATA_TYPE);
    addSingleKeyWord("int", DATA_TYPE);
    addSingleKeyWord("integer", DATA_TYPE);
    addSingleKeyWord("money", DATA_TYPE);
    addSingleKeyWord("name", DATA_TYPE);
    addSingleKeyWord("NATURAL", DATA_TYPE);
    addSingleKeyWord("NATURALN", DATA_TYPE);
    addSingleKeyWord("NUMBER", DATA_TYPE);
    addSingleKeyWord("numeric", DATA_TYPE);
    addSingleKeyWord("nchar", DATA_TYPE);
    addSingleKeyWord("nvarchar", DATA_TYPE);
    addSingleKeyWord("ntext", DATA_TYPE);
    addSingleKeyWord("pls_integer", DATA_TYPE);
    addSingleKeyWord("POSITIVE", DATA_TYPE);
    addSingleKeyWord("POSITIVEN", DATA_TYPE);
    addSingleKeyWord("RAW", DATA_TYPE);
    addSingleKeyWord("real", DATA_TYPE);
    addSingleKeyWord("ROWID", DATA_TYPE);
    addSingleKeyWord("SIGNTYPE", DATA_TYPE);
    addSingleKeyWord("smalldatetime", DATA_TYPE);
    addSingleKeyWord("smallint", DATA_TYPE);
    addSingleKeyWord("smallmoney", DATA_TYPE);
    addSingleKeyWord("timestamp", DATA_TYPE);
    addSingleKeyWord("tinyint", DATA_TYPE);
    addSingleKeyWord("uniqueidentifier", DATA_TYPE);
    addSingleKeyWord("UROWID", DATA_TYPE);
    addSingleKeyWord("varbinary", DATA_TYPE);
    addSingleKeyWord("varchar", DATA_TYPE);
    addSingleKeyWord("varchar2", DATA_TYPE);
  }
  
  public void defaultAll() {
    defaultKeyWords();
    defaultDocumentationKeywords();
    defaultSqlFunctions();
    defaultDataTypes();
  }

  public void getText(int offset, int length, Segment txt) throws BadLocationException {
    if (offset +length > getLength()) {
      length = getLength() -offset;
    }
    super.getText(offset, length, txt);
  }

  protected boolean tokenizeLine(Segment line, LineInfo info, int lastToken) {
    info.clear();
    int index = line.offset;
    int start = index;
    int length = line.count +line.offset;
    
    while (index < length) {
      switch (line.array[index]) {
        case '`':
        case '"': {
          if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            lastToken = QUOTED_NAME;
            start = index;
          }
          else if (lastToken == QUOTED_NAME) {
            info.addToken(QUOTED_NAME, index -start +1);
            lastToken = NONE;
            start = index +1;
          }
          break;
        }
        case '\'': {
          if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            lastToken = STRING;
            start = index;
          }
          else if (lastToken == STRING) {
            info.addToken(STRING, index -start +1);
            lastToken = NONE;
            start = index +1;
          }
          break;
        }
        case '/': {
          if (lastToken == NONE) {
            if (index +1 < length && line.array[index +1] == '*') {
              info.addToken(NONE, index -start);
              int i = index +2;
              while (i < length && line.array[i] == ' ') {
                i++;
              }
              if (i < length && line.array[i] == '+') {
                lastToken = HINT;
              }
              else if (i < length && line.array[i] == '*') {
                lastToken = DOCUMENTATION;
              }
              else {
                lastToken = COMMENT;
              }
              start = index;
            }
            else {
              info.addToken(NONE, index -start);
              start = index;
              while (index +1 < length && operators.indexOf(line.array[index +1]) >= 0) {
                index++;
              }
              info.addToken(OPERATOR, index -start +1);
              start = index +1;
            }
          }
          break;
        }
        case '*': {
          if ((lastToken == COMMENT || lastToken == HINT || lastToken == DOCUMENTATION) && 
              index +1 < length && line.array[index +1] == '/') {
            index++;
            info.addToken(lastToken, index -start +1);
            lastToken = NONE;
            start = index +1;
          }
          else if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            start = index;
            while (index +1 < length && operators.indexOf(line.array[index +1]) >= 0) {
              index++;
            }
            info.addToken(OPERATOR, index -start +1);
            start = index +1;
          }
          break;
        }
        case '-': {
          if (lastToken == NONE) {
            if (index +1 < length && line.array[index +1] == '-') {
              info.addToken(NONE, index -start);
              start = index;
              index+=2;
              while (index < length && line.array[index] == ' ') {
                index++;
              }
              if (index < length && line.array[index] == '+') {
                info.addToken(HINT, length -start);
              }
              else {
                info.addToken(COMMENT, length -start);
              }
              index = length -1;
              start = index +1;
            }
            else {
              info.addToken(NONE, index -start);
              start = index;
              while (index +1 < length && operators.indexOf(line.array[index +1]) >= 0) {
                index++;
              }
              info.addToken(OPERATOR, index -start +1);
              start = index +1;
            }
          }
          break;
        }
        case '>':
        case '%':
        case '^':
        case '!':
        case '+':
        case '=':
        case '|':
        case '#': {
          if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            start = index;
            while (index +1 < length && operators.indexOf(line.array[index +1]) >= 0) {
              index++;
            }
            info.addToken(OPERATOR, index -start +1);
            start = index +1;
          }
          break;
        }
        case '@': {
          if (lastToken == DOCUMENTATION && index +1 < length && (Character.isLetter(line.array[index +1]) || line.array[index] == '_')) {
            info.addToken(DOCUMENTATION, index -start);
            StringBuilder bf = new StringBuilder("@");
            index++;
            start = index;
            while (index < length && 
                (Character.isLetter(line.array[index]) || 
                 Character.isDigit(line.array[index]) || 
                 line.array[index] == '$' || 
                 line.array[index] == '#' || 
                 line.array[index] == '_')) {
              bf.append(line.array[index]);
              index++;
            }
            int style = getStyle(bf.toString());
            if (style != NONE) {
              info.addToken(style, bf.length());
            }
            else {
              info.addToken(DOCUMENTATION_KEYWORD, bf.length());
            }
            start = index;
            index--;
          }
          else if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            start = index;
            while (index +1 < length && operators.indexOf(line.array[index +1]) >= 0) {
              index++;
            }
            info.addToken(OPERATOR, index -start +1);
            start = index +1;
          }
          break;
        }
        case '<': {
          if (lastToken == DOCUMENTATION) {
            info.addToken(DOCUMENTATION, index -start);
            StringBuilder bf = new StringBuilder("<");
            index++;
            start = index;
            while (index < length && line.array[index] != '>' && line.array[index] != '\n') {
              bf.append(line.array[index]);
              index++;
            }
            if (line.array[index] == '>') { 
              bf.append('>');
              index++;
            }
            info.addToken(DOCUMENTATION_HTML_TAG, bf.length());
            start = index;
            index--;
          }
          else if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            start = index;
            while (index +1 < length && operators.indexOf(line.array[index +1]) >= 0) {
              index++;
            }
            info.addToken(OPERATOR, index -start +1);
            start = index +1;
          }
          break;
        }
        case '$': {
          if (lastToken == NONE && index +1 < length && (Character.isDigit(line.array[index +1]))) {
            info.addToken(NONE, index -start);
            start = index;
            index++;
            while (index < length && Character.isDigit(line.array[index])) {
              index++;
            }
            info.addToken(COMMAND_PARAMETER, index -start);
            start = index;
            index--;
          }
          else if (lastToken == NONE && index +1 < length && (Character.isLetter(line.array[index +1]) || line.array[index +1] == '_')) {
            StringBuilder bf = new StringBuilder();
            info.addToken(NONE, index -start);
            start = index;
            while (index < length &&
                (Character.isLetter(line.array[index]) || 
                 Character.isDigit(line.array[index]) || 
                 line.array[index] == '$' || 
                 line.array[index] == '#' || 
                 line.array[index] == '_')) {
              bf.append(line.array[index]);
              index++;
            }
            int style = NONE;
            String words = info.buildFromLastTokens(SPECJAL_CHAR, bf.toString(), line);
            style = getStyle(words);
            if (style == NONE) {
              style = getStyle(bf.toString());
            }
            if (style != NONE) {
              info.addToken(style, bf.length());
            }
            else {
              info.addToken(IDENTIFIER, bf.length());
            }
            start = index;
            index--;
          }
          else if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            start = index;
            while (index +1 < length && operators.indexOf(line.array[index +1]) >= 0) {
              index++;
            }
            info.addToken(OPERATOR, index -start +1);
            start = index +1;
          }
          break;
        }
        case '&':
        case ':': {
          if (lastToken == NONE && index +1 < length && (Character.isLetter(line.array[index +1]) || line.array[index] == '_')) {
            info.addToken(NONE, index -start);
            start = index;
            index++;
            while (index < length && 
                (Character.isLetter(line.array[index]) || 
                 Character.isDigit(line.array[index]) || 
                 line.array[index] == '$' ||
                 line.array[index] == '#' || 
                 line.array[index] == '_')) {
              index++;
            }
            info.addToken(COMMAND_PARAMETER, index -start);
            start = index;
            index--;
          }
          else if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            start = index;
            while (index +1 < length && operators.indexOf(line.array[index +1]) >= 0) {
              index++;
            }
            info.addToken(OPERATOR, index -start +1);
            start = index +1;
          }
          break;
        }
        case ']':
        case '[':
        case '}':
        case '{':
        case ')':
        case '(': {
          if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            if (line.array[index] == '(' && index +2 < length && line.array[index +1] == '+' && line.array[index +2] == ')') {
              info.addToken(OPERATOR, 3);
              index += 2;
              start = index +1;
            }
            else {
              info.addToken(BRACKET, 1);
              start = index +1;
            }
          }
          break;
        }
        case ',':
        case ';':
        case '.': {
          if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            info.addToken(SPECJAL_CHAR, 1);
            start = index +1;
          }
          break;
        }
        default: {
          if (lastToken == NONE) {
            if (Character.isDigit(line.array[index])) {
              info.addToken(NONE, index -start);
              start = index;
              while (index < length && Character.isDigit(line.array[index])) {
                index++;
              }
              if (index < length && line.array[index] == '.') {
                index++;
                while (index < length && Character.isDigit(line.array[index])) {
                  index++;
                }
              }
              if (index < length && (line.array[index] == 'e' || line.array[index] == 'E')) {
                index++;
                if (index < length && (line.array[index] == '-' || line.array[index] == '+')) {
                  index++;
                  while (index < length && Character.isDigit(line.array[index])) {
                    index++;
                  }
                }
              }
              info.addToken(NUMERIC, index -start);
              start = index;
              index--;
            }
            else if (Character.isLetter(line.array[index]) || line.array[index] == '_') {
              StringBuilder bf = new StringBuilder();
              info.addToken(NONE, index -start);
              start = index;
              while (index < length &&
                  (Character.isLetter(line.array[index]) || 
                   Character.isDigit(line.array[index]) || 
                   line.array[index] == '$' || 
                   line.array[index] == '#' || 
                   line.array[index] == '_')) {
                bf.append(line.array[index]);
                index++;
              }
              int style = NONE;
              String words = info.buildFromLastTokens(SPECJAL_CHAR, bf.toString(), line);
              style = getStyle(words);
              if (style == NONE) {
                style = getStyle(bf.toString());
              }
              if (style != NONE) {
                info.addToken(style, bf.length());
              }
              else {
                info.addToken(IDENTIFIER, bf.length());
              }
              start = index;
              index--;
            }
          }
        }
      }
      index++;
    }

    info.addToken(lastToken, index -start);
    
    return true; //lastToken != NONE || (oldToken != info.lastStyleId() && oldToken != NONE);
  }
  
//  public String[] getWordsAt(int off) throws BadLocationException {
//    Segment segment = new Segment();
//    segment.setPartialReturn(true);
//    SyntaxDocument.LineInfo info = getLineInfoAtOffset(off, segment);
//    if (info != null) {
//      Token token = info.getTokenAtOffset(off -getLineOffsetAt(off));
//      if (token != null) {
//        ArrayList<String> words = new ArrayList<String>();
//        int[] styles = {KEYWORD, TABLE, ERROR, SQL_FUNCTION, USER_FUNCTION, DATA_TYPE, IDENTIFIER, SYSTEM_OBJECT, COMMAND_PARAMETER, LOCAL_IDENTIFIER};
//        String w = info.getStringAtOffset(token.offset, segment);
//        if (token.anyOfStyle(styles)) {
//          words.add(w.toUpperCase());
//        }
//        else if (token.styleId == QUOTED_NAME && w.length() > 0) {
//          if (w.charAt(0) == '"' || w.charAt(0) == '`') {
//            words.add(w.substring(1, w.length() -1));
//          }
//          else {
//            words.add(w);
//          }
//        }
//        Token prev = token.prev;
//        if (prev != null && prev.length == 0) {
//          prev = prev.prev;
//        }
//        while (prev != null) {
//          w = info.getStringAtOffset(prev.offset, segment).trim();
//          while (w.length() == 0) {
//            prev = prev.prev;
//            if (prev != null && prev.length == 0) {
//              prev = prev.prev;
//            }
//            if (prev == null) {
//              break;
//            }
//            w = info.getStringAtOffset(prev.offset, segment).trim();
//          }
//          if (!w.equals(".")) {
//            break;
//          }
//          prev = prev.prev;
//          if (prev != null && prev.length == 0) {
//            prev = prev.prev;
//          }
//          if (prev != null) {
//            w = info.getStringAtOffset(prev.offset, segment);
//            if (prev.anyOfStyle(styles)) {
//              words.add(0, w.toUpperCase());
//            }
//            else if (prev.styleId == QUOTED_NAME) {
//              words.add(0, w.substring(1, w.length() -1));
//            }
//            else {
//              break;
//            }
//            prev = prev.prev;
//            if (prev != null && prev.length == 0) {
//              prev = prev.prev;
//            }
//          }
//          else {
//            break;
//          }
//        }
//        Token next = token.next;
//        if (next != null && next.length == 0) {
//          next = next.next;
//        }
//        while (next != null) {
//          w = info.getStringAtOffset(next.offset, segment).trim();
//          while (w.length() == 0) {
//            next = next.next;
//            if (next != null && next.length == 0) {
//              next = next.next;
//            }
//            if (next == null) {
//              break;
//            }
//            w = info.getStringAtOffset(next.offset, segment).trim();
//          }
//          if (!w.equals(".")) {
//            break;
//          }
//          next = next.next;
//          if (next != null && next.length == 0) {
//            next = next.next;
//          }
//          if (next != null) {
//            w = info.getStringAtOffset(next.offset, segment);
//            if (next.anyOfStyle(styles)) {
//              words.add(w.toUpperCase());
//            }
//            else if (next.styleId == QUOTED_NAME) {
//              words.add(w.substring(1, w.length() -1));
//            }
//            else {
//              break;
//            }
//            next = next.next;
//            if (next != null && next.length == 0) {
//              next = next.next;
//            }
//          }
//          else {
//            break;
//          }
//        }
//        return words.toArray(new String[words.size()]);
//      }
//    }
//    return super.getWordsAt(off);
//  }
  
  public void setTextComponent(SyntaxEditor textComponent) {
    super.setTextComponent(textComponent);
    ((SyntaxEditor)textComponent).setComments(new CommentSpec[] {new CommentSpec("/*", "*/"), new CommentSpec("--", "\n")});
  }
  
  public String toString() {
    return "SQL Syntax Highlight";
  }

}
