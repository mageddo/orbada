/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.firebird.syntax.parser;

import java.util.ArrayList;
import pl.mpak.sky.gui.swing.syntax.SQLSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.structure.BlockElement;
import pl.mpak.sky.gui.swing.syntax.structure.CallableElement;
import pl.mpak.sky.gui.swing.syntax.structure.CallableParameter;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.Condition;
import pl.mpak.sky.gui.swing.syntax.structure.Cursor;
import pl.mpak.sky.gui.swing.syntax.structure.DeclaredElement;
import pl.mpak.sky.gui.swing.syntax.structure.Else;
import pl.mpak.sky.gui.swing.syntax.structure.Expression;
import pl.mpak.sky.gui.swing.syntax.structure.For;
import pl.mpak.sky.gui.swing.syntax.structure.GotoLabel;
import pl.mpak.sky.gui.swing.syntax.structure.If;
import pl.mpak.sky.gui.swing.syntax.structure.ParserException;
import pl.mpak.sky.gui.swing.syntax.structure.Procedure;
import pl.mpak.sky.gui.swing.syntax.structure.SqlStructureParser;
import pl.mpak.sky.gui.swing.syntax.structure.Statement;
import pl.mpak.sky.gui.swing.syntax.structure.Trigger;
import pl.mpak.sky.gui.swing.syntax.structure.Variable;
import pl.mpak.sky.gui.swing.syntax.structure.While;

/**
 *
 * @author akaluza
 */
public class FirebirdPSqlStructureParser extends SqlStructureParser {

  public FirebirdPSqlStructureParser() {
  }

