package com.hzp.hiapp.demo.room

import androidx.lifecycle.LiveData
import androidx.room.*

//data access object 数据访问对象，这里面会定义数据操作的增删改查的方法
@Dao
interface CacheDao {

    @Query("select * from table_cache where `cache_key`=:keyword limit 1")
    fun query(keyword:String):Cache //List<Cache>


    //可以通过liveData以观察者的形式获取数据库数据，可以避免不必要的npe
    //更重要的是 他可以监听数据库表中的数据的变化。一旦发生了insert update delete。
    // room会自动读取表中最新的数据,发送给UI层刷新数据
    @Query("select * from table_cache")
    fun query2():LiveData<List<Cache>>


    //根据cache_key删除数据
    @Delete(entity = Cache::class)
    fun delete(key:String)

    //根据cache对象删除数据
    @Delete(entity = Cache::class)
    fun delete(cache:Cache)

    //onConflict:cache_key发生冲突的解决方法 OnConflictStrategy.REPLACE:替换覆盖 IGNORE:忽略停止 ABORT:终止
    @Insert(entity = Cache::class,onConflict = OnConflictStrategy.REPLACE)
    fun insert(cache: Cache)

    @Update()
    fun update(cache: Cache)
}