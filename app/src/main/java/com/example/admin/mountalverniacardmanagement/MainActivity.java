package com.example.admin.mountalverniacardmanagement;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mtoogle;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;
    Card fragment = new Card();
    NfcAdapter nfcAdapter;
    EditText serialET, craET;
    String NFCserial, NFCserialPost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config = getResources().getConfiguration();
//        if(config.smallestScreenWidthDp <= 480) {
//            setContentView(R.layout.activity_main_small);
//        }
//
//        else {
//            setContentView(R.layout.activity_main);
//        }
        setContentView(R.layout.activity_main);



        serialET = (EditText)findViewById(R.id.scanET);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        insert.setEnabled(false);
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {

        } else {
            Toast.makeText(this, "NFC not detected", Toast.LENGTH_LONG).show();
//            finish();
        }

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mtoogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);


        mDrawerLayout.addDrawerListener(mtoogle);
        //initiate page when main activity starts
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new Card(), "card");
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Card Management");

        mtoogle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch(item.getItemId()) {

                    case R.id.Card:

                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new Card(),"card");
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Card Management");
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.ManualCleaning:

                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new ManualCleaning(),"manual");
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Manual Cleaning Request");
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.RoomStatus:

                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new RoomStatus());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("SG Ward Room Status");
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.Reports:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();

                        fragmentTransaction.replace(R.id.main_container, new Reports());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Reports");
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        break;

                }
                return true;
            }

        });
    }


    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }


    @Override
    protected void onNewIntent(Intent intent) {
//   Card fragment = (Card) getFragmentManager().findFragmentById(R.id.drawerLayout);
//        Card fragment = new Card();
//        Card my = (Card) fragment;

//        Fragment fragment = getFragmentManager().findFragmentById(R.id.main_container);
//        if (fragment instanceof ManualCleaning) {
//            ManualCleaning my = (ManualCleaning) fragment;
//        }
//        Card fragment = (Card) getSupportFragmentManager().findFragmentById(R.id.main_container);

//        Fragment fragment = getFragmentManager().findFragmentByTag("manual");




        super.onNewIntent(intent);
        byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        NFCserial = bytesToHexString(id);
        Card fragment = (Card) getSupportFragmentManager().findFragmentByTag("card");
        if(fragment != null) {
            fragment.serialETSetText(NFCserial);
        }

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 5000);


//        my.serialETSetText(nfcSerial);
//        Card.textViewLanguage.setText("hello mister how do you do");

//        serialET.setText(nfcSerial);
//        Card.serialETSetText(nfcSerial);
//        Bundle bundle = new Bundle();
//        bundle.putString("NFC", id );


//        if (fragment instanceof Card) {
//            Card my = (Card) fragment;
            // Pass intent or its data to the fragment's method
//            my.serialETSetText(nfcSerial);

//        }

        //String tagid = tag.toString();
//        String nfcSerial = bytesToHexString(id);
//        serialET.setText(nfcSerial);
//        String bednumDisplay = null;
//        bednumDisplay = getserialnum();

//        if(bednumDisplay != null) {
//            if(!logstatusdisplay.equals("0")) {
//                craET.setText("Card Error Reset");
//                clear.setEnabled(true);
//            }
//            else {
//                if(cleanstatusdisplay.equals("0")) {
//                    craET.setText(bednumDisplay + " Normal");
//                    clear.setEnabled(true);
//                }
//                else if(cleanstatusdisplay.equals("1")) {
//                    craET.setText(bednumDisplay + " ISO");
//                    clear.setEnabled(true);
//                }
//
//            }
//        }
//        else {
//            craET.setText("Card not set");
//            clear.setEnabled(false);
//        }

    }
//
    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);

        super.onResume();

    }

    @Override
    protected void onPause() {
        nfcAdapter.disableForegroundDispatch(this);

        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mtoogle.onOptionsItemSelected(item)) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
