/*
 * MainFrame.java
 *
 * Created on 18 stycze� 2007, 19:38
 */

package pl.mpak.orbada.gui;

import java.util.Arrays;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.OrbadaCancelCloseException;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.gui.admin.UserListDialog;
import pl.mpak.orbada.gui.cm.CreatePerspectiveAction;
import pl.mpak.orbada.gui.cm.CreatePerspectiveGadgetAction;
import pl.mpak.orbada.gui.cm.MovePerspectiveLeftAction;
import pl.mpak.orbada.gui.cm.MovePerspectiveRightAction;
import pl.mpak.orbada.gui.cm.PerspectivePropertiesAction;
import pl.mpak.orbada.gui.comps.OrbadaJavaSyntaxDocument;
import pl.mpak.orbada.gui.comps.OrbadaSQLSyntaxDocument;
import pl.mpak.orbada.gui.comps.PerspectiveContextMenu;
import pl.mpak.orbada.gui.comps.table.DataTable;
import pl.mpak.orbada.gui.laf.services.CrossPlatformLookAndFeelService;
import pl.mpak.orbada.gui.laf.services.CrossPlatformLookAndFeelSettingsService;
import pl.mpak.orbada.gui.laf.services.MotifLookAndFeelService;
import pl.mpak.orbada.gui.laf.services.NimbusLookAndFeelService;
import pl.mpak.orbada.gui.laf.services.SystemLookAndFeelService;
import pl.mpak.orbada.gui.laf.services.WindowsLookAndFeelService;
import pl.mpak.orbada.gui.schemas.DriverListDialog;
import pl.mpak.orbada.gui.schemas.SchemaListDialog;
import pl.mpak.orbada.gui.services.GeneralSchemaSettingsService;
import pl.mpak.orbada.gui.services.TextFileViewService;
import pl.mpak.orbada.gui.templates.TemplateListDialog;
import pl.mpak.orbada.gui.tools.ToolListDialog;
import pl.mpak.orbada.gui.webapp.RequestProblemDialog;
import pl.mpak.orbada.gui.webapp.RequestSuggestionDialog;
import pl.mpak.orbada.services.DefaultPleaseWaitRenderer;
import pl.mpak.orbada.services.TimeOrbadaFontPleaseWaitRenderer;
import pl.mpak.orbada.util.Utils;
import pl.mpak.orbada.util.tools.ToolAction;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.FocusManager;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import json.JSONObject;
import pl.mpak.orbada.db.Perspective;
import pl.mpak.orbada.db.Schema;
import pl.mpak.orbada.db.Tool;
import pl.mpak.orbada.gui.cm.CreateViewAction;
import pl.mpak.orbada.plugins.IProcessMessagable;
import pl.mpak.orbada.plugins.ISettings;
import pl.mpak.orbada.plugins.providers.DatabaseInfoProvider;
import pl.mpak.orbada.plugins.providers.GlobalActionProvider;
import pl.mpak.orbada.plugins.providers.GlobalFocusProvider;
import pl.mpak.orbada.plugins.providers.GlobalMenuProvider;
import pl.mpak.orbada.plugins.providers.GlobalToolBarProvider;
import pl.mpak.orbada.plugins.providers.IDatabaseProvider;
import pl.mpak.orbada.plugins.providers.ImportToolActionProvider;
import pl.mpak.orbada.plugins.providers.PerpectiveGadgetProvider;
import pl.mpak.orbada.plugins.providers.StatusBarProvider;
import pl.mpak.orbada.plugins.providers.ToolConfigurationActionProvider;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.sky.SkySetting;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.TabCloseComponent;
import pl.mpak.sky.gui.swing.comp.ToolButton;
import pl.mpak.sky.gui.swing.syntax.ApplySyntaxTextAreaEvent;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.util.DisplayChanger;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StreamUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;
import pl.mpak.util.id.VersionID;
import pl.mpak.util.patt.Resolvers;
import pl.mpak.util.timer.Timer;
import pl.mpak.util.timer.TimerManager;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author  akaluza
 */
public class MainFrame extends javax.swing.JFrame implements IProcessMessagable {
  
  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private PerspectivePanel activePerspective;
  private LinkedList<GlobalFocusProvider> globalFocusSerives;
  private PerspectivePanel toolsPerspective;
  private boolean cancelClose;
  private String oryginalTitle;
  private GlassPane glassPane;
  private JSONObject updateInfo;
  private long lastCheckUpdates;
  private boolean newVersionPresents;
  private DisplayChanger displayChanger;
  private boolean fullScreenMode = false;
  
  private PerspectivePropertiesAction cmPerspectiveProperties;
  private MovePerspectiveLeftAction cmMovePerspectiveLeft;
  private MovePerspectiveRightAction cmMovePerspectiveRight;

  public MainFrame() {
    displayChanger = new DisplayChanger(this);
    displayChanger.setExclusiveMode(false);
    initComponents();
    init();
  }
  
  private void init() {
    buttonDonation.setVisible(false);

    Application.get().registerRequestMessager(this);
    menuSettings.setText(SwingUtil.setButtonText(menuSettings, stringManager.getString("mf-menuSettings-text"))); // NOI18N
    menuSpecialFiles.setText(SwingUtil.setButtonText(menuSpecialFiles, stringManager.getString("mf-menuSpecialFiles-text"))); // NOI18N
    menuSelectNewPerspective.setText(SwingUtil.setButtonText(menuSelectNewPerspective, stringManager.getString("mf-menuSelectNewPerspective-text"))); // NOI18N
    menuPerspectiveGadgets.setText(SwingUtil.setButtonText(menuPerspectiveGadgets, stringManager.getString("mf-menuPerspectiveGadgets-text"))); // NOI18N
    menuNavigation.setText(SwingUtil.setButtonText(menuNavigation, stringManager.getString("mf-menuNavigation-text"))); // NOI18N
    menuDocumentation.setVisible(new File(Resolvers.expand("$(user.dir)/doc/index.html")).exists());

    panelToolTip.setVisible(false);
    try {
      setIconImage(getToolkit().createImage(getClass().getResource("/res/icons/orbada32.png")));
    } catch (Throwable ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }

    tabbedPerpectives.setComponentPopupMenu(new PerspectiveContextMenu(tabbedPerpectives));
    setSize(800, 600);
    setExtendedState(Frame.MAXIMIZED_BOTH);
    setActivePerspective(null);
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        reloadToolsMenu();
      }
    });
    applySettings();
    cmUserList.setEnabled(Application.get().isUserAdmin());

    menuUserGuide.setVisible(false);
  }
  
  private void checkDonation() {
    if (Application.get().getLastVersion() != null && Application.get().getLastVersion().getBuild() < Consts.orbadaVersion.getBuild()) {
      buttonDonation.setVisible(true);
      TimerManager.getGlobal().add(new Timer(60000, true) {
        @Override
        public void run() {
          java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
              buttonDonation.setVisible(false);
            }
          });
        }
      });
    }
  }

  public void afterInit() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        checkDonation();
        checkUpdates();
        if (Application.get().getSettings().getValue(Consts.newConnectionAtStartup, true)) {
          if (!Application.get().isFirstRun() ||
              Application.get().getPluginManager().getPluginByUniqueID(Consts.orbadaWelcomePluginId) == null) {
            cmNewConnection.performe();
          }
        }
      }
    });
  }

  /**
   * Funkcja s�u�y do sprawdzania czy dost�pna jest aktualizacja.
   */
  public void checkUpdates() {
    lastCheckUpdates = System.currentTimeMillis();
    newVersionPresents = false;
    menuGetUpdates.setVisible(false);
    menuLastChanges.setVisible(false);
    buttonGetUpdate.setVisible(false);
    updateInfo = null;
    if (!getSettings().getValue(Consts.disableCheckUpdates, false)) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          TimerManager.getGlobal().add(new Timer(1000, true) {
            @Override
            public void run() {
              try {
                URL url = new URL("http://orbada.sourceforge.net/changes_json.php");
                //URL url = new URL("http://orbada.sourceforge.net/changes.json");
                //URL url = new URL("http://localhost:8080/orbada/www_orbada_json.change_list");
                String source = StreamUtil.stream2String(url.openStream());
                try {
                  updateInfo = new JSONObject(source);
                  if (updateInfo.has("lastVersion")) {
                    JSONObject lastVersion = updateInfo.getJSONObject("lastVersion");
                    if (lastVersion != null) {
                      final String version = lastVersion.getString("version");
                      final String released = lastVersion.getString("released");
                      VersionID ver = new VersionID(version);
                      if (ver.getBuild() > Consts.orbadaVersion.getBuild()) {
                        newVersionPresents = true;
                        java.awt.EventQueue.invokeLater(new Runnable() {
                          @Override
                          public void run() {
                            menuGetUpdates.setVisible(true);
                            buttonGetUpdate.setVisible(true);
                            menuLastChanges.setVisible(false);
                            cmGetUpdate.setTooltip(
                              "<html>" +stringManager.getString("mf-cmGetUpdate-hint") +"<hr>" +
                              MainFrame.stringManager.getString("version-dd") +" <b>" +version +"</b><br>" + //NOI18N
                              MainFrame.stringManager.getString("released-dd") +" <b>" +released +"</b>" //NOI18N
                              );
                          }
                        });
                      }
                      else {
                        menuLastChanges.setVisible(true);
                      }
                    }
                  }
                } catch (Exception ex) {
                  Logger.getLogger("error-logger").error("Updates", ex); //NOI18N
                }
              } catch (Exception ex) {
              }
              // if occur any problem with update info file then we find him on local drive
              if (updateInfo == null) {
                File ui = new File("./doc/changes.json"); //NOI18N
                if (ui.exists()) {
                  try {
                    String source = StreamUtil.stream2String(new FileInputStream(ui));
                    updateInfo = new JSONObject(source);
                    menuLastChanges.setVisible(true);
                  } catch (Exception ex) {
                  }
                }
              }
            }
          });
        }
      });
    }
    else {
      cmGetUpdate.setEnabled(false);
    }
  }
  
  public void reloadToolsMenu() {
    toolBarTools.removeAll();
    menuTools.removeAll();
    for (Tool tool : Application.get().getToolList().toArray()) {
      if (tool.isMenu()) {
        menuTools.add(new JMenuItem(new ToolAction(tool)), 0);
      }
      if (tool.isToolButton()) {
        if (tool.getIcon() != null) {
          toolBarTools.add(new ToolButton(new ToolAction(tool)));
        }
        else {
          JButton button = new JButton(new ToolAction(tool));
          button.setFocusable(false);
          toolBarTools.add(button);
        }
      }
    }
    menuTools.addSeparator();
    menuTools.add(cmToolsSettings);
    menuTools.add(cmTemplateSettings);

    ImportToolActionProvider[] itapa = Application.get().getPluginManager().getServiceArray(ImportToolActionProvider.class);
    if (itapa != null && itapa.length > 0) {
      menuTools.addSeparator();
      JMenu im = new JMenu(stringManager.getString("import-menu")); //NOI18N
      menuTools.add(SwingUtil.updateMenuText(im));
      for (ImportToolActionProvider itap : itapa) {
        im.add(itap);
        if (itap.isButton()) {
          if (itap.getSmallIcon() != null) {
            toolBarTools.add(new ToolButton(itap));
          }
          else {
            JButton button = new JButton(itap);
            button.setFocusable(false);
            toolBarTools.add(button);
          }
        }
      }
    }
    
    ToolConfigurationActionProvider[] tcapa = Application.get().getPluginManager().getServiceArray(ToolConfigurationActionProvider.class);
    if (tcapa != null && tcapa.length > 0) {
      menuTools.addSeparator();
      for (ToolConfigurationActionProvider tcap : tcapa) {
        menuTools.add(tcap);
        if (tcap.isButton()) {
          if (tcap.getSmallIcon() != null) {
            toolBarTools.add(new ToolButton(tcap));
          }
          else {
            JButton button = new JButton(tcap);
            button.setFocusable(false);
            toolBarTools.add(button);
          }
        }
      }
    }
    toolBarTools.setVisible(toolBarTools.getComponentCount() > 0);
  }
  
  public void initGlobalServices() {
    Application.get().getPluginManager().addProvider(TimeOrbadaFontPleaseWaitRenderer.class);
    Application.get().getPluginManager().addProvider(DefaultPleaseWaitRenderer.class);
    
    Application.get().getPluginManager().addProvider(SystemLookAndFeelService.class);
    Application.get().getPluginManager().addProvider(WindowsLookAndFeelService.class);
    Application.get().getPluginManager().addProvider(NimbusLookAndFeelService.class);
    Application.get().getPluginManager().addProvider(CrossPlatformLookAndFeelService.class);
    Application.get().getPluginManager().addProvider(MotifLookAndFeelService.class);
    Application.get().getPluginManager().addProvider(CrossPlatformLookAndFeelSettingsService.class);

    Application.get().getPluginManager().addProvider(GeneralSchemaSettingsService.class);

    getRootPane().setGlassPane(glassPane = new GlassPane());
    StatusBarProvider[] sbp = Application.get().getPluginManager().getServiceArray(StatusBarProvider.class);
    if (sbp != null && sbp.length > 0) {
      for (int i=0; i<sbp.length; i++) {
        addStatusBar(sbp[i].getComponent());
      }
    }
    globalFocusSerives = new LinkedList<GlobalFocusProvider>();
    GlobalFocusProvider[] gfp = Application.get().getServiceArray(GlobalFocusProvider.class);
    if (gfp != null && gfp.length > 0) {
      globalFocusSerives.addAll(Arrays.asList(gfp));
      FocusManager.getCurrentManager().addVetoableChangeListener(new VetoableChangeListener() {
        @Override
        public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
          if ("focusOwner".equals(evt.getPropertyName())) { //NOI18N
            if (evt.getOldValue() instanceof JComponent) {
              Iterator<GlobalFocusProvider> i = globalFocusSerives.iterator();
              while (i.hasNext()) {
                i.next().focusLost((JComponent)evt.getOldValue());
              }
            }
            if (evt.getNewValue() instanceof JComponent) {
              Iterator<GlobalFocusProvider> i = globalFocusSerives.iterator();
              while (i.hasNext()) {
                i.next().focusGained((JComponent)evt.getNewValue());
              }
              if (!(evt.getNewValue() instanceof JRootPane)) {
                menuEdit.removeAll();
                menuEdit.setEnabled(false);
              }
              if (((JComponent)evt.getNewValue()).getComponentPopupMenu() != null) {
                clonePopup(menuEdit, ((JComponent)evt.getNewValue()).getComponentPopupMenu());
              }
            }
          }
        }
      });
    }
    GlobalMenuProvider[] gmp = Application.get().getServiceArray(GlobalMenuProvider.class);
    if (gmp != null && gmp.length > 0) {
      for (int i=0; i<gmp.length; i++) {
        JMenu menu = new JMenu();
        addMenu(menu);
        gmp[i].setMenu(menu);
        gmp[i].initialize();
      }
    }
    GlobalToolBarProvider[] gtbp = Application.get().getServiceArray(GlobalToolBarProvider.class);
    if (gtbp != null && gtbp.length > 0) {
      for (int i=0; i<gtbp.length; i++) {
        JToolBar tb = new JToolBar();
        addToolBar(tb);
        gtbp[i].setToolBar(tb);
        gtbp[i].initialize();
      }
    }
    GlobalActionProvider[] gap = Application.get().getServiceArray(GlobalActionProvider.class);
    if (gap != null && gap.length > 0) {
      for (int i=0; i<gap.length; i++) {
        addAction(gap[i]);
      }
    }
  }

  private JMenu cloneMenu(JMenu intoMenu, JMenu menu) {
    intoMenu.removeAll();
    for (int i=0; i<menu.getItemCount(); i++) {
      Component c = menu.getItem(i);
      if (c instanceof JMenuItem && ((JMenuItem)c).getAction() != null) {
        intoMenu.add(((JMenuItem)c).getAction());
      }
      else if (c instanceof JMenu) {
        intoMenu.add(cloneMenu(new JMenu(((JMenu)c).getText()), (JMenu)c));
      }
      else if (c instanceof JSeparator) {
        intoMenu.addSeparator();
      }
    }
    intoMenu.setEnabled(menu.isEnabled());
    return intoMenu;
  }

  private JMenu clonePopup(final JMenu intoMenu, JPopupMenu menu) {
    intoMenu.removeAll();
    for (int i=0; i<menu.getComponentCount(); i++) {
      Component c = menu.getComponent(i);
      if (c instanceof JMenuItem && ((JMenuItem)c).getAction() != null) {
        intoMenu.add(((JMenuItem)c).getAction());
      }
      else if (c instanceof JMenu) {
        intoMenu.add(cloneMenu(new JMenu(((JMenu)c).getText()), (JMenu)c));
      }
      else if (c instanceof JSeparator) {
        intoMenu.addSeparator();
      }
    }
    intoMenu.setEnabled(true);
    intoMenu.revalidate();
    return intoMenu;
  }
  
  @Override
  public void setTitle(String title) {
    if (oryginalTitle == null) {
      oryginalTitle = title;
      super.setTitle(title);
      return;
    }
    StringBuilder sb = new StringBuilder(oryginalTitle);
    if (activePerspective != null && activePerspective.getTabComponent() != null) {
      sb.append(" [").append(activePerspective.getTabComponent().getTitle()).append("]"); //NOI18N
    }
    if (!StringUtil.isEmpty(title)) {
      sb.append(" [").append(title).append("]"); //NOI18N
    }
    if (Application.get().isSafeMode()) {
      sb.append(" ").append(stringManager.getString("mf-safe-mode")); //NOI18N
    }
    super.setTitle(sb.toString().replaceAll("<.*?>", "")); //NOI18N
  }
  
