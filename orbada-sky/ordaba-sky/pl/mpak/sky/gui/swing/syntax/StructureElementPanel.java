package pl.mpak.sky.gui.swing.syntax;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Closeable;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JMenu;

import pl.mpak.sky.Messages;
import pl.mpak.sky.gui.swing.Action;
import pl.mpak.sky.gui.swing.SwingUtil;
import pl.mpak.sky.gui.swing.comp.PopupMenu;
import pl.mpak.sky.gui.swing.comp.StatusPanel;
import pl.mpak.sky.gui.swing.syntax.structure.BlockElement;
import pl.mpak.sky.gui.swing.syntax.structure.CallableElement;
import pl.mpak.sky.gui.swing.syntax.structure.CaseBlock;
import pl.mpak.sky.gui.swing.syntax.structure.CaseWhen;
import pl.mpak.sky.gui.swing.syntax.structure.CatchException;
import pl.mpak.sky.gui.swing.syntax.structure.CodeElement;
import pl.mpak.sky.gui.swing.syntax.structure.DeclaredElement;
import pl.mpak.sky.gui.swing.syntax.structure.ExceptionBlock;

public class StructureElementPanel extends StatusPanel implements Closeable {
  private static final long serialVersionUID = 1L;

  private CodeElement codeElement;
  private PopupMenu popupMenu;
  private SyntaxEditor editor;

  public StructureElementPanel(SyntaxEditor editor, CodeElement codeElement, Icon icon) {
    super(null, " " +codeElement.getDisplayCodeName() +" ", icon); //$NON-NLS-1$ //$NON-NLS-2$
    this.codeElement = codeElement;
    this.editor = editor;
    init();
  }

  public StructureElementPanel(SyntaxEditor editor, CodeElement codeElement) {
    this(editor, codeElement, null);
  }
  
  private void init() {
    setOpaque(true);
    setDisplayActivation(true);
    setToolTipText(StructureUtil.getTooltip(codeElement, editor));
    addMouseListener(new MouseListener() {
      Color oldColor;
      public void mouseClicked(MouseEvent e) {
        popup();
      }
      public void mouseEntered(MouseEvent e) {
        oldColor = getBackground();
        setBackground(SwingUtil.addColor(oldColor, 20, 20, 20));
      }
      public void mouseExited(MouseEvent e) {
        setBackground(oldColor);
      }
      public void mousePressed(MouseEvent e) {
      }
      public void mouseReleased(MouseEvent e) {
      }
    });
  }
  
  private void popup() {
    if (popupMenu == null) {
      popupMenu = new PopupMenu(this);
      popupMenu.add(new GotoCodeAction(codeElement, editor));
      if (codeElement instanceof BlockElement) {
        BlockElement block = (BlockElement)codeElement;
        if (((BlockElement)codeElement).getBeginBlockOffset() > codeElement.getStartOffset()) {
          popupMenu.add(new GotoCodeAction(codeElement, editor, ((BlockElement)codeElement).getBeginBlockOffset(), "Begin")); //$NON-NLS-1$
        }
        if (block.getCallableList().size() > 0 || block.getDeclaredList().size() > 0 || block.getExceptionBlock() != null) {
          popupMenu.addSeparator();
        }
        int cnt = 0;
        for (CallableElement e : block.getCallableList()) {
          if (++cnt > 25) {
            popupMenu.add(editor.structureElementSelect);
            break;
          }
          popupMenu.add(new GotoCodeAction(e, editor));
        }
        if (block.getDeclaredList().size() > 0) {
          JMenu declared = new JMenu(Messages.getString("StructureElementPanel.declare")); //$NON-NLS-1$
          popupMenu.add(declared);
          cnt = 0;
          for (DeclaredElement e : block.getDeclaredList()) {
            declared.add(new GotoCodeAction(e, editor));
            if (++cnt > 25) {
              declared.add(editor.structureElementSelect);
              break;
            }
          }
        }
        if (block.getExceptionBlock() != null) {
          JMenu except = new JMenu(Messages.getString("StructureElementPanel.exceptions")); //$NON-NLS-1$
          popupMenu.add(except);
          for (CatchException e : block.getExceptionBlock().getCatchList()) {
            except.add(new GotoCodeAction(e, editor));
          }
        }
      }
      else if (codeElement instanceof ExceptionBlock) {
        ExceptionBlock block = (ExceptionBlock)codeElement;
        if (block.getCatchList().size() > 0) {
          popupMenu.addSeparator();
          for (CatchException e : block.getCatchList()) {
            popupMenu.add(new GotoCodeAction(e, editor));
          }
        }
      }
      else if (codeElement instanceof CaseBlock) {
        CaseBlock block = (CaseBlock)codeElement;
        if (block.getWhenList().size() > 0 || block.getElseBlock() != null) {
          popupMenu.addSeparator();
        }
        if (block.getWhenList().size() > 0) {
          for (CaseWhen e : block.getWhenList()) {
            popupMenu.add(new GotoCodeAction(e, editor));
          }
        }
        if (block.getElseBlock() != null) {
          popupMenu.add(new GotoCodeAction(block.getElseBlock(), editor));
        }
      }
    }
    if (popupMenu != null) {
      popupMenu.show(this, 0, getHeight());
    }
  }

  public CodeElement getCodeElement() {
    return codeElement;
  }
  
  class GotoCodeAction extends Action {
    
    private CodeElement item;
    private SyntaxEditor editor;
    private int offset;

    public GotoCodeAction(CodeElement item, SyntaxEditor editor) {
      this(item, editor, item.getStartOffset());
    }
    
    public GotoCodeAction(CodeElement item, SyntaxEditor editor, int offset) {
      super();
      this.item = item;
      this.offset = offset;
      this.editor = editor;
//      setText(StructureUtil.getText(item));
      setText(StructureUtil.getTooltip(item, editor));
      //setTooltip(StructureUtil.getTooltip(item, editor));
    }
    
    public GotoCodeAction(CodeElement item, SyntaxEditor editor, int offset, String postText) {
      this(item, editor, offset);
      setText(getText() +" " +postText); //$NON-NLS-1$
    }
    
    public void actionPerformed(ActionEvent e) {
      editor.setCaretPosition(offset, true);
    }
    
  }

  public void close() throws IOException {
    codeElement = null;
    editor = null;
    if (popupMenu != null) {
      popupMenu.removeAll();
    }
  }

}
