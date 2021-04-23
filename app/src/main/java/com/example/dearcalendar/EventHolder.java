package com.example.dearcalendar;

import android.graphics.Color;

public class EventHolder {
    private String color;
    private String title;
    private String start;
    private String end;

    public EventHolder()
    {

    }
    public EventHolder(String color, String title, String start, String end) {
        this.color = color;
        this.title = title;
        this.start = start;
        this.end = end;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
