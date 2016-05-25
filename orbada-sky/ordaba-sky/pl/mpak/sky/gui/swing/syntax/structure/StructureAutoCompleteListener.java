package pl.mpak.sky.gui.swing.syntax.structure;

import java.util.ArrayList;
import java.util.Collections;

import pl.mpak.sky.SkySetting;
import pl.mpak.sky.gui.swing.AutoCompleteItem;
import pl.mpak.sky.gui.swing.AutoCompleteListener;
import pl.mpak.sky.gui.swing.ParametrizedAutoCompleteItem;
import pl.mpak.sky.gui.swing.syntax.SyntaxDocument;
import pl.mpak.sky.gui.swing.syntax.SyntaxEditor;

public class StructureAutoCompleteListener implements AutoCompleteListener {
  
  private SyntaxEditor editor;
  
  public StructureAutoCompleteListener(SyntaxEditor editor) {
    super();
    this.editor = editor;
  }

  public AutoCompleteItem[] populate(String[] words, boolean bracketMode, int commaCount) {
    if (words.length > 1 || !SkySetting.getBoolean(SkySetting.SyntaxEditor_StructureAutoComplete, true)) {
      return null;
    }
    boolean ignoreCase = ((editor.getDocument() instanceof SyntaxDocument) ?  ((SyntaxDocument)editor.getDocument()).isIgnoreCase() : true);
    String word = (words.length == 0 ? "" : (ignoreCase ? words[0].toUpperCase() : words[0]));
    BlockElement block = editor.getStructure(true);
    if (block == null) {
      return null;
    }
    CodeElement code = block.getElementAt(editor.getCaretPosition());
    if (code == null) {
      return null;
    }
    ArrayList<AutoCompleteItem> list = new ArrayList<AutoCompleteItem>();
    while (code != null) {
      if (code instanceof BlockElement) {
        block = (BlockElement)code;
        if (SkySetting.getBoolean(SkySetting.SyntaxEditor_StructureAutoCompleteVariables, true)) {
          if (code instanceof CallableElement) {
            for (DeclaredElement d : ((CallableElement)code).getParameterList()) {
              if (d.getName() != null) {
                String name = (ignoreCase ? d.getName().toUpperCase() : d.getName());
                if (name.startsWith(word) && !bracketMode) {
                  AutoCompleteItem item = new AutoCompleteItem(d.getName(), d.getCodeName(), "<html><b>" +d.getName() +"</b> (" +d.getCodeName() +")");
                  list.add(item);
                }
              }
            }
          }
          for (DeclaredElement d : block.getDeclaredList()) {
            if (d.getName() != null) {
              String name = (ignoreCase ? d.getName().toUpperCase() : d.getName());
              if (name.startsWith(word) && !bracketMode) {
                AutoCompleteItem item = new AutoCompleteItem(d.getName(), d.getCodeName(), "<html><b>" +d.getName() +"</b> (" +d.getCodeName() +")");
                list.add(item);
              }
            }
          }
        }
        for (CallableElement c : block.getCallableList()) {
          if (c.getName() != null) {
            String name = (ignoreCase ? c.getName().toUpperCase() : c.getName());
            ParametrizedAutoCompleteItem item = null;
            if (bracketMode) {
              if (name.equals(word)) {
                item = new ParametrizedAutoCompleteItem(c.getName(), c.getCodeName());
              }
            }
            else if (name.startsWith(word)) {
              item = new ParametrizedAutoCompleteItem(c.getName(), c.getCodeName());
            }
            if (item != null) {
              for (DeclaredElement d : c.getParameterList()) {
                item.add(d.getName(), d.getType());
              }
              if (bracketMode) {
                item.setCommaCount(commaCount);
              }
              list.add(item);
            }
          }
        }
      }
      code = code.getOwner();
    }
    Collections.sort(list);
    return list.toArray(new AutoCompleteItem[list.size()]);
  }

  public SyntaxEditor getEditor() {
    return editor;
  }

  public void setEditor(SyntaxEditor editor) {
    this.editor = editor;
  }

  
  
}
