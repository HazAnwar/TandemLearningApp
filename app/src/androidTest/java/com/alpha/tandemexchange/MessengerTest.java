package com.alpha.tandemexchange;

import com.layer.atlas.AtlasAddressBar;
import com.layer.atlas.AtlasMessagesRecyclerView;

public class MessengerTest extends MainActivityTest{

    MessagesListActivity messageactivity = new MessagesListActivity();

    public void testMessageBar(){
        AtlasAddressBar addressbar = messageactivity.mAddressBar;
        assertNotNull(addressbar);
    }

    public void testMessage(){
        AtlasMessagesRecyclerView messagerecycler = messageactivity.mMessagesList;
        assertNotNull(messagerecycler);

    }
}