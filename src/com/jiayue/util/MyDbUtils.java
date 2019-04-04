package com.jiayue.util;

import android.content.Context;
import android.util.Log;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.x;

public class MyDbUtils {

    public static DbManager getBookVoDb(Context context) {
        final DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                //设置数据库名，默认xutils.db
                .setDbName("book_vo.db")
//        //设置表创建的监听
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {
                        Log.i("JAVA", "onTableCreated：" + table.getName());
                    }
                })
                //设置是否允许事务，默认true
                .setAllowTransaction(true)
                //设置数据库路径，默认安装程序路径下
//        .setDbDir(ActivityUtils.getSDPath())
                //设置数据库的版本号
                .setDbVersion(4)
                //设置数据库更新的监听
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion,
                                          int newVersion) {
                        // 为数据库表BookVO添加isPlay字段
                        try {
                            switch (oldVersion) {
                                case 1:
                                    db.getDatabase().execSQL("alter table BookVO add isPlay int");
                                    db.getDatabase().execSQL("alter table BookVO add bookImg char(50)");
                                    db.getDatabase().execSQL("alter table BookVO add bookImgPath char(50)");
                                    db.getDatabase().execSQL("alter table BookVO add isImage int");
                                    break;
                                case 2:
                                    db.getDatabase().execSQL("alter table BookVO add bookImg char(50)");
                                    db.getDatabase().execSQL("alter table BookVO add bookImgPath char(50)");
                                    db.getDatabase().execSQL("alter table BookVO add isImage int");
                                    break;
                                case 3:
                                    db.getDatabase().execSQL("alter table BookVO add isImage int");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                //设置数据库打开的监听
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        //开启数据库支持多线程操作，提升性能
                        db.getDatabase().enableWriteAheadLogging();
                    }
                });
        DbManager db = x.getDb(daoConfig);

        return db;
    }

    public static DbManager getSiftVoDb(Context context) {

        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                //设置数据库名，默认xutils.db
                .setDbName("sift_vo.db")
//        //设置表创建的监听
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {
                        Log.i("JAVA", "onTableCreated：" + table.getName());
                    }
                })
                //设置是否允许事务，默认true
                .setAllowTransaction(true)
                //设置数据库路径，默认安装程序路径下
//        .setDbDir(ActivityUtils.getSDPath())
                //设置数据库的版本号
                .setDbVersion(2)
                //设置数据库更新的监听
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion,
                                          int newVersion) {
                        try {
                            switch (oldVersion) {
                                case 1:
                                    db.getDatabase().execSQL("alter table siftvo add confidence int");
                                    db.getDatabase().execSQL("alter table siftvo add imageName char(50)");
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                //设置数据库打开的监听
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        //开启数据库支持多线程操作，提升性能
                        db.getDatabase().enableWriteAheadLogging();
                    }
                });
        DbManager db = x.getDb(daoConfig);
        return db;
    }

    public static DbManager getOneAttachDb(Context context) {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                //设置数据库名，默认xutils.db
                .setDbName("attach_one.db")
//        //设置表创建的监听
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {
                        Log.i("JAVA", "onTableCreated：" + table.getName());
                        
                    }
                })
                //设置是否允许事务，默认true
                .setAllowTransaction(true)
                //设置数据库路径，默认安装程序路径下
//        .setDbDir(ActivityUtils.getSDPath())
                //设置数据库的版本号
                .setDbVersion(3)
                //设置数据库更新的监听
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion,
                                          int newVersion) {
                    	try {
                            switch (oldVersion) {
                                case 1:
                                    db.getDatabase().execSQL("alter table AttachOne add isPay int");
                                    db.getDatabase().execSQL("alter table AttachOne add price float");
                                    db.getDatabase().execSQL("alter table AttachOne add attachOneIsPay int");
                                    db.getDatabase().execSQL("alter table AttachOne add attachOneTotalPrice float");
                                    break;
                                case 2:
                                    db.getDatabase().execSQL("alter table AttachOne add attachOneIsPay int");
                                    db.getDatabase().execSQL("alter table AttachOne add attachOneTotalPrice float");
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                //设置数据库打开的监听
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        //开启数据库支持多线程操作，提升性能
                        db.getDatabase().enableWriteAheadLogging();
                    }
                });
        DbManager db = x.getDb(daoConfig);
        return db;
    }

    public static DbManager getTwoAttachDb(Context context) {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                //设置数据库名，默认xutils.db
                .setDbName("attach_two.db")
//        //设置表创建的监听
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {
                        Log.i("JAVA", "onTableCreated：" + table.getName());
                    }
                })
                //设置是否允许事务，默认true
                .setAllowTransaction(true)
                //设置数据库路径，默认安装程序路径下
//        .setDbDir(ActivityUtils.getSDPath())
                //设置数据库的版本号
                .setDbVersion(2)
                //设置数据库更新的监听
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion,
                                          int newVersion) {
                    	try {
                            switch (oldVersion) {
                                case 1:
                                    db.getDatabase().execSQL("alter table AttachTwo add isPay int");
                                    db.getDatabase().execSQL("alter table AttachTwo add price float");
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                //设置数据库打开的监听
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        //开启数据库支持多线程操作，提升性能
                        db.getDatabase().enableWriteAheadLogging();
                    }
                });
        DbManager db = x.getDb(daoConfig);
        return db;
    }
}
