package com.example.admin.mountalverniacardmanagement;


import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import static android.graphics.Color.rgb;

/**
 * A simple {@link Fragment} subclass.
 */
public class Reports extends Fragment {

    String initAddress = null;
    EditText startDate;
    EditText endDate;
    Button download,viewBtn;
    ListView viewListView;
    MyListAdaper myListAdapter;

    DatePickerDialog datePickerDialog;

    String startDateChoose = null;
    String endDateChoose = null;

    String[] idData = new String[0];
    //String[] serialnumdata = new String[0];
    String[] bedNumData = new String[0];
    String[] patientleftTime = new String[0];
    String[] startCleaning = new String[0];
    String[] endCleaning = new String[0];
    String[] wardNumData = new String[0];
    String[] endingMessage = new String[0];
    String[] cleanStatus = new String[0];



    InputStream is1 = null;
    String line = null;
    String result = null;


    public Reports() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Configuration config = getResources().getConfiguration();
        View view;
        if(config.smallestScreenWidthDp <= 320) {
            view = inflater.inflate(R.layout.fragment_reports_small, container, false);
        }
        else {
            view = inflater.inflate(R.layout.fragment_reports, container, false);
        }
//        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        View header = (View)getLayoutInflater().inflate(R.layout.headerview,null);
//        viewListView.addHeaderView(header);
        initAddress = Address.getAddress();
        viewListView = (ListView)view.findViewById(R.id.viewListView);
        viewListView.addHeaderView(header);
        startDate = (EditText)view.findViewById(R.id.startdate);
//        Date today  = Calendar.getInstance().getTime();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String todayString = formatter.format(today);
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH) + 1; // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        String yearString = Integer.toString(mYear);
        String monthString = Integer.toString(mMonth);
        String dayString = Integer.toString(mDay);
        String todayString = yearString + "-" + monthString + "-" + dayString;
        startDate.setText(todayString);


        startDate.setInputType(InputType.TYPE_NULL);

        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text

                                startDateChoose = year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth;
//                                startDateChoose = dayOfMonth + "/"
//                                        + (monthOfYear + 1) + "/" + year;

                                startDate.setText(startDateChoose);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();


            }
        });

        endDate = (EditText)view.findViewById(R.id.enddate);
        endDate.setText(todayString);
        endDate.setInputType(InputType.TYPE_NULL);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text

                                endDateChoose = year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth;
//                                endDateChoose = dayOfMonth + "/"
//                                        + (monthOfYear + 1) + "/" + year;

                                endDate.setText(endDateChoose);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


        download = (Button)view.findViewById(R.id.downloadBTN);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startdateString = startDate.getText().toString();
                String enddateString = endDate.getText().toString();
                JSONArray ja = getData(startdateString, enddateString);
                JSONObject jo = null;
                String comma = ",";
