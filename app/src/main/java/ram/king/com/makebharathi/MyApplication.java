package ram.king.com.makebharathi;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {

    private DatabaseReference mRootRef;
    private DatabaseReference mConditionRef;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mRootRef = FirebaseDatabase.getInstance().getReference();
            mConditionRef = mRootRef.child("condition");

        Fabric.with(this, new Crashlytics());
        Stetho.initializeWithDefaults(this);
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
