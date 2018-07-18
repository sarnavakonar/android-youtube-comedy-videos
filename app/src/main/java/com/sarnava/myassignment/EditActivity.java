package com.sarnava.myassignment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    ImageView img;
    Spinner spinner;
    EditText age;
    TextView tv;
    Button btn;
    String gender;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        SharedPreferences sp = getSharedPreferences("info", Context.MODE_PRIVATE);
        String s = sp.getString("age", "");
        if(s != ""){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        tv = (TextView) findViewById(R.id.txt);
        img=(ImageView) findViewById(R.id.pic);
        age=(EditText) findViewById(R.id.age);
        btn=(Button) findViewById(R.id.btn);
        spinner=(Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Picasso.with(this).load(user.getPhotoUrl()).into(img);
            tv.setText("Welcome "+"\nLets finish your profile");
        }

        mAuth= FirebaseAuth.getInstance();
        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(i);
                }
            }
        };

        List<String> categories = new ArrayList<String>();
        categories.add("M");
        categories.add("F");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gender = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        finish();
    }

    public void main(View v){
        String u_age = age.getText().toString();
        if(u_age.length()>0) {
            SharedPreferences sp = getSharedPreferences("info", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("gender", gender);
            editor.putString("age", u_age);
            editor.apply();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(),"Enter age",Toast.LENGTH_SHORT).show();
        }
    }
}
