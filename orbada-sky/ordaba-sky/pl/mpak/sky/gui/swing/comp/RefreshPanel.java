package pl.mpak.sky.gui.swing.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.util.timer.Timer;

/**
 * <p>You mast call cancel() when close window/panel/etc</p>
 * 
 * @author akaluza
 *
 */
public class RefreshPanel extends JPanel {

  private static final long serialVersionUID = 6341912061260353955L;
  
  private static pl.mpak.util.timer.TimerQueue refreshQueue;
  
  private final EventListenerList refreshListenerList = new EventListenerList();
  private Timer timer;
  private ComboBox comboRefresh;
  private Action cmQuickStop;
  private JLabel labelRefresh;
  private ComboBoxModel model;

  public RefreshPanel() {
    super();
    init();
  }

  public RefreshPanel(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
    init();
  }

  private void init() {
    if (refreshQueue == null) {
      refreshQueue = pl.mpak.util.timer.TimerManager.getTimer("refresh-panel-queue"); // NOI18N
    }
    
    setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 3, 0));
    
    cmQuickStop = new Action();
    comboRefresh = new ComboBox();
    labelRefresh = new JLabel(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/orange_bdot.gif")); // NOI18N
    
    cmQuickStop.setActionCommandKey("cmQuickStop"); // NOI18N
    cmQuickStop.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        comboRefresh.setSelectedIndex(0);
      }
    });
    cmQuickStop.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/red_bdot.gif")); // NOI18N
    cmQuickStop.setText(Messages.getString("refresh-cmQuickStop-text")); // NOI18N
    cmQuickStop.setEnabled(false);
    
    model = new javax.swing.DefaultComboBoxModel(new String[] { "off", "0.5", "1", "2", "5", "10", "30", "60", "120" });
    comboRefresh.setModel(model);
    comboRefresh.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        if (comboRefresh.getSelectedItem() != null) {
          if (comboRefresh.getSelectedIndex() > 0) {
            timer.setInterval((int)getRefreshTime());
            timer.setEnabled(true);
          }
          else {
            timer.setEnabled(false);
          }
          cmQuickStop.setEnabled(timer.isEnabled());
        }
      }
    });
    
    labelRefresh.setEnabled(false);
    
    add(new Label(Messages.getString("refresh-label-dd"), comboRefresh)); // NOI18N
    add(comboRefresh);
    add(new Label(Messages.getString("refresh-sec-1"))); // NOI18N
    add(new ToolButton(cmQuickStop));
    add(labelRefresh);
    
    timer = new Timer(1000) {
      private volatile boolean refreshing = false;
      {
        setEnabled(false);
      }
      public void run() {
        if (!refreshing) {
          refreshing = true;
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              labelRefresh.setEnabled(true);
            }
          });
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              try {
                fireRefresh();
              }
              finally {
                java.awt.EventQueue.invokeLater(new Runnable() {
                  public void run() {
                    labelRefresh.setEnabled(false);
                  }
                });
                refreshing = false;
              }
            }
          });
        }
      }
    };
    refreshQueue.add(timer);
  }
  
  private long getRefreshTime() {
    if (comboRefresh.getSelectedIndex() == 0) {
      return 1000 *1000;
    }
    return (long)(Double.parseDouble(comboRefresh.getSelectedItem().toString()) *1000);
  }
  
  /**
   * You mast call this function when close window/panel/etc
   */
  public void cancel() {
    timer.cancel();
  }

  public void addRefreshListener(RefreshListener listener) {
    refreshListenerList.add(RefreshListener.class, listener);
  }

  public void removeRefreshListener(RefreshListener listener) {
    refreshListenerList.remove(RefreshListener.class, listener);
  }
  
  public void fireRefresh() {
    long time = System.nanoTime();
    try {
      RefreshEvent e = new RefreshEvent(this);
      for (RefreshListener listener : refreshListenerList.getListeners(RefreshListener.class)) {
        listener.refresh(e);
      }
    }
    finally {
      if (System.nanoTime() -time > getRefreshTime() *1000000) {
        comboRefresh.setSelectedIndex(comboRefresh.getSelectedIndex() +1);
      }
    }
  }

  public ComboBoxModel getModel() {
    return model;
  }

  public void setModel(ComboBoxModel model) {
    this.model = model;
  }

}
