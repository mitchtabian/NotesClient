package com.codingwithmitch.notesclient.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import com.codingwithmitch.notesclient.models.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM note")
    List<Note> getAllNotes();

    @Insert
    long[] insertNotes(Note... notes);

    @Delete
    int delete(Note note);

    @Query("UPDATE note SET title = :title, content = :content, timestamp = :timestamp WHERE uid = :uid")
    int updateNote(String title, String content, String timestamp, int uid);

    @Query("SELECT * FROM Note LIMIT :row, 2 ")
    public List<Note> getSomeNotes(int row);

    @Query("SELECT COUNT(*) FROM Note")
    public Integer getNumRows();
}
