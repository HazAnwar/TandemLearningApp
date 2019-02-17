package com.alpha.tandemexchange;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.alpha.tandemexchange.util.AuthenticationProvider;
import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageUtils;
import com.layer.atlas.provider.ParticipantProvider;
import com.layer.atlas.util.Util;
import com.layer.atlas.util.picasso.requesthandlers.MessagePartRequestHandler;
import com.layer.sdk.LayerClient;
import com.squareup.picasso.Picasso;
import java.util.Arrays;

/**
 * Main Activity of the application, which will open after the splash screen on app launch if the user is logged in.
 * If the user is not logged in or does not have an account, Register activity will open first
 */
public class MainActivity extends AppCompatActivity {
    private static Flavor sFlavor = new com.alpha.tandemexchange.Flavor();
    private static LayerClient sLayerClient;
    private static ParticipantProvider sParticipantProvider;
    private static AuthenticationProvider sAuthProvider;
    private static Picasso sPicasso;
    private static Application sInstance;
    StoreLocalUserData storeLocalUserData;
    Toolbar mainToolbar;
    DrawerLayout settingsDrawer;
    NavigationView navigationDrawer;
    ActionBarDrawerToggle toolbarDrawerToggle;
    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    @Override
    public void onStart() {
        super.onStart();
        routeLogin(MainActivity.this);
        if (authenticate()) {
            //do nothing
        } else {
            routeLogin(MainActivity.this);
            finish();
            startActivity(new Intent(this, Register.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Enable verbose logging in debug builds
        if (BuildConfig.DEBUG) {
            com.layer.atlas.util.Log.setAlwaysLoggable(true);
            LayerClient.setLoggingEnabled(this, true);
        }

        // Allow the LayerClient to track app state
        LayerClient.applicationCreated(this.getApplication());

        sInstance = this.getApplication();

        //initialise GUI widgets with the code+++++
        //setup toolbar/actionbar and turns it into a settings drawer panel
        mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        mainToolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(mainToolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mainToolbar.setElevation(-5);
        }

        //setup tabLayout with the different tabs for GUI
        tabLayout = (TabLayout) findViewById(R.id.tabSwitcher);
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
        tabLayout.addTab(tabLayout.newTab().setText("Users"));
        tabLayout.addTab(tabLayout.newTab().setText("Messages"));

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new TabFragment(getSupportFragmentManager(), 3);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //do nothing
            }
        });
        //set the viewPager to open the user page when the application is opened
        viewPager.setCurrentItem(1);

        //setup the navigationDrawer for material design settings options
        settingsDrawer = (DrawerLayout) findViewById(R.id.settingsDrawer);
        navigationDrawer = (NavigationView) findViewById(R.id.navigationDrawer);

        //add a listener to the navigationDrawer for when something is clicked
        navigationDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //close the navigationDrawer when a menu option has been clicked
                settingsDrawer.closeDrawers();
                //checks which item was clicked on and then does the appropriate feedback...
                switch (menuItem.getItemId()) {
                    case R.id.menuSettings:
                        AppSettingsActivity();
                        return true;
                    case R.id.menuRefresh:
                        refreshActivity();
                        return true;
                    case R.id.menuAboutUs:
                        aboutUsActivity();
                        return true;
                    case R.id.menuLogOut:
                        storeLocalUserData.clearUserData();
                        storeLocalUserData.setLoggedIn(false);
                        Toast.makeText(getApplicationContext(), "You have successfully logged out.", Toast.LENGTH_SHORT).show();
                        MainActivity.deauthenticate(new Util.DeauthenticationCallback() {
                            @Override
                            public void onDeauthenticationSuccess(LayerClient client) {
                                routeLogin(MainActivity.this);
                            }
                            @Override
                            public void onDeauthenticationFailed(LayerClient client, String reason) {

                            }
                        });
                        openLoginActivity();
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Something went wrong, please try again...", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        toolbarDrawerToggle = new ActionBarDrawerToggle(this, settingsDrawer, mainToolbar, R.string.navDrawOpen, R.string.navDrawClose) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we don't want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we don't want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        settingsDrawer.setDrawerListener(toolbarDrawerToggle);
        //calling sync state is necessary or else your hamburger icon wont show up
        toolbarDrawerToggle.syncState();
        storeLocalUserData = new StoreLocalUserData(this);
    }

    @Override
    public void onBackPressed() {
        if (authenticate()) {
            finish();
        }
        super.onBackPressed();  // optional depending on your needs
    }

    //initialise Layer messaging into the application
    public static Application getInstance() {
        return sInstance;
    }

    private void openLoginActivity() {
        finish();
        startActivity(new Intent(this, Login.class));
    }

    private void AppSettingsActivity() {
        startActivity(new Intent(this, AppSettingsActivity.class));
    }

    private void refreshActivity() {
        finish();
        routeLogin(MainActivity.this);
        startActivity(new Intent(this, MainActivity.class));
    }

    private void aboutUsActivity() {
        startActivity(new Intent(this, AboutUs.class));
    }

    private boolean authenticate() {
        return storeLocalUserData.isUserLoggedIn();
    }

    public static boolean routeLogin(Activity from) {
        return getAuthenticationProvider().routeLogin(getLayerClient(), getLayerAppId(), from);
    }

    /**
     * Authenticates with the AuthenticationProvider and Layer, returning asynchronous results to
     * the provided callback.
     *
     * @param credentials Credentials associated with the current AuthenticationProvider.
     * @param callback    Callback to receive authentication results.
     */
    @SuppressWarnings("unchecked")
    public static void authenticate(Object credentials, AuthenticationProvider.Callback callback) {
        LayerClient client = getLayerClient();
        if (client == null) return;
        String layerAppId = getLayerAppId();
        if (layerAppId == null) return;
        getAuthenticationProvider()
                .setCredentials(credentials)
                .setCallback(callback);
        client.authenticate();
    }

    /**
     * Deauthenticates with Layer and clears cached AuthenticationProvider credentials.
     *
     * @param callback Callback to receive deauthentication success and failure.
     */
    public static void deauthenticate(final Util.DeauthenticationCallback callback) {
        Util.deauthenticate(getLayerClient(), new Util.DeauthenticationCallback() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDeauthenticationSuccess(LayerClient client) {
                getAuthenticationProvider().setCredentials(null);
                callback.onDeauthenticationSuccess(client);
            }

            @Override
            public void onDeauthenticationFailed(LayerClient client, String reason) {
                callback.onDeauthenticationFailed(client, reason);
            }
        });
    }