//                String[] endingMessage = new String[0];



                checkDiskPermission();
                String state;
                state = Environment.getExternalStorageState();
                if(Environment.MEDIA_MOUNTED.equals(state))
                {
                    File Root = Environment.getExternalStorageDirectory();
                    File dir = new File(Root.getAbsolutePath() + "/Mt_Alvernia");
                    if (!dir.exists()){
                        dir.mkdir();
                    }
                    File file = new File(dir, "PatientLeaveToStartCleaning" + startDateChoose+ endDateChoose +".csv");
                    Root.mkdirs();

                    idData = new String[ja.length()];
//                    serialnumdata = new String[ja.length()];
//                    status = new String[ja.length()];
                    System.out.print(ja.length());
                    bedNumData = new String[ja.length()];
                    wardNumData = new String[ja.length()];
                    patientleftTime = new String[ja.length()];
                    startCleaning = new String[ja.length()];
                    endCleaning = new String[ja.length()];
                    endingMessage = new String[ja.length()];
                    cleanStatus = new String[ja.length()];

                    try {
                        for (int i = 0; i < ja.length(); i++) {

                            jo = ja.getJSONObject(i);
                            //idData[i] = jo.getString("id");
                            idData[i] = jo.getString("oldID");
                            //                        serialnumdata[i] = jo.getString("serialnum");
                            //                        status[i] = jo.getString("status");
                            bedNumData[i] = jo.getString("bednum");
                            wardNumData[i] = jo.getString("wardID");
                            patientleftTime[i] = jo.getString("patientLeaveTime");
                            startCleaning[i] = jo.getString("startCleaning");
                            endCleaning[i] = jo.getString("endCleaning");
                            cleanStatus[i] = jo.getString("cleanStatus");
                            if(cleanStatus[i].equals("0")) {
                                cleanStatus[i] = "Discharge";
                            } else if (cleanStatus[i].equals("1")) {
                                cleanStatus[i] = "Toilet";
                            } else if (cleanStatus[i].equals("2")) {
                                cleanStatus[i] = "Mopping";
                            } else if (cleanStatus[i].equals("3")) {
                                cleanStatus[i] = "Rubbish";
                            } else if (cleanStatus[i].equals("4")) {
                                cleanStatus[i] = "Internal Transfer";
                            } else if (cleanStatus[i].equals("5")) {
                                cleanStatus[i] = "ISO Discharge";
                            } else if (cleanStatus[i].equals("6")) {
                                cleanStatus[i] = "ISO Toilet";
                            } else if (cleanStatus[i].equals("7")) {
                                cleanStatus[i] = "ISO Mopping";
                            } else if (cleanStatus[i].equals("8")) {
                                cleanStatus[i] = "ISO Rubbish";
                            } else if (cleanStatus[i].equals("9")) {
                                cleanStatus[i] = "ISO Internal Transfer";
                            }
                            else if (cleanStatus[i].equals("10")) {
                                cleanStatus[i] = "Staff Room";
                            }
                            else if (cleanStatus[i].equals("11")) {
                                cleanStatus[i] = "Nurses Counter";
                            }
                            else if (cleanStatus[i].equals("12")) {
                                cleanStatus[i] = "Kitchen";
                            }
                            else if (cleanStatus[i].equals("13")) {
                                cleanStatus[i] = "Corridor";
                            }
                            if(bedNumData[i] == null || bedNumData[i].equals("")) {
                                endingMessage = null;
                            }
                            else {
                                endingMessage[i] = wardNumData[i] + comma + bedNumData[i] + comma
                                        + patientleftTime[i] + comma + startCleaning[i]
                                        + comma +endCleaning[i] + comma + cleanStatus[i] +"\n";
                            }


                        }
                    } catch (JSONException e) {
                        endingMessage = null;
                        e.printStackTrace();
                    }

                    //json data
                    //String message = "testing123";
                    // String message = editText.getText().toString();

                    try {
//                        if(endingMessage[0].equals("") || endingMessage[0] == null){
//                                Toast.makeText(getContext(), "No data to download",Toast.LENGTH_LONG).show();
//                        }
                        if(endingMessage == null || endingMessage[0].equals("")){

                            Toast.makeText(getContext(), "No data to download",Toast.LENGTH_LONG).show();
                        }
                        else {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            for (int i = 0; i < endingMessage.length; i++) {
                                fileOutputStream.write(endingMessage[i].getBytes());
                            }
                            fileOutputStream.close();
                            //editText.setText("");
//                        Toast.makeText(getContext(), "Download Completed",Toast.LENGTH_LONG).show();

                            Toast.makeText(getContext(), "File located at My Files -> Internal Storage -> Mt_Alvernia", Toast.LENGTH_LONG).show();
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }
        });

        viewBtn = (Button)view.findViewById(R.id.viewBTN);
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                viewListView.removeHeaderView(header);
//                header.setVisibility(View.GONE);
                String startdateString = startDate.getText().toString();
                String enddateString = endDate.getText().toString();
                JSONArray ja = getData(startdateString, enddateString);
                JSONObject jo = null;
