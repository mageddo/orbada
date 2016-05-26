package pl.mpak.orbada.oracle.dba.gui.dfvisual;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import pl.mpak.orbada.gui.comps.table.Table;
import pl.mpak.orbada.db.ConnectionFactory;
import pl.mpak.orbada.gui.cm.ComponentActionsAction;
import pl.mpak.orbada.gui.comps.table.SearchOnTable;
import pl.mpak.orbada.oracle.OrbadaOraclePlugin;
import pl.mpak.orbada.oracle.dba.OrbadaOracleDbaPlugin;
import pl.mpak.orbada.oracle.dba.Sql;
import pl.mpak.orbada.plugins.IViewAccesibilities;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.MessageBox;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.TableColumn;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.gui.swing.QueryTableCellRenderer;
import pl.mpak.util.ButtonTableHeader;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.Order;
import pl.mpak.util.SortableTable;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.TaskUtil;
import pl.mpak.util.task.Task;
import pl.mpak.util.task.TaskExecutor;
import pl.mpak.util.timer.Timer;

/**
 *
 * @author  akaluza
 */
public class VisualDataFilePanelView extends javax.swing.JPanel implements Closeable {
  
  private final StringManager stringManager = StringManagerFactory.getStringManager(OrbadaOracleDbaPlugin.class);

  private static Dimension[] displayBlockSizes = {
    new Dimension(3, 3),
    new Dimension(5, 5),
    new Dimension(8, 8),
    new Dimension(5, 9)
  };
  
  private IViewAccesibilities accesibilities;
  private boolean viewClosing = false;
  private volatile boolean cancelFlag;
  private volatile boolean colecting;
  private Timer timerRefresh;
  private Database database;
  private volatile boolean refreshRequest;
  private volatile boolean painting;
  private JPanel panelBlocks;
  
  private HashMap<String, TablespaceInfo> tablespaceMap;
  private HashMap<Long, DataFileInfo> dataFileMap;
  private HashMap<String, SegmentInfo> segmentMap;
  private HashMap<String, SegmentTypeInfo> segmentTypeMap;
  private HashMap<String, OwnerInfo> userMap;
  private HashMap<String, PartitionInfo> partitionMap;
  private HashMap<Long, ExtentInfo> extentMap;

  private HashMap<String, SegmentInfo> segmentTempMap;
  private HashMap<String, SegmentTypeInfo> segmentTypeTempMap;
  private HashMap<String, OwnerInfo> userTempMap;
  private HashMap<String, PartitionInfo> partitionTempMap;
  
  private ArrayList<DataFileInfo> dataFileList;
  private ArrayList<NameInfo> legendList;
  
  private long selectedFileId = -1;
  private NameInfo selectedInfo = null;
  private boolean selectedInfoSwitcher;
  private SearchOnTable searchOnTable;
  
  public VisualDataFilePanelView(IViewAccesibilities accesibilities) {
    this.accesibilities = accesibilities;
    cloneDatabase();
    this.tablespaceMap = new HashMap<String, TablespaceInfo>();
    this.dataFileMap = new HashMap<Long, DataFileInfo>();
    this.segmentMap = new HashMap<String, SegmentInfo>();
    this.segmentTypeMap = new HashMap<String, SegmentTypeInfo>();
    this.userMap = new HashMap<String, OwnerInfo>();
    this.partitionMap = new HashMap<String, PartitionInfo>();
    this.extentMap = new HashMap<Long, ExtentInfo>();
    this.dataFileList = new ArrayList<DataFileInfo>();
    this.legendList = new ArrayList<NameInfo>();
    initComponents();
    init();
  }

