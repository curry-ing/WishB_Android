package com.vivavu.dream.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.Today;
import com.vivavu.dream.model.bucket.TodayGroup;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by yuja on 14. 3. 5.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "dream.db";
    private static final int DATABASE_VERSION = 11;

    // the DAO object we use to access the SimpleData table
    private Dao<Bucket, Integer> bucketDao = null;
    private RuntimeExceptionDao<Bucket, Integer> bucketRuntimeDao = null;

    // the DAO object we use to access the SimpleData table
    private Dao<Today, Integer> todayDao = null;
    private RuntimeExceptionDao<Today, Integer> todayRuntimeDao = null;

// the DAO object we use to access the SimpleData table
    private Dao<TodayGroup, Date> todayGroupDao = null;
    private RuntimeExceptionDao<TodayGroup, Date> todayGroupRuntimeDao = null;


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Bucket.class);
            TableUtils.createTable(connectionSource, Today.class);
            TableUtils.createTable(connectionSource, TodayGroup.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

        /*
        // here we try inserting data in the on-create as a test
        RuntimeExceptionDao<Comment, Integer> dao = getCommentsDataDao();
        // create some entries in the onCreate
        Comment comment = new Comment("First Test Comment");
        dao.create(comment);
        Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Bucket.class, true);
            TableUtils.dropTable(connectionSource, Today.class, true);
            TableUtils.dropTable(connectionSource, TodayGroup.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Bucket, Integer> getBucketDao() throws SQLException {
        if (bucketDao == null) {
            bucketDao = getDao(Bucket.class);
        }
        return bucketDao;
    }

    public RuntimeExceptionDao<Bucket, Integer> getBucketRuntimeDao() {
        if (bucketRuntimeDao == null) {
            bucketRuntimeDao = getRuntimeExceptionDao(Bucket.class);
        }
        return bucketRuntimeDao;
    }

    public Dao<Today, Integer> getTodayDao() throws SQLException {
        if(todayDao == null){
            todayDao = getDao(Today.class);
        }
        return todayDao;
    }

    public RuntimeExceptionDao<Today, Integer> getTodayRuntimeDao() {
        if (todayRuntimeDao == null) {
            todayRuntimeDao = getRuntimeExceptionDao(Today.class);
        }
        return todayRuntimeDao;
    }

    public Dao<TodayGroup, Date> getTodayGroupDao() throws SQLException {
        if(todayGroupDao == null){
            todayGroupDao = getDao(TodayGroup.class);
        }
        return todayGroupDao;
    }

    public RuntimeExceptionDao<TodayGroup, Date> getTodayGroupRuntimeDao() {
        if (todayGroupRuntimeDao == null) {
            todayGroupRuntimeDao = getRuntimeExceptionDao(TodayGroup.class);
        }
        return todayGroupRuntimeDao;
    }

    @Override
    public void close() {
        super.close();
        bucketDao = null;
        bucketRuntimeDao = null;
        todayDao = null;
        todayRuntimeDao= null;
        todayGroupDao = null;
        todayGroupRuntimeDao = null;
    }
}
