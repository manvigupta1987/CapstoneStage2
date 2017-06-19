package com.example.manvi.walkmore.ui.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;

import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manvi.walkmore.data.WalkMorePreferences;
import com.example.manvi.walkmore.ui.fragment.GoalDialogueFragment;
import com.example.manvi.walkmore.ui.receiver.BootReceiver;
import com.example.manvi.walkmore.BuildConfig;
import com.example.manvi.walkmore.R;
import com.example.manvi.walkmore.ui.receiver.ReminderAlarm;
import com.example.manvi.walkmore.ui.fragment.HistoryFragment;
import com.example.manvi.walkmore.ui.fragment.MainActivityFragment;
import com.example.manvi.walkmore.ui.fragment.SettingsFragment;
import com.example.manvi.walkmore.ui.service.ReminderTask;
import com.example.manvi.walkmore.utils.ConstantUtils;
import com.example.manvi.walkmore.utils.DialogueUtill;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;
import static com.example.manvi.walkmore.R.id.drawer_layout;


public final class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.main_activity_view)
    LinearLayout mMainActivity;

    private CircleImageView imgProfile;
    private TextView txtName, txtEmail;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int RC_SIGN_IN = 2;
    // index to identify current nav menu item
    private static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_DATA = "history-data";
    private static final String TAG_GOAL = "goal-fragment";
    private static final String TAG_SETTINGS = "settings";
    private static String CURRENT_TAG = TAG_HOME;
    private static String PREV_TAG = CURRENT_TAG;
    private static int INSTRUCTIONS_CODE = 1;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    private Handler mHandler;
    private MainActivityFragment mMainFragment;
    private HistoryFragment mHistoryFragment;
    private SettingsFragment mSettingFragment;
    private GoalDialogueFragment mGoalFragment;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions mGso;
    private boolean saveInstance = false;

    private static boolean mPermissionGranted = false;
    private static int REQUEST_OATH = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Context mContext = MainActivity.this;
        setupToolBar();
        mPermissionGranted = ConstantUtils.checkPermissions(mContext);
        buildGoogleClient();
        mHandler = new Handler();

        // Navigation view header
        View navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtEmail = (TextView) navHeader.findViewById(R.id.email);
        imgProfile = (CircleImageView) navHeader.findViewById(R.id.img_profile);
        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        scheduleAlarms(this);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerBroadcastReceiver();

        showAlterDiaglogueBox(getIntent());
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(TAG_HOME)){
                saveInstance = true;
                navItemIndex = 0;
                mMainFragment = (MainActivityFragment) getSupportFragmentManager()
                        .getFragment(savedInstanceState, TAG_HOME);
                CURRENT_TAG = TAG_HOME;
            } else if(savedInstanceState.containsKey(TAG_DATA)){
                saveInstance = true;
                navItemIndex = 1;
                mHistoryFragment = (HistoryFragment) getSupportFragmentManager()
                        .getFragment(savedInstanceState, TAG_DATA);
                CURRENT_TAG = TAG_DATA;
            } else if(savedInstanceState.containsKey(TAG_SETTINGS)) {
                saveInstance = true;
                navItemIndex = 3;
                mSettingFragment = (SettingsFragment) getSupportFragmentManager()
                        .getFragment(savedInstanceState, TAG_SETTINGS);
                CURRENT_TAG = TAG_SETTINGS;
            } else if(savedInstanceState.containsKey(TAG_GOAL)){
                saveInstance = true;
                navItemIndex = 2;
                mGoalFragment = (GoalDialogueFragment)getSupportFragmentManager().getFragment(savedInstanceState,TAG_GOAL);
                CURRENT_TAG = TAG_GOAL;
            }
        } else {
            saveInstance = false;
        }
        //This code requires to load the navigation drawer and home fragment from onCreate() function
        // when login is not required. When login is required, this functionaliy is handled by handleInSignIn function.
        if(!WalkMorePreferences.loginRequired(this)) {
            SetupEditUserProfileActivity();
            setUpNavigationView();
            if (!saveInstance) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
            }
            loadHomeFragment();
            updateNavigationHeader();
        }
    }

    private void showAlterDiaglogueBox(Intent intent){
        if (intent != null && intent.getAction() != null)
        {
            if(intent.getAction().equals(ReminderTask.ACTION_INCREMENT_GOAL)){
                DialogueUtill.showDialogue(MainActivity.this);
                drawer.closeDrawers();
            }
        }
    }

    private void updateNavigationHeader(){
        Bundle bundle = WalkMorePreferences.getLoginInformation(this);
        String personName = bundle.getString(getString(R.string.person_name_key));
        String personEmail = bundle.getString(getString(R.string.person_email_key));
        String personPhoto =  bundle.getString(getString(R.string.person_photo_key));
        if(personName!=null && !personName.isEmpty()) {
            txtName.setText(personName);
            txtName.setContentDescription(getString(R.string.a11y_name, personName));
        } else{
            txtName.setText("");
            txtName.setContentDescription(getString(R.string.a11y_name, ""));
        }

        if(personEmail!=null && !personEmail.isEmpty()) {
            txtEmail.setText(personEmail);
            txtEmail.setContentDescription(getString(R.string.a11y_emailId, personEmail));
        }else {
            txtEmail.setText("");
            txtEmail.setContentDescription(getString(R.string.a11y_emailId, ""));
        }
        if(personPhoto!=null && !personPhoto.isEmpty()) {
            // Loading profile image
            Picasso.with(this).load(personPhoto)
                    .placeholder(R.drawable.profile_pic)
                    .error(R.drawable.profile_pic)
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.profile_pic);
        }
    }

    private void buildGoogleClient(){
        if(mGso == null) {
            mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
        }
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                    .build();
        }
    }

    private void setupToolBar(){
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Snackbar.make(
                    mMainActivity,
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS_REQUEST_CODE);

        }
    }

    private void SetupEditUserProfileActivity(){
        SharedPreferences pref = getSharedPreferences(getString(R.string.ActivityPREF_key), Context.MODE_PRIVATE);
        boolean firstRun = pref.getBoolean(getString(R.string.firstRun_key), true);
        if (firstRun) {
            Intent intent = new Intent(this, EditActivity.class);
            intent.setAction(ConstantUtils.FIRST_TIME);
            // here run your first-time instructions, for example :

            startActivityForResult(intent, INSTRUCTIONS_CODE);

            SharedPreferences settings = getSharedPreferences(getString(R.string.ActivityPREF_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(getString(R.string.firstRun_key), false);
            editor.apply();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Timber.d("Array is empty");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
                SetupEditUserProfileActivity();
            } else {
                // Permission denied.

                // In this Activity we've chosen to notify the user that they
                // have rejected a core permission for the app since it makes the Activity useless.
                // We're communicating this message in a Snackbar since this is a sample app, but
                // core permissions would typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Snackbar.make(
                        mMainActivity,
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    //This function schedules an alarm manager to trigger an event at midnight to reset the daily step counts.
    public static void scheduleAlarms(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, ConstantUtils.TIME_SEC);
        calendar.set(Calendar.MINUTE, ConstantUtils.TIME_MIN);
        calendar.set(Calendar.HOUR, ConstantUtils.TIME_HOUR);
        calendar.set(Calendar.AM_PM, Calendar.AM);

        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    //This is called only when silent sign In is failed.
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //This is required for slient sign In. This will be called only once until user un-install the app.
        if(WalkMorePreferences.loginRequired(this)) {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                // If the user has not previously signed in on this device or the sign-in has expired,
                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                // single sign-on will occur in this branch.
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }
    }


    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        // set toolbar title
        setToolbarTitle();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (saveInstance) {
            if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
                drawer.closeDrawers();
                // show or hide the fab button
                return;
            }
        }
        saveInstance = false;
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @SuppressLint("RtlHardcoded")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                if(CURRENT_TAG.equals( TAG_GOAL)){
                    mGoalFragment.show(getSupportFragmentManager(),TAG_GOAL);
                    findPrevIndex();
                    setToolbarTitle();
                } else {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    if (fragment != null) {
                        Slide slide = new Slide(Gravity.RIGHT | Gravity.END);
                        slide.setDuration(500);
                        fragment.setEnterTransition(slide);
                        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                    }
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //Closing drawer on item click
        drawer.closeDrawers();
        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                mMainFragment = (MainActivityFragment)(getSupportFragmentManager().findFragmentByTag(TAG_HOME));
                if(mMainFragment == null) {
                    mMainFragment = MainActivityFragment.newInstance();
                }
                return mMainFragment;
            case 1:
                mHistoryFragment = (HistoryFragment)(getSupportFragmentManager().findFragmentByTag(TAG_DATA));
                if(mHistoryFragment == null) {
                    mHistoryFragment = HistoryFragment.newInstance();
                }
                return mHistoryFragment;
            case 2: //Goal Option
                //return null;
                mGoalFragment = (GoalDialogueFragment)(getSupportFragmentManager().findFragmentByTag(TAG_GOAL));
                if(mGoalFragment==null)
                {
                    mGoalFragment = new GoalDialogueFragment();
                }
                return mGoalFragment;
            case 3:
                mSettingFragment = (SettingsFragment) (getSupportFragmentManager().findFragmentByTag(TAG_SETTINGS));
                if(mSettingFragment == null) {
                    mSettingFragment =  new SettingsFragment();
                }
                return mSettingFragment;
            default:
                mMainFragment = (MainActivityFragment)(getSupportFragmentManager().findFragmentByTag(TAG_HOME));
                if(mMainFragment == null) {
                    mMainFragment = MainActivityFragment.newInstance();
                }
                return mMainFragment;
        }
    }

    private void setToolbarTitle() {
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
        }
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                PREV_TAG = CURRENT_TAG;
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_data:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_DATA;
                        break;
                    case R.id.nav_goal:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_GOAL;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer);
       //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        // checking if user is on other navigation menu
        // rather than home
        if (navItemIndex != 0) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }else if (resultCode == RESULT_CANCELED) {
                Snackbar.make(findViewById(drawer_layout),
                        R.string.sign_in_unsuccessful, Snackbar.LENGTH_SHORT).show();
            }
        } else if(requestCode == INSTRUCTIONS_CODE){
            setUpNavigationView();
            if (!saveInstance) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
            }
            loadHomeFragment();
        } else if(requestCode == REQUEST_OATH ) {
            if(mMainFragment!=null){
                mMainFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            WalkMorePreferences.updateLoginRequired(this, false);
            if (!mPermissionGranted) {
                requestPermissions();
            } else {
                SetupEditUserProfileActivity();
                setUpNavigationView();
                if (!saveInstance) {
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                }
                loadHomeFragment();
            }

            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();
                Uri personPhoto = acct.getPhotoUrl();
                WalkMorePreferences.storePersonformation(this, personName);
                WalkMorePreferences.storeEmailformation(this, personEmail);
                WalkMorePreferences.storePhotoLinkformation(this, personPhoto);

                txtName.setText(personName);
                txtName.setContentDescription(getString(R.string.a11y_name, personName));
                txtEmail.setText(personEmail);
                txtEmail.setContentDescription(getString(R.string.a11y_emailId, personEmail));
                // Loading profile image
                if(personPhoto!=null && !personPhoto.equals(Uri.EMPTY)) {
                    Picasso.with(this).load(personPhoto)
                            .placeholder(R.drawable.profile_pic)
                            .error(R.drawable.profile_pic)
                            .into(imgProfile);
                }else {
                    imgProfile.setImageResource(R.drawable.profile_pic);
                }
            }
        } else {
            if(ConstantUtils.isConnectedToInternet(this)) {
                signIn();
            }else{
                DialogueUtill.showNoInternetConnectivity(this);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                int stepCount =0;
                if(mMainFragment!=null) {
                    stepCount = mMainFragment.getmDailyStepsCount();
                }
                String message = getString(R.string.target_complete_message,stepCount);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setType("text/plain");
                if (sendIntent.resolveActivity(getPackageManager())!=null){
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.send_to)));
                }
                return true;
            case R.id.action:
                View menuItemView = findViewById(R.id.action);
                PopupMenu menu = new PopupMenu(this, menuItemView);
                menu.inflate(R.menu.popup_main);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.action_logout:
                                logout();
                                return true;
                            case R.id.action_edit:
                                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                                startActivity(intent);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuPopupHelper menuHelper = new MenuPopupHelper(this, (MenuBuilder) menu.getMenu(),menuItemView);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        WalkMorePreferences.updateLoginRequired(this, true);
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Toast.makeText(getApplicationContext(),getString(R.string.logout), Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void registerBroadcastReceiver() {
        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(Intent.ACTION_SCREEN_ON);

        BroadcastReceiver mPowerKeyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strAction = intent.getAction();
                if (strAction.equals(Intent.ACTION_SCREEN_ON)) {
                    Intent intent1 = new Intent(ConstantUtils.ACTION_DATA_STARTED).setPackage(context.getPackageName());
                    context.sendBroadcast(intent1);
                }
            }
        };
        getApplicationContext().registerReceiver(mPowerKeyReceiver, theFilter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(CURRENT_TAG);

        // Since GardenFragment appends the garden number to the end, need to remove this for the key
        if(currentFragment!=null) {
            String currentFragmentTag = currentFragment.getTag();
            getSupportFragmentManager().putFragment(outState, currentFragmentTag, currentFragment);
        }
    }

    private void findPrevIndex(){
        switch (PREV_TAG){
            case TAG_HOME:
                navItemIndex = 0;
                break;
            case TAG_DATA:
                navItemIndex = 1;
                break;
            case TAG_SETTINGS:
                navItemIndex = 3;
                break;
            default:
                navItemIndex =0;
        }
    }
}
