package com.alpha.tandemexchange;

import android.net.http.AndroidHttpClient;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Spinner;
import android.widget.Toolbar;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.params.HttpParams;
import org.mockito.internal.stubbing.answers.ThrowsException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.IOException;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    Toolbar mainToolbar;

    public Spinner testSpinner1;
    public Spinner testSpinner2;

    ProfileFragment profilefragment = new ProfileFragment();

    public static final int INITIAL_POSITION = 0;
    public final int FINAL_POSITION = testSpinner1.getAdapter().getCount();

    String expected1;
    String actual1;
    int position1;

    String expected2;
    String actual2;
    int position2;

    String [] languagelist;

    public MainActivityTest (){
        super(MainActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        MainActivity mainactivity = getActivity();
        mainToolbar = (Toolbar) mainactivity.findViewById(R.id.toolbar);
        setActivityInitialTouchMode(false);
    }

    public void testSpinner(){

        languagelist = profilefragment.languageList;

        testSpinner1.getAdapter();
        testSpinner2.getAdapter();

        assertTrue(testSpinner1.getOnItemSelectedListener() != null);
        assertTrue(testSpinner2.getOnItemSelectedListener() != null);

        testSpinner1 = profilefragment.spinnerUserKnow;
        testSpinner2 = profilefragment.spinnerUserLearn;

        //iterates through the items in the spinner

        for(int i=INITIAL_POSITION;i<=FINAL_POSITION;i++){
            try {
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        testSpinner1.setSelection(INITIAL_POSITION);
                        testSpinner2.setSelection(INITIAL_POSITION);
                    }
                });
            }catch (Throwable e) {
                e.printStackTrace();
            }
            TouchUtils.tapView(this, testSpinner1);
            TouchUtils.tapView(this, testSpinner2);

            sendRepeatedKeys(i, KeyEvent.KEYCODE_DPAD_DOWN);

            sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);

            expected1 = testSpinner1.getSelectedItem().toString();
            position1 = testSpinner1.getSelectedItemPosition();
            actual1 = languagelist[position1];
            assertEquals(expected1, actual1);

            expected2 = testSpinner2.getSelectedItem().toString();
            position2 = testSpinner2.getSelectedItemPosition();
            actual2 = languagelist[position2];
            assertEquals(expected2,actual2);
        }
    }

    // method to check MainActivity is created
    public void testActivityExists(){
        MainActivity mainactivity = getActivity();
        assertNotNull(mainactivity);
        Log.i("tag", "testActivityExists");
    }

    // to test the main toolbar exists
    public void testmainToolbarExists(){
        assertNotNull(mainToolbar);
        Log.i("tag", "testmainToolbarExists");
    }

    public void testServerthrows() throws IOException {

        org.apache.http.HttpResponse expectedResponse = mock(org.apache.http.HttpResponse.class, new ThrowsException(new UnsupportedOperationException()));

        doReturn("mock expectedResponse").when(expectedResponse).toString();
        doReturn(null).when(expectedResponse).getEntity();
        doReturn(null).when(expectedResponse).getFirstHeader("ETag");

        final HttpHost httpHost = new HttpHost("whatever", 1234);

        org.apache.http.client.HttpClient client = mock(org.apache.http.client.HttpClient.class, new ThrowsException(new UnsupportedOperationException()));
        org.apache.http.client.HttpClient backend = mock(org.apache.http.client.HttpClient.class, new ThrowsException(new UnsupportedOperationException()));

        HttpParams httpParams = mock(HttpParams.class);
        doReturn(httpHost).when(httpParams).getParameter(ClientPNames.DEFAULT_HOST);

        doReturn(httpParams).when(client).getParams();

        doReturn(expectedResponse).when(client).execute(eq(httpHost), any(HttpRequest.class));

//        AndroidHttpClient service = new AndroidHttpClient(client, backend);

//        HttpResponse androidHttpResponse = service.get("");
//        assertNotNull(androidHttpResponse);

    }
}