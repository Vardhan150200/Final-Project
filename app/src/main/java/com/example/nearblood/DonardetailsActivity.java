package com.example.nearblood;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class DonardetailsActivity extends AppCompatActivity implements View.OnClickListener {
    DatabaseReference myRef;
    List<Details> detailsList;
    ListView listView;
    Spinner spinner;
    String group;
    String date,date1;
    CustomAdapter adapter ;
    double currentLat,currentLon;
    private static int count = 0;
    private static boolean isNotAdded = true;
    Location CurrentLocation,OtherUserLocation;
    private CheckBox checkBox_header;

    private ArrayList<String> phonnoList=new ArrayList<>();
    private static final int NOTIFICATION_REQUEST_CODE = 1234;
    /**
     * To save checked items, and <b>re-add</b> while scrolling.
     */
    SparseBooleanArray mChecked = new SparseBooleanArray();

    EditText donarEdittext;
    Button donarSend;
    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    MyAppPrefsManager myAppPrefsManager;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donardetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search Donor");
        utilites.internetCheck(DonardetailsActivity.this);

        Date cd = Calendar.getInstance().getTime();
        System.out.println("Current time => " + cd);
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        date = df.format(cd);
        //String dateInString = "2011-09-13";  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Calendar c = Calendar.getInstance(); // Get Calendar Instance
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, -180);  // add 45 days
        sdf = new SimpleDateFormat("MM/dd/yyyy");

        Date resultdate = new Date(c.getTimeInMillis());   // Get new time
        date1 = sdf.format(resultdate);
        System.out.println("String date:"+date1);

        myAppPrefsManager=new MyAppPrefsManager(this);


        donarSend=(Button)findViewById(R.id.donarSend) ;
        linearLayout=(LinearLayout) findViewById(R.id.linearLayout) ;
        relativeLayout=(RelativeLayout) findViewById(R.id.relativeLayout) ;
        donarSend.setOnClickListener(this);
        donarEdittext =(EditText)findViewById(R.id.donareditText);
        donarEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    donarSend.setEnabled(true);
                } else {
                    donarSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        listView=findViewById(R.id.listdonar);
        detailsList=new ArrayList<>();
        myRef= FirebaseDatabase.getInstance().getReference("Users");
        myRef.keepSynced(true);
        checkBox_header = (CheckBox) findViewById(R.id.checkBox_header);



        /*
         * To avoid adding multiple times
         */
        checkBox_header.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                linearLayout.setVisibility(View.VISIBLE);



                for (int i = 0; i < count; i++) {
                    mChecked.put(i, checkBox_header.isChecked());
                }


                adapter.notifyDataSetChanged();

            }
        });

        spinner=findViewById(R.id.spinnerBloodGroup);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                group=spinner.getSelectedItem().toString().trim();
                Toast.makeText(DonardetailsActivity.this, ""+group, Toast.LENGTH_SHORT).show();
                if(group!="Select Your Blood Group") {
                    Query query = myRef.orderByChild("blood").equalTo(group);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            detailsList.clear();
                            if (dataSnapshot.exists() || i == 0) {
                                // dataSnapshot is the "issue" node with all children with id 0
                                for (DataSnapshot issue : dataSnapshot.getChildren()) {

                                    // do something with the individual "issues"
                                    Details details = issue.getValue(Details.class);
                                    detailsList.add(details);

                                }
                                for (Details details:detailsList
                                     ) {
                                    if (details.getEmail().equals(myAppPrefsManager.getUserName())){
                                        currentLon = details.getLongitude();
                                        currentLat = details.getLatitude();
                                    }

                                }

                                ListIterator<Details> iter = detailsList.listIterator();
                                while (iter.hasNext()) {
                                        if (iter.next().getEmail().equals(myAppPrefsManager.getUserName())) {
//
                                            iter.remove();
                                        }


                                }
                                Toast.makeText(DonardetailsActivity.this, "" + currentLat + " " + currentLon, Toast.LENGTH_SHORT).show();
                            CurrentLocation = new Location("");
                            CurrentLocation.setLatitude(currentLat);
                            CurrentLocation.setLongitude(currentLon);
                            TreeMap<Details, String> tree_map = new TreeMap<Details, String>(new sortByLocation(CurrentLocation));
                            for (Details details: detailsList) {

                                tree_map.put(details,details.getEmail());

                            }
                            Toast.makeText(DonardetailsActivity.this, ""+tree_map, Toast.LENGTH_LONG).show();
                            detailsList.clear();
                                for (Map.Entry<Details, String>
                                        entry : tree_map.entrySet()){
                                    detailsList.add(entry.getKey());
                                }
                                if (detailsList.size() == 0 && i != 0) {
                                    relativeLayout.setVisibility(View.GONE);
                                    Toast.makeText(DonardetailsActivity.this, "No Donor's Found", Toast.LENGTH_SHORT).show();

                                }

                                adapter = new CustomAdapter(DonardetailsActivity.this, detailsList);
                                listView.setAdapter(adapter);

                                if (detailsList.size() > 0) {
                                    relativeLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                relativeLayout.setVisibility(View.GONE);
                                Toast.makeText(DonardetailsActivity.this, "No Donor's Found", Toast.LENGTH_SHORT).show();
                                listView.setAdapter(null);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(DonardetailsActivity.this, ""+databaseError, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.donarSend) {
            String donarMessage = donarEdittext.getText().toString();
            String res = donarMessage+"\nClick below link to reach me\nhttps://www.nearblood.com/"+currentLat+"/"+currentLon+"/"+myAppPrefsManager.getUserName();

            if (!donarMessage.isEmpty()) {
                donarSend.setEnabled(true);

                String toNumbers = "";

                for (String s : phonnoList) {

                    Log.e("PHONELIST", "" + s);
                    toNumbers = toNumbers + s + ";";


                }
                phonnoList.clear();
                if (!toNumbers.isEmpty()) {
                    toNumbers = toNumbers.substring(0, toNumbers.length() - 1);
                    Uri sendSmsTo = Uri.parse("smsto:" + toNumbers);
                    Intent intent = new Intent(
                            Intent.ACTION_SENDTO, sendSmsTo);
                    intent.putExtra("sms_body",res);
                    startActivity(intent);
                }
            }
        }




    }

    /*
     * CustomAdapter
     */
    public class CustomAdapter extends BaseAdapter {

        Activity sActivity;
        List<Details> detailsList;

        private CustomAdapter(Activity sActivity, List<Details> detailsList) {
            this.sActivity = sActivity;
            this.detailsList = detailsList;
        }

        @Override
        public int getCount() {


            /* Length of our listView*/

            count = detailsList.size();
            return count;
        }

        @Override
        public Object getItem(int position) {


            /*Current Item*/

            return position;
        }

        @Override
        public long getItemId(int position) {


            /*Current Item's ID*/

            return position;
        }

        @SuppressLint({"InflateParams", "SetTextI18n"})
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View mView = convertView;

            if (mView == null) {

                /*LayoutInflater*/

                final LayoutInflater sInflater = (LayoutInflater) sActivity.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

                /*Inflate Custom List View*/

                assert sInflater != null;
                mView = sInflater.inflate(R.layout.custom_lis_view, null, false);

            }


            TextView textView=(TextView)mView.findViewById(R.id.textView);
            TextView textView2=(TextView)mView.findViewById(R.id.pincode);

            textView.setText("Name: "+detailsList.get(position).getName());
            textView2.setText("Age: "+detailsList.get(position).getAge());

            final CheckBox mCheckBox=mView.findViewById(R.id.checkBox);



            mCheckBox.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                linearLayout.setVisibility(View.VISIBLE);
                                donarSend.setEnabled(true);
                                //donarEdittext.setFocusableInTouchMode(true);
                                /* * Saving Checked Position*/
                                mChecked.put(position, isChecked);
//                                checkBox_header.setChecked(isChecked);
                                phonnoList.add(detailsList.get(position).getNumber());

                                /*  * Find if all the check boxes are true*/
                                if (isAllValuesChecked()) {
                                    linearLayout.setVisibility(View.VISIBLE);
                                    //mChecked.put(position, isChecked);
                                    //donarEdittext.setFocusableInTouchMode(true);
                                    Log.e("CONDITION", "" + isAllValuesChecked());
                                    checkBox_header.setChecked(isChecked);
                                    phonnoList.add(detailsList.get(position).getNumber());
                                }

                            }
                            else {
                                linearLayout.setVisibility(View.VISIBLE);
                                /* * Removed UnChecked Position*/
                                mChecked.delete(position);
                                phonnoList.remove(detailsList.get(position).getNumber());
                                /*  * Remove Checked in Header*/
                                checkBox_header.setChecked(false);

                            }

                        }
                    });


            /* * Set CheckBox "TRUE" or "FALSE" if mChecked == true*/

            mCheckBox.setChecked((mChecked.get(position)));

            /* **************ADDING CONTENTS**************** */


            /*  * Return View here*/

            return mView;
        }


        /* * Find if all values are checked.*/

        boolean isAllValuesChecked() {

            for (int i = 0;i < count; i++) {
                if (!mChecked.get(i)) {
                    return false;
                }
            }

            return true;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//finish();
            onBackPressed();
        }
        return true;
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

}