//  @Override
//  public String getTitle() {
//    StringBuilder sb = new StringBuilder(super.getTitle());
//    if (Application.get().isSafeMode()) {
//      sb.append(" " +stringManager.getString("mf-safe-mode"));
//    }
//    if (!Application.get().getUser().isRegistered()) {
//      sb.append(" [Nie zarejestrowany]");
//    }
//    return sb.toString();
//  }
  public JSONObject getUpdateInfo() {
    return updateInfo;
  }
  
  private void updateStatusBar() {
    GridLayout gl = (GridLayout)panelStatus.getLayout();
    gl.setRows(panelStatus.getComponentCount());
  }
  
  public void addStatusBar(Component component) {
    if (component instanceof JComponent) {
      ((JComponent)component).setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("controlShadow"))); //NOI18N
    }
    panelStatus.add(component);
    updateStatusBar();
  }
  
  public void removeStatusBar(Component component) {
    panelStatus.remove(component);
    updateStatusBar();
  }
  
  public void addAction(Action action) {
    if (action == null) {
      toolBarMain.addSeparator();
    } else {
      toolBarMain.add(new ToolButton(action));
    }
    
  }
  
  public void removeAction(Action action) {
    for (int i=0; i<toolBarMain.getComponentCount(); i++) {
      Component c = toolBarMain.getComponent(i);
      if (c instanceof ToolButton) {
        if (((ToolButton)c).getAction().equals(action)) {
          if (i > 0 && toolBarMain.getComponent(i -1) instanceof JToolBar.Separator) {
            toolBarMain.remove(i -1);
          }
          toolBarMain.remove(c);
          break;
        }
      }
    }
  }
  
  public void addMenu(JMenu menu) {
    if (menu != null) {
      menuBarMain.add(menu, menuBarMain.getComponentIndex(menuTools));
    }
  }
  
  public void removeMenu(JMenu menu) {
    if (menu != null) {
      menuBarMain.remove(menu);
    }
  }
  
  public void addToolBar(JToolBar toolBar) {
    if (toolBar != null) {
      toolBar.setRollover(true);
      panelToolBars.add(toolBar);
    }
  }
  
  public void removeToolBar(JToolBar toolBar) {
    if (toolBar != null) {
      panelToolBars.remove(toolBar);
    }
  }
  
  public void openPerspectiveFor(final Database database, final Schema schema) {
    if (InternalDatabase.get() != null) {
      Query query = InternalDatabase.get().createQuery();
      try {
        query.setSqlText(
          "select pps_id, pps_default, pps_name from perspectives\n" + //NOI18N
          " where pps_usr_id = :usr_id\n" + //NOI18N
          "   and (pps_sch_id = :sch_id or (:sch_id is null and pps_sch_id is null))\n" + //NOI18N
          " order by case when pps_default = 'T' then 0 else 1 end, pps_name"); //NOI18N
        query.paramByName("usr_id").setString(Application.get().getUserId()); //NOI18N
        if (schema != null) {
          query.paramByName("sch_id").setString(schema.getSchId()); //NOI18N
        }
        else {
          query.paramByName("sch_id").setString(null); //NOI18N
        }
        query.open();
        if (!query.eof()) {
          int count = 0;
          while (!query.eof()) {
            if (StringUtil.toBoolean(query.fieldByName("pps_default").getString())) { //NOI18N
              Perspective perspective = new Perspective(InternalDatabase.get(), query.fieldByName("pps_id").getString()); //NOI18N
              addPerspectiveQueue(database, schema, perspective);
            }
            else if (count == 0) {
              Perspective perspective = new Perspective(InternalDatabase.get(), query.fieldByName("pps_id").getString()); //NOI18N
              addPerspectiveQueue(database, schema, perspective);
            }
            else {
              break;
            }
            query.next();
            count++;
          }
        }
        else {
          newPerspectiveFor(database, schema);
        }
      }
      catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      finally {
        query.close();
      }
    }
  }
  
  public void newPerspectiveFor(Database database, Schema schema) {
    Perspective perspective = new Perspective(InternalDatabase.get());
    perspective.setUsrId(Application.get().getUserId());
    if (schema != null) {
      perspective.setSchId(schema.getSchId());
    }
    perspective.setName(database != null ? "$(public-name)" : stringManager.getString("mf-tools")); //NOI18N
    try {
      perspective.applyInsert();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    addPerspective(database, schema, perspective);
  }
  
  private void addPerspectiveQueue(final Database database, final Schema schema, final Perspective perspective) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        addPerspective(database, schema, perspective);
      }
    });
  }
  
  public void addPerspective(Database database, Schema schema, Perspective perspective) {
    final PerspectivePanel panel = new PerspectivePanel(database, schema, perspective);
    //panel.setBorder(new javax.swing.border.LineBorder(Color.RED, 3));
    String title = perspective.getDisplayName(database);
    Icon icon = schema != null ? schema.getIcon() : null;
    tabbedPerpectives.addTab(title, icon, panel);
    final TabCloseComponent tabComponent = new TabCloseComponent(title, null, icon);
    tabbedPerpectives.setTabComponentAt(tabbedPerpectives.indexOfComponent(panel), tabComponent);
    tabbedPerpectives.setSelectedComponent(panel);
    panel.setTabComponent(tabComponent);
    if (database == null) {
      toolsPerspective = panel;
      cmSpecialPerspective.setEnabled(false);
    }
    panel.getTabbedViews().setComponentPopupMenu(menuViewPopup);
  }
  
  public void moveTabLeft() {
    Utils.moveTabLeft(tabbedPerpectives);
  }
  
  public void moveTabRight() {
    Utils.moveTabRight(tabbedPerpectives);
  }
  
  public PerspectivePanel getActivePerspective() {
    return activePerspective;
  }

  public PerspectivePanel getToolsPerspective() {
    return toolsPerspective;
  }
  
  public Database getActiveDatabase() {
    return activePerspective != null ? activePerspective.getDatabase() : null;
  }
  
  public void setTransactionActionEnabled() {
    try {
      boolean db = activePerspective != null && activePerspective.getDatabase() != null && activePerspective.getDatabase().isConnected();
      cmDbCommit.setEnabled(activePerspective != null && activePerspective != toolsPerspective && db && activePerspective.getDatabase().isStartTransaction());
      cmDbRollback.setEnabled(activePerspective != null && activePerspective != toolsPerspective && db && activePerspective.getDatabase().isStartTransaction());
      cmDBStartTransaction.setEnabled(activePerspective != null && activePerspective != toolsPerspective && db && activePerspective.getDatabase().startTransactionAvailable() && !activePerspective.getDatabase().isStartTransaction());
    } catch (SQLException ex) {
      cmDbCommit.setEnabled(activePerspective != null && activePerspective != toolsPerspective);
      cmDbRollback.setEnabled(activePerspective != null && activePerspective != toolsPerspective);
      cmDBStartTransaction.setEnabled(false);
    }
  }
  
  public void setActivePerspective(PerspectivePanel panel) {
    activePerspective = panel;
    cmClosePerspective.setEnabled(activePerspective != null);
    cmCustomizeConnection.setEnabled(activePerspective != null);
    cmResetPerspective.setEnabled(activePerspective != null);
    menuPerspectiveView.setEnabled(activePerspective != null);
    cmSelectNewPerspective.setEnabled(activePerspective != null && activePerspective != toolsPerspective);
    menuSelectNewPerspective.setEnabled(activePerspective != null && activePerspective != toolsPerspective);
    cmDbReconnect.setEnabled(activePerspective != null && activePerspective != toolsPerspective);
    cmDbDisconnect.setEnabled(activePerspective != null && activePerspective != toolsPerspective);
    cmSelectPerspective.setEnabled(tabbedPerpectives.getTabCount() > 1);
    cmSelectView.setEnabled(tabbedPerpectives.getTabCount() > 0);
    cmMaximizeSplitView.setEnabled(activePerspective != null);
    cmDatabaseInfo.setEnabled(activePerspective != null && activePerspective.getDatabase() != null);
    cmCustomizeConnection.setEnabled(activePerspective != null && activePerspective.getDatabase() != null);
    cmPerspectiveProperties.setEnabled(activePerspective != null);
    cmMovePerspectiveLeft.setEnabled(tabbedPerpectives.getTabCount() > 1);
    cmMovePerspectiveRight.setEnabled(tabbedPerpectives.getTabCount() > 1);
    cmDeletePerspective.setEnabled(activePerspective != null);
    if (activePerspective == null) {
      cmMoveViewRight.setEnabled(false);
      cmMoveViewLeft.setEnabled(false);
      cmViewProperites.setEnabled(false);
      cmCloseView.setEnabled(false);
    }
    else {
      cmMoveViewRight.setEnabled(activePerspective.getViewCount() > 1);
      cmMoveViewLeft.setEnabled(activePerspective.getViewCount() > 1);
      cmViewProperites.setEnabled(panel.getViewCount() > 1);
      cmCloseView.setEnabled(activePerspective.getViewCount() > 1);
    }
    setTransactionActionEnabled();
    
    cmUserProperties.setEnabled(InternalDatabase.get() != null);
    cmChangeUserPassword.setEnabled(InternalDatabase.get() != null);
    cmDrivers.setEnabled(InternalDatabase.get() != null);
    cmNewConnection.setEnabled(InternalDatabase.get() != null);
    menuPerspectiveGadgets.setEnabled(activePerspective != null && activePerspective.getGadgetServiceList().size() > 0);
    if (activePerspective == null) {
      setTitle(null);
    }
    if (!newVersionPresents && lastCheckUpdates > 0 && System.currentTimeMillis() -lastCheckUpdates > 86400000 /*24h*/) {
      checkUpdates();
    }
  }
  
  public void viewAdded(PerspectivePanel panel, ViewProvider view) {
    cmMoveViewRight.setEnabled(panel.getViewCount() > 1);
    cmMoveViewLeft.setEnabled(panel.getViewCount() > 1);
    cmSelectView.setEnabled(panel.getViewCount() > 1);
    cmViewProperites.setEnabled(panel.getViewCount() > 1);
  }
  
  public void viewClosed(PerspectivePanel panel) {
    cmMoveViewRight.setEnabled(panel.getViewCount() > 1);
    cmMoveViewLeft.setEnabled(panel.getViewCount() > 1);
    cmSelectView.setEnabled(panel.getViewCount() > 1);
    cmViewProperites.setEnabled(panel.getViewCount() > 1);
  }
  
  public int getDatabaseInUse(Database database) {
    int result = 0;
    for (int i=0; i<tabbedPerpectives.getTabCount(); i++) {
      Component c = tabbedPerpectives.getComponentAt(i);
      if (c instanceof PerspectivePanel) {
        if (((PerspectivePanel)c).getDatabase() != null && ((PerspectivePanel)c).getDatabase().equals(database)) {
          result++;
        }
      }
    }
    return result;
  }
  
  public JTabbedPane getTabbedPerpectives() {
    return tabbedPerpectives;
  }
  
  private ISettings getSettings() {
    return Application.get().getSettings();
  }
  
  public void applySettings() {
    // proxy
    if (StringUtil.equalsIgnoreCase(getSettings().getValue(Consts.proxySettings, Consts.proxyDefaultSettings), Consts.proxySettingsSystemProxy)) {
      System.setProperty("java.net.useSystemProxies", "true"); //NOI18N
    }
    else if (StringUtil.equalsIgnoreCase(getSettings().getValue(Consts.proxySettings, Consts.proxyDefaultSettings), Consts.proxySettingsManualProxy)) {
      if (!"".equals(getSettings().getValue(Consts.proxyHttpAddress, ""))) { //NOI18N
        System.getProperties().put("http.proxyHost", getSettings().getValue(Consts.proxyHttpAddress, "")); //NOI18N
        if (!"".equals(getSettings().getValue(Consts.proxyHttpPort, ""))) { //NOI18N
          System.getProperties().put("http.proxyPort", getSettings().getValue(Consts.proxyHttpPort, "")); //NOI18N
        }
      }
      if (getSettings().getValue(Consts.proxyAuthNeeded, false)) {
        System.getProperties().put("http.proxyUser", getSettings().getValue(Consts.proxyAuthUser, "")); //NOI18N
        System.getProperties().put("http.proxyPassword", getSettings().getValue(Consts.proxyAuthPassword, "")); //NOI18N
      }
    }

    QueryTableCellRenderer.colorizedCells = getSettings().getValue(Consts.colorizedQueryTable, QueryTableCellRenderer.colorizedCells);
    QueryTableCellRenderer.nullValue = getSettings().getValue(Consts.queryTableNullValue, QueryTableCellRenderer.nullValue);
    QueryTableCellRenderer.numberColor = getSettings().getValue(Consts.queryTableColorNumber, QueryTableCellRenderer.numberColor);
    QueryTableCellRenderer.stringColor = getSettings().getValue(Consts.queryTableColorString, QueryTableCellRenderer.stringColor);
    QueryTableCellRenderer.boolColor = getSettings().getValue(Consts.queryTableColorBool, QueryTableCellRenderer.boolColor);
    QueryTableCellRenderer.dateColor = getSettings().getValue(Consts.queryTableColorDate, QueryTableCellRenderer.dateColor);
    QueryTableCellRenderer.nullColor = getSettings().getValue(Consts.queryTableColorNull, QueryTableCellRenderer.nullColor);
    QueryTableCellRenderer.nullColor = getSettings().getValue(Consts.queryTableColorNull, QueryTableCellRenderer.nullColor);
    if (!getSettings().getValue(Consts.defaultColorSelectedTableRow, false)) {
      QueryTableCellRenderer.selectionBackground = getSettings().getValue(Consts.colorSelectedTableRow, new Color(140, 165, 200));
    }
    DataTable.dataFont = getSettings().getValue(Consts.queryTableDataFont, DataTable.dataFont);
    javax.swing.UIManager.put("ColorizedTable.eventRowShift", getSettings().getValue(Consts.tableEvenRowShift, 15L).intValue()); //NOI18N
    javax.swing.UIManager.put("ColorizedTable.focusColumnShift", getSettings().getValue(Consts.tableFocusedColumnShift, 30L).intValue()); //NOI18N
    SkySetting.setBoolean(SkySetting.CmCopyEdit_AsHtmlToo, getSettings().getValue(Consts.editorCopySyntaxHighlight, true));
    SkySetting.setBoolean(SkySetting.CmTab_TabMoveSelected, getSettings().getValue(Consts.editorTabMovesSelected, true));
    SkySetting.setBoolean(SkySetting.CmTab_TabAsSpaces, getSettings().getValue(Consts.editorTabAsSpaces, false));
    SkySetting.setBoolean(SkySetting.CmAutoComplete_AutoCompleteDot, getSettings().getValue(Consts.editorAutoCompleteDot, true));
    SkySetting.setString(SkySetting.CmAutoComplete_AutoCompleteActiveChars, getSettings().getValue(Consts.editorAutoCompleteActivateChars, SkySetting.Default_CmAutoComplete_AutoCompleteActiveChars));
    SkySetting.setString(SkySetting.CmAutoComplete_AutoCompleteInactiveChars, getSettings().getValue(Consts.editorAutoCompleteInactivateChars, SkySetting.Default_CmAutoComplete_AutoCompleteInactiveChars));
    SkySetting.setBoolean(SkySetting.AutoCompleteText_InsertionText, getSettings().getValue(Consts.editorAutoCompleteInsertion, false));
    SkySetting.setBoolean(SkySetting.AutoCompleteText_AutomaticSingle, getSettings().getValue(Consts.editorAutoCompleteInsertSingle, false));
    SkySetting.setBoolean(SkySetting.SyntaxEditor_StructureAutoComplete, getSettings().getValue(Consts.editorAutoCompleteStructureParser, true));
    SkySetting.setBoolean(SkySetting.SyntaxEditor_StructureAutoCompleteVariables, getSettings().getValue(Consts.editorAutoCompleteStructureParserVariables, true));
    SkySetting.setBoolean(SkySetting.SyntaxEditor_CurrentTextTrimWhitespaces, getSettings().getValue(Consts.editorEditorTrimWhitespaces, false));
    SkySetting.setBoolean(SkySetting.SyntaxEditor_SmartEnd, getSettings().getValue(Consts.editorEditorSmartHomeEnd, false));
    SkySetting.setBoolean(SkySetting.SyntaxEditor_SmartHome, getSettings().getValue(Consts.editorEditorSmartHomeEnd, false));
    SkySetting.setInteger(SkySetting.SyntaxEditor_TabToSpaceCount, getSettings().getValue(Consts.editorTabToSpaceCount, (long)SkySetting.Default_SyntaxEditor_TabToSpaceCount).intValue());
    SkySetting.setBoolean(SkySetting.SyntaxEditor_PreDefinedSnippets, getSettings().getValue(Consts.editorPreDefinedSnippets, true));
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        applySyntaxEditorSettings();
      }
    });
    if (!getSettings().getValue(Consts.dataFormatDateDefault, true)) {
      Variant.setDateFormat(getSettings().getValue(Consts.dataFormatDateString, Consts.defaultDataFormatDateString));
    }
    else {
      Variant.setDateFormat((String)null);
    }
    if (!getSettings().getValue(Consts.dataFormatTimeDefault, true)) {
      Variant.setTimeFormat(getSettings().getValue(Consts.dataFormatTimeString, Consts.defaultDataFormatTimeString));
    }
    else {
      Variant.setTimeFormat((String)null);
    }
    if (!getSettings().getValue(Consts.dataFormatTimestampDefault, true)) {
      Variant.setTimeStampFormat(getSettings().getValue(Consts.dataFormatTimestampString, Consts.defaultDataFormatTimestampString));
    }
    else {
      Variant.setTimeStampFormat((String)null);
    }
    if (!getSettings().getValue(Consts.dataFormatDecimalSeparatorDefault, true)) {
      Variant.setDecimalSeparator(getSettings().getValue(Consts.dataFormatDecimalSeparator, Consts.defaultDataFormatDecimalSeparator));
    }
    else {
      Variant.setDecimalSeparator((String)null);
    }
    if (!getSettings().getValue(Consts.dataFormatNumericDefault, true)) {
      Variant.setDecimalFormat(getSettings().getValue(Consts.dataFormatNumericString, Consts.defaultDataFormatNumericString));
    }
    else {
      Variant.setDecimalFormat((String)null);
    }
    if (!getSettings().getValue(Consts.dataFormatBigDecimalDefault, true)) {
      Variant.setBigDecimalFormat(getSettings().getValue(Consts.dataFormatBigDecimalString, Consts.defaultDataFormatBigDecimalString));
    }
    else {
      Variant.setBigDecimalFormat((String)null);
    }
    Variant.setDefaultNumberFormatMaximumFractionDigits(Consts.defaultNumberFormatMaximumFractionDigits);
  }
  
  private void applySyntaxEditorSettings() {
    SyntaxTextArea.apply(new ApplySyntaxTextAreaEvent() {
      @Override
      public void apply(SyntaxTextArea editor) {
        editor.getEditorArea().setBackground(getSettings().getValue("syntax-editor-background-color", editor.getEditorArea().getBackground())); //NOI18N
        editor.getEditorArea().setFont(getSettings().getValue("syntax-editor-font", editor.getEditorArea().getFont())); //NOI18N
        if (editor.getDocument() instanceof OrbadaSQLSyntaxDocument) {
          OrbadaSQLSyntaxDocument.loadSettings(editor.getDocument());
          editor.repaint();
        }
        else if (editor.getDocument() instanceof OrbadaJavaSyntaxDocument) {
          OrbadaJavaSyntaxDocument.loadSettings(editor.getDocument());
          editor.repaint();
        }
      }
    });
  }
  
  public void shutDown(boolean fromX) {
    cancelClose = false;
    if (getSettings().getValue(Consts.appCloseWarning, false) &&
        MessageBox.show(this, stringManager.getString("closing"), stringManager.getString("closing-app-question"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.NO) {
      cancelClose = true;
    }
    if (!cancelClose) {
      cmCloseAllPerspectives.performe();
    }
    if (!cancelClose) {
      dispose();
      Application.get().shutDown();
    }
    else if (fromX) {
      throw new OrbadaCancelCloseException();
    }
  }
  
  @Override
  public void dispose() {
    super.dispose();
  }
  
  public boolean closePerspective(PerspectivePanel panel) {
    if (!panel.canClose()) {
      cancelClose = true;
      return false;
    }
    tabbedPerpectives.remove(panel);
    try {
      panel.close();
    } catch (Throwable t) {
      ExceptionUtil.processException(t);
    }
    if (panel.equals(toolsPerspective)) {
      toolsPerspective = null;
      cmSpecialPerspective.setEnabled(true);
    }
    return true;
  }
  
  @Override
  public GlassPane getGlassPane() {
    return glassPane;
  }

  @Override
  public void processMessage(PluginMessage message) {
    if (Consts.globalMessageTransactionStateChange.equals(message.getMessageId())) {
      setTransactionActionEnabled();
    }
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmExit = new pl.mpak.sky.gui.swing.Action();
        cmNewConnection = new pl.mpak.sky.gui.swing.Action();
        cmDrivers = new pl.mpak.sky.gui.swing.Action();
        cmNewPerspective = new pl.mpak.sky.gui.swing.Action();
        cmCustomizeConnection = new pl.mpak.sky.gui.swing.Action();
        cmResetPerspective = new pl.mpak.sky.gui.swing.Action();
        cmCloseAllPerspectives = new pl.mpak.sky.gui.swing.Action();
        cmClosePerspective = new pl.mpak.sky.gui.swing.Action();
        cmDbCommit = new pl.mpak.sky.gui.swing.Action();
        cmDbRollback = new pl.mpak.sky.gui.swing.Action();
        cmDbReconnect = new pl.mpak.sky.gui.swing.Action();
        cmDbDisconnect = new pl.mpak.sky.gui.swing.Action();
        cmSelectPerspective = new pl.mpak.sky.gui.swing.Action();
        cmSpecialPerspective = new pl.mpak.sky.gui.swing.Action();
        cmMoveViewRight = new pl.mpak.sky.gui.swing.Action();
        cmMoveViewLeft = new pl.mpak.sky.gui.swing.Action();
        cmOrbadaSettings = new pl.mpak.sky.gui.swing.Action();
        cmMaximizeSplitView = new pl.mpak.sky.gui.swing.Action();
        cmDatabaseInfo = new pl.mpak.sky.gui.swing.Action();
        cmPluginSettings = new pl.mpak.sky.gui.swing.Action();
        cmOrbadaConfigFile = new pl.mpak.sky.gui.swing.Action();
        cmToolsSettings = new pl.mpak.sky.gui.swing.Action();
        cmPluginManager = new pl.mpak.sky.gui.swing.Action();
        cmChangeUserPassword = new pl.mpak.sky.gui.swing.Action();
        cmUserProperties = new pl.mpak.sky.gui.swing.Action();
        cmOrbadaLogFile = new pl.mpak.sky.gui.swing.Action();
        cmOrbadaLogErrorFile = new pl.mpak.sky.gui.swing.Action();
        menuPerspectives = new javax.swing.JPopupMenu();
        menuNewPerspective = new javax.swing.JMenuItem();
        sepMenuPerspective = new javax.swing.JSeparator();
        cmSelectNewPerspective = new pl.mpak.sky.gui.swing.Action();
        cmDeletePerspective = new pl.mpak.sky.gui.swing.Action();
        cmSelectView = new pl.mpak.sky.gui.swing.Action();
        cmTemplateSettings = new pl.mpak.sky.gui.swing.Action();
        cmViewProperites = new pl.mpak.sky.gui.swing.Action();
        cmSelectTab = new pl.mpak.sky.gui.swing.Action();
        cmCloseView = new pl.mpak.sky.gui.swing.Action();
        menuViewPopup = new javax.swing.JPopupMenu();
        ppmViewProperties = new javax.swing.JMenuItem();
        ppmMoveViewLeft = new javax.swing.JMenuItem();
        ppmMoveViewRight = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JSeparator();
        ppmCloseView = new javax.swing.JMenuItem();
        cmSuggestionRequest = new pl.mpak.sky.gui.swing.Action();
        cmProblemRequest = new pl.mpak.sky.gui.swing.Action();
        cmComponentShortcut = new pl.mpak.sky.gui.swing.Action();
        cmUserGuide = new pl.mpak.sky.gui.swing.Action();
        cmGetUpdate = new pl.mpak.sky.gui.swing.Action();
        cmOrbadaLastChanges = new pl.mpak.sky.gui.swing.Action();
        cmUserList = new pl.mpak.sky.gui.swing.Action();
        cmAboutOrbada = new pl.mpak.sky.gui.swing.Action();
        cmOrbadaLog4jFile = new pl.mpak.sky.gui.swing.Action();
        cmDocumentation = new pl.mpak.sky.gui.swing.Action();
        cmPageProjectHome = new pl.mpak.sky.gui.swing.Action();
        cmPageReviews = new pl.mpak.sky.gui.swing.Action();
        cmPageDonations = new pl.mpak.sky.gui.swing.Action();
        cmFullScreen = new pl.mpak.sky.gui.swing.Action();
        cmDBStartTransaction = new pl.mpak.sky.gui.swing.Action();
        cmClonePerspectiveViews = new pl.mpak.sky.gui.swing.Action();
        panelMain = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        panelPerspectiveTools = new javax.swing.JPanel();
        panelToolTip = new javax.swing.JPanel();
        buttonMoveViewLeft = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonMoveViewRight = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator8 = new javax.swing.JSeparator();
        buttonClosePerspective = new pl.mpak.sky.gui.swing.comp.ToolButton();
        panelToolBars = new javax.swing.JPanel();
        toolBarMain = new javax.swing.JToolBar();
        tbConnection = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonSelectNewPerspective = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonNewToolsPerspective = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonDatabaseInfo = new pl.mpak.sky.gui.swing.comp.ToolButton();
        jSeparator6 = new javax.swing.JSeparator();
        toolButton1 = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonDbCommit = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonDbRollback = new pl.mpak.sky.gui.swing.comp.ToolButton();
        toolBarTools = new javax.swing.JToolBar();
        jPanel2 = new javax.swing.JPanel();
        toolBarUpdates = new javax.swing.JToolBar();
        buttonDonation = new pl.mpak.sky.gui.swing.comp.ToolButton();
        buttonGetUpdate = new pl.mpak.sky.gui.swing.comp.ToolButton();
        panelPerspectives = new javax.swing.JPanel();
        tabbedPerpectives = new javax.swing.JTabbedPane();
        panelStatus = new javax.swing.JPanel();
        menuBarMain = new javax.swing.JMenuBar();
        menuProgram = new javax.swing.JMenu();
        menuNewConnection = new javax.swing.JMenuItem();
        menuDrivers = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        menuSettings = new javax.swing.JMenu();
        menuOrbadaSettings = new javax.swing.JMenuItem();
        menuPluginSettings = new javax.swing.JMenuItem();
        menuPluginManager = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        menuOrbadaConfigFile = new javax.swing.JMenuItem();
        menuOrbadaLog4jFile = new javax.swing.JMenuItem();
        menuSpecialFiles = new javax.swing.JMenu();
        menuOrbadaLogFile = new javax.swing.JMenuItem();
        menuOrbadaLogErrorFile = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JSeparator();
        menuFullScreen = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        menuUserProperties = new javax.swing.JMenuItem();
        menuChangeUserPassword = new javax.swing.JMenuItem();
        menuUserList = new javax.swing.JMenuItem();
        sepRequests = new javax.swing.JSeparator();
        menuSuggestionRequest = new javax.swing.JMenuItem();
        menuProblemRequest = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        menuExit = new javax.swing.JMenuItem();
        menuConnection = new javax.swing.JMenu();
        menuDBStartTransaction = new javax.swing.JMenuItem();
        jSeparator19 = new javax.swing.JPopupMenu.Separator();
        menuDbCommit = new javax.swing.JMenuItem();
        menuDbRollback = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        menuDbDisconnect = new javax.swing.JMenuItem();
        menuDbReconnect = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JSeparator();
        menuCustomizeConnection = new javax.swing.JMenuItem();
        menuDatabaseInfo = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuTools = new javax.swing.JMenu();
        jSeparator12 = new javax.swing.JSeparator();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuTemplatesSettings = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        menuPerspective = new javax.swing.JMenu();
        menuToolsPerspective = new javax.swing.JMenuItem();
        menuSelectNewPerspective = new javax.swing.JMenu();
        menuWindowNewPerspective = new javax.swing.JMenuItem();
        sepMenuWindowPerspective = new javax.swing.JSeparator();
        menuPerspectiveGadgets = new javax.swing.JMenu();
        jSeparator3 = new javax.swing.JSeparator();
        menuClosePerspective = new javax.swing.JMenuItem();
        menuCloseAllPerspectives = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        menuResetPerspective = new javax.swing.JMenuItem();
        menuPerspectiveProperties = new javax.swing.JMenuItem();
        menuMovePerspectiveLeft = new javax.swing.JMenuItem();
        menuMovePerspectiveRight = new javax.swing.JMenuItem();
        menuClonePerspectiveViews = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        menuViewProperites = new javax.swing.JMenuItem();
        menuMoveViewLeft = new javax.swing.JMenuItem();
        menuMoveViewRight = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JSeparator();
        menuCloseView = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JSeparator();
        menuMaximizeSplitView = new javax.swing.JMenuItem();
        menuNavigation = new javax.swing.JMenu();
        menuSelectPerspective = new javax.swing.JMenuItem();
        menuSelectView = new javax.swing.JMenuItem();
        menuSelectTab = new javax.swing.JMenuItem();
        menuPerspectiveView = new javax.swing.JMenu();
        menuHelp = new javax.swing.JMenu();
        menuUserGuide = new javax.swing.JMenuItem();
        menuComponentShortcut = new javax.swing.JMenuItem();
        menuDocumentation = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JSeparator();
        menuPageProjectHome = new javax.swing.JMenuItem();
        menuPageReviews = new javax.swing.JMenuItem();
        menuPageDonations = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JSeparator();
        menuLastChanges = new javax.swing.JMenuItem();
        menuGetUpdates = new javax.swing.JMenuItem();
        menuAboutOrbada = new javax.swing.JMenuItem();

        cmExit.setActionCommandKey("cmExit");
        cmExit.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        cmExit.setText(stringManager.getString("mf-cmexit-text")); // NOI18N
        cmExit.setTooltip(stringManager.getString("mf-cmexit-hint")); // NOI18N
        cmExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmExitActionPerformed(evt);
            }
        });

        cmNewConnection.setActionCommandKey("cmNewConnection");
        cmNewConnection.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.SHIFT_MASK));
        cmNewConnection.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/connection.gif")); // NOI18N
        cmNewConnection.setText(stringManager.getString("mf-cmNewConnection-text")); // NOI18N
        cmNewConnection.setTooltip(stringManager.getString("mf-cmNewConnection-hint")); // NOI18N
        cmNewConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmNewConnectionActionPerformed(evt);
            }
        });

        cmDrivers.setActionCommandKey("cmDrivers");
        cmDrivers.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/driverproperties.gif")); // NOI18N
        cmDrivers.setText(stringManager.getString("mf-cmDrivers-text")); // NOI18N
        cmDrivers.setTooltip(stringManager.getString("mf-cmDrivers-hint")); // NOI18N
        cmDrivers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDriversActionPerformed(evt);
            }
        });

        cmNewPerspective.setActionCommandKey("cmNewPerspective");
        cmNewPerspective.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new_window_perspective16.gif")); // NOI18N
        cmNewPerspective.setText(stringManager.getString("mf-cmNewPerspective-text")); // NOI18N
        cmNewPerspective.setTooltip(stringManager.getString("mf-cmNewPerspective-hint")); // NOI18N
        cmNewPerspective.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmNewPerspectiveActionPerformed(evt);
            }
        });

        cmCustomizeConnection.setActionCommandKey("cmCustomizeConnection"); // NOI18N
        cmCustomizeConnection.setText(stringManager.getString("mf-cmCustomizeConnection-text")); // NOI18N
        cmCustomizeConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCustomizeConnectionActionPerformed(evt);
            }
        });

        cmResetPerspective.setActionCommandKey("cmResetPerspective");
        cmResetPerspective.setText(stringManager.getString("mf-cmResetPerspective-text")); // NOI18N
        cmResetPerspective.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmResetPerspectiveActionPerformed(evt);
            }
        });

        cmCloseAllPerspectives.setActionCommandKey("cmCloseAllPerspectives");
        cmCloseAllPerspectives.setText(stringManager.getString("mf-cmCloseAllPerspectives-text")); // NOI18N
        cmCloseAllPerspectives.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCloseAllPerspectivesActionPerformed(evt);
            }
        });

        cmClosePerspective.setActionCommandKey("cmClosePerspective");
        cmClosePerspective.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        cmClosePerspective.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/stop10.gif")); // NOI18N
        cmClosePerspective.setText(stringManager.getString("mf-cmClosePerspective-text")); // NOI18N
        cmClosePerspective.setTooltip(stringManager.getString("mf-cmClosePerspective-hint")); // NOI18N
        cmClosePerspective.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmClosePerspectiveActionPerformed(evt);
            }
        });

        cmDbCommit.setActionCommandKey("cmDbCommit");
        cmDbCommit.setEnabled(false);
        cmDbCommit.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/commit.gif")); // NOI18N
        cmDbCommit.setText(stringManager.getString("mf-cmDbCommit-text")); // NOI18N
        cmDbCommit.setTooltip(stringManager.getString("mf-cmDbCommit-hint")); // NOI18N
        cmDbCommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDbCommitActionPerformed(evt);
            }
        });

        cmDbRollback.setActionCommandKey("cmDbRollback");
        cmDbRollback.setEnabled(false);
        cmDbRollback.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/rollback.gif")); // NOI18N
        cmDbRollback.setText(stringManager.getString("mf-cmDbRollback-text")); // NOI18N
        cmDbRollback.setTooltip(stringManager.getString("mf-cmDbRollback-hint")); // NOI18N
        cmDbRollback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDbRollbackActionPerformed(evt);
            }
        });

        cmDbReconnect.setActionCommandKey("cmDbReconnect");
        cmDbReconnect.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/connect16.gif")); // NOI18N
        cmDbReconnect.setText(stringManager.getString("mf-cmDbReconnect-text")); // NOI18N
        cmDbReconnect.setTooltip(stringManager.getString("mf-cmDbReconnect-hint")); // NOI18N
        cmDbReconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDbReconnectActionPerformed(evt);
            }
        });

        cmDbDisconnect.setActionCommandKey("cmDbDisconnect");
        cmDbDisconnect.setText(stringManager.getString("mf-cmDbDisconnect-text")); // NOI18N
        cmDbDisconnect.setTooltip(stringManager.getString("mf-cmDbDisconnect-hint")); // NOI18N
        cmDbDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDbDisconnectActionPerformed(evt);
            }
        });

        cmSelectPerspective.setActionCommandKey("cmSelectPerspective"); // NOI18N
        cmSelectPerspective.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0, java.awt.event.InputEvent.ALT_MASK));
        cmSelectPerspective.setText(stringManager.getString("mf-cmSelectPerspective-text")); // NOI18N
        cmSelectPerspective.setTooltip(stringManager.getString("mf-cmSelectPerspective-hint")); // NOI18N
        cmSelectPerspective.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSelectPerspectiveActionPerformed(evt);
            }
        });

        cmSpecialPerspective.setActionCommandKey("cmSpecialPerspective"); // NOI18N
        cmSpecialPerspective.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new_tools_perspective16.gif")); // NOI18N
        cmSpecialPerspective.setText(stringManager.getString("mf-cmSpecialPerspective-text")); // NOI18N
        cmSpecialPerspective.setTooltip(stringManager.getString("mf-cmSpecialPerspective-hint")); // NOI18N
        cmSpecialPerspective.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSpecialPerspectiveActionPerformed(evt);
            }
        });

        cmMoveViewRight.setActionCommandKey("cmMoveViewRight");
        cmMoveViewRight.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/right10.gif")); // NOI18N
        cmMoveViewRight.setText(stringManager.getString("mf-cmMoveViewRight-text")); // NOI18N
        cmMoveViewRight.setTooltip(stringManager.getString("mf-cmMoveViewRight-hint")); // NOI18N
        cmMoveViewRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmMoveViewRightActionPerformed(evt);
            }
        });

        cmMoveViewLeft.setActionCommandKey("cmMoveViewLeft");
        cmMoveViewLeft.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/left10.gif")); // NOI18N
        cmMoveViewLeft.setText(stringManager.getString("mf-cmMoveViewLeft-text")); // NOI18N
        cmMoveViewLeft.setTooltip(stringManager.getString("mf-cmMoveViewLeft-hint")); // NOI18N
        cmMoveViewLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmMoveViewLeftActionPerformed(evt);
            }
        });

        cmOrbadaSettings.setActionCommandKey("cmOrbadaSettings"); // NOI18N
        cmOrbadaSettings.setText(stringManager.getString("mf-cmOrbadaSettings-text")); // NOI18N
        cmOrbadaSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmOrbadaSettingsActionPerformed(evt);
            }
        });

        cmMaximizeSplitView.setActionCommandKey("cmMaximizeSplitView");
        cmMaximizeSplitView.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_MASK));
        cmMaximizeSplitView.setText(stringManager.getString("mf-cmMaximizeSplitView-text")); // NOI18N
        cmMaximizeSplitView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmMaximizeSplitViewActionPerformed(evt);
            }
        });

        cmDatabaseInfo.setActionCommandKey("cmDatabaseInfo"); // NOI18N
        cmDatabaseInfo.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/db_info16.gif")); // NOI18N
        cmDatabaseInfo.setText(stringManager.getString("mf-cmDatabaseInfo-text")); // NOI18N
        cmDatabaseInfo.setTooltip(stringManager.getString("mf-cmDatabaseInfo-hint")); // NOI18N
        cmDatabaseInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDatabaseInfoActionPerformed(evt);
            }
        });

        cmPluginSettings.setActionCommandKey("cmPluginSettings"); // NOI18N
        cmPluginSettings.setText(stringManager.getString("mf-cmPluginSettings-text")); // NOI18N
        cmPluginSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmPluginSettingsActionPerformed(evt);
            }
        });

        cmOrbadaConfigFile.setActionCommandKey("cmOrbadaConfigFile"); // NOI18N
        cmOrbadaConfigFile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/save16.gif")); // NOI18N
        cmOrbadaConfigFile.setText(stringManager.getString("mf-cmOrbadaConfigFile-text")); // NOI18N
        cmOrbadaConfigFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmOrbadaConfigFileActionPerformed(evt);
            }
        });

        cmToolsSettings.setActionCommandKey("cmToolsSettings"); // NOI18N
        cmToolsSettings.setText(stringManager.getString("mf-cmToolsSettings-text")); // NOI18N
        cmToolsSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmToolsSettingsActionPerformed(evt);
            }
        });

        cmPluginManager.setActionCommandKey("cmPluginManager"); // NOI18N
        cmPluginManager.setText(stringManager.getString("mf-cmPluginManager-text")); // NOI18N
        cmPluginManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmPluginManagerActionPerformed(evt);
            }
        });

        cmChangeUserPassword.setActionCommandKey("cmChangeUserPassword"); // NOI18N
        cmChangeUserPassword.setText(stringManager.getString("mf-cmChangeUserPassword-text")); // NOI18N
        cmChangeUserPassword.setTooltip(stringManager.getString("mf-cmChangeUserPassword-hint")); // NOI18N
        cmChangeUserPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmChangeUserPasswordActionPerformed(evt);
            }
        });

        cmUserProperties.setActionCommandKey("cmUserProperties"); // NOI18N
        cmUserProperties.setText(stringManager.getString("mf-cmUserProperties-text")); // NOI18N
        cmUserProperties.setTooltip(stringManager.getString("mf-cmUserProperties-hint")); // NOI18N
        cmUserProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmUserPropertiesActionPerformed(evt);
            }
        });

        cmOrbadaLogFile.setActionCommandKey("cmOrbadaLogFile"); // NOI18N
        cmOrbadaLogFile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/save16.gif")); // NOI18N
        cmOrbadaLogFile.setText(stringManager.getString("mf-cmOrbadaLogFile-text")); // NOI18N
        cmOrbadaLogFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmOrbadaLogFileActionPerformed(evt);
            }
        });

        cmOrbadaLogErrorFile.setActionCommandKey("cmOrbadaLogErrorFile"); // NOI18N
        cmOrbadaLogErrorFile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/save16.gif")); // NOI18N
        cmOrbadaLogErrorFile.setText(stringManager.getString("mf-cmOrbadaLogErrorFile-text")); // NOI18N
        cmOrbadaLogErrorFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmOrbadaLogErrorFileActionPerformed(evt);
            }
        });

        menuPerspectives.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                menuPerspectivesPopupMenuWillBecomeVisible(evt);
            }
        });

        menuNewPerspective.setAction(cmNewPerspective);
        menuPerspectives.add(menuNewPerspective);
        menuPerspectives.add(sepMenuPerspective);

        cmSelectNewPerspective.setActionCommandKey("cmSelectNewPerspective"); // NOI18N
        cmSelectNewPerspective.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/new_window_perspective16.gif")); // NOI18N
        cmSelectNewPerspective.setText(stringManager.getString("mf-cmSelectNewPerspective-text")); // NOI18N
        cmSelectNewPerspective.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSelectNewPerspectiveActionPerformed(evt);
            }
        });

        cmDeletePerspective.setActionCommandKey("cmDeletePerspective"); // NOI18N
        cmDeletePerspective.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/trash.gif")); // NOI18N
        cmDeletePerspective.setText(stringManager.getString("mf-cmDeletePerspective-text")); // NOI18N
        cmDeletePerspective.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDeletePerspectiveActionPerformed(evt);
            }
        });

        cmSelectView.setActionCommandKey("cmSelectView"); // NOI18N
        cmSelectView.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, java.awt.event.InputEvent.ALT_MASK));
        cmSelectView.setText(stringManager.getString("mf-cmSelectView-text")); // NOI18N
        cmSelectView.setTooltip(stringManager.getString("mf-cmSelectView-hint")); // NOI18N
        cmSelectView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSelectViewActionPerformed(evt);
            }
        });

        cmTemplateSettings.setActionCommandKey("cmTemplateSettings");
        cmTemplateSettings.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, java.awt.event.InputEvent.ALT_MASK));
        cmTemplateSettings.setText(stringManager.getString("mf-cmTemplateSettings-text")); // NOI18N
        cmTemplateSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmTemplateSettingsActionPerformed(evt);
            }
        });

        cmViewProperites.setActionCommandKey("cmViewProperites");
        cmViewProperites.setText(stringManager.getString("mf-cmViewProperites-text")); // NOI18N
        cmViewProperites.setTooltip(stringManager.getString("mf-cmViewProperites-hint")); // NOI18N
        cmViewProperites.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmViewProperitesActionPerformed(evt);
            }
        });

        cmSelectTab.setActionCommandKey("cmSelectTab");
        cmSelectTab.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, java.awt.event.InputEvent.ALT_MASK));
        cmSelectTab.setText(stringManager.getString("mf-cmSelectTab-text")); // NOI18N
        cmSelectTab.setTooltip(stringManager.getString("mf-cmSelectTab-hint")); // NOI18N
        cmSelectTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSelectTabActionPerformed(evt);
            }
        });

        cmCloseView.setActionCommandKey("cmCloseView");
        cmCloseView.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        cmCloseView.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/close10.gif")); // NOI18N
        cmCloseView.setText(stringManager.getString("mf-cmCloseView-text")); // NOI18N
        cmCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmCloseViewActionPerformed(evt);
            }
        });

        ppmViewProperties.setAction(cmViewProperites);
        menuViewPopup.add(ppmViewProperties);

        ppmMoveViewLeft.setAction(cmMoveViewLeft);
        menuViewPopup.add(ppmMoveViewLeft);

        ppmMoveViewRight.setAction(cmMoveViewRight);
        menuViewPopup.add(ppmMoveViewRight);
        menuViewPopup.add(jSeparator15);

        ppmCloseView.setAction(cmCloseView);
        menuViewPopup.add(ppmCloseView);

        cmSuggestionRequest.setActionCommandKey("cmSuggestionRequest");
        cmSuggestionRequest.setText(stringManager.getString("mf-cmSuggestionRequest-text")); // NOI18N
        cmSuggestionRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmSuggestionRequestActionPerformed(evt);
            }
        });

        cmProblemRequest.setActionCommandKey("cmProblemRequest");
        cmProblemRequest.setText(stringManager.getString("mf-cmProblemRequest-text")); // NOI18N
        cmProblemRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmProblemRequestActionPerformed(evt);
            }
        });

        cmComponentShortcut.setActionCommandKey("cmComponentShortcut");
        cmComponentShortcut.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.SHIFT_MASK));
        cmComponentShortcut.setText(stringManager.getString("mf-cmComponentShortcut-text")); // NOI18N
        cmComponentShortcut.setTooltip(stringManager.getString("mf-cmComponentShortcut-hint")); // NOI18N
        cmComponentShortcut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmComponentShortcutActionPerformed(evt);
            }
        });

        cmUserGuide.setActionCommandKey("cmUserGuide");
        cmUserGuide.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        cmUserGuide.setText(stringManager.getString("mf-cmUserGuide-text")); // NOI18N
        cmUserGuide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmUserGuideActionPerformed(evt);
            }
        });

        cmGetUpdate.setActionCommandKey("cmGenUpdate");
        cmGetUpdate.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/search-commercial.png")); // NOI18N
        cmGetUpdate.setText(stringManager.getString("mf-cmGetUpdate-text")); // NOI18N
        cmGetUpdate.setTooltip(stringManager.getString("mf-cmGetUpdate-hint")); // NOI18N
        cmGetUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmGetUpdateActionPerformed(evt);
            }
        });

        cmOrbadaLastChanges.setActionCommandKey("cmOrbadaLastChanges");
        cmOrbadaLastChanges.setText(stringManager.getString("mf-cmOrbadaLastChanges-text")); // NOI18N
        cmOrbadaLastChanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmOrbadaLastChangesActionPerformed(evt);
            }
        });

        cmUserList.setActionCommandKey("cmUserList");
        cmUserList.setText(stringManager.getString("mf-cmUserList-text")); // NOI18N
        cmUserList.setTooltip(stringManager.getString("mf-cmUserList-hint")); // NOI18N
        cmUserList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmUserListActionPerformed(evt);
            }
        });

        cmAboutOrbada.setActionCommandKey("cmAboutOrbada");
        cmAboutOrbada.setText(stringManager.getString("mf-cmAboutOrbada-text")); // NOI18N
        cmAboutOrbada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmAboutOrbadaActionPerformed(evt);
            }
        });

        cmOrbadaLog4jFile.setActionCommandKey("cmOrbadaLog4jFile"); // NOI18N
        cmOrbadaLog4jFile.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/save16.gif")); // NOI18N
        cmOrbadaLog4jFile.setText(stringManager.getString("mf-cmOrbadaLog4jFile-text")); // NOI18N
        cmOrbadaLog4jFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmOrbadaLog4jFileActionPerformed(evt);
            }
        });

        cmDocumentation.setActionCommandKey("cmDocumentation");
        cmDocumentation.setText(stringManager.getString("mf-cmDocumentation-text")); // NOI18N
        cmDocumentation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDocumentationActionPerformed(evt);
            }
        });

        cmPageProjectHome.setActionCommandKey("cmPageProjectHome");
        cmPageProjectHome.setText(stringManager.getString("cmPageProjectHome-text")); // NOI18N
        cmPageProjectHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmPageProjectHomeActionPerformed(evt);
            }
        });

        cmPageReviews.setActionCommandKey("cmPageReviews");
        cmPageReviews.setText(stringManager.getString("cmPageReviews-text")); // NOI18N
        cmPageReviews.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmPageReviewsActionPerformed(evt);
            }
        });

        cmPageDonations.setActionCommandKey("cmPageDonations");
        cmPageDonations.setText(stringManager.getString("cmPageDonations-text")); // NOI18N
        cmPageDonations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmPageDonationsActionPerformed(evt);
            }
        });

        cmFullScreen.setActionCommandKey("cmFullScreen");
        cmFullScreen.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        cmFullScreen.setText(stringManager.getString("cmFullScreen-text")); // NOI18N
        cmFullScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmFullScreenActionPerformed(evt);
            }
        });

        cmDBStartTransaction.setActionCommandKey("cmDBStartTransaction");
        cmDBStartTransaction.setEnabled(false);
        cmDBStartTransaction.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/start_transaction.gif")); // NOI18N
        cmDBStartTransaction.setText(stringManager.getString("cmDBStartTransaction-text")); // NOI18N
        cmDBStartTransaction.setTooltip(stringManager.getString("cmDBStartTransaction-hint")); // NOI18N
        cmDBStartTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmDBStartTransactionActionPerformed(evt);
            }
        });

        cmClonePerspectiveViews.setText(stringManager.getString("main-cmClonePerspectiveViews-text")); // NOI18N
        cmClonePerspectiveViews.setTooltip(stringManager.getString("main-cmClonePerspectiveViews-hint")); // NOI18N
        cmClonePerspectiveViews.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmClonePerspectiveViewsActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(stringManager.getString("MainFrame-title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        panelMain.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        panelPerspectiveTools.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 5));

        panelToolTip.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        panelPerspectiveTools.add(panelToolTip);

        buttonMoveViewLeft.setAction(cmMoveViewLeft);
        buttonMoveViewLeft.setPreferredSize(new java.awt.Dimension(18, 18));
        panelPerspectiveTools.add(buttonMoveViewLeft);

        buttonMoveViewRight.setAction(cmMoveViewRight);
        buttonMoveViewRight.setPreferredSize(new java.awt.Dimension(18, 18));
        panelPerspectiveTools.add(buttonMoveViewRight);

        jSeparator8.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator8.setPreferredSize(new java.awt.Dimension(4, 0));
        panelPerspectiveTools.add(jSeparator8);

        buttonClosePerspective.setAction(cmClosePerspective);
        buttonClosePerspective.setPreferredSize(new java.awt.Dimension(18, 18));
        panelPerspectiveTools.add(buttonClosePerspective);

        jPanel1.add(panelPerspectiveTools, java.awt.BorderLayout.EAST);

        panelToolBars.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        toolBarMain.setFloatable(false);
        toolBarMain.setRollover(true);

        tbConnection.setAction(cmNewConnection);
        toolBarMain.add(tbConnection);

        buttonSelectNewPerspective.setAction(cmSelectNewPerspective);
        buttonSelectNewPerspective.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSelectNewPerspective.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarMain.add(buttonSelectNewPerspective);

        buttonNewToolsPerspective.setAction(cmSpecialPerspective);
        buttonNewToolsPerspective.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonNewToolsPerspective.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarMain.add(buttonNewToolsPerspective);

        buttonDatabaseInfo.setAction(cmDatabaseInfo);
        buttonDatabaseInfo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDatabaseInfo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarMain.add(buttonDatabaseInfo);

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        toolBarMain.add(jSeparator6);

        toolButton1.setAction(cmDBStartTransaction);
        toolButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toolBarMain.add(toolButton1);

        buttonDbCommit.setAction(cmDbCommit);
        buttonDbCommit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDbCommit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarMain.add(buttonDbCommit);

        buttonDbRollback.setAction(cmDbRollback);
        buttonDbRollback.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDbRollback.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarMain.add(buttonDbRollback);

        panelToolBars.add(toolBarMain);

        toolBarTools.setRollover(true);
        panelToolBars.add(toolBarTools);

        jPanel1.add(panelToolBars, java.awt.BorderLayout.WEST);

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        toolBarUpdates.setFloatable(false);
        toolBarUpdates.setRollover(true);

        buttonDonation.setAction(cmPageDonations);
        buttonDonation.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        buttonDonation.setHideActionText(false);
        buttonDonation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDonation.setMaximumSize(new java.awt.Dimension(140, 26));
        buttonDonation.setPreferredSize(new java.awt.Dimension(140, 26));
        buttonDonation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBarUpdates.add(buttonDonation);

        buttonGetUpdate.setAction(cmGetUpdate);
        buttonGetUpdate.setForeground(java.awt.Color.red);
        buttonGetUpdate.setFont(new java.awt.Font("Tahoma", 0, 10));
        buttonGetUpdate.setHideActionText(false);
        buttonGetUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        buttonGetUpdate.setMaximumSize(new java.awt.Dimension(120, 26));
        buttonGetUpdate.setPreferredSize(new java.awt.Dimension(120, 26));
        toolBarUpdates.add(buttonGetUpdate);

        jPanel2.add(toolBarUpdates);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        panelMain.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        panelPerspectives.setLayout(new java.awt.BorderLayout());

        tabbedPerpectives.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabbedPerpectives.setFocusable(false);
        panelPerspectives.add(tabbedPerpectives, java.awt.BorderLayout.CENTER);

        panelMain.add(panelPerspectives, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelMain, java.awt.BorderLayout.CENTER);

        panelStatus.setLayout(new java.awt.GridLayout(1, 0));
        getContentPane().add(panelStatus, java.awt.BorderLayout.SOUTH);

        menuBarMain.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("Button.shadow")));
        menuBarMain.setFocusable(false);

        menuProgram.setMnemonic('P');
        menuProgram.setText(stringManager.getString("mf-menuProgram-text")); // NOI18N
        menuProgram.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                menuProgramMenuSelected(evt);
            }
        });

        menuNewConnection.setAction(cmNewConnection);
        menuProgram.add(menuNewConnection);

        menuDrivers.setAction(cmDrivers);
        menuProgram.add(menuDrivers);
        menuProgram.add(jSeparator1);

        menuSettings.setText("Ustawienia"); // NOI18N

        menuOrbadaSettings.setAction(cmOrbadaSettings);
        menuSettings.add(menuOrbadaSettings);

        menuPluginSettings.setAction(cmPluginSettings);
        menuSettings.add(menuPluginSettings);

        menuPluginManager.setAction(cmPluginManager);
        menuSettings.add(menuPluginManager);
        menuSettings.add(jSeparator9);

        menuOrbadaConfigFile.setAction(cmOrbadaConfigFile);
        menuSettings.add(menuOrbadaConfigFile);

        menuOrbadaLog4jFile.setAction(cmOrbadaLog4jFile);
        menuSettings.add(menuOrbadaLog4jFile);

        menuProgram.add(menuSettings);

        menuSpecialFiles.setText("Specjalne pliki"); // NOI18N

        menuOrbadaLogFile.setAction(cmOrbadaLogFile);
        menuSpecialFiles.add(menuOrbadaLogFile);

        menuOrbadaLogErrorFile.setAction(cmOrbadaLogErrorFile);
        menuSpecialFiles.add(menuOrbadaLogErrorFile);

        menuProgram.add(menuSpecialFiles);
        menuProgram.add(jSeparator18);

        menuFullScreen.setAction(cmFullScreen);
        menuProgram.add(menuFullScreen);
        menuProgram.add(jSeparator13);

        menuUserProperties.setAction(cmUserProperties);
        menuProgram.add(menuUserProperties);

        menuChangeUserPassword.setAction(cmChangeUserPassword);
        menuProgram.add(menuChangeUserPassword);

        menuUserList.setAction(cmUserList);
        menuProgram.add(menuUserList);
        menuProgram.add(sepRequests);

        menuSuggestionRequest.setAction(cmSuggestionRequest);
        menuProgram.add(menuSuggestionRequest);

        menuProblemRequest.setAction(cmProblemRequest);
        menuProgram.add(menuProblemRequest);
        menuProgram.add(jSeparator2);

        menuExit.setAction(cmExit);
        menuProgram.add(menuExit);

        menuBarMain.add(menuProgram);

        menuConnection.setMnemonic('O');
        menuConnection.setText(stringManager.getString("mf-menuConnection-text")); // NOI18N

        menuDBStartTransaction.setAction(cmDBStartTransaction);
        menuConnection.add(menuDBStartTransaction);
        menuConnection.add(jSeparator19);

        menuDbCommit.setAction(cmDbCommit);
        menuConnection.add(menuDbCommit);

        menuDbRollback.setAction(cmDbRollback);
        menuConnection.add(menuDbRollback);
        menuConnection.add(jSeparator7);

        menuDbDisconnect.setAction(cmDbDisconnect);
        menuConnection.add(menuDbDisconnect);

        menuDbReconnect.setAction(cmDbReconnect);
        menuConnection.add(menuDbReconnect);
        menuConnection.add(jSeparator11);

        menuCustomizeConnection.setAction(cmCustomizeConnection);
        menuConnection.add(menuCustomizeConnection);

        menuDatabaseInfo.setAction(cmDatabaseInfo);
        menuConnection.add(menuDatabaseInfo);

        menuBarMain.add(menuConnection);

        menuEdit.setMnemonic('E');
        menuEdit.setText(stringManager.getString("mf-menuEdit-text")); // NOI18N
        menuBarMain.add(menuEdit);

        menuTools.setMnemonic('N');
        menuTools.setText(stringManager.getString("mf-menuTools-text")); // NOI18N
        menuTools.add(jSeparator12);

        jMenuItem1.setAction(cmToolsSettings);
        menuTools.add(jMenuItem1);

        menuTemplatesSettings.setAction(cmTemplateSettings);
        menuTools.add(menuTemplatesSettings);

        jMenu1.setText("jMenu1");
        menuTools.add(jMenu1);

        menuBarMain.add(menuTools);

        menuPerspective.setMnemonic('K');
        menuPerspective.setText(stringManager.getString("mf-menuPerspective-text")); // NOI18N

        menuToolsPerspective.setAction(cmSpecialPerspective);
        menuPerspective.add(menuToolsPerspective);

        menuSelectNewPerspective.setText("Choose a new perspective for the connection"); // NOI18N
        menuSelectNewPerspective.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                menuSelectNewPerspectiveMenuSelected(evt);
            }
        });

        menuWindowNewPerspective.setAction(cmNewPerspective);
        menuSelectNewPerspective.add(menuWindowNewPerspective);
        menuSelectNewPerspective.add(sepMenuWindowPerspective);

        menuPerspective.add(menuSelectNewPerspective);

        menuPerspectiveGadgets.setText("Narz�dzia perspektywy"); // NOI18N
        menuPerspectiveGadgets.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                menuPerspectiveGadgetsMenuSelected(evt);
            }
        });
        menuPerspective.add(menuPerspectiveGadgets);
        menuPerspective.add(jSeparator3);

        menuClosePerspective.setAction(cmClosePerspective);
        menuPerspective.add(menuClosePerspective);

        menuCloseAllPerspectives.setAction(cmCloseAllPerspectives);
        menuPerspective.add(menuCloseAllPerspectives);
        menuPerspective.add(jSeparator5);

        menuResetPerspective.setAction(cmResetPerspective);
        menuPerspective.add(menuResetPerspective);

        menuPerspectiveProperties.setText("! w�a�ciwo�ci perspektywy");
        menuPerspectiveProperties.setAction(cmPerspectiveProperties = new PerspectivePropertiesAction());
        menuPerspective.add(menuPerspectiveProperties);

        menuMovePerspectiveLeft.setText("! przesu� w lewo");
        menuMovePerspectiveLeft.setAction(cmMovePerspectiveLeft = new MovePerspectiveLeftAction());
        menuPerspective.add(menuMovePerspectiveLeft);

        menuMovePerspectiveRight.setText("! przesu� w prawo");
        menuMovePerspectiveRight.setAction(cmMovePerspectiveRight = new MovePerspectiveRightAction());
        menuPerspective.add(menuMovePerspectiveRight);

        menuClonePerspectiveViews.setAction(cmClonePerspectiveViews);
        menuPerspective.add(menuClonePerspectiveViews);
        menuPerspective.add(jSeparator4);

        menuViewProperites.setAction(cmViewProperites);
        menuPerspective.add(menuViewProperites);

        menuMoveViewLeft.setAction(cmMoveViewLeft);
        menuPerspective.add(menuMoveViewLeft);

        menuMoveViewRight.setAction(cmMoveViewRight);
        menuPerspective.add(menuMoveViewRight);
        menuPerspective.add(jSeparator10);

        menuCloseView.setAction(cmCloseView);
        menuPerspective.add(menuCloseView);
        menuPerspective.add(jSeparator14);

        menuMaximizeSplitView.setAction(cmMaximizeSplitView);
        menuPerspective.add(menuMaximizeSplitView);

        menuNavigation.setText("Navigation"); // NOI18N

        menuSelectPerspective.setAction(cmSelectPerspective);
        menuNavigation.add(menuSelectPerspective);

        menuSelectView.setAction(cmSelectView);
        menuNavigation.add(menuSelectView);

        menuSelectTab.setAction(cmSelectTab);
        menuNavigation.add(menuSelectTab);

        menuPerspective.add(menuNavigation);

        menuBarMain.add(menuPerspective);

        menuPerspectiveView.setMnemonic('W');
        menuPerspectiveView.setText(stringManager.getString("mf-menuPerspectiveView-text")); // NOI18N
        menuPerspectiveView.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                menuPerspectiveViewMenuSelected(evt);
            }
        });
        menuBarMain.add(menuPerspectiveView);

        menuHelp.setText(stringManager.getString("mf-menuHelp-text")); // NOI18N

        menuUserGuide.setAction(cmUserGuide);
        menuHelp.add(menuUserGuide);

        menuComponentShortcut.setAction(cmComponentShortcut);
        menuHelp.add(menuComponentShortcut);

        menuDocumentation.setAction(cmDocumentation);
        menuHelp.add(menuDocumentation);
        menuHelp.add(jSeparator17);

        menuPageProjectHome.setAction(cmPageProjectHome);
        menuHelp.add(menuPageProjectHome);

        menuPageReviews.setAction(cmPageReviews);
        menuHelp.add(menuPageReviews);

        menuPageDonations.setAction(cmPageDonations);
        menuHelp.add(menuPageDonations);
        menuHelp.add(jSeparator16);

        menuLastChanges.setAction(cmOrbadaLastChanges);
        menuHelp.add(menuLastChanges);

        menuGetUpdates.setAction(cmGetUpdate);
        menuGetUpdates.setText("Pobierz aktualizacje...");
        menuHelp.add(menuGetUpdates);

        menuAboutOrbada.setAction(cmAboutOrbada);
        menuHelp.add(menuAboutOrbada);

        menuBarMain.add(menuHelp);

        setJMenuBar(menuBarMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void cmDatabaseInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDatabaseInfoActionPerformed
  if (activePerspective != null && activePerspective.getDatabase() != null) {
    boolean found = false;
    DatabaseInfoProvider[] dip = Application.get().getServiceArray(DatabaseInfoProvider.class);
    if (dip != null && dip.length > 0) {
      for (int i=0; i<dip.length; i++) {
        if (dip[i].isForDatabase(activePerspective.getDatabase())) {
          found = true;
          MessageBox.show(this, stringManager.getString("mf-database-info"), dip[i].getBanner(activePerspective.getDatabase()), ModalResult.OK);
          break;
        }
      }
    }
    if (!found) {
      try {
        StringBuilder message = new StringBuilder();
        message.append(stringManager.getString("mf-database-dd")).append(" ").append(activePerspective.getDatabase().getMetaData().getDatabaseProductName()).append(", ").append(activePerspective.getDatabase().getMetaData().getDatabaseProductVersion()).append("\n");
        message.append(stringManager.getString("mf-driver-dd")).append(" ").append(activePerspective.getDatabase().getMetaData().getDriverName()).append(", ").append(activePerspective.getDatabase().getMetaData().getDriverVersion());
        MessageBox.show(this, stringManager.getString("mf-database-info"), message.toString(), ModalResult.OK);
      } catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }
}//GEN-LAST:event_cmDatabaseInfoActionPerformed

private void cmMaximizeSplitViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmMaximizeSplitViewActionPerformed
  Component comp = FocusManager.getCurrentManager().getFocusOwner();
  if (comp != null) {
    JSplitPane split = (JSplitPane)SwingUtil.getOwnerComponent(JSplitPane.class, comp);
    if (split != null) {
      int maxSize = split.getWidth();
      if (split.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
        maxSize = split.getHeight();
      }
      if (SwingUtil.isComponentOn(split.getLeftComponent(), comp)) {
        if (split.getDividerLocation() < maxSize -100) {
          split.setDividerLocation(1.00);
        }
        else {
          split.setDividerLocation(split.getLastDividerLocation());
        }
      }
      if (SwingUtil.isComponentOn(split.getRightComponent(), comp)) {
        if (split.getDividerLocation() > 100) {
          split.setDividerLocation(0.00);
        }
        else {
          split.setDividerLocation(split.getLastDividerLocation());
        }
      }
    }
  }
}//GEN-LAST:event_cmMaximizeSplitViewActionPerformed

