package com.zhipan.grammarly.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.zhipan.grammarly.data.model.Avatar;

import java.util.List;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM contacts ORDER BY id ASC")
    LiveData<List<Avatar>> getAll();

    @Query("SELECT * FROM contacts ORDER BY id ASC")
    List<Avatar> getAllInRawForm();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Avatar avatar);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Avatar> avatars);

    @Query("DELETE FROM contacts")
    void deleteAll();
}
