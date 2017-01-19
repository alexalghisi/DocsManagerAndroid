package com.example.ilazar.mykeep.net.mapping;

import android.util.JsonReader;
import android.util.Log;

import com.example.ilazar.mykeep.content.Doc;

import java.io.IOException;

import static com.example.ilazar.mykeep.net.mapping.Api.Note.STATUS;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.TEXT;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.TITLE;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.UPDATED;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.USER_ID;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.VERSION;
import static com.example.ilazar.mykeep.net.mapping.Api.Note._ID;

public class NoteReader implements ResourceReader<Doc, JsonReader> {
  private static final String TAG = NoteReader.class.getSimpleName();

  @Override
  public Doc read(JsonReader reader) throws IOException {
    Doc doc = new Doc();
    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      if (name.equals(_ID)) {
        doc.setId(reader.nextString());
      } else if (name.equals(TEXT)) {
        doc.setText(reader.nextString());
      } else if (name.equals(TITLE)) {
        doc.setTitle(reader.nextString());
      } else if (name.equals(STATUS)) {
        doc.setStatus(Doc.Status.valueOf(reader.nextString()));
      } else if (name.equals(UPDATED)) {
        doc.setUpdated(reader.nextLong());
      } else if (name.equals(USER_ID)) {
        doc.setUserId(reader.nextString());
      } else if (name.equals(VERSION)) {
        doc.setVersion(reader.nextInt());
      } else {
        reader.skipValue();
        Log.w(TAG, String.format("Doc property '%s' ignored", name));
      }
    }
    reader.endObject();
    return doc;
  }
}