    //==============================================================================================
    // Getters / Setters
    //==============================================================================================

    /**
     * Gets or creates a LayerClient, using a default set of LayerClient.Options and flavor-specific
     * App ID and Options from the `generateLayerClient` method.  Returns `null` if the flavor was
     * unable to create a LayerClient (due to no App ID, etc.).
     *
     * @return New or existing LayerClient, or `null` if a LayerClient could not be constructed.
     * @see Flavor#generateLayerClient(Context, LayerClient.Options)
     */
    public static LayerClient getLayerClient() {
        if (sLayerClient == null) {
            // Custom options for constructing a LayerClient
            LayerClient.Options options = new LayerClient.Options()

                    /* Fetch the minimum amount per conversation when first authenticated */
                    .historicSyncPolicy(LayerClient.Options.HistoricSyncPolicy.FROM_LAST_MESSAGE)

                    /* Automatically download text and ThreePartImage info/preview */
                    .autoDownloadMimeTypes(Arrays.asList(
                            TextCellFactory.MIME_TYPE,
                            ThreePartImageUtils.MIME_TYPE_INFO,
                            ThreePartImageUtils.MIME_TYPE_PREVIEW));

            // Allow flavor to specify Layer App ID and customize Options.
            sLayerClient = sFlavor.generateLayerClient(sInstance, options);

            // Flavor was unable to generate Layer Client (no App ID, etc.)
            if (sLayerClient == null) return null;

            /* Register AuthenticationProvider for handling authentication challenges */
            sLayerClient.registerAuthenticationListener(getAuthenticationProvider());
        }
        return sLayerClient;
    }

    /**
     * Accessor (getter) method used to get the Layer App Id
     * @return the Layer App Id
     */
    public static String getLayerAppId() {
        return sFlavor.getLayerAppId();
    }

    /**
     * Accessor (getter) method used to get the participant provider
     * @return the Layer App Id
     *
     */
    public static ParticipantProvider getParticipantProvider() {
        if (sParticipantProvider == null) {
            sParticipantProvider = sFlavor.generateParticipantProvider(sInstance, getAuthenticationProvider());
        }
        return sParticipantProvider;
    }

    /**
     * Accessor (getter) method used to get the Authentication Provider
     * @return the Authentication Provider
     */
    public static AuthenticationProvider getAuthenticationProvider() {
        if (sAuthProvider == null) {
            sAuthProvider = sFlavor.generateAuthenticationProvider(sInstance);

            // If we have cached credentials, try authenticating with Layer
            LayerClient layerClient = getLayerClient();
            if (layerClient != null && sAuthProvider.hasCredentials()) layerClient.authenticate();
        }
        return sAuthProvider;
    }

    /**
     * Accessor (getter) method used to get the Picasso
     * @return the picasso
     */
    public static Picasso getPicasso() {
        if (sPicasso == null) {
            // Picasso with custom RequestHandler for loading from Layer MessageParts.
            sPicasso = new Picasso.Builder(sInstance)
                    .addRequestHandler(new MessagePartRequestHandler(getLayerClient()))
                    .build();
        }
        return sPicasso;
    }

    /**
     *Sets the user interface for the app
     */
    public interface Flavor {
        String getLayerAppId();

        LayerClient generateLayerClient(Context context, LayerClient.Options options);

        AuthenticationProvider generateAuthenticationProvider(Context context);

        ParticipantProvider generateParticipantProvider(Context context, AuthenticationProvider authenticationProvider);
    }
}