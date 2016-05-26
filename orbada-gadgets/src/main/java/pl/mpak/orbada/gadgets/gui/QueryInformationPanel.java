/*
 * QueryInformationPanel.java
 *
 * Created on 12 paüdziernik 2008, 20:56
 */

package pl.mpak.orbada.gadgets.gui;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import pl.mpak.orbada.gadgets.Consts;
import pl.mpak.orbada.gadgets.OrbadaGadgetsPlugin;
import pl.mpak.orbada.plugins.IGadgetAccesibilities;
import pl.mpak.orbada.plugins.IProcessMessagable;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.usedb.UseDBException;
import pl.mpak.util.Configurable;
import pl.mpak.util.Titleable;
import pl.mpak.util.task.Task;
import pl.mpak.util.timer.Timer;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author  akaluza
 */
public class QueryInformationPanel extends javax.swing.JPanel implements Titleable, Closeable, Configurable, IProcessMessagable {

  private StringManager stringManager = StringManagerFactory.getStringManager("gadgets");

  private IGadgetAccesibilities accesibilities;
  private ArrayList<QueryInformation> infoList;
  private Timer timer;
  private boolean refreshing;

  /** Creates new form QueryInformationPanel */
  public QueryInformationPanel(IGadgetAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    infoList = new ArrayList<QueryInformation>();
    initComponents();
    init();
  }

  private void init() {
    OrbadaGadgetsPlugin.refreshQueue.add(timer = new Timer(1000L) {
      public void run() {
        if (!refreshing) {
          refresh();
        }
      }
    });
    accesibilities.getApplication().getOrbadaDatabase().getTaskPool().addTask(new Task() {
      public void run() {
        reload();
      }
    });
    accesibilities.getApplication().registerRequestMessager(this);
  }
  
  private void reload() {
    synchronized (infoList) {
      infoList.clear();
      Query query = accesibilities.getApplication().getOrbadaDatabase().createQuery();
      try {
        query.setSqlText(
          "select gqi_sql, qip_interval_s, qip_order\n" +
          "  from og_queryinfos, og_queryinfo_perspectives\n" +
          " where gqi_id = qip_gqi_id \n" +
          "   and qip_pps_id = :PPS_ID\n" +
          " order by qip_order");
        query.paramByName("PPS_ID").setString(accesibilities.getPerspectiveAccesibilities().getPerspectiveId());
        query.open();
        while (!query.eof()) {
          infoList.add(new QueryInformation(accesibilities.getDatabase(), query.fieldByName("gqi_sql").getString(), query.fieldByName("qip_interval_s").getInteger()));
          query.next();
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
  
  private void refresh() {
    if (!SwingUtil.isVisible(this)) {
      return;
    }
    refreshing = true;
    try {
      synchronized (infoList) {
        boolean mustRefresh = false;
        for (QueryInformation info : infoList) {
          if (info.isChange()) {
            mustRefresh = true;
            break;
          }
        }
        if (mustRefresh) {
          StringBuffer sb = new StringBuffer("<html>");
          sb.append("<style>");
          sb.append("  body { font-family: Tahoma, Arial, serif; font-size: 9px; }");
          sb.append("</style>");
          sb.append("<head></head>");
          sb.append("<body>");
          if (infoList.size() > 0) {
            sb.append("<center><table cellspacing=0 cellpading=0 width=\"100%\">\n");
            for (QueryInformation info : infoList) {
              sb.append(info.getInfo());
            }
            sb.append("</table></center>\n");
          }
          else {
            sb.append(stringManager.getString("QueryInformationPanel-info-1"));
            sb.append(stringManager.getString("QueryInformationPanel-info-2"));
          }
          sb.append("</body>");
          sb.append("</html>");
          editInfo.setText(sb.toString());
        }
      }
    }
    finally {
      refreshing = false;
    }
  }

  public String getTitle() {
    return String.format(stringManager.getString("QueryInformationPanel-title"), new Object[] {accesibilities.getDatabase().getPublicName()});
  }

  public void close() throws IOException {
    accesibilities.getApplication().unregisterRequestMessager(this);
    timer.cancel();
    infoList.clear();
  }

  public boolean configure() {
    try {
      boolean result = QueryInformationConfigDialog.show(accesibilities.getPerspectiveAccesibilities());
      if (result) {
        java.awt.EventQueue.invokeLater(new Runnable() {
          public void run() {
            reload();
          }
        });
      }
      return result;
    } catch (UseDBException ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      return false;
    }
  }

  public boolean isConfig() {
    return true;
  }
  
  public void processMessage(PluginMessage message) {
    if (message.isMessageId(Consts.QueryInfoPanelReload)) {
      accesibilities.getApplication().getOrbadaDatabase().getTaskPool().addTask(new Task() {
        public void run() {
          reload();
        }
      });
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

    jScrollPane1 = new javax.swing.JScrollPane();
    editInfo = new pl.mpak.sky.gui.swing.comp.HtmlEditorPane();

    setLayout(new java.awt.BorderLayout());

    editInfo.setDoubleBuffered(true);
    jScrollPane1.setViewportView(editInfo);

    add(jScrollPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.HtmlEditorPane editInfo;
  private javax.swing.JScrollPane jScrollPane1;
  // End of variables declaration//GEN-END:variables

}
