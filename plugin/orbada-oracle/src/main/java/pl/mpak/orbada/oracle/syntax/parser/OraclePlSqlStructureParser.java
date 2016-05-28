package pl.mpak.orbada.oracle.syntax.parser;

import java.util.ArrayList;

import pl.mpak.sky.gui.swing.syntax.SQLSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.structure.BlockElement;
import pl.mpak.sky.gui.swing.syntax.structure.CallableElement;
import pl.mpak.sky.gui.swing.syntax.structure.CallableParameter;
import pl.mpak.sky.gui.swing.syntax.structure.CaseBlock;
import pl.mpak.sky.gui.swing.syntax.structure.CaseElse;
import pl.mpak.sky.gui.swing.syntax.structure.CaseWhen;
import pl.mpak.sky.gui.swing.syntax.structure.CatchException;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.Condition;
import pl.mpak.sky.gui.swing.syntax.structure.Constant;
import pl.mpak.sky.gui.swing.syntax.structure.Cursor;
import pl.mpak.sky.gui.swing.syntax.structure.DeclaredElement;
import pl.mpak.sky.gui.swing.syntax.structure.Else;
import pl.mpak.sky.gui.swing.syntax.structure.ExceptionBlock;
import pl.mpak.sky.gui.swing.syntax.structure.Expression;
import pl.mpak.sky.gui.swing.syntax.structure.Exception;
import pl.mpak.sky.gui.swing.syntax.structure.For;
import pl.mpak.sky.gui.swing.syntax.structure.Function;
import pl.mpak.sky.gui.swing.syntax.structure.GotoLabel;
import pl.mpak.sky.gui.swing.syntax.structure.If;
import pl.mpak.sky.gui.swing.syntax.structure.Loop;
import pl.mpak.sky.gui.swing.syntax.structure.ParserException;
import pl.mpak.sky.gui.swing.syntax.structure.Procedure;
import pl.mpak.sky.gui.swing.syntax.structure.SqlStructureParser;
import pl.mpak.sky.gui.swing.syntax.structure.Statement;
import pl.mpak.sky.gui.swing.syntax.structure.Trigger;
import pl.mpak.sky.gui.swing.syntax.structure.Type;
import pl.mpak.sky.gui.swing.syntax.structure.Variable;
import pl.mpak.sky.gui.swing.syntax.structure.While;

public class OraclePlSqlStructureParser extends SqlStructureParser {

  public OraclePlSqlStructureParser() {
  }

  /**
   * <p>G³ówna funkcja do parsowania
   * @return
   * @throws ParserException
   */
  public BlockElement parse() {
    BlockElement block = null;
    try {
      while (nextToken(true) != null) {
        if (isToken("function")) {
          block = new Function(null);
          parseCallable((Function)block);
          break;
        }
        else if (isToken("procedure")) {
          block = new Procedure(null);
          parseCallable((Procedure)block);
          break;
        }
        else if (isToken("package")) {
          block = new Package(null);
          parsePackage((Package)block);
          break;
        }
        else if (isToken("type")) {
          block = new TypeObject(null);
          parseTypeObject((TypeObject)block);
          break;
        }
        else if (isToken("trigger")) {
          block = new Trigger(null);
          parseTrigger((Trigger)block);
          break;
        }
      }
    }
    catch (ParserException ex) {
    }
    finally {
      updateKeyWordStyles();
    }
    return block;
  }
  
  private String parseIdentifier() throws ParserException {
    StringBuilder sb = new StringBuilder();
    sb.append(getTokenString());
    putKeyWord(getTokenString(), SQLSyntaxDocument.USER_FUNCTION);
    nextToken(true);
    while (isToken(".")) {
      sb.append(getTokenString());
      nextToken(true);
      sb.append(getTokenString());
      putKeyWord(getTokenString(), SQLSyntaxDocument.USER_FUNCTION);
      nextToken(true);
    }
    return sb.toString();
  }
  
