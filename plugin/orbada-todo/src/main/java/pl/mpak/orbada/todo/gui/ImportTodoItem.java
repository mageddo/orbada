/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.todo.gui;

import java.io.IOException;
import pl.mpak.orbada.todo.db.Todo;
import pl.mpak.usedb.UseDBException;
import pl.mpak.util.variant.VariantException;

/**
 *
 * @author akaluza
 */
public class ImportTodoItem {
  
  private Todo newTodo;
  private Todo todo;
  private boolean insert;
  private boolean update;
  private boolean importTodo;

  public ImportTodoItem(Todo newTodo) throws UseDBException, VariantException, IOException {
    this.newTodo = newTodo;
    todo = new Todo(newTodo.fieldByName("TD_ID").getString());
    insert = todo.fieldByName("TD_ID").isNull();
    if (!insert) {
      update = newTodo.fieldByName("TD_UPDATED").getLong() > todo.fieldByName("TD_UPDATED").getLong();
    }
    else {
      update = true;
    }
    importTodo = insert || update;
  }
  
  public boolean isInsert() {
    return insert;
  }

  public Todo getNewTodo() {
    return newTodo;
  }

  public Todo getTodo() {
    return todo;
  }

  public boolean isUpdate() {
    return update;
  }

  public boolean isImportTodo() {
    return importTodo;
  }

  public void setImportTodo(boolean importTodo) {
    this.importTodo = importTodo;
  }
  
  public void updateTodo() throws Exception {
    if (importTodo) {
      if (insert) {
        newTodo.applyInsert();
      }
      else if (update) {
        newTodo.applyUpdate();
      }
    }
  }
  
}