private void cmOrbadaSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOrbadaSettingsActionPerformed
  OrbadaSettingsDialog.showDialog();
}//GEN-LAST:event_cmOrbadaSettingsActionPerformed

private void cmMoveViewLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmMoveViewLeftActionPerformed
  if (activePerspective != null) {
    activePerspective.moveViewLeft();
  }
}//GEN-LAST:event_cmMoveViewLeftActionPerformed

private void cmMoveViewRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmMoveViewRightActionPerformed
  if (activePerspective != null) {
    activePerspective.moveViewRight();
  }
}//GEN-LAST:event_cmMoveViewRightActionPerformed
  
private void cmSpecialPerspectiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSpecialPerspectiveActionPerformed
  openPerspectiveFor(null, null);
}//GEN-LAST:event_cmSpecialPerspectiveActionPerformed

private void cmSelectPerspectiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSelectPerspectiveActionPerformed
  PerspectivePanel c = SelectPerspectiveDialog.show(tabbedPerpectives, activePerspective);
  if (c != null) {
    setActivePerspective(c);
    tabbedPerpectives.setSelectedComponent(c);
  }
}//GEN-LAST:event_cmSelectPerspectiveActionPerformed

private void cmCloseAllPerspectivesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloseAllPerspectivesActionPerformed
  int i = 0;
  while (i < tabbedPerpectives.getTabCount()) {
    Component c = tabbedPerpectives.getComponentAt(i);
    if (c instanceof PerspectivePanel) {
      if (!closePerspective((PerspectivePanel)c)) {
        i++;
      }
    } else {
      i++;
    }
    if (cancelClose) {
      break;
    }
  }
  java.awt.EventQueue.invokeLater(new Runnable() {
    @Override
    public void run() {
      setActivePerspective((PerspectivePanel)tabbedPerpectives.getSelectedComponent());
    }
  });
}//GEN-LAST:event_cmCloseAllPerspectivesActionPerformed

