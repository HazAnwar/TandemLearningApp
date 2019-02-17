package com.alpha.tandemexchange;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.method.KeyListener;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.layer.sdk.messaging.Conversation;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This creates the user's profile fragment.
 * The fragment contains the all the user's details.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    // An array of all the languages that may be selected in the 'UserLearn" and 'UserKnow' spinners

    String[] languageList = {"Select Language", "Arabic", "Bengali", "Bulgarian", "Catalan", "Dari", "Dutch",
            "English", "French", "German", "Greek", "Gujarati", "Hebrew", "Hindi", "Italian",
            "Japanese", "Korean", "Macedonian", "Mandarin", "Punjabi", "Pashto", "Persian (Farsi)",
            "Polish", "Portuguese", "Portuguese (Brazilian)", "Romanian", "Russian", "Serbian", "Spanish", "Swedish",
            "Turkish", "Urdu"};
    EditText profileForename, profileSurname, profileUsername, profileBiography, picName;
    private static final String SERVER = "http://nikiltest.site88.net/";
    MenuItem cancelEditProfile, saveEditProfile, editProfile;
    String stringForename, stringSurname, stringBiography;
    int indexUserKnow, indexUserLearn, userLevelScore;
    String editForename, editSurname, editBiography;
    private static final int IMAGE_SELECTED = 1;
    Spinner spinnerUserKnow, spinnerUserLearn;
    StoreLocalUserData storeLocalUserData;
    KeyListener keyListener;
    Button uploadImageBtn;
    ImageView profilePic;
    Boolean errorChecker;
    TextView userScore, usernameTextview;
    ImageView editPic;

    /**
     * Creates an instance of the profile fragment, setting up the GUI and data
     * @param inflater Inflates the profile fragment
     * @param container
     * @param savedInstanceState
     * @return returns the profile fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.fragment_profile, null);
        setHasOptionsMenu(true);

        profileForename = (EditText) profileView.findViewById(R.id.profileForename);
        profileSurname = (EditText) profileView.findViewById(R.id.profileSurname);
        profileBiography = (EditText) profileView.findViewById(R.id.aboutUser);
        profilePic = (ImageView) profileView.findViewById(R.id.profilePic);
        editPic = (ImageView) profileView.findViewById(R.id.editPic);
        uploadImageBtn = (Button) profileView.findViewById(R.id.uploadImageBtn);
        picName = (EditText) profileView.findViewById(R.id.picName);
        userScore = (TextView) profileView.findViewById(R.id.userScore);
        usernameTextview = (TextView) profileView.findViewById(R.id.usernameTextview);
        spinnerUserKnow = (Spinner) profileView.findViewById(R.id.userKnowSpinner);
        spinnerUserLearn = (Spinner) profileView.findViewById(R.id.userLearnSpinner);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            profileForename.setTextColor(Color.GRAY);
            profileSurname.setTextColor(Color.GRAY);
            profileUsername.setTextColor(Color.GRAY);
            profileBiography.setTextColor(Color.GRAY);
            picName.setTextColor(Color.GRAY);
            userScore.setTextColor(Color.GRAY);
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, languageList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserKnow.setAdapter(spinnerArrayAdapter);
        spinnerUserLearn.setAdapter(spinnerArrayAdapter);
        editPic.setOnClickListener(this);
        uploadImageBtn.setOnClickListener(this);
        keyListener = profileForename.getKeyListener();

        storeLocalUserData = new StoreLocalUserData(getActivity());

        displayUserData();
        imageName();
        disableEdit();
        return profileView;
    }

    /**
     * Inflates the menu; this adds items to the action bar.
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        cancelEditProfile = menu.findItem(R.id.cancelEditProfile);
        saveEditProfile = menu.findItem(R.id.saveEditProfile);
        editProfile = menu.findItem(R.id.editProfile);
    }

    /**
     * Sets the behaviour for the edit button, cancel button and save the changes button.
     * The edit button makes the changes and sets turns off its visibility.
     * The cancel button produces a message stating that the changes were not made, sets the previous
     * information and sets the save and cancel buttons to invisible.
     * The save button shows a message if there is an error and saves the changes.
     * It also sets sets the save and cancel buttons to invisible.
     * @param item is the view that has been clicked
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editProfile:
                enableEdit();
                editProfile.setVisible(false);
                saveEditProfile.setVisible(true);
                cancelEditProfile.setVisible(true);
                return true;

            case R.id.cancelEditProfile:
                Toast.makeText(getContext(), "Reverting any unsaved changes...", Toast.LENGTH_SHORT).show();
                profileBiography.setText(stringBiography);
                profileForename.setText(stringForename);
                profileSurname.setText(stringSurname);
                spinnerUserKnow.setSelection(indexUserKnow);
                spinnerUserLearn.setSelection(indexUserLearn);
                disableEdit();
                editProfile.setVisible(true);
                saveEditProfile.setVisible(false);
                cancelEditProfile.setVisible(false);
                return true;

            case R.id.saveEditProfile:
                saveEdit();
                if (errorChecker){
                    Toast.makeText(getContext(), "Please fix errors before continuing...", Toast.LENGTH_SHORT).show();
                } else {
                    disableEdit();
                    editProfile.setVisible(true);
                    saveEditProfile.setVisible(false);
                    cancelEditProfile.setVisible(false);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method sets the user's details and displays it on the profile fragment
     */
    private void displayUserData(){
        User user = storeLocalUserData.getLoggedInUser();
        usernameTextview.setText(user.username);
        profileForename.setText(user.forename);
        stringForename = user.forename;
        profileSurname.setText(user.surname);
        stringSurname = user.surname;
        profileBiography.setText(user.aboutme);
        stringBiography = user.aboutme;


        if (user.bitmap != null) {
            setAndCrop(profilePic, user.bitmap);
        } else {
            profilePic.setImageResource(R.drawable.profile);
        }

        indexUserKnow = getValueOfSpinnerFromDatabase(spinnerUserKnow, user.languageKnow.toString());
        spinnerUserKnow.setSelection(indexUserKnow);
        indexUserLearn = getValueOfSpinnerFromDatabase(spinnerUserLearn, user.languageLearn.toString());
        spinnerUserLearn.setSelection(indexUserLearn);
        int score = user.score;
        userScore.setText("Score: "+ String.valueOf(score));
    }

    /**
     * This method sets a name to the user's profile picture
     */
    private void imageName(){
        User user = storeLocalUserData.getLoggedInUser();
        String outputInfo;
        outputInfo = user.userid+"";
        picName.setText(outputInfo);
    }

    /**
     * Counts the index of the language selected
     * @param spinner
     * @param string
     * @return the index selected
     */
    private int getValueOfSpinnerFromDatabase(Spinner spinner, String string)
    {
        int index=0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(string)){
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * This method disables the ability to edit the user profile unless the edit button is selected
     */
    private void disableEdit(){
        profileForename.setEnabled(false);
        profileForename.setKeyListener(null);
        profileSurname.setEnabled(false);
        profileSurname.setKeyListener(null);
        profileBiography.setEnabled(false);
        profileBiography.setKeyListener(null);
        spinnerUserKnow.setEnabled(false);
        spinnerUserLearn.setEnabled(false);
        editPic.setEnabled(false);
        editPic.setVisibility(View.INVISIBLE);
        uploadImageBtn.setVisibility(View.INVISIBLE);
    }

    /**
     * This is a method to enable editing of certain fields to update the user data
     */
    private void enableEdit(){
        profileForename.setEnabled(true);
        profileForename.setKeyListener(keyListener);
        profileSurname.setEnabled(true);
        profileSurname.setKeyListener(keyListener);
        profileBiography.setEnabled(true);
        profileBiography.setKeyListener(keyListener);
        spinnerUserKnow.setEnabled(true);
        spinnerUserLearn.setEnabled(true);
        editPic.setEnabled(true);
        editPic.setVisibility(View.VISIBLE);
    }

    /**
     * This method is used to save the changes the user has made to their profile
     */
    private void saveEdit(){
        int indexLearn, indexKnow;
        indexKnow = spinnerUserKnow.getSelectedItemPosition();
        indexLearn = spinnerUserLearn.getSelectedItemPosition();
        if (profileBiography.length() > 100){
            errorChecker = true;
            profileBiography.setError("Too long, please shorten. Max 100 characters.");
        } else if (profileForename.length() < 2) {
            errorChecker = true;
            profileForename.setError("Forename is too short, please enter name correctly!");
        } else if (profileSurname.length() < 2){
            errorChecker = true;
            profileSurname.setError("Surname is too short, please enter name correctly!");
        } else if ((indexKnow == 0) || (indexLearn == 0)) {
            errorChecker = true;
            Toast.makeText(getContext(), "Please select a language...", Toast.LENGTH_SHORT).show();
        } else {
            errorChecker = false;
            editForename = profileForename.getText().toString();
            editSurname = profileSurname.getText().toString();
            editBiography = profileBiography.getText().toString();
            indexUserKnow = spinnerUserKnow.getSelectedItemPosition();
            indexUserLearn = spinnerUserLearn.getSelectedItemPosition();

            String languageKnow = spinnerUserKnow.getSelectedItem().toString();
            String languageLearn = spinnerUserLearn.getSelectedItem().toString();

            // save changes to the server
            User user = storeLocalUserData.getLoggedInUser();
            int userId = user.userid;
            int userScore = user.score;
            String userEmail = user.email;
            Bitmap bitmap = user.bitmap;
            user = new User(bitmap, userId, editForename, editSurname, userEmail, userScore, editBiography, languageKnow, languageLearn);
            updateUser(user);
        }
    }

    /**
     * This method is used to update user data in sharedPreferences
     * @param user
     */
    private void updateUser(User user){
        ServerRequest request = new ServerRequest(getActivity());
        request.editUserDataInBackground(user);
        storeLocalUserData = new StoreLocalUserData(getContext());
        storeLocalUserData.storeUserData(user);
    }

    /**
     * This method opens gallery through intent when button is clicked to upload a profile pic.
     * The user is notified when image upload is in progress
     * @param v is the view that is clicked
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.editPic){
            Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGallery, IMAGE_SELECTED);
            uploadImageBtn.setVisibility(View.VISIBLE);
        }
        else if (v.getId() == R.id.uploadImageBtn){
            Bitmap picture = ((BitmapDrawable)profilePic.getDrawable()).getBitmap();
            Toast.makeText(getContext(),"Profile Picture Uploading...", Toast.LENGTH_SHORT).show();
            new UploadProfilePic(picture,picName.getText().toString()).execute();
        }
    }

    /**
     * This method ??????????????
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_SELECTED && resultCode == Activity.RESULT_OK && data != null){
            Uri chosenImage = data.getData();
            profilePic.setImageURI(chosenImage);
        }
    }

    /**
     * This private class is used to upload the image the user has selected to the online server
     */
    private class UploadProfilePic extends AsyncTask <Void,Void,Void> {
        Bitmap picture;
        String pictureName;
        String encode;

        public  UploadProfilePic(Bitmap picture, String pictureName){
            this.picture = picture;
            this.pictureName = pictureName;
        }

        /**
         * This method converts the image to a Base64 string before uploading to the online server
         * @param params
         * @return null
         */
        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream bytePicture = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.JPEG,100,bytePicture);
            encode = Base64.encodeToString(bytePicture.toByteArray(),Base64.DEFAULT);
            ArrayList<NameValuePair> picWithName = new ArrayList<>();
            picWithName.add(new BasicNameValuePair("picture", encode));
            picWithName.add(new BasicNameValuePair("pictureName", pictureName));
            HttpParams request = param();
            HttpClient c = new DefaultHttpClient(request);
            HttpPost p = new HttpPost(SERVER + "testPic.php");
            try {
                p.setEntity(new UrlEncodedFormEntity(picWithName));
                c.execute(p);
            }catch (Exception exception){
                exception.printStackTrace();
            }
            return null;
        }

        /**
         * Informs the user that their profile picture has successfully uploaded
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getContext(),"Your new profile picture has successfully uploaded!", Toast.LENGTH_SHORT).show();
        }

        /**
         * This method makes a httpParams request and sets a timeout for the connection
         * @return the httpParams request
         */
        private HttpParams param (){
            HttpParams request = new BasicHttpParams();
            HttpConnectionParams.setSoTimeout(request, 1000 * 25);
            HttpConnectionParams.setConnectionTimeout(request, 1000 * 25);
            return request;
        }
    }

    /**
     * Crops the picture to a square and scales it to fit into the ImageView
     * @param imageView is the imageView that contains the user's profile picture
     * @param bitmap is the profile picture for the user profile
     */
    public void setAndCrop(ImageView imageView, Bitmap bitmap){
        Bitmap outputImg;
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width != height){
            int newWidth = (height > width) ? width : height;
            int newHeight = (height > width)? height - (height - width) : height;
            int cropWidth = (width - height) / 2;
            cropWidth = (cropWidth < 0)? 0: cropWidth;
            int cropHeight = (height - width) / 2;
            cropHeight = (cropHeight < 0)? 0: cropHeight;
            outputImg = Bitmap.createBitmap(bitmap, cropWidth, cropHeight, newWidth, newHeight);
        } else {
            outputImg = bitmap;
        }
        imageView.setImageBitmap(outputImg);
    }

}

