package com.kuaibao.skuaidi.greendao.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.socks.library.KLog;

import org.greenrobot.greendao.database.Database;

import gen.greendao.dao.DaoMaster;
import gen.greendao.dao.DispatchDao;
import gen.greendao.dao.ICallLogDao;
import gen.greendao.dao.INetCallInfoDao;
import gen.greendao.dao.KBAccountDao;
import gen.greendao.dao.LoginUserAccountDao;
import gen.greendao.dao.SKuaidiCircleDao;
import gen.greendao.dao.UserBindDao;
import gen.greendao.dao.UserTelePreferDao;

/**
 * 数据库更新
 * Created by lgg
 */
public class MySQLiteOpenHelper extends DaoMaster.OpenHelper
{

    private static final String TAG = "greenDao";

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        KLog.i(TAG, "Upgrading schema from version " + oldVersion + " to " + newVersion + " by migrating all tables data");
        // 注意把所新版本的表的xxDao都添加到这里
        MigrationHelper.getInstance().migrate(db, KBAccountDao.class, LoginUserAccountDao.class, UserBindDao.class, DispatchDao.class, UserTelePreferDao.class, SKuaidiCircleDao.class, ICallLogDao.class, INetCallInfoDao.class);
    }
}
