package com.example.ilazar.mykeep;

import android.app.Application;
import android.util.Log;

import com.example.ilazar.mykeep.net.NoteRestClient;
import com.example.ilazar.mykeep.net.NoteSocketClient;
import com.example.ilazar.mykeep.service.DocsManager;

public class KeepApp extends Application {
  public static final String TAG = KeepApp.class.getSimpleName();
  private DocsManager mDocsManager;
  private NoteRestClient mNoteRestClient;
  private NoteSocketClient mNoteSocketClient;

  @Override
  public void onCreate() {
    Log.d(TAG, "onCreate");
    super.onCreate();
    mDocsManager = new DocsManager(this);
    mNoteRestClient = new NoteRestClient(this);
    mNoteSocketClient = new NoteSocketClient(this);
    mDocsManager.setNoteRestClient(mNoteRestClient);
    mDocsManager.setNoteSocketClient(mNoteSocketClient);
  }

  public DocsManager getNoteManager() {
    return mDocsManager;
  }

  @Override
  public void onTerminate() {
    Log.d(TAG, "onTerminate");
    super.onTerminate();
  }
}
