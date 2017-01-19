package com.example.ilazar.mykeep.net.mapping;

import android.util.JsonWriter;

import com.example.ilazar.mykeep.content.Doc;

import java.io.IOException;

import static com.example.ilazar.mykeep.net.mapping.Api.Note.STATUS;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.TEXT;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.UPDATED;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.USER_ID;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.VERSION;
import static com.example.ilazar.mykeep.net.mapping.Api.Note._ID;

public class NoteWriter implements ResourceWriter<Doc, JsonWriter>{
  @Override
  public void write(Doc doc, JsonWriter writer) throws IOException {
    writer.beginObject();
    {
      if (doc.getId() != null) {
        writer.name(_ID).value(doc.getId());
      }
      writer.name(TEXT).value(doc.getText());
      writer.name(STATUS).value(doc.getStatus().name());
      if (doc.getUpdated() > 0) {
        writer.name(UPDATED).value(doc.getUpdated());
      }
      if (doc.getUserId() != null) {
        writer.name(USER_ID).value(doc.getUserId());
      }
      if (doc.getVersion() > 0) {
        writer.name(VERSION).value(doc.getVersion());
      }
    }
    writer.endObject();
  }
}
