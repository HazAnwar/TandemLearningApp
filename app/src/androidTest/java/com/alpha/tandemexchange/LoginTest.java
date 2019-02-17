package com.alpha.tandemexchange;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginTest extends ActivityInstrumentationTestCase2<Login> {

    public LoginTest(Class<Login> activityClass) {
        super(activityClass);
    }

    @Test
    public void testOnCreate() throws Exception {
        assertNotNull(getActivity().findViewById(R.id.toolbar));
        assertNotNull(getActivity().findViewById(R.id.loginEmail));
        assertNotNull(getActivity().findViewById(R.id.loginPassword));
        assertNotNull(getActivity().findViewById(R.id.loginButton));
        assertNotNull(getActivity().findViewById(R.id.forgotPassword));
        assertNotNull(getActivity().findViewById(R.id.togglePassword));
        assertNotNull(getActivity().findViewById(R.id.loginButton).hasOnClickListeners());
        assertNotNull(getActivity().findViewById(R.id.forgotPassword).hasOnClickListeners());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            assertEquals(Color.BLACK, ((EditText) getActivity().findViewById(R.id.loginEmail)).getCurrentTextColor());
            assertEquals(Color.BLACK, ((EditText) getActivity().findViewById(R.id.loginPassword)).getCurrentTextColor());
            assertEquals(Color.BLACK, ((Button) getActivity().findViewById(R.id.loginButton)).getCurrentTextColor());
            assertEquals(Color.BLACK, ((TextView) getActivity().findViewById(R.id.forgotPassword)).getCurrentTextColor());
            assertEquals(Color.BLACK, ((TextView) getActivity().findViewById(R.id.title)).getCurrentTextColor());
        }

        assertNotNull(getActivity().findViewById(R.id.togglePassword));
    }

    @Test
    public void testOnCreateOptionsMenu() throws Exception {

    }

    @Test
    public void testOnOptionsItemSelected() throws Exception {

    }

    @Test
    public void testOnClick() throws Exception {

    }

    @Test
    public void testRandomString() throws Exception {

    }
}