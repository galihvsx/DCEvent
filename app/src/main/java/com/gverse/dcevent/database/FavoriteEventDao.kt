package com.gverse.dcevent.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(fav: FavoriteEvent)

    @Update
    fun update(fav: FavoriteEvent)

    @Query("DELETE FROM FavoriteEvent WHERE id = :id")
    fun delete(id: String)

    @Query("SELECT * FROM FavoriteEvent")
    fun getAllFavorites(): LiveData<List<FavoriteEvent>>

    @Query("SELECT * FROM FavoriteEvent WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<FavoriteEvent>

    @Query("SELECT EXISTS(SELECT * FROM FavoriteEvent WHERE id = :id)")
    fun isFavorite(id: String): LiveData<Boolean>
}