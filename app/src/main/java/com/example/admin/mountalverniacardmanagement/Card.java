package com.example.admin.mountalverniacardmanagement;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.IOException;
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
public class Card extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    Context context;
    String newSerial;
    String initAddress = null;
//    String address = null;
//    String address = "http://weijietest.000webhostapp.com/Mah/getbednum.php";
    String bednumDisplay = null;

    Spinner bedSpinner, wardSpinner;
    EditText serialET, craET;

    RadioButton rb,rb1;
    RadioGroup rg;
    int radioInt;

    Button insert, clear;
    ListView lv;
    SwipeRefreshLayout swipeRefreshLayout;

    String line1 = null;
    String result1 = null;
    InputStream is = null;
    String line = null;
    String result = null;
    int methodcounter, methodcounterPost = 0;

    String[] wardNumData = new String[0];
    String[] bedNumData = new String[0];
    String[] bedNumData10 = new String[0];
    String[] serialdata10 = new String[0];
    String[] logtime = new String[0];
    String[] logstatus = new String[0];
    String[] cleanStatus = new String[0];


    String logstatusdisplay =null;
    String cleanstatusdisplay = null;

    MyListAdaper myListAdapter;

    @Override
    public void onRefresh() {
        getLast10Bed();
        ArrayList<MyObject> list10 = new ArrayList<MyObject>();
        for(int i = 0; i < bedNumData10.length; i++) {
            MyObject myObject = new MyObject(bedNumData10[i],serialdata10[i],logtime[i],logstatus[i],cleanStatus[i]);
            list10.add(myObject);
        }
        myListAdapter.updateResults(list10);

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



    public Card() {

        // Required empty public constructor
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
//        swipeRefreshLayout.setOnRefreshListener(this);
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Configuration config = getResources().getConfiguration();
        View view;
        if(config.smallestScreenWidthDp <= 320) {
             view = inflater.inflate(R.layout.fragment_card_small, container, false);
        }
        else {
             view = inflater.inflate(R.layout.fragment_card, container, false);
        }

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));

        initAddress = Address.getAddress();
