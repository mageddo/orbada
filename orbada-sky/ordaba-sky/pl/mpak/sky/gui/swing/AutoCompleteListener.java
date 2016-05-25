package pl.mpak.sky.gui.swing;

import java.util.EventListener;

public interface AutoCompleteListener extends EventListener {

  public AutoCompleteItem[] populate(String[] words, boolean bracketMode, int commaCount);

}
