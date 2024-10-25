package io.github.evalexp;

/**
 * Swing UI Framework entrance
 */
public class Launcher {

    /**
     * launch the application
     * @param clazz entrance class, must extend JFrame
     */
    public static void launch(Class<?> clazz) {
        Context.loadDefault();
        Context.newFrame(clazz, "mainFrame");
    }

    /**
     * launch the application with specific language
     * @param clazz entrance class, must extend JFrame
     * @param locale target language
     */
    public static void launch(Class<?> clazz, String locale) {
        Context.setDefaultLanguage(locale);
        Launcher.launch(clazz);
    }
}