//        isoSpinner = (Spinner)view.findViewById(R.id.chooseISO);
        bedSpinner = (Spinner)view.findViewById(R.id.bedSpinner);
        wardSpinner = (Spinner)view.findViewById(R.id.wardSpinner);
        serialET = (EditText)view.findViewById(R.id.scanET);
        serialET.setInputType(InputType.TYPE_NULL);
        craET = (EditText)view.findViewById(R.id.craET);
        craET.setInputType(InputType.TYPE_NULL);
        insert = (Button) view.findViewById(R.id.addtoDB);
        clear = (Button)view. findViewById(R.id.clearBtn);
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
                          radioInt = 5;
                          break;

                  }

              }
          });
        //Assign Button
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String serialETString = serialET.getText().toString();
//                String isoSpinnerString = Integer.toString(isoSpinner.getSelectedItemPosition());
//                String radioString = Integer.toString(radioInt);

                if (serialETString == null || serialETString.equals("")) {
                    Toast.makeText(getActivity(), "Scan card", Toast.LENGTH_LONG).show();
                } else {
                    String radioString = Integer.toString(radioInt);
                    String type = "add";
                    String bedSpinnerString = bedSpinner.getSelectedItem().toString();
                    String wardid = "SG";
                    insert.setEnabled(false);
                    backgroundWorker BackgroundWorker = new backgroundWorker(getActivity());
                    BackgroundWorker.execute(type, serialETString, bedSpinnerString, wardid, radioString);

                    onRefresh();
                    onRefresh();

//                    getLast10Bed();
//                    ArrayList<MyObject> list10 = new ArrayList<MyObject>();
//                    for (int i = 0; i < bedNumData10.length; i++) {
//                        MyObject myObject = new MyObject(bedNumData10[i], serialdata10[i], logtime[i], logstatus[i], cleanStatus[i]);
//                        list10.add(myObject);
//                    }
//                    //refresh list only
//                    lv.setAdapter(myListAdapter);
//                    myListAdapter.updateResults(list10);
////                    lv.setAdapter(myListAdapter);

                    insert.setEnabled(true);
                    Toast.makeText(getActivity(), "Card Updated to Room : " + bedSpinnerString, Toast.LENGTH_LONG).show();
                    serialET.setText("");
                    craET.setText("");
                    rb.setChecked(true);
                    rb1.setChecked(false);


                }
            }
        });

        //Clear Button
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String serialETString = serialET.getText().toString();

                if (serialETString == null || serialETString.equals("")) {
                    Toast.makeText(getActivity(), "Scan card", Toast.LENGTH_LONG).show();
                }
                else {
                    String type = "delete";
//                    String bedSpinnerString = bedSpinner.getSelectedItem().toString();
                    String wardid = "SG";
//                String isoSpinnerString = Integer.toString(isoSpinner.getSelectedItemPosition());
                    String radioString = Integer.toString(radioInt);
                    backgroundWorker BackgroundWorker = new backgroundWorker(getActivity());
                    BackgroundWorker.execute(type, serialETString, bednumDisplay, wardid, radioString);

                    onRefresh();
                    onRefresh();

                    Toast.makeText(getActivity(), "Card Remove from Room : " + bednumDisplay, Toast.LENGTH_LONG).show();

                    serialET.setText("");
                    craET.setText("");
                    rb.setChecked(true);
                    rb1.setChecked(false);
                }

            }
        });

        //Pull Down refresh
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

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
            bedSpinner.setAdapter(spinnerAdapter);
            bedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                    bedSpinner.setSelection(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

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



        getLast10Bed();

        ArrayList<MyObject> list = new ArrayList<MyObject>();
        for(int i = 0; i < bedNumData10.length; i++) {
            MyObject myObject = new MyObject(bedNumData10[i],serialdata10[i],logtime[i],logstatus[i],cleanStatus[i]);
            list.add(myObject);
        }

        myListAdapter = new MyListAdaper(getActivity(),R.layout.list_item ,list);

//        myListAdapter.notifyDataSetChanged();
        lv.setAdapter(myListAdapter);



        return view;
    }

    public void serialETSetText(String value){
       methodcounter++;
//       Log.i("methodCounter")
       newSerial = value;
        serialET.setText(value);
        bednumDisplay = getserialnum(value);


        if(bednumDisplay != null) {
            if(logstatusdisplay.equals("1")) {
                craET.setText(bednumDisplay + " to be clean");
                clear.setEnabled(false);
                insert.setEnabled(false);
            }
            else if(logstatusdisplay.equals("2")) {
                craET.setText(bednumDisplay + " is cleaning");
                clear.setEnabled(false);
                insert.setEnabled(false);
            }
            else if(logstatusdisplay.equals("3")) {
                craET.setText(bednumDisplay + " is ISO Airing");
                clear.setEnabled(false);
                insert.setEnabled(false);
            }

            else {
                if(cleanstatusdisplay.equals("0")) {
                    craET.setText(bednumDisplay + " Normal");
                    clear.setEnabled(true);
                    insert.setEnabled(true);
                }
                else if(cleanstatusdisplay.equals("5")) {
                    craET.setText(bednumDisplay + " ISO");
                    clear.setEnabled(true);
                    insert.setEnabled(true);
                }

            }



        }
        else {
            craET.setText("Card not set");
            clear.setEnabled(false);
//            insert.setEnabled(true);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Log.i("MD", Integer.toString(methodcounter));
//                Log.i("MDP", Integer.toString(methodcounterPost));
                methodcounterPost++;
                if(methodcounter == methodcounterPost) {
                    clearET();

                }
            }
        }, 5000);



