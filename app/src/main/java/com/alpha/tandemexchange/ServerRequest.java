package com.alpha.tandemexchange;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Class to connect to server and get the user data from the database
 */
public class ServerRequest extends MainActivity {

    Context maContext = null;
    Dialog waveDialog = null;
    ProgressDialog progressDialog = null;
    WaveLoadingView mWaveLoadingView = null;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://nikiltest.site88.net/";
    public ArrayList<User> allUsersList = new ArrayList<>();

    public ServerRequest(Context context){
        maContext = context;
    }

    public void storedUserDataInBackground(User user, GetUserCallBack userCallBack){
        new storeUserDataAsyncTask(user, userCallBack).execute();
    }

    /**
     * Method to execute the  AsyncTask
     * @param user is the user
     */
    public void editUserDataInBackground(User user){
        new editUserDataAsyncTask(user).execute();
    }

    /**
     * Method to execute the  AsyncTask
     * @param user is the user
     */
    public void editScoreInBackground(User user){
        new editScoreAsyncTask(user).execute();
    }

    /**
     * Method to execute the  AsyncTask
     * @param user is the user
     */
    public void changePasswordInBackground(User user){
        new changePasswordAsyncTask(user).execute();
    }

    /**
     * Method to execute the  AsyncTask
     * @param user is the user
     */
    public void fetchUserDataInBackground(User user, GetUserCallBack callBack){
        new fetchUserDataAsyncTask(user, callBack).execute();
    }

    /**
     * Method to execute the  AsyncTask
     */
    public void getAllUserDataAsyncTask(){
        new getAllUserDataAsyncTask().execute();
    }

    /**
     * Method to show a dialog (waveDialog) to indicate that the data is loading
     */
    private void showWaveDialog() {
        waveDialog = new Dialog(maContext, android.R.style.Theme_NoTitleBar_Fullscreen);
        waveDialog.setContentView(R.layout.layout_progress);
        waveDialog.setCancelable(false);
        waveDialog.show();
        mWaveLoadingView = (WaveLoadingView) waveDialog.findViewById(R.id.waveLoadingView);
    }

    /**
     * Method to dismiss waveDialog if it is showing. This method is called when the data has
     * finished loading
     */
    private void dismissWaveDialog() {
        if (waveDialog != null && waveDialog.isShowing()) {
            waveDialog.dismiss();
        }
        waveDialog = null;
        mWaveLoadingView = null;
    }

    /**
     * Method to show a dialog (progressDialog) of how data loading is progressing
     * @param title is the title of the dialog
     * @param message is the message of the dialog
     */
    private void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(maContext);
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    /**
     * Method to dismiss progressDialog if it is showing
     */
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    /**
     * When the activity is paused, progressDialog is dismissed
     */
    @Override
    public void onPause() {
        super.onPause();
        dismissProgressDialog();
        progressDialog = null;
    }

    /**
     * Inner class extending AsyncTask to save and store the user data when he/she has created a new account
     */
    public class storeUserDataAsyncTask extends AsyncTask<Void, Void, Void>{
        User user;
        GetUserCallBack userCallBack;

        /**
         * Constructor to create an instance of the class with the user who is going to have his/her data updated
         * @param user is the user who is going to have his/her data updated
         */
        public storeUserDataAsyncTask(User user, GetUserCallBack userCallBack){
            this.user = user;
            this.userCallBack = userCallBack;
        }

        /**
         * Shows a progress dialog while the data is being saved
         */
        protected void onPreExecute() {
            showProgressDialog("Saving", "We're just saving your data, please wait...");
        }

        /**
         * Saves the data of a newly registered user in the background
         * @param params
         * @return null
         */
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> sendData = new ArrayList<>();
            sendData.add(new BasicNameValuePair("username", user.username));
            sendData.add(new BasicNameValuePair("forename", user.forename));
            sendData.add(new BasicNameValuePair("surname", user.surname));
            sendData.add(new BasicNameValuePair("password", user.password));
            sendData.add(new BasicNameValuePair("email", user.email));

