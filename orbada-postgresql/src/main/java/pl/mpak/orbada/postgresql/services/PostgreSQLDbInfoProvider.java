/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.postgresql.services;

import java.awt.Component;
import java.sql.SQLException;
import java.util.EventObject;
import java.util.HashMap;
import pl.mpak.orbada.plugins.dbinfo.DbDatabaseInfo;
import pl.mpak.orbada.plugins.dbinfo.DbObjectContainer;
import pl.mpak.orbada.plugins.dbinfo.jdbc.JdbcDbDatabaseInfo;
import pl.mpak.orbada.plugins.providers.DatabaseInfoProvider;
import pl.mpak.orbada.postgresql.OrbadaPostgreSQLPlugin;
import pl.mpak.orbada.postgresql.Sql;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.DatabaseListener;
import pl.mpak.usedb.core.ExecutableListener;
import pl.mpak.usedb.core.ParametrizedCommand;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.util.QueryUtil;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class PostgreSQLDbInfoProvider extends DatabaseInfoProvider {

  private final StringManager stringManager = StringManagerFactory.getStringManager("postgresql");

  public static PostgreSQLDbInfoProvider instance;

  private HashMap<String, DbDatabaseInfo> databaseInfoList;
  private HashMap<String, HashMap<String, String[]>> databaseStringList;

  public PostgreSQLDbInfoProvider() {
    instance = this;
    if (databaseInfoList == null) {
      databaseInfoList = new HashMap<String, DbDatabaseInfo>();
    }
    if (databaseStringList == null) {
      databaseStringList = new HashMap<String, HashMap<String, String[]>>();
    }
  }

  @Override
  public boolean isForDatabase(Database database) {
    if (database == null) {
      return false;
    }
    return OrbadaPostgreSQLPlugin.driverType.equals(database.getDriverType());
  }

  @Override
  public String getDescription() {
    return stringManager.getString("PostgreSQLDbInfoProvider-description");
  }

  @Override
  public String getBanner(Database database) {
    Query query = database.createQuery();
    try {
      query.open(Sql.getVersion());
      return String.format(stringManager.getString("PostgreSQLDbInfoProvider-banner"), new Object[] {query.fieldByName("v").getString()});
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
    }
    return null;
  }

  @Override
  public String getVersion(Database database) {
    try {
      return database.getMetaData().getDatabaseProductVersion();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
      return null;
    }
  }

  private String getUniqueName(Database database) {
    return database.getUniqueID();
  }

  public String[] getContainerList(DbObjectContainer list) {
    if (list != null) {
      return list.namesArray();
    }
    return new String[] {};
  }

  @Override
  public void resetDatabaseInfo(Database database) {
    databaseInfoList.remove(getUniqueName(database));
    databaseStringList.remove(getUniqueName(database));
  }

  private String[] getStrings(Database database, String type) {
    HashMap<String, String[]> result = databaseStringList.get(getUniqueName(database));
    if (result == null) {
      return null;
    }
    return result.get(type);
  }

  public String[] putStrings(Database database, String type, String[] array) {
    HashMap<String, String[]> list = databaseStringList.get(getUniqueName(database));
    if (list == null) {
      list = new HashMap<String, String[]>();
      databaseStringList.put(getUniqueName(database), list);
    }
    list.put(type, array);
    return array;
  }

  @Override
  public String[] getKeywords(Database database) {
    String[] array = getStrings(database, "KEYWORDS");
    if (array == null) {
      array = putStrings(database, "KEYWORDS", QueryUtil.queryToArray(database, Sql.getKeywordList()));
    }
    array = StringUtil.unionList(
      array, 
      new String[] {
        "return", "perform", "raise", "notice", "exception", "rowtype", "loop", "elsif", "while", "exit", "sql", "plpgsql", "epoch"
      });
    return array;
  }

  @Override
  public String[] getOperators(Database database) {
    String[] list = {"ALL", "ANY", "BETWEEN", "IN", "IS", "LIKE", "NOT", "EXISTS", "EACH"};
    return list;
  }

  @Override
  public String[] getUserTables(Database database) {
    String[] array = getStrings(database, "USER_TABLES");
    if (array == null) {
      array = putStrings(database, "USER_TABLES", QueryUtil.queryToArray(database, Sql.getUserTables()));
    }
    return array;
  }

  @Override
  public String[] getExceptions(Database database) {
    String[] list = {
      "warning", "dynamic_result_sets_returned", "implicit_zero_bit_padding", "null_value_eliminated_in_set_function", "privilege_not_granted", 
      "privilege_not_revoked", "string_data_right_truncation", "deprecated_feature", "no_data", "no_additional_dynamic_result_sets_returned", 
      "sql_statement_not_yet_complete", "connection_exception", "connection_does_not_exist", "connection_failure", 
      "sqlclient_unable_to_establish_sqlconnection", "sqlserver_rejected_establishment_of_sqlconnection", "transaction_resolution_unknown", 
      "protocol_violation", "triggered_action_exception", "feature_not_supported", "invalid_transaction_initiation", "locator_exception", 
      "invalid_locator_specification", "invalid_grantor", "invalid_grant_operation", "invalid_role_specification", "case_not_found", 
      "cardinality_violation", "data_exception", "array_subscript_error", "character_not_in_repertoire", "datetime_field_overflow", 
      "division_by_zero", "error_in_assignment", "escape_character_conflict", "indicator_overflow", "interval_field_overflow", 
      "invalid_argument_for_logarithm", "invalid_argument_for_ntile_function", "invalid_argument_for_nth_value_function", 
      "invalid_argument_for_power_function", "invalid_argument_for_width_bucket_function", "invalid_character_value_for_cast", 
      "invalid_datetime_format", "invalid_escape_character", "invalid_escape_octet", "invalid_escape_sequence", 
      "nonstandard_use_of_escape_character", "invalid_indicator_parameter_value", "invalid_parameter_value", "invalid_regular_expression", 
      "invalid_row_count_in_limit_clause", "invalid_row_count_in_result_offset_clause", "invalid_time_zone_displacement_value", 
      "invalid_use_of_escape_character", "most_specific_type_mismatch", "null_value_not_allowed", "null_value_no_indicator_parameter", 
      "numeric_value_out_of_range", "string_data_length_mismatch", "string_data_right_truncation", "substring_error", "trim_error", 
      "unterminated_c_string", "zero_length_character_string", "floating_point_exception", "invalid_text_representation", 
      "invalid_binary_representation", "bad_copy_file_format", "untranslatable_character", "not_an_xml_document", "invalid_xml_document", 
      "invalid_xml_content", "invalid_xml_comment", "invalid_xml_processing_instruction", "integrity_constraint_violation", 
      "restrict_violation", "not_null_violation", "foreign_key_violation", "unique_violation", "check_violation", "exclusion_violation", 
      "invalid_cursor_state", "invalid_transaction_state", "active_sql_transaction", "branch_transaction_already_active", 
      "held_cursor_requires_same_isolation_level", "inappropriate_access_mode_for_branch_transaction", 
      "inappropriate_isolation_level_for_branch_transaction", "no_active_sql_transaction_for_branch_transaction", 
      "read_only_sql_transaction", "schema_and_data_statement_mixing_not_supported", "no_active_sql_transaction", 
      "in_failed_sql_transaction", "invalid_sql_statement_name", "triggered_data_change_violation", "invalid_authorization_specification", 
      "invalid_password", "dependent_privilege_descriptors_still_exist", "dependent_objects_still_exist", "invalid_transaction_termination", 
      "sql_routine_exception", "function_executed_no_return_statement", "modifying_sql_data_not_permitted", 
      "prohibited_sql_statement_attempted", "reading_sql_data_not_permitted", "invalid_cursor_name", "external_routine_exception", 
      "containing_sql_not_permitted", "modifying_sql_data_not_permitted", "prohibited_sql_statement_attempted", "reading_sql_data_not_permitted", 
      "external_routine_invocation_exception", "invalid_sqlstate_returned", "null_value_not_allowed", "trigger_protocol_violated", 
      "srf_protocol_violated", "savepoint_exception", "invalid_savepoint_specification", "invalid_catalog_name", "invalid_schema_name", 
      "transaction_rollback", "transaction_integrity_constraint_violation", "serialization_failure", "statement_completion_unknown", 
      "deadlock_detected", "syntax_error_or_access_rule_violation", "syntax_error", "insufficient_privilege", "cannot_coerce", "grouping_error", 
      "windowing_error", "invalid_recursion", "invalid_foreign_key", "invalid_name", "name_too_long", "reserved_name", "datatype_mismatch", 
      "indeterminate_datatype", "collation_mismatch", "indeterminate_collation", "wrong_object_type", "undefined_column", "undefined_function", 
      "undefined_table", "undefined_parameter", "undefined_object", "duplicate_column", "duplicate_cursor", "duplicate_database", 
      "duplicate_function", "duplicate_prepared_statement", "duplicate_schema", "duplicate_table", "duplicate_alias", "duplicate_object", 
      "ambiguous_column", "ambiguous_function", "ambiguous_parameter", "ambiguous_alias", "invalid_column_reference", "invalid_column_definition", 
      "invalid_cursor_definition", "invalid_database_definition", "invalid_function_definition", "invalid_prepared_statement_definition", 
      "invalid_schema_definition", "invalid_table_definition", "invalid_object_definition", "with_check_option_violation", "insufficient_resources", 
      "disk_full", "out_of_memory", "too_many_connections", "program_limit_exceeded", "statement_too_complex", "too_many_columns", 
      "too_many_arguments", "object_not_in_prerequisite_state", "object_in_use", "cant_change_runtime_param", "lock_not_available", 
      "operator_intervention", "query_canceled", "admin_shutdown", "crash_shutdown", "cannot_connect_now", "database_dropped", "io_error", 
      "undefined_file", "duplicate_file", "config_file_error", "lock_file_exists", "fdw_error", "fdw_column_name_not_found", 
      "fdw_dynamic_parameter_value_needed", "fdw_function_sequence_error", "fdw_inconsistent_descriptor_information", "fdw_invalid_attribute_value", 
      "fdw_invalid_column_name", "fdw_invalid_column_number", "fdw_invalid_data_type", "fdw_invalid_data_type_descriptors", 
      "fdw_invalid_descriptor_field_identifier", "fdw_invalid_handle", "fdw_invalid_option_index", "fdw_invalid_option_name", 
      "fdw_invalid_string_length_or_buffer_length", "fdw_invalid_string_format", "fdw_invalid_use_of_null_pointer", "fdw_too_many_handles", 
      "fdw_out_of_memory", "fdw_no_schemas", "fdw_option_name_not_found", "fdw_reply_handle", "fdw_schema_not_found", "fdw_table_not_found", 
      "fdw_unable_to_create_execution", "fdw_unable_to_create_reply", "fdw_unable_to_establish_connection", "plpgsql_error", "raise_exception", 
      "no_data_found", "too_many_rows", "internal_error", "data_corrupted", "index_corrupted", "others"
    };
    return list;
  }

  @Override
  public String[] getSqlFunctions(Database database) {
    String[] array = getStrings(database, "SYSTEM_FUNCTIONS");
    if (array == null) {
      array = putStrings(database, "SYSTEM_FUNCTIONS", QueryUtil.queryToArray(database, Sql.getSystemFunctionList()));
    }
    array = StringUtil.unionList(
      array, 
      new String[] {
        "version", "coalesce", "replace"
      });
    return array;
  }

  @Override
  public String[] getUserFunctions(final Database database) {
    String[] array = getStrings(database, "USER_FUNCTIONS");
    if (array == null) {
      array = putStrings(database, "USER_FUNCTIONS", QueryUtil.queryToArray(database, Sql.getUserFunctionList()));
    }
    return array;
  }

  @Override
  public String[] getPublicTables(final Database database) {
    String[] array = getStrings(database, "SYSTEM_TABLES");
    if (array == null) {
      array = putStrings(database, "SYSTEM_TABLES", QueryUtil.queryToArray(database, Sql.getSystemTables()));
    }
    array = StringUtil.unionList(array, getSchemas(database));
    return array;
  }

  @Override
  public String[] getDataTypes(final Database database) {
    String[] array = getStrings(database, "DATA_TYPES");
    if (array == null) {
      array = putStrings(database, "DATA_TYPES", QueryUtil.queryToArray(database, Sql.getDataTypes()));
    }
    array = StringUtil.unionList(
      array, 
      new String[] {
        "name", "serial", "bigserial", "record"
      });
    return array;
  }
  
  public static String getConnectedSchema(final Database database) {
    return database.getUserProperties().getProperty("schema-name");
  }

  @Override
  public String[] getSchemas(final Database database) {
    String[] array = getStrings(database, "SCHEMAS");
    if (array == null) {
      try {
        //array = putStrings(database, "SCHEMAS", QueryUtil.queryToArray(database, "select nspname from pg_catalog.pg_namespace order by 1"));
        array = putStrings(database, "SCHEMAS", database.getSchemaArray());
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
    return array;
  }

  @Override
  public String[] getTableTypes(final Database database) {
    String[] list = {};
    String[] tableTypes = getContainerList((DbObjectContainer)getDatabaseInfo(database).getObjectInfo("/TABLE TYPES"));
    list = StringUtil.unionList(list, tableTypes);
    return list;
  }

  @Override
  public Component[] getExtendedPanelInfo(Database database) {
    return null;
  }

  @Override
  public DbDatabaseInfo getDatabaseInfo(final Database database) {
    synchronized (this) {
      DbDatabaseInfo info = databaseInfoList.get(getUniqueName(database));
      if (info == null) {
        databaseInfoList.put(getUniqueName(database), info = new JdbcDbDatabaseInfo(database));
        database.addDatabaseListener(new DatabaseListener() {
          @Override
          public void beforeConnect(EventObject e) {
          }
          @Override
          public void afterConnect(EventObject e) {
          }
          @Override
          public void beforeDisconnect(EventObject e) {
          }
          @Override
          public void afterDisconnect(EventObject e) {
            databaseInfoList.remove(getUniqueName(database));
            databaseStringList.remove(getUniqueName(database));
          }
        });
        database.addExecutableListener(new ExecutableListener() {
          @Override
          public boolean canExecute(EventObject e) throws SQLException {
            return parseCommand((ParametrizedCommand)e.getSource());
          }
        });
      }
      return info;
    }
  }

  private boolean parseCommand(ParametrizedCommand command) throws SQLException {
//    if (settings.getValue(FirebirdGeneralSettingsProvider.SET_ConnectionTransaction, false)) {
//      if (pattCommit.matcher(command.getSqlText()).find()) {
//        command.getDatabase().commit();
//        return false;
//      }
//      else if (pattRollback.matcher(command.getSqlText()).find()) {
//        command.getDatabase().rollback();
//        return false;
//      }
//    }
    return true;
  }

  @Override
  public String[] getUniqueIdentFields(Database database, String catalogName, String schemaName, String tableName) {
    String[] result = super.getUniqueIdentFields(database, catalogName, schemaName, tableName);
    if (result == null || result.length == 0) {
      return new String[] {"OID"};
    }
    return result;
  }

  @Override
  public String getGroupName() {
    return OrbadaPostgreSQLPlugin.driverType;
  }

}