  private DeclaredElement parseParameter(CodeElement callable) throws ParserException {
    skipBlank();
    DeclaredElement param = new CallableParameter(callable);
    param.setStartOffset(getStartOffset());
    param.setName(getTokenString());
    putKeyWord(getTokenString(), SQLSyntaxDocument.LOCAL_IDENTIFIER);
    nextToken(true);
    StringBuilder sb = new StringBuilder();
    int brackets = 0;
    while ((!isToken(")") && !isToken(",")) || brackets > 0) {
      if (getToken().styleId != SQLSyntaxDocument.COMMENT && getToken().styleId != SQLSyntaxDocument.DOCUMENTATION) {
        if (isToken("(")) {
          brackets++;
        }
        else if (isToken(")")) {
          brackets--;
        }
        String t = getTokenString();
        if (!t.trim().equals(t) && t.trim().length() == 0) {
          t = " ";
        }
        sb.append(t);
      }
      if (nextToken() == null) {
        break;
      }
    }
    param.setType(sb.toString().trim());
    param.setEndOffset(getEndOffset());
    return param;
  }
  
  private DeclaredElement parseDeclaration(BlockElement block) throws ParserException {
    skipBlank();
    int start = getStartOffset();
    String name = getTokenString();
    nextToken(true);
    DeclaredElement declare;
    if (isToken("constant")) {
      putKeyWord(name, SQLSyntaxDocument.LOCAL_IDENTIFIER);
      declare = new Constant(block);
      nextToken(true);
    }
    else if (isToken("exception")) {
      putKeyWord(name, SQLSyntaxDocument.ERROR);
      declare = new Exception(block);
      nextToken(true);
    }
    else {
      putKeyWord(name, SQLSyntaxDocument.LOCAL_IDENTIFIER);
      declare = new Variable(block);
    }
    declare.setStartOffset(start);
    declare.setName(name);
    StringBuilder sb = new StringBuilder();
    int roundedParenth = 0;
    int quadParenth = 0;
    while (!isToken(";")) {
      if (getToken().styleId != SQLSyntaxDocument.COMMENT && getToken().styleId != SQLSyntaxDocument.DOCUMENTATION) {
        sb.append(getTokenString());
      }
      else {
        sb.append(" ");
      }
      if (isToken("(")) {
        roundedParenth++;
      }
      else if (isToken(")")) {
        roundedParenth--;
      }
      else if (isToken("[")) {
        quadParenth++;
      }
      else if (isToken("]")) {
        quadParenth--;
      }
      nextToken();
      if (isToken(",") && roundedParenth == 0 && quadParenth == 0) {
        break;
      }
    }
    declare.setType(sb.toString().trim());
    declare.setEndOffset(getEndOffset());
    return declare;
  }
  
  private DeclaredElement parsePragma(BlockElement block) throws ParserException {
    nextToken(true);
    DeclaredElement declare = new Pragma(block);
    declare.setStartOffset(getStartOffset());
    declare.setName(getTokenString());
    nextToken(true);
    StringBuilder sb = new StringBuilder();
    int roundedParenth = 0;
    while (!isToken(";")) {
      if (getToken().styleId != SQLSyntaxDocument.COMMENT && getToken().styleId != SQLSyntaxDocument.DOCUMENTATION) {
        sb.append(getTokenString());
      }
      else {
        sb.append(" ");
      }
      if (isToken("(")) {
        roundedParenth++;
      }
      else if (isToken(")")) {
        roundedParenth--;
      }
      nextToken(true);
      if (isToken(",") && roundedParenth == 0) {
        break;
      }
    }
    declare.setType(sb.toString().trim());
    declare.setEndOffset(getEndOffset());
    return declare;
  }
  
  private void declareElement(BlockElement block) throws ParserException {
    if (isToken("cursor")) {
      block.putCallable(parseCursor(new Cursor(block)));
    }
    else if (isToken("function")) {
      block.putCallable(parseCallable(new Function(block)));
    }
    else if (isToken("procedure")) {
      block.putCallable(parseCallable(new Procedure(block)));
    }
    else if (isToken("constructor") || isToken("member") || isToken("static")) {
      nextToken(true);
      if (isToken("function")) {
        block.putCallable(parseCallable(new Function(block)));
      }
      else if (isToken("procedure")) {
        block.putCallable(parseCallable(new Procedure(block)));
      }
    }
    else if (isToken("type")) {
      block.putDeclare(parseType(new Type(block)));
    }
    else if (isToken("pragma")) {
      block.putDeclare(parsePragma(block));
    }
    else {
      block.putDeclare(parseDeclaration(block));
    }
    if (isToken(";") || isToken(",")) {
      nextToken(true);
    }
  }
  
