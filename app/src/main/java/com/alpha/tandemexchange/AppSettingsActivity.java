package com.alpha.tandemexchange;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.layer.atlas.AtlasAvatar;
import com.layer.atlas.provider.Participant;
import com.layer.atlas.util.Util;
import com.layer.sdk.LayerClient;
import com.layer.sdk.changes.LayerChangeEvent;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerAuthenticationListener;
import com.layer.sdk.listeners.LayerChangeEventListener;
import com.layer.sdk.listeners.LayerConnectionListener;
import com.layer.sdk.messaging.Conversation;
import java.util.List;

/**
 * Class with the main app settings and options
 */
public class AppSettingsActivity extends BaseActivity implements LayerConnectionListener, LayerAuthenticationListener, LayerChangeEventListener, View.OnLongClickListener {
    Toolbar toolbar;

    /**
     * Account
     */
    private AtlasAvatar mAvatar;
    private TextView mUserName;
    private TextView mUserState;

    /**
     * Notifications
     */
    private Switch mShowNotifications;

    /**
     * Statistics
     */
    private TextView mConversationCount;
    private TextView mMessageCount;
    private TextView mUnreadMessageCount;

    public AppSettingsActivity() {
        super(R.layout.activity_app_settings, R.menu.menu_settings, R.string.title_settings, true);
    }

    /**
     * Creates an instance of the App and account Settings activity setting up the required GUI and data elements
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // View cache
        mAvatar = (AtlasAvatar) findViewById(R.id.avatar);
        mUserName = (TextView) findViewById(R.id.user_name);
        mUserState = (TextView) findViewById(R.id.user_state);
        mShowNotifications = (Switch) findViewById(R.id.show_notifications_switch);
        mConversationCount = (TextView) findViewById(R.id.conversation_count);
        mMessageCount = (TextView) findViewById(R.id.message_count);
        mUnreadMessageCount = (TextView) findViewById(R.id.unread_message_count);
        mAvatar.init(getParticipantProvider(), getPicasso());

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            mUserName.setTextColor(Color.GRAY);
            mUserState.setTextColor(Color.GRAY);
            mConversationCount.setTextColor(Color.GRAY);
            mMessageCount.setTextColor(Color.GRAY);
            mUnreadMessageCount.setTextColor(Color.GRAY);
        }

        // Long-click copy-to-clipboard
        mUserName.setOnLongClickListener(this);
        mUserState.setOnLongClickListener(this);
        mConversationCount.setOnLongClickListener(this);
        mMessageCount.setOnLongClickListener(this);
        mUnreadMessageCount.setOnLongClickListener(this);

        mShowNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PushNotificationReceiver.getNotifications(AppSettingsActivity.this).setEnabled(isChecked);
            }
        });

    }

    /**
     * Makes the back button on the toolbar go back to the Main activity
     * @param item is the toolbar button that has been clicked
     * @return returns the clicked button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to set the behaviour when the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        getLayerClient()
                .registerAuthenticationListener(this)
                .registerConnectionListener(this)
                .registerEventListener(this);
        refresh();
    }

    /**
     * Method to set the behaviour when the activity is paused
     */
    @Override
    protected void onPause() {
        getLayerClient()
                .unregisterAuthenticationListener(this)
                .unregisterConnectionListener(this)
                .unregisterEventListener(this);
        super.onPause();
    }

    /**
     * Method to enable/disable showing the notifications
     * @param enabled is true/false depending on whether notifications have to be enabled/disabled
     */
    public void setEnabled(final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShowNotifications.setEnabled(enabled);
            }
        });
    }

    /**
     * Method to refresh the user data
     */
    private void refresh() {
        if (!getLayerClient().isAuthenticated()) return;

        /* Account */
        Participant participant = getParticipantProvider().getParticipant(getLayerClient().getAuthenticatedUserId());
        mAvatar.setParticipants(getLayerClient().getAuthenticatedUserId());
        mUserName.setText(participant.getName());
        mUserState.setText(getLayerClient().isConnected() ? R.string.settings_content_connected : R.string.settings_content_disconnected);

        /* Notifications */
        mShowNotifications.setChecked(PushNotificationReceiver.getNotifications(this).isEnabled());

        /* Statistics */
        long totalMessages = 0;
        long totalUnread = 0;
        List<Conversation> conversations = getLayerClient().getConversations();
        for (Conversation conversation : conversations) {
            totalMessages += conversation.getTotalMessageCount();
            totalUnread += conversation.getTotalUnreadMessageCount();
        }
        mConversationCount.setText(String.format("%d", conversations.size()));
        mMessageCount.setText(String.format("%d", totalMessages));
        mUnreadMessageCount.setText(String.format("%d", totalUnread));

    }

    @Override
    public void onAuthenticated(LayerClient layerClient, String s) {
        refresh();
    }

    @Override
    public void onDeauthenticated(LayerClient layerClient) {
        refresh();
    }

    @Override
    public void onAuthenticationChallenge(LayerClient layerClient, String s) {

    }

    @Override
    public void onAuthenticationError(LayerClient layerClient, LayerException e) {

    }

    @Override
    public void onConnectionConnected(LayerClient layerClient) {
        refresh();
    }

    @Override
    public void onConnectionDisconnected(LayerClient layerClient) {
        refresh();
    }

    @Override
    public void onConnectionError(LayerClient layerClient, LayerException e) {

    }

    @Override
    public void onChangeEvent(LayerChangeEvent layerChangeEvent) {
        refresh();
    }

    /**
     * This method tries to extract text from a TextView child of View v
     * @param v is the view from which text is extracted
     * @return returns true/false depending of whether there is a TextView in the View v
     */
    @Override
    public boolean onLongClick(View v) {
        if (v instanceof TextView) {
            Util.copyToClipboard(v.getContext(), R.string.settings_clipboard_description, ((TextView) v).getText().toString());
            Toast.makeText(this, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