private void cmDbReconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDbReconnectActionPerformed
  if (activePerspective != null && activePerspective.getDatabase() != null) {
    if (activePerspective.getDatabase().isConnected() &&
        MessageBox.show(this, stringManager.getString("mf-connection"), stringManager.getString("mf-reconnect-q"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.NO) {
      return;
    }
    try {
      activePerspective.getDatabase().close();
    }
    catch (Throwable ex) {
      ExceptionUtil.processException(ex);
    }
    try {
      activePerspective.getDatabase().connect();
      IDatabaseProvider[] dpa = Application.get().getServiceArray(IDatabaseProvider.class);
      for (IDatabaseProvider dp : dpa) {
        if (dp.isForDatabase(activePerspective.getDatabase())) {
          dp.afterConnection(activePerspective.getDatabase());
        }
      }
    } catch (SQLException ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
}//GEN-LAST:event_cmDbReconnectActionPerformed

private void menuPerspectiveViewMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_menuPerspectiveViewMenuSelected
  menuPerspectiveView.removeAll();
  if (activePerspective != null) {
    if (activePerspective.getGadgetServiceList().size() > 0) {
      JMenu menuGadgets = new JMenu(stringManager.getString("mf-perspective-tools"));
      for (PerpectiveGadgetProvider pg : activePerspective.getGadgetServiceList()) {
        menuGadgets.add(new JMenuItem(new CreatePerspectiveGadgetAction(pg, activePerspective)), 0);
      }
      menuPerspectiveView.add(menuGadgets);
      menuPerspectiveView.addSeparator();
    }
    ViewProvider[] viewList = Utils.sortViewList(Application.get().getServiceArray(ViewProvider.class));
    boolean added = false;
    boolean defaultAdded = false;
    if (viewList != null && viewList.length > 0) {
      String groupName = "";
      for (ViewProvider vp : viewList) {
        if (vp.isForDatabase(activePerspective.getDatabase())) {
          if (!groupName.equals(vp.getGroupName()) && added) {
            menuPerspectiveView.addSeparator();
          }
          if (vp.isDefaultView()) {
            if (!defaultAdded) {
              menuPerspectiveView.insertSeparator(0);
              defaultAdded = true;
            }
            menuPerspectiveView.insert(new CreateViewAction(vp, activePerspective), 0);
          }
          if (vp.getSubmenu() != null) {
            for (String title : vp.getSubmenu()) {
              if (title == null) {
                if (!vp.isDefaultView()) {
                  menuPerspectiveView.add(new CreateViewAction(vp, activePerspective));
                }
              }
              else {
                JMenu submenu = null;
                for (int i=0; i<menuPerspectiveView.getItemCount(); i++) {
                  Component c = menuPerspectiveView.getMenuComponent(i);
                  if (c instanceof JMenu && title.equals(((JMenu)c).getText())) {
                    submenu = (JMenu)c;
                    break;
                  }
                }
                if (submenu == null) {
                  submenu = new JMenu(title);
                  menuPerspectiveView.add(submenu);
                }
                submenu.add(new CreateViewAction(vp, activePerspective));
              }
            }
          }
          else {
            if (!vp.isDefaultView()) {
              menuPerspectiveView.add(new CreateViewAction(vp, activePerspective));
            }
          }
          groupName = vp.getGroupName() != null ? vp.getGroupName() : "";
          added = true;
        }
      }
    }
    menuPerspectiveView.addSeparator();
    menuPerspectiveView.add(cmSelectView);
  }
}//GEN-LAST:event_menuPerspectiveViewMenuSelected

private void cmNewPerspectiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewPerspectiveActionPerformed
  if (activePerspective != null) {
    newPerspectiveFor(activePerspective.getDatabase(), activePerspective.getSchema());
  }
}//GEN-LAST:event_cmNewPerspectiveActionPerformed

private void cmClosePerspectiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmClosePerspectiveActionPerformed
  if (activePerspective != null) {
    if (closePerspective(activePerspective)) {
      setActivePerspective(null);
    }
  }
}//GEN-LAST:event_cmClosePerspectiveActionPerformed

