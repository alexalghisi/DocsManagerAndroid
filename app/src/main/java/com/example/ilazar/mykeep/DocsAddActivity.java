package com.example.ilazar.mykeep;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ilazar.mykeep.content.Doc;
import com.example.ilazar.mykeep.util.OnErrorListener;
import com.example.ilazar.mykeep.util.OnSuccessListener;

/**
 * Created by alessandro on 19.01.2017.
 */

public class DocsAddActivity extends AppCompatActivity {

    public static final String TAG = DocsAddActivity.class.getSimpleName();
    /**
     * Reference to the singleton app used to access the app state and logic.
     */
    private KeepApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs_add);
        setupToolbar();
        mApp = (KeepApp) getApplication();

        final Button button = (Button) findViewById(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Doc doc = new Doc();
                doc.setTitle(((EditText)findViewById(R.id.editTitle)).getText().toString());
                doc.setText(((EditText)findViewById(R.id.editText)).getText().toString());
                doc.setUserId("test");
                doc.setVersion(1);
                mApp.getNoteManager().addDocAsync(doc,
                    new OnSuccessListener<Doc>() {
                        @Override
                        public void onSuccess(final Doc docs) {
                            Log.d(TAG, "getNotesAsyncCall - success");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Return to list docs.
                                    //showContent(docs);

                                }
                            });
                        }
                    }, new OnErrorListener() {
                        @Override
                        public void onError(final Exception e) {
                            Log.d(TAG, "getNotesAsyncCall - error");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //showError(e);
                                }
                            });
                        }
                    }
                );

                // Switch back activity
                Context context = v.getContext();
                Intent intent = new Intent(context, DocsListActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