  private void init() {
    comboGroup.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
      stringManager.getString("segment-name"),
      stringManager.getString("segment-kind"),
      stringManager.getString("owner"),
      stringManager.getString("partition") }));

    timerRefresh = new Timer(1000) {
      public void run() {
        if (refreshRequest && !painting || selectedInfo != null) {
          selectedInfoSwitcher = !selectedInfoSwitcher;
          panelBlocks.repaint();
          refreshRequest = false;
        }
      }
    };
    OrbadaOraclePlugin.getRefreshQueue().add(timerRefresh);
    scrollBlocks.setViewportView(getPanelBlocks());
    scrollBlocks.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
      public void adjustmentValueChanged(AdjustmentEvent e) {
        refreshRequest = true;
      }
    });
    
    tableDataFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableDataFiles.setModel(getDataFileTableModel());
    tableDataFiles.addColumn(new TableColumn(0, 40, stringManager.getString("file-id"), new QueryTableCellRenderer()));
    tableDataFiles.addColumn(new TableColumn(1, 450, stringManager.getString("database-file"), new QueryTableCellRenderer(Font.BOLD)));
    tableDataFiles.addColumn(new TableColumn(2, 130, stringManager.getString("space"), new QueryTableCellRenderer()));
    tableDataFiles.addColumn(new TableColumn(3, 85, stringManager.getString("status"), new QueryTableCellRenderer()));
    tableDataFiles.addColumn(new TableColumn(4, 85, stringManager.getString("connected"), new QueryTableCellRenderer()));
    tableDataFiles.addColumn(new TableColumn(5, 60, stringManager.getString("block-size"), new QueryTableCellRenderer()));
    tableDataFiles.addColumn(new TableColumn(6, 85, stringManager.getString("blocks"), new QueryTableCellRenderer()));
    tableDataFiles.addColumn(new TableColumn(7, 85, stringManager.getString("bytes"), new QueryTableCellRenderer()));
    tableDataFiles.addColumn(new TableColumn(8, 85, stringManager.getString("free-blocks"), new QueryTableCellRenderer()));
    tableDataFiles.addColumn(new TableColumn(9, 85, stringManager.getString("free-bytes"), new QueryTableCellRenderer()));
    tableDataFiles.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (tableDataFiles.getSelectedRow() >= 0 && dataFileList.get(tableDataFiles.getSelectedRow()).getFileId() != selectedFileId) {
          resetAndCollect();
        }
      }
    });
    tableLegend.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableLegend.setModel(new LegendTableModel());
    tableLegend.addColumn(new TableColumn(0, 30, stringManager.getString("color"), new QueryTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof Color) {
          rendererLabel.setText("");
          rendererLabel.setBackground((Color)value);
        }
        return rendererLabel;
      }
    }));
    tableLegend.addColumn(new TableColumn(1, 150, stringManager.getString("name"), new QueryTableCellRenderer(Font.BOLD)));
    tableLegend.addColumn(new TableColumn(2, 90, stringManager.getString("blocks"), new QueryTableCellRenderer()));
    tableLegend.addColumn(new TableColumn(3, 90, stringManager.getString("bytes"), new QueryTableCellRenderer()));
    tableLegend.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (tableLegend.getSelectedRow() >= 0) {
          selectedInfo = legendList.get(tableLegend.getSelectedRow());
        }
        else {
          selectedInfo = null;
        }
        refreshRequest = true;
      }
    });
    tableLegend.setTableHeader(new ButtonTableHeader(tableLegend.getColumnModel()));
    searchOnTable = new SearchOnTable(tableLegend);
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        collectDataFileList();
      }
    });
    panelProgress.setVisible(false);
    new ComponentActionsAction(getDatabase(), tableDataFiles, buttonActions, menuActions, "oracle-visual-datafiles-actions");
  };
  
  private TableModel getDataFileTableModel() {
    return new AbstractTableModel() {
      public int getRowCount() {
        return dataFileList.size();
      }
      public int getColumnCount() {
        return 0;
      }
      public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
          case 0: return dataFileList.get(rowIndex).getFileId();
          case 1: return dataFileList.get(rowIndex).getName();
          case 2: return dataFileList.get(rowIndex).getTablespace();
          case 3: return dataFileList.get(rowIndex).getStatus();
          case 4: return dataFileList.get(rowIndex).getOnlineStatus();
          case 5: return dataFileList.get(rowIndex).getBlockSize();
          case 6: return dataFileList.get(rowIndex).getBlocks();
          case 7: return dataFileList.get(rowIndex).getBytes();
          case 8: return dataFileList.get(rowIndex).getFreeBlocks();
          case 9: return dataFileList.get(rowIndex).getFreeBytes();
          default: return null;
        }
      }
    };
  }

  private JPanel getPanelBlocks() {
    if (panelBlocks == null) {
      panelBlocks = new JPanel() {
        {
          setBackground(NameInfo.backgroundColor);
          setFocusable(true);
          addMouseMotionListener(new MouseMotionListener() {
            int lastX = -1;
            int lastY = -1;
            public void mouseDragged(MouseEvent e) {
            }
            public void mouseMoved(MouseEvent e) {
              Dimension dbs = getDisplayBlockSize();
              Rectangle rect = scrollBlocks.getViewport().getViewRect();
              int width = rect.width;
              int columns = width /(dbs.width +1);
              int x = Math.min(e.getX() /(dbs.width +1), columns);
              int y = e.getY() /(dbs.height +1);
              if (lastX != x || lastY != y) {
                ExtentInfo onInfo = null;
                synchronized (extentMap) {
                  int blockId = y *columns +x;
                  for (ExtentInfo info : extentMap.values()) {
                    if (blockId >= info.getBlockId() && blockId < info.getBlockId() +info.getBlocks()) {
                      onInfo = info;
                      break;
                    }
                  }
                  if (onInfo != null) {
                    SegmentInfo segInfo = segmentMap.get(onInfo.getSegmentName());
                    OwnerInfo ownInfo = userMap.get(onInfo.getOwnerName());
                    PartitionInfo partInfo = partitionMap.get(onInfo.getPartitionName());
                    setToolTipText(
                      String.format(
                        stringManager.getString("VisualDataFilePanelView-block-info"),
                        new Object[] {
                          x, y, y *columns +x, onInfo.getBlocks(),
                          onInfo.getSegmentName(), onInfo.getSegmentType(), (segInfo != null ? segInfo.getBlocks() : 0),
                          onInfo.getOwnerName(), (ownInfo != null ? ownInfo.getBlocks() : 0),
                          onInfo.getPartitionName(), (partInfo != null ? partInfo.getBlocks() : 0)}));
                  }
                  else {
                    setToolTipText(null);
                  }
                }
              }
            }
          });
        }
        @Override
        public void paintComponent(Graphics g) {
          painting = true;
          try {
            super.paintComponent(g);
            paintBlocks(g);
          }
          finally {
            painting = false;
          }
        }
      };
    }
    return panelBlocks;
  }
  
  private void cloneDatabase() {
    ConnectionFactory factory = new ConnectionFactory(getDatabase());
    try {
      database = factory.createDatabase();
    } catch (Exception ex) {
      MessageBox.show(this, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
    }
  }
  
  private void collectDataFileList() {
    synchronized (extentMap) {
      dataFileMap.clear();
      Query query = getDatabase().createQuery();
      try {
        query.open(Sql.getDataFileList());
        while (!query.eof()) {
          DataFileInfo info = new DataFileInfo(
            query.fieldByName("file_name").getString(), 
            query.fieldByName("tablespace_name").getString(), 
            query.fieldByName("file_id").getLong(), 
            query.fieldByName("blocks").getLong(), 
            query.fieldByName("bytes").getLong(), 
            query.fieldByName("block_size").getLong(),
            query.fieldByName("status").getString(),
            query.fieldByName("online_status").getString(),
            query.fieldByName("free_blocks").getLong(),
            query.fieldByName("free_bytes").getLong());
          dataFileMap.put(info.getFileId(), info);
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
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        int columnIndex = tableDataFiles.getSelectedColumn();
        synchronized (extentMap) {
          dataFileList.clear();
          int lastSelected = -1;
          int index = 0;
          for (DataFileInfo info : dataFileMap.values()) {
            if (selectedFileId == info.getFileId()) {
              lastSelected = index;
            }
            index++;
            dataFileList.add(info);
          }
          tableDataFiles.revalidate();
          if (lastSelected == -1 && dataFileList.size() > 0) {
            tableDataFiles.changeSelection(0, columnIndex, false, false);
          }
          else {
            tableDataFiles.changeSelection(lastSelected, columnIndex, false, false);
          }
        }
      }
    });
  }
  
  private void paintBlocks(Graphics g) {
    if (selectedFileId == -1) {
      return;
    }
    
    DataFileInfo fileInfo = dataFileMap.get(selectedFileId);
    Dimension dbs = getDisplayBlockSize();
    Rectangle rect = scrollBlocks.getViewport().getViewRect();
    Rectangle clip = g.getClipBounds();
    clip.grow(dbs.width, dbs.height);
    int width = rect.width;
    int columns = width /(dbs.width +1);
    int groupSelected = comboGroup.getSelectedIndex();
    synchronized (extentMap) {
      for (ExtentInfo info : extentMap.values()) {
        long y = (info.getBlockId() /columns) *(dbs.height +1);
        long yh = ((info.getBlockId() +info.getBlocks()) /columns) *(dbs.height +1) +(dbs.height +1);
        if (clip.y >= Math.min(y, clip.y) && clip.y +clip.height <= Math.max(yh, clip.y +clip.height)) {
          long x = (info.getBlockId() %columns) *(dbs.width +1);
          NameInfo currInfo = null;
          Color color = Color.BLACK;
          Color selectedColor = null;
          if ("FREE".equals(info.getSegmentType())) {
            color = Color.WHITE;
          }
          else if (groupSelected == 0) {
            currInfo = segmentMap.get(info.getSegmentName());
          }
          else if (groupSelected == 1) {
            currInfo = segmentTypeMap.get(info.getSegmentType());
          }
          else if (groupSelected == 2) {
            currInfo = userMap.get(info.getOwnerName());
          }
          else if (groupSelected == 3) {
            currInfo = partitionMap.get(info.getPartitionName());
          }
          if (currInfo != null) {
            if (currInfo.equals(selectedInfo)) {
              selectedColor = selectedInfo.getLightedColor();
            }
            color = currInfo.getColor();
          }
          if (selectedColor != null && selectedInfoSwitcher) {
            g.setColor(selectedColor);
          }
          else {
            g.setColor(color);
          }
          for (int ic=0; ic<info.getBlocks(); ic++) {
            if (x +dbs.width > width) {
              x = 0;
              y += (dbs.height +1);
            }
            if (clip.contains((int)x, (int)y, dbs.width, dbs.height)) {
              g.fillRect((int)x, (int)y, dbs.width, dbs.height);
            }
            x += (dbs.width +1);
          }
        }
        if (cancelFlag) {
          break;
        }
      }
    }
  }
  
  private void updateDisplayPanelSize() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        if (selectedFileId == -1) {
          return;
        }
        DataFileInfo fileInfo = dataFileMap.get(selectedFileId);
        Dimension dbs = getDisplayBlockSize();
        int width = scrollBlocks.getViewport().getViewRect().width;
        int columns = width /(dbs.width +1);
        if (fileInfo != null) {
          panelBlocks.setPreferredSize(new Dimension(width, (int)((Math.floor(fileInfo.getBlocks() /columns) +1) *(dbs.height +1))));
        }
        panelBlocks.revalidate();
        scrollBlocks.revalidate();
        scrollBlocks.getVerticalScrollBar().setUnitIncrement(dbs.height *10);
        refreshRequest = true;
      }
    });
  }

  private void refreshTask() {
    if (!SwingUtil.isVisible(this) || viewClosing || colecting) {
      return;
    }
    getDatabase().getTaskPool().addTask(new Task("Zbieranie informacji o pliku bazy danych...") {
      public void run() {
        refresh();
      }
    });
  }
  
  private void prepareInfo() {
    synchronized (extentMap) {
      Iterator<ExtentInfo> i = extentMap.values().iterator();
      while (i.hasNext()) {
        i.next().setToRemove();
      }
      segmentTempMap = segmentTempMap != null ? new HashMap<String, SegmentInfo>() : segmentMap;
      segmentTypeTempMap = segmentTypeTempMap != null ? new HashMap<String, SegmentTypeInfo>() : segmentTypeMap;
      userTempMap = userTempMap != null ? new HashMap<String, OwnerInfo>() : userMap;
      partitionTempMap = partitionTempMap != null ? new HashMap<String, PartitionInfo>() : partitionMap;

      tablespaceMap.clear();
      dataFileMap.clear();
      Query query = getDatabase().createQuery();
      try {
        query.open("select tablespace_name, block_size, status from dba_tablespaces");
        while (!query.eof()) {
          TablespaceInfo info = new TablespaceInfo(
            query.fieldByName("tablespace_name").getString(), 
            query.fieldByName("block_size").getLong(), 
            query.fieldByName("status").getString());
          tablespaceMap.put(info.getName(), info);
          query.next();
        }
        collectDataFileList();
      }
      catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
      finally {
        query.close();
      }
      updateDisplayPanelSize();
    }
  }
  
  private void collectInfo() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        labelProgress.setText("...");
        collectProgress.setValue(0);
        panelProgress.setVisible(true);
      }
    });
    Query query = getDatabase().createQuery();
    try {
      ArrayList<ExtentInfo> list = new ArrayList<ExtentInfo>();
      query.setSqlText("select tablespace_name, owner, segment_name, partition_name, segment_type, file_id, block_id, blocks from dba_extents where file_id = :FILE_ID");
      query.paramByName("FILE_ID").setLong(selectedFileId);
      query.open();
      final DataFileInfo dataFileInfo = dataFileMap.get(selectedFileId);
      long blocks = 0;
      int cnt = 0;
      while (!query.eof()) {
        ExtentInfo info = new ExtentInfo(
          query.fieldByName("tablespace_name").getString(),
          query.fieldByName("owner").getString(),
          query.fieldByName("segment_name").getString(),
          query.fieldByName("segment_type").getString(),
          query.fieldByName("partition_name").getValue().toString(),
          query.fieldByName("file_id").getLong(),
          query.fieldByName("block_id").getLong(),
          query.fieldByName("blocks").getLong());
        blocks += query.fieldByName("blocks").getLong();
        updateMaps(info, dataFileInfo);
        list.add(info);
        if (++cnt %200 == 0) {
          final int percentage = (int)((double)blocks /dataFileInfo.getBlocks() *100);
          updateExtentMap(list);
          list = new ArrayList<ExtentInfo>();
          if (Thread.currentThread() instanceof TaskExecutor) {
            Task task = ((TaskExecutor)Thread.currentThread()).getCurrentTask();
            if (task != null) {
              task.setPercenExecution(percentage);
              if (task.isCanceled()) {
                cancelFlag = true;
              }
            }
          }
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              collectProgress.setValue(percentage);
              labelProgress.setText(percentage +"%");
            }
          });
        }
        if (cancelFlag) {
          return;
        }
        query.next();
      }
      query.setSqlText("select tablespace_name, file_id, block_id, blocks from dba_free_space where file_id = :FILE_ID");
      query.paramByName("FILE_ID").setLong(selectedFileId);
      query.open();
      cnt = 0;
      while (!query.eof()) {
        ExtentInfo info = new ExtentInfo(
          query.fieldByName("tablespace_name").getString(),
          "", "", "FREE", "",
          query.fieldByName("file_id").getLong(),
          query.fieldByName("block_id").getLong(),
          query.fieldByName("blocks").getLong());
        blocks += query.fieldByName("blocks").getLong();
        list.add(info);
        if (++cnt %200 == 0) {
          final int percentage = (int)((double)blocks /dataFileInfo.getBlocks() *100);
          updateExtentMap(list);
          list = new ArrayList<ExtentInfo>();
          if (Thread.currentThread() instanceof TaskExecutor) {
            Task task = ((TaskExecutor)Thread.currentThread()).getCurrentTask();
            if (task != null) {
              task.setPercenExecution(percentage);
              if (task.isCanceled()) {
                cancelFlag = true;
              }
            }
          }
          final double blocksProgress = blocks;
          java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              collectProgress.setValue((int)(blocksProgress /dataFileInfo.getBlocks() *100));
              labelProgress.setText(percentage +"%");
            }
          });
        }
        if (cancelFlag) {
          return;
        }
        query.next();
      }
      if (list.size() > 0) {
        updateExtentMap(list);
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    finally {
      query.close();
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          panelProgress.setVisible(false);
        }
      });
    }
  }
  
  private void updateExtentMap(final ArrayList<ExtentInfo> list) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        synchronized (extentMap) {
          for (ExtentInfo info : list) {
            extentMap.put(info.getBlockId(), info);
          }
        }
        updateLegend();
        refreshRequest = true;
      }
    });
  }

  private void updateMaps(ExtentInfo info, DataFileInfo dfi) {
    synchronized (extentMap) {
      if (!"".equals(info.getSegmentName())) {
        SegmentInfo si = segmentTempMap.get(info.getSegmentName());
        if (si == null) {
          si = new SegmentInfo(info.getSegmentName());
          segmentTempMap.put(si.getName(), si);
          if (segmentMap.get(si.getName()) != null) {
            si.setColor(segmentMap.get(si.getName()).getColor());
          }
        }
        info.updateInfo(si, dfi.getBlockSize());
      }
      if (!"".equals(info.getSegmentType())) {
        SegmentTypeInfo sti = segmentTypeTempMap.get(info.getSegmentType());
        if (sti == null) {
          sti = new SegmentTypeInfo(info.getSegmentType());
          segmentTypeTempMap.put(sti.getName(), sti);
          if (segmentTypeMap.get(sti.getName()) != null) {
            sti.setColor(segmentTypeMap.get(sti.getName()).getColor());
          }
        }
        info.updateInfo(sti, dfi.getBlockSize());
      }
      if (!"".equals(info.getOwnerName())) {
        OwnerInfo oi = userTempMap.get(info.getOwnerName());
        if (oi == null) {
          oi = new OwnerInfo(info.getOwnerName());
          userTempMap.put(oi.getName(), oi);
          if (userMap.get(oi.getName()) != null) {
            oi.setColor(userMap.get(oi.getName()).getColor());
          }
        }
        info.updateInfo(oi, dfi.getBlockSize());
      }
      if (!"".equals(info.getPartitionName())) {
        PartitionInfo pi = partitionTempMap.get(info.getPartitionName());
        if (pi == null) {
          pi = new PartitionInfo(info.getPartitionName());
          partitionTempMap.put(pi.getName(), pi);
          if (partitionMap.get(pi.getName()) != null) {
            pi.setColor(partitionMap.get(pi.getName()).getColor());
          }
        }
        info.updateInfo(pi, dfi.getBlockSize());
      }
      if (!"".equals(info.getTablespaceName())) {
        TablespaceInfo pi = tablespaceMap.get(info.getTablespaceName());
        info.updateInfo(pi, dfi.getBlockSize());
      }
    }
  }
  
  private void doneInfo() {
    synchronized (extentMap) {
      Iterator<ExtentInfo> i = extentMap.values().iterator();
      while (i.hasNext()) {
        ExtentInfo info = i.next();
        if (info.isToRemove()) {
          i.remove();
        }
      }
      segmentMap = segmentTempMap;
      segmentTypeMap = segmentTypeTempMap;
      userMap = userTempMap;
      partitionMap = partitionTempMap;
      refreshRequest = true;
    }
  }
  
  private void refresh() {
    if (!SwingUtil.isVisible(this) || viewClosing || colecting) {
      return;
    }
    cancelFlag = false;
    setColecting(true);
    try {
      prepareInfo();
      collectInfo();
      doneInfo();
    }
    finally {
      setColecting(false);
    }
  }

  private void updateLegend() {
    int index = -1;
    synchronized (extentMap) {
      int i = 0;
      legendList.clear();
      if (comboGroup.getSelectedIndex() == 0) {
        for (NameInfo info : segmentMap.values()) {
          if (selectedInfo != null && info.getName().equals(selectedInfo.getName())) {
            index = i;
          }
          i++;
          legendList.add(info);
        }
      }
      else if (comboGroup.getSelectedIndex() == 1) {
        for (NameInfo info : segmentTypeMap.values()) {
          if (selectedInfo != null && info.getName().equals(selectedInfo.getName())) {
            index = i;
          }
          i++;
          legendList.add(info);
        }
      }
      else if (comboGroup.getSelectedIndex() == 2) {
        for (NameInfo info : userMap.values()) {
          if (selectedInfo != null && info.getName().equals(selectedInfo.getName())) {
            index = i;
          }
          i++;
          legendList.add(info);
        }
      }
      else if (comboGroup.getSelectedIndex() == 3) {
        for (NameInfo info : partitionMap.values()) {
          if (selectedInfo != null && info.getName().equals(selectedInfo.getName())) {
            index = i;
          }
          i++;
          legendList.add(info);
        }
      }
    }
    final int selectedIndex = index;
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        tableLegend.revalidate();
        tableLegend.repaint();
        tableLegend.changeSelection(selectedIndex, 1, false, false);
        ((LegendTableModel)tableLegend.getModel()).sortByColumn(1, Order.ASCENDING, 0);
      }
    });
  }
  
  public void setColecting(boolean colecting) {
    this.colecting = colecting;
    cmRefresh.setEnabled(!colecting);
    labelRefresh.setEnabled(colecting);
  }
  
  public Database getDatabase() {
    return database == null ? accesibilities.getDatabase() : database;
  }
  
  private Dimension getDisplayBlockSize() {
    return displayBlockSizes[comboBlockSize.getSelectedIndex()];
  }
  
  public void close() throws IOException {
    viewClosing = true;
    cancel();
    searchOnTable.done();
    timerRefresh.cancel();
    if (database != null) {
      database.close();
    }
    accesibilities = null;
  }
  
  private void cancel() {
    cancelFlag = true;
    while (colecting || painting) {
      TaskUtil.sleep(10);
    }
  }
  
  private void resetAndCollect() {
    cancel();
    synchronized (extentMap) {
      extentMap.clear();
      segmentTempMap = null;
      segmentTypeTempMap = null;
      userTempMap = null;
      partitionTempMap = null;
      segmentMap.clear();
      segmentTypeMap.clear();
      userMap.clear();
      partitionMap.clear();
    }
    updateLegend();
    if (tableDataFiles.getSelectedRow() >= 0) {
      selectedFileId = dataFileList.get(tableDataFiles.getSelectedRow()).getFileId();
    }
    refreshTask();
  }
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    cmRefresh = new pl.mpak.sky.gui.swing.Action();
    menuActions = new javax.swing.JPopupMenu();
    jPanel1 = new javax.swing.JPanel();
    jSplitPane2 = new javax.swing.JSplitPane();
    jScrollPane2 = new javax.swing.JScrollPane();
    tableDataFiles = new Table();
    jSplitPane1 = new javax.swing.JSplitPane();
    jPanel3 = new javax.swing.JPanel();
    jLabel4 = new javax.swing.JLabel();
    comboGroup = new pl.mpak.sky.gui.swing.comp.ComboBox();
    jLabel5 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tableLegend = new Table();
    jPanel4 = new javax.swing.JPanel();
    scrollBlocks = new javax.swing.JScrollPane();
    jPanel6 = new javax.swing.JPanel();
    jToolBar1 = new javax.swing.JToolBar();
    jPanel5 = new javax.swing.JPanel();
    jLabel3 = new javax.swing.JLabel();
    comboBlockSize = new pl.mpak.sky.gui.swing.comp.ComboBox();
    panelProgress = new javax.swing.JPanel();
    collectProgress = new javax.swing.JProgressBar();
    labelProgress = new javax.swing.JLabel();
    jPanel2 = new javax.swing.JPanel();
    toolBar = new javax.swing.JToolBar();
    buttonRefresh = new pl.mpak.sky.gui.swing.comp.ToolButton();
    labelRefresh = new javax.swing.JLabel();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    buttonActions = new pl.mpak.sky.gui.swing.comp.ToolButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();

    cmRefresh.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/refresh16.gif")); // NOI18N
    cmRefresh.setText(stringManager.getString("cmRefresh-text")); // NOI18N
    cmRefresh.setTooltip(stringManager.getString("cmRefresh-hint")); // NOI18N
    cmRefresh.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cmRefreshActionPerformed(evt);
      }
    });

    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(java.awt.event.ComponentEvent evt) {
        formComponentResized(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    jSplitPane2.setBorder(null);
    jSplitPane2.setDividerLocation(100);
    jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    jSplitPane2.setContinuousLayout(true);
    jSplitPane2.setOneTouchExpandable(true);

    tableDataFiles.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    jScrollPane2.setViewportView(tableDataFiles);

    jSplitPane2.setLeftComponent(jScrollPane2);

    jSplitPane1.setBorder(null);
    jSplitPane1.setDividerLocation(250);
    jSplitPane1.setContinuousLayout(true);
    jSplitPane1.setOneTouchExpandable(true);

    jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("Button.shadow")));
    jPanel3.setPreferredSize(new java.awt.Dimension(150, 0));

    jLabel4.setText(stringManager.getString("show-by-dd")); // NOI18N

    comboGroup.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        comboGroupItemStateChanged(evt);
      }
    });

    jLabel5.setText(stringManager.getString("legend-dd")); // NOI18N

    tableLegend.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    jScrollPane1.setViewportView(tableLegend);

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
            .addGap(9, 9, 9))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(comboGroup, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
            .addContainerGap())
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
              .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
            .addContainerGap())))
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel4)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(comboGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel5)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
        .addContainerGap())
    );

    jSplitPane1.setLeftComponent(jPanel3);

    jPanel4.setLayout(new java.awt.BorderLayout());
    jPanel4.add(scrollBlocks, java.awt.BorderLayout.CENTER);

    jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    jToolBar1.setFloatable(false);
    jToolBar1.setRollover(true);

    jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 1));

    jLabel3.setText(stringManager.getString("mark-size-dd")); // NOI18N
    jPanel5.add(jLabel3);

    comboBlockSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "3x3", "5x5", "8x8", "5x9" }));
    comboBlockSize.setSelectedIndex(1);
    comboBlockSize.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        comboBlockSizeItemStateChanged(evt);
      }
    });
    jPanel5.add(comboBlockSize);

    jToolBar1.add(jPanel5);

    panelProgress.add(collectProgress);

    labelProgress.setText("jLabel1");
    panelProgress.add(labelProgress);

    jToolBar1.add(panelProgress);

    jPanel6.add(jToolBar1);

    jPanel4.add(jPanel6, java.awt.BorderLayout.NORTH);

    jSplitPane1.setRightComponent(jPanel4);

    jSplitPane2.setRightComponent(jSplitPane1);

    jPanel1.add(jSplitPane2, java.awt.BorderLayout.CENTER);

    add(jPanel1, java.awt.BorderLayout.CENTER);

    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

    toolBar.setFloatable(false);
    toolBar.setRollover(true);

    buttonRefresh.setAction(cmRefresh);
    buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(buttonRefresh);

    labelRefresh.setIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/orange_bdot.gif")); // NOI18N
    labelRefresh.setEnabled(false);
    toolBar.add(labelRefresh);
    toolBar.add(jSeparator1);
    toolBar.add(buttonActions);
    toolBar.add(jSeparator2);

    jPanel2.add(toolBar);

    add(jPanel2, java.awt.BorderLayout.NORTH);
  }// </editor-fold>//GEN-END:initComponents
  
