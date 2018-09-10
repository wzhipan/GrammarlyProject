package com.zhipan.grammarly.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.zhipan.grammarly.data.model.Avatar;

@Database(entities = {Avatar.class}, exportSchema = false, version = 1)
public abstract class ContactsDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "contacts";
    private static volatile ContactsDatabase sInstance;

    public abstract ContactDao getContactDao();

    public static ContactsDatabase getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null)
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            ContactsDatabase.class, DATABASE_NAME).build();
            }
        }

        return sInstance;
    }
}
