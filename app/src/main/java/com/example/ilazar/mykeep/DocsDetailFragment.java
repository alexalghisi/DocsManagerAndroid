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
    public static final String TEXT = "text";
    public static final String DATE = "date";

    /**
     * The dummy content this fragment is presenting.
     */
    private Doc mDoc;

    private NoteManager mApp;

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
        mApp = (NoteManager) context.getApplicationContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        mDoc = new Doc();
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(NOTE_ID)) {
            // In a real-world scenario, use a Loader
            // to load content from a content provider.
            Activity activity = this.getActivity();
            mAppBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            mDoc.setId(getArguments().getString(NOTE_ID));
        }
        Log.d("ID?", Boolean.toString(getArguments().containsKey(NOTE_ID)));
        Log.d("Text?", Boolean.toString(getArguments().containsKey(TEXT)));
        Log.d("Date?", Boolean.toString(getArguments().containsKey(DATE)));

        if(getArguments().containsKey(TEXT)) {
            mDoc.setText(getArguments().getString(TEXT));
        }
        if(getArguments().containsKey(DATE)) {
            mDoc.setDate(getArguments().getString(DATE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.note_detail, container, false);
        mNoteTextView = (TextView) rootView.findViewById(R.id.note_text);

        if(getArguments().containsKey(TEXT)) {
            mDoc.setText(getArguments().getString(TEXT));
        }
        if(getArguments().containsKey(DATE)) {
            mDoc.setDate(getArguments().getString(DATE));
        }
        fillNoteDetails();
        return rootView;
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    private void fillNoteDetails() {
        Log.d("New doc", mDoc.toString());
        if (mDoc != null) {
            if (mAppBarLayout != null) {
                mAppBarLayout.setTitle(mDoc.getId());
            }
            mNoteTextView.setText(mDoc.getDate() + " \n" + mDoc.getText());
        }
    }
}
