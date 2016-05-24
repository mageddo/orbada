/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import pl.mpak.sky.gui.swing.syntax.SQLSyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor.TokenRef;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;

/**
 *
 * @author akaluza
 */
public class OracleUtil {

  public static SyntaxDocument.LineMark[] getErrLines(Query errorQuery, Color bkColor, ImageIcon errIcon) {
    ArrayList<SyntaxDocument.LineMark> lineList = new ArrayList<SyntaxDocument.LineMark>();
    try {
      errorQuery.first();
      while (!errorQuery.eof()) {
        lineList.add(new SyntaxDocument.LineMark(errorQuery.fieldByName("line").getInteger(), bkColor, errIcon, errorQuery.fieldByName("text").getString()));
        errorQuery.next();
      }
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    return lineList.toArray(new SyntaxDocument.LineMark[lineList.size()]);
  }
  
  public static Icon getObjectIcon(String objectType) {
    if ("TRIGGER".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trigger.gif");
    }
    else if ("FUNCTION".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/function.gif");
    }
    else if ("JAVA CLASS".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/java_class.gif");
    }
    else if ("JAVA SOURCE".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/java_source.gif");
    }
    else if ("MATERIALIZED VIEW".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/mview.gif");
    }
    else if ("PACKAGE".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/package.gif");
    }
    else if ("PACKAGE BODY".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/package.gif");
    }
    else if ("PROCEDURE".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/procedure.gif");
    }
    else if ("TABLE".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/table.gif");
    }
    else if ("TYPE".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/type.gif");
    }
    else if ("VIEW".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/view.gif");
    }
    else if ("DATABASE LINK".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_link.gif");
    }
    else if ("SYNONYM".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/synonym.gif");
    }
    else if ("DIRECTORY".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/directory.gif");
    }
    else if ("SEQUENCE".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/sequence.gif");
    }
    else if ("INDEX".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/index.gif");
    }
    else if ("COLUMN".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/column.gif");
    }
    else if ("ATTRIBUTE".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/member.gif");
    }
    else if ("MEMBER".equalsIgnoreCase(objectType)) {
      return pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/member.gif");
    }
    else {
      return null;
    }
  }

  public static String prepareSqlFromPLSQL(List<TokenRef> tokens) {
    if (tokens == null) {
      return null;
    }
    boolean select = false;
    boolean insert = false;
    Iterator<TokenRef> i = tokens.iterator();
    while (i.hasNext()) {
      TokenRef token = i.next();
      if (token.isTokenIgnoreCase("CURSOR")) {
        i.remove();
        while (i.hasNext()) {
          token = i.next();
          i.remove();
          if (token.isTokenIgnoreCase("IS")) {
            break;
          }
        }
      }
      if (!select && !insert && token.isTokenIgnoreCase("SELECT")) {
        select = true;
      }
      if (!select && !insert && token.isTokenIgnoreCase("INSERT")) {
        insert = true;
      }
      if (token.styleId == SQLSyntaxDocument.LOCAL_IDENTIFIER) {
        token.token = ":" +token.token;
        if (i.hasNext() && (token = i.next()).isToken(".")) {
          token.token = "_";
        }
      }
      else if (token.styleId == SQLSyntaxDocument.COMMAND_PARAMETER) {
        if (i.hasNext() && (token = i.next()).isToken(".")) {
          token.token = "_";
        }
      }
      else if (!insert && token.isTokenIgnoreCase("INTO")) {
        token.token = "/*" +token.token;
        while (i.hasNext()) {
          token = i.next();
          if (token.isTokenIgnoreCase("FROM")) {
            token.token = "*/ " +token.token;
            break;
          }
        }
      }
      else if (!i.hasNext() && token.isToken(";")) {
        i.remove();
      }
    }
    StringBuilder sb = new StringBuilder();
    i = tokens.iterator();
    while (i.hasNext()) {
      TokenRef token = i.next();
      sb.append(token.token);
    }
    return sb.toString();
  }

}
