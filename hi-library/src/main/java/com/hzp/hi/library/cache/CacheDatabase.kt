package com.hzp.hi.library.cache

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hzp.hi.library.util.AppGlobals


@Database(entities = [Cache::class],version = 1,exportSchema = false)
abstract class CacheDatabase :RoomDatabase(){
    abstract val cacheDao: CacheDao
    companion object{
        private var database:CacheDatabase

        fun get():CacheDatabase{
            return database
        }

        init {
            val context = AppGlobals.get()!!.applicationContext
            database=Room.databaseBuilder(context,CacheDatabase::class.java,"hi_cache").build()
        }
    }
}