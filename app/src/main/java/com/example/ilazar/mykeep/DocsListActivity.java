package com.example.ilazar.mykeep;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ilazar.mykeep.content.Doc;
import com.example.ilazar.mykeep.util.Cancellable;
import com.example.ilazar.mykeep.util.DialogUtils;
import com.example.ilazar.mykeep.util.OnErrorListener;
import com.example.ilazar.mykeep.util.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Notes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DocsDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class DocsListActivity extends AppCompatActivity {

    public static final String TAG = DocsListActivity.class.getSimpleName();

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;

    /**
     * Whether or not the the notes were loaded.
     */
    private boolean mNotesLoaded;

    /**
     * Reference to the singleton app used to access the app state and logic.
     */
    private KeepApp mApp;
    private Context mContext;

    /**
     * Reference to the last async call used for cancellation.
     */
    private Cancellable mGetNotesAsyncCall;
    private View mContentLoadingView;
    private RecyclerView mRecyclerView;

    private boolean mIsLoading;
    LinearLayoutManager mLayoutManager;

    private List<Doc> mNote;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mApp = (KeepApp) getApplication();
        mContext = getApplicationContext();
        mNote = new ArrayList<>();
        setContentView(R.layout.activity_note_list);
        setupToolbar();
        setupFloatingActionBar();
        setupRecyclerView();
        checkTwoPaneMode();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        startGetDocsAsyncCall();
        mApp.getNoteManager().subscribeChangeListener();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        //ensureGetNotesAsyncTaskCancelled();
        ensureGetNotesAsyncCallCancelled();
        mApp.getNoteManager().unsubscribeChangeListener();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }

    private void setupFloatingActionBar() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, DocsAddActivity.class);
                context.startActivity(intent);

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    private void setupRecyclerView() {
        mContentLoadingView = findViewById(R.id.content_loading);
        mRecyclerView = (RecyclerView) findViewById(R.id.note_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                //Log.d("DY :::", Integer.toString(dy));
                if(dy >= 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    //Log.d("LEFT ::: ", Integer.toString(visibleItemCount + pastVisiblesItems));
                    //Log.d("RIGHT ::: ", Integer.toString(totalItemCount));

                    if (true)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            Log.v("... ... ...", "Last Item Wow !");
                            startGetDocsAsyncCall();
                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        });
    }


   /* RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (mIsLoading)
                return;
            int visibleItemCount = recyclerView.LayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
            if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                //End of list
                Log.d("END", "OF LIST");
            }
        }
    };*/


    private void checkTwoPaneMode() {
        if (findViewById(R.id.note_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void startGetDocsAsyncCall() {
        if (mNotesLoaded) {
            Log.d(TAG, "start getNotesAsyncCall - content already loaded, return");
            return;
        }
        mIsLoading = true;
        showLoadingIndicator();
        if (isOnline()) {
            // Load from server and update database.
            mGetNotesAsyncCall = mApp.getNoteManager().getNotesAsync(
                    new OnSuccessListener<List<Doc>>() {
                        @Override
                        public void onSuccess(final List<Doc> docs) {
                            Log.d(TAG, "getNotesAsyncCall - success");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mNote.addAll(docs);
                                    showContent();
                                    mIsLoading = false;
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
                                    showError(e);
                                }
                            });
                        }
                    }
            );
        } else {
            // Load from local storage.
            Log.d("Device", " NOT ONLINE !!!");
        }
    }


    private void ensureGetNotesAsyncCallCancelled() {
        if (mGetNotesAsyncCall != null) {
            Log.d(TAG, "ensureGetNotesAsyncCallCancelled - cancelling the task");
            mGetNotesAsyncCall.cancel();
        }
    }

    private void showError(Exception e) {
        Log.e(TAG, "showError", e);
        if (mContentLoadingView.getVisibility() == View.VISIBLE) {
            mContentLoadingView.setVisibility(View.GONE);
        }
        DialogUtils.showError(this, e);
    }

    private void showLoadingIndicator() {
        Log.d(TAG, "showLoadingIndicator");
        mRecyclerView.setVisibility(View.GONE);
        mContentLoadingView.setVisibility(View.VISIBLE);
    }

    private void showContent() {
        Log.d(TAG, "showContent");
        mRecyclerView.setAdapter(new NoteRecyclerViewAdapter(mNote));
        mContentLoadingView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public class NoteRecyclerViewAdapter
            extends RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder> {

        private final List<Doc> mValues;

        public NoteRecyclerViewAdapter(List<Doc> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.note_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getId());
            holder.mContentView.setText(mValues.get(position).getText());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(DocsDetailFragment.NOTE_ID, holder.mItem.getId());
                        arguments.putString(DocsDetailFragment.TEXT, holder.mItem.getText());
                        arguments.putString(DocsDetailFragment.DATE, holder.mItem.getDate());

                        DocsDetailFragment fragment = new DocsDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.note_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DocsDetailActivity.class);
                        intent.putExtra(DocsDetailFragment.NOTE_ID, holder.mItem.getId());
                        intent.putExtra(DocsDetailFragment.TEXT, holder.mItem.getText());
                        intent.putExtra(DocsDetailFragment.DATE, holder.mItem.getDate());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Doc mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
