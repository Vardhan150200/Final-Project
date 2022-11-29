package com.example.nearblood;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.nearblood.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Uri uri;
    private MyAppPrefsManager myAppPrefsManager;
    private DatabaseReference myRef;
    private double receiverLat,receiverLon;
    private String param2;
    private Double currentUserLat,currentUserLon;
    private ApiInterface apiInterface;
    private List<LatLng> polylinesList;
    private PolylineOptions polylineOptions;
    public LatLng origion,dest;
    public GeoApiContext geoApiContext=null;
    private AccesVar accesVar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myAppPrefsManager=new MyAppPrefsManager(this);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myRef= FirebaseDatabase.getInstance().getReference("Users");
        myRef.keepSynced(true);
        geoApiContext=new GeoApiContext.Builder().apiKey(getString(R.string.api_key)).build();

        uri = getIntent().getData();
        if (uri != null) {

            // if the uri is not null then we are getting
            // the path segments and storing it in list.
            List<String> parameters = uri.getPathSegments();

            // after that we are extracting string
            // from that parameters.
            receiverLat = Double.parseDouble( parameters.get(0));
            receiverLon = Double.parseDouble(parameters.get(1));
            param2 = parameters.get(2);

            //Toast.makeText(this, ""+param2, Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
        Query query = myRef.orderByChild("email").equalTo(param2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Toast.makeText(MapsActivity.this, ""+snapshot.exists(), Toast.LENGTH_SHORT).show();
                if(snapshot.exists()){
                    //Toast.makeText(MapsActivity.this, "Exists", Toast.LENGTH_SHORT).show();
                    for(DataSnapshot issue : snapshot.getChildren()){
                        //Toast.makeText(MapsActivity.this, ""+issue.getKey(), Toast.LENGTH_SHORT).show();
                        Details details = issue.getValue(Details.class);
                        assert details != null;
                        //Toast.makeText(MapsActivity.this, ""+details.getBloodStatus(), Toast.LENGTH_SHORT).show();

                        if (!Objects.equals(details.getBloodStatus(), "1")){
                            myRef.child(Objects.requireNonNull(issue.getKey())).child("bloodStatus").setValue("1");
                        }
                        else{
                            Toast.makeText(MapsActivity.this, "Donar Already Assigned", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MapsActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()).baseUrl("https://maps.googleapis.com/").build();

        apiInterface = retrofit.create(ApiInterface.class);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        myRef= FirebaseDatabase.getInstance().getReference("Users");
        myRef.keepSynced(true);
        Query query  = myRef.orderByChild("email").equalTo(myAppPrefsManager.getUserName());
        Toast.makeText(this, ""+myAppPrefsManager.getUserName(), Toast.LENGTH_SHORT).show();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(MapsActivity.this, "Exists", Toast.LENGTH_SHORT).show();

                    for(DataSnapshot issue : snapshot.getChildren()){
                        Details details = issue.getValue(Details.class);
                        assert details != null;

                        currentUserLat =details.getLatitude();
                        currentUserLon =details.getLongitude();
                        origion = new LatLng(currentUserLat,currentUserLon);
                        dest = new LatLng(receiverLat,receiverLon);
                        accesVar = new AccesVar(currentUserLat,currentUserLon,origion,dest);
                        //getDirections(currentUserLat+","+currentUserLon,receiverLat+","+receiverLon,origion,dest);
                        calculateDirections(receiverLat,receiverLon,currentUserLat,currentUserLon);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
    private void getDirections(String origin,String destination, LatLng origion,LatLng dest){
        polylinesList = new ArrayList<>();
        apiInterface.getDirections("driving","less_driving",origin,destination,getString(R.string.api_key)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Results>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Results results) {

                        List<Route> routeList = results.getRoutes();
                        Log.i("msg","===========================================================================================");
                        for(Route route:routeList){
                            Log.i("Route",""+route);
                            Log.i("points", ""+route.getOverviewPolyline().getPoints());
                            String polyline = route.getOverviewPolyline().getPoints();

                            polylinesList.addAll(decodePoly(polyline));
                        }


                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(ContextCompat.getColor(getApplicationContext(),R.color.red));
                        polylineOptions.width(40);
                        polylineOptions.startCap(new ButtCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polylinesList);
                        mMap.addPolyline(polylineOptions);
                        LatLngBounds.Builder builder= new LatLngBounds.Builder();
                        builder.include(origion);
                        builder.include(dest);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),100));

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private List<LatLng> decodePoly(String encoded) {

        Log.i("Location", "String received: "+encoded);
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((int) (((double) lat /1E5)* 1E6), (int) (((double) lng/1E5   * 1E6)));
            poly.add(p);
        }

        return poly;
    }

    private void calculateDirections(double receiverLat,double receiverLon,double currentUserLat,double currentUserLon){


        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                receiverLat,receiverLon

        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                    currentUserLat,currentUserLon
                )
        );

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {


            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {


                for(DirectionsRoute route: result.routes){

                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(MapsActivity.this, R.color.red));
                    polyline.setClickable(true);

                }
            }
        });
    }
}