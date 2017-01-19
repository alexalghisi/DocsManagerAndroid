package com.example.ilazar.mykeep.net.mapping;

import com.example.ilazar.mykeep.content.Doc;

import org.json.JSONObject;

import static com.example.ilazar.mykeep.net.mapping.Api.Note.STATUS;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.TEXT;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.UPDATED;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.USER_ID;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.VERSION;
import static com.example.ilazar.mykeep.net.mapping.Api.Note._ID;

public class NoteJsonObjectReader implements ResourceReader<Doc, JSONObject> {
  private static final String TAG = NoteJsonObjectReader.class.getSimpleName();

  @Override
  public Doc read(JSONObject obj) throws Exception {
    Doc doc = new Doc();
    doc.setId(obj.getString(_ID));
    doc.setText(obj.getString(TEXT));
    doc.setUpdated(obj.getLong(UPDATED));
    doc.setStatus(Doc.Status.valueOf(obj.getString(STATUS)));
    doc.setUserId(obj.getString(USER_ID));
    doc.setVersion(obj.getInt(VERSION));
    return doc;
  }
}
