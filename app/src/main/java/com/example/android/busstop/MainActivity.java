package com.example.android.busstop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.telephony.SmsManager;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    private final String mPhoneNum = "33333";
    private ListView mListView;
    private String message;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    private static final String COL1 = "stop_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set up list, variables and the floating action bar to link to adding new bus

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabaseHelper = new DatabaseHelper(this);
        mListView = (ListView)findViewById(R.id.lvBuses);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add_black_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewBus.class);
                startActivity(intent);
            }
        });

        populateListView();
    }

    private void populateListView() {

        // Read all data from SQLite and load into list

        Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            String info = data.getString(4) + " " + data.getString(2) + " to " + data.getString(3);
            listData.add(info);
        }
        final ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // After element is clicked, message to bus is sent

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String info = adapter.getItem(i).toString();
                String[] parts = info.split(" ", 2);
                int busNum = Integer.parseInt(parts[0]);

                String[] part = parts[1].split(" to ");
                String start = part[0];
                String destin = part[1];

                Cursor data = mDatabaseHelper.getStopNum(start, destin, busNum);
                data.moveToFirst();
                String stopNum = data.getString(data.getColumnIndex(COL1));


                message = stopNum + " " + busNum;
                Toast.makeText(getApplicationContext(), "sending SMS function", Toast.LENGTH_LONG).show();
                sendSMSMessage();
            }
        });


        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            // After element is held, will be available to edit

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l){

                String info = adapter.getItem(i).toString();

                Intent editScreenIntent = new Intent(MainActivity.this, NewBus.class);
                editScreenIntent.putExtra("info", info);
                startActivity(editScreenIntent);

                return true;
            }
        });
    }

    protected void sendSMSMessage() {
        //// TODO: 2017-09-24 Check this out and what it means
        // Check for SMS permission, not too sure if everything works

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        // Sending SMS

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(mPhoneNum, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}