//                String comma = ",";

                idData = new String[ja.length()];
//                    serialnumdata = new String[ja.length()];
//                    status = new String[ja.length()];
                System.out.print(ja.length());
                bedNumData = new String[ja.length()];
                wardNumData = new String[ja.length()];
                patientleftTime = new String[ja.length()];
                startCleaning = new String[ja.length()];
                endCleaning = new String[ja.length()];
                endingMessage = new String[ja.length()];
                cleanStatus = new String[ja.length()];

                try {
                    for (int i = 0; i < ja.length(); i++) {

                        jo = ja.getJSONObject(i);
                        //idData[i] = jo.getString("id");
                        idData[i] = jo.getString("oldID");
                        //                        serialnumdata[i] = jo.getString("serialnum");
                        //                        status[i] = jo.getString("status");
                        cleanStatus[i] = jo.getString("cleanStatus");
                        bedNumData[i] = jo.getString("bednum");
                        wardNumData[i] = jo.getString("wardID");
                        patientleftTime[i] = jo.getString("patientLeaveTime");
                        startCleaning[i] = jo.getString("startCleaning");
                        endCleaning[i] = jo.getString("endCleaning");

//                        if(cleanStatus[i].equals("0")) {
//                            cleanStatus[i] = "Discharge";
//                        } else if (cleanStatus[i].equals("1")) {
//                            cleanStatus[i] = "Toilet";
//                        } else if (cleanStatus[i].equals("2")) {
//                            cleanStatus[i] = "Mopping";
//                        } else if (cleanStatus[i].equals("3")) {
//                            cleanStatus[i] = "Rubbish";
//                        } else if (cleanStatus[i].equals("4")) {
//                            cleanStatus[i] = "Internal Transfer";
//                        } else if (cleanStatus[i].equals("5")) {
//                            cleanStatus[i] = "ISO Discharge";
//                        } else if (cleanStatus[i].equals("6")) {
//                            cleanStatus[i] = "ISO Toilet";
//                        } else if (cleanStatus[i].equals("7")) {
//                            cleanStatus[i] = "ISO Mopping";
//                        } else if (cleanStatus[i].equals("8")) {
//                            cleanStatus[i] = "ISO Rubbish";
//                        } else if (cleanStatus[i].equals("9")) {
//                            cleanStatus[i] = "ISO Internal Transfer";
//                        }


//                        endingMessage[i] = wardNumData[i] + comma + bedNumData[i] + comma + patientleftTime[i] + comma + startCleaning[i] + comma +endCleaning[i] + "\n";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }




                myListAdapter = new MyListAdaper(getActivity(),R.layout.list_item3 ,bedNumData,wardNumData,patientleftTime,startCleaning,endCleaning,cleanStatus);
//                if(bedNumData.length > 1) {
//                    header.setVisibility(View.VISIBLE);
//                    viewListView.addHeaderView(header);
//
//
//                }
                viewListView.setAdapter(myListAdapter);



            }
            });
        return view;
    }

    private JSONArray getData(String stringStartDate, String stringEndDate) {
        String PTime = stringStartDate;
        String STime = stringEndDate;
        JSONArray ja = null;
        String downloadURL = null;

//        downloadURL = "http://weijietest.000webhostapp.com/Mah/getTime.php?" + "Ptime=" + PTime +"&Stime="+ STime;
        downloadURL = initAddress+"getTime.php?" + "Ptime=" + PTime +"&Stime="+ STime;



        try {
            URL url = new URL(downloadURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);

            is1 = new BufferedInputStream(con.getInputStream());



        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Read content
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(is1));
            StringBuilder sb = new StringBuilder();

            while((line=br.readLine()) != null) {
                sb.append(line + "\n");
            }
            is1.close();


//                    if(line == null) {
//                    //sb.replace(0,sb.length(),"[{\"oldID\":\"\",\"bednum\":\"\",\"wardID\":\"\",\"startCleaning\":\"\",\"endCleaning\":\"\",\"patientLeaveTime\":\"\"}]" + ",");
//                    //sb.append("[{\"oldID\":\"\",\"bednum\":\"\",\"wardID\":\"\",\"startCleaning\":\"\",\"endCleaning\":\"\",\"patientLeaveTime\":\"\"}]" + ",");
////                    sb.replace(0,sb.length(),"[{\"oldID\":\"NoBed\",\"bednum\":\"nobed\",\"wardID\":\"noward\",\"startCleaning\":\"noStart\",\"endCleaning\":\"noClean\",\"patientLeaveTime\":\"noClean\"}]" + ",");
////                    sb.append("[{\"0\":\"NoBed\",\"bednum\":\"nobed\",\"wardID\":\"noward\",\"startCleaning\":\"noStart\",\"endCleaning\":\"noClean\"}]" + ",");
////                    sb.append("[{\"wardID\":\"NoBed\"}]" + ",");
////                    sb.append("[{\"startCleaning\":\"NoBed\"}]" + ",");
////                    sb.append("[{\"endCleaning\":\"NoBed\"}]"+"\n");
//                    //sb.append("[{\"0\":\"NoBed\"}]" + "\n");
//                }
            result=sb.toString();
            String firstLetter = result.substring(0,1);
            if(firstLetter.equals("<")) {
                sb.replace(0,sb.length(),"[{\"oldID\":\"\",\"bednum\":\"\",\"wardID\":\"\",\"startCleaning\":\"\",\"endCleaning\":\"\",\"patientLeaveTime\":\"\"}]" + ",");
                result=sb.toString();
            }



        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //Parse JSON DATA
        try

        {
            ja = new JSONArray(result);
//                JSONObject jo = null;




//                for(int i = 0; i<ja.length(); i++) {
//                    jo = ja.getJSONObject(i);
//                    //idData[i] = jo.getString("id");
//                    idData[i] = jo.getString("bedID");
////                        serialnumdata[i] = jo.getString("serialnum");
////                        status[i] = jo.getString("status");
//                    bedNumData[i] = jo.getString("bednum");
//                    wardNumData[i] = jo.getString("wardID");
//                    patientleftTime[i] = jo.getString("patientLeaveTime");
//                    startCleaning[i] = jo.getString("startCleaning");
//                }





        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ja;

    }

    private class MyListAdaper extends BaseAdapter {
        LayoutInflater mInflater;
        String[] bednum;
        String[] wardnum;
        String[] patientLeave;
        String[] startCleaning;
        String[] endCleaning;
        String[] description;

        private int layout;

        private MyListAdaper(Context c, int resource,String[] bednum,String[] wardnum,String[] patientLeave,String[] startCleaning,String[] endCleaning, String[] description) {

            layout = resource;
            mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.bednum = bednum;
            this.wardnum = wardnum;
            this.patientLeave = patientLeave;
            this.startCleaning = startCleaning;
            this.endCleaning = endCleaning;
            this.description = description;

        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.list_item3, null);
//            View header = (View)getLayoutInflater().inflate(R.layout.headerview,null);
            TextView bednumTV = (TextView) convertView.findViewById(R.id.bedNumTV);
            TextView wardnumTV = (TextView)convertView.findViewById(R.id.wardNumTV);
            TextView patientLeaveTV = (TextView)convertView.findViewById(R.id.PatientLeaveTime);
            TextView startCleaningTV = (TextView)convertView.findViewById(R.id.StartCleaningTime);
            TextView endCleaningTV = (TextView)convertView.findViewById(R.id.EndCleaningTime);
            TextView descriptionTV = (TextView)convertView.findViewById(R.id.descriptionTV);

//            String datetemp =listadapter.get(position).logtime1.toString();
//            datetemp = datetemp.substring(5);



            String patientLeaveT = patientLeave[position];
            String startCleaningT = startCleaning[position];
            String endCleaningT = endCleaning[position];
//            if(!patientLeaveT.equals("")) {

//            if(!patientLeaveT.equals("")) {
            if(patientLeaveT != null) {
//                viewListView.addHeaderView(header);

                if (position % 2 == 1) {
//                    convertView.width
                    convertView.setMinimumWidth(5400);
                    convertView.setBackgroundColor(rgb(220, 220, 220));
                } else {
//                convertView.setBackgroundColor(Color.WHITE);
                }
                //set value to textview

                if(cleanStatus[position].equals("0")) {
                    cleanStatus[position] = "Discharge";
                } else if (cleanStatus[position].equals("1")) {
                    cleanStatus[position] = "Toilet";
                } else if (cleanStatus[position].equals("2")) {
                    cleanStatus[position] = "Mopping";
                } else if (cleanStatus[position].equals("3")) {
                    cleanStatus[position] = "Rubbish";
                } else if (cleanStatus[position].equals("4")) {
                    cleanStatus[position] = "Internal Transfer";
                } else if (cleanStatus[position].equals("5")) {
                    cleanStatus[position] = "ISO Discharge";
                } else if (cleanStatus[position].equals("6")) {
                    cleanStatus[position] = "ISO Toilet";
                } else if (cleanStatus[position].equals("7")) {
                    cleanStatus[position] = "ISO Mopping";
                } else if (cleanStatus[position].equals("8")) {
                    cleanStatus[position] = "ISO Rubbish";
                } else if (cleanStatus[position].equals("9")) {
                    cleanStatus[position] = "ISO Internal Transfer";
                }
                else if (cleanStatus[position].equals("10")) {
                    cleanStatus[position] = "Staff Room";
                }
                else if (cleanStatus[position].equals("11")) {
                    cleanStatus[position] = "Nurses Counter";
                }
                else if (cleanStatus[position].equals("12")) {
                    cleanStatus[position] = "Kitchen";
                }
                else if (cleanStatus[position].equals("13")) {
                    cleanStatus[position] = "Corridor";
                }

                bednumTV.setText(bednum[position]);
                wardnumTV.setText(wardnum[position]);
                descriptionTV.setText(cleanStatus[position]);


                    String pday = patientLeaveT.substring(8, 10);
                    String pmonth = patientLeaveT.substring(5, 7);
                    String pyear = patientLeaveT.substring(2, 4);
                    String ptime = patientLeaveT.substring(11, 16);
                    String pdate = pday + "/" + pmonth + "/" + pyear + " " + ptime;
                    patientLeaveTV.setText(pdate);


                    String sday = startCleaningT.substring(8, 10);
                    String smonth = startCleaningT.substring(5, 7);
                    String syear = startCleaningT.substring(2, 4);
                    String stime = startCleaningT.substring(11, 16);
                    String sdate = sday + "/" + smonth + "/" + syear + " " + stime;
                    startCleaningTV.setText(sdate);


                    String eday = endCleaningT.substring(8, 10);
                    String emonth = endCleaningT.substring(5, 7);
                    String eyear = endCleaningT.substring(2, 4);
                    String etime = endCleaningT.substring(11, 16);
                    String edate = eday + "/" + emonth + "/" + eyear + " " + etime;
                    endCleaningTV.setText(edate);
            }
            else {

//                viewListView.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "No Data" , Toast.LENGTH_LONG).show();
            }




//            String datetemp;
//            String dateMonth = patientLeave[position];
//            dateMonth = dateMonth.substring(5, 8);
//            String dateDay = startCleaning[position];
//            dateDay = dateDay.substring(8,10);
//            String dateTime = endCleaning[position];
//            dateTime = dateTime.substring(11);




            return convertView;
        }

        @Override
        public int getCount() {
            return bednum.length;
        }

        @Override
        public Object getItem(int position) {
            return bednum[position];

        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }


    private void checkDiskPermission ()
    {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "No Permissions" , Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
        else
        {
//            Toast.makeText(getActivity(), "Has Permissions" , Toast.LENGTH_LONG).show();
        }
    }

}
