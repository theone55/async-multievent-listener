package com.justfors.multievent.listeners;

import com.justfors.multievent.events.EventOne;
import com.justfors.multievent.annotation.Listener;

@Listener
public class Listener4 implements EventOne {
    @Override
    public void onEventOne(String args) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this.getClass().getName() + "  args "+ args);
    }

    @Override
    public void onEventqwe(String qwe, String qwqq) {

    }

}