private void cmDriversActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDriversActionPerformed
  DriverListDialog.showDialog();
}//GEN-LAST:event_cmDriversActionPerformed

  private void cmNewConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmNewConnectionActionPerformed
    SchemaListDialog.showDialog();
}//GEN-LAST:event_cmNewConnectionActionPerformed
  
  private void cmExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmExitActionPerformed
    shutDown(false);
  }//GEN-LAST:event_cmExitActionPerformed

  private void cmPluginSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmPluginSettingsActionPerformed
    PluginSettingsDialog.showDialog();
  }//GEN-LAST:event_cmPluginSettingsActionPerformed

  private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    Application.get().postPluginMessage(new PluginMessage(Consts.orbadaSystemPluginId, "status-text", stringManager.getString("mf-orbada-started")));
    Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessageOrbadaStarted, null));
  }//GEN-LAST:event_formComponentShown

  private void cmCustomizeConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCustomizeConnectionActionPerformed
    if (activePerspective != null && activePerspective.getDatabase() != null) {
      PluginSettingsDialog.showDialog(activePerspective.getDatabase());
    }
}//GEN-LAST:event_cmCustomizeConnectionActionPerformed

  private void cmDbRollbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDbRollbackActionPerformed
    if (activePerspective != null && activePerspective.getDatabase() != null) {
      try {
        activePerspective.getDatabase().rollback();
      } catch (SQLException ex) {
        MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      }
    }
  }//GEN-LAST:event_cmDbRollbackActionPerformed

  private void cmDbCommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDbCommitActionPerformed
    if (activePerspective != null && activePerspective.getDatabase() != null) {
      try {
        activePerspective.getDatabase().commit();
      } catch (SQLException ex) {
        MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      }
    }
  }//GEN-LAST:event_cmDbCommitActionPerformed

  private void cmOrbadaConfigFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOrbadaConfigFileActionPerformed
    if (getToolsPerspective() == null) {
      openPerspectiveFor(null, null);
    }
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        tabbedPerpectives.setSelectedComponent(getToolsPerspective());
        getToolsPerspective().createView(new TextFileViewService(new File(Application.get().getConfigPath() + "/res/orbada.properties")), false, false);
      }
    });
    //Application.get().execTool("notepad", new Object[] {Application.get().getConfigPath() +"/orbada.properties"});
  }//GEN-LAST:event_cmOrbadaConfigFileActionPerformed

  private void cmToolsSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmToolsSettingsActionPerformed
    ToolListDialog.showDialog();
    Application.get().getToolList().reload();
    reloadToolsMenu();
  }//GEN-LAST:event_cmToolsSettingsActionPerformed

  private void cmPluginManagerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmPluginManagerActionPerformed
    PluginManagerDialog.showDialog();
  }//GEN-LAST:event_cmPluginManagerActionPerformed

  private void cmChangeUserPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmChangeUserPasswordActionPerformed
    ChangeUserPasswordDialog.showDialog();
  }//GEN-LAST:event_cmChangeUserPasswordActionPerformed

  private void cmUserPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmUserPropertiesActionPerformed
    UserPropertiesDialog.showDialog();
  }//GEN-LAST:event_cmUserPropertiesActionPerformed

  private void menuProgramMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_menuProgramMenuSelected
    cmUserProperties.setText(String.format(stringManager.getString("mf-user-properties"), new Object[] {Application.get().getUserName()}));
