/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.export;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 *
 * @author akaluza
 */
public class ExportToSqlInsertDefinition implements Serializable {
  
  private String name;
  private String command;
  private String columnName;
  private String varcharValue;
  private String varcharChars;
  private String charPrefix;
  private String timestampValue;
  private String numericValue;
  private String trueValue;
  private String falseValue;
  private String binaryValue;
  private String nullValue;
  private boolean binaryHex;
  private boolean dotToComma;

  public ExportToSqlInsertDefinition() {
  }

  public ExportToSqlInsertDefinition(String name) {
    this.name = name;
  }

  public ExportToSqlInsertDefinition(
    String name, String command, String columnName,
    String varcharValue, String varcharChars, String charPrefix, String timestampValue, String numericValue, 
    String trueValue, String falseValue, String binaryValue, String nullValue, boolean binaryHex, boolean dotToComma
  ) {
    this.name = name;
    this.command = command;
    this.columnName = columnName;
    this.varcharValue = varcharValue;
    this.varcharChars = varcharChars;
    this.charPrefix = charPrefix;
    this.timestampValue = timestampValue;
    this.numericValue = numericValue;
    this.trueValue = trueValue;
    this.falseValue = falseValue;
    this.binaryValue = binaryValue;
    this.nullValue = nullValue;
    this.binaryHex = binaryHex;
    this.dotToComma = dotToComma;
  }

  public static ExportToSqlInsertDefinition toDefinition(String text) {
    ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(ExportToSqlInsertDefinition.class.getClassLoader());
    try {
      ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes());
      XMLDecoder xdec = new XMLDecoder(in);
      ExportToSqlInsertDefinition eti = (ExportToSqlInsertDefinition)xdec.readObject();
      xdec.close();
      return eti;
    }
    finally {
      Thread.currentThread().setContextClassLoader(oldCL);
    }
  }
  
  public static String toString(ExportToSqlInsertDefinition def) {
    ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(ExportToSqlInsertDefinition.class.getClassLoader());
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      XMLEncoder xenc = new XMLEncoder(out);
      xenc.writeObject(def);
      xenc.flush();
      xenc.close();
      return new String(out.toByteArray());
    }
    finally {
      Thread.currentThread().setContextClassLoader(oldCL);
    }
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isBinaryHex() {
    return binaryHex;
  }

  public void setBinaryHex(boolean binaryHex) {
    this.binaryHex = binaryHex;
  }

  public String getBinaryValue() {
    return binaryValue;
  }

  public void setBinaryValue(String binaryValue) {
    this.binaryValue = binaryValue;
  }

  public String getTrueValue() {
    return trueValue;
  }

  public void setTrueValue(String trueValue) {
    this.trueValue = trueValue;
  }

  public String getFalseValue() {
    return falseValue;
  }

  public void setFalseValue(String falseValue) {
    this.falseValue = falseValue;
  }

  public String getCharPrefix() {
    return charPrefix;
  }

  public void setCharPrefix(String charPrefix) {
    this.charPrefix = charPrefix;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getNullValue() {
    return nullValue;
  }

  public void setNullValue(String nullValue) {
    this.nullValue = nullValue;
  }

  public String getNumericValue() {
    return numericValue;
  }

  public void setNumericValue(String numericValue) {
    this.numericValue = numericValue;
  }

  public String getTimestampValue() {
    return timestampValue;
  }

  public void setTimestampValue(String timestampValue) {
    this.timestampValue = timestampValue;
  }

  public String getVarcharChars() {
    return varcharChars;
  }

  public void setVarcharChars(String varcharChars) {
    this.varcharChars = varcharChars;
  }

  public String getVarcharValue() {
    return varcharValue;
  }

  public void setVarcharValue(String varcharValue) {
    this.varcharValue = varcharValue;
  }

  public boolean isDotToComma() {
    return dotToComma;
  }

  public void setDotToComma(boolean dotToComma) {
    this.dotToComma = dotToComma;
  }

}
