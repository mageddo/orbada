/*
 * Application.java
 *
 * Created on 18 styczeï¿½ 2007, 19:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


package pl.mpak.orbada.core;

import javax.swing.FocusManager;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import pl.mpak.g2.G2Util;
import pl.mpak.g2.RasterFont;
import pl.mpak.orbada.Consts;
import pl.mpak.orbada.ErrorMessages;
import pl.mpak.orbada.OrbadaCancelCloseException;
import pl.mpak.orbada.OrbadaException;
import pl.mpak.orbada.db.InternalDatabase;
import pl.mpak.orbada.db.Orbada;
import pl.mpak.orbada.db.OrbadaSession;
import pl.mpak.orbada.db.User;
import pl.mpak.orbada.gui.LoginDialog;
import pl.mpak.orbada.gui.LoginInfo;
import pl.mpak.orbada.gui.MainFrame;
import pl.mpak.orbada.gui.PerspectivePanel;
import pl.mpak.orbada.gui.cm.HelpAction;
import pl.mpak.orbada.plugins.*;
import pl.mpak.orbada.plugins.GeneratorException;
import pl.mpak.orbada.plugins.providers.ILookAndFeelStarter;
import pl.mpak.orbada.plugins.queue.PluginMessage;
import pl.mpak.orbada.util.Generator;
import pl.mpak.orbada.util.patt.*;
import pl.mpak.orbada.util.settings.SettingsFactory;
import pl.mpak.orbada.util.tools.ToolList;
import pl.mpak.orbada.webapp.WebAppAccessibilities;
import pl.mpak.plugins.spi.IPluginProvider;
import pl.mpak.sky.gui.mr.ModalResult;
import pl.mpak.sky.gui.swing.*;
import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Command;
import pl.mpak.usedb.core.Database;
import pl.mpak.usedb.core.Query;
import pl.mpak.util.*;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.id.VersionID;
import pl.mpak.util.patt.ResolvableModel;
import pl.mpak.util.patt.Resolvers;
import pl.mpak.util.variant.Variant;

/**
 *
 * @author akaluza
 */
public class Application implements IApplication, WindowListener {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private static Application application;

  private String[] ordabaArguments;
  private MainFrame mainFrame;
  private OrbadaPluginManager pluginManager;
  private Logger logger;
  private Properties properties;
  private ToolList toolList;
  public  Properties localProperties;
  private File localPropertiesFile;

  private boolean safeMode = false;
  private boolean multiUser;
  private VersionID lastVersion;

  private final ArrayList<IProcessMessagable> messagerList = new ArrayList<IProcessMessagable>();
  private HashMap<String, ISettings> settingsList;
  private HashMap<String, IGenerator> generatorList;
  private ISettings applicationSettings;
  private User user;
  private OrbadaSession orbadaSession;
  private WebAppAccessibilities webAppAccessibilities;

  private static SplashScreen splash = SplashScreen.getSplashScreen();
  private static Graphics2D splashGraphics = null;
  private static int splashSize = 10;
  private static int splashCounter = 0;
  private boolean firstRun;

  private static Image orbadaSplashLogo;
	private static final String fontChars = "A?BC?DE?FGHIJKL?MN?OÓPQRS?TUVWXYZ??0123456789!@#$%^&*()-=_+[]{};':\",./<>?|\\";
  private static BufferedImage fontsImage;
  private static RasterFont fonts;
  private static BufferedImage fontsSmallImage;
  private static RasterFont fontsSmall;
  private int exitCode = Consts.exitCode_Normal;

