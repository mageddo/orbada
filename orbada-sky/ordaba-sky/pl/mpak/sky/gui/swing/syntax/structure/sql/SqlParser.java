package pl.mpak.sky.gui.swing.syntax.structure.sql;

import java.util.ArrayList;
import java.util.List;

import pl.mpak.sky.gui.swing.syntax.SQLSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor.TokenRef;
import pl.mpak.sky.gui.swing.syntax.TokenCursor;
import pl.mpak.sky.gui.swing.syntax.structure.BindElement;
import pl.mpak.sky.gui.swing.syntax.structure.BooleanElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElementList;
import pl.mpak.sky.gui.swing.syntax.structure.CommentElement;
import pl.mpak.sky.gui.swing.syntax.structure.DocumentationElement;
import pl.mpak.sky.gui.swing.syntax.structure.Expression;
import pl.mpak.sky.gui.swing.syntax.structure.HintElement;
import pl.mpak.sky.gui.swing.syntax.structure.KeywordElement;
import pl.mpak.sky.gui.swing.syntax.structure.NullElement;
import pl.mpak.sky.gui.swing.syntax.structure.NumericElement;
import pl.mpak.sky.gui.swing.syntax.structure.OperatorElement;
import pl.mpak.sky.gui.swing.syntax.structure.StarElement;
import pl.mpak.sky.gui.swing.syntax.structure.StringElement;
import pl.mpak.sky.gui.swing.syntax.structure.SymbolElement;
import pl.mpak.sky.gui.swing.syntax.structure.ValueElement;

public class SqlParser {

  private TokenCursor tc;
  
  public SqlParser(List<TokenRef> list) {
    tc = new TokenCursor(list, SQLSyntaxDocument.blankAllStyles);
  }
  
  private boolean breakKeyword() {
    return 
      tc.isToken(new String[] {
        "select", "values", "from", "where", "order", "having", "left", "right", 
        "inner", "full", "join", "cross", "group", "union", "minus", "except", 
        "intersect", "into"});
  }

  private boolean breakElement() {
    return tc.isToken(new String[] {",", "]", ")", ";"});
  }

  public DocumentationElement parseDocumentation(CodeElement owner) {
    DocumentationElement doc = new DocumentationElement(owner);
    doc.setStartOffset(tc.getStartOffset());
    
    while (tc.isStyle(SQLSyntaxDocument.documentationStyles)) {
      CodeElement v = new ValueElement(owner, tc.getString());
      doc.add(v);
      v.setStartOffset(tc.getStartOffset());
      tc.nextToken();
      v.setEndOffset(tc.getEndOffset());
    }
    
    doc.setEndOffset(tc.getEndOffset());
    return doc;
  }
  
