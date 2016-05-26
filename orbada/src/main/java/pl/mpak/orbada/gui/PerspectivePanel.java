package pl.mpak.orbada.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.Icon;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.gui.gadgets.GadgetPanel;
import pl.mpak.orbada.util.Utils;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.plugins.providers.PerspectiveProvider;
import pl.mpak.orbada.plugins.providers.ViewProvider;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.util.Utils;
import pl.mpak.sky.gui.swing.TabCloseComponent;
import pl.mpak.usedb.core.Database;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

import pl.mpak.orbada.db.Gadget;
import pl.mpak.orbada.db.Perspective;
import pl.mpak.orbada.db.Schema;
import pl.mpak.orbada.db.View;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.orbada.plugins.providers.PerpectiveGadgetProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.CloseAbilitable;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author  akaluza
 */
public class PerspectivePanel extends javax.swing.JPanel implements Closeable {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("orbada");

  private Database database;
  private LinkedList<ViewAccesibilities> viewList = new LinkedList<ViewAccesibilities>();
  private Schema schema;
  private Perspective perspective;
  private TabCloseComponent tabComponent;
  private int splitMouseX;
  PerspectiveAccesibilities perspectiveAccesibilities;
  private ArrayList<PerpectiveGadgetProvider> gadgetServiceList = new ArrayList<PerpectiveGadgetProvider>();
  private JSplitPane gadgetsSpliter;
  private ArrayList<GadgetPanel> gadgetList = new ArrayList<GadgetPanel>();
  private boolean updatingAllGadgets = false;

  
  public PerspectivePanel() {
    this(null, null);
  }

  public PerspectivePanel(Database database, Schema schema) {
    this.database = database;
    this.schema = schema;
    initComponents();
    init();
  }

  public PerspectivePanel(Database database, Schema schema, Perspective perspective) {
    this.database = database;
    this.schema = schema;
    this.perspective = perspective;
    initComponents();
    init();
  }

  public Database getDatabase() {
    return database;
  }
  
  public Schema getSchema() {
    return schema;
  }

  public TabCloseComponent getTabComponent() {
    return tabComponent;
  }

  public void setTabComponent(TabCloseComponent tabComponent) {
    this.tabComponent = tabComponent;
  }
  
  public Perspective getPerspective() {
    return perspective;
  }

  public PerspectiveAccesibilities getPerspectiveAccesibilities() {
    return perspectiveAccesibilities;
  }

