package com.jx_linkcreate.productshow.manager;

import android.content.Context;

import com.randal.aviana.database.KeyValueTable;
import com.randal.aviana.database.SQLiteUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    public static final String APP_KEY = "027001";

    public static final String ADD_ITEM = "add_item";

    public static final String DATABASE_NAME = "jxlc.db";
    public static final String DATABASE_TABLE_FILTERS = "filters";
    public static final String DATABASE_TABLE_TITLES = "titles";

    public ArrayList<String> mTitles = new ArrayList<>();
    public HashMap<String, ArrayList<String>> mSubTitles = new HashMap<>();

    private Context mContext;
    private volatile static ConfigManager sMgr;

    private ConfigManager(Context context) {
        mContext = context;
    }

    public static ConfigManager getInstance(Context context) {
        if (sMgr == null) {
            synchronized (ConfigManager.class) {
                if (sMgr == null) {
                    sMgr = new ConfigManager(context);
                }
            }
        }
        return sMgr;
    }

    public void init() {
        syncVariable();

        // todo for debug
        mTitles.add("地区");
        ArrayList<String> area = new ArrayList<>();
        area.add("武汉");
        area.add("黄石");
        mSubTitles.put(mTitles.get(0), area);
    }

    private void syncVariable() {
        KeyValueTable table = SQLiteUtils.createOrOpenKeyValueTable(
                mContext, DATABASE_NAME, DATABASE_TABLE_FILTERS);
        String value = table.get(DATABASE_TABLE_TITLES);

        if (!value.isEmpty()) {
            mTitles = new ArrayList<>(Arrays.asList(value.split(";")));
        }

        if (mTitles.size() > 0) {
            for (String title : mTitles) {
                String subTitles = table.get(title);
                if (!subTitles.isEmpty()) {
                    ArrayList<String> array = new ArrayList<>(Arrays.asList(subTitles.split(";")));
                    mSubTitles.put(title, array);
                }
            }
        }
    }

    public void addTitle(String name) {
        addValue2Database(DATABASE_TABLE_TITLES, name);
        syncVariable();
    }

    public void removeTitle(String name) {
        removeValueFromDatabase(DATABASE_TABLE_TITLES, name);
        syncVariable();
    }

    public void addSubTitle(String parent, String name) {
        addValue2Database(parent, name);
        syncVariable();
    }

    public void removeSubTitle(String parent, String name) {
        removeValueFromDatabase(parent, name);
        syncVariable();
    }

    public ArrayList<String> getAllSubTitle() {
        ArrayList<String> retVal = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> entry : mSubTitles.entrySet()) {
            retVal.addAll(entry.getValue());
        }
        return retVal;
    }

    private void addValue2Database(String key, String value) {
        KeyValueTable table = SQLiteUtils.createOrOpenKeyValueTable(
                mContext, DATABASE_NAME, DATABASE_TABLE_FILTERS);

        String val = table.get(key);
        if (val.isEmpty()) {
            table.put(key, value);
        } else {
            val = val + ";" + value;
            table.put(key, val);
        }
    }

    private void removeValueFromDatabase(String key, String value) {
        KeyValueTable table = SQLiteUtils.createOrOpenKeyValueTable(
                mContext, DATABASE_NAME, DATABASE_TABLE_FILTERS);

        String val = table.get(key);
        if (!val.isEmpty()) {
            ArrayList<String> array = new ArrayList<>(Arrays.asList(val.split(";")));
            array.remove(value);
            table.put(key, join(array, ";"));
        }
    }

    public String join(List<String> list, String sp) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < list.size(); ++ i) {
            builder.append(list.get(i));
            builder.append(sp);
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