private void cmRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmRefreshActionPerformed
  refreshTask();
}//GEN-LAST:event_cmRefreshActionPerformed

private void comboBlockSizeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboBlockSizeItemStateChanged
  if (evt.getStateChange() == ItemEvent.SELECTED) {
    updateDisplayPanelSize();
  }
}//GEN-LAST:event_comboBlockSizeItemStateChanged

private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
  updateDisplayPanelSize();
}//GEN-LAST:event_formComponentResized

private void comboGroupItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboGroupItemStateChanged
  if (evt.getStateChange() == ItemEvent.SELECTED) {
    updateLegend();
    refreshRequest = true;
  }
}//GEN-LAST:event_comboGroupItemStateChanged

  private class LegendTableModel extends AbstractTableModel implements SortableTable {
    public int getRowCount() {
      return legendList.size();
    }
    public int getColumnCount() {
      return 0;
    }
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (rowIndex >= legendList.size()) {
        return null;
      }
      switch (columnIndex) {
        case 0: return legendList.get(rowIndex).getColor();
        case 1: return legendList.get(rowIndex).getName();
        case 2: return legendList.get(rowIndex).getBlocks();
        case 3: return legendList.get(rowIndex).getBytes();
        default: return null;
      }
    }

    public void sortByColumn(final int column, final Order order, int modifiers) {
      int columnIndex = tableLegend.getSelectedColumn();
      Collections.sort(legendList, new Comparator<NameInfo>() {
        public int compare(NameInfo o1, NameInfo o2) {
          switch (column) {
            case 0: return (order != Order.ASCENDING ? (int)Math.signum(o2.getColor().getRGB() -o1.getColor().getRGB()) : (int)Math.signum(o1.getColor().getRGB() -o2.getColor().getRGB()));
            case 1: return (order != Order.ASCENDING ? o2.getName().compareTo(o1.getName()) : o1.getName().compareTo(o2.getName()));
            case 2: return (order != Order.ASCENDING ? (int)Math.signum(o2.getBlocks() -o1.getBlocks()) : (int)Math.signum(o1.getBlocks() -o2.getBlocks()));
            case 3: return (order != Order.ASCENDING ? (int)Math.signum(o1.getBytes() -o1.getBytes()) : (int)Math.signum(o1.getBytes() -o2.getBytes()));
          }
          return 0;
        }
      });
      tableLegend.changeSelection(-1, columnIndex, false, false);
    }

  };

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonActions;
  private pl.mpak.sky.gui.swing.comp.ToolButton buttonRefresh;
  private pl.mpak.sky.gui.swing.Action cmRefresh;
  private javax.swing.JProgressBar collectProgress;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboBlockSize;
  private pl.mpak.sky.gui.swing.comp.ComboBox comboGroup;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JPanel jPanel6;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JSplitPane jSplitPane2;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JLabel labelProgress;
  private javax.swing.JLabel labelRefresh;
  private javax.swing.JPopupMenu menuActions;
  private javax.swing.JPanel panelProgress;
  private javax.swing.JScrollPane scrollBlocks;
  private Table tableDataFiles;
  private Table tableLegend;
  private javax.swing.JToolBar toolBar;
  // End of variables declaration//GEN-END:variables
  
}
