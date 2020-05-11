package com.alibaba.markovdemo.engine.stages;

import com.alibaba.fastjson.JSONObject;

public final class Utils {

    public enum PrepareDataType {
        Tair,
        ScsIndex,
        Tdbm,
        KVfile,
        TransMsg,
        MetisData,
        Imock,
        Text,
        ArtisMsg,
        Mysql,
        TreeIndex,
        Conf,
        Igraph,
        IgraphDw,
        //全量索引
        Index,
        //直通车sn需要
        OrsMock,
        //online
        tt,
        Swift,
        IgraphOnline

    }


    public static String getValueOrEmptyString(JSONObject obj, String key){
        String value = obj.getString(key);
        return value == null? "":value;
    }

    public static boolean isEmptyStringOrNull(String str){
        return str == null || str.isEmpty();
    }

}
