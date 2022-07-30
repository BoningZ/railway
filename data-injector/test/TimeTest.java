package test;

import java.sql.Time;

public class TimeTest {
    public static void main(String[] args) {
        Time t=new Time(0);
        t.setMinutes(15);
        t.setHours(60);
        System.out.println(t);
    }
}
