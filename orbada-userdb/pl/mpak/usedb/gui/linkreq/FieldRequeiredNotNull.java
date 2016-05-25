package pl.mpak.usedb.gui.linkreq;

import pl.mpak.usedb.Messages;

/**
 * @author akaluza
 * <p>Klasa pozwala sprawdziæ czy pole nie jest czasem puste.
 */
public class FieldRequeiredNotNull extends FieldRequiredNamed {

  public FieldRequeiredNotNull(String publicFieldName) {
    super(publicFieldName);
  }

  public boolean accept(Object value) {
    return value != null && !"".equals(value); //$NON-NLS-1$
  }

  public String getMessage(Object value) {
    return String.format(Messages.getString("FieldRequeiredNotNull.field-cant-null"), new Object[] {publicFieldName}); //$NON-NLS-1$
  }

}
