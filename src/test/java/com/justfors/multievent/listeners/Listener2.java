package com.justfors.multievent.listeners;

import com.justfors.multievent.events.EventTwo;
import com.justfors.multievent.annotation.Listener;

@Listener
public class Listener2 implements EventTwo {
    @Override
    public void onEventTwo(String args) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this.getClass().getName() + "  args "+ args);
    }
}
