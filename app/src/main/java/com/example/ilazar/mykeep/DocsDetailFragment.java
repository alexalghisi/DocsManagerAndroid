package com.example.ilazar.mykeep;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * A fragment representing a single Doc detail screen.
 * This fragment is either contained in a {@link DocsListActivity}
 * in two-pane mode (on tablets) or a {@link DocsDetailActivity}
 * on handsets.
 */
public class DocsDetailFragment extends Fragment {
    public static final String TAG = DocsDetailFragment.class.getSimpleName();

    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String NOTE_ID = "note_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Doc mDoc;

    private KeepApp mApp;

    private Cancellable mFetchNoteAsync;
    private TextView mNoteTextView;
    private CollapsingToolbarLayout mAppBarLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DocsDetailFragment() {
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        mApp = (KeepApp) context.getApplicationContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(NOTE_ID)) {
            // In a real-world scenario, use a Loader
            // to load content from a content provider.
            Activity activity = this.getActivity();
            mAppBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.note_detail, container, false);
        mNoteTextView = (TextView) rootView.findViewById(R.id.note_text);
        fillNoteDetails();
        fetchNoteAsync();
        return rootView;
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    private void fetchNoteAsync() {
        mFetchNoteAsync = mApp.getNoteManager().getNoteAsync(
                getArguments().getString(NOTE_ID),
                new OnSuccessListener<Doc>() {

                    @Override
                    public void onSuccess(final Doc doc) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDoc = doc;
                                fillNoteDetails();
                            }
                        });
                    }
                }, new OnErrorListener() {

                    @Override
                    public void onError(final Exception e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.showError(getActivity(), e);
                            }
                        });
                    }
                });
    }

    private void fillNoteDetails() {
        if (mDoc != null) {
            if (mAppBarLayout != null) {
                mAppBarLayout.setTitle(mDoc.getTitle());
            }
            mNoteTextView.setText(mDoc.getText());
        }
    }
}
