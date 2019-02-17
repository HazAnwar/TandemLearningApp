package com.alpha.tandemexchange;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.alpha.tandemexchange.util.AuthenticationProvider;
import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.alpha.tandemexchange.util.Util.streamToString;

/**
 * This class is used to authenticate a user for messaging on the layer server when he log in to the app
 */
public class MyAuthenticationProvider implements AuthenticationProvider<MyAuthenticationProvider.Credentials> {
    private final SharedPreferences mPreferences;
    private Callback mCallback;

    public MyAuthenticationProvider(Context context) {
        mPreferences = context.getSharedPreferences(MyAuthenticationProvider.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * sets the credentials of the user an layer app id
     * @param credentials Credentials to cache.
     * @return
     */
    @Override
    public AuthenticationProvider<Credentials> setCredentials(Credentials credentials) {
        if (credentials == null) {
            mPreferences.edit().clear().commit();
            return this;
        }
        mPreferences.edit()
                .putString("appId", credentials.getLayerAppId())
                .putString("name", credentials.getUserName())
                .commit();
        return this;
    }

    /**
     * checks the credentials to see if it has Layer appId
     * @return
     */
    @Override
    public boolean hasCredentials() {
        return mPreferences.contains("appId");
    }

    /**
     * @param callback Callback to receive authentication success and failure.
     * @return
     */
    @Override
    public AuthenticationProvider<Credentials> setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }

    /**
     * 
     * @param layerClient
     * @param userId
     */
    @Override
    public void onAuthenticated(LayerClient layerClient, String userId) {
        layerClient.connect();
        if (mCallback != null) {
            mCallback.onSuccess(this, userId);
        }
    }

    /**
     * Deauthenticates the user
     * @param layerClient
     */
    @Override
    public void onDeauthenticated(LayerClient layerClient) {
    //do nothing
    }

    /**
     * Challenges the Layer authentication when you sign in
     * @param layerClient
     * @param nonce
     */
    @Override
    public void onAuthenticationChallenge(LayerClient layerClient, String nonce) {
        respondToChallenge(layerClient, nonce);
    }

    /**
     * Error message informing user that there has been an error in authentication
     * @param layerClient
     * @param e
     */
    @Override
    public void onAuthenticationError(LayerClient layerClient, LayerException e) {
        String error = "Failed to authenticate with Layer: " + e.getMessage();
        if (mCallback != null) {
            mCallback.onError(this, error);
        }
    }

    /**
     * Authenticates the user when they log in
     * @param layerClient
     * @param layerAppId
     * @param from
     * @return
     */
    @Override
    public boolean routeLogin(LayerClient layerClient, String layerAppId, Activity from) {
        if (layerAppId == null) {
            return true;
        }
        if (layerClient != null && !layerClient.isAuthenticated()) {
            if (hasCredentials()) {
                // Use the cached AuthenticationProvider credentials to authenticate with Layer.
                layerClient.authenticate();
            } else {
                // App ID, but no user: must authenticate.
                Intent intent = new Intent(from, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                from.startActivity(intent);
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param layerClient
     * @param nonce
     */
    private void respondToChallenge(LayerClient layerClient, String nonce) {
        Credentials credentials = new Credentials(mPreferences.getString("appId", null), mPreferences.getString("name", null));
        if (credentials.getUserName() == null || credentials.getLayerAppId() == null) {
            return;
        }

        try {
            // Post request
            String url = "https://layer-identity-provider.herokuapp.com/apps/" + credentials.getLayerAppId() + "/atlas_identities";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X_LAYER_APP_ID", credentials.getLayerAppId());

            // Credentials
            JSONObject rootObject = new JSONObject()
                    .put("nonce", nonce)
                    .put("name", credentials.getUserName());

            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            OutputStream os = connection.getOutputStream();
            os.write(rootObject.toString().getBytes("UTF-8"));
            os.close();

            // Handle failure
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK && statusCode != HttpURLConnection.HTTP_CREATED) {
                String error = String.format("Got status %d when requesting authentication for '%s' with nonce '%s' from '%s'",
                        statusCode, credentials.getUserName(), nonce, url);
                if (mCallback != null) mCallback.onError(this, error);
                return;
            }

            // Parse response
            InputStream in = new BufferedInputStream(connection.getInputStream());
            String result = streamToString(in);
            in.close();
            connection.disconnect();
            JSONObject json = new JSONObject(result);
            if (json.has("error")) {
                String error = json.getString("error");
                if (mCallback != null) mCallback.onError(this, error);
                return;
            }

            // Answer authentication challenge.
            String identityToken = json.optString("identity_token", null);
            layerClient.answerAuthenticationChallenge(identityToken);
        } catch (Exception e) {
            String error = "Error when authenticating with provider: " + e.getMessage();
            if (mCallback != null) mCallback.onError(this, error);
        }
    }


    public static class Credentials {
        private final String mLayerAppId;
        private final String mUserName;

        public Credentials(Uri layerAppId, String userName) {
            this(layerAppId == null ? null : layerAppId.getLastPathSegment(), userName);
        }

        public Credentials(String layerAppId, String userName) {
            mLayerAppId = layerAppId == null ? null : (layerAppId.contains("/") ? layerAppId.substring(layerAppId.lastIndexOf("/") + 1) : layerAppId);
            mUserName = userName;
        }

        public String getUserName() {
            return mUserName;
        }

        public String getLayerAppId() {
            return mLayerAppId;
        }
    }
}

