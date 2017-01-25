package com.example.ilazar.mykeep.net.mapping;

import android.util.JsonWriter;

import com.example.ilazar.mykeep.content.Doc;

import static com.example.ilazar.mykeep.net.mapping.Api.Note.TEXT;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.DATE;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.USER_ID;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.VERSION;

import java.io.IOException;

import static com.example.ilazar.mykeep.net.mapping.Api.Auth.PASSWORD;
import static com.example.ilazar.mykeep.net.mapping.Api.Auth.USERNAME;

public class DocsWriter implements ResourceWriter<Doc, JsonWriter> {
  @Override
  public void write(Doc doc, JsonWriter writer) throws IOException {
    writer.beginObject();
    {
      writer.name(DATE).value(doc.getDate());
      writer.name(TEXT).value(doc.getText());
      writer.name(VERSION).value(doc.getVersion());
      writer.name(USER_ID).value(doc.getUserId());
    }
    writer.endObject();
  }
}