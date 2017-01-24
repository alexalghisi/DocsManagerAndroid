package com.example.ilazar.mykeep.service;

import android.content.Context;
import android.util.Log;

import com.example.ilazar.mykeep.content.Doc;
import com.example.ilazar.mykeep.content.User;
import com.example.ilazar.mykeep.content.database.KeepDatabase;
import com.example.ilazar.mykeep.net.LastModifiedList;
import com.example.ilazar.mykeep.net.NoteRestClient;
import com.example.ilazar.mykeep.net.NoteSocketClient;
import com.example.ilazar.mykeep.net.ResourceChangeListener;
import com.example.ilazar.mykeep.net.ResourceException;
import com.example.ilazar.mykeep.util.Cancellable;
import com.example.ilazar.mykeep.util.CancellableCallable;
import com.example.ilazar.mykeep.util.OnErrorListener;
import com.example.ilazar.mykeep.util.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DocsManager extends Observable {
  private static final String TAG = DocsManager.class.getSimpleName();
  private final KeepDatabase mKD;

  private ConcurrentMap<String, Doc> mDocs = new ConcurrentHashMap<String, Doc>();
  private String mNotesLastUpdate;

  private final Context mContext;
  private NoteRestClient mNoteRestClient;
  private NoteSocketClient mNoteSocketClient;
  private String mToken;
  private User mCurrentUser;

  public DocsManager(Context context) {
    mContext = context;
    mKD = new KeepDatabase(context);
  }

  public CancellableCallable<LastModifiedList<Doc>> getNotesCall() {
    Log.d(TAG, "getNotesCall");
    return mNoteRestClient.search(mNotesLastUpdate);
  }

  public List<Doc> executeNotesCall(CancellableCallable<LastModifiedList<Doc>> getNotesCall) throws Exception {
    Log.d(TAG, "execute getNotes...");
    LastModifiedList<Doc> result = getNotesCall.call();
    List<Doc> docs = result.getList();
    if (docs != null) {
      mNotesLastUpdate = result.getLastModified();
      updateCachedNotes(docs);
      notifyObservers();
    }
    return cachedNotesByUpdated();
  }

  public NoteLoader getNoteLoader() {
    Log.d(TAG, "getNoteLoader...");
    return new NoteLoader(mContext, this);
  }

  public void setNoteRestClient(NoteRestClient noteRestClient) {
    mNoteRestClient = noteRestClient;
  }

  public Cancellable getNotesAsync(final OnSuccessListener<List<Doc>> successListener, OnErrorListener errorListener) {
    Log.d(TAG, "getNotesAsync...");
    return mNoteRestClient.searchAsync(mNotesLastUpdate, new OnSuccessListener<LastModifiedList<Doc>>() {

      @Override
      public void onSuccess(LastModifiedList<Doc> result) {
        Log.d(TAG, "getNotesAsync succeeded");
        List<Doc> docs = result.getList();
        if (docs != null) {
          mNotesLastUpdate = result.getLastModified();
          updateCachedNotes(docs);
        }
        successListener.onSuccess(cachedNotesByUpdated());
        notifyObservers();
      }
    }, errorListener);
  }

  public Cancellable getNoteAsync(
      final String noteId,
      final OnSuccessListener<Doc> successListener,
      final OnErrorListener errorListener) {
    Log.d(TAG, "getNoteAsync...");
    return mNoteRestClient.readAsync(noteId, new OnSuccessListener<Doc>() {

      @Override
      public void onSuccess(Doc doc) {
        Log.d(TAG, "getNoteAsync succeeded");
        if (doc == null) {
          setChanged();
          mDocs.remove(noteId);
        } else {
          if (!doc.equals(mDocs.get(doc.getId()))) {
            setChanged();
            mDocs.put(noteId, doc);
          }
        }
        successListener.onSuccess(doc);
        notifyObservers();
      }
    }, errorListener);
  }

  public Cancellable addDocAsync(
          final Doc doc,
          final OnSuccessListener<Doc> successListener,
          final OnErrorListener errorListener) {
    Log.d(TAG, "addDocAsync...");

    return mNoteRestClient.addAsync(doc, new OnSuccessListener<Doc>() {

      @Override
      public void onSuccess(Doc doc) {
        Log.d(TAG, "addDocAsync succeeded");
        if (doc !=null) {
          mDocs.put(doc.getId(), doc);
        }
        successListener.onSuccess(doc);
        notifyObservers();
      }
    }, errorListener);
  }

  public Cancellable saveNoteAsync(
      final Doc doc,
      final OnSuccessListener<Doc> successListener,
      final OnErrorListener errorListener) {
    Log.d(TAG, "saveNoteAsync...");
    return mNoteRestClient.updateAsync(doc, new OnSuccessListener<Doc>() {

      @Override
      public void onSuccess(Doc doc) {
        Log.d(TAG, "saveNoteAsync succeeded");
        mDocs.put(doc.getId(), doc);
        successListener.onSuccess(doc);
        setChanged();
        notifyObservers();
      }
    }, errorListener);
  }

  public void subscribeChangeListener() {
    mNoteSocketClient.subscribe(new ResourceChangeListener<Doc>() {
      @Override
      public void onCreated(Doc doc) {
        Log.d(TAG, "changeListener, onCreated");
        ensureNoteCached(doc);
      }

      @Override
      public void onUpdated(Doc doc) {
        Log.d(TAG, "changeListener, onUpdated");
        ensureNoteCached(doc);
      }

      @Override
      public void onDeleted(String noteId) {
        Log.d(TAG, "changeListener, onDeleted");
        if (mDocs.remove(noteId) != null) {
          setChanged();
          notifyObservers();
        }
      }

      private void ensureNoteCached(Doc doc) {
        if (!doc.equals(mDocs.get(doc.getId()))) {
          Log.d(TAG, "changeListener, cache updated");
          mDocs.put(doc.getId(), doc);
          setChanged();
          notifyObservers();
        }
      }

      @Override
      public void onError(Throwable t) {
        Log.e(TAG, "changeListener, error", t);
      }
    });
  }

  public void unsubscribeChangeListener() {
    mNoteSocketClient.unsubscribe();
  }

  public void setNoteSocketClient(NoteSocketClient noteSocketClient) {
    mNoteSocketClient = noteSocketClient;
  }

  private void updateCachedNotes(List<Doc> docs) {
    Log.d(TAG, "updateCachedNotes");
    for (Doc doc : docs) {
      mDocs.put(doc.getId(), doc);
    }
    setChanged();
  }

  private List<Doc> cachedNotesByUpdated() {
    ArrayList<Doc> docs = new ArrayList<>(mDocs.values());
    Collections.sort(docs, new NoteByUpdatedComparator());
    return docs;
  }

  public List<Doc> getCachedNotes() {
    return cachedNotesByUpdated();
  }

  public Cancellable loginAsync(
      String username, String password,
      final OnSuccessListener<String> successListener,
      final OnErrorListener errorListener) {
    final User user = new User(username, password);
    return mNoteRestClient.getToken(
        user, new OnSuccessListener<String>() {

          @Override
          public void onSuccess(String token) {
            mToken = token;
            if (mToken != null) {
              user.setToken(mToken);
              setCurrentUser(user);
              mKD.saveUser(user);
              successListener.onSuccess(mToken);
            } else {
              errorListener.onError(new ResourceException(new IllegalArgumentException("Invalid credentials")));
            }
          }
        }, errorListener);
  }

  public void setCurrentUser(User currentUser) {
    mCurrentUser = currentUser;
    mNoteRestClient.setUser(currentUser);
  }

  public User getCurrentUser() {
    return mKD.getCurrentUser();
  }

  private class NoteByUpdatedComparator implements java.util.Comparator<Doc> {
    @Override
    public int compare(Doc n1, Doc n2) {
      return (int) (n1.getUpdated() - n2.getUpdated());
    }
  }
}
