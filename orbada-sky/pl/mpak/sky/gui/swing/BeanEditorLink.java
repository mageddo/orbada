package pl.mpak.sky.gui.swing;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.text.JTextComponent;

import pl.mpak.sky.SkyException;

public class BeanEditorLink implements FocusListener {

  private JTextComponent component;
  private Object linkObject;
  
  private Method getter;
  private Method setter;
  
  public BeanEditorLink(JTextComponent component, Object linkObject, String propertyName) throws SkyException {
    super();
    this.component = component;
    this.linkObject = linkObject;
    
    BeanInfo info;
    try {
      info = Introspector.getBeanInfo(linkObject.getClass(), Object.class);
    }
    catch (IntrospectionException e) {
      throw new SkyException(e);
    }
    PropertyDescriptor[] pd = info.getPropertyDescriptors();
    for (int i=0; i<pd.length; i++) {
      if (pd[i].getName().equals(propertyName)) {
        getter = pd[i].getReadMethod();
        setter = pd[i].getWriteMethod();
        break;
      }
    }
    
    if (getter == null && setter == null) {
      throw new SkyException("Can't find getter and/or setter for property.");
    }

    if (getter != null) {
      try {
        this.component.setText(getter.invoke(linkObject, (Object[])null).toString());
      }
      catch (IllegalArgumentException e) {
        throw new SkyException(e);
      }
      catch (IllegalAccessException e) {
        throw new SkyException(e);
      }
      catch (InvocationTargetException e) {
        throw new SkyException(e);
      }
    }
    
    this.component.addFocusListener(this);
  }

  public void focusGained(FocusEvent e) {
    
  }
  
  public void update() {
    if (setter != null) {
      try {
        final Class<?> parmType = setter.getParameterTypes()[0];
        if (parmType == boolean.class) {
          setter.invoke(linkObject, new Object[] {Boolean.valueOf(component.getText())});
        }
        else if (parmType == int.class) {
          setter.invoke(linkObject, new Object[] {Integer.valueOf(component.getText())});
        }
        else if (parmType == short.class) {
          setter.invoke(linkObject, new Object[] {Short.valueOf(component.getText())});
        }
        else if (parmType == long.class) {
          setter.invoke(linkObject, new Object[] {Long.valueOf(component.getText())});
        }
        else if (parmType == float.class) {
          setter.invoke(linkObject, new Object[] {Float.valueOf(component.getText())});
        }
        else if (parmType == double.class) {
          setter.invoke(linkObject, new Object[] {Double.valueOf(component.getText())});
        }
        else if (parmType == Color.class) {
          setter.invoke(linkObject, new Object[] {new Color(new Integer(component.getText()))});
        }
        else {
          setter.invoke(linkObject, new Object[] {component.getText()});
        }
      }
      catch (IllegalArgumentException e1) {
        ;
      }
      catch (IllegalAccessException e1) {
        ;
      }
      catch (InvocationTargetException e1) {
        ;
      }
    }
  }

  public void focusLost(FocusEvent e) {
    update();
  }

}
