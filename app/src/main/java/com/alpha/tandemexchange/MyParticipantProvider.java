package com.alpha.tandemexchange;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.layer.atlas.provider.Participant;
import com.layer.atlas.provider.ParticipantProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.alpha.tandemexchange.util.Util.streamToString;

/**
 * Used to add and load current participants and save the current map of participants
 * to sharedPreferences
 */

public class MyParticipantProvider implements ParticipantProvider {
    private final Context mContext;
    private String mLayerAppIdLastPathSegment;
    private final Queue<ParticipantListener> mParticipantListeners = new ConcurrentLinkedQueue<>();
    private final Map<String, MyParticipant> mParticipantMap = new HashMap<>();
    private final AtomicBoolean mFetching = new AtomicBoolean(false);

    public MyParticipantProvider(Context context) {
        mContext = context.getApplicationContext();
    }

    public MyParticipantProvider setLayerAppId(String layerAppId) {
        if (layerAppId.contains("/")) {
            mLayerAppIdLastPathSegment = Uri.parse(layerAppId).getLastPathSegment();
        } else {
            mLayerAppIdLastPathSegment = layerAppId;
        }
        load();
        fetchParticipants();
        return this;
    }


    /**
     * Atlas ParticipntProvider
     * @param filter The filter to apply to Participants
     * @param result The Map to operate on
     * @return
     */
    @Override
    public Map<String, Participant> getMatchingParticipants(String filter, Map<String, Participant> result) {
        if (result == null) {
            result = new HashMap<String, Participant>();
        }

        synchronized (mParticipantMap) {
            // With no filter, return all Participants
            if (filter == null) {
                result.putAll(mParticipantMap);
                return result;
            }

            // Filter participants by substring matching first- and last- names
            for (MyParticipant p : mParticipantMap.values()) {
                boolean matches = false;
                if (p.getName() != null && p.getName().toLowerCase().contains(filter))
                    matches = true;
                if (matches) {
                    result.put(p.getId(), p);
                } else {
                    result.remove(p.getId());
                }
            }
            return result;
        }
    }

    /**
     * Accessor (getter) method used to access the participant
     * @param userId
     * @return
     */
    @Override
    public Participant getParticipant(String userId) {
        synchronized (mParticipantMap) {
            MyParticipant participant = mParticipantMap.get(userId);
            if (participant != null) return participant;
            fetchParticipants();
            return null;
        }
    }

    /**
     * Adds the provided Participants to this ParticipantProvider, saves the participants, and
     * returns the list of added participant IDs.
     */
    private MyParticipantProvider setParticipants(Collection<MyParticipant> participants) {
        List<String> newParticipantIds = new ArrayList<>(participants.size());
        synchronized (mParticipantMap) {
            for (MyParticipant participant : participants) {
                String participantId = participant.getId();
                if (!mParticipantMap.containsKey(participantId))
                    newParticipantIds.add(participantId);
                mParticipantMap.put(participantId, participant);
            }
            save();
        }
        alertParticipantsUpdated(newParticipantIds);
        return this;
    }


    /**
     * Loads additional participants from SharedPreferences
     */
    private boolean load() {
        synchronized (mParticipantMap) {
            String jsonString = mContext.getSharedPreferences("participants", Context.MODE_PRIVATE).getString("json", null);
            if (jsonString == null) return false;

            try {
                for (MyParticipant participant : participantsFromJson(new JSONArray(jsonString))) {
                    mParticipantMap.put(participant.getId(), participant);
                }
                return true;
            } catch (JSONException e) {
            //do nothing
            }
            return false;
        }
    }

    /**
     * Saves the current map of participants to SharedPreferences
     */
    private boolean save() {
        synchronized (mParticipantMap) {
            try {
                mContext.getSharedPreferences("participants", Context.MODE_PRIVATE).edit()
                        .putString("json", participantsToJson(mParticipantMap.values()).toString())
                        .commit();
                return true;
            } catch (JSONException e) {
            //do nothing
            }
        }
        return false;
    }


    /**
     * Fetches the list of participants that have signed into the application
      * @return
     */
    private MyParticipantProvider fetchParticipants() {
        if (!mFetching.compareAndSet(false, true)) return this;
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                try {
                    // Post request
                    String url = "https://layer-identity-provider.herokuapp.com/apps/" + mLayerAppIdLastPathSegment + "/atlas_identities";
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(false);
                    connection.setRequestMethod("GET");
                    connection.addRequestProperty("Content-Type", "application/json");
                    connection.addRequestProperty("Accept", "application/json");
                    connection.addRequestProperty("X_LAYER_APP_ID", mLayerAppIdLastPathSegment);

                    // Handle failure
                    int statusCode = connection.getResponseCode();
                    if (statusCode != HttpURLConnection.HTTP_OK && statusCode != HttpURLConnection.HTTP_CREATED) {
                        return null;
                    }

                    // Parse response
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    String result = streamToString(in);
                    in.close();
                    connection.disconnect();
                    JSONArray json = new JSONArray(result);
                    setParticipants(participantsFromJson(json));
                } catch (Exception e) {
                    //do nothing
                } finally {
                    mFetching.set(false);
                }
                return null;
            }
        }.execute();
        return this;
    }


    /**
     * Gets the list of participants
     * @param participantArray
     * @return
     * @throws JSONException
     */
    private static List<MyParticipant> participantsFromJson(JSONArray participantArray) throws JSONException {
        List<MyParticipant> participants = new ArrayList<>(participantArray.length());
        for (int i = 0; i < participantArray.length(); i++) {
            JSONObject participantObject = participantArray.getJSONObject(i);
            MyParticipant participant = new MyParticipant();
            participant.setId(participantObject.optString("id"));
            participant.setName(participantObject.optString("name"));
            participant.setAvatarUrl(null);
            participants.add(participant);
        }
        return participants;
    }

    /**
     *
     * @param participants
     * @return participantsArray
     * @throws JSONException
     */
    private static JSONArray participantsToJson(Collection<MyParticipant> participants) throws JSONException {
        JSONArray participantsArray = new JSONArray();
        for (MyParticipant participant : participants) {
            JSONObject participantObject = new JSONObject();
            participantObject.put("id", participant.getId());
            participantObject.put("name", participant.getName());
            participantsArray.put(participantObject);
        }
        return participantsArray;
    }

    /**
     * Adds the participant listener
     * @param participantListener
     * @return
     */
    private MyParticipantProvider registerParticipantListener(ParticipantListener participantListener) {
        if (!mParticipantListeners.contains(participantListener)) {
            mParticipantListeners.add(participantListener);
        }
        return this;
    }

    /**
     * Removes the participant listener
     * @param participantListener
     * @return
     */
    private MyParticipantProvider unregisterParticipantListener(ParticipantListener participantListener) {
        mParticipantListeners.remove(participantListener);
        return this;
    }

    /**
     * Updates the participant ID
     * @param updatedParticipantIds
     */
    private void alertParticipantsUpdated(Collection<String> updatedParticipantIds) {
        for (ParticipantListener listener : mParticipantListeners) {
            listener.onParticipantsUpdated(this, updatedParticipantIds);
        }
    }



    public interface ParticipantListener {
        void onParticipantsUpdated(MyParticipantProvider provider, Collection<String> updatedParticipantIds);
    }
}