            HttpParams requestParam = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(requestParam, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(requestParam, CONNECTION_TIMEOUT);
            HttpClient client = new DefaultHttpClient(requestParam);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Register.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(sendData));
                client.execute(post);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Method to dismiss the progress dialog once the data has been saved
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            userCallBack.done(null);
            dismissProgressDialog();
        }
    }

    /**
     * Inner class extending AsyncTask to save and store the user profile data when he/she has edited his/her information
     */
    public class editUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;

        /**
         * Constructor to create an instance of the class with the user to be edited
         * @param user is the user who is going to have his/her data edited
         */
        public editUserDataAsyncTask(User user){
            this.user = user;
        }

        /**
         * Shows a progress dialog while the edited data is being saved
         */
        protected void onPreExecute() {
            showProgressDialog("Updating", "We're just saving your updates, please wait...");
        }

        /**
         * Saves the profile data in the background when a user has edited it
         * @param params
         * @return null
         */
        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> sendData = new ArrayList<>();
            sendData.add(new BasicNameValuePair("forename", user.forename));
            sendData.add(new BasicNameValuePair("surname", user.surname));
            sendData.add(new BasicNameValuePair("aboutme", user.aboutme));
            sendData.add(new BasicNameValuePair("know", user.languageKnow));
            sendData.add(new BasicNameValuePair("learn", user.languageLearn));
            sendData.add(new BasicNameValuePair("email", user.email));

            HttpParams requestParam = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(requestParam, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(requestParam, CONNECTION_TIMEOUT);
            HttpClient client = new DefaultHttpClient(requestParam);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "EditUserData.php");
            try{
                post.setEntity(new UrlEncodedFormEntity(sendData));
                client.execute(post);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Method to dismiss the progress dialog once the data has been saved
         * @param aVoid
         */
        @Override
        protected void onPostExecute(User aVoid) {
            super.onPostExecute(aVoid);
            dismissProgressDialog();
        }
    }

    /**
     * Inner class extending AsyncTask to save and store the update user score
     */
    public class editScoreAsyncTask extends AsyncTask<Void, Void, User> {
        User user;

        /**
         * Constructor to create an instance of the class with the user who is going to have his/her score updated
         * @param user is the user who is going to have his/her score updated
         */
        public editScoreAsyncTask(User user){
            this.user = user;
        }

        protected void onPreExecute() {
           // showProgressDialog("Fetching score");
        }

        /**
         * Saves the new password in the background when a user has changed it
         * @param params
         * @return null
         */
        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> sendData = new ArrayList<>();
            sendData.add(new BasicNameValuePair("score", user.score+""));
            sendData.add(new BasicNameValuePair("email", user.email));

            HttpParams requestParam = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(requestParam, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(requestParam, CONNECTION_TIMEOUT);
            HttpClient client = new DefaultHttpClient(requestParam);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "updateScore.php");
            try{
                post.setEntity(new UrlEncodedFormEntity(sendData));
                client.execute(post);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(User aVoid) {
            super.onPostExecute(aVoid);
            //dismissProgressDialog();
        }
    }

    /**
     * Inner class extending AsyncTask to save and store the new password if the user has changed it
     */
    public class changePasswordAsyncTask extends AsyncTask<Void, Void, User> {
        User user;

        /**
         * Constructor to create an instance of the class with the user who has changed his/her password
         * @param user is the user who has changed his/her password
         */
        public changePasswordAsyncTask(User user){
            this.user = user;
        }

        /**
         * Shows a progress dialog while the new password is being saved
         */
        protected void onPreExecute() {
            showProgressDialog("Updating Password", "We're just updating your password, please wait...");
        }

        /**
         * Saves the new password in the background when a user has changed it
         * @param params
         * @return null
         */
        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> sendData = new ArrayList<>();
            sendData.add(new BasicNameValuePair("email", user.email));
            sendData.add(new BasicNameValuePair("password", user.password));

            HttpParams requestParam = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(requestParam, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(requestParam, CONNECTION_TIMEOUT);
            HttpClient client = new DefaultHttpClient(requestParam);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "changePassword.php");
            try{
                post.setEntity(new UrlEncodedFormEntity(sendData));
                client.execute(post);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Method to dismiss the progress dialog once the new password has been saved
         * @param aVoid
         */
        @Override
        protected void onPostExecute(User aVoid) {
            super.onPostExecute(aVoid);
            dismissProgressDialog();
        }
    }

    /**
     * Inner class extending AsyncTask to fetch the user data when logging in
     */
    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallBack userCallBack;

        public fetchUserDataAsyncTask(User user, GetUserCallBack userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        /**
         * Shows a progress dialog while the data is loading and the user is being logged in
         */
        protected void onPreExecute() {
            showProgressDialog("Logging In", "We're just logging you in, please wait...");
        }

        /**
         * Fetches the user profile data when logging in
         * @param params
         * @return the user that has logged in
         */
        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> sendData = new ArrayList<>();
            sendData.add(new BasicNameValuePair("email", user.email));
            sendData.add(new BasicNameValuePair("password", user.password));
            HttpParams requestParam = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(requestParam, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(requestParam, CONNECTION_TIMEOUT);
            HttpClient client = new DefaultHttpClient(requestParam);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchUserData.php");
            User returnedUser = null;
            try{
                post.setEntity(new UrlEncodedFormEntity(sendData));
                HttpResponse response = client.execute(post);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject JObj = new JSONObject(result);
                if(JObj.length() == 0){
                    returnedUser=null;
                } else {
                    String username = JObj.getString("username");
                    String forename = JObj.getString("forename");
                    String surname = JObj.getString("surname");
                    int userid = JObj.getInt("userid");
                    String aboutme = JObj.getString("aboutme");
                    int score = JObj.getInt("score");
                    String know = JObj.getString("know");
                    String learn = JObj.getString("learn");
                    Bitmap bitmap = null;

                    String IMAGE_LINK = "http://nikiltest.site88.net/profilePictures/" + userid +".JPG";
                    try {
                        URLConnection c = new URL(IMAGE_LINK).openConnection();
                        c.setReadTimeout(1000 * 25);
                        c.setConnectTimeout(1000 * 25);
                        bitmap = BitmapFactory.decodeStream((InputStream) c.getContent());

                    } catch (Exception exception){
                        exception.printStackTrace();
                    }

                    returnedUser = new User(bitmap, username, userid, forename, surname, user.password, user.email, score, aboutme, know, learn);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return returnedUser;
        }

        /**
         * Method to dismiss the progress dialog once the data has loaded and the user is logged in
         * @param returnedUser is the user logging in
         */
        @Override
        protected void onPostExecute(User returnedUser) {
            super.onPostExecute(returnedUser);
            userCallBack.done(returnedUser);
            dismissProgressDialog();
        }
    }

    /**
     * Inner class extending AsyncTask to get all users and their data
     */
    public class getAllUserDataAsyncTask extends AsyncTask<Void, Integer, ArrayList> {
        public getAllUserDataAsyncTask() {
            Log.d("Downloading Data", "Currently downloading data for all users...");
        }

        protected void onPreExecute() {
            showWaveDialog();
        }

        /**
         * Method to show the progress of all the users loading
         * @param progress is the amount of progress of the users loading
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {
            mWaveLoadingView.setProgressValue(progress[0]);
        }

        /**
         * Fetches all the users profile data
         * @param params
         * @return a list with all the users and their data
         */
        @Override
        protected ArrayList doInBackground(Void... params) {
            int progVal = 0;
            publishProgress(progVal);
            HttpParams requestParam = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(requestParam, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(requestParam, CONNECTION_TIMEOUT);
            HttpClient client = new DefaultHttpClient(requestParam);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchAllUserData.php");
            User returnedUser = null;
            try{
                HttpResponse response = client.execute(post);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                JSONArray JArr = new JSONArray(result);
                for (int i = 0; i < JArr.length(); i++) {
                    JSONObject JObj = JArr.getJSONObject(i);
                    int userid = JObj.getInt("userid");
                    String username = JObj.getString("username");
                    String forename = JObj.getString("forename");
                    String surname = JObj.getString("surname");
                    String email = JObj.getString("email");
                    int score = JObj.getInt("score");
                    String aboutme = JObj.getString("aboutme");
                    String know = JObj.getString("know");
                    String learn = JObj.getString("learn");
                    Bitmap bitmap = null;
                    String IMAGE_LINK = "http://nikiltest.site88.net/profilePictures/" + userid +".JPG";
                    try {
                        URLConnection c = new URL(IMAGE_LINK).openConnection();
                        c.setReadTimeout(1000 * 25);
                        c.setConnectTimeout(1000 * 25);
                        bitmap = BitmapFactory.decodeStream((InputStream) c.getContent());
                    } catch (Exception exception){
                        exception.printStackTrace();
                    }
                    returnedUser = new User(bitmap, username, userid, forename, surname, email, score, aboutme, know, learn);
                    allUsersList.add(returnedUser);
                    progVal = progVal + (120/JArr.length());
                    publishProgress(progVal);
                }
                progVal = 100;
                publishProgress(progVal);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return allUsersList;
        }

        /**
         * Method to dismiss the progress dialog once the new password has been saved
         * @param arrayList contains all the users with their respective data
         */
        @Override
        protected void onPostExecute(ArrayList arrayList) {
            super.onPostExecute(arrayList);
            Collections.sort(arrayList, new Comparator<User>() {
                public int compare(User user1, User user2) {
                    return user1.getForename().compareTo(user2.getForename());
                }
            });
            SearchFragment.refreshRecyclerView(arrayList);
            SearchFragment.setUsersList(arrayList);
            dismissWaveDialog();
        }
    }

}
