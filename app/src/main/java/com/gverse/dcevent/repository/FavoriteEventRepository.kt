package com.gverse.dcevent.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.gverse.dcevent.database.FavoriteEvent
import com.gverse.dcevent.database.FavoriteEventDao
import com.gverse.dcevent.database.FavoriteEventRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteEventRepository(application: Application) {
    private val mFavoriteEventDao: FavoriteEventDao
    private val executorService : ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteEventRoomDatabase.getDatabase(application)
        mFavoriteEventDao = db.FavoriteEventDao()
    }

    fun getAllFavorites(): LiveData<List<FavoriteEvent>> {
        return mFavoriteEventDao.getAllFavorites()
    }

    fun insert(fav: FavoriteEvent) {
        executorService.execute { mFavoriteEventDao.insert(fav) }
    }

    fun delete(favId: String) {
        executorService.execute { mFavoriteEventDao.delete(favId) }
    }

    fun getFavoriteEventById(id: String) : LiveData<FavoriteEvent> = mFavoriteEventDao.getFavoriteEventById(id)

    fun isFavorite(eventId: String) : LiveData<Boolean> = mFavoriteEventDao.isFavorite(eventId)
}