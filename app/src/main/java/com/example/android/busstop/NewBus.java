package com.example.android.busstop;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class NewBus extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    private EditText mEtxtStopNum, mEtxtBusNum, mEtxtStart, mEtxtDestin;
    private Button mBtnSave, mBtnDelete;

    Boolean update;
    String info;
    Long id;

    private static final String COL1 = "stop_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bus);
        mEtxtBusNum = (EditText) findViewById(R.id.etxtBusNumber);
        mEtxtStopNum = (EditText) findViewById(R.id.etxtStopNumber);
        mEtxtStart = (EditText) findViewById(R.id.etxtStart);
        mEtxtDestin = (EditText) findViewById(R.id.etxtDestination);
        mBtnSave = (Button) findViewById(R.id.btnSave);
        mBtnDelete = (Button) findViewById(R.id.btnDelete);
        mDatabaseHelper = new DatabaseHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras == null){
            update = false;
        }else{
            update = true;
            info = extras.getString("info");
            loadData();
        }

        mBtnSave.setOnClickListener(new View.OnClickListener(){

            // When save is clicked, get information and proceed

            @Override
            public void onClick(View v){

                int stopNum = Integer.parseInt(mEtxtStopNum.getText().toString());
                int busNum = Integer.parseInt(mEtxtBusNum.getText().toString());

                String start = mEtxtStart.getText().toString();
                String destin = mEtxtDestin.getText().toString();

                if (update){ // If information originally existed, update
                    mDatabaseHelper.updateDetails(stopNum, start, destin, busNum, id);
                }else{ // If it is new info, add to database
                    newBus(stopNum, start, destin, busNum);
                }

                Intent intent = new Intent(NewBus.this, MainActivity.class);
                startActivity(intent);

            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener(){

            // When Delete is clicked, delete the information from database

            @Override
            public void onClick(View v){
                mDatabaseHelper.deleteRoute(id);
                Intent intent = new Intent(NewBus.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadData(){

        // Load the information from database when it is to update a route

        String[] parts = info.split(" ", 2);
        int busNum = Integer.parseInt(parts[0]);
        mEtxtBusNum.setText(parts[0]);

        String[] part = parts[1].split(" to ");
        String start = part[0];
        String destin = part[1];

        mEtxtStart.setText(start);
        mEtxtDestin.setText(destin);

        Cursor data = mDatabaseHelper.getBusDetails(start, destin, busNum);

        data.moveToFirst();

        id = data.getLong(0);

        mEtxtStopNum.setText(data.getString(data.getColumnIndex(COL1)));
    }

    private void newBus(int stopNum, String start, String destin, int busNum){

        // When it is a new route, information is added to database, then page is wiped clean

        boolean upload = mDatabaseHelper.addData(stopNum, start, destin, busNum);

        if (upload == true){
            Toast.makeText(NewBus.this, "Data Successfully Entered", Toast.LENGTH_SHORT).show();
            mEtxtDestin.setText("");
            mEtxtStart.setText("");
            mEtxtBusNum.setText("");
            mEtxtStopNum.setText("");
            Intent intent = new Intent(NewBus.this, MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(NewBus.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
    }


}
