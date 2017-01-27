package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class BookQuestActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    /**
     * Constant value for the book loader ID; can choose any integer
     * and really only comes into play if using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;

    // The adapter that binds our data to the ListView.
    private BookAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_quest);

        // Find a reference to the {@link ListView} in the layout
        final ListView bookListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);


        // TextView to signify no books found based on search
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);


        // mSearchEditText is the EditText where the user enters keywords for search.
        final EditText mSearchEditText = (EditText) findViewById(R.id.search_edit_text);


        // mSearchButton is the Button the user clicks to begin search.
        Button mSearchButton = (Button) findViewById(R.id.search_button);

        // Set a click listener on the mSearchButton.
        mSearchButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                // Get the text from the mSearchEditText, update, adn set to mSearchText.
                String mSearchText = mSearchEditText.getText().toString().replaceAll(" ", "+");

                // If mSearchText is empty, prompt user to enter keywords to find books.
                if (mSearchText.isEmpty()) {
                    Toast.makeText(BookQuestActivity.this,
                            "Please enter title or author to begin your search.", Toast.LENGTH_SHORT).show();
                }

                // Restart the loader for the new search.
                getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BookQuestActivity.this);

                // Log message to indicate the loader has restarted on click.
                Log.i("onClick", "loader restarted");

                // Set new adapter on new list of books based on search.
                mAdapter = new BookAdapter(BookQuestActivity.this, new ArrayList<Book>());
                bookListView.setAdapter(mAdapter);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network.
        NetworkInfo activeNetwork = connectivityMgr.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, BookQuestActivity.this);

        } else {

            // Otherwise, display error
            // First, hide loading indicator so error message will be visible.
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message.
            mEmptyStateTextView.setText(R.string.no_internet_connection);

        }
    }

    /**
     * This method gets the user's keywords and updates mSearchEditText
     */
    public String getSearchText() {

        // Log message for getSearchText
        Log.i("getSearchText", "get search text");

        EditText mSearchEditText = (EditText) findViewById(R.id.search_edit_text);
        return mSearchEditText.getText().toString().replaceAll(" ", "+");

    }

    /**
     * This method uses GOOGLE_BOOKS_API_URL  and getSearchText()
     * to create a complete search string for the user's search.
     */
    public String getSearchUrl() {
        String mGoogleBooksAPIUrl = "https://www.googleapis.com/books/v1/volumes?maxResults=20&q=";

        // Log message for getSearchUrl
        Log.i("getSearchUrl", "get search url");
        return mGoogleBooksAPIUrl + getSearchText();
    }


    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        // Create a new loader for new mSearchUrl
        return new BookLoader(this, getSearchUrl());
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        // Log message for onLoadFinished
        Log.i("onLoadFinished", "loader finished");

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to prompt user for search
        mEmptyStateTextView.setText(R.string.no_books_found);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapters
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.i("onLoaderReset", "loader reset");
        mAdapter.clear();
    }

}
