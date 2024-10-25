package io.github.evalexp;

import io.github.evalexp.annotations.I18N;
import io.github.evalexp.util.ReflectUtil;

import java.awt.*;
import java.lang.reflect.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * UI Container, frame component storage
 */
public class Container {
    // JFrame object
    private Object frame;
    // components container
    private Map<Object, String> components = new LinkedHashMap<>();
    // i18n change method
    private List<String> i18nSearchMethods;

    /**
     * create the ui container
     * @param frame container's frame
     * @param i18nSearchMethods i18n text search method, for component text setter
     */
    public Container(Object frame, List<String> i18nSearchMethods) {
        this.frame = frame;
        this.i18nSearchMethods = i18nSearchMethods;
    }

    /**
     * clear this container
     */
    public void clear() {
        components.clear();
        frame = null;
    }

    /**
     * to auto wire the components
     * @param field target field
     * @param keepOrigin if keep origin value
     * @param value not construct, inject this value
     * @return the component
     */
    private Object injectObject(Field field, boolean keepOrigin, Object value) {
        if (keepOrigin) {
            try {
                Method getter = ReflectUtil.searchGetterByField(frame, field);
                if (getter != null) {
                    getter.setAccessible(true);
                    value = getter.invoke(frame);
                } else {
                    field.setAccessible(true);
                    value = field.get(frame);
                }
            } catch (Exception e) {}
        }
        if (value == null) {
            try {
                value = field.getType().getConstructor().newInstance();
            } catch (Exception e) {
                return null;
            }
        }
        try {
            Method setter = ReflectUtil.searchSetterByField(frame, field);
            if (setter != null) {
                setter.invoke(frame, value);
            } else {
                field.setAccessible(true);
                field.set(frame, value);
            }
        } catch (Exception e) {
            return null;
        }
        return value;
    }

    private Object injectObject(Field field) {
        return injectObject(field, true, null);
    }

    /**
     * auto wire i18n components and container
     */
    public void autoWired() {
        for (Field field : ReflectUtil.searchFieldByAnnotation(frame, I18N.class)) {
            Object value = injectObject(field);
            if (value != null) components.put(value, field.getAnnotation(I18N.class).key());
        }

        for (Field field : ReflectUtil.searchFieldByAnnotation(frame, io.github.evalexp.annotations.Container.class))
            injectObject(field, false, this);

        this.render();
    }

    /**
     * render all i18n components
     */
    public void render() {
        for (Map.Entry<Object, String> entry : components.entrySet()) {
            this.tryRenderComponent(entry.getKey(), entry.getValue());
        }
    }

    /**
     * try render the component with i18n text
     * @param component target component
     * @param i18nKey i18n text key, search in language file
     */
    private void tryRenderComponent(Object component, String i18nKey) {
        Method i18nCompatibleMethod = ReflectUtil.searchI18NCompatibleMethod(component, this.i18nSearchMethods);
        if (i18nCompatibleMethod != null) {
            try {
                i18nCompatibleMethod.invoke(component, Context.getLocale().text(i18nKey));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get the container's frame
     * @return JFrame object
     */
    public Object getFrame() {
        return this.frame;
    }

    /**
     * create a new component with i18n feature
     * @param componentClass component class
     * @param i18nKey text key
     * @param args component construct args
     * @return the new component
     * @param <T> type of the component
     */
    public <T extends Component> T newComponent(Class<T> componentClass, String i18nKey, Object... args) {
        try {
            return (T) newCompatibleComponent(componentClass, i18nKey, args);
        } catch (Exception e){
            return null;
        }
    }

    /**
     * create a compatible component even if target class is not a swing component
     * @param componentClass component class
     * @param i18nKey text key
     * @param args component construct args
     * @return the new component
     */
    public <T> T newCompatibleComponent(Class<T> componentClass, String i18nKey, Object... args) {
        Constructor<?> constructor = ReflectUtil.getConstructor(componentClass, args);
        try {
            T component = (T) constructor.newInstance(args);
            components.put(component, i18nKey);
            this.tryRenderComponent(component, i18nKey);
            return component;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * release the component so that would not accept i18n text change
     * @param component target component
     * @param <T> type of the component
     */
    public <T extends Component> void releaseComponent(T component) {
        this.components.remove(component);
    }

    /**
     * pack the component with i18n feature
     * @param component target component
     * @param i18nKey text key
     * @return if success
     * @param <T> type of the component
     */
    public <T extends Component> boolean packComponent(T component, String i18nKey) {
        if (component == null || Context.getLocale().text(i18nKey) == null) return false;
        components.put(component, i18nKey);
        this.tryRenderComponent(component, i18nKey);
        return true;
    }
}
