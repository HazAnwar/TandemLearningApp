package com.alpha.tandemexchange;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.layer.atlas.provider.ParticipantProvider;
import com.layer.sdk.LayerClient;
import com.squareup.picasso.Picasso;

/**
 * Used to ensure the LayerClient is still connected when resuming Activities
 */

public abstract class BaseActivity extends AppCompatActivity {
    private final int mLayoutResId;
    private final int mMenuResId;
    private final int mMenuTitleResId;
    private final boolean mMenuBackEnabled;

    /**
     * Constructor used to pass the following parameters
     * @param layoutResId
     * @param menuResId
     * @param menuTitleResId
     * @param menuBackEnabled
     */
    public BaseActivity(int layoutResId, int menuResId, int menuTitleResId, boolean menuBackEnabled) {
        mLayoutResId = layoutResId;
        mMenuResId = menuResId;
        mMenuTitleResId = menuTitleResId;
        mMenuBackEnabled = menuBackEnabled;
    }

    /**
     * Creates an instance of the BaseActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLayoutResId);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        if (mMenuBackEnabled) actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mMenuTitleResId);
    }

    /**
     * Sets the title of the action bar
     * @param title
     */
    @Override
    public void setTitle(CharSequence title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            super.setTitle(title);
        } else {
            actionBar.setTitle(title);
        }
    }

    /**
     * Sets the titleId of the action bar
     * @param titleId
     */
    @Override
    public void setTitle(int titleId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            super.setTitle(titleId);
        } else {
            actionBar.setTitle(titleId);
        }
    }

    /**
     * Specifies what will happen when the chat is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        LayerClient client = MainActivity.getLayerClient();
        if (client == null) return;
        if (client.isAuthenticated()) {
            client.connect();
        } else {
            client.authenticate();
        }
    }

    /**
     * Creates a menu and inflates it
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(mMenuResId, menu);
        return true;
    }

    /**
     * Specifies what needs to be done when an option is selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Menu "Navigate Up" acts like hardware back button
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Accessor (getter) method to access the LayerClient
     * @return LayerClient
     */
    protected LayerClient getLayerClient() {
        return MainActivity.getLayerClient();
    }

    /**
     * Accessor (getter) method to access the ParticipantProvider
     * @return ParticipantProvider
     */
    protected ParticipantProvider getParticipantProvider() {
        return MainActivity.getParticipantProvider();
    }

    /**
     * Accessor (getter) method to access the Picasso
     * @return picasso
     */
    protected Picasso getPicasso() {
        return MainActivity.getPicasso();
    }
}
