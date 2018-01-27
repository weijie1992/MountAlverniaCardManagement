package com.example.admin.mountalverniacardmanagement;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class RoomStatus extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

//    String address = "http://weijietest.000webhostapp.com/Mah/cleaningteam.php";
    String initAddress = null;
    String address = null;
//    String initAddress = getActivity().getResources().getString(R.string.address);
//    String address = initAddress + "cleaningteam.php";
    InputStream is = null;
    String line = null;
    String result = null;
    String[] idData = new String[0];
    String[] serialnumdata = new String[0];
    String[] bedNumData = new String[0];
    String[] wardNumData = new String[0];
    String[] status = new String[0];
    String[] patientLeaveTime = new String[0];
    String[] startCleaningTime = new String[0];
    String[] endcCleaningTime = new String[0];
    String[] isoStatus = new String[0];

    ListView lv;
    SwipeRefreshLayout swipeRefreshLayout;

    TableRow row;

    ArrayList<MyObject> list;
    ArrayList<MyObject> temp = new ArrayList<MyObject>();
    MyListAdaper myListAdaper;


    public RoomStatus() {
        // Required empty public constructor
    }

    @Override
    public void onRefresh() {
        getData();
        ArrayList<MyObject> refreshlist = new ArrayList<MyObject>();
        for(int i = 0; i < idData.length; i++) {
            MyObject myObject = new MyObject(idData[i],bedNumData[i],wardNumData[i],status[i],patientLeaveTime[i],startCleaningTime[i],endcCleaningTime[i],isoStatus[i]);
            refreshlist.add(myObject);

        }
        lv.setAdapter(myListAdaper);
        myListAdaper.updateResults(refreshlist);
//        getActivity().finish();
//        startActivity(getActivity().getIntent());


        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    class MyObject {
        String idDataStr = null;
        //        String serialnumdataStr = null;
        String bedNumDataStr = null;
        String wardNumDataStr = null;
        String statusStr = null;
        String patientLeaveTimeStr = null;
        String startCleaningTimeStr = null;
        String endcCleaningTimeStr = null;
        String isoStatusstr = null;


        public MyObject(String IDS, String BNDS, String WNDS,String SS,String PLTS, String SCTS, String ECTS, String iss) {
            idDataStr = IDS;
//            serialnumdataStr = SNDS;
            bedNumDataStr = BNDS;
            wardNumDataStr = WNDS;
            statusStr = SS;
            patientLeaveTimeStr = PLTS;
            startCleaningTimeStr = SCTS;
            endcCleaningTimeStr = ECTS;
            isoStatusstr = iss;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Configuration config = getResources().getConfiguration();
        View view;
        if(config.smallestScreenWidthDp <= 320) {
            view = inflater.inflate(R.layout.fragment_room_status_small, container, false);
        }
        else {
            view = inflater.inflate(R.layout.fragment_room_status, container, false);
        }
//        View view = inflater.inflate(R.layout.fragment_room_status, container, false);

        initAddress = Address.getAddress();
        address = initAddress + "cleaningteam.php";

        lv = (ListView)view.findViewById(R.id.listview);
//        Resources resource = getView().getResources();
        row = (TableRow)view.findViewById(R.id.tableRow);
        row.setBackgroundColor(Color.GRAY);
        getData();

        list = new ArrayList<MyObject>();
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        for(int i = 0; i < idData.length; i++) {
            MyObject myObject = new MyObject(idData[i],bedNumData[i],wardNumData[i],status[i],
                    patientLeaveTime[i],startCleaningTime[i],endcCleaningTime[i],isoStatus[i]);
            list.add(myObject);
        }
        myListAdaper = new MyListAdaper(getActivity(),R.layout.list_item ,list);
        lv.setAdapter(myListAdaper);

        return view;
    }

    private void getData()
    {
        try {
            URL url = new URL(address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);

            is = new BufferedInputStream(con.getInputStream());



        }
        catch (Exception e) {
            e.printStackTrace()    ;
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
            String firstLetter = result.substring(0,1);
            if(firstLetter.equals("<")) {

//                idData = new String[1];
//                status = new String[1];
//                bedNumData = new String[1];
//                wardNumData = new String[1];
//                patientLeaveTime = new String[1];
//                startCleaningTime = new String[1];
//                endcCleaningTime = new String[1];

                idData[0] = null;
                status[0] = null;
                bedNumData[0] = null;
                wardNumData[0] = null;
                patientLeaveTime[0] = null;
                startCleaningTime[0] = null;
                endcCleaningTime[0] = null;
                isoStatus[0] = null;
                lv.setAdapter(null);
                return;

//                idData = new String[1];
//                // serialnumdata = new String[ja.length()];
//                status = new String[1];
//                bedNumData = new String[1];
//                wardNumData = new String[1];
//                patientLeaveTime = new String[1];
//                startCleaningTime = new String[1];
//                endcCleaningTime = new String[1];

//
//                getActivity().finish();
//                startActivity(getActivity().getIntent());

            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //Parse JSON DATA

        try
        {
            JSONArray ja = new JSONArray(result);
            if(ja.length() == 0) {
//                idData[0] = null;
//
//
//                status[0] = null;
//                bedNumData[0] = null;
//                wardNumData[0] = null;
//                patientLeaveTime[0] = null;
//                startCleaningTime[0] = null;
//                endcCleaningTime[0] = null;
//                return;

            } else {
                JSONObject jo = null;

                idData = new String[ja.length()];
                // serialnumdata = new String[ja.length()];
                status = new String[ja.length()];
                bedNumData = new String[ja.length()];
                wardNumData = new String[ja.length()];
                patientLeaveTime = new String[ja.length()];
                startCleaningTime = new String[ja.length()];
                endcCleaningTime = new String[ja.length()];
                isoStatus = new String[ja.length()];
//            Log.i("ja", ja.length().toString());
                for (int i = 0; i < ja.length(); i++) {
                    jo = ja.getJSONObject(i);
                    //idData[i] = jo.getString("id");
                    idData[i] = jo.getString("id");
//                if(jo.getString("serialnum") == null) {
//                    serialnumdata[i] = "123";
//                } else {
//                    serialnumdata[i] = jo.getString("serialnum");
//                }

                    status[i] = jo.getString("status");
                    bedNumData[i] = jo.getString("bednum");
                    wardNumData[i] = jo.getString("wardID");
                    patientLeaveTime[i] = jo.getString("patientLeaveTime");
                    startCleaningTime[i] = jo.getString("startCleaning");
                    endcCleaningTime[i] = jo.getString("endCleaning");
                    isoStatus[i] = jo.getString("cleanStatus");
//                if(jo.getString("patientLeaveTime") != null) {
//                    patientLeaveTime[i] = jo.getString("patientLeaveTime");
//                }
//                if(jo.getString("startCleaning") != null) {
//                    startCleaningTime[i] = jo.getString("startCleaning");
//                }

//                if(startCleaningTime[i].equals("null") || startCleaningTime[i] == null || startCleaningTime[i] == "null") {
//                    startCleaningTime[i] = patientLeaveTime[i];
//                }
                }
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    private class MyListAdaper extends BaseAdapter {
        LayoutInflater mInflater;
        private int layout;
        ArrayList<MyObject> listAdapter = new ArrayList<MyObject>();

        private MyListAdaper(Context c, int resource, ArrayList<MyObject> list) {
            layout = resource;
            mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.listAdapter = list;
        }



        public void updateResults(ArrayList<MyObject> results) {

//           listAdapter.removeAll(listAdapter);
//            listAdapter.remove();

            listAdapter.clear();
            listAdapter.addAll(results);

//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            //Triggers the list update
            notifyDataSetChanged();
        }




        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //ViewHolder mainViewholder = null;
            if(idData[0] != null ) {
//                if (convertView == null) {
                Log.i("thread", "in getview");

                Configuration config = getResources().getConfiguration();
                View view;
                if(config.smallestScreenWidthDp <= 320) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item1_small, null);
                }
                else {
                    convertView = getLayoutInflater().inflate(R.layout.list_item1, null);
                }

//                convertView = getLayoutInflater().inflate(R.layout.list_item1, null);
//                final Button startBtn = (Button) convertView.findViewById(R.id.startBtn);
//                TextView wardnumTV = (TextView) convertView.findViewById(R.id.wardNumTV);
                TextView bednumTV = (TextView) convertView.findViewById(R.id.bedNumTV);
                TextView timeTV = (TextView) convertView.findViewById(R.id.timeTV);
                TextView remarksTV = (TextView) convertView.findViewById(R.id.RemarksTV);
                TextView cleanTV = (TextView) convertView.findViewById(R.id.cleanTV);
                Button finishBtn = (Button)convertView.findViewById(R.id.finishBtn);


                String patientLeaveTimeString = null;
                String startCleaningTimeString = null;
                String endCleaningTimeString = null;
                String P1 = null, S1 = null, E1 = null;
//                if (listAdapter.size() > 0) {
                final String idDataString = listAdapter.get(position).idDataStr.toString();
                //final String serialNumString = listAdapter.get(position).serialnumdataStr.toString();
                final String wardnumString = listAdapter.get(position).wardNumDataStr.toString();
                final String bednumString = listAdapter.get(position).bedNumDataStr.toString();
                final String spinnerValue = listAdapter.get(position).statusStr.toString();
                final String isoStatusString = listAdapter.get(position).isoStatusstr.toString();
                patientLeaveTimeString = listAdapter.get(position).patientLeaveTimeStr.toString();
                if (!patientLeaveTimeString.equals("null")) {
                    P1 = patientLeaveTimeString.substring(11, 16);
                }

                startCleaningTimeString = listAdapter.get(position).startCleaningTimeStr.toString();
                if (!startCleaningTimeString.equals("null")) {
                    S1 = startCleaningTimeString.substring(11, 16);
                }

                endCleaningTimeString = listAdapter.get(position).endcCleaningTimeStr.toString();

                if (!endCleaningTimeString.equals("null")) {
                    E1 = endCleaningTimeString.substring(11, 16);
                }


                if(isoStatusString.equals("0")) {
                    remarksTV.setText("Discharge");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.home, 0, 0, 0);

                } else if (isoStatusString.equals("1")) {
                    remarksTV.setText("Toilet");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.toilet, 0, 0, 0);
                } else if (isoStatusString.equals("2")) {
                    remarksTV.setText("Mopping");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mopping, 0, 0, 0);
                } else if (isoStatusString.equals("3")) {
                    remarksTV.setText("Rubbish");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rubbishbin, 0, 0, 0);
                } else if (isoStatusString.equals("4")) {
                    remarksTV.setText("Internal Transfer");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.internaltransfer, 0, 0, 0);
                } else if (isoStatusString.equals("5")) {
                    remarksTV.setText("ISO Discharge");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.isodischarge, 0, 0, 0);
                } else if (isoStatusString.equals("6")) {
                    remarksTV.setText("ISO Toilet");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.toilet, 0, 0, 0);
                } else if (isoStatusString.equals("7")) {
                    remarksTV.setText("ISO Mopping");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mopping, 0, 0, 0);
                } else if (isoStatusString.equals("8")) {
                    remarksTV.setText("ISO Rubbish");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rubbishbin, 0, 0, 0);
                } else if (isoStatusString.equals("9")) {
                    remarksTV.setText("ISO Internal Transfer");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.internaltransfer, 0, 0, 0);
                }
                else if (isoStatusString.equals("10")) {
                    remarksTV.setText("Staff Room");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.staffroom, 0, 0, 0);
                }
                else if (isoStatusString.equals("11")) {
                    remarksTV.setText("Nurse Counter");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.nursescounter, 0, 0, 0);
                }
                else if (isoStatusString.equals("12")) {
                    remarksTV.setText("Kitchen");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.kitchen, 0, 0, 0);
                }
                else if (isoStatusString.equals("13")) {
                    remarksTV.setText("Corridor");
                    remarksTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.corridor, 0, 0, 0);
                }