  private void parseDeclares(BlockElement block) throws ParserException {
    if (!isToken("begin") && !isToken("end")) {
      declareElement(block);
      while (!isToken("begin") && !isToken("end") && !isToken(")")) {
        declareElement(block);
      }
    }
  }

  private void parseParameters(CodeElement block, ArrayList<DeclaredElement> parameterList) throws ParserException {
    nextToken(true);
    if (!isToken(")")) {
      parameterList.add(parseParameter(block));
      while (isToken(",")) {
        nextToken(true);
        parameterList.add(parseParameter(block));
      }
      nextToken(true);
    }
  }
  
  private If parseIf(If block) throws ParserException {
    block.setStartOffset(getEndOffset());
    nextToken(true);
    Condition cond = null;
    while (!isToken("then")) {
      if (cond == null) {
        cond = new Condition(block);
        cond.setStartOffset(getStartOffset());
      }
      nextToken(true);
    }
    if (cond != null) {
      block.setCondition(cond);
      cond.setEndOffset(getEndOffset());
    }
    parseBlockStatements(block);
    while (isToken("elsif") || isToken("else")) {
      Else eb = new Else(block);
      eb.setStartOffset(getStartOffset());
      if (isToken("elsif")) {
        block.setElseBlock(eb);
        block.getElseBlock().getCodeList().add(parseIf(new If(block.getElseBlock())));
        eb.setEndOffset(getEndOffset());
        block.setEndOffset(getEndOffset());
        return block;
      }
      else {
        block.setElseBlock((Else)parseBlockStatements(eb));
        eb.setEndOffset(getEndOffset());
      }
    }
    nextToken(true);
    if (isToken("if")) {
      nextToken(true);
    }
    block.setEndOffset(getEndOffset());
    return block;
  }
  
  private For parseFor(For block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    DeclaredElement declare = new Variable(block);
    declare.setStartOffset(getStartOffset());
    declare.setName(getTokenString());
    putKeyWord(getTokenString(), SQLSyntaxDocument.LOCAL_IDENTIFIER);
    nextToken(true);
    declare.setEndOffset(getEndOffset());
    block.putDeclare(declare);
    if (isToken("in")) {
      nextToken(true);
    }
    Expression expr = null;
    while (!isToken("loop")) {
      if (expr == null) {
        expr = new Expression(block);
        expr.setStartOffset(getStartOffset());
      }
      nextToken(true);
    }
    if (expr != null) {
      block.setExpression(expr);
      expr.setEndOffset(getEndOffset());
    }
    parseBlockStatements(block);
    nextToken(true);
    if (isToken("loop")) {
      nextToken(true);
    }
    block.setEndOffset(getEndOffset());
    return block;
  }
  
  private While parseWhile(While block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    Expression expr = null;
    while (!isToken("loop")) {
      if (expr == null) {
        expr = new Expression(block);
        expr.setStartOffset(getStartOffset());
      }
      nextToken(true);
    }
    if (expr != null) {
      block.setExpression(expr);
      expr.setEndOffset(getEndOffset());
    }
    parseBlockStatements(block);
    nextToken(true);
    if (isToken("loop")) {
      nextToken(true);
    }
    block.setEndOffset(getEndOffset());
    return block;
  }
  
  private Loop parseLoop(Loop block) throws ParserException {
    block.setStartOffset(getStartOffset());
    parseBlockStatements(block);
    nextToken(true);
    if (isToken("loop")) {
      nextToken(true);
    }
    block.setEndOffset(getEndOffset());
    return block;
  }
  
  private CatchException parseCatchException(CatchException block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    Expression expr = null;
    while (!isToken("then")) {
      if (expr == null) {
        expr = new Expression(block);
        expr.setStartOffset(getStartOffset());
      }
      nextToken(true);
    }
    if (expr != null) {
      block.setExpression(expr);
      expr.setEndOffset(getEndOffset());
    }
    parseBlockStatements(block);
    block.setEndOffset(getEndOffset());
    return block;
  }
  
