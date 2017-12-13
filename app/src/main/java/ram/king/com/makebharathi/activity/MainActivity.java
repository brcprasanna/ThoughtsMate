/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ram.king.com.makebharathi.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

import ram.king.com.makebharathi.BuildConfig;
import ram.king.com.makebharathi.R;
import ram.king.com.makebharathi.fragment.MyFavouritesFragment;
import ram.king.com.makebharathi.fragment.MyPostsFragment;
import ram.king.com.makebharathi.fragment.RecentPostsFragment;
import ram.king.com.makebharathi.util.AppConstants;
import ram.king.com.makebharathi.util.AppUtil;
import ram.king.com.makebharathi.util.MessageEvent;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static ViewPager mViewPager;
    private FragmentPagerAdapter mPagerAdapter;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static int getViewPagerNumber() {
        return mViewPager.getCurrentItem();
    }

    public static int getViewPagePosition() {
        return mViewPager.getCurrentItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppUtil.getDynamicLink(this);
        String prefLanguage = AppUtil.getString(this, AppConstants.PREFERRED_LANGUAGE, AppConstants.DEFAULT_LANGUAGE);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name) + " " + "(" + prefLanguage + ")");

        //initialize fresco
        //Fresco.initialize(this);

        // Get Remote Config instance.
        // [START get_remote_config_instance]
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // [END get_remote_config_instance]

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. See Best Practices in the
        // README for more information.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        // [END enable_dev_mode]

        // Set default Remote Config parameter values. An app uses the in-app default values, and
        // when you need to adjust those defaults, you set an updated value for only the values you
        // want to change in the Firebase console. See Best Practices in the README for more
        // information.
        // [START set_default_values]
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        // [END set_default_values]

        fetchRemoteConfig();

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{
                    new RecentPostsFragment(),
                    new MyPostsFragment(),
                    new MyFavouritesFragment(),
            };

            private final String[] mFragmentNames = new String[]{
                    getString(R.string.heading_recent),
                    getString(R.string.heading_my_posts),
                    getString(R.string.heading_my_favourites)
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Button launches NewPostActivity
        findViewById(R.id.fab_new_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));


            }
        });


        // [END create_interstitial_ad_listener]



    }


    // [END get_deep_link]

    // [END on_create]

    private void fetchRemoteConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                        }
                        setRemoteConfigToLocal();
                    }
                });
        // [END fetch_config_with_callback]

    }

    private void setRemoteConfigToLocal() {
        // [START get_config_values]
        String prevLimitToLast = AppUtil.getString(this, AppConstants.QUERY_LIMIT_TO_LAST, AppConstants.DEFAULT_LIMIT_TO_LAST);
        String limitToLast = mFirebaseRemoteConfig.getString(AppConstants.QUERY_LIMIT_TO_LAST);
        boolean prevUseLimitToLast = AppUtil.getBoolean(this, AppConstants.USE_LIMIT_TO_LAST, AppConstants.DEFAULT_USE_LIMIT_TO_LAST);
        boolean useLimitToLast = mFirebaseRemoteConfig.getBoolean(AppConstants.USE_LIMIT_TO_LAST);


        //Toast.makeText(MainActivity.this, limitToLast,
        //        Toast.LENGTH_SHORT).show();

        AppUtil.putString(this, AppConstants.QUERY_LIMIT_TO_LAST, limitToLast);
        AppUtil.putBoolean(this, AppConstants.USE_LIMIT_TO_LAST, useLimitToLast);

        prevLimitToLast = prevLimitToLast + prevUseLimitToLast;
        limitToLast = limitToLast + useLimitToLast;

        if (!prevLimitToLast.equals(limitToLast)) {
            Toast.makeText(MainActivity.this, "Refreshing..",
                    Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new MessageEvent("changed"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        } else if (i == R.id.action_choose_lang) {
            buildLangDialogList();

            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    private void buildLangDialogList() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.ic_language_black_24dp);
        builderSingle.setTitle("Select a Language:-");



        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice, AppConstants.languages);

        int selectedIndex = Arrays.asList(AppConstants.languages).indexOf(AppUtil.getString(this,AppConstants.PREFERRED_LANGUAGE,AppConstants.DEFAULT_LANGUAGE));
        builderSingle.setSingleChoiceItems(AppConstants.languages,selectedIndex,null);

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name)+" "+"("+strName+")");

                AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                //builderInner.setMessage(strName);
                AppUtil.putString(MainActivity.this,AppConstants.PREFERRED_LANGUAGE,strName);
                EventBus.getDefault().post(new MessageEvent("changed"));
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

}
