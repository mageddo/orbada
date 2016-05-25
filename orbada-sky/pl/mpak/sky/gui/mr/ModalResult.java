package pl.mpak.sky.gui.mr;

/**
 * ModalResult jest u¿ywany wszêdzie tam gdzie spodziewany jest rezultat 
 * zamkniêcia okna, panelu, realizacji zadania
 * 
 * ModalResult u¿yty jest w SkyBasePanel i SkyBaseDialog
 * w komponentach tych jest równiez listener do którego automatycznie
 * dodawane s¹ te komponenty przez co mo¿na obs³u¿yæ funkcjê 
 * modaleResultChange(ModalResultEvent e)
 * 
 * Ustawiaj¹c ModalResult w powy¿szych komponentach przy pomocy setModalResult
 * wywo³ywana jest powy¿sza funkcja.
 * 
 * Komponent SkySimpleDialog, który s³u¿y do pokazania panelu w oknie modalnym
 * ModalResult obs³ugiwany jest automatycznie i po ustawieniu setModalResult na != NONE
 * okno automatycznie jest zamykane
 * 
 * @author Andrzej Ka³u¿a
 */
public class ModalResult {
  public final static int NONE    = 0;
  public final static int OK      = 1;
  public final static int CANCEL  = 2;
  public final static int IGNORE  = 3;
  public final static int RETRY   = 4;
  public final static int YES     = 5;
  public final static int NO      = 6;
  
  public final static int[] OKCANCEL = new int[] {OK, CANCEL};
  public final static int[] YESNO = new int[] {YES, NO};
  public final static int[] YESNOCANCEL = new int[] {YES, NO, CANCEL};
  public final static int[] IGNORERETRYCANCEL = new int[] {IGNORE, RETRY, CANCEL};
}
