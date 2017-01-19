package com.example.ilazar.mykeep.net;

import android.content.Context;
import android.util.Log;

import com.example.ilazar.mykeep.R;
import com.example.ilazar.mykeep.content.Doc;
import com.example.ilazar.mykeep.net.mapping.IdJsonObjectReader;
import com.example.ilazar.mykeep.net.mapping.NoteJsonObjectReader;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.example.ilazar.mykeep.net.mapping.Api.Note.NOTE_CREATED;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.NOTE_DELETED;
import static com.example.ilazar.mykeep.net.mapping.Api.Note.NOTE_UPDATED;

public class NoteSocketClient {
  private static final String TAG = NoteSocketClient.class.getSimpleName();
  private final Context mContext;
  private Socket mSocket;
  private ResourceChangeListener<Doc> mResourceListener;

  public NoteSocketClient(Context context) {
    mContext = context;
    Log.d(TAG, "created");
  }

  public void subscribe(final ResourceChangeListener<Doc> resourceListener) {
    Log.d(TAG, "subscribe");
    mResourceListener = resourceListener;
    try {
      mSocket = IO.socket(mContext.getString(R.string.api_url));
      mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
        @Override
        public void call(Object... args) {
          Log.d(TAG, "socket connected");
        }
      });
      mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
        @Override
        public void call(Object... args) {
          Log.d(TAG, "socket disconnected");
        }
      });
      mSocket.on(NOTE_CREATED, new Emitter.Listener() {
        @Override
        public void call(Object... args) {
          try {
            Doc doc = new NoteJsonObjectReader().read((JSONObject) args[0]);
            Log.d(TAG, String.format("doc created %s", doc.toString()));
            mResourceListener.onCreated(doc);
          } catch (Exception e) {
            Log.w(TAG, "note created", e);
            mResourceListener.onError(new ResourceException(e));
          }
        }
      });
      mSocket.on(NOTE_UPDATED, new Emitter.Listener() {
        @Override
        public void call(Object... args) {
          try {
            Doc doc = new NoteJsonObjectReader().read((JSONObject) args[0]);
            Log.d(TAG, String.format("doc updated %s", doc.toString()));
            mResourceListener.onUpdated(doc);
          } catch (Exception e) {
            Log.w(TAG, "note updated", e);
            mResourceListener.onError(new ResourceException(e));
          }
        }
      });
      mSocket.on(NOTE_DELETED, new Emitter.Listener() {
        @Override
        public void call(Object... args) {
          try {
            String id = new IdJsonObjectReader().read((JSONObject) args[0]);
            Log.d(TAG, String.format("note deleted %s", id));
            mResourceListener.onDeleted(id);
          } catch (Exception e) {
            Log.w(TAG, "note deleted", e);
            mResourceListener.onError(new ResourceException(e));
          }
        }
      });
      mSocket.connect();
    } catch (Exception e) {
      Log.w(TAG, "socket error", e);
      mResourceListener.onError(new ResourceException(e));
    }
  }

  public void unsubscribe() {
    Log.d(TAG, "unsubscribe");
    if (mSocket != null) {
      mSocket.disconnect();
    }
    mResourceListener = null;
  }

}