//    cmSuggestionRequest.setEnabled(Application.get().getUser().isRegistered());
//    cmProblemRequest.setEnabled(Application.get().getUser().isRegistered());
  }//GEN-LAST:event_menuProgramMenuSelected

  private void cmOrbadaLogFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOrbadaLogFileActionPerformed
    if (getToolsPerspective() == null) {
      openPerspectiveFor(null, null);
    }
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        Enumeration e = Logger.getLogger("orbada").getAllAppenders();
        while (e.hasMoreElements()) {
          final Object o = e.nextElement();
          if (o instanceof FileAppender) {
            java.awt.EventQueue.invokeLater(new Runnable() {
              @Override
              public void run() {
                tabbedPerpectives.setSelectedComponent(getToolsPerspective());
                getToolsPerspective().createView(new TextFileViewService(new File(((FileAppender)o).getFile())), false, false);
              }
            });
            //Application.get().execTool("notepad", new Object[] {((FileAppender)o).getFile()});
          }
        }
      }
    });
}//GEN-LAST:event_cmOrbadaLogFileActionPerformed

  private void cmOrbadaLogErrorFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOrbadaLogErrorFileActionPerformed
    if (getToolsPerspective() == null) {
      openPerspectiveFor(null, null);
    }
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        Enumeration e = Logger.getLogger("error-logger").getAllAppenders();
        while (e.hasMoreElements()) {
          final Object o = e.nextElement();
          if (o instanceof FileAppender) {
            java.awt.EventQueue.invokeLater(new Runnable() {
              @Override
              public void run() {
                tabbedPerpectives.setSelectedComponent(getToolsPerspective());
                getToolsPerspective().createView(new TextFileViewService(new File(((FileAppender)o).getFile())), false, false);
              }
            });
            //Application.get().execTool("notepad", new Object[] {((FileAppender)o).getFile()});
          }
        }
      }
    });
}//GEN-LAST:event_cmOrbadaLogErrorFileActionPerformed

  private void cmSelectNewPerspectiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSelectNewPerspectiveActionPerformed
    menuPerspectives.show(buttonSelectNewPerspective, 0, buttonSelectNewPerspective.getHeight());
  }//GEN-LAST:event_cmSelectNewPerspectiveActionPerformed

  private void menuPerspectivesPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_menuPerspectivesPopupMenuWillBecomeVisible
    menuPerspectives.removeAll();
    if (activePerspective != null) {
      menuPerspectives.add(cmNewPerspective);
      menuPerspectives.addSeparator();
      boolean added = false;
      Query query = InternalDatabase.get().createQuery();
      try {
        query.setSqlText("select * from perspectives where pps_usr_id = :usr_id and pps_sch_id = :sch_id order by pps_id");
        query.paramByName("usr_id").setString(Application.get().getUserId());
        query.paramByName("sch_id").setString(activePerspective.getSchema().getSchId());
        query.open();
        while (!query.eof()) {
          Perspective perspective = new Perspective(InternalDatabase.get());
          perspective.updateFrom(query);
          JMenuItem item = menuPerspectives.add(
            new CreatePerspectiveAction(
              perspective,
              activePerspective.getDatabase(),
              activePerspective.getSchema()
            ));
          if (perspective.getPpsId().equals(activePerspective.getPerspective().getPpsId())) {
            Font font = item.getFont();
            item.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
          }
          added = true;
          query.next();
        }
      }
      catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      finally {
        query.close();
      }
      if (added) {
        menuPerspectives.addSeparator();
      }
      menuPerspectives.add(cmDeletePerspective);
    }
  }//GEN-LAST:event_menuPerspectivesPopupMenuWillBecomeVisible

  private void menuSelectNewPerspectiveMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_menuSelectNewPerspectiveMenuSelected
    menuSelectNewPerspective.removeAll();
    if (activePerspective != null) {
      menuSelectNewPerspective.add(cmNewPerspective);
      menuSelectNewPerspective.addSeparator();
      boolean added = false;
      Query query = InternalDatabase.get().createQuery();
      try {
        query.setSqlText("select * from perspectives where pps_usr_id = :usr_id and (pps_sch_id = :sch_id or (:sch_id is null and pps_sch_id is null)) order by pps_id");
        query.paramByName("usr_id").setString(Application.get().getUserId());
        if (activePerspective.getSchema() != null) {
          query.paramByName("sch_id").setString(activePerspective.getSchema().getSchId());
        }
        query.open();
        while (!query.eof()) {
          Perspective perspective = new Perspective(InternalDatabase.get());
          perspective.updateFrom(query);
          JMenuItem item = menuSelectNewPerspective.add(
            new CreatePerspectiveAction(
              perspective,
              activePerspective.getDatabase(),
              activePerspective.getSchema()
            ));
          if (perspective.getPpsId().equals(activePerspective.getPerspective().getPpsId())) {
            Font font = item.getFont();
            item.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
          }
          added = true;
          query.next();
        }
      }
      catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      finally {
        query.close();
      }
      if (added) {
        menuSelectNewPerspective.addSeparator();
      }
      menuSelectNewPerspective.add(cmDeletePerspective);
    }
  }//GEN-LAST:event_menuSelectNewPerspectiveMenuSelected

  private void cmDeletePerspectiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDeletePerspectiveActionPerformed
    if (activePerspective != null) {
      Perspective perspective = activePerspective.getPerspective();
      if (MessageBox.show(this, stringManager.getString("mf-perspective"), String.format(stringManager.getString("mf-delete-current-perspective-q"), new Object[] {perspective.getName()}), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.YES) {
        cmClosePerspective.performe();
        try {
          perspective.applyDelete();
        } catch (Exception ex) {
          MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
        }
      }
    }
  }//GEN-LAST:event_cmDeletePerspectiveActionPerformed

  private void cmResetPerspectiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmResetPerspectiveActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_cmResetPerspectiveActionPerformed

  private void cmSelectViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSelectViewActionPerformed
    ViewAccesibilities va = SelectViewDialog.show(activePerspective);
    if (va != null) {
      activePerspective.getTabbedViews().setSelectedComponent(va.getViewComponent());
    }
  }//GEN-LAST:event_cmSelectViewActionPerformed

  private void cmTemplateSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmTemplateSettingsActionPerformed
    TemplateListDialog.showDialog();
  }//GEN-LAST:event_cmTemplateSettingsActionPerformed

  private void cmViewProperitesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmViewProperitesActionPerformed
    if (activePerspective != null && activePerspective.getTabbedViews().getSelectedComponent() != null) {
      ViewAccesibilities va = activePerspective.getViewAccesibilities(activePerspective.getTabbedViews().getSelectedComponent());
      ViewPropertiesDialog.showDialog(va);
    }
  }//GEN-LAST:event_cmViewProperitesActionPerformed

private void menuPerspectiveGadgetsMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_menuPerspectiveGadgetsMenuSelected
  menuPerspectiveGadgets.removeAll();
  if (activePerspective != null) {
    for (PerpectiveGadgetProvider pg : activePerspective.getGadgetServiceList()) {
      menuPerspectiveGadgets.add(new JMenuItem(new CreatePerspectiveGadgetAction(pg, activePerspective)), 0);
    }
  }
}//GEN-LAST:event_menuPerspectiveGadgetsMenuSelected

private void cmSelectTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSelectTabActionPerformed
  SelectTabDialog.show(FocusManager.getCurrentManager().getFocusOwner());
}//GEN-LAST:event_cmSelectTabActionPerformed

private void cmCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmCloseViewActionPerformed
  if (activePerspective != null) {
    activePerspective.closeCurrentView();
  }
}//GEN-LAST:event_cmCloseViewActionPerformed

private void cmSuggestionRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmSuggestionRequestActionPerformed
  RequestSuggestionDialog.showDialog();
}//GEN-LAST:event_cmSuggestionRequestActionPerformed

private void cmProblemRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmProblemRequestActionPerformed
  RequestProblemDialog.showDialog();
}//GEN-LAST:event_cmProblemRequestActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
  shutDown(true);
}//GEN-LAST:event_formWindowClosing

