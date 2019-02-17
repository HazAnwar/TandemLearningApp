package com.alpha.tandemexchange;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * AboutUs class creates a new activity which displays some information about the Modern Language
 * Centre
 */

public class AboutUs extends AppCompatActivity {

    /**
     * The toolbar of the activity
     */
    Toolbar toolbar;

    /**
     * Creates a new instance of the activity and and sets up the toolbar to match the theme of the
     * rest of the app
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setTitle("About us");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textView = (TextView) findViewById(R.id.textViewAboutUs);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            textView.setTextColor(Color.BLACK);
        }
    }

    /**
     * Makes the back button on the toolbar go back to the Main activity
     * @param item is the toolbar button that has been clicked
     * @return returns the clicked button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}