//
                if(bednumString.equals("")) {
                    bednumTV.setText("-");
                }
                else {
                    bednumTV.setText(bednumString);
                }

                if (spinnerValue.equals("1")) {
                    if(isoStatusString.equals("0")||isoStatusString.equals("1")
                            ||isoStatusString.equals("2")||isoStatusString.equals("3")
                            ||isoStatusString.equals("4") ||isoStatusString.equals("10")
                            ||isoStatusString.equals("11") ||isoStatusString.equals("12")
                            ||isoStatusString.equals("13"))  {
                        convertView.setBackgroundColor(Color.WHITE);
                    }
//                    convertView.setBackgroundColor(Color.parseColor("#a52a2a"));
                    else if(isoStatusString.equals("5")||isoStatusString.equals("6")
                            ||isoStatusString.equals("7")||isoStatusString.equals("8")
                            ||isoStatusString.equals("9"))
                    {
                        convertView.setBackgroundColor(Color.RED);
                    }

                    //wardnumTV.setBackgroundColor(Color.RED);
//                    wardnumTV.setText(wardnumString);
//                    startBtn.setBackgroundResource(R.drawable.playicon);
//                    startBtn.setText("Start");
                    //bednumTV.setBackgroundColor(Color.YELLOW);
                    finishBtn.setVisibility(View.INVISIBLE);

                    cleanTV.setText("To Clean");
                    timeTV.setText(P1);
//                    if (P1 != null) {
//                        timeTV.setText(P1);
//                    }



                } else if (spinnerValue.equals("2")) {
                    //wardnumTV.setBackgroundColor(Color.RED);
                    convertView.setBackgroundColor(Color.YELLOW);
//                    wardnumTV.setText(wardnumString);
//                    startBtn.setText("End");
                    Log.i("thread", "in yellow");
//                    startBtn.setBackgroundResource(R.drawable.stopicon);
                    //bednumTV.setBackgroundColor(Color.RED);
//                    bednumTV.setText(bednumString);
                    timeTV.setText(S1);
                    finishBtn.setVisibility(View.INVISIBLE);
                    cleanTV.setText("Cleaning");
//                    if (S1 != null) {
//                        timeTV.setText(S1);
//                    }


                } else if (spinnerValue.equals("0")) {
                    //wardnumTV.setBackgroundColor(Color.RED);
                    convertView.setBackgroundColor(Color.GREEN);
//                    wardnumTV.setText(wardnumString);
//                    startBtn.setVisibility(View.INVISIBLE);
                    Log.i("thread", "in yellow");
//                    startBtn.setBackgroundResource(R.drawable.stopicon);
                    //bednumTV.setBackgroundColor(Color.RED);
//                    bednumTV.setText(bednumString);
                    timeTV.setText(E1);
                    finishBtn.setVisibility(View.INVISIBLE);
                    cleanTV.setText("Cleaned");
//                    if (E1 != null) {
//                        timeTV.setText(E1);
//                    }

                }

                else if (spinnerValue.equals("3")) {
                    //wardnumTV.setBackgroundColor(Color.RED);
                    convertView.setBackgroundColor(Color.parseColor("#FEBF3A"));
//                    wardnumTV.setText(wardnumString);
//                    cleanTV.setText("ISO AIRING");
                    cleanTV.setVisibility(View.INVISIBLE);
                    finishBtn.setText("Finish");

//
                    timeTV.setText(E1);


                }
                finishBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FinishISO finishISO = new FinishISO(getActivity());
                        finishISO.execute(idDataString,bednumString);
                        try {
                            TimeUnit.MILLISECONDS.sleep(700);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getActivity(), "Ended ISO", Toast.LENGTH_SHORT).show();
                        onRefresh();

                    }
                });



            }


            return convertView;
        }

        @Override
        public int getCount() {
            return listAdapter.size();
        }

        @Override
        public Object getItem(int i) {
            return listAdapter.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
    }

}
