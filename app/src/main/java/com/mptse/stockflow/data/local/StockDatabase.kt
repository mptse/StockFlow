package com.mptse.stockflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProductEntity::class, StockMovementEntity::class], version = 4, exportSchema = false)
abstract class StockDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun stockMovementDao(): StockMovementDao

    companion object {
        @Volatile
        private var INSTANCE: StockDatabase? = null

        fun getDatabase(context: Context): StockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockDatabase::class.java,
                    "stockflow_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