private void cmComponentShortcutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmComponentShortcutActionPerformed
  ComponentActionMapDialog.showDialog();
}//GEN-LAST:event_cmComponentShortcutActionPerformed

private void cmUserGuideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmUserGuideActionPerformed
  Utils.gotoHelp();
}//GEN-LAST:event_cmUserGuideActionPerformed

private void cmGetUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmGetUpdateActionPerformed
  UpdateInfoDialog.showDialog();
}//GEN-LAST:event_cmGetUpdateActionPerformed

private void cmOrbadaLastChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOrbadaLastChangesActionPerformed
  UpdateInfoDialog.showDialog();
}//GEN-LAST:event_cmOrbadaLastChangesActionPerformed

private void cmDbDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDbDisconnectActionPerformed
  if (MessageBox.show(this, stringManager.getString("mf-connection"), stringManager.getString("mf-close-connection-with-database-q"), ModalResult.YESNO, MessageBox.QUESTION) == ModalResult.NO) {
    return;
  }
  if (activePerspective != null && activePerspective.getDatabase() != null) {
    try {
      activePerspective.getDatabase().close();
    }
    catch (Throwable ex) {
      ExceptionUtil.processException(ex);
    }
  }
}//GEN-LAST:event_cmDbDisconnectActionPerformed

private void cmUserListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmUserListActionPerformed
  UserListDialog.showDialog();
}//GEN-LAST:event_cmUserListActionPerformed

private void cmAboutOrbadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmAboutOrbadaActionPerformed
  AboutOrbadaDialog.showDialog();
}//GEN-LAST:event_cmAboutOrbadaActionPerformed

private void cmOrbadaLog4jFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmOrbadaLog4jFileActionPerformed
    if (getToolsPerspective() == null) {
      openPerspectiveFor(null, null);
    }
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        tabbedPerpectives.setSelectedComponent(getToolsPerspective());
        getToolsPerspective().createView(new TextFileViewService(new File(Application.get().getConfigPath() +"/log4j.xml")), false, false);
      }
    });
}//GEN-LAST:event_cmOrbadaLog4jFileActionPerformed

private void cmDocumentationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDocumentationActionPerformed
  try {
    Desktop.getDesktop().browse(new URI("http://orbada.sourceforge.net/"));
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmDocumentationActionPerformed

private void cmPageProjectHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmPageProjectHomeActionPerformed
  try {
    Desktop.getDesktop().browse(new URI("http://orbada.sourceforge.net/"));
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmPageProjectHomeActionPerformed

private void cmPageReviewsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmPageReviewsActionPerformed
  try {
    Desktop.getDesktop().browse(new URI("http://sourceforge.net/projects/orbada/reviews/"));
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmPageReviewsActionPerformed

private void cmPageDonationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmPageDonationsActionPerformed
  try {
    Desktop.getDesktop().browse(new URI("http://sourceforge.net/project/project_donations.php?group_id=361699"));
  } catch (Exception ex) {
    ExceptionUtil.processException(ex);
  }
}//GEN-LAST:event_cmPageDonationsActionPerformed

private void cmFullScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmFullScreenActionPerformed
  final WindowListener[] wl = getWindowListeners();
  for (WindowListener w : wl) {
    removeWindowListener(w);
  }
  java.awt.EventQueue.invokeLater(new Runnable() {
    @Override
    public void run() {
      fullScreenMode = !fullScreenMode;
      displayChanger.setDisplayMode(fullScreenMode);
      MainFrame.this.repaint();
      java.awt.EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          for (WindowListener w : wl) {
            addWindowListener(w);
          }
        }
      });
    }
  });
}//GEN-LAST:event_cmFullScreenActionPerformed

  private void cmDBStartTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmDBStartTransactionActionPerformed
    if (activePerspective != null && activePerspective.getDatabase() != null) {
      try {
        activePerspective.getDatabase().startTransaction();
      } catch (SQLException ex) {
        MessageBox.show(SwingUtil.getRootFrame(), stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      }
    }
  }//GEN-LAST:event_cmDBStartTransactionActionPerformed

  private void cmClonePerspectiveViewsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmClonePerspectiveViewsActionPerformed
    PerspectivePanel panel = SelectPerspectiveDialog.show(tabbedPerpectives, activePerspective, true);
    if (panel != null) {
      activePerspective.createPerspectiveViews(panel);
    }
  }//GEN-LAST:event_cmClonePerspectiveViewsActionPerformed
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonClosePerspective;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonDatabaseInfo;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonDbCommit;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonDbRollback;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonDonation;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonGetUpdate;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonMoveViewLeft;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonMoveViewRight;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonNewToolsPerspective;
    private pl.mpak.sky.gui.swing.comp.ToolButton buttonSelectNewPerspective;
    private pl.mpak.sky.gui.swing.Action cmAboutOrbada;
    private pl.mpak.sky.gui.swing.Action cmChangeUserPassword;
    private pl.mpak.sky.gui.swing.Action cmClonePerspectiveViews;
    private pl.mpak.sky.gui.swing.Action cmCloseAllPerspectives;
    private pl.mpak.sky.gui.swing.Action cmClosePerspective;
    private pl.mpak.sky.gui.swing.Action cmCloseView;
    private pl.mpak.sky.gui.swing.Action cmComponentShortcut;
    private pl.mpak.sky.gui.swing.Action cmCustomizeConnection;
    private pl.mpak.sky.gui.swing.Action cmDBStartTransaction;
    private pl.mpak.sky.gui.swing.Action cmDatabaseInfo;
    private pl.mpak.sky.gui.swing.Action cmDbCommit;
    private pl.mpak.sky.gui.swing.Action cmDbDisconnect;
    private pl.mpak.sky.gui.swing.Action cmDbReconnect;
    private pl.mpak.sky.gui.swing.Action cmDbRollback;
    private pl.mpak.sky.gui.swing.Action cmDeletePerspective;
    private pl.mpak.sky.gui.swing.Action cmDocumentation;
    public pl.mpak.sky.gui.swing.Action cmDrivers;
    private pl.mpak.sky.gui.swing.Action cmExit;
    private pl.mpak.sky.gui.swing.Action cmFullScreen;
    private pl.mpak.sky.gui.swing.Action cmGetUpdate;
    private pl.mpak.sky.gui.swing.Action cmMaximizeSplitView;
    private pl.mpak.sky.gui.swing.Action cmMoveViewLeft;
    private pl.mpak.sky.gui.swing.Action cmMoveViewRight;
    public pl.mpak.sky.gui.swing.Action cmNewConnection;
    private pl.mpak.sky.gui.swing.Action cmNewPerspective;
    private pl.mpak.sky.gui.swing.Action cmOrbadaConfigFile;
    private pl.mpak.sky.gui.swing.Action cmOrbadaLastChanges;
    private pl.mpak.sky.gui.swing.Action cmOrbadaLog4jFile;
    private pl.mpak.sky.gui.swing.Action cmOrbadaLogErrorFile;
    private pl.mpak.sky.gui.swing.Action cmOrbadaLogFile;
    public pl.mpak.sky.gui.swing.Action cmOrbadaSettings;
    private pl.mpak.sky.gui.swing.Action cmPageDonations;
    private pl.mpak.sky.gui.swing.Action cmPageProjectHome;
    private pl.mpak.sky.gui.swing.Action cmPageReviews;
    private pl.mpak.sky.gui.swing.Action cmPluginManager;
    private pl.mpak.sky.gui.swing.Action cmPluginSettings;
    private pl.mpak.sky.gui.swing.Action cmProblemRequest;
    private pl.mpak.sky.gui.swing.Action cmResetPerspective;
    private pl.mpak.sky.gui.swing.Action cmSelectNewPerspective;
    private pl.mpak.sky.gui.swing.Action cmSelectPerspective;
    private pl.mpak.sky.gui.swing.Action cmSelectTab;
    private pl.mpak.sky.gui.swing.Action cmSelectView;
    private pl.mpak.sky.gui.swing.Action cmSpecialPerspective;
    private pl.mpak.sky.gui.swing.Action cmSuggestionRequest;
    private pl.mpak.sky.gui.swing.Action cmTemplateSettings;
    private pl.mpak.sky.gui.swing.Action cmToolsSettings;
    public pl.mpak.sky.gui.swing.Action cmUserGuide;
    private pl.mpak.sky.gui.swing.Action cmUserList;
    private pl.mpak.sky.gui.swing.Action cmUserProperties;
    private pl.mpak.sky.gui.swing.Action cmViewProperites;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JPopupMenu.Separator jSeparator19;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JMenuItem menuAboutOrbada;
    private javax.swing.JMenuBar menuBarMain;
    private javax.swing.JMenuItem menuChangeUserPassword;
    private javax.swing.JMenuItem menuClonePerspectiveViews;
    private javax.swing.JMenuItem menuCloseAllPerspectives;
    private javax.swing.JMenuItem menuClosePerspective;
    private javax.swing.JMenuItem menuCloseView;
    private javax.swing.JMenuItem menuComponentShortcut;
    private javax.swing.JMenu menuConnection;
    private javax.swing.JMenuItem menuCustomizeConnection;
    private javax.swing.JMenuItem menuDBStartTransaction;
    private javax.swing.JMenuItem menuDatabaseInfo;
    private javax.swing.JMenuItem menuDbCommit;
    private javax.swing.JMenuItem menuDbDisconnect;
    private javax.swing.JMenuItem menuDbReconnect;
    private javax.swing.JMenuItem menuDbRollback;
    private javax.swing.JMenuItem menuDocumentation;
    private javax.swing.JMenuItem menuDrivers;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuFullScreen;
    private javax.swing.JMenuItem menuGetUpdates;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuLastChanges;
    private javax.swing.JMenuItem menuMaximizeSplitView;
    private javax.swing.JMenuItem menuMovePerspectiveLeft;
    private javax.swing.JMenuItem menuMovePerspectiveRight;
    private javax.swing.JMenuItem menuMoveViewLeft;
    private javax.swing.JMenuItem menuMoveViewRight;
    private javax.swing.JMenu menuNavigation;
    private javax.swing.JMenuItem menuNewConnection;
    private javax.swing.JMenuItem menuNewPerspective;
    private javax.swing.JMenuItem menuOrbadaConfigFile;
    private javax.swing.JMenuItem menuOrbadaLog4jFile;
    private javax.swing.JMenuItem menuOrbadaLogErrorFile;
    private javax.swing.JMenuItem menuOrbadaLogFile;
    private javax.swing.JMenuItem menuOrbadaSettings;
    private javax.swing.JMenuItem menuPageDonations;
    private javax.swing.JMenuItem menuPageProjectHome;
    private javax.swing.JMenuItem menuPageReviews;
    private javax.swing.JMenu menuPerspective;
    private javax.swing.JMenu menuPerspectiveGadgets;
    private javax.swing.JMenuItem menuPerspectiveProperties;
    private javax.swing.JMenu menuPerspectiveView;
    private javax.swing.JPopupMenu menuPerspectives;
    private javax.swing.JMenuItem menuPluginManager;
    private javax.swing.JMenuItem menuPluginSettings;
    private javax.swing.JMenuItem menuProblemRequest;
    private javax.swing.JMenu menuProgram;
    private javax.swing.JMenuItem menuResetPerspective;
    private javax.swing.JMenu menuSelectNewPerspective;
    private javax.swing.JMenuItem menuSelectPerspective;
    private javax.swing.JMenuItem menuSelectTab;
    private javax.swing.JMenuItem menuSelectView;
    private javax.swing.JMenu menuSettings;
    private javax.swing.JMenu menuSpecialFiles;
    private javax.swing.JMenuItem menuSuggestionRequest;
    private javax.swing.JMenuItem menuTemplatesSettings;
    private javax.swing.JMenu menuTools;
    private javax.swing.JMenuItem menuToolsPerspective;
    private javax.swing.JMenuItem menuUserGuide;
    private javax.swing.JMenuItem menuUserList;
    private javax.swing.JMenuItem menuUserProperties;
    private javax.swing.JPopupMenu menuViewPopup;
    private javax.swing.JMenuItem menuViewProperites;
    private javax.swing.JMenuItem menuWindowNewPerspective;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelPerspectiveTools;
    private javax.swing.JPanel panelPerspectives;
    private javax.swing.JPanel panelStatus;
    private javax.swing.JPanel panelToolBars;
    private javax.swing.JPanel panelToolTip;
    private javax.swing.JMenuItem ppmCloseView;
    private javax.swing.JMenuItem ppmMoveViewLeft;
    private javax.swing.JMenuItem ppmMoveViewRight;
    private javax.swing.JMenuItem ppmViewProperties;
    private javax.swing.JSeparator sepMenuPerspective;
    private javax.swing.JSeparator sepMenuWindowPerspective;
    private javax.swing.JSeparator sepRequests;
    private javax.swing.JTabbedPane tabbedPerpectives;
    private pl.mpak.sky.gui.swing.comp.ToolButton tbConnection;
    private javax.swing.JToolBar toolBarMain;
    private javax.swing.JToolBar toolBarTools;
    private javax.swing.JToolBar toolBarUpdates;
    private pl.mpak.sky.gui.swing.comp.ToolButton toolButton1;
    // End of variables declaration//GEN-END:variables

}
