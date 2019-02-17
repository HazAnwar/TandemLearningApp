package com.alpha.tandemexchange;

import android.content.Context;

import com.alpha.tandemexchange.util.AuthenticationProvider;
import com.layer.atlas.provider.ParticipantProvider;
import com.layer.sdk.LayerClient;

/**
 * Class used to call in methods from Atlas
 */

public class Flavor implements MainActivity.Flavor {
    // Set your Layer App ID from your Layer developer dashboard to bypass the QR-Code scanner.
    private final static String LAYER_APP_ID = "layer:///apps/staging/fbbdfcc4-f1e7-11e5-a982-f13184055d8f";
    private final static String GCM_SENDER_ID = "748607264448";

    private String mLayerAppId;


    //==============================================================================================
    // Layer App ID (from LAYER_APP_ID constant or set by QR-Code scanning AppIdScanner Activity
    //==============================================================================================

    /**
     * Accessor (getter) method used to get the LayerAppID
     * @return the layer App Id
     */
    @Override
    public String getLayerAppId() {
        // In-memory cached App ID?
        if (mLayerAppId != null) {
            return mLayerAppId;
        }

        // Constant App ID?
        mLayerAppId = LAYER_APP_ID;

        // Saved App ID?
        String saved = MainActivity.getInstance()
                .getSharedPreferences("layerAppId", Context.MODE_PRIVATE)
                .getString("layerAppId", null);
        if (saved == null) return null;

        return mLayerAppId;
    }

    /**
     * Sets the current Layer App ID, and saves it for use next time (to bypass QR code scanner).
     * @param appId Layer App ID to use when generating a LayerClient.
     */
    protected static void setLayerAppId(String appId) {
        appId = appId.trim();
        MainActivity.getInstance().getSharedPreferences("layerAppId", Context.MODE_PRIVATE).edit()
                .putString("layerAppId", appId).commit();
    }


    //==============================================================================================
    // Generators
    //==============================================================================================

    /**
     * Generates the Layer Client
     * @param context
     * @param options
     * @return the Layer Client
     */
    @Override
    public LayerClient generateLayerClient(Context context, LayerClient.Options options) {
        // If no App ID is set yet, return `null`; we'll launch the AppIdScanner to get one.
        String appId = getLayerAppId();
        if (appId == null) return null;
        options.googleCloudMessagingSenderId(GCM_SENDER_ID);
        return LayerClient.newInstance(context, appId, options);
    }

    /**
     * Generates the Participant Provider
     * @param context
     * @param authenticationProvider
     * @return the Participant Provider
     */
    @Override
    public ParticipantProvider generateParticipantProvider(Context context, AuthenticationProvider authenticationProvider) {
        return new MyParticipantProvider(context).setLayerAppId(getLayerAppId());
    }

    /**
     * Generates Authentication Provider
     * @param context
     * @return the Authentication Provider
     */
    @Override
    public AuthenticationProvider generateAuthenticationProvider(Context context) {
        return new MyAuthenticationProvider(context);
    }
}