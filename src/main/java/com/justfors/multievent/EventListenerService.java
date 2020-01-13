package com.justfors.multievent;

import com.justfors.multievent.annotation.Event;
import com.justfors.multievent.annotation.Listener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class EventListenerService {

    private static EventListenerService instance = null;
    private static ExecutorService executor = null;
    private static Map<Class, List<?>> eventListeners;
    private final Log logger = LogFactory.getLog(getClass());

    private EventListenerService() { }

    public static EventListenerService getEventListenerServiceInstance(){
        return getEventListenerServiceInstance(0);
    }

    public static EventListenerService getEventListenerServiceInstance(Integer threadQuantity){
        if (instance == null) {
            instance = new EventListenerService();
            int threadPoolSize = (threadQuantity != null && !threadQuantity.equals(0)) ? threadQuantity : 5;
            executor = Executors.newFixedThreadPool(threadPoolSize);
            eventListeners = new HashMap<>();
        }

        return instance;
    }

    public static EventListenerService getEventListenerServiceInstance(ExecutorService executorService){
        if (instance == null) {
            instance = new EventListenerService();
            executor = executorService;
            eventListeners = new HashMap<>();
        }

        return instance;
    }

    public <T> void broadCast(Class<? extends T> event, Consumer<T> action) {
        List<T> listeners = (List<T>) eventListeners.get(event);
        executor.execute(() -> runListeners(
                listeners,
                action
        ));
    }

    private <T> void runListeners(List<T> listeners, Consumer<T> action) {
        if (listeners != null) {
            for (T listener : listeners) {
                try {
                    action.accept(listener);
                } catch (Throwable e) {
                    logger.error(e);
                }
            }
        }
    }

    /*
    * Init without DI
    * */
    public void init(Class mainClass, List<Class> events) {
        Reflections reflections = new Reflections(mainClass.getPackage().getName());
        for (Class clazz : events) {
            if (!clazz.isAnnotationPresent(Event.class)) {
                logger.info(clazz.getName() + " hasn't annotated @Event");
                continue;
            }
            Set<Class<?>> loaderEventOne = reflections.getSubTypesOf(clazz);
            List<Object> listeners = new ArrayList<>();
            for (Class implClass : loaderEventOne) {
                if (implClass.isAnnotationPresent(Listener.class)) {
                    try {
                        listeners.add(implClass.getDeclaredConstructor().newInstance());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        logger.error("Class which implement " + clazz.getName() + " (" + implClass.getName() + ") should have no arg constructor");
                    }
                } else {
                    logger.info(implClass.getName() + " hasn't annotated @Listener");
                }
            }
            eventListeners.put(clazz, listeners);
        }
    }

    /*
    * Init with DI
    * */
    public void init(Map<Class, List<?>> listenersMap) {
        for (Map.Entry<Class, List<?>> entry : listenersMap.entrySet()) {
            Class eventClass = entry.getKey();
            boolean includeOnlyImplementations = true;
            for (Object implClass : entry.getValue()) {
                if (!eventClass.isAssignableFrom(implClass.getClass())) {
                    includeOnlyImplementations = false;
                    logger.error("List of implClasses must include only implementations of event interface, problem in key " + eventClass.getName());
                    break;
                }
            }
            if (includeOnlyImplementations) {
                eventListeners.put(eventClass, entry.getValue());
            }
        }
    }


}