  public enum ApplicationEvent {
    ACTIVATED,
    CLOSING,
    DEACTIVATED,
    ICONIFIED,
    DEICONIFIED
  }
  private final EventListenerList applicationListenerList = new EventListenerList();

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    application = new Application(args);
    application.init();
  }

  private void init() {
    //ToolButton.SIZE = 32;
    initGraphics();
    renderSplashText(stringManager.getString("Application-initialization-3dot"));
    initActionStats();
    initResolvers();
    initLocalProperties();
    initProperties();
    initLogger();
    initLAF();
    initAWTWindowEvents();
    settingsList = new HashMap<String, ISettings>();
    generatorList = new HashMap<String, IGenerator>();
    webAppAccessibilities = new WebAppAccessibilities();
    initExceptionUtil();
    InternalDatabase.init();
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        InternalDatabase.beforeLogin();
        loginUser();
        InternalDatabase.afterLogin();
        toolList = new ToolList();
        initSession();
        renderSplashText(stringManager.getString("Application-init-tools-3dot"));
        java.awt.EventQueue.invokeLater(new Runnable() {
          @Override
          public void run() {
            initPluginManager();
            renderSplashText(stringManager.getString("Application-prepare-service-3dot"));
            java.awt.EventQueue.invokeLater(new Runnable() {
              @Override
              public void run() {
                mainFrame = new MainFrame();
                mainFrame.addWindowListener(Application.this);
                pluginManager.initializePlugins();
                mainFrame.initGlobalServices();
                mainFrame.setVisible(true);
                mainFrame.afterInit();
                closeSplashScreen();
              }
            });
          }
        });
      }
    });
  }

  /** Creates a new instance of Application
   * @param args
   */
  public Application(String[] args) {
    ordabaArguments = args;
  }

  private void initGraphics() {
    if (orbadaSplashLogo == null) {
      orbadaSplashLogo = ImageManager.getImage("/res/orbada-splash.png").getImage();
      orbadaSplashLogo = orbadaSplashLogo.getScaledInstance(46, 46, Image.SCALE_DEFAULT);
    }
    if (fontsImage == null) {
      fontsImage = G2Util.createImage(getClass().getResource("/res/fonts.png"));
      fonts = new RasterFont(fontsImage, fontChars, -3);
      fontsSmallImage = G2Util.createImage(getClass().getResource("/res/fonts-sm.png"));
      fontsSmall = new RasterFont(fontsSmallImage, fontChars, -2);
    }
  }

  private void initAWTWindowEvents() {
    Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
      HelpAction cmHelp = new HelpAction();
      @Override
      public void eventDispatched(AWTEvent event) {
        if (event.getID() == java.awt.event.WindowEvent.WINDOW_OPENED) {
          if (event.getSource() instanceof JDialog) {
            SwingUtil.addAction((JDialog)event.getSource(), cmHelp.getActionCommandKey(), cmHelp);
          }
        }
        else if (event.getID() == java.awt.event.WindowEvent.WINDOW_CLOSED) {
        }
      }
    }, AWTEvent.WINDOW_EVENT_MASK);

  }

  private void copyFile(String inputFile, File outputFile) {
    try {
      InputStream is = getClass().getResource(inputFile).openStream();
      OutputStream os = new FileOutputStream(outputFile);
      try {
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i = is.read(buf)) != -1) {
          os.write(buf, 0, i);
        }
      }
      finally {
        is.close();
        os.close();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
      System.exit(Consts.exitCode_Terminate);
    }
  }

  public String getConfigFile() {
    String configFile = "orbada";
    for (String arg : ordabaArguments) {
      if (StringUtil.equals(StringUtil.argName(arg), "config")) {
        configFile = StringUtil.argValue(arg);
        break;
      }
    }
    return getConfigPath() +"/" +configFile +".properties";
  }

  private void initLocalProperties() {
    localPropertiesFile = new File(getConfigPath() + "/local-settings.properties");
    localProperties = new Properties();
    if (localPropertiesFile.exists()) {
      try {
        localProperties.load(new FileInputStream(localPropertiesFile));
      } catch (IOException ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

  private void initProperties() {
    String configFile = getConfigFile();
    File file = new File(configFile);
    if (!file.exists()) {
      copyFile("/res/orbada.properties", file);
      File filejgoodies = new File(getConfigPath() +"/laf-jgoodies.properties");
      if (!filejgoodies.exists()) {
        copyFile("/res/laf-jgoodies.properties", filejgoodies);
      }
    }
    System.out.println("Using config file: " +file);
    properties = new Properties();
    try {
      properties.load(new FileInputStream(file));
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(Consts.exitCode_Terminate);
    }
    if (StringUtil.toBoolean(getProperty("sha1-password-aes"))) {
      Resolvers.register(new SHA1_Password_AESResolver());
    }
  }

  @Override
  public void updateLAF() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        initLAF();
        try {
          SwingUtilities.updateComponentTreeUI(getMainFrame());
          Component comp = FocusManager.getCurrentManager().getFocusOwner();
          Window current = (Window)SwingUtil.getOwnerComponent(Window.class, comp);
          if (getMainFrame() != current && current != null) {
            SwingUtilities.updateComponentTreeUI(current);
          }
        }
        catch (Throwable ex) {
          ExceptionUtil.processException(ex);
        }
      }
    });
  }

  private void initLAF() {
    try {
      if (SystemUtil.isMacOs()) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
      }
      String orbadaLafClassName = localProperties.getProperty(Consts.lookAndFeelLocalClass, getProperty(Consts.lookAndFeelLocalClass));
      if (!StringUtil.isEmpty(orbadaLafClassName)) {
        Logger.getLogger("orbada").info("Orbada LAF from \"" +Consts.lookAndFeelLocalClass +"\" property: " +orbadaLafClassName);
        Class lafClass = Class.forName(orbadaLafClassName);
        Object lafObject = lafClass.newInstance();
        if (lafObject instanceof ILookAndFeelStarter) {
          ((ILookAndFeelStarter)lafObject).setApplication(this);
          ((ILookAndFeelStarter)lafObject).start();
        }
        else {
          Logger.getLogger("orbada").error("Orbada LAF from \"" +Consts.lookAndFeelLocalClass +"\" is not implementation of ILookAndFeelStarter");
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
      }
      else {
        String className = localProperties.getProperty("laf.class", getProperty("laf.class"));
        if (className != null) {
          if (!"".equals(className.trim())) {
            UIManager.setLookAndFeel(className);
            Logger.getLogger("orbada").info("LAF from \"laf.class\" property: " +className);
          }
          else {
            Logger.getLogger("orbada").info("LAF from \"laf.class\" property: none");
          }
        } else {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
  //        UIManager.put("Table.selectionBackground", new Color(140, 165, 200));
          Logger.getLogger("orbada").info("LAF Orbada default: " +UIManager.getSystemLookAndFeelClassName());
        }
      }
    } catch (Throwable ex) {
      ExceptionUtil.processException(ex);
    }
  }

  private void initActionStats() {
    Action.addActionGlobalListener(new ActionGlobalListener() {
      @Override
      public void beforeAction(ActionGlobalEvent e) {
        //System.out.println("Before:" +new ActionInfo(e).toString());
      }
      @Override
      public void afterAction(ActionGlobalEvent e) {
        //System.out.println("After:" +new ActionInfo(e).toString());
      }
    });
  }

  private void initResolvers() {
    Resolvers.register(new CurrDateResolver());
    Resolvers.register(new CurrTimeResolver());
    Resolvers.register(new UniqueIdResolver());
    Resolvers.register(new OrbadaHomePath());
    Resolvers.register(new OrbadaConfigPath());
    Resolvers.register(new JvmPIDResolver());
  }

  public String getLogFile() {
    String log4jFile = "log4j.xml";
    for (String arg : ordabaArguments) {
      if (StringUtil.equals(StringUtil.argName(arg), "log4j")) {
        log4jFile = StringUtil.argValue(arg);
        break;
      }
    }
    return getConfigPath() +"/" +log4jFile;
  }

  private void initLogger() {
    String log4jFile = getLogFile();
    File file = new File(log4jFile);
    if (!file.exists()) {
      copyFile("/res/log4j.xml", file);
    }
    System.out.println("Using logger config file: " +file);
    logger = Logger.getLogger("orbada");
    DOMConfigurator.configure(log4jFile);
    logger.debug("Orbada starting...");
  }

  private void initPluginManager() {
    renderSplashText(stringManager.getString("Application-init-plugins-3dot"));
    pluginManager = new OrbadaPluginManager(logger);
    pluginManager.getPluginQueue().setExtendedMessagerList(messagerList);
    pluginManager.findPlugins();
    //splashSize = Math.min(splashSize, pluginManager.getFoundList().size() *2 +10);
    pluginManager.loadPlugins();
  }

  private void initExceptionUtil() {
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        ExceptionUtil.processException(e);
      }
    });
    ExceptionUtil.addProcessExceptionListener(new ProcessExceptionListener() {
      @Override
      public void processException(EventObject event) {
        if (event.getSource() instanceof OrbadaCancelCloseException) {
          return;
        }
        Logger.getLogger("error-logger").error("ExceptionUtil", (Throwable)event.getSource());
        ((Throwable)event.getSource()).printStackTrace();
      }
    });
  }

  private void loginUser() {
    Logger.getLogger("orbada").info("Login Orbada user...");
    renderSplashText(stringManager.getString("Application-login-user-3dot"));
    if (InternalDatabase.get() == null) {
      user = new User(InternalDatabase.get());
      return;
    }
    try {
      Query query = InternalDatabase.get().createQuery();
      try {
        query.setCloseResultAfterOpen(true);
        query.open("select count( 0 ) cnt from users where usr_orbada is null and usr_active = 'T'");
        if (query.fieldByName("cnt").getInteger() == 0) {
          user = new User(InternalDatabase.get());
          user.fieldByName("usr_name").setValue(new Variant(System.getProperty("user.name").toLowerCase()));
          user.fieldByName("usr_admin").setValue(new Variant("Y"));
          user.applyInsert();
        }
        else if (query.fieldByName("cnt").getInteger() == 1) {
          query.open("select usr_id, usr_name, usr_password from users where usr_orbada is null and usr_active = 'T'");
          if (!query.fieldByName("usr_password").isNull()) {
            int tryCount = 3;
            while (tryCount > 0) {
              tryCount--;
              LoginInfo info = LoginDialog.show(query.fieldByName("usr_name").getString(), "", stringManager.getString("Application-login-user"));
              if (info == null) {
                throw new OrbadaException(ErrorMessages.ORBADA_01002_LOGIN_CANCELED);
              }
              if (!query.fieldByName("usr_password").getString().equals(info.getPassword())) {
                if (tryCount == 0) {
                  throw new OrbadaException(ErrorMessages.ORBADA_01003_BAD_USER_PASSWD);
                }
              }
              else {
                break;
              }
            }
          }
          user = new User(InternalDatabase.get(), query.fieldByName("usr_id").getString());
        }
        else {
          multiUser = true;
          String userName = localProperties.getProperty("last-logged-user-name", "");
          int tryCount = 3;
          while (tryCount > 0) {
            tryCount--;
            LoginInfo info = LoginDialog.show(userName, "", stringManager.getString("Application-login-user"));
            if (info == null) {
              throw new OrbadaException(ErrorMessages.ORBADA_01002_LOGIN_CANCELED);
            }
            userName = info.getUserName();
            query.setSqlText("select usr_id, usr_password from users where usr_name = :usr_name");
            query.paramByName("usr_name").setString(userName.toLowerCase());
            query.open();
            if (!query.isEmpty() && !query.fieldByName("usr_password").getString().equals(info.getPassword())) {
              if (tryCount == 0) {
                throw new OrbadaException(ErrorMessages.ORBADA_01003_BAD_USER_PASSWD);
              }
            }
            else {
              break;
            }
          }
          user = new User(InternalDatabase.get(), query.fieldByName("usr_id").getString());
          localProperties.put("last-logged-user-name", userName);
        }
      }
      finally {
        query.close();
      }
      Resolvers.register(new OrbadaUserIdResolver());
      Resolvers.register(new OrbadaUserNameResolver());
      Logger.getLogger("orbada").info(String.format("Orbada user \"%s\" loged", new Object[] {user.getUserName()}));
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
      if (ex instanceof OrbadaException && !"ORBADA-01002".equals(((OrbadaException)ex).getCode())) {
        MessageBox.show(null, stringManager.getString("error"), ex.getMessage(), ModalResult.OK, MessageBox.ERROR);
      }
      System.exit(Consts.exitCode_Terminate);
    }
  }

  private void initSession() {
    if (getOrbadaDatabase() == null) {
      return;
    }
    orbadaSession = new OrbadaSession(getOrbadaDatabase());
    orbadaSession.setStartTime(new Date().getTime());
    orbadaSession.setUsrId(getUserId());
    orbadaSession.setVersion(Consts.orbadaVersion.toString());
    try {
      orbadaSession.applyInsert();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
    Resolvers.register(new ResolvableModel() {
      @Override
      public String getModel() {
        return "orbada.session.id";
      }
      @Override
      public String getResolve() {
        return orbadaSession.getId();
      }
    });
    if (!isMultiUserApp()) {
      long days = Long.parseLong(getProperty("session.delete-days", "10"));
      Command command = getOrbadaDatabase().createCommand();
      try {
        command.setSqlText("delete from orbada_sessions where oses_start_time < :data");
        command.paramByName("data").setTimestamp(new Timestamp(System.currentTimeMillis() -(days *24 *60 *60 *1000L)));
        command.execute();
        command.setSqlText("delete from schema_sessions where sses_start_time < :data");
        command.paramByName("data").setTimestamp(new Timestamp(System.currentTimeMillis() -(days *24 *60 *60 *1000L)));
        command.execute();
      }
      catch (Exception ex) {
        ExceptionUtil.processException(ex);
      }
    }
  }

  private void doneSession() {
    orbadaSession.setEndTime(new Date().getTime());
    try {
      orbadaSession.applyUpdate();
    } catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  public static void closeSplashScreen() {
    if (splash != null && splash.isVisible()) {
      splash.close();
      splashGraphics = null;
      splash = null;
    }
  }

  public static void drawSplashInfo(Graphics2D g2, Rectangle bounds) {
    int y = 10;
    g2.drawImage(orbadaSplashLogo, bounds.width -10 -orbadaSplashLogo.getWidth(null), 25, null);
    fonts.draw(15, y, g2, "ORBADA");
    y += (fonts.getHeight() -5);
    fontsSmall.draw(18, y, g2, Consts.orbadaSubname.toUpperCase());
    y += fontsSmall.getHeight(Consts.orbadaSubname.toUpperCase());
    fontsSmall.draw(18, y, g2, Consts.orbadaVersion.toString(VersionID.VersionString.vsLong).toUpperCase());
    y += (fontsSmall.getHeight() *2);
    fontsSmall.draw(18, y, g2, Consts.orbadaAutor.toUpperCase());
    y += fontsSmall.getHeight();
    fontsSmall.draw(18, y, g2, "(C) " +Consts.orbadaYears.toUpperCase());
  }

  public static void renderSplashText(final String text) {
    splashCounter++;
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (splash != null && splash.isVisible()) {
          if (splashGraphics == null) {
            splashGraphics = splash.createGraphics();
          }
          splashGraphics.setComposite(AlphaComposite.Clear);
          Rectangle b = splash.getBounds();
          splashGraphics.fillRect(Consts.splashscreenVerticalShift, b.height -Consts.splashscreenBottomShift -18, b.width -Consts.splashscreenVerticalShift *2, 20);
          splashGraphics.setPaintMode();
          splashGraphics.setColor(Color.BLACK);
          if (Application.get().isFirstRun()) {
            splashGraphics.drawString(Consts.orbadaFirstRun, Consts.splashscreenVerticalShift +2, b.height -Consts.splashscreenBottomShift -27);
          }
          splashGraphics.drawString(text, Consts.splashscreenVerticalShift +2, b.height -Consts.splashscreenBottomShift -7);
          splashGraphics.setColor(new Color(20, 53, 70));
          splashGraphics.drawRect(Consts.splashscreenVerticalShift -1, b.height -Consts.splashscreenBottomShift-2, b.width -Consts.splashscreenVerticalShift *2 +2, 6);
          splashGraphics.fillRect(Consts.splashscreenVerticalShift +1, b.height -Consts.splashscreenBottomShift -0, (int)((double)(b.width -(Consts.splashscreenVerticalShift +1) *2) *((double)splashCounter /(double)splashSize)), 3);

          final Composite oc = splashGraphics.getComposite();
          splashGraphics.setComposite(AlphaComposite.Src);
          drawSplashInfo(splashGraphics, b);
          splashGraphics.setComposite(oc);
          splash.update();
        }
      }
    });
  }

  @Override
  public void addApplicationListener(ApplicationListener listener) {
    synchronized (applicationListenerList) {
      applicationListenerList.add(ApplicationListener.class, listener);
    }
  }

  @Override
  public void removeApplicationListener(ApplicationListener listener) {
    synchronized (applicationListenerList) {
      applicationListenerList.remove(ApplicationListener.class, listener);
    }
  }

  public void fireApplicationListener(ApplicationEvent event) {
    synchronized (applicationListenerList) {
      ApplicationListener[] listeners = applicationListenerList.getListeners(ApplicationListener.class);
      for (int i=0; i<listeners.length; i++) {
        switch (event) {
        case ACTIVATED:
          listeners[i].activated();
          break;
        case DEACTIVATED:
          listeners[i].deactivated();
          break;
        case ICONIFIED:
          listeners[i].iconified();
          break;
        case DEICONIFIED:
          listeners[i].deiconified();
          break;
        case CLOSING:
          listeners[i].closing();
          break;
        }
      }
    }
  }

  public static Application get() {
    return application;
  }

  @Override
  public String[] getArguments() {
    return ordabaArguments;
  }

  public Properties getProperties() {
    return properties;
  }

  public ISettings getSettings() {
    if (applicationSettings == null) {
      applicationSettings = getSettings(Consts.orbadaSettings);
    }
    return applicationSettings;
  }

  @Override
  public void registerDriverType(String driverType) {
    InternalDatabase.registerDriverType(driverType);
  }

  public void addStatusBar(Component component) {
    mainFrame.addStatusBar(component);
  }

  public void removeStatusBar(Component component) {
    mainFrame.removeStatusBar(component);
  }

  @Override
  public void windowActivated(WindowEvent e) {
    fireApplicationListener(ApplicationEvent.ACTIVATED);
  }
  @Override
  public void windowClosed(WindowEvent e) {
    windowClosing(e);
  }
  @Override
  public void windowClosing(WindowEvent e) {
    logger.debug("Orbada closing...");
    fireApplicationListener(ApplicationEvent.CLOSING);
    pluginManager.unloadPlugins();
    InternalDatabase.done();
    logger.debug("Orbada closing: Okay");
    System.exit(exitCode);
  }
  @Override
  public void windowDeactivated(WindowEvent e) {
    fireApplicationListener(ApplicationEvent.DEACTIVATED);
  }
  @Override
  public void windowDeiconified(WindowEvent e) {
    fireApplicationListener(ApplicationEvent.DEICONIFIED);
  }
  @Override
  public void windowIconified(WindowEvent e) {
    fireApplicationListener(ApplicationEvent.ICONIFIED);
  }
  @Override
  public void windowOpened(WindowEvent e) {
  }

  public String getSettingsPath() {
    File file = new File(Resolvers.expand("$(orbada.home)"));
    file.mkdirs();
    return file.getAbsolutePath();
  }

  @Override
  public String getConfigPath() {
    File file = new File(Resolvers.expand("$(orbada.config)"));
    file.mkdirs();
    return file.getAbsolutePath();
  }

  /**
   * <p>Tworzy grupï¿½ ustawieï¿½ uï¿½ytkownika.
   * @param groupName
   * @return
   */
  @Override
  public ISettings getSettings(String groupName) {
    ISettings result = settingsList.get(groupName.toUpperCase());
    if (result == null) {
      result = SettingsFactory.createInstance(groupName);
      settingsList.put(groupName.toUpperCase(), result);
    }
    return result;
  }

  @Override
  public ISettings getSettings(String schemaId, String groupName) {
    ISettings result = settingsList.get(schemaId +":" +groupName.toUpperCase());
    if (result == null) {
      result = SettingsFactory.createInstance(schemaId, groupName);
      settingsList.put(schemaId +":" +groupName.toUpperCase(), result);
    }
    return result;
  }

  @Override
  public void addAction(Action action) {
    mainFrame.addAction(action);
  }

  @Override
  public void removeAction(Action action) {
    mainFrame.removeAction(action);
  }

  public MainFrame getMainFrame() {
    return mainFrame;
  }

  public OrbadaPluginManager getPluginManager() {
    return pluginManager;
  }

  @Override
  public <T extends IPluginProvider> T[] getServiceArray(Class<T> t) {
    return getPluginManager().getServiceArray(t);
  }

  @Override
  public void postPluginMessage(PluginMessage message) {
    try {
      pluginManager.getPluginQueue().post(message);
    } catch (InterruptedException ex) {
      ExceptionUtil.processException(ex);
    }
  }

  @Override
  public void registerRequestMessager(IProcessMessagable processMessagable) {
    synchronized (messagerList) {
      if (messagerList.indexOf(processMessagable) == -1) {
        messagerList.add(processMessagable);
      }
    }
  }

  @Override
  public void unregisterRequestMessager(IProcessMessagable processMessagable) {
    synchronized (messagerList) {
      messagerList.remove(processMessagable);
    }
  }

  @Override
  public String getProperty(String name) {
    return getProperty(name, null);
  }

  @Override
  public String getProperty(String name, String defaultValue) {
    if (Consts.orbadaUpdatePage.equals(name)) {
      return Consts.orbadaUpdateRootPageUrl;
    }
    else if (Consts.orbadaWebPage.equals(name)) {
      return Consts.orbadaWebRootPageUrl;
    }
    return properties.getProperty(name, defaultValue);
  }

  @Override
  public String getOrbadaString(String name) {
    return getOrbadaString(name, null);
  }

  public String getOrbadaString(String name, String defaultValue) {
    try {
      Orbada orbada = new Orbada(getOrbadaDatabase(), Application.get().getUserId(), name);
      if (orbada.getValue() == null) {
        return defaultValue;
      }
      return orbada.getValue();
    }
    catch (UseDBException ex) {
      return defaultValue;
    }
  }

  public void setOrbadaString(String name, String value) {
    try {
      Orbada orbada = new Orbada(getOrbadaDatabase(), Application.get().getUserId(), name);
      orbada.setValue(value);
      if (orbada.isExists()) {
        orbada.applyUpdate();
      }
      else {
        orbada.setId(new UniqueID().toString());
        orbada.setName(name);
        orbada.setUsrId(Application.get().getUserId());
        orbada.applyInsert();
      }
    }
    catch (Exception ex) {
      ExceptionUtil.processException(ex);
    }
  }

  public void shutDown() {
    Logger.getLogger("orbada").info("Orbada shutdown in progress...");
    getSettings().store();
    doneSession();
    try {
      localProperties.store(new FileOutputStream(localPropertiesFile), "Orbada Local Stored Settings");
    } catch (IOException ex) {
      ExceptionUtil.processException(ex);
    }
    Logger.getLogger("orbada").info("Orbada shutdown Ok");
  }

  @Override
  public void execTool(String command, Object[] args) {
    toolList.exec(command, args);
  }

  public ToolList getToolList() {
    return toolList;
  }

  @Override
  public String getUserId() {
    return user.getUserId();
  }

  @Override
  public String getUserName() {
    return user.getUserName();
  }

  @Override
  public boolean isUserAdmin() {
    return user.isUserAdmin();
  }

  @Override
  public boolean isMultiUserApp() {
    return multiUser;
  }

  public User getUser() {
    return user;
  }

  public boolean isSafeMode() {
    return safeMode;
  }

  public void setSafeMode(boolean safeMode) {
    this.safeMode = safeMode;
  }

  @Override
  public IGenerator initGenerator(String name, BigInteger startValue, BigInteger minValue, BigInteger maxValue, BigInteger increment, Boolean cycle) throws GeneratorException {
    IGenerator generator = getGenerator(name);
    if (generator == null) {
      generator = new Generator(name, startValue, minValue, maxValue, increment, cycle);
      generatorList.put(name, generator);
    }
    return generator;
  }

  @Override
  public IGenerator getGenerator(String name) {
    return generatorList.get(name);
  }

  @Override
  public Database getOrbadaDatabase() {
    return InternalDatabase.get();
  }

  @Override
  public IPerspectiveAccesibilities getActivePerspective() {
    PerspectivePanel panel = getMainFrame().getActivePerspective();
    if (panel != null) {
      return panel.getPerspectiveAccesibilities();
    }
    return null;
  }

  @Override
  public String getOrbadaSessionId() {
    return orbadaSession.getId();
  }

  @Override
  public IWebAppAccessibilities getWebAppAccessibilities() {
    return (IWebAppAccessibilities) webAppAccessibilities;
  }

  @Override
  public PleaseWait startPleaseWait(PleaseWait wait) {
    getMainFrame().getGlassPane().addPleaseWait(wait);
    return wait;
  }

  @Override
  public void stopPleaseWait(PleaseWait wait) {
    getMainFrame().getGlassPane().removePleaseWait(wait);
  }

  public void setFirstRun(boolean firstRun) {
    this.firstRun = firstRun;
  }

  @Override
  public boolean isFirstRun() {
    return firstRun;
  }

  public int getExitCode() {
    return exitCode;
  }

  public void setExitCode(int exitCode) {
    this.exitCode = exitCode;
  }

  public RasterFont getOrbadaFont() {
    return fonts;
  }

  public RasterFont getOrbadaSmallFont() {
    return fontsSmall;
  }

  @Override
  public VersionID getLastVersion() {
    return lastVersion;
  }

  public void setLastVersion(VersionID lastVersion) {
    this.lastVersion = lastVersion;
  }

}