  @Override
  public BlockElement parse() throws ParserException {
    BlockElement block = null;
    try {
      while (nextToken(true) != null) {
        if (isToken("procedure")) {
          block = new Procedure(null);
          parseCallable((Procedure)block);
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
    StringBuffer sb = new StringBuffer();
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
    param.setType(parseDeclareType());
    param.setEndOffset(getEndOffset());
    return param;
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

  private BlockElement parseCallable(CallableElement block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    block.setName(parseIdentifier());
    skipBlank();
    if (isToken("(")) {
      parseParameters(block, block.getParameterList());
    }
    if (isToken("returns")) {
      nextToken(true);
      if (isToken("(")) {
        parseParameters(block, block.getParameterList());
      }
    }
    while (!isToken("as") && !isToken("is")) {
      nextToken(true);
    }
    nextToken(true);
    parseDeclares(block);
    block.setBeginBlockOffset(getStartOffset());
    parseBlock(block);
    if (isToken(block.getName())) {
      nextToken(true);
    }
    block.setEndOffset(getEndOffset());
    return block;
  }

  private BlockElement parseTrigger(Trigger trigger) throws ParserException {
    trigger.setStartOffset(getStartOffset());
    nextToken(true);
    trigger.setName(parseIdentifier());
    nextToken(true);
    while (!isToken("as") && !isToken("is")) {
      nextToken(true);
    }
    nextToken(true);
    parseDeclares(trigger);
    trigger.setBeginBlockOffset(getStartOffset());
    parseBlock(trigger);
    if (isToken(trigger.getName())) {
      nextToken(true);
    }
    trigger.setEndOffset(getEndOffset());
    return trigger;
  }

  private String parseDeclareType() throws ParserException {
    StringBuffer sb = new StringBuffer();
    while (!isToken(")") && !isToken(",") && !isToken(";")) {
      if (isToken("(")) {
        sb.append(getTokenString());
        nextToken(false);
        while (!isToken(")")) {
          sb.append(getTokenString());
          nextToken(false);
        }
        sb.append(getTokenString());
      }
      else if (getToken().styleId != SQLSyntaxDocument.COMMENT && getToken().styleId != SQLSyntaxDocument.DOCUMENTATION) {
        sb.append(getTokenString());
      }
      else {
        sb.append("");
      }
      nextToken(false);
    }
    return sb.toString().trim();
  }

  private DeclaredElement parseDeclaration(int startOffset, String name, BlockElement block) throws ParserException {
    skipBlank();
    DeclaredElement declare = new Variable(block);
    declare.setName(name);
    declare.setStartOffset(startOffset);
    declare.setType(parseDeclareType());
    declare.setEndOffset(getEndOffset());
    return declare;
  }

  private Cursor parseCursor(int startOffset, String name, Cursor block) throws ParserException {
    skipBlank();
    if (isToken("for")) {
      nextToken(true);
    }
    block.setName(name);
    block.setStartOffset(startOffset);
    while (!isToken(";")) {
      nextToken(true);
    }
    block.setEndOffset(getEndOffset());
    return block;
  }

  private void declareElement(BlockElement block) throws ParserException {
    skipBlank();
    int startOffset = getStartOffset();
    if (isToken("declare")) {
      nextToken(true);
    }
    if (isToken("variable")) {
      nextToken(true);
    }
    String name = getTokenString();
    putKeyWord(name, SQLSyntaxDocument.LOCAL_IDENTIFIER);
    nextToken(true);
    if (isToken("cursor")) {
      block.putCallable(parseCursor(startOffset, name, new Cursor(block)));
    }
    else {
      block.putDeclare(parseDeclaration(startOffset, name, block));
    }
    if (isToken(";")) {
      nextToken(true);
    }
  }

  private void parseDeclares(BlockElement block) throws ParserException {
    if (!isToken("begin")) {
      declareElement(block);
      while (!isToken("begin")) {
        declareElement(block);
      }
    }
  }

  private void parseStatement(BlockElement block) throws ParserException {
    skipBlank();
    if (isToken("if")) {
      block.putCode(parseIf(new If(block)));
    }
    else if (isToken("for")) {
      block.putCode(parseFor(new For(block)));
    }
    else if (isToken("while")) {
      block.putCode(parseWhile(new While(block)));
    }
    else {
      int startOffset = getStartOffset();
      String labelName = getTokenString();
      nextToken(true);
      if (isToken(":")) {
        GotoLabel label = new GotoLabel(block);
        label.setName(labelName);
        putKeyWord(labelName, SQLSyntaxDocument.LOCAL_IDENTIFIER);
        label.setStartOffset(startOffset);
        label.setEndOffset(getEndOffset());
        block.putCode(label);
      }
      else {
        Statement statement = new Statement(block);
        statement.setStartOffset(startOffset);
        while (!isToken(";")) {
          nextToken(true);
        }
        statement.setEndOffset(getEndOffset());
        block.putCode(statement);
      }
      nextToken(false);
    }
  }

  private If parseIf(If block) throws ParserException {
    block.setStartOffset(getStartOffset());
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
    nextToken(true);
    if (isToken("begin")) {
      BlockElement bt = parseBlock(new BlockElement(block, "Block"));
      block.putCode(bt);
      block.setEndOffset(bt.getEndOffset());
    }
    else {
      parseStatement(block);
      if (block.getCodeList().size() > 0) {
        block.setEndOffset(block.getCodeList().get(block.getCodeList().size() -1).getEndOffset());
      }
      else {
        block.setEndOffset(getEndOffset());
      }
    }
    skipBlank();
    if (isToken("else")) {
      Else eb = new Else(block);
      eb.setStartOffset(getStartOffset());
      nextToken(true);
      if (isToken("begin")) {
        BlockElement bt = parseBlock(new BlockElement(eb, "Block"));
        eb.putCode(bt);
        eb.setEndOffset(bt.getEndOffset());
      }
      else {
        parseStatement(eb);
        if (block.getCodeList().size() > 0) {
          eb.setEndOffset(eb.getCodeList().get(eb.getCodeList().size() -1).getEndOffset());
        }
        else {
          eb.setEndOffset(getEndOffset());
        }
      }
      block.setElseBlock(eb);
      block.setEndOffset(eb.getEndOffset());
      skipBlank();
    }
    return block;
  }

  private For parseFor(For block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    Expression expr = null;
    while (!isToken("do")) {
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
    nextToken(true);
    if (isToken("begin")) {
      BlockElement bt = parseBlock(new BlockElement(block, "Block"));
      block.putCode(bt);
      block.setEndOffset(bt.getEndOffset());
    }
    else {
      parseStatement(block);
      if (block.getCodeList().size() > 0) {
        block.setEndOffset(block.getCodeList().get(block.getCodeList().size() -1).getEndOffset());
      }
      else {
        block.setEndOffset(getEndOffset());
      }
    }
    skipBlank();
    return block;
  }

  private While parseWhile(While block) throws ParserException {
    block.setStartOffset(getStartOffset());
    nextToken(true);
    Expression expr = null;
    while (!isToken("do")) {
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
    nextToken(true);
    if (isToken("begin")) {
      BlockElement bt = parseBlock(new BlockElement(block, "Block"));
      block.putCode(bt);
      block.setEndOffset(bt.getEndOffset());
    }
    else {
      parseStatement(block);
      if (block.getCodeList().size() > 0) {
        block.setEndOffset(block.getCodeList().get(block.getCodeList().size() -1).getEndOffset());
      }
      else {
        block.setEndOffset(getEndOffset());
      }
    }
    skipBlank();
    return block;
  }

  private BlockElement parseBlockStatements(BlockElement block) throws ParserException {
    skipBlank();
    while (!isToken("end")) {
      if (isToken("if")) {
        block.putCode(parseIf(new If(block)));
      }
      else if (isToken("for")) {
        block.putCode(parseFor(new For(block)));
      }
      else if (isToken("while")) {
        block.putCode(parseWhile(new While(block)));
      }
      else if (isToken("begin")) {
        block.putCode(parseBlock(new BlockElement(block, "Block")));
      }
      else {
        parseStatement(block);
        skipBlank();
      }
    }
    nextToken(false);
    block.setEndOffset(getEndOffset());
    skipBlank();
    return block;
  }

  private BlockElement parseBlock(BlockElement block) throws ParserException {
    skipBlank();
    if (block.getStartOffset() == 0) {
      block.setStartOffset(getStartOffset());
    }
    nextToken(true);
    if (!isToken("end")) {
      parseBlockStatements(block);
    }
    if (block.getEndOffset() == 0) {
      block.setEndOffset(getEndOffset());
    }
    return block;
  }

}
