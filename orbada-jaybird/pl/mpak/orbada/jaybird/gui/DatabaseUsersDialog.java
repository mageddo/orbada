/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DatabaseUsersDialog.java
 *
 * Created on 2009-05-23, 22:37:18
 */

package pl.mpak.orbada.jaybird.gui;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.firebirdsql.management.FBUserManager;
import org.firebirdsql.management.User;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.jaybird.OrbadaJaybirdPlugin;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.TableColumn;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DatabaseUsersDialog extends javax.swing.JDialog {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(OrbadaJaybirdPlugin.class);
  private Thread worker;
  private FBUserManager manager;
  private OutputStream logger;
  private ArrayList<User> userList;
  private ISettings settings;

  /** Creates new form DatabaseUsersDialog */
  public DatabaseUsersDialog() {
    super(SwingUtil.getRootFrame(), true);
    initComponents();
    init();
  }

  public static void showDialog() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        DatabaseUsersDialog dialog = new DatabaseUsersDialog();
        dialog.setVisible(true);
      }
    });
  }

  private void init() {
    settings = Application.get().getSettings("jaybird-server");
    userList = new ArrayList<User>();
    tableUsers.setModel(getUserTableModel());
    tableUsers.addColumn(new TableColumn(1, 40, stringManager.getString("Id")));
    tableUsers.addColumn(new TableColumn(2, 150, stringManager.getString("User")));
    tableUsers.addColumn(new TableColumn(3, 200, stringManager.getString("FullUserName")));
    tableUsers.setEnabled(false);

    textHost.setText(settings.getValue("host", textHost.getText()));
    textUser.setText(settings.getValue("user-name", textUser.getText()));
    textPort.setText(settings.getValue("port", textPort.getText()));

    getRootPane().setDefaultButton(buttonOk);
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmClose.getShortCut(), "cmCancel");
    getRootPane().getActionMap().put("cmCancel", cmClose);
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonOk, buttonCancel});
    SwingUtil.setButtonSizesTheSame(new AbstractButton[] {buttonNew, buttonEdit, buttonDelete});
    SwingUtil.centerWithinScreen(this);
  }

  public void dispose() {
    settings.setValue("host", textHost.getText());
    settings.setValue("user-name", textUser.getText());
    settings.setValue("port", textPort.getText());
    settings.store();
    super.dispose();
  }

  private void enableControls(final boolean value) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        textHost.setEnabled(value);
        textUser.setEnabled(value);
        textPassword.setEnabled(value);
        textPort.setEnabled(value);
        cmGet.setEnabled(value);
        cmClose.setEnabled(value);
        tableUsers.setEnabled(value);
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            cmNew.setEnabled(value && manager != null);
            cmEdit.setEnabled(value && tableUsers.getSelectedRow() >= 0);
            cmDelete.setEnabled(value && tableUsers.getSelectedRow() >= 0);
          }
        });
      }
    });
  }

  private TableModel getUserTableModel() {
    return new AbstractTableModel() {
      public int getRowCount() {
        return userList.size();
      }

      public int getColumnCount() {
        return 0;
      }

      public Object getValueAt(int rowIndex, int columnIndex) {
        User u = userList.get(rowIndex);
        switch (columnIndex) {
          case 1: return u.getUserId();
          case 2: return u.getUserName();
          case 3: {
            StringBuffer sb = new StringBuffer();
            if (u.getFirstName() != null) {
              sb.append(u.getFirstName());
            }
            if (u.getMiddleName() != null) {
              if (sb.length() > 0) {
                sb.append(" ");
              }
              sb.append(u.getMiddleName());
            }
            if (u.getLastName() != null) {
              if (sb.length() > 0) {
                sb.append(" ");
              }
              sb.append(u.getLastName());
            }
            return sb.toString();
          }
        }
        return null;
      }
    };
  }

  private OutputStream getLogger() {
    if (logger == null) {
      logger = new OutputStream() {
        final StringBuffer sb = new StringBuffer();
        @Override
        public void write(int b) throws IOException {
          synchronized (sb) {
            sb.append((char)b);
          }
          if (sb.length() > 10000) {
            flush();
          }
        }
        @Override
        public void flush() throws IOException {
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              synchronized (sb) {
                textLog.append(sb.toString());
                sb.setLength(0);
              }
            }
          });
        }
      };
    }
    return logger;
  }

  private void getUserList() {
    manager = new FBUserManager();
    try {
      enableControls(false);
      manager.setHost(textHost.getText());
      manager.setUser(textUser.getText());
      manager.setPassword(new String(textPassword.getPassword()));
      manager.setPort(Integer.valueOf(textPort.getText()));
      manager.setLogger(getLogger());
      worker = new Thread("Firbird Database User List") {
        @Override
        public void run() {
          try {
            userList.clear();
            Map map = manager.getUsers();
            for (Object o : map.values()) {
              if (o instanceof User) {
                userList.add((User)o);
              }
            }
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                tableUsers.revalidate();
                tabbed.setSelectedComponent(panelUsers);
                if (userList.size() > 0) {
                  tableUsers.changeSelection(0, 1);
                  tableUsers.requestFocusInWindow();
                }
              }
            });
          } catch (final Exception ex) {
            manager = null;
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("Error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
              }
            });
          }
          finally {
            try {
              getLogger().flush();
            } catch (IOException ex) {
            }
            enableControls(true);
          }
        }
      };
      worker.start();
    }
    catch (Throwable ex) {
      ExceptionUtil.processException(ex);
      enableControls(true);
    }
  }

  private void newUser(final User user) {
    try {
      enableControls(false);
      worker = new Thread("Firbird Database New User") {
        @Override
        public void run() {
          try {
            manager.add(user);
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                userList.add(user);
                tableUsers.revalidate();
                tableUsers.changeSelection(userList.size() -1, 1);
              }
            });
          } catch (final Exception ex) {
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("Error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
              }
            });
          }
          finally {
            try {
              getLogger().flush();
            } catch (IOException ex) {
            }
            enableControls(true);
          }
        }
      };
      worker.start();
    }
    catch (Throwable ex) {
      ExceptionUtil.processException(ex);
      enableControls(true);
    }
  }

  private void updateUser(final User user) {
    try {
      enableControls(false);
      worker = new Thread("Firbird Database Update User") {
        @Override
        public void run() {
          try {
            manager.update(user);
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                tableUsers.revalidate();
              }
            });
          } catch (final Exception ex) {
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("Error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
              }
            });
          }
          finally {
            try {
              getLogger().flush();
            } catch (IOException ex) {
            }
            enableControls(true);
          }
        }
      };
      worker.start();
    }
    catch (Throwable ex) {
      ExceptionUtil.processException(ex);
      enableControls(true);
    }
  }

  private void deleteUser(final User user) {
    try {
      enableControls(false);
      worker = new Thread("Firbird Database Delete User") {
        @Override
        public void run() {
          try {
            manager.delete(user);
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                userList.remove(user);
                tableUsers.revalidate();
                if (userList.size() > 0) {
                  tableUsers.changeSelection(0, 1);
                }
              }
            });
          } catch (final Exception ex) {
            java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("Error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
              }
            });
          }
          finally {
            try {
              getLogger().flush();
            } catch (IOException ex) {
            }
            enableControls(true);
          }
        }
      };
      worker.start();
    }
    catch (Throwable ex) {
      ExceptionUtil.processException(ex);
      enableControls(true);
    }
  }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmGet = new pl.mpak.sky.gui.swing.Action();
    cmClose = new pl.mpak.sky.gui.swing.Action();
    cmNew = new pl.mpak.sky.gui.swing.Action();
    cmEdit = new pl.mpak.sky.gui.swing.Action();
    cmDelete = new pl.mpak.sky.gui.swing.Action();
    buttonOk = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    tabbed = new javax.swing.JTabbedPane();
    jPanel1 = new javax.swing.JPanel();
    jLabel2 = new javax.swing.JLabel();
    textHost = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel3 = new javax.swing.JLabel();
    textUser = new pl.mpak.sky.gui.swing.comp.TextField();
    jLabel4 = new javax.swing.JLabel();
    textPassword = new javax.swing.JPasswordField();
    jLabel6 = new javax.swing.JLabel();
    textPort = new pl.mpak.sky.gui.swing.comp.TextField();
    panelUsers = new javax.swing.JPanel();
    jScrollPane2 = new javax.swing.JScrollPane();
    tableUsers = new pl.mpak.orbada.gui.comps.table.Table();
    buttonNew = new javax.swing.JButton();
    buttonEdit = new javax.swing.JButton();
    buttonDelete = new javax.swing.JButton();
    panelLog = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textLog = new pl.mpak.sky.gui.swing.comp.TextArea();

    cmGet.setActionCommandKey("cmGet");
    cmGet.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmGet.setText(stringManager.getString("cmGet.text")); // NOI18N
    cmGet.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmGetActionPerformed(evt);
      }
    });

    cmClose.setActionCommandKey("cmClose");
    cmClose.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
    cmClose.setText(stringManager.getString("cmClose.text")); // NOI18N
    cmClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmCloseActionPerformed(evt);
      }
    });

    cmNew.setActionCommandKey("cmNew");
    cmNew.setEnabled(false);
    cmNew.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/new16.gif"))); // NOI18N
    cmNew.setText(stringManager.getString("cmNew.text")); // NOI18N
    cmNew.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmNewActionPerformed(evt);
      }
    });

    cmEdit.setActionCommandKey("cmEdit");
    cmEdit.setEnabled(false);
    cmEdit.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/edit16.gif"))); // NOI18N
    cmEdit.setText(stringManager.getString("cmEdit.text")); // NOI18N
    cmEdit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmEditActionPerformed(evt);
      }
    });

    cmDelete.setActionCommandKey("cmDelete");
    cmDelete.setEnabled(false);
    cmDelete.setSmallIcon(new javax.swing.ImageIcon(getClass().getResource("/pl/mpak/res/icons/trash.gif"))); // NOI18N
    cmDelete.setText(stringManager.getString("cmDelete.text")); // NOI18N
    cmDelete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmDeleteActionPerformed(evt);
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle(stringManager.getString("DatabaseUsersDialog.title")); // NOI18N

    buttonOk.setAction(cmGet);
    buttonOk.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonOk.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonCancel.setAction(cmClose);
    buttonCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonCancel.setPreferredSize(new java.awt.Dimension(85, 25));

    tabbed.setFocusable(false);

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText(stringManager.getString("Host-dd")); // NOI18N

    textHost.setText("localhost");

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText(stringManager.getString("User-dd")); // NOI18N

    textUser.setText("SYSDBA");

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText(stringManager.getString("Password-dd")); // NOI18N

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel6.setText(stringManager.getString("Port-dd")); // NOI18N

    textPort.setText("3050");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(textUser, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
              .addComponent(textHost, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
              .addComponent(textPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
              .addComponent(textPort, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(textHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(textPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel6)
          .addComponent(textPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(179, Short.MAX_VALUE))
    );

    tabbed.addTab(stringManager.getString("Settings"), jPanel1); // NOI18N

    jScrollPane2.setViewportView(tableUsers);

    buttonNew.setAction(cmNew);
    buttonNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonNew.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonEdit.setAction(cmEdit);
    buttonEdit.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonEdit.setPreferredSize(new java.awt.Dimension(85, 25));

    buttonDelete.setAction(cmDelete);
    buttonDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
    buttonDelete.setPreferredSize(new java.awt.Dimension(85, 25));

    javax.swing.GroupLayout panelUsersLayout = new javax.swing.GroupLayout(panelUsers);
    panelUsers.setLayout(panelUsersLayout);
    panelUsersLayout.setHorizontalGroup(
      panelUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(panelUsersLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(panelUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
          .addGroup(panelUsersLayout.createSequentialGroup()
            .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    panelUsersLayout.setVerticalGroup(
      panelUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUsersLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(panelUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    tabbed.addTab(stringManager.getString("Users"), panelUsers); // NOI18N

    textLog.setColumns(20);
    textLog.setEditable(false);
    textLog.setRows(5);
    textLog.setFont(new java.awt.Font("Monospaced", 0, 12));
    jScrollPane1.setViewportView(textLog);

    javax.swing.GroupLayout panelLogLayout = new javax.swing.GroupLayout(panelLog);
    panelLog.setLayout(panelLogLayout);
    panelLogLayout.setHorizontalGroup(
      panelLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(panelLogLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
        .addContainerGap())
    );
    panelLogLayout.setVerticalGroup(
      panelLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(panelLogLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        .addContainerGap())
    );

    tabbed.addTab("Log", panelLog);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(tabbed, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(tabbed, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void cmGetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmGetActionPerformed
      getUserList();
}//GEN-LAST:event_cmGetActionPerformed

    private void cmCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloseActionPerformed
      dispose();
}//GEN-LAST:event_cmCloseActionPerformed

    private void cmNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewActionPerformed
      User user = UserEditDialog.showDialog(null);
      if (user != null) {
        newUser(user);
      }
}//GEN-LAST:event_cmNewActionPerformed

    private void cmEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmEditActionPerformed
      if (tableUsers.getSelectedRow() >= 0) {
        User user = UserEditDialog.showDialog(userList.get(tableUsers.getSelectedRow()));
        if (user != null) {
          updateUser(user);
        }
      }
}//GEN-LAST:event_cmEditActionPerformed

    private void cmDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeleteActionPerformed
      if (tableUsers.getSelectedRow() >= 0) {
        if (MessageBox.show(this, stringManager.getString("Deletion"), stringManager.getString("UserDelete.query_message"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
          deleteUser(userList.get(tableUsers.getSelectedRow()));
        }
      }
}//GEN-LAST:event_cmDeleteActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonDelete;
  private javax.swing.JButton buttonEdit;
  private javax.swing.JButton buttonNew;
  private javax.swing.JButton buttonOk;
  private pl.mpak.sky.gui.swing.Action cmClose;
  private pl.mpak.sky.gui.swing.Action cmDelete;
  private pl.mpak.sky.gui.swing.Action cmEdit;
  private pl.mpak.sky.gui.swing.Action cmGet;
  private pl.mpak.sky.gui.swing.Action cmNew;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JPanel panelLog;
  private javax.swing.JPanel panelUsers;
  private javax.swing.JTabbedPane tabbed;
  private pl.mpak.orbada.gui.comps.table.Table tableUsers;
  private pl.mpak.sky.gui.swing.comp.TextField textHost;
  private pl.mpak.sky.gui.swing.comp.TextArea textLog;
  private javax.swing.JPasswordField textPassword;
  private pl.mpak.sky.gui.swing.comp.TextField textPort;
  private pl.mpak.sky.gui.swing.comp.TextField textUser;
  // End of variables declaration//GEN-END:variables

}
