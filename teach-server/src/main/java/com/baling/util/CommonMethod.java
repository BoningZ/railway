package com.baling.util;

import com.baling.payload.response.DataResponse;
import com.baling.security.services.UserDetailsImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CommonMethod {
    public static DataResponse getReturnData(Object obj, String msg){
        Map data = new HashMap();
        data.put("data",obj);
        data.put("msg",msg);
        return new   DataResponse("0",data);
    }
    public static DataResponse getReturnMessage(String code, String msg){
        Map data = new HashMap();
        data.put("data",null);
        data.put("msg",msg);
        return new   DataResponse(code,data);
    }
    public static  DataResponse getReturnData(Object obj){
        return getReturnData(obj,null);
    }
    public static DataResponse getReturnMessageOK(String msg){
        return getReturnMessage("0", msg);
    }
    public static DataResponse getReturnMessageOK(){
        return getReturnMessage("0", null);
    }
    public static DataResponse getReturnMessageError(String msg){
        return getReturnMessage("1", msg);
    }

    public static Integer getUserId(){
        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userDetails != null)
            return userDetails.getId();
        else
            return null;
    }

    public static String getNextNum3( String num) {
        String str;
        String prefix;
        if(num.length() == 3) {
            str = num;
            prefix= "";
        }
        else {
            str = num.substring(num.length() - 3, num.length());
            prefix = num.substring(0,num.length() - 3);
        }
        int c;
        if(str.charAt(0)=='0') {
            if(str.charAt(1)=='0') {
                c = str.charAt(2)-'0';
            }else {
                c = (str.charAt(1)-'0')*10 + str.charAt(2)-'0';
            }
        }else {
            c = (str.charAt(0)-'0')*100  +(str.charAt(1)-'0')*10 + str.charAt(2)-'0';
        }
        c++;
        if(c < 10) {
            return prefix+"00" + c;
        }else if(c < 100) {
            return prefix+"0" + c;
        }else {
            return prefix+ c;
        }
    }
    public static String getNextNum4( String num) {
        String str;
        String prefix;
        if(num.length() == 4) {
            str = num;
            prefix= "";
        }
        else {
            str = num.substring(num.length() - 4, num.length());
            prefix = num.substring(0,num.length() - 4);
        }
        int c;
        if(str.charAt(0)=='0') {
            if (str.charAt(1) == '0') {
                if (str.charAt(2) == '0') {
                    c = str.charAt(3) - '0';
                } else {
                    c = (str.charAt(2) - '0') * 10 + str.charAt(3) - '0';
                }
            } else {
                c = (str.charAt(1) - '0') * 100 + (str.charAt(2) - '0') * 10 + str.charAt(3) - '0';
            }
        }else {
            c = (str.charAt(0) - '0') * 1000 + (str.charAt(1) - '0') * 100 + (str.charAt(2) - '0') * 10 + str.charAt(3) - '0';
        }
        c++;
        if(c < 10) {
            return prefix+"000" + c;
        }else if(c < 100) {
            return prefix+"00" + c;
        }else if(c < 1000){
            return prefix + "0" + c;
        }else {
            return prefix+ c;
        }
    }

    public static Map<String, Object> convertToMap(Object obj) {
        try {
            if (obj instanceof Map)return (Map)obj;
            Map<String, Object> returnMap = PropertyUtils.describe(obj);
            returnMap.remove("class");
            return returnMap;
        } catch (IllegalAccessException e1) {
            e1.getMessage();
        } catch (InvocationTargetException e2) {
            e2.getMessage();
        } catch (NoSuchMethodException e3) {
            e3.getMessage();
        }
        return new HashMap();
    }

    public static java.sql.Date addByDay(java.sql.Date origin,int delta){
        Calendar c= Calendar.getInstance();
        c.setTime(origin);
        c.add(Calendar.DATE,delta);
        return (Date) c.getTime();
    }

    public static String minToHourString(int min){
        return String.format("%02d:%02d",min/60,min%60);
    }

    public static int numOfOnes(int status){
        int res=0;
        while(status>0){res+=status%2;status/=2;}
        return res;
    }



}
