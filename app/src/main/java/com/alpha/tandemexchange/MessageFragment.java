package com.alpha.tandemexchange;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.layer.atlas.AtlasConversationsRecyclerView;
import com.layer.atlas.adapters.AtlasConversationsAdapter;
import com.layer.atlas.util.views.SwipeableItem;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;

import java.util.List;

/**
 * MessageFragment allows the user to look at their previous chats and also create a new one by clicking on the
 * */

public class MessageFragment extends Fragment {
    MenuItem messageSettings;
    StoreLocalUserData storeLocalUserData;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        storeLocalUserData = new StoreLocalUserData(getActivity());
        final View message = inflater.inflate(R.layout.fragment_messages, null);

        final AtlasConversationsRecyclerView conversationsList = (AtlasConversationsRecyclerView) message.findViewById(R.id.conversations_list);

        // Atlas methods
        conversationsList.init(MainActivity.getLayerClient(), MainActivity.getParticipantProvider(), MainActivity.getPicasso())
                .setInitialHistoricMessagesToFetch(20)
                .setOnConversationClickListener(new AtlasConversationsAdapter.OnConversationClickListener() {
                    @Override
                    public void onConversationClick(AtlasConversationsAdapter adapter, Conversation conversation) {
                        Intent intent = new Intent(getActivity(), MessagesListActivity.class);
                        userScore();
                        intent.putExtra(PushNotificationReceiver.LAYER_CONVERSATION_KEY, conversation.getId());
                        startActivity(intent);
                    }

                    @Override
                    public boolean onConversationLongClick(AtlasConversationsAdapter adapter, Conversation conversation) {
                        return false;
                    }
                })
                .setOnConversationSwipeListener(new SwipeableItem.OnSwipeListener<Conversation>() {
                    @Override
                    public void onSwipe(final Conversation conversation, int direction) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.alert_message_delete_conversation)
                                .setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO: simply update this one conversation
                                        conversationsList.getAdapter().notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton(R.string.alert_button_delete, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        conversation.delete(LayerClient.DeletionMode.ALL_PARTICIPANTS);
                                    }
                                }).show();
                    }
                });

        message.findViewById(R.id.floating_action_button)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), MessagesListActivity.class));
                    }
                });
        return message;
    }

    /**
     * This method is used to update the users score after a change which is decided in the userScore method
     * @param user
     */
    private void updateScore (User user){
        ServerRequest request = new ServerRequest(getActivity());
        request.editScoreInBackground(user);
        storeLocalUserData = new StoreLocalUserData(getContext());
        storeLocalUserData.storeUserData(user);
    }

    /**
     * This is the method that updates the user score based on the number of their current conversation and messages.
     * On message deletion it is assumed that the user is no longer active for that chat therefore the user score will
     * depend on how many conversations the user is having but the points they have earned will never be reduced
     * if they delete all their messages
     */
    private void userScore(){
        User user = storeLocalUserData.getLoggedInUser();
        int score = user.score;
        List<Conversation> conversations = MainActivity.getLayerClient().getConversations();
        int totalMessages = 0;
        for (Conversation conversation : conversations) {
            totalMessages += conversation.getTotalMessageCount();
        }
        int uScore = (conversations.size()*5)+ totalMessages;
        if(score <= uScore){
            score = uScore;
            user = new User(user.username, user.userid, user.email, score, user.bitmap);
            updateScore(user);
        }
        else{
            //do nothing
        }
    }

    /**
     * Creates a settings menu button for the message fragment page
     * @param menu
     * @param inflater
     */
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.message_menu, menu);
        messageSettings = menu.findItem(R.id.action_settings);
    }

    /**
     * on click on the settings button it will start the app settings activity
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), AppSettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}