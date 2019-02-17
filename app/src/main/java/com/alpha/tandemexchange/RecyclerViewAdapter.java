package com.alpha.tandemexchange;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.layer.atlas.provider.Participant;
import com.layer.atlas.provider.ParticipantProvider;
import com.layer.sdk.exceptions.LayerConversationException;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.ConversationOptions;
import java.util.List;

/**
 * Class is a custom RecyclerView Adapter to display the users data. The data for each user is displayed
 * in a CardView, which is then populated with the required user data
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    /**
     * List of users with their data
     */
    private List<User> users;

    MessagesListActivity messagesListActivity;

    /**
     * Instantiates the class
     * @param users is the list of users
     */
    public RecyclerViewAdapter(List<User> users) {
        this.users = users;
    }

    /**
     * Method to return the number of users in the list
     * @return returns the size of the users list
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * Sets the data in the ViewHolder
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final User user = users.get(i);
        viewHolder.name.setText(user.forename + " " + user.surname);
        viewHolder.score.setText(String.valueOf(user.score));
        viewHolder.aboutme.setText(user.aboutme);
        viewHolder.languageKnow.setText(" " + user.languageKnow);
        viewHolder.languageLearn.setText(" " + user.languageLearn);
        setAndCrop(viewHolder.searchProfilePic, user.bitmap);

        StateListAnimator stateListAnimator = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            stateListAnimator = AnimatorInflater.loadStateListAnimator(viewHolder.cardView.getContext(), R.anim.cardview_touch);
            viewHolder.cardView.setStateListAnimator(stateListAnimator);
        }
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView fullName, username, score, languageKnow, languageLearn, aboutMe;
                ImageView profilePicture;

                Dialog profileDialog = new Dialog(v.getContext());
                profileDialog.setContentView(R.layout.layout_profile);
                profileDialog.setCancelable(false);
                profileDialog.setCanceledOnTouchOutside(true);

                username = (TextView) profileDialog.findViewById(R.id.userUsername);
                username.setText(user.username);

                fullName = (TextView) profileDialog.findViewById(R.id.userFullName);
                fullName.setText(user.forename + " " + user.surname);

                aboutMe = (TextView) profileDialog.findViewById(R.id.userAbout);
                aboutMe.setText(user.aboutme);

                languageKnow = (TextView) profileDialog.findViewById(R.id.userKnow);
                languageKnow.setText(user.languageKnow);

                languageLearn = (TextView) profileDialog.findViewById(R.id.userLearn);
                languageLearn.setText(user.languageLearn);

                score = (TextView) profileDialog.findViewById(R.id.userScore);
                score.setText(String.valueOf(user.score));

                profilePicture = (ImageView) profileDialog.findViewById(R.id.userProfilePicture);
                profilePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Dialog messageDialog = new Dialog(view.getContext());
                        messageDialog.setTitle("Send a Message to " + user.forename);
                        messageDialog.setContentView(R.layout.layout_message);
                        messageDialog.setCanceledOnTouchOutside(true);

                        TextView howToMessage = (TextView) messageDialog.findViewById(R.id.howToMessage);
                        howToMessage.setText("To send a message to " + user.forename + ", please go to the messages tab and " +
                                "press the add icon to create a new message and put his/her username (" + user.username + ") in to send them a message.");

                        messageDialog.show();

                        messagesListActivity = new MessagesListActivity();
                        //messagesListActivity.openChat(user);
                    }
                });

                setAndCrop(profilePicture, user.bitmap);
                profileDialog.show();
                Window window = profileDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });
    }

    /**
     * Creates the ViewHolder and inflates it
     * @param viewGroup
     * @param i
     * @return returns the ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search, viewGroup, false);
        return new ViewHolder(itemView);
    }

    /**
     * Class to populate the RecyclerView ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView name, aboutme, score, languageKnow, languageLearn;
        protected ImageView searchProfilePic;
        protected CardView cardView;

        /**
         * Populates the View with the GUI elements
         * @param v
         */
        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.searchName);
            score = (TextView) v.findViewById(R.id.searchScore);
            aboutme = (TextView) v.findViewById(R.id.searchBiography);
            cardView = (CardView) v.findViewById(R.id.cardSearchList);
            languageKnow = (TextView) v.findViewById(R.id.searchLanguageKnow);
            languageLearn = (TextView) v.findViewById(R.id.searchLanguageLearn);
            searchProfilePic = (ImageView) v.findViewById(R.id.searchProfilePic);
            TextView textView1 = (TextView) v.findViewById(R.id.textView1);
            TextView textView2 = (TextView) v.findViewById(R.id.textView2);

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                name.setTextColor(Color.GRAY);
                aboutme.setTextColor(Color.GRAY);
                languageKnow.setTextColor(Color.GRAY);
                languageLearn.setTextColor(Color.GRAY);
                textView1.setTextColor(Color.GRAY);
                textView2.setTextColor(Color.GRAY);
            }
        }
    }

    /**
     * Method to crop and displays the user's profile picture correctly from a Bitmap
     * @param imageView is the user's profile picture
     * @param bitmap is the Bitmap from which the profile picture is extracted and then displayed
     */
    public void setAndCrop(ImageView imageView, Bitmap bitmap){
        if (bitmap != null){
            int width  = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width != height){
                int newWidth = (height > width) ? width : height;
                int newHeight = (height > width)? height - (height - width) : height;
                int cropWidth = (width - height) / 2;
                cropWidth = (cropWidth < 0)? 0: cropWidth;
                int cropHeight = (height - width) / 2;
                cropHeight = (cropHeight < 0)? 0: cropHeight;
                Bitmap outputImg = Bitmap.createBitmap(bitmap, cropWidth, cropHeight, newWidth, newHeight);
                imageView.setImageBitmap(outputImg);
            } else {
                imageView.setImageBitmap(bitmap);
            }
        } else {
            imageView.setImageResource(R.drawable.profile);
        }

    }

}