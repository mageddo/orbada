package pl.mpak.sky.gui.swing.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.EventListenerList;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.TableRowChangeKeyListener;

public class SearchPanel extends JPanel {

  private static final long serialVersionUID = 6411737136101251495L;
  
  private final EventListenerList searchListenerList = new EventListenerList();
  private Action cmSearch;
  private TextField textSearch;
  private JTable table;
  private TableRowChangeKeyListener tableRowChange;
  
  public SearchPanel() {
    super();
    init();
  }

  public SearchPanel(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
    init();
  }

  private void init() {
    setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 3, 0));
    
    cmSearch = new Action();
    textSearch = new TextField();
    
    cmSearch.setActionCommandKey("cmSearch"); // NOI18N
    cmSearch.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fireSearch();
      }
    });
    cmSearch.setSmallIcon(pl.mpak.sky.gui.swing.ImageManager.getImage("/pl/mpak/res/icons/find_source.gif")); // NOI18N
    cmSearch.setShortCut(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
    cmSearch.setText(Messages.getString("search-cmSearch-text")); // NOI18N
    
    textSearch.setPreferredSize(new java.awt.Dimension(120, 20));
    SwingUtil.addAction(textSearch, cmSearch);
    
    add(new Label(Messages.getString("search-label-dd"), textSearch));
    add(textSearch);
    add(new ToolButton(cmSearch));
  }

  public JTable getTable() {
    return table;
  }

  public void setTable(JTable table) {
    if (tableRowChange != null) {
      textSearch.removeKeyListener(tableRowChange);
      tableRowChange = null;
    }
    this.table = table;
    if (this.table != null) {
      tableRowChange = new TableRowChangeKeyListener(this.table);
      textSearch.addKeyListener(tableRowChange);
    }
  }

  public void addSearchListener(SearchListener listener) {
    searchListenerList.add(SearchListener.class, listener);
  }

  public void removeSearchListener(SearchListener listener) {
    searchListenerList.remove(SearchListener.class, listener);
  }
  
  public String getText() {
    return textSearch.getText();
  }
  
  public TextField getTextSearch() {
    return textSearch;
  }

  public void fireSearch() {
    SearchEvent e = new SearchEvent(this);
    for (SearchListener listener : searchListenerList.getListeners(SearchListener.class)) {
      listener.search(e);
    }
  }

}
