package com.alpha.tandemexchange;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Class to store the user data from the server
 */
public class StoreLocalUserData {

    public static final String SP_NAME = "userDetails";

    /**
     * Field in which the user data will be stored
     */
    SharedPreferences userLocalDatabase;

    /**
     * Instantiates the class and sets the content to SharedPreferences
     * @param context is the class context
     */
    public StoreLocalUserData(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    /**
     * Method to store the data of the user in SharedPreferences
     * @param user is the user of whom data is being stored
     */
    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();

        if(user.bitmap ==null){
            spEditor.putString("bitmap", null);
        } else {
            spEditor.putString("bitmap", encodeTobase64(user.bitmap));
        }

        spEditor.putInt("userid", user.userid);
        spEditor.putString("username", user.username);
        spEditor.putString("forename", user.forename);
        spEditor.putString("surname", user.surname);
        spEditor.putString("password", user.password);
        spEditor.putString("email", user.email);
        spEditor.putInt("score", user.score);
        spEditor.putString("aboutme", user.aboutme);
        spEditor.putString("know", user.languageKnow);
        spEditor.putString("learn", user.languageLearn);
        spEditor.commit();
    }

    /**
     * Method to get the data of the logged in user, which is stored in SharedPreferences
     * @return returns the logged in user and data
     */
    public User getLoggedInUser(){
        Bitmap bitmap;
        if(userLocalDatabase.getString("bitmap", "") == null){
            bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.profile);
        } else{
            bitmap = decodeBase64(userLocalDatabase.getString("bitmap", ""));
        }
        int userid = userLocalDatabase.getInt("userid", 0);
        String username  = userLocalDatabase.getString("username", "");
        String forename  = userLocalDatabase.getString("forename", "");
        String surname  = userLocalDatabase.getString("surname", "");
        String password  = userLocalDatabase.getString("password", "");
        String email  = userLocalDatabase.getString("email", "");
        int score = userLocalDatabase.getInt("score", 0);
        String aboutme = userLocalDatabase.getString("aboutme", "");
        String know = userLocalDatabase.getString("know", "");
        String learn = userLocalDatabase.getString("learn", "");

        User storedUser  = new User(bitmap, username, userid, forename, surname, password, email, score, aboutme, know, learn);
        return storedUser;
    }

    /**
     * Method to set the user as logged in
     * @param loggedIn whether the user is logged in or not
     */
    public void setLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    /**
     * Method to check whether the user is logged in
     * @return returns whether the user is logged in or not
     */
    public boolean isUserLoggedIn(){
        return userLocalDatabase.getBoolean("loggedIn", false) == true;
    }

    /**
     * Method to clear the user data from the SharedPreferences
     */
    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

    /**
     * Method to encode the profile picture into a String from a Bitmap image
     * @param image is the profile image to be encoded
     * @return returns the String of the encoded image
     */
    public static String encodeTobase64(Bitmap image) {
        Bitmap bitmap = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    /**
     * Method to decode the profile into a Bitmap image from a String
     * @param input is the String to be decoded
     * @return returns the decoded Bitmap image
     */
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