//
}

    public void clearET() {
        serialET.setText("");
        craET.setText("");
    }


    private class MyListAdaper extends BaseAdapter {
        LayoutInflater mInflater;
        //        String[] logtime;
//        String[] logstatus;
//        String[] serialdata10;
//        String[] bedNumData10;
        ArrayList<MyObject> listadapter = new ArrayList<MyObject>();
        String[] arrayResults;

        private int layout;
        //        private List<String> mObjects;
        private MyListAdaper(Context c, int resource, ArrayList<MyObject> list) {

            layout = resource;
            this.listadapter = list;
//            this.bedNumData10 = bedNumData10;
//            this.logtime = logtime;
//            this.logstatus = logstatus;
//            this.serialdata10 = serialdata10;
            mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void updateResults(ArrayList<MyObject> results) {

            listadapter.clear();
            listadapter.addAll(results);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Triggers the list update
            notifyDataSetChanged();
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Configuration config = getResources().getConfiguration();
            View view;
            if(config.smallestScreenWidthDp <= 320) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_small, null);
            }
            else {
                convertView = getLayoutInflater().inflate(R.layout.list_item, null);
            }

//            convertView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView dateTV = (TextView) convertView.findViewById(R.id.dateTV);
            TextView cardTV = (TextView)convertView.findViewById(R.id.cardTV);
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
            datetemp = dateDay + "-" + dateMonth +" " +dateTime;

            dateTV.setText(datetemp);

            cardTV.setText(listadapter.get(position).serialdata101.toString());
//            dateTV.setText(logtime[position]);
//            cardTV.setText(serialdata10[position]);
            if(listadapter.get(position).logstatus1.toString().equals("1")) {
                if(listadapter.get(position).cleanstatus1.toString().equals("0")){
                    statusTV.setText(" Assigned to " + bedNumData10[position] + " Normal");
                }
                else if(listadapter.get(position).cleanstatus1.toString().equals("5")){
                    statusTV.setText(" Assigned to " + bedNumData10[position] + " ISO");
                }

            }

            else if(listadapter.get(position).logstatus1.toString().equals("2")) {
                if(listadapter.get(position).cleanstatus1.toString().equals("0")){
                    statusTV.setText(" Reassigned to " + bedNumData10[position] + " Normal");
                }
                else if(listadapter.get(position).cleanstatus1.toString().equals("5")){
                    statusTV.setText(" Reassigned to " + bedNumData10[position] + " ISO");
                }

            }

            else if(listadapter.get(position).logstatus1.toString().equals("3")) {
                statusTV.setText(" Unassigned from " + bedNumData10[position]);
            }




            return convertView;
        }

        @Override
        public int getCount() {
            return listadapter.size();
        }

        @Override
        public Object getItem(int position) {
            return listadapter.get(position);

        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }


    private void getLast10Bed() {
        JSONArray ja = null;
        String address = initAddress + "getlogs.php";
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



        }

        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    public String getserialnum(String nfcSerial) {
        String bednumDisplay = null;

//        String get_url = "http://weijietest.000webhostapp.com/Mah/getSerialnum.php?serialnum=";
        String get_url =initAddress +"getSerialnum.php?serialnum=";
        JSONArray ja = null;
        try {
            String finalurl = get_url + nfcSerial;
            URL url = new URL(finalurl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);


            is = new BufferedInputStream(httpURLConnection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
//            String firstLetter = result.substring(0,1);
//            if(firstLetter.equals("<")) {
//                return null;
//            }
            ja = new JSONArray(result);

            JSONObject jo = null;


            for (int i = 0; i < ja.length(); i++) {

                jo = ja.getJSONObject(i);
                bednumDisplay = jo.getString("bednum");
                logstatusdisplay = jo.getString("status");
                cleanstatusdisplay = jo.getString("cleanStatus");
            }

        } catch (JSONException e) {

            e.printStackTrace();

        }


        return bednumDisplay;
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
