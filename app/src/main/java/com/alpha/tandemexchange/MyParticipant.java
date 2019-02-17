package com.alpha.tandemexchange;

import android.net.Uri;

import com.layer.atlas.provider.Participant;

/**
 * Details of the logged in user which is used by layer
 */
public class MyParticipant implements Participant {
    private String mId;
    private String mName;
    private Uri mAvatarUrl;

    /**
     * Accessor (getter) method used to get the ID
     * @return the ID
     */
    @Override
    public String getId() {
        return mId;
    }

    /**
     * Setter method used to set the ID
     */
    public void setId(String id) {
        mId = id;
    }

    /**
     * Accessor (getter) method used to get the name
     * @return the name
     */
    @Override
    public String getName() {
        return mName;
    }

    /**
     * Setter method used to set the name
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Accessor (getter) method used to get the messaging picture url
     * @return the url
     */
    public Uri getAvatarUrl() {
        return mAvatarUrl;
    }

    /**
     * Setter method used to set the Avatar url
     */
    public void setAvatarUrl(Uri avatarUrl) {
        mAvatarUrl = avatarUrl;
    }

    /**
     *
     * @param another
     * @return
     */
    @Override
    public int compareTo(Participant another) {
        return getName().toLowerCase().compareTo(another.getName().toUpperCase());
    }
}