  private ExceptionBlock parseException(ExceptionBlock block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    while (isToken("when")) {
      block.getCatchList().add(parseCatchException(new CatchException(block)));
    }
    block.setEndOffset(getEndOffset());
    return block;
  }
  
  private CaseWhen parseCaseWhen(CaseWhen block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    Expression expr = null;
    while (!isToken("then")) {
      if (expr == null) {
        expr = new Expression(block);
        expr.setStartOffset(getStartOffset());
      }
      nextToken(true);
    }
    if (expr != null) {
      block.setExpression(expr);
      expr.setEndOffset(getEndOffset());
    }
    parseBlockStatements(block);
    block.setEndOffset(getEndOffset());
    return block;
  }
  
  private CaseElse parseCaseElse(CaseElse block) throws ParserException {
    block.setStartOffset(getStartOffset());
    parseBlockStatements(block);
    block.setEndOffset(getEndOffset());
    return block;
  }
  
  private CaseBlock parseCase(CaseBlock block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    Expression expr = null;
    while (!isToken("when")) {
      if (expr == null) {
        expr = new Expression(block);
        expr.setStartOffset(getStartOffset());
      }
      nextToken(true);
    }
    if (expr != null) {
      block.setExpression(expr);
      expr.setEndOffset(getEndOffset());
    }
    while (isToken("when")) {
      block.getWhenList().add(parseCaseWhen(new CaseWhen(block)));
    }
    if (isToken("else")) {
      block.setElseBlock(parseCaseElse(new CaseElse(block)));
    }
    nextToken(true);
    if (isToken("case")) {
      nextToken(true);
    }
    block.setEndOffset(getEndOffset());
    return block;
  }
  
  private Statement parseStatement(BlockElement block, Statement statement) throws ParserException {
    statement.setStartOffset(getStartOffset());
    while (!isToken(";")) {
      nextToken(true);
    }
    statement.setEndOffset(getEndOffset());
    return statement;
  }
  
  private GotoLabel parseGotoLabel(GotoLabel block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    block.setName(getTokenString());
    putKeyWord(getTokenString(), SQLSyntaxDocument.LOCAL_IDENTIFIER);
    nextToken(true);
    if (isToken(">>")) {
      block.setEndOffset(getEndOffset() +2);
    }
    else {
      block.setEndOffset(getEndOffset());
    }
    return block;
  }
  
  private BlockElement parseBlockStatements(BlockElement block) throws ParserException {
    while (nextToken(true) != null) {
      if (isToken("else")) {
        return block;
      }
      else if (isToken("elsif")) {
        return block;
      }
      else if (isToken("end")) {
        return block;
      }
      else if (isToken("when")) {
        return block;
      }
      else if (isToken("if")) {
        block.putCode(parseIf(new If(block)));
      }
      else if (isToken("for")) {
        block.putCode(parseFor(new For(block)));
      }
      else if (isToken("while")) {
        block.putCode(parseWhile(new While(block)));
      }
      else if (isToken("loop")) {
        block.putCode(parseLoop(new Loop(block)));
      }
      else if (isToken("case")) {
        block.putCode(parseCase(new CaseBlock(block)));
      }
      else if (isToken("declare") || isToken("begin")) {
        block.putCode(parseBlockCode(new BlockElement(block, "Block")));
      }
      else if (isToken("<<")) {
        block.putCode(parseGotoLabel(new GotoLabel(block)));
      }
      else if (isToken("exception")) {
        return block;
      }
      else {
        block.putCode(parseStatement(block, new Statement(block)));
      }
    }
    return block;
  }
  
  private BlockElement parseBlockCode(BlockElement block) throws ParserException {
    block.setStartOffset(getStartOffset());
    parseBlock(block);
    block.setEndOffset(getEndOffset());
    return block;
  }
  
