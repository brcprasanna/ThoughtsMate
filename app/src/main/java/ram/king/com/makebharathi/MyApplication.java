package ram.king.com.makebharathi;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Irfan on 1/15/2016.
 */
public class MyApplication extends Application {

    private DatabaseReference mRootRef;
    private DatabaseReference mConditionRef;

    @Override
    public void onCreate() {
        super.onCreate();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mConditionRef = mRootRef.child("condition");

        Fabric.with(this, new Crashlytics());

        /*// Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this);

        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
         defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);*/
    }


    public DatabaseReference getFbroot() {
        return mRootRef;
    }

    public void setFbRoot(DatabaseReference reference) {
        this.mRootRef = reference;
    }

    public DatabaseReference getFbConditionRef() {
        return mConditionRef;
    }

    public void setFbConditionRef(DatabaseReference reference) {
        this.mConditionRef = reference;
    }

}
