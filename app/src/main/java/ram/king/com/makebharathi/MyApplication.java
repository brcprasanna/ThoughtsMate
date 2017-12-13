package ram.king.com.makebharathi;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {

    private DatabaseReference mRootRef;
    private DatabaseReference mConditionRef;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mRootRef = FirebaseDatabase.getInstance().getReference();
            mConditionRef = mRootRef.child("condition");

        Fabric.with(this, new Crashlytics());
        Fresco.initialize(this);
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
