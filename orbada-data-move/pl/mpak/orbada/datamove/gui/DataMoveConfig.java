/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.datamove.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import pl.mpak.orbada.datamove.OrbadaDataMovePlugin;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class DataMoveConfig {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaDataMovePlugin.class);

  private String tableName;
  private String insertCommand;
  private String selectCommand;
  private String updateCommand;
  private boolean commit;
  private int commitCount;
  private boolean noUpdate;
  private boolean ignoreErrors;
  private boolean destParamTypes;

  public DataMoveConfig() {
    commit = true;
    destParamTypes = true;
    noUpdate = true;
  }

  public void load(File file) throws IOException {
    Properties props = new Properties();
    InputStream is = new FileInputStream(file);
    try {
      props.loadFromXML(is);
      tableName = props.getProperty("table-name");
      insertCommand = props.getProperty("insert-command");
      selectCommand = props.getProperty("select-command");
      updateCommand = props.getProperty("update-command");
      commit = StringUtil.toBoolean(props.getProperty("commit"));
      commitCount = Integer.parseInt(props.getProperty("commit-count", "1"));
      noUpdate = StringUtil.toBoolean(props.getProperty("no-update"));
      ignoreErrors = StringUtil.toBoolean(props.getProperty("ignore-errors"));
      destParamTypes = StringUtil.toBoolean(props.getProperty("dest-param-types"));
    }
    finally {
      is.close();
    }
  }

  public void save(File file) throws IOException {
    Properties props = new Properties();
    OutputStream os = new FileOutputStream(file);
    try {
      props.setProperty("table-name", tableName);
      props.setProperty("insert-command", insertCommand);
      props.setProperty("select-command", selectCommand);
      props.setProperty("update-command", updateCommand);
      props.setProperty("commit", commit ? "true" : "false");
      props.setProperty("commit-count", String.valueOf(commitCount));
      props.setProperty("no-update", noUpdate ? "true" : "false");
      props.setProperty("ignore-errors", ignoreErrors ? "true" : "false");
      props.setProperty("dest-param-types", destParamTypes ? "true" : "false");
      props.storeToXML(os, "Orbada Data Move Config");
    }
    finally {
      os.close();
    }
  }

  public boolean isCommit() {
    return commit;
  }

  public void setCommit(boolean commit) {
    this.commit = commit;
  }

  public int getCommitCount() {
    return commitCount;
  }

  public void setCommitCount(int commitCount) {
    this.commitCount = commitCount;
  }

  public String getInsertCommand() {
    return insertCommand;
  }

  public void setInsertCommand(String insertCommand) {
    this.insertCommand = insertCommand;
  }

  public boolean isNoUpdate() {
    return noUpdate;
  }

  public void setNoUpdate(boolean noUpdate) {
    this.noUpdate = noUpdate;
  }

  public String getSelectCommand() {
    return selectCommand;
  }

  public void setSelectCommand(String selectCommand) {
    this.selectCommand = selectCommand;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getUpdateCommand() {
    return updateCommand;
  }

  public void setUpdateCommand(String updateCommand) {
    this.updateCommand = updateCommand;
  }

  public boolean isIgnoreErrors() {
    return ignoreErrors;
  }

  public void setIgnoreErrors(boolean ignoreErrors) {
    this.ignoreErrors = ignoreErrors;
  }

  public boolean isDestParamTypes() {
    return destParamTypes;
  }

  public void setDestParamTypes(boolean destParamTypes) {
    this.destParamTypes = destParamTypes;
  }

}
