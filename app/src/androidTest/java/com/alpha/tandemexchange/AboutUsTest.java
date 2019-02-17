package com.alpha.tandemexchange;

import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.test.SingleLaunchActivityTestCase;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class AboutUsTest extends ActivityInstrumentationTestCase2<AboutUs> {

    /**
     * <b>NOTE:</b> The parameter <i>pkg</i> must refer to the package identifier of the
     * package hosting the activity to be launched, which is specified in the AndroidManifest.xml
     * file.  This is not necessarily the same as the java package name.
     *
     * @param pkg           The package hosting the activity to be launched.
     * @param activityClass The activity to test.
     */
    public AboutUsTest(String pkg, Class<AboutUs> activityClass) {
        super(pkg, activityClass);
    }

    @Test
    public void testOnCreate() throws Exception {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        assertNotNull(getActivity().getSupportActionBar());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            assertEquals(toolbar.getTitle(), "About us");
        }
    }
}