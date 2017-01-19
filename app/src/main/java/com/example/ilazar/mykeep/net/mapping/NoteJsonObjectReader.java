package com.example.ilazar.mykeep.net.mapping;

import com.example.ilazar.mykeep.content.Note;

import org.json.JSONObject;

import static com.example.ilazar.mykeep.net.mapping.Api.Note.STATUS;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.TEXT;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.UPDATED;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.USER_ID;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.VERSION;
import static com.example.ilazar.mykeep.net.mapping.Api.Note._ID;

public class NoteJsonObjectReader implements ResourceReader<Note, JSONObject> {
  private static final String TAG = NoteJsonObjectReader.class.getSimpleName();

  @Override
  public Note read(JSONObject obj) throws Exception {
    Note note = new Note();
    note.setId(obj.getString(_ID));
    note.setText(obj.getString(TEXT));
    note.setUpdated(obj.getLong(UPDATED));
    note.setStatus(Note.Status.valueOf(obj.getString(STATUS)));
    note.setUserId(obj.getString(USER_ID));
    note.setVersion(obj.getInt(VERSION));
    return note;
  }
}