  public CodeElement parseElement(CodeElement owner) {
    CodeElement result = null;
    int startOffset = tc.getStartOffset();
    
    if (tc.isToken(new String[] {"default"})) {
      result = new ValueElement(owner, tc.getString(), tc.getString());
      tc.nextToken();
    }
    else if (tc.isToken(new String[] {"true", "false", "yes", "no"})) {
      result = new BooleanElement(owner, tc.getString());
      tc.nextToken();
    }
    else if (tc.isToken("null")) {
      result = new NullElement(owner, tc.getString());
      tc.nextToken();
    }
    else if (tc.isToken(new String[] {
        "and", "or", "like", "ilike", "not", "is", "any", "all", "exists", "between", 
        "in", "distinct", "over", "case", "whene", "then", "else", "end", "as", "partition",
        "by", "rows", "range", "to", "on", "desc", "asc", "nulls", "last", "first", "when",
        "strict", "using", "start", "with", "connect", "recursive", "limit", "ordinality"}) ||
        (breakKeyword() && owner.getOwner(ExpressionList.class, new Class<?>[] {Select.class, Delete.class, Update.class, Insert.class}) != null)) {
      result = new KeywordElement(owner, tc.getString());
      tc.nextToken();
    }
    else if (tc.isStyle(SQLSyntaxDocument.OPERATOR)) {
      result = new OperatorElement(owner, tc.getString());
      tc.nextToken();
    }
    else if (tc.isStyle(SQLSyntaxDocument.COMMAND_PARAMETER)) {
      result = new BindElement(owner, tc.getString());
      tc.nextToken();
    }
    else if (tc.isStyle(SQLSyntaxDocument.STRING)) {
      result = new StringElement(owner, tc.getString());
      tc.nextToken();
    }
    else if (tc.isStyle(SQLSyntaxDocument.NUMERIC)) {
      result = new NumericElement(owner, tc.getString());
      tc.nextToken();
    }
    else if (tc.isToken("(")) {
      result = parseSubExpressionList(owner);
      if (tc.isToken(".")) {
        result = parseIdentifier(owner, result);
      }
    }
    else if (tc.isToken("[")) {
      result = parseExpressionArray(owner);
    }
    else if (tc.isStyle(SQLSyntaxDocument.HINT)) {
      result = new HintElement(owner, tc.getString());
      tc.nextToken();
    }
    else if (tc.isStyle(SQLSyntaxDocument.COMMENT)) {
      result = new CommentElement(owner, tc.getString());
      tc.nextToken();
    }
    else if (tc.isStyle(SQLSyntaxDocument.DOCUMENTATION)) {
      result = parseDocumentation(owner);
    }
    else if (!breakKeyword()) {
      result = parseIdentifier(owner, null);
    }
    
    if (result != null) {
      result.setStartOffset(startOffset);
      result.setEndOffset(tc.getEndOffset());
    }
    
    return result;
  }
  
  public CodeElement checkStar(CodeElementList<? extends CodeElement> list, CodeElement e) {
    if ((e instanceof OperatorElement && "*".equals(((OperatorElement)e).getOperator()) && list.size() == 0) || 
        e instanceof OperatorElement && "*".equals(((OperatorElement)e).getOperator()) && list.size() > 0 && list.get(list.size() -1) instanceof KeywordElement) {
      return new Identifier(list, new StarElement(null), e);
    }
    return e;
  }
  