  private BlockElement parseBlock(BlockElement block) throws ParserException {
    skipBlank();
    if (isToken("declare")) {
      nextToken(true);
    }
    parseDeclares(block);
    block.setBeginBlockOffset(getStartOffset());
    if (!isToken("end") && !isToken(")")) {
      parseBlockStatements(block);
      if (isToken("exception")) {
        block.setExceptionBlock(parseException(new ExceptionBlock(block)));
      }
      nextToken(true);
    }
    block.setEndOffset(getEndOffset());
    return block;
  }

  private CallableElement parseCallable(CallableElement block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    block.setName(parseIdentifier());
    skipBlank();
    if (isToken("(")) {
      parseParameters(block, block.getParameterList());
    }
    if (isToken("return")) {
      nextToken(true);
      DeclaredElement param = new CallableParameter(block);
      param.setStartOffset(getStartOffset());
      if (isToken("self")) {
        // for function (constructor) in type obejct
        param.setName(getTokenString());
        nextToken(true);
        if (isToken("as")) {
          nextToken(true);
        }
      }
      StringBuilder sb = new StringBuilder();
      while (!isToken("is") && !isToken("as") && !isToken(";") && !isToken(",")) {
        if (getToken().styleId != SQLSyntaxDocument.COMMENT && getToken().styleId != SQLSyntaxDocument.DOCUMENTATION) {
          sb.append(getTokenString());
        }
        else {
          sb.append("");
        }
        nextToken(true);
      }
      param.setType(sb.toString().trim());
      param.setEndOffset(getEndOffset());
      block.getParameterList().add(param);
    }
    if (!isToken(";") && !isToken(",")) {
      while (!isToken("as") && !isToken("is")) {
        nextToken(true);
      }
      nextToken(true);
      parseBlock(block);
      if (isToken(block.getName())) {
        nextToken(true);
      }
    }
    block.setEndOffset(getEndOffset());
    return block;
  }

  private Trigger parseTrigger(Trigger block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    block.setName(parseIdentifier());
    while (!isToken("begin") && !isToken("declare")) {
      nextToken(true);
    }
    parseBlock(block);
    if (isToken(block.getName())) {
      nextToken(true);
    }
    block.setEndOffset(getEndOffset());
    return block;
  }

  private Cursor parseCursor(Cursor block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    block.setName(getTokenString());
    putKeyWord(getTokenString(), SQLSyntaxDocument.USER_FUNCTION);
    nextToken(true);
    if (isToken("(")) {
      parseParameters(block, block.getParameterList());
      nextToken(true);
    }
    nextToken(true);
    while (!isToken(";")) {
      nextToken(true);
    }
    block.setEndOffset(getEndOffset());
    return block;
  }

  private Type parseType(Type block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    block.setName(getTokenString());
    putKeyWord(getTokenString(), SQLSyntaxDocument.DATA_TYPE);
    nextToken(true);
    int roundedParenth = 0;
    while (!isToken(";")) {
      if (isToken("(")) {
        roundedParenth++;
      }
      else if (isToken(")")) {
        roundedParenth--;
      }
      nextToken(true);
      if (isToken(",") && roundedParenth == 0) {
        break;
      }
    }
    block.setEndOffset(getEndOffset());
    return block;
  }

  private BlockElement parsePackage(Package block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    if (isToken("body")) {
      nextToken(true);
    }
    block.setName(parseIdentifier());
    while (!isToken("as") && !isToken("is")) {
      nextToken(true);
    }
    nextToken(true);
    parseBlock(block);
    if (isToken(block.getName())) {
      nextToken(true);
    }
    block.setEndOffset(getEndOffset());
    return block;
  }

  private BlockElement parseTypeObject(TypeObject block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    if (isToken("body")) {
      nextToken(true);
    }
    block.setName(parseIdentifier());
    putKeyWord(getTokenString(), SQLSyntaxDocument.DATA_TYPE);
    while (!isToken("as") && !isToken("is")) {
      nextToken(true);
    }
    nextToken(true);
    if (isToken("object")) {
      nextToken(true);
    }
    if (isToken("(")) {
      nextToken(true);
      parseBlock(block);
      if (isToken(")")) {
        nextToken(true);
      }
    }
    else {
      parseBlock(block);
    }
    block.setEndOffset(getEndOffset());
    return block;
  }

}
