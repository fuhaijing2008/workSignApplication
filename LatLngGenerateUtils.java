package com.example.esc.worksigninapplication;

/**
 * Created by esc on 2016/12/13 .
 */
public class LatLngGenerateUtils {

    public static String generateRandomLat(){

        String lat = "39.332";
        return lat+ generateRandomElevenDates();
    }
    public static String generateRandomLng(){
        String lng = "116.977";
        return lng+ generateRandomElevenDates();
    }
    public static String generateRandomElevenDates(){

        String num = "";
        for(int i=0;i<11;i++)
            num = num+ generateRandomNum();
        return num;
    }
    public static String generateRandomNum(){
        int random=(int)(Math.random()*10);
        return random+"";
    }
}