  public SymbolElement parseSymbol(CodeElement owner) {
    SymbolElement result;
    if (tc.isToken("*")) {
      result = new StarElement(owner);
      result.setStartOffset(tc.getStartOffset());
    }
    else {
      result = new SymbolElement(owner);
      result.setStartOffset(tc.getStartOffset());
      result.setName(tc.getString());
      if (tc.token() != null) {
        result.setToken(tc.token().ref);
      }
    }
    tc.nextToken();
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public Identifier parseIdentifier(CodeElement owner, CodeElement firstElement) {
    Identifier result = new Identifier(owner);
    result.setStartOffset(tc.getStartOffset());
    
    if (firstElement != null) {
      firstElement.changeOwner(result);
      result.add(firstElement);
    }
    else {
      result.add(parseSymbol(result));
    }
    while (tc.isToken(".") && tc.token() != null) {
      tc.nextToken();
      if (breakKeyword() || breakElement() || tc.isToken(new String[] {"(", "["})) {
        result.add(new SymbolElement(result, "null"));
        break;
      }
      result.add(parseSymbol(result));
    }
    
    result.setEndOffset(tc.getEndOffset());
    return result;
  }

  public ExpressionArray parseExpressionArray(CodeElement owner) {
    ExpressionArray result = new ExpressionArray(owner);
    result.setStartOffset(tc.getStartOffset());
    
    tc.nextToken();

    if (!tc.isToken("]")) {
      result.add(parseExpression(result));
      while (tc.isToken(",")) {
        tc.nextToken();
        if (breakElement()) {
          result.add(new SymbolElement(result, "null"));
          break;
        }
        result.add(parseExpression(result));
      }
    }

    if (tc.isToken("]")) {
      tc.nextToken();
    }
    
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public ExpressionList parseSubExpressionList(CodeElement owner) {
    ExpressionList result = new ExpressionList(owner);
    result.setStartOffset(tc.getStartOffset());
    
    tc.nextToken();
    
    if (!tc.isToken(")")) {
      WithList wl = null;
      if (tc.isToken("with")) {
        wl = parseWithList(null);
      }
      if (tc.isToken("select")) {
        Select ce = parseSelect(result, wl);
        result.add(ce);
      }
      else if (tc.isToken("values")) {
        Values ce = parseValues(result, wl);
        result.add(ce);
      }
      else {
        result.add(parseExpression(result));
        while (tc.isToken(",")) {
          tc.nextToken();
          if (breakElement()) {
            result.add(new SymbolElement(result, "null"));
            break;
          }
          result.add(parseExpression(result));
        }
      }
    }

    if (tc.isToken(")")) {
      tc.nextToken();
    }
    
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public Into parseInto(CodeElement owner) {
    Into result = new Into(owner);
    result.setStartOffset(tc.getStartOffset());

    result.addKeyword(new KeywordElement(result, tc.getString()));
    tc.nextToken();
    
    result.add(parseExpression(result));
    while (tc.isToken(",")) {
      tc.nextToken();
      if (breakElement()) {
        result.add(new SymbolElement(result, "null"));
        break;
      }
      result.add(parseExpression(result));
    }
    
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public OrderBy parseOrderBy(CodeElement owner) {
    OrderBy result = new OrderBy(owner);
    result.setStartOffset(tc.getStartOffset());
    
    result.addKeyword(new KeywordElement(result, tc.getString()));
    tc.nextToken();
    if (tc.isToken("by")) {
      result.addKeyword(new KeywordElement(result, tc.getString()));
      tc.nextToken();
    }
    
    result.add(parseExpression(result));
    while (tc.isToken(",")) {
      tc.nextToken();
      if (breakElement()) {
        result.add(new SymbolElement(result, "null"));
        break;
      }
      result.add(parseExpression(result));
    }
    
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public GroupBy parseGroupBy(CodeElement owner) {
    GroupBy result = new GroupBy(owner);
    result.setStartOffset(tc.getStartOffset());

    result.addKeyword(new KeywordElement(result, tc.getString()));
    tc.nextToken();
    if (tc.isToken("by")) {
      result.addKeyword(new KeywordElement(result, tc.getString()));
      tc.nextToken();
    }
    
    result.add(parseExpression(result));
    while (tc.isToken(",")) {
      tc.nextToken();
      if (breakElement()) {
        result.add(new SymbolElement(result, "null"));
        break;
      }
      result.add(parseExpression(result));
    }
    
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public Having parseHaving(CodeElement owner) {
    Having result = new Having(owner);
    result.setStartOffset(tc.getStartOffset());
    
    result.addKeyword(new KeywordElement(result, tc.getString()));
    tc.nextToken();
    
    result.add(parseExpression(result));
    while (tc.isToken(",")) {
      tc.nextToken();
      if (breakElement()) {
        result.add(new SymbolElement(result, "null"));
        break;
      }
      result.add(parseExpression(result));
    }
    
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public With parseWith(CodeElement owner) {
    With result = new With(owner);
    result.setStartOffset(tc.getStartOffset());
    
    CodeElement e = parseElement(result);
    if (e != null) {
      result.add(e);
      while (tc.token() != null && !breakElement()) {
        e = parseElement(result);
        if (e != null) {
          result.add(e);
        }
        else {
          break;
        }
      }
    }

    result.updateInfo();
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public WithList parseWithList(CodeElement owner) {
    WithList result = new WithList(owner);
    result.setStartOffset(tc.getStartOffset());
    
    result.addKeyword(new KeywordElement(result, tc.getString()));
    tc.nextToken();
    
    result.add(parseWith(result));
    while (tc.isToken(",")) {
      tc.nextToken();
      if (breakElement()) {
        result.add(new SymbolElement(result, "null"));
        break;
      }
      result.add(parseWith(result));
    }
    
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public ValuesList parseValuesList(CodeElement owner) {
    ValuesList result = new ValuesList(owner);
    result.setStartOffset(tc.getStartOffset());
    
    result.add(parseExpression(result));
    while (tc.isToken(",")) {
      tc.nextToken();
      if (breakElement()) {
        result.add(new SymbolElement(result, "null"));
        break;
      }
      result.add(parseExpression(result));
    }
    
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public Values parseValues(CodeElement owner, WithList wl) {
    Values result = new Values(owner);
    
    if (wl != null) {
      wl.changeOwner(result);
      result.setWithList(wl);
    }
    
    result.setStartOffset(tc.getStartOffset());
    
    result.addKeyword(new KeywordElement(result, tc.getString()));
    tc.nextToken();
    
    result.setValues(parseValuesList(result));
    
    if (tc.isToken(new String[] {"union", "minus", "intersect", "except"})) {
      result.setUnion(parseUnion(result));
    }
    if (tc.isToken("order")) {
      result.setOrderBy(parseOrderBy(result));
    }
    
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public Where parseWhere(CodeElement owner) {
    Where result = new Where(owner);
    result.setStartOffset(tc.getStartOffset());
    
    result.addKeyword(new KeywordElement(result, tc.getString()));
    tc.nextToken();
    
    result.add(parseExpression(result));
    while (tc.isToken(",")) {
      tc.nextToken();
      if (breakElement()) {
        result.add(new SymbolElement(result, "null"));
        break;
      }
      result.add(parseExpression(result));
    }
    
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public Expression parseExpression(CodeElement owner) {
    Expression result = new Expression(owner);
    result.setStartOffset(tc.getStartOffset());
    
    CodeElement e = parseElement(result);
    if (e != null) {
      result.add(checkStar(result, e));
      while (tc.token() != null && !breakElement()) {
        e = parseElement(result);
        if (e != null) {
          result.add(checkStar(result, e));
        }
        else {
          break;
        }
      }
    }

    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public Column parseColumn(CodeElement owner) {
    Column result = new Column(owner);
    result.setStartOffset(tc.getStartOffset());
    
    CodeElement e = parseElement(result);
    if (e != null) {
      result.add(checkStar(result, e));
      while (tc.token() != null && !breakElement()) {
        e = parseElement(result);
        if (e != null) {
          result.add(checkStar(result, e));
        }
        else {
          break;
        }
      }
    }

    result.updateInfo();
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public ColumnList parseColumnList(CodeElement owner) {
    ColumnList result = new ColumnList(owner);
    result.setStartOffset(tc.getStartOffset());
    
    result.add(parseColumn(result));
    while (tc.isToken(",")) {
      tc.nextToken();
      if (breakKeyword() || breakElement()) {
        result.add(new SymbolElement(result, "null"));
        break;
      }
      result.add(parseColumn(result));
    }

    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public Table parseTable(CodeElement owner) {
    Table result = new Table(owner);
    result.setStartOffset(tc.getStartOffset());
    
    if (tc.isToken(",")) {
      tc.nextToken();
    }
    else if (tc.isToken(new String[] {"join", "inner", "left", "right", "full", "cross"})) {
      result.addKeyword(new KeywordElement(result, tc.getString()));
      tc.nextToken();
      if (tc.isToken(new String[] {"join", "outer"})) {
        result.addKeyword(new KeywordElement(result, tc.getString()));
        tc.nextToken();
      }
      if (tc.isToken(new String[] {"join"})) {
        result.addKeyword(new KeywordElement(result, tc.getString()));
        tc.nextToken();
      }
    }

    CodeElement e = parseElement(result);
    if (e != null) {
      result.add(e);
      while (tc.token() != null && !breakElement()) {
        e = parseElement(result);
        if (e != null) {
          result.add(e);
        }
        else {
          break;
        }
      }
    }

    result.updateInfo();
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public TableList parseTableList(CodeElement owner) {
    TableList result = new TableList(owner);
    result.setStartOffset(tc.getStartOffset());

    result.addKeyword(new KeywordElement(result, tc.getString()));
    tc.nextToken();
    
    result.add(parseTable(result));
    while (tc.isToken(",") || tc.isToken(new String[] {"left", "right", "join", "full", "inner"})) {
      result.add(parseTable(result));
    }

    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public Union parseUnion(CodeElement owner) {
    Union result = new Union(owner);
    result.setStartOffset(tc.getStartOffset());

    if (tc.isToken(new String[] {"union", "intersect", "minus", "except"})) {
      result.addKeyword(new KeywordElement(result, tc.getString()));
      tc.nextToken();
      if (tc.isToken("all")) {
        result.addKeyword(new KeywordElement(result, tc.getString()));
        tc.nextToken();
      }
    }
    if (tc.isToken("select")) {
      result.setElement(parseSelect(result, null));
    }
    else if (tc.isToken("values")) {
      result.setElement(parseValues(result, null));
    }
    
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public Select parseSelect(CodeElement owner, WithList wl) {
    Select result = new Select(owner);
    
    if (wl != null) {
      wl.changeOwner(result);
      result.setWithList(wl);
    }
    
    result.setStartOffset(tc.getStartOffset());

    result.addKeyword(new KeywordElement(result, tc.getString()));
    tc.nextToken();
    result.setColumnList(parseColumnList(result));
    if (tc.isToken("into")) {
      result.setInto(parseInto(result));
    }
    if (tc.isToken("from")) {
      result.setTableList(parseTableList(result));
    }
    if (tc.isToken("where")) {
      result.setWhere(parseWhere(result));
    }
    if (tc.isToken("group")) {
      result.setGroupBy(parseGroupBy(result));
    }
    if (tc.isToken("having")) {
      result.setHaving(parseHaving(result));
    }
    if (tc.isToken(new String[] {"union", "minus", "intersect", "except"})) {
      result.setUnion(parseUnion(result));
    }
    if (tc.isToken("order")) {
      result.setOrderBy(parseOrderBy(result));
    }
    
    result.updateInfo();
    result.setEndOffset(tc.getEndOffset());
    return result;
  }
  
  public ArrayList<CodeElement> skipComments() {
    ArrayList<CodeElement> cmnts = new ArrayList<CodeElement>();
    while (tc.isStyle(SQLSyntaxDocument.commentStyles)) {
      CodeElement result = null;
      if (tc.isStyle(SQLSyntaxDocument.HINT)) {
        result = new HintElement(null, tc.getString());
        tc.nextToken();
      }
      else if (tc.isStyle(SQLSyntaxDocument.COMMENT)) {
        result = new CommentElement(null, tc.getString());
        tc.nextToken();
      }
      else if (tc.isStyle(SQLSyntaxDocument.DOCUMENTATION)) {
        result = parseDocumentation(null);
      }
      if (result != null) {
        cmnts.add(result);
      }
    }
    if (cmnts.size() == 0) {
      return null;
    }
    return cmnts;
  }
  
  public CodeElement parse() {
    WithList wl = null;
    CodeElement ce = null;
    if (tc.isToken("with")) {
      wl = parseWithList(null);
    }
    if (tc.isToken("select")) {
      ce = parseSelect(null, wl);
    }
    else if (tc.isToken("values")) {
      ce = parseValues(null, wl);
    }
    else if (wl != null) {
      ce = wl;
    }
    if (tc.token() != null) {
      Expression e = new Expression(null);
      e.add(ce);
      if (ce != null) {
        ce.changeOwner(e);
      }
      while (tc.token() != null) {
        CodeElement pe = parseElement(e);
        if (pe != null) {
          e.add(pe);
        }
        else if (breakKeyword()) {
          e.add(new KeywordElement(e, tc.getString()));
          tc.nextToken();
        }
      }
      return e;
    }
    return ce;
  }

}