  private void init() {
    panelPerspectiveGadgets.setVisible(false);
    perspectiveAccesibilities = new PerspectiveAccesibilities(this);

    ViewProvider[] vps = Application.get().getServiceArray(ViewProvider.class);
    int count = Application.get().getMainFrame().getDatabaseInUse(database);
    int viewCount = 0;
    try {
      viewCount = createPerspectiveViews(perspective.fieldByName("pps_id").getString());
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }

    addComponentListener(perspectiveAccesibilities);
    PerspectiveProvider[] pps = Application.get().getServiceArray(PerspectiveProvider.class);
    if (pps != null && pps.length > 0) {
      for (PerspectiveProvider pp : pps) {
        if (pp.isForDatabase(getDatabase())) {
          pp.setAccesibilities(perspectiveAccesibilities);
          pp.initialize();
          perspectiveAccesibilities.addSerivce(pp);
        }
      }
    }
    // domy�lnie otwierane widoki
    if (count == 0 && viewCount == 0 && vps != null && vps.length > 0) {
      for (ViewProvider vp : vps) {
        if (vp.isForDatabase(getDatabase()) && vp.isDefaultView()) {
          createView(vp, false, false);
        }
      }
    }
    
    PerpectiveGadgetProvider[] pgp = Application.get().getServiceArray(PerpectiveGadgetProvider.class);
    if (pgp != null && pgp.length > 0) {
      for (PerpectiveGadgetProvider pg : pgp) {
        if (pg.isForDatabase(getDatabase())) {
          gadgetServiceList.add(pg);
        }
      }
    }
    
    int gadgetCount = 0;
    Query gadgets = InternalDatabase.get().createQuery();
    updatingAllGadgets = true;
    try {
      gadgets.setSqlText("select gdg_name, gdg_minimized, gdg_height from gadgets where gdg_pps_id = :pps_id order by gdg_order");
      gadgets.paramByName("pps_id").setString(perspective.fieldByName("pps_id").getString());
      gadgets.open();
      while (!gadgets.eof()) {
        String gadgetId = gadgets.fieldByName("gdg_name").getString();
        for (PerpectiveGadgetProvider pg : gadgetServiceList) {
          if (gadgetId.equals(pg.getGadgetId())) {
            GadgetPanel gp = createGadget(pg, gadgets.fieldByName("gdg_height").getInteger());
            gp.setMinimized(StringUtil.toBoolean(gadgets.fieldByName("gdg_minimized").getString()));
            gadgetCount++;
            break;
          }
        }
        gadgets.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      updatingAllGadgets = false;
      updateGadgetPanel();
      gadgets.close();
    }
    if (gadgetCount > 0) {
      panelPerspectiveGadgets.setPreferredSize(new Dimension(Math.max(perspective.getGadgetsWidth(), 20), 0));
    }
    Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessagePerspectiveOpened, this));
  }
  
  public boolean canClose() {
    for (int i = 0; i < tabbedViews.getTabCount(); i++) {
      Component c = tabbedViews.getComponentAt(i);
      if (c instanceof CloseAbilitable) {
        if (!((CloseAbilitable)c).canClose()) {
          return false;
        }
      }
    }
    if (getDatabase() != null && getDatabase().getMetaData() != null && Application.get().getMainFrame().getDatabaseInUse(getDatabase()) == 1) {
      try {
        if (getDatabase().getMetaData().supportsTransactions() && !getDatabase().getConnection().getAutoCommit()) {
          switch (MessageBox.show(this, stringManager.getString("PerspectivePanel-database"), String.format(stringManager.getString("PerspectivePanel-close-connection-q"), new Object[] {getDatabase().getUrl()}), new int[] {ModalResult.YES, ModalResult.NO, ModalResult.CANCEL}, MessageBox.QUESTION)) {
            case ModalResult.YES:
              getDatabase().commit();
              break;
            case ModalResult.NO:
              if (!Application.get().getSettings().getValue(Consts.noRollbackOnClose, false)) {
                getDatabase().rollback();
              }
              break;
            default:
              return false;
          }
        }
      } catch (SQLException ex) {
        ExceptionUtil.processException(ex);
      }
    }
    return true;
  }
  
  public int createPerspectiveViews(String apps_id) {
    ViewProvider[] vps = Application.get().getServiceArray(ViewProvider.class);
    int viewCount = 0;

    Query views = InternalDatabase.get().createQuery();
    try {
      views.setSqlText("select vws_name, vws_hide_title, vws_hide_icon from views where vws_pps_id = :pps_id order by vws_order");
      views.paramByName("pps_id").setString(apps_id);
      views.open();
      while (!views.eof()) {
        String viewId = views.fieldByName("vws_name").getString();
        for (ViewProvider vp : vps) {
          if (vp.isForDatabase(getDatabase()) && viewId.equals(vp.getViewId())) {
            viewCount++;
            createView(vp, "T".equalsIgnoreCase(views.fieldByName("vws_hide_title").getString()), "T".equalsIgnoreCase(views.fieldByName("vws_hide_icon").getString()));
            break;
          }
        }
        views.next();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      views.close();
    }
    return viewCount;
  }
  
  public void createPerspectiveViews(PerspectivePanel panelFrom) {
    closeAllViews();
    Iterator<ViewAccesibilities> i = panelFrom.viewList.iterator();
    while (i.hasNext()) {
      ViewAccesibilities va = i.next();
      createView(va.getViewProvider(), va.isHideTitle(), va.isHideIcon());
    }
  }
  
  public void closeAllViews() {
    while (tabbedViews.getTabCount() > 0) {
      closeView(tabbedViews.getComponentAt(0));
    }
  }
  
  @Override
  public void close() throws IOException {
    Command command = InternalDatabase.get().createCommand();
    try {
      command.setSqlText("delete from views where vws_pps_id = :pps_id");
      command.paramByName("pps_id").setString(perspective.fieldByName("pps_id").getString());
      command.execute();

      int viewCount = 0;
      for (int i = 0; i < tabbedViews.getTabCount(); i++) {
        Component c = tabbedViews.getComponentAt(i);
        ViewAccesibilities va = getViewAccesibilities(c);
        if (va != null && va.getViewProvider().getViewId() != null) {
          View v = new View(InternalDatabase.get());
          v.setPpsId(perspective.fieldByName("pps_id").getString());
          v.setName(va.getViewProvider().getViewId());
          v.setOrder(viewCount);
          v.setHideTitle(va.isHideTitle());
          v.setHideIcon(va.isHideIcon());
          v.applyInsert();
          viewCount++;
        }
      }
      perspective.setViewCount(viewCount);
      perspective.applyUpdate();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }

    closeAllViews();
    
    updatingAllGadgets = true;
    try {
      command.setSqlText("delete from gadgets where gdg_pps_id = :pps_id");
      command.paramByName("pps_id").setString(perspective.fieldByName("pps_id").getString());
      command.execute();

      int gadgetCount = 0;
      for (int i = 0; i < gadgetList.size(); i++) {
        GadgetPanel gp = gadgetList.get(i);
        Gadget g = new Gadget(InternalDatabase.get());
        g.setPpsId(perspective.fieldByName("pps_id").getString());
        g.setName(gp.getProvider().getGadgetId());
        g.setOrder(gadgetCount);
        JSplitPane split = (JSplitPane)SwingUtil.getOwnerComponent(JSplitPane.class, gp);
        if (split != null) {
          g.setHeight(split.getDividerLocation());
        }
        else {
          g.setHeight(gp.getPreferredSize().height);
        }
        g.setMinimized(gp.isMinimized());
        g.applyInsert();
        gadgetCount++;
      }
      perspective.setGadgetCount(gadgetCount);
      if (gadgetCount > 0) {
        perspective.setGadgetsWidth(tabbedViews.getWidth());
      }
      perspective.applyUpdate();
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    updatingAllGadgets = true;
    try {
      while (gadgetList.size() > 0) {
        closeGadget(gadgetList.get(0));
      }
    }
    finally {
      updatingAllGadgets = false;
      updateGadgetPanel();
    }
    
    if (getDatabase() != null && Application.get().getMainFrame().getDatabaseInUse(getDatabase()) == 0) {
      getDatabase().close();
    }
    tabbedViews.removeAll();
    
    perspectiveAccesibilities.close();
    removeComponentListener(perspectiveAccesibilities);
    perspectiveAccesibilities = null;
    viewList = null;
    tabComponent = null;
    Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessagePerspectiveClosed, this));
  }

  private boolean canClose(Component component) {
    if (component instanceof CloseAbilitable) {
      return ((CloseAbilitable)component).canClose();
    }
    return true;
  }

  public void closeView(Component component) {
    if (component instanceof Closeable) {
      try {
        ((Closeable) component).close();
      } catch (IOException ex) {
      }
    }
    ViewAccesibilities va = getViewAccesibilities(component);
    if (va != null) {
      va.close();
      viewList.remove(va);
    }
    tabbedViews.remove(component);
    Application.get().getMainFrame().viewClosed(this);
    Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessageViewClosed, component));
  }
  
  private GadgetPanel findGadget(JComponent onComponent, PerpectiveGadgetProvider provider) {
    GadgetPanel gp = null;
    for (int i=0; i<onComponent.getComponentCount(); i++) {
      if (onComponent.getComponent(i) instanceof GadgetPanel) {
        gp = (GadgetPanel)onComponent.getComponent(i);
        if (gp.getProvider().getClass().equals(provider.getClass())) {
          return gp;
        }
      }
      else if (onComponent.getComponent(i) instanceof JComponent) {
        JComponent component = findGadget((JComponent)onComponent.getComponent(i), provider);
        if (component != null) {
          return (GadgetPanel)component;
        }
      }
    }
    return null;
  }
  
  private void updateGadgetPanel() {
    panelGadgets.removeAll();
    if (gadgetList.isEmpty()) {
      panelPerspectiveGadgets.setVisible(false);
      removePerspectiveSpliter();
    }
    else {
      Component owner = gadgetList.get(0);
      for (int i=1; i<gadgetList.size(); i++) {
        JSplitPane split = createSpliter(JSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(owner);
        split.setBottomComponent(gadgetList.get(i));
        if (owner instanceof GadgetPanel) {
          if (((GadgetPanel)owner).getInitialHeight() > 0) {
            split.setDividerLocation(((GadgetPanel)owner).getInitialHeight());
          }
          else {
            split.setDividerLocation(0.9f);
          }
        }
        owner = split;
      }
      panelGadgets.add(owner);
      panelPerspectiveGadgets.setVisible(true);
      panelPerspectiveGadgets.validate();
      createPerspectiveSpliter();
    }
    panelGadgets.revalidate();
  }
  
  public void closeGadget(GadgetPanel gp) {
    try {
      if (gp instanceof GadgetPanel) {
        ((GadgetPanel)gp).close();
      }
    } catch (IOException ex) {
    } finally {
      gadgetList.remove(gp);
    }
    if (!updatingAllGadgets) {
      updateGadgetPanel();
    }
  }
  
  public GadgetPanel createGadget(PerpectiveGadgetProvider provider, int height) {
    GadgetPanel gp = findGadget(panelGadgets, provider);
    if (gp != null) {
      return gp;
    }
    gadgetList.add(gp = new GadgetPanel(this, provider, height));
    if (!updatingAllGadgets) {
      updateGadgetPanel();
    }
    return gp;
  }

  public ArrayList<PerpectiveGadgetProvider> getGadgetServiceList() {
    return gadgetServiceList;
  }
  
  private void createViewQueue(final ViewProvider view, final boolean hideTitle, final boolean hideIcon) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        createView(view, hideTitle, hideIcon);
      }
    });
  }

  public Component createView(ViewProvider view, boolean hideTitle, boolean hideIcon) {
    Icon icon = view.getIcon();
    TabCloseComponent tabClose = new TabCloseComponent(view.getPublicName(), icon);
    
    ViewAccesibilities va = new ViewAccesibilities(getDatabase(), tabClose, this, view);
    final Component c = view.createView(va);
    if (c != null) {
      va.setViewComponent(c);
      va.setHideTitle(hideTitle);
      va.setHideIcon(hideIcon);

      tabClose.getAction().addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (canClose(c)) {
            closeView(c);
          }
        }
      });

      tabbedViews.addTab(
        view.getPublicName(), null, c,
        "<html><b>" +view.getPublicName() +"</b><br>&nbsp;" +view.getDescription());
      tabbedViews.setTabComponentAt(tabbedViews.indexOfComponent(c), tabClose);
      tabbedViews.setSelectedComponent(c);
      viewList.add(va);
      Application.get().getMainFrame().viewAdded(this, view);
      Application.get().postPluginMessage(new PluginMessage(null, Consts.globalMessageViewOpened, c));
    }
    return c;
  }
  
  public void closeCurrentView() {
    for (int i=0; i<tabbedViews.getTabCount(); i++) {
      if (tabbedViews.getComponentAt(i).isVisible()) {
        if (canClose(tabbedViews.getComponentAt(i))) {
          closeView(tabbedViews.getComponentAt(i));
        }
        break;
      }
    }
  }

  public void setSelectedView(Component view) {
    tabbedViews.setSelectedComponent(view);
  }

  public int getViewCount() {
    return tabbedViews.getTabCount();
  }

  public Component getViewComponent(ViewProvider view) {
    Iterator<ViewAccesibilities> i = viewList.iterator();
    while (i.hasNext()) {
      ViewAccesibilities va = i.next();
      if (va.getViewProvider().getViewId() != null && va.getViewProvider().getViewId().equals(view.getViewId())) {
        return va.getViewComponent();
      }
    }
    return null;
  }

  public Component[] getViewComponentList(ViewProvider view) {
    ArrayList<Component> list = new ArrayList<Component>();
    Iterator<ViewAccesibilities> i = viewList.iterator();
    while (i.hasNext()) {
      ViewAccesibilities va = i.next();
      if (va.getViewProvider().getViewId() != null && va.getViewProvider().getViewId().equals(view.getViewId())) {
        list.add(va.getViewComponent());
      }
    }
    return list.toArray(new Component[list.size()]);
  }
  
  protected ViewAccesibilities getViewAccesibilities(Component view) {
    Iterator<ViewAccesibilities> i = viewList.iterator();
    while (i.hasNext()) {
      ViewAccesibilities va = i.next();
      if (va.getViewComponent().equals(view)) {
        return va;
      }
    }
    return null;
  }
  
  public boolean isViewCreated(Class<? extends ViewProvider> provider) {
    Iterator<ViewAccesibilities> i = viewList.iterator();
    while (i.hasNext()) {
      ViewAccesibilities va = i.next();
      if (va.getViewProvider().getClass() == provider) {
        return true;
      }
    }
    return false;
  }

  public IViewAccesibilities getViewAccesibilitiesForChild(Component child) {
    while (child != null && !equals(child)) {
      Iterator<ViewAccesibilities> i = viewList.iterator();
      while (i.hasNext()) {
        ViewAccesibilities va = i.next();
        if (va.getViewComponent().equals(child)) {
          return va;
        }
      }
      child = child.getParent();
    }
    return null;
  }

  public void moveViewRight() {
    Utils.moveTabRight(tabbedViews);
  }

  public void moveViewLeft() {
    Utils.moveTabLeft(tabbedViews);
  }

  private void updateStatusBar() {
    GridLayout gl = (GridLayout)panelStatus.getLayout();
    gl.setRows(panelStatus.getComponentCount());
  }
  
  public void addStatusBar(Component component) {
    if (component instanceof JComponent) {
      ((JComponent)component).setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("controlShadow")));
    }
    panelStatus.add(component);
    updateStatusBar();
  }
  
  public void removeStatusBar(Component component) {
    panelStatus.remove(component);
    updateStatusBar();
  }
  
  public javax.swing.JTabbedPane getTabbedViews() {
    return tabbedViews;
  }
  
  private JSplitPane createSpliter(int orientation) {
    JSplitPane split = new JSplitPane(orientation);
    split.setBorder(null);
    split.setContinuousLayout(true);
    split.setOneTouchExpandable(true);
    return split;
  }
  
  private JSplitPane createPerspectiveSpliter() {
    if (gadgetsSpliter == null) {
      gadgetsSpliter = createSpliter(JSplitPane.HORIZONTAL_SPLIT);
      gadgetsSpliter.setLeftComponent(tabbedViews);
      gadgetsSpliter.setRightComponent(panelPerspectiveGadgets);
      gadgetsSpliter.setResizeWeight(1f);
      panelRoot.remove(tabbedViews);
      panelRoot.remove(panelPerspectiveGadgets);
      panelRoot.add(gadgetsSpliter, BorderLayout.CENTER);
      if (perspective.getGadgetsWidth() > 50) {
        gadgetsSpliter.setDividerLocation(perspective.getGadgetsWidth());
      }
      else {
        gadgetsSpliter.setDividerLocation(0.5f);
      }
    }
    return gadgetsSpliter;
  }
  
  private void removePerspectiveSpliter() {
    if (gadgetsSpliter != null) {
      perspective.setGadgetsWidth(gadgetsSpliter.getDividerLocation());
      panelRoot.remove(gadgetsSpliter);
      panelRoot.add(tabbedViews, BorderLayout.CENTER);
      panelRoot.add(panelPerspectiveGadgets, BorderLayout.EAST);
      gadgetsSpliter = null;
    }
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    panelStatus = new javax.swing.JPanel();
    panelRoot = new javax.swing.JPanel();
    tabbedViews = new javax.swing.JTabbedPane();
    panelPerspectiveGadgets = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    panelGadgets = new javax.swing.JPanel();

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentHidden(java.awt.event.ComponentEvent evt) {
        formComponentHidden(evt);
      }
      public void componentShown(java.awt.event.ComponentEvent evt) {
        formComponentShown(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    panelStatus.setLayout(new java.awt.GridLayout(1, 0));
    add(panelStatus, java.awt.BorderLayout.SOUTH);

    panelRoot.setLayout(new java.awt.BorderLayout());

    tabbedViews.setFocusable(false);
    panelRoot.add(tabbedViews, java.awt.BorderLayout.CENTER);

    panelPerspectiveGadgets.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setBorder(null);

    pl.mpak.sky.gui.swing.VerticalFlowLayout verticalFlowLayout1 = new pl.mpak.sky.gui.swing.VerticalFlowLayout();
    verticalFlowLayout1.setHgap(1);
    verticalFlowLayout1.setVgap(1);
    verticalFlowLayout1.setAlignment(java.awt.FlowLayout.CENTER);
    verticalFlowLayout1.setVerticalFill(true);
    panelGadgets.setLayout(verticalFlowLayout1);
    jScrollPane1.setViewportView(panelGadgets);

    panelPerspectiveGadgets.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    panelRoot.add(panelPerspectiveGadgets, java.awt.BorderLayout.EAST);

    add(panelRoot, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  
private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
  Application.get().getMainFrame().setActivePerspective(this);
  if (tabbedViews.getSelectedComponent() != null) {
    ViewAccesibilities va = getViewAccesibilities(tabbedViews.getSelectedComponent());
    va.componentShow();
  }
}//GEN-LAST:event_formComponentShown

private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
  if (tabbedViews.getSelectedComponent() != null) {
    ViewAccesibilities va = getViewAccesibilities(tabbedViews.getSelectedComponent());
    va.componentHide();
  }
  Application.get().getMainFrame().setActivePerspective(null);
}//GEN-LAST:event_formComponentHidden


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JPanel panelGadgets;
  private javax.swing.JPanel panelPerspectiveGadgets;
  private javax.swing.JPanel panelRoot;
  private javax.swing.JPanel panelStatus;
  private javax.swing.JTabbedPane tabbedViews;
  // End of variables declaration//GEN-END:variables
  
}
