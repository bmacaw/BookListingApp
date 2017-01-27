package com.example.android.booklistingapp;

/**
 * An {@link Book} object contains information related to a single book.
 */

public class Book {

    /*
    * Title of the book
    */
    private String mTitle;
    /*
    * Author of the book
    */
    private String mAuthors;


    /**
     * Constructs a new (@link Book) object
     *
     * @param title   is the title of the book
     * @param authors is the author or authors of the book
     */

    public Book(String title, String authors) {
        mTitle = title;
        mAuthors = authors;
    }

    /**
     * Returns the title of the book
     */
    public String getTitle() {
        return mTitle;
    }


    /**
     * Returns the authors of the book
     */
    public String getAuthors() {
        return mAuthors;
    }


}
