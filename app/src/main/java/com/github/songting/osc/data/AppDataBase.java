package com.github.songting.osc.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.github.songting.osc.cache.Cache;
import com.github.songting.osc.cache.CacheDao;
import com.github.songting.osc.cache.VodCollect;
import com.github.songting.osc.cache.VodCollectDao;
import com.github.songting.osc.cache.VodRecord;
import com.github.songting.osc.cache.VodRecordDao;


/**
 * 类描述:
 *
 * @author pj567
 * @since 2020/5/15
 */
@Database(entities = {Cache.class, VodRecord.class, VodCollect.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract CacheDao getCacheDao();

    public abstract VodRecordDao getVodRecordDao();

    public abstract VodCollectDao getVodCollectDao();
}
