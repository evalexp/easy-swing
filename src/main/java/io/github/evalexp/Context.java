package io.github.evalexp;

import io.github.evalexp.annotations.Frame;
import io.github.evalexp.annotations.Initializer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Framework context, use static method to control the context
 */
public class Context {
    private static final List<String> i18nSearchMethods = new ArrayList<>();
    private static final Map<String, Container> containers = new HashMap<>();
    private static String language = "zh_CN";
    private static Locale locale;
    private static final List<String> languageFileSearchPaths = new ArrayList<>();

    /**
     * create a new frame to show
     * @param clazz target frame class
     * @param id frame id
     * @param args frame initializer args
     */
    public static void newFrame(Class<?> clazz, String id, Object ...args) {
        if (clazz.isAnnotationPresent(Frame.class)) {
            try {
                Object object = clazz.getConstructor().newInstance();
                if (!(object instanceof JFrame)) {
                    System.err.println("Frame must extend JFrame");
                }
                Container container = new Container(object, Context.i18nSearchMethods);
                Context.containers.put(id, container);
                container.autoWired();
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Initializer.class) && method.getParameterCount() == args.length) {
                        int i = 0;
                        for (; i < method.getParameterCount(); i++) {
                            if (!method.getParameterTypes()[i].isAssignableFrom(args[i].getClass())) break;
                        }
                        if (i == args.length)
                            method.invoke(object, args);
                    }
                }
                ((JFrame) object).setVisible(true);
                ((JFrame) object).addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        Context.destroyFrame(id);
                        super.windowClosed(e);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * set swing LAF
     * @param lookAndFeel laf
     * @return success
     */
    public static boolean setLookAndFeel(LookAndFeel lookAndFeel) {
        try {
            UIManager.setLookAndFeel(lookAndFeel);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * remove the frame, auto call when frame dispose
     * @param id frame id
     */
    public static void destroyFrame(String id) {
        Context.containers.get(id).clear();
        Context.containers.remove(id);
    }

    /**
     * update frame ui tree
     */
    private static void updateTreeUI() {
        for (Container container : Context.containers.values()) {
            try {
                container.render();
                SwingUtilities.updateComponentTreeUI((Component) container.getFrame());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * for compatible, you can add some text setter method, the method should only accept one parameter with String type
     * @param searchMethod method name
     */
    public static void addI18nSearchMethod(String searchMethod) {
        Context.i18nSearchMethods.add(searchMethod);
    }

    /**
     * for compatible, you can add some language search path, will search $locale.yml file
     * @param searchPath the path, base on classpath
     */
    public static void addLanguageFileSearchPath(String searchPath) {
        Context.languageFileSearchPaths.add(searchPath);
    }

    /**
     * get current locale
     * @return current locale object, for get i18n text
     */
    public static Locale getLocale() {
        return Context.locale;
    }

    /**
     * load default locale
     */
    public static void loadDefault() {
        Context.changeLocale(Context.language, true);
    }

    /**
     * set default locale
     * @param locale locale
     */
    public static void setDefaultLanguage(String locale) {
        Context.language = locale;
    }

    /**
     * change locale
     * @param locale target locale
     */
    public static void changeLocale(String locale) {
        Context.changeLocale(locale, false);
    }

    /**
     * change locale, if force is true, even load failed would set it to current locale
     * @param locale target locale
     * @param force force set current locale
     */
    private static void changeLocale(String locale, boolean force) {
        Locale l = new Locale(locale, Context.languageFileSearchPaths);
        if (force || l.isLoaded()){
            Context.locale = l;
            Context.updateTreeUI();
        }
    }

    static {
        addI18nSearchMethod("setText");
        addLanguageFileSearchPath("languages");
    }
}
