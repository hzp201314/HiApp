package com.hzp.hiapp.demo.room

import android.graphics.Bitmap
import androidx.annotation.NonNull
import androidx.room.*

@Entity(tableName = "table_cache")
class Cache {

    @PrimaryKey(autoGenerate = false)
    var cache_key: String = ""

    //    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cacheId", defaultValue = "1")
    val cache_id: Long = 0

    @Ignore
    var bitmap: Bitmap? = null

    //如果想让内嵌对象中的字段也一同映射成数据库表的字段，可以用这个注解@Embedded
    //但有要求，User对象必须也适用@Entity注解标记，并且拥有一个不为空的主键
    @Embedded
    var user: User? = null
}

@Entity(tableName = "table_user")
class User {
    @PrimaryKey
    @NonNull
    var name: String = ""
    var age = 10
}