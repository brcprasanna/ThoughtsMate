package ram.king.com.makebharathi.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ram.king.com.makebharathi.MyApplication;
import ram.king.com.makebharathi.R;

public class StatusActivity extends AppCompatActivity {


   private Toolbar mToolbar;
    NavigationView mNavigationView;
    EditText edtStatus;
    Button btnSend, btnDelete;
    TextView txtShow;

    String user;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        user = getIntent().getStringExtra("user");

        //for emulator
        if (user == null)
            user = "Emulator";
        edtStatus = (EditText) findViewById(R.id.input_status);
        btnSend = (Button) findViewById(R.id.btnSubmitStatus);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        txtShow = (TextView) findViewById(R.id.txtShow);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateStatus();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearStatus();
            }
        });



        ((MyApplication) this.getApplication()).getFbConditionRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
                txtShow.setText(text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void clearStatus() {
        ((MyApplication) this.getApplication()).getFbConditionRef().setValue("");
    }

    private void validateStatus() {
        if (edtStatus.getText().toString().length() > 0) {
            submitStatus();
        } else {
            if (edtStatus.getText().toString().length() <= 0) {
                Toast.makeText(this, "Please enter your status", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void submitStatus() {
        //final String uniqId = randomString(6);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Submitting your status");
        pDialog.setCancelable(false);
        pDialog.show();
        ((MyApplication) this.getApplication()).getFbConditionRef().setValue(txtShow.getText().toString() + user +":"+edtStatus.getText().toString()+"\n");
        pDialog.hide();
        edtStatus.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}

