package com.justfors.multievent.listeners;

import com.justfors.multievent.events.EventTwo;
import com.justfors.multievent.annotation.Listener;

import java.util.Arrays;


public class Listener1 implements EventTwo {

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
