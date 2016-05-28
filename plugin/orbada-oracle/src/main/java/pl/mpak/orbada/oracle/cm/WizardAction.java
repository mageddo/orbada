package pl.mpak.orbada.oracle.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardDialog;
import pl.mpak.orbada.universal.gui.wizards.SqlCodeWizardPanel;
import pl.mpak.sky.gui.swing.Action;

/**
 *
 * @author akaluza
 */
public class WizardAction extends Action {
  
  private SqlCodeWizardPanel wizardPanel;
  
  public WizardAction(SqlCodeWizardPanel wizardPanel) {
    super(wizardPanel.getDialogTitle());
    setActionCommandKey("WizardAction");
    addActionListener(createActionListener());
    this.wizardPanel = wizardPanel;
  }

  public WizardAction(SqlCodeWizardPanel wizardPanel, Icon icon) {
    this(wizardPanel);
    setSmallIcon(icon);
  }

  private ActionListener createActionListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SqlCodeWizardDialog.show(wizardPanel, true);
      }
    };
  }
  
}
