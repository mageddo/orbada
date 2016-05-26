package orbada.db;

import orbada.Consts;
import orbada.ErrorMessages;
import orbada.OrbadaException;
import orbada.core.Application;
import orbada.gui.LoginDialog;
import orbada.gui.LoginInfo;
import org.apache.log4j.Logger;
import pl.mpak.orbada.db.Orbada;
import pl.mpak.orbada.db.User;
import orbada.util.ScriptUtil;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseManager;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.id.VersionID;
import pl.mpak.util.patt.Resolvers;

/**
 *
 * @author akaluza
 */
public class InternalDatabase extends OrbadaDatabase {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private static InternalDatabase orbadaDatabase;
  
  public static Database get() {
    return orbadaDatabase;
  }

  public static void init() {
    String className = Application.get().getProperty("internal.db.class");
    String url = Resolvers.expand(Application.get().getProperty("internal.db.url"));
    String driver = Application.get().getProperty("internal.db.driver");
    String user = Resolvers.expand(Application.get().getProperty("internal.db.user"));
    String password = Application.get().getProperty("internal.db.password");
    String jdbcSource = Resolvers.expand(Application.get().getProperty("internal.db.source"));
    String jdbcExtraLibrary = Resolvers.expand(Application.get().getProperty("internal.db.extra.library"));
    if (StringUtil.equals("", jdbcSource) && StringUtil.equals("HSQLDB", driver)) {
      jdbcSource = Resolvers.expand("$(user.dir)/jdbc/hsqldb-internal/hsqldb.jar");
    }

    Logger.getLogger("orbada").debug(jdbcSource +":" +className +":" +url +":" +driver +":" +user +":" +password);
    try {
      if (Application.get().getProperty("internal.db.user") == null ||
          Application.get().getProperty("internal.db.password") == null) {
        LoginInfo info = LoginDialog.show(user, password, stringManager.getString("InternalDatabase-connect-to-database"));
        if (info == null) {
          System.exit(-1);
        }
        user = info.getUserName();
        password = info.getPassword();
      }

      Application.renderSplashText(String.format(stringManager.getString("InternalDatabase-connect-to-database-3dot"), new Object[] {driver}));
      Logger.getLogger("orbada").debug("Connecting to " + driver + " database on " + url);
      try {
        orbadaDatabase = DatabaseManager.createDatabase(InternalDatabase.class, DriverClassLoaderManager.getDriver(jdbcSource, jdbcExtraLibrary, className), url, user, password);
      } catch (Exception ex) {
        throw new OrbadaException(ErrorMessages.ORBADA_01001_NO_DRIVER_FOUND, ex);
      }
      orbadaDatabase.setDriverType(driver);
      orbadaDatabase.setPublicName("Orbada Internal Connection");
      orbadaDatabase.setAutoCommit(true);
      orbadaDatabase.setAutoConnect(true);
      for (String par : Database.useDbParameters) {
        String value = Application.get().getProperty(par);
        if (!StringUtil.isEmpty(value)) {
          orbadaDatabase.getUserProperties().put(par, value);
        }
      }
      try {
        if ("MYSQL".equalsIgnoreCase(InternalDatabase.get().getDriverType())) {
          new Command(orbadaDatabase).execute("SET SQL_MODE='ANSI_QUOTES'");
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      Logger.getLogger("orbada").info("Connecting to " + driver + " database: OK");
    } catch (Exception ex) {
      Application.get().setSafeMode(true);
      MessageBox.show(
        stringManager.getString("InternalDatabase-database-connect"),
        String.format(
          stringManager.getString("InternalDatabase-check-connect-parameters"),
          new Object[] {Application.get().getConfigFile(), url, StringUtil.toString(StringUtil.wordWrap(ex.getMessage(), 150), null)}));
      Logger.getLogger("orbada").error("Connecting to " + driver + " database: error", ex);
      //System.exit(-1);
    }
  }
  
  public static void done() {
    if (InternalDatabase.get() == null) {
      return;
    }
    try {
      if ("HSQLDB".equalsIgnoreCase(InternalDatabase.get().getDriverType()) &&
        ":FILE:".equalsIgnoreCase(InternalDatabase.get().getUrl())) {
        new Command(orbadaDatabase).execute("SHUTDOWN COMPACT");
      }
      orbadaDatabase.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
  public static void beforeLogin() {
    Query query;
    try {
      query = new Query(orbadaDatabase);
      try {
        try {
          query.open("select 0 from dual");
        } catch (Exception e) {
          orbadaDatabase.executeCommand("create table dual (dummy varchar(1))");
          orbadaDatabase.executeCommand("insert into dual values ('X')");
        }
        try {
          query.open("select 0 from users where 1 = 0");
        } catch (Exception e) {
          orbadaDatabase.executeCommand(
            "create table users (\n" +
            "  usr_id varchar(40) not null,\n" +
            "  usr_created timestamp not null,\n" +
            "  usr_updated timestamp not null,\n" +
            "  usr_name varchar(100) not null,\n" +
            "  usr_password varchar(100),\n" +
            "  usr_description varchar(1000),\n" +
            "  usr_admin varchar(1),\n" +
            "  usr_orbada varchar(1),\n" +
            "  usr_active varchar(1),\n" +
            "  usr_registered varchar(1)\n" +
            ")");
          orbadaDatabase.executeCommandNoException("alter table users add constraint users_pk primary key (usr_id)");
          orbadaDatabase.executeCommand("create unique index user_name_ui on users (usr_name)");
          
          orbadaDatabase.executeCommand(
            "create table tools (\n" +
            "  to_id varchar(40) not null primary key,\n" +
            "  to_usr_id varchar(40) not null,\n" +
            "  to_command varchar(100) not null,\n" +
            "  to_source varchar(2000) not null,\n" +
            "  to_arguments varchar(1000),\n" +
            "  to_title varchar(200),\n" +
            "  to_menu varchar(1),\n" +
            "  to_bsh_get_arguments varchar(4000),\n" +
            "  to_bsh_before_exec varchar(4000),\n" +
            "  to_bsh_after_exec varchar(4000),\n" +
            "  to_toolbutton varchar(1),\n" +
            "  to_icon " +Application.get().getProperty("data.type.blob", "BLOB") +"\n" +
            ")");
          orbadaDatabase.executeCommand("CREATE UNIQUE INDEX TOOLS_COMMAND_UI ON TOOLS(TO_USR_ID, TO_COMMAND)");
          orbadaDatabase.executeCommandNoException("alter table tools add constraint tool_user_fk foreign key (to_usr_id) references users (usr_id) on delete cascade");
          Application.get().setFirstRun(true);
        }
      }
      finally {
        query.close();
      }
    } catch (Exception e) {
      ExceptionUtil.processException(e);
    }
  }

  public static void afterLogin() {
    VersionID lastVersionID = null;
    Query query;
    try {
      query = new Query(orbadaDatabase);
      try {
        if (Application.get().isUserAdmin()) {
          try {
            query.open("select 0 from orbada where 1 = 0");
            try {
              query.open("select id, user_id from orbada where 1 = 0");
            }
            catch (Exception e2) {
              orbadaDatabase.executeCommandNoException("ALTER TABLE orbada DROP CONSTRAINT orbada_pk");
              try {orbadaDatabase.executeCommand("alter table orbada add id varchar(40)");} catch (Exception ex) {}
              try {orbadaDatabase.executeCommand("alter table orbada add user_id varchar(40)");} catch (Exception ex) {}
              try {
                query.open("select name from orbada");
                while (!query.eof()) {
                  orbadaDatabase.executeCommand("update orbada set id = '" +(new UniqueID()).toString() +"', user_id = '" +Application.get().getUserId() +"' where name = '" +query.fieldByName("name").getString() +"'");
                  query.next();
                }
              }
              catch (Exception e3) {
              }
              orbadaDatabase.executeCommandNoException("ALTER TABLE orbada ADD CONSTRAINT orbada_pk PRIMARY KEY (id)");
              orbadaDatabase.executeCommandNoException("ALTER TABLE orbada ADD CONSTRAINT orbada_user_fk FOREIGN KEY (user_id) references users(usr_id) on delete cascade");
              try {orbadaDatabase.executeCommand("CREATE INDEX orbada_name_i on orbada(user_id, name)");} catch (Exception ex) {}
            }
            orbadaDatabase.executeCommand("update orbada a set value = (select value from orbada b where name = 'version' and a.user_id = b.user_id) where name = 'last-version' and user_id = '" +Application.get().getUserId() +"'");
            orbadaDatabase.executeCommand("update orbada a set value = '" +Consts.orbadaVersion +"' where name = 'version' and user_id = '" +Application.get().getUserId() +"'");
            query.open("select value from orbada where name = 'last-version' and user_id = '" +Application.get().getUserId() +"'");
            lastVersionID = new VersionID(query.fieldByName("value").getValue().toString());
            Application.get().setLastVersion(lastVersionID);
          } catch (Exception e) {
            orbadaDatabase.executeCommand(
              "create table orbada (\n" +
              "  id varchar(40) not null,\n" +
              "  user_id varchar(40),\n" +
              "  name varchar(100) not null,\n" +
              "  value varchar(1000)\n" +
              ")");
            orbadaDatabase.executeCommandNoException("ALTER TABLE orbada ADD CONSTRAINT orbada_pk PRIMARY KEY (id)");
            orbadaDatabase.executeCommandNoException("ALTER TABLE orbada ADD CONSTRAINT orbada_user_fk FOREIGN KEY (user_id) references users(usr_id) on delete cascade");
            orbadaDatabase.executeCommand("CREATE INDEX orbada_name_i on orbada(user_id, name)");
            lastVersionID = null;
          }
        }
        
        if (lastVersionID == null) {
          orbadaDatabase.executeCommand("insert into orbada (id, user_id, name, value) values ('" +(new UniqueID()).toString() +"', '" +Application.get().getUserId() +"', 'last-version', '" +Consts.orbadaVersion +"')");
          orbadaDatabase.executeCommand("insert into orbada (id, user_id, name, value) values ('" +(new UniqueID()).toString() +"', '" +Application.get().getUserId() +"', 'version', '" +Consts.orbadaVersion +"')");
          orbadaDatabase.executeCommand("insert into orbada (id, user_id, name, value) values ('" +(new UniqueID()).toString() +"', '" +Application.get().getUserId() +"', 'application-name', 'Organizer Bazy Danych')");
          orbadaDatabase.executeCommand("insert into orbada (id, user_id, name, value) values ('" +(new UniqueID()).toString() +"', '" +Application.get().getUserId() +"', 'short-name', 'ORBADA')");
          orbadaDatabase.executeCommand("insert into orbada (id, user_id, name, value) values ('" +(new UniqueID()).toString() +"', '" +Application.get().getUserId() +"', 'copyright', '" +Consts.orbadaCopyrights +"')");
        }

        if (lastVersionID == null && Application.get().isUserAdmin()) {
          orbadaDatabase.executeCommand("CREATE TABLE DRIVER_TYPES(DTP_ID VARCHAR(40) NOT NULL PRIMARY KEY,DTP_NAME VARCHAR(100) NOT NULL)");
          orbadaDatabase.executeCommand("CREATE UNIQUE INDEX DRIVER_TYPE_NAME_UI ON DRIVER_TYPES(DTP_NAME)");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000001-00000001','HSQLDB')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000002-00000001','Oracle')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000003-00000001','Interbase')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000004-00000001','Firebird')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000005-00000001','Microsoft SQL Server')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000006-00000001','IBM DB2')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000007-00000001','Apache Derby')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000008-00000001','JDBC-ODBC Bridge')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000001000-00000001','Others')");

          orbadaDatabase.executeCommand(
            "CREATE TABLE DRIVERS(\n" +
            "  DRV_ID VARCHAR(40) NOT NULL,\n" +
            "  DRV_USR_ID VARCHAR(40),\n" +
            "  DRV_NAME VARCHAR(100) NOT NULL,\n" +
            "  DRV_LIBRARY_SOURCE VARCHAR(1000),\n" +
            "  DRV_TYPE_NAME VARCHAR(100),\n" +
            "  DRV_CLASS_NAME VARCHAR(1000),\n" +
            "  DRV_URL_TEMPLATE VARCHAR(1000)\n" +
            ")");
          orbadaDatabase.executeCommandNoException("alter table DRIVERS add constraint DRIVERS_pk primary key (DRV_ID)");
          orbadaDatabase.executeCommandNoException("alter table drivers add constraint driver_user_fk foreign key (drv_usr_id) references users (usr_id) on delete cascade");
          orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000001-00000002',NULL,'Oracle','./jdbc/ojdbc14.jar','Oracle','oracle.jdbc.OracleDriver','jdbc:oracle:thin:@{host}:{port}[1521]:{database}')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000002-00000002',NULL,'Apache Derby','./jdbc/derbyclient.jar','Apache Derby','org.apache.derby.jdbc.ClientDriver','jdbc:derby://{host}:{port}[1527]{database};create=true')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000003-00000002',NULL,'HSQLDB Embeded 1.8','./jdbc/hsqldb-1.8/hsqldb.jar','HSQLDB','org.hsqldb.jdbcDriver','jdbc:hsqldb:file:{database};create=true')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000004-00000002',NULL,'JDBC ODBC Bridge',NULL,'ODBC Bridge','sun.jdbc.odbc.JdbcOdbcDriver','jdbc:odbc:<database>')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000005-00000002',NULL,'Microsoft SQL Server Driver','sqljdbc.jar','Microsoft SQL Server','com.microsoft.sqlserver.jdbc.SQLServerDriver','jdbc:sqlserver://<host>\\<database>')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000006-00000002',NULL,'HSQLDB Server 1.8','./jdbc/hsqldb-1.8/hsqldb.jar','HSQLDB','org.hsqldb.jdbcDriver','jdbc:hsqldb:hsql://{host}/{database}')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000007-00000002',NULL,'PostgreSQL','./jdbc/postgresql-9.2-1002.jdbc4.jar','PostgreSQL','org.postgresql.Driver','jdbc:postgresql://<host>:<port>[5432]/<database>')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000008-00000002',NULL,'Firebird','./jdbc/jaybird-full-2.1.6.jar','Firebird','org.firebirdsql.jdbc.FBDriver','jdbc:firebirdsql://<host>:<port>/<database>')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000009-00000002',NULL,'Oracle OCI','./jdbc/ojdbc14.jar','Oracle','oracle.jdbc.driver.OracleDriver','jdbc:oracle:oci:@<sid>')");

          orbadaDatabase.executeCommand(
            "create table schemas (\n" +
            "  sch_id varchar(40) not null,\n" +
            "  sch_usr_id varchar(40),\n" +
            "  sch_name varchar(100) not null,\n" +
            "  sch_drv_id varchar(40),\n" +
            "  sch_host varchar(200),\n" +
            "  sch_database varchar(200),\n" +
            "  sch_user varchar(100),\n" +
            "  sch_password varchar(100),\n" +
            "  sch_port integer,\n" +
            "  sch_url varchar(1000),\n" +
            "  sch_auto_commit varchar(1),\n" +
            "  sch_db_version varchar(100),\n" +
            "  sch_selected timestamp,\n" +
            "  sch_icon " +Application.get().getProperty("data.type.blob", "BLOB") +"\n" +
            ")");
          orbadaDatabase.executeCommandNoException("alter table schemas add constraint schemas_pk primary key (sch_id)");
          orbadaDatabase.executeCommand("create index schemas_selected_i on schemas (sch_selected)");
          orbadaDatabase.executeCommand("create index schemas_name_i on schemas (sch_name)");
          orbadaDatabase.executeCommand("create index schemas_drv_id_i on schemas (sch_drv_id)");
          orbadaDatabase.executeCommandNoException("ALTER TABLE SCHEMAS ADD CONSTRAINT SCHEMA_DRIVER_FK FOREIGN KEY (SCH_DRV_ID) REFERENCES DRIVERS(DRV_ID)");
          orbadaDatabase.executeCommandNoException("alter table schemas add constraint schema_user_fk foreign key (sch_usr_id) references users (usr_id) on delete cascade");

          orbadaDatabase.executeCommand(
            "create table plugins (\n" +
            "  plg_id varchar(100) not null,\n" +
            "  plg_usr_id varchar(40) not null,\n" +
            "  plg_file_name varchar(1000),\n" +
            "  plg_class_name varchar(1000),\n" +
            "  plg_description varchar(1000),\n" +
            "  plg_author varchar(500),\n" +
            "  plg_version varchar(100),\n" +
            "  plg_web_site varchar(1000),\n" +
            "  plg_update_site varchar(1000),\n" +
            "  plg_enabled varchar(1),\n" +
            "  plg_loaded VARCHAR(1)\n" +
            ")");
          orbadaDatabase.executeCommandNoException("alter table plugins add constraint plugins_pk primary key (plg_id)");
          orbadaDatabase.executeCommand("create index plugins_class_name_i on plugins(plg_class_name)");
          orbadaDatabase.executeCommandNoException("alter table plugins add constraint plugin_user_fk foreign key (plg_usr_id) references users (usr_id) on delete cascade");

          orbadaDatabase.executeCommand(  
            "create table perspectives (\n" +
            "  pps_id varchar(40) not null,\n" +
            "  pps_usr_id varchar(40) not null,\n" +
            "  pps_sch_id varchar(40),\n" +
            "  pps_name varchar(100) not null,\n" +
            "  pps_description varchar(1000),\n" +
            "  pps_default varchar(1),\n" +
            "  pps_view_count integer default 0 not null,\n" +
            "  pps_gadget_count integer default 0 not null,\n" +
            "  pps_gadgets_width integer default 150 not null\n" +
            ")");
          orbadaDatabase.executeCommandNoException("ALTER TABLE PERSPECTIVES ADD CONSTRAINT PERSPECTIVES_PK PRIMARY KEY (PPS_ID)");
          orbadaDatabase.executeCommandNoException("ALTER TABLE PERSPECTIVES ADD CONSTRAINT PERSPECTIVES_USER_FK FOREIGN KEY (PPS_USR_ID) REFERENCES USERS (USR_ID) ON DELETE CASCADE");
          orbadaDatabase.executeCommandNoException("ALTER TABLE PERSPECTIVES ADD CONSTRAINT PERSPECTIVES_SCHEMA_FK FOREIGN KEY (PPS_SCH_ID) REFERENCES SCHEMAS (SCH_ID) ON DELETE CASCADE");
          orbadaDatabase.executeCommand(  
            "create table views (\n" +
            "  vws_id varchar(40) not null,\n" +
            "  vws_pps_id varchar(40) not null,\n" +
            "  vws_order integer not null,\n" +
            "  vws_name varchar(200),\n" +
            "  vws_hide_title varchar(1),\n" +
            "  vws_hide_icon varchar(1)\n" +
            ")");
          orbadaDatabase.executeCommandNoException("ALTER TABLE VIEWS ADD CONSTRAINT VIEWS_PK PRIMARY KEY (VWS_ID)");
          orbadaDatabase.executeCommandNoException("ALTER TABLE VIEWS ADD CONSTRAINT VIEWS_PERSPECTIVES_FK FOREIGN KEY (VWS_PPS_ID) REFERENCES PERSPECTIVES (PPS_ID) ON DELETE CASCADE");

          orbadaDatabase.executeCommand(  
            "create table templates (\n" +
            "  tpl_id varchar(40) not null,\n" +
            "  tpl_usr_id varchar(40),\n" +
            "  tpl_name varchar(100) not null,\n" +
            "  tpl_description varchar(1000),\n" +
            "  tpl_body " +Application.get().getProperty("data.type.clob", "CLOB") +"\n" +
            ")");
          orbadaDatabase.executeCommandNoException("ALTER TABLE TEMPLATES ADD CONSTRAINT TEMPLATES_PK PRIMARY KEY (TPL_ID)");
          orbadaDatabase.executeCommand("CREATE UNIQUE INDEX TEMPLATES_NAME_UI ON TEMPLATES(TPL_USR_ID, TPL_NAME)");
          orbadaDatabase.executeCommandNoException("ALTER TABLE TEMPLATES ADD CONSTRAINT TEMPLATE_USER_FK FOREIGN KEY (TPL_USR_ID) REFERENCES USERS (USR_ID) ON DELETE CASCADE");

          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000009-00000001','PointBase')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000010-00000001','Cloudscape')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000011-00000001','PostgreSQL')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000012-00000001','FirstSQL/J')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000013-00000001','IDS Server')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000014-00000001','Informix Dynamic Server')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000015-00000001','InstantDB')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000016-00000001','Hypersonic SQL')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000017-00000001','jTDS')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000018-00000001','Mckoi SQL Database')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000019-00000001','MySQL')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000020-00000001','Quadcap')");
          orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000021-00000001','Sybase')");

          orbadaDatabase.executeCommand(
            "create table driver_type_specs (\n" +
            "  dts_id varchar(40) not null,\n" +
            "  dts_dtp_id varchar(40) not null,\n" +
            "  dts_name varchar(100),\n" +
            "  dts_class varchar(1000) not null,\n" +
            "  dts_url_template varchar(1000) not null\n" +
            ")");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000001-00000010', '20071001000000-0000000000000006-00000001', 'Net', 'COM.ibm.db2.jdbc.net.DB2Driver', 'jdbc:db2://<HOST>:<PORT>/<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000002-00000010', '20071001000000-0000000000000006-00000001', 'Local', 'COM.ibm.db2.jdbc.app.DB2Driver', 'jdbc:db2:<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000005-00000010', '20071001000000-0000000000000006-00000001', 'JCC', 'com.ibm.db2.jcc.DB2Driver', 'jdbc:db2://<hostname>:<port>/<database>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000003-00000010', '20071001000000-0000000000000006-00000001', 'DataDirect Connect for JDBC', 'com.ddtek.jdbc.db2.DB2Driver', 'jdbc:datadirect:db2://<HOST>:<PORT>[;databaseName=<DB>]')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000004-00000010', '20071001000000-0000000000000006-00000001', 'SUN JDBC', 'com.sun.sql.jdbc.db2.DB2Driver', 'jdbc:sun:db2://server_name:portNumber;databaseName=DATABASENAME')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000006-00000010', '20071001000000-0000000000000002-00000001', 'Thin', 'oracle.jdbc.OracleDriver', 'jdbc:oracle:thin:@<HOST>:<PORT>:<SID>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000007-00000010', '20071001000000-0000000000000002-00000001', 'OCI 8i', 'oracle.jdbc.driver.OracleDriver', 'jdbc:oracle:oci8:@<SID>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000008-00000010', '20071001000000-0000000000000002-00000001', 'OCI 9i', 'oracle.jdbc.driver.OracleDriver', 'jdbc:oracle:oci:@<SID>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000009-00000010', '20071001000000-0000000000000002-00000001', 'DataDirect Connect for JDBC', 'com.ddtek.jdbc.oracle.OracleDriver', 'jdbc:datadirect:oracle://<HOST>:<PORT>;SID=<SID>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000010-00000010', '20071001000000-0000000000000002-00000001', 'SUN driver', 'com.sun.sql.jdbc.oracle.OracleDriver', 'jdbc:sun:oracle://server_name[:portNumber][;SID=DATABASENAME]')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000011-00000010', '20071001000000-0000000000000008-00000001', 'SUN driver', 'sun.jdbc.odbc.JdbcOdbcDriver', 'jdbc:odbc:<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000012-00000010', '20071001000000-0000000000000009-00000001', 'Network Server', 'com.pointbase.jdbc.jdbcUniversalDriver', 'jdbc:pointbase://<HOST>[:<PORT>]/<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000013-00000010', '20071001000000-0000000000000009-00000001', 'Embedded Server', 'com.pointbase.jdbc.jdbcUniversalDriver', 'jdbc:pointbase://embedded[:<PORT>]/<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000014-00000010', '20071001000000-0000000000000009-00000001', 'Mobile Server', 'com.pointbase.jdbc.jdbcUniversalDriver', 'jdbc:pointbase:<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000015-00000010', '20071001000000-0000000000000001-00000001', 'Server', 'org.hsqldb.jdbcDriver', 'jdbc:hsqldb:hsql://<HOST>[:<PORT>]')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000016-00000010', '20071001000000-0000000000000001-00000001', 'Standalone', 'org.hsqldb.jdbcDriver', 'jdbc:hsqldb:<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000017-00000010', '20071001000000-0000000000000007-00000001', 'Embedded', 'org.apache.derby.jdbc.EmbeddedDriver', 'jdbc:derby:<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000018-00000010', '20071001000000-0000000000000007-00000001', 'Network', 'org.apache.derby.jdbc.ClientDriver', 'jdbc:derby://<HOST>[:<PORT>]/databaseName[;attr1=value1[;...]]')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000019-00000010', '20071001000000-0000000000000010-00000001', '', 'COM.cloudscape.core.JDBCDriver', 'jdbc:cloudscape:<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000020-00000010', '20071001000000-0000000000000010-00000001', 'RMI', 'RmiJdbc.RJDriver', 'jdbc:rmi://<HOST>:<PORT>/jdbc:cloudscape:<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000021-00000010', '20071001000000-0000000000000004-00000001', 'JCA', 'org.firebirdsql.jdbc.FBDriver', 'jdbc:firebirdsql:[//<HOST>[:<PORT>]/]<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000022-00000010', '20071001000000-0000000000000012-00000001', 'Enterprise Server Edition', 'COM.FirstSQL.Dbcp.DbcpDriver', 'jdbc:dbcp://<HOST>:<PORT>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000023-00000010', '20071001000000-0000000000000012-00000001', 'Professional Edition', 'COM.FirstSQL.Dbcp.DbcpDriver', 'jdbc:dbcp://local')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000024-00000010', '20071001000000-0000000000000013-00000001', '', 'ids.sql.IDSDriver', 'jdbc:ids://<HOST>:<PORT>/conn?dsn=''<ODBC_DSN_NAME>''')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000025-00000010', '20071001000000-0000000000000014-00000001', '', 'com.informix.jdbc.IfxDriver', 'jdbc:informix-sqli://<HOST>:<PORT>/<DB>:INFORMIXSERVER=<SERVER_NAME>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000026-00000010', '20071001000000-0000000000000014-00000001', 'DataDirect Connect for JDBC', 'com.ddtek.jdbc.informix.InformixDriver', 'jdbc:datadirect:informix://<HOST>:<PORT>;informixServer=<SERVER_NAME>;databaseName=<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000027-00000010', '20071001000000-0000000000000015-00000001', 'v3.13 and earlier', 'jdbc.idbDriver', 'jdbc:idb:<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000028-00000010', '20071001000000-0000000000000015-00000001', 'v3.14 and later', 'org.enhydra.instantdb.jdbc.idbDriver', 'jdbc:idb:<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000029-00000010', '20071001000000-0000000000000003-00000001', 'InterClient driver', 'interbase.interclient.Driver', 'jdbc:interbase://<HOST>/<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000030-00000010', '20071001000000-0000000000000016-00000001', 'v1.2 and earlier', 'hSql.hDriver', 'jdbc:HypersonicSQL:<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000031-00000010', '20071001000000-0000000000000016-00000001', 'v1.3 and later', 'org.hsql.jdbcDriver', 'jdbc:HypersonicSQL:<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000032-00000010', '20071001000000-0000000000000017-00000001', '', 'net.sourceforge.jtds.jdbc.Driver', 'jdbc:jtds:sqlserver://<server>[:<PORT>][/<DATABASE>]')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000033-00000010', '20071001000000-0000000000000018-00000001', 'Server', 'com.mckoi.JDBCDriver', 'jdbc:jtds:sqlserver://<server>[:<PORT>][/<DATjdbc:mckoi://<HOST>[:<PORT>]')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000034-00000010', '20071001000000-0000000000000018-00000001', 'Standalone', 'com.mckoi.JDBCDriver', 'jdbc:mckoi:local://<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000035-00000010', '20071001000000-0000000000000005-00000001', 'Weblogic driver', 'weblogic.jdbc.mssqlserver4.Driver', 'jdbc:weblogic:mssqlserver4:<DB>@<HOST>:<PORT>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000036-00000010', '20071001000000-0000000000000005-00000001', 'DataDirect Connect for JDBC', 'com.ddtek.jdbc.sqlserver.SQLServerDriver', 'jdbc:datadirect:sqlserver://<HOST>:<PORT>[;databaseName=<DB>]')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000037-00000010', '20071001000000-0000000000000005-00000001', 'JTurbo driver', 'com.ashna.jturbo.driver.Driver', 'jdbc:JTurbo://<HOST>:<PORT>/<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000038-00000010', '20071001000000-0000000000000005-00000001', 'Sprinta driver', 'com.inet.tds.TdsDriver', 'jdbc:inetdae:<HOST>:<PORT>?database=<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000039-00000010', '20071001000000-0000000000000005-00000001', '2000 Microsoft driver', 'com.microsoft.jdbc.sqlserver.SQLServerDriver', 'jdbc:microsoft:sqlserver://<HOST>:<PORT>[;DatabaseName=<DB>]')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000040-00000010', '20071001000000-0000000000000005-00000001', '2005 Microsoft driver', 'com.microsoft.sqlserver.jdbc.SQLServerDriver', 'jdbc:sqlserver://<hostname>:<PORT>;databaseName=<database>;selectMethod=cursor')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000041-00000010', '20071001000000-0000000000000005-00000001', 'SUN driver', 'com.sun.sql.jdbc.sqlserver.SQLServerDriver', 'jdbc:sun:sqlserver://server_name[:portNumber]')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000042-00000010', '20071001000000-0000000000000019-00000001', 'Connector/J driver', 'com.mysql.jdbc.Driver', 'jdbc:mysql://<HOST>:<PORT>/<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000043-00000010', '20071001000000-0000000000000019-00000001', 'MM.MySQL driver', 'org.gjt.mm.mysql.Driver', 'jdbc:mysql://<HOST>:<PORT>/<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000044-00000010', '20071001000000-0000000000000011-00000001', 'v6.5 and earlier', 'postgresql.Driver', 'jdbc:postgresql://<HOST>:<PORT>/<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000045-00000010', '20071001000000-0000000000000011-00000001', 'v7.0 and later', 'org.postgresql.Driver', 'jdbc:postgresql://<HOST>:<PORT>/<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000046-00000010', '20071001000000-0000000000000020-00000001', 'Embeddable Database', 'com.quadcap.jdbc.JdbcDriver', 'jdbc:qed:<DB>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000047-00000010', '20071001000000-0000000000000021-00000001', 'jConnect 4.2 and earlier', 'com.sybase.jdbc.SybDriver', 'jdbc:sybase:Tds:<HOST>:<PORT>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000048-00000010', '20071001000000-0000000000000021-00000001', 'jConnect 5.2', 'com.sybase.jdbc2.jdbc.SybDriver', 'jdbc:sybase:Tds:<HOST>:<PORT>')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000049-00000010', '20071001000000-0000000000000021-00000001', 'DataDirect Connect for JDBC', 'com.ddtek.jdbc.sybase.SybaseDriver', 'jdbc:datadirect:sybase://<HOST>:<PORT>[;databaseName=<DB>]')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000050-00000010', '20071001000000-0000000000000021-00000001', 'SUN driver', 'com.sun.sql.jdbc.sybase.SybaseDriver', 'jdbc:sun:sybase://server_name[:portNumber]')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000051-00000010', '20071001000000-0000000000000001-00000001', 'In-Memory', 'org.hsqldb.jdbcDriver', 'jdbc:hsqldb:.')");
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000052-00000010', '20071001000000-0000000000000001-00000001', 'Webserver', 'org.hsqldb.jdbcDriver', 'jdbc:hsqldb:http://<HOST>[:<PORT>]')");

          orbadaDatabase.executeCommand(  
            "create table gadgets (\n" +
            "  gdg_id varchar(40) not null,\n" +
            "  gdg_order integer not null,\n" +
            "  gdg_name varchar(200),\n" +
            "  gdg_pps_id varchar(40),\n" +
            "  gdg_height integer,\n" +
            "  gdg_minimized varchar(1)\n" +
            ")");
          orbadaDatabase.executeCommandNoException("alter table gadgets add constraint gadget_pk primary key (gdg_id)");
          orbadaDatabase.executeCommandNoException("alter table gadgets add constraint gadget_perspective_fk foreign key (gdg_pps_id) references perspectives (pps_id) on delete cascade");

          orbadaDatabase.executeCommand(  
            "create table generators (\n" +
            "  gen_name varchar(100) not null,\n" +
            "  gen_value varchar(20) default '1' not null,\n" +
            "  gen_min_value varchar(20) default '1' not null,\n" +
            "  gen_max_value varchar(20) default '99999999999999999999' not null,\n" +
            "  gen_increment varchar(20) default '1' not null,\n" +
            "  gen_cycle varchar(1) default 'F' not null,\n" +
            "  gen_locked varchar(1)\n" +
            ")");
          orbadaDatabase.executeCommandNoException("alter table generators add constraint generators_pk primary key (gen_name)");
        }

        if (Application.get().isUserAdmin()) {
          try {
            query.open("select count( 0 ) cusr from users where usr_orbada = 'Y'");
            if (query.fieldByName("cusr").getLong() == 0) {
              User orbadaUser = new User(orbadaDatabase);
              orbadaUser.fieldByName("USR_ID").setString(Consts.orbadaUserId);
              orbadaUser.fieldByName("USR_NAME").setString("ORBADA");
              orbadaUser.fieldByName("USR_DESCRIPTION").setString("ORBADA System User - do not change or delete this user");
              orbadaUser.fieldByName("USR_ORBADA").setString("Y");
              orbadaUser.applyInsert();
            }
          } catch (Exception e) {
            ExceptionUtil.processException(e);
          }
        }

        if ((lastVersionID == null || lastVersionID.getBuild() < 4) && Application.get().isUserAdmin()) {
          ScriptUtil.executeInternalScript(get().getClass().getResourceAsStream("/orbada/sql/sessions.sql"));
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 5) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommand("alter table schemas add sch_properties varchar(4000)");
          } catch (Exception e) {
          }
        }
        if (lastVersionID == null || lastVersionID.getBuild() < 7) {
          try {
            orbadaDatabase.executeCommand("insert into orbada (id, user_id, name, value) values ('" +(new UniqueID()).toString() +"', '" +Application.get().getUserId() +"', 'unique-id', '" +(new UniqueID().toString()) +"')");
          } catch (Exception e) {
          }
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 8) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000022-00000001','H2')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000053-00000010', '20071001000000-0000000000000022-00000001', 'Server on User Home', 'org.h2.Driver', 'jdbc:h2:tcp://$(host)/~/$(database)')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000054-00000010', '20071001000000-0000000000000022-00000001', 'Server on User Home with Port', 'org.h2.Driver', 'jdbc:h2:tcp://$(host):$(port)/~/$(database)')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000055-00000010', '20071001000000-0000000000000022-00000001', 'Server', 'org.h2.Driver', 'jdbc:h2:tcp://$(host)/$(database)')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000056-00000010', '20071001000000-0000000000000022-00000001', 'Embeded on User Home', 'org.h2.Driver', 'jdbc:h2:~/$(database)')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000057-00000010', '20071001000000-0000000000000022-00000001', 'Embeded', 'org.h2.Driver', 'jdbc:h2:$(database)')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000058-00000010', '20071001000000-0000000000000022-00000001', 'In-memmory (Privet)', 'org.h2.Driver', 'jdbc:h2:mem:')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000059-00000010', '20071001000000-0000000000000022-00000001', 'In-memmory (Named)', 'org.h2.Driver', 'jdbc:h2:mem:$(database)')");
            orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000010-00000002',NULL,'H2 Server','./jdbc/h2.jar','H2','org.h2.Driver','jdbc:h2:tcp://$(host)/~/$(database)')");
          } catch (Exception e) {
          }
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 11) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000023-00000001','DBF')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000060-00000010', '20071001000000-0000000000000023-00000001', 'DBase File', 'com.sqlmagic.tinysql.dbfFileDriver', 'jdbc:dbfFile:$(database)')");
            orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000011-00000002',NULL,'DBF','./jdbc/tinySQL-2.26.jar','DBF','com.sqlmagic.tinysql.dbfFileDriver','jdbc:dbfFile:$(database)')");
          } catch (Exception e) {
          }
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 13) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommand("alter table schemas add sch_user_properties varchar(4000)");
          } catch (Exception e) {
          }
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 18) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000024-00000001','SQLite')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000061-00000010', '20071001000000-0000000000000024-00000001', 'SQLite', 'org.sqlite.JDBC', 'jdbc:sqlite:$(database)')");
            orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000012-00000002',NULL,'SQLite','./jdbc/sqlitejdbc.jar','SQLite','org.sqlite.JDBC','jdbc:sqlite:$(database)')");
          } catch (Exception e) {
          }
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 29) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommandNoException("alter table plugins drop constraint plugins_pk");
            //orbadaDatabase.executeCommandNoException("alter table plugins add primary key (plg_id, plg_usr_id)");
          } catch (Exception ex) {
            ExceptionUtil.processException(ex);
          }
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 74) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000062-00000010', '20071001000000-0000000000000019-00000001', 'Connector/J driver (short)', 'com.mysql.jdbc.Driver', 'jdbc:mysql://<HOST>/<DB>')");
            query.open("select count(0) cdrv from drivers where drv_type_name = 'MySQL'");
            if (query.fieldByName("cdrv").getInteger() == 0) {
              orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000013-00000002',NULL,'MySQL Server','./jdbc/mysql-connector-java-5.1.7-bin.jar','MySQL','com.mysql.jdbc.Driver','jdbc:mysql://<HOST>/<DB>')");
            }
            Logger.getLogger("orbada").info("IDE:UPDATE: Driver for MySQL added");
          } catch (Exception e) {
            ExceptionUtil.processException(e);
          }
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 76) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommand("alter table schemas add sch_public_name varchar(200)");
            orbadaDatabase.executeCommand("update schemas set sch_public_name = sch_name");
            Logger.getLogger("orbada").info("IDE:UPDATE: Column sch_public_name on schemas added");
          } catch (Exception e) {
            ExceptionUtil.processException(e);
          }
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 134) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000014-00000002',NULL,'HSQLDB Embeded 2.1','./jdbc/hsqldb-2.1/hsqldb.jar','HSQLDB','org.hsqldb.jdbc.JDBCDriver','jdbc:hsqldb:file:{database};create=true')");
            orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000015-00000002',NULL,'HSQLDB Server 2.1','./jdbc/hsqldb-2.1/hsqldb.jar','HSQLDB','org.hsqldb.jdbc.JDBCDriver','jdbc:hsqldb:hsql://{host}[:{port}]/{database}')");
            Logger.getLogger("orbada").info("IDE:UPDATE: new HSQLDB 2.1 driver was added");
          } catch (Exception e) {
            ExceptionUtil.processException(e);
          }
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 135) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommand("UPDATE DRIVERS SET DRV_LIBRARY_SOURCE = './jdbc/hsqldb-1.8/hsqldb.jar' WHERE DRV_ID IN ('20071001000000-0000000000000003-00000002', '20071001000000-0000000000000006-00000002') AND DRV_LIBRARY_SOURCE = './lib/hsqldb.jar'");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000063-00000010', '20071001000000-0000000000000001-00000001', 'Server', 'org.hsqldb.jdbc.JDBCDriver', 'jdbc:hsqldb:hsql://{host}[:{port}]/{database}')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000064-00000010', '20071001000000-0000000000000001-00000001', 'Server with TLS', 'org.hsqldb.jdbc.JDBCDriver', 'jdbc:hsqldb:hsqls://{host}[:{port}]/{database}')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000065-00000010', '20071001000000-0000000000000001-00000001', 'Standalone', 'org.hsqldb.jdbc.JDBCDriver', 'jdbc:hsqldb:{database}')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000066-00000010', '20071001000000-0000000000000001-00000001', 'In-Memory', 'org.hsqldb.jdbc.JDBCDriver', 'jdbc:hsqldb:.')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000067-00000010', '20071001000000-0000000000000001-00000001', 'Webserver', 'org.hsqldb.jdbc.JDBCDriver', 'jdbc:hsqldb:http://{host}[:{port}]')");
            orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000068-00000010', '20071001000000-0000000000000001-00000001', 'Webserver with TLS', 'org.hsqldb.jdbc.JDBCDriver', 'jdbc:hsqldb:http://{host}[:{port}]')");
            Logger.getLogger("orbada").info("IDE:UPDATE: new HSQLDB 2.1 class url was added");
          } catch (Exception e) {
            ExceptionUtil.processException(e);
          }
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 138) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommand("INSERT INTO DRIVER_TYPES VALUES('20071001000000-0000000000000025-00000001','CSV')");
            Logger.getLogger("orbada").info("IDE:UPDATE: Driver type for CSV added");
            orbadaDatabase.executeCommand(
              "insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) " +
              "values ('20071001000000-0000000000000069-00000010', '20071001000000-0000000000000025-00000001', 'CSV JDBC', 'org.relique.jdbc.csv.CsvDriver', 'jdbc:relique:csv:{database}')");
            orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000016-00000002',NULL,'CSV JDBC','./jdbc/csvjdbc.jar','CSV','org.relique.jdbc.csv.CsvDriver','jdbc:relique:csv:{database}')");
            Logger.getLogger("orbada").info("IDE:UPDATE: Driver CSV JDBC added");
            orbadaDatabase.executeCommand(
              "insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) " +
              "values ('20071001000000-0000000000000070-00000010', '20071001000000-0000000000000025-00000001', 'StelsCSV JDBC Driver', 'jstels.jdbc.csv.CsvDriver2', 'jdbc:jstels:csv:{database}')");
            orbadaDatabase.executeCommand("INSERT INTO DRIVERS VALUES('20071001000000-0000000000000017-00000002',NULL,'StelsCSV JDBC Driver','./jdbc/csvdriver.jar','CSV','jstels.jdbc.csv.CsvDriver2','jdbc:jstels:csv:{database}')");
            Logger.getLogger("orbada").info("IDE:UPDATE: StelsCSV JDBC Driver added");
          } catch (Exception e) {
            ExceptionUtil.processException(e);
          }
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 141) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommand("alter table drivers add drv_extra_library varchar(4000)");
            Logger.getLogger("orbada").info("IDE:UPDATE: Table drivers alteres");
          } catch (Exception e) {
            ExceptionUtil.processException(e);
          }
        }
        if ((lastVersionID == null || lastVersionID.getBuild() < 145) && Application.get().isUserAdmin()) {
          try {
            orbadaDatabase.executeCommand(
              "INSERT INTO DRIVERS (DRV_ID, DRV_USR_ID, DRV_NAME, DRV_LIBRARY_SOURCE, DRV_TYPE_NAME, DRV_CLASS_NAME, DRV_URL_TEMPLATE) " +
              "VALUES('20071001000000-0000000000000018-00000002',NULL,'Oracle JDBC/ODBC',null,'Oracle','sun.jdbc.odbc.JdbcOdbcDriver','jdbc:odbc:{database}')");
            Logger.getLogger("orbada").info("IDE:UPDATE: Driver Oracle JDBC/ODBC added");
            orbadaDatabase.executeCommand(
              "INSERT INTO DRIVERS (DRV_ID, DRV_USR_ID, DRV_NAME, DRV_LIBRARY_SOURCE, DRV_TYPE_NAME, DRV_CLASS_NAME, DRV_URL_TEMPLATE) " +
              "VALUES('20071001000000-0000000000000019-00000002',NULL,'Firebird JDBC/ODBC',null,'Firebird','sun.jdbc.odbc.JdbcOdbcDriver','jdbc:odbc:{database}')");
            Logger.getLogger("orbada").info("IDE:UPDATE: Driver Firebird JDBC/ODBC added");
          } catch (Exception e) {
            ExceptionUtil.processException(e);
          }
        }
        if (lastVersionID == null || lastVersionID.getBuild() < 233) {
          new Orbada(orbadaDatabase, Application.get().getUserId(), "updated").applyDelete();
          new Orbada(orbadaDatabase, Application.get().getUserId(), "last-update").applyDelete();
        }
        if (lastVersionID == null || lastVersionID.getBuild() < 256) {
          orbadaDatabase.executeCommand("insert into driver_type_specs (dts_id, dts_dtp_id, dts_name, dts_class, dts_url_template) values ('20071001000000-0000000000000071-00000010', '20071001000000-0000000000000002-00000001', 'Thin SERVICE_NAME', 'oracle.jdbc.OracleDriver', 'jdbc:oracle:thin:@{host}:{port}[1521]/{database}')");
        }
      } finally {
        query.close();
      }
    } catch (Exception e) {
      ExceptionUtil.processException(e);
    }
  }

  public static void registerDriverType(String driverType) {
    if (InternalDatabase.get() == null) {
      return;
    }
    if (!StringUtil.isEmpty(driverType)) {
      Query query;
      try {
        query = new Query(orbadaDatabase);
        try {
          query.setSqlText("select * from driver_types where dtp_name = :driver_type");
          query.paramByName("driver_type").setString(driverType);
          query.open();
          if (query.eof()) {
            Command command = new Command(orbadaDatabase);
            command.setSqlText("INSERT INTO DRIVER_TYPES VALUES('" +new UniqueID().toString() +"', :DRIVER_TYPE)");
            command.paramByName("DRIVER_TYPE").setString(driverType);
            command.execute();
          }
        } finally {
          query.close();
        }
      } catch (Exception e) {
        ExceptionUtil.processException(e);
      }
    }
  }
  
}
