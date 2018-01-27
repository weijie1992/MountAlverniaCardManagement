package com.example.admin.mountalverniacardmanagement;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.graphics.Color.rgb;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManualCleaning extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    String initAddress = null;
    Spinner roomSpinner, isoSpinner, wardSpinner;
//    String address = "http://weijietest.000webhostapp.com/Mah/getbednum.php";
    InputStream is = null;
    String line = null;
    String result = null;

    String[] bedNumData = new String[0];

    SwipeRefreshLayout swipeRefreshLayout;
    Button sendBTN;
   String[] wardNumData = new String[0];
    String[] bedNumData10 = new String[0];
    String[] serialdata10 = new String[0];
    String[] logtime = new String[0];
    String[] logstatus = new String[0];
    String[] cleanStatus = new String[0];
    String line1 = null;
    String result1 = null;
    MyListAdaper myListAdapter;
    ListView lv;

    RadioButton rb,rb1;
    RadioGroup rg;
    int radioInt;

    @Override
    public void onRefresh() {
        getLast10Bed();


        ArrayList<MyObject> list10 = new ArrayList<MyObject>();
        for(int i = 0; i < bedNumData10.length; i++) {
            MyObject myObject = new MyObject(bedNumData10[i],serialdata10[i],logtime[i],logstatus[i],cleanStatus[i]);
            list10.add(myObject);
        }

        myListAdapter.updateResults(list10);


        Log.i("thread", "in onrefresh");

        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    class MyObject {
        String bedNumData101 = null;
        String serialdata101 = null;
        String logtime1 = null;
        String logstatus1 = null;
        String cleanstatus1 = null;
        public MyObject(String bd10, String sd10, String lt, String ls,String cs) {
            bedNumData101 = bd10;
            serialdata101 = sd10;
            logtime1 = lt;
            logstatus1 = ls;
            cleanstatus1 = cs;
        }
    }


    public ManualCleaning() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Configuration config = getResources().getConfiguration();
        View view;
        if(config.smallestScreenWidthDp <= 320) {
            view = inflater.inflate(R.layout.fragment_manual_cleaning_small, container, false);
        }
        else {
            view = inflater.inflate(R.layout.fragment_manual_cleaning, container, false);
        }
//        View view =  inflater.inflate(R.layout.fragment_manual_cleaning, container, false);

        initAddress = Address.getAddress();
        roomSpinner = (Spinner)view.findViewById(R.id.roomSpinner);
        isoSpinner = (Spinner)view.findViewById(R.id.cleaningSpinner);
        wardSpinner = (Spinner)view.findViewById(R.id.wardSpinner);
        sendBTN = (Button)view.findViewById(R.id.sendBTN);
        lv = (ListView)view.findViewById(R.id.listview);

        rg = (RadioGroup)view.findViewById(R.id.radioGroup);
        rb = (RadioButton) view.findViewById(R.id.radioNormal);
        rb1 = (RadioButton) view.findViewById(R.id.radioIso);
        //radio group
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                int checkedId = rg.getCheckedRadioButtonId();
                switch(checkedId){
                    case R.id.radioNormal:
                        radioInt = 0;
                        break;
                    case R.id.radioIso:
                        radioInt = 1;
                        break;

                }

            }
        });

        String[] iso = new String[9];
        iso[0] = "Discharge Cleaning";
        iso[1] = "Toilet";
        iso[2] = "Mopping";
        iso[3] = "Rubbish Bin";
        iso[4] = "Internal Transfer";
        iso[5] = "Staff Room";
        iso[6] = "Nurses Counter";
        iso[7] = "Kitchen";
        iso[8] = "Corridors";

        //Spinner ISOSTATUS
        ArrayAdapter<String> IsospinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, iso);
        IsospinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isoSpinner.setAdapter(IsospinnerAdapter);
        isoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                isoSpinner.setSelection(position);
                if(position >=5) {
                    wardSpinner.setEnabled(false);
                    roomSpinner.setEnabled(false);
                    rb.setEnabled(false);
                    rb1.setEnabled(false);
                }
                else {
                    wardSpinner.setEnabled(true);
                    roomSpinner.setEnabled(true);
                    rb.setEnabled(true);
                    rb1.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Future work, fetch from DB
        wardNumData = new String[1];
        wardNumData[0] = "SG";

        ArrayAdapter<String> wardSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, wardNumData);
        wardSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wardSpinner.setAdapter(wardSpinnerAdapter);
        wardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                wardSpinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Spinner for bed number
        JSONArray ja = getData();
        JSONObject jo = null;
        bedNumData = new String[ja.length()];
        try {
            for (int i = 0; i < ja.length(); i++) {

                jo = ja.getJSONObject(i);
                bedNumData[i] = jo.getString("bednum");

            }
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, bedNumData);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            roomSpinner.setAdapter(spinnerAdapter);
            roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                    roomSpinner.setSelection(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } //end spinner


        getLast10Bed();
        ArrayList<MyObject> list = new ArrayList<MyObject>();
        for(int i = 0; i < bedNumData10.length; i++) {
            MyObject myObject = new MyObject(bedNumData10[i],serialdata10[i],logtime[i],logstatus[i],cleanStatus[i]);
            list.add(myObject);
        }
        myListAdapter = new MyListAdaper(getActivity(),R.layout.list_item ,list);
        lv.setAdapter(myListAdapter);


        //Pull Down refresh
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);//end pull down refresh



        //Button Onclick
        sendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bednumString = roomSpinner.getSelectedItem().toString();
                String isoStatusString = isoSpinner.getSelectedItem().toString();
                String isoString = null;

                if(isoSpinner.getSelectedItemPosition() <= 4) {
                    if (radioInt == 0) {
                        isoString = Integer.toString(isoSpinner.getSelectedItemPosition());
//                        ManualBackground manualBackground = new ManualBackground(getActivity());
//                        manualBackground.execute(bednumString, isoString);
                    } else if (radioInt == 1) {
                        int isoInt = isoSpinner.getSelectedItemPosition() + 5;
                        isoString = Integer.toString(isoInt);
//                        ManualBackground manualBackground = new ManualBackground(getActivity());
//                        manualBackground.execute(bednumString, isoString);
                    }
                }
                else if(isoSpinner.getSelectedItemPosition() >= 5) {
                    bednumString = "";
                    if(isoSpinner.getSelectedItemPosition() == 5) {
                        isoString = "10";
                    }
                    else if(isoSpinner.getSelectedItemPosition() == 6) {
                        isoString = "11";
                    }
                    else if(isoSpinner.getSelectedItemPosition() == 7) {
                        isoString = "12";
                    }
                    else if(isoSpinner.getSelectedItemPosition() == 8) {
                        isoString = "13";
                    }

                }
                ManualBackground manualBackground = new ManualBackground(getActivity());
                manualBackground.execute(bednumString, isoString);


                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    onRefresh();
                    onRefresh();
                    onRefresh();
                    onRefresh();
                    rb.setChecked(true);
                    rb1.setChecked(false);

                    Toast.makeText(getActivity(),  bednumString + " " + isoStatusString + " message sent", Toast.LENGTH_LONG).show();


            }
        });



        return view;
    }

    private class MyListAdaper extends BaseAdapter {
        LayoutInflater mInflater;
        public int layout;
        ArrayList<MyObject> listadapter = new ArrayList<MyObject>();


        private MyListAdaper(Context c, int resource, ArrayList<MyObject> list) {
            layout = resource;
            mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.listadapter = list;

        }

        public void updateResults(ArrayList<MyObject> results) {
//
            listadapter.clear();
            listadapter.addAll(results);


            //Triggers the list update
            notifyDataSetChanged();

//            lv.setAdapter(myListAdapter);
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            Log.i("thread", "in notify");
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            convertView = inflater.inflate(layout, parent, false);

            Configuration config = getResources().getConfiguration();
            View view;
            if(config.smallestScreenWidthDp <= 320) {
                convertView = getLayoutInflater().inflate(R.layout.list_item2_small, null);
            }
            else {
                convertView = getLayoutInflater().inflate(R.layout.list_item2, null);
            }
//            convertView = getLayoutInflater().inflate(R.layout.list_item2, null);
            TextView dateTV = (TextView) convertView.findViewById(R.id.dateTV);
//            TextView cardTV = (TextView)convertView.findViewById(R.id.cardTV);
            TextView statusTV = (TextView)convertView.findViewById(R.id.statusTV);

//            String datetemp =listadapter.get(position).logtime1.toString();
//            datetemp = datetemp.substring(5);
            if (position % 2 == 1) {

                convertView.setBackgroundColor(rgb(220,220,220));
            } else {
//                convertView.setBackgroundColor(Color.WHITE);
            }
            String datetemp;
            String dateMonth = listadapter.get(position).logtime1.toString();
            dateMonth = dateMonth.substring(5, 7);
            String dateDay = listadapter.get(position).logtime1.toString();
            dateDay = dateDay.substring(8,10);
            String dateTime = listadapter.get(position).logtime1.toString();
            dateTime = dateTime.substring(11);
            datetemp = dateDay + "-" + dateMonth + " " + dateTime;

            dateTV.setText(datetemp);

//            cardTV.setText(listadapter.get(position).serialdata101.toString());
//            cardTV.setText("-");
//            dateTV.setText(logtime[position]);
//            cardTV.setText(serialdata10[position]);

            if(listadapter.get(position).cleanstatus1.toString().equals("0")) {
                statusTV.setText("Bed " + bedNumData10[position] + " Discharge Cleaning");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("1")) {
                statusTV.setText("Bed " +bedNumData10[position] + " Toilet");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("2")) {
                statusTV.setText("Bed " +bedNumData10[position] + " Mopping");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("3")) {
                statusTV.setText("Bed " +bedNumData10[position] + " Rubbish Bin");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("4")) {
                statusTV.setText("Bed " +bedNumData10[position] + " Internal Transfer");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("5")) {
                statusTV.setText("Bed " +bedNumData10[position] + " ISO Discharge Cleaning");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("6")) {
                statusTV.setText("Bed " +bedNumData10[position] + " ISO Toilet");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("7")) {
                statusTV.setText("Bed " +bedNumData10[position] + " ISO Mopping");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("8")) {
                statusTV.setText("Bed " +bedNumData10[position] + " ISO Rubbish Bin");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("9")) {
                statusTV.setText("Bed " +bedNumData10[position] + " ISO Internal Transfer");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("10")) {
                statusTV.setText("Staff room");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("11")) {
                statusTV.setText("Nurses counter");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("12")) {
                statusTV.setText("Kitchen");

            }
            else if(listadapter.get(position).cleanstatus1.toString().equals("13")) {
                statusTV.setText("Corridors");

            }



            Log.i("thread", "in convertview");

            return convertView;
        }

        @Override
        public int getCount() {
            Log.i("thread", "in getcount");
            return listadapter.size();

        }

        @Override
        public Object getItem(int position) {
            Log.i("thread", "in getitem");
            return listadapter.get(position);

        }

        @Override
        public long getItemId(int position) {
            Log.i("thread", "in getitemid");
            return position;
        }
    }



    private void getLast10Bed() {
        JSONArray ja = null;
//        String address = "http://weijietest.000webhostapp.com/Mah/manualLogs.php";
        String address = initAddress+"manualLogs.php";
        try {
            URL url = new URL(address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);

            is = new BufferedInputStream(con.getInputStream());

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //Read content
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            while((line1=br.readLine()) != null) {
                sb.append(line1 + "\n");
            }
            is.close();

            result1=sb.toString();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //Parse JSON DATA
        try
        {
//            if(result1.substring(0,4).equals("null")){
//                return null;
//            }
            ja = new JSONArray(result1);
            JSONObject jo =  null;

            bedNumData10 = new String[ja.length()];
            serialdata10 = new String[ja.length()];
            logtime = new String[ja.length()];
            logstatus = new String[ja.length()];
            cleanStatus = new String[ja.length()];

            for (int i = 0; i < ja.length(); i++) {

                jo = ja.getJSONObject(i);
                bedNumData10[i] = jo.getString("bednum");
                serialdata10[i] = jo.getString("serialnum");
                logtime[i] = jo.getString("logtime");
                logstatus[i] = jo.getString("logstatus");
                cleanStatus[i] = jo.getString("cleanStatus");
            }
            Log.i("thread", "in getlast10beds");



        }

        catch (Exception e)
        {
            e.printStackTrace();
        }


    }



    public JSONArray getData() {
        JSONArray ja = null;
        String address = initAddress+"getbednum.php";
        try {
            URL url = new URL(address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);

            is = new BufferedInputStream(con.getInputStream());

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //Read content
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            while((line=br.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();

            result=sb.toString();

//            String firstLetter = result.substring(0,1);
//            if(firstLetter.equals("n")) {
//                sb.replace(0,sb.length(),"[{\"bedID\":\"\",\"bednum\":\"\",\"wardID\":\"\"}]"+"\n");
//                result=sb.toString();
//            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //Parse JSON DATA
        try
        {
            ja = new JSONArray(result);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ja;
    }




}
