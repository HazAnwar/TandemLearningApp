package com.alpha.tandemexchange;

import android.graphics.Bitmap;

import static org.mockito.Mockito.*;
import org.mockito.Mock;

import android.content.SharedPreferences;


public class UserTest extends MainActivityTest{

    //data for test user
    User testuser = new User("Username", "Forename", "Surname", "password", "email@kcl.ac.uk");

    StoreLocalUserData localdata = new StoreLocalUserData(getActivity().getApplicationContext());

    private static final String TEST_FNAME = "Forename";

    private static final String TEST_SNAME = "Surname";

    private static final String TEST_EMAIL = "email@kcl.ac.uk";

    @Mock
    SharedPreferences mMockSharedPreferences;

    @Mock
    SharedPreferences mMockBrokenSharedPreferences;

    @Mock
    SharedPreferences.Editor mMockEditor;

    @Mock
    SharedPreferences.Editor mMockBrokenEditor;

    public void testUserData(){

        localdata.storeUserData(testuser);
        assertNotNull(testuser);
        assertNotNull(localdata);
    }

    public void testProfilePic() {
        Bitmap bm = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Bitmap bitmap = testuser.bitmap;
        assertEquals(bm, bitmap);
        assertEquals(100, bitmap.getWidth());
        assertEquals(100, bitmap.getHeight());
    }

    public void testSharedPreference(){
        testuser.forename = TEST_FNAME;
        testuser.surname = TEST_SNAME;
        testuser.email = TEST_EMAIL;

        localdata.storeUserData(testuser);

        assertNotNull(localdata);

        when(mMockBrokenEditor.commit()).thenReturn(false);
        when(mMockBrokenSharedPreferences.edit()).thenReturn(mMockBrokenEditor);

        when(mMockEditor.commit()).thenReturn(true);
        when(mMockSharedPreferences.edit()).thenReturn(mMockEditor);

    }

}