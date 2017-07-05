package gomez.alejandro.teconfortasdatos;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Modulos.DataParser;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    ListView list;


    public static Polyline ruta,line;
    public static boolean bandera=false,bandera2=false;
    public static int band=0;
    String[] itemname ={ "Hoteles", "Hoteles cerca","Restaurantes","Restaurantes cerca"  };
    Integer[] imgid= {
            R.drawable.markhotel,
            R.drawable.hotel_cerca,
            R.drawable.restaurantmarker,
            R.drawable.rest_cerca,

    };

    String la="";
    String lo="";
    String server_url="http://teconfortascolima.esy.es/webservice/servicios/sitioscercas.php";
    String server_url2="http://teconfortascolima.esy.es/webservice/servicios/todoslossitios.php";
    String server_url3="http://teconfortascolima.esy.es/webservice/servicios/informacionmarker.php";
    String server_url4="http://teconfortascolima.esy.es/webservice/servicios/todoslosrestaurantes.php";
    String server_url5="http://teconfortascolima.esy.es/webservice/servicios/restaurantescercas.php";
    private RequestQueue requestQueue;
    private ImageLoader mImageloader;



    ListView listView;
    DrawerLayout drawerLayout;
    Button btn;
    ImageButton btnopen;
    private Marker MI;


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        CustomListAdapter adapter=new CustomListAdapter(this, itemname, imgid);
        list=(ListView)findViewById(R.id.list_view);
        list.setAdapter(adapter);
        btn=(Button)findViewById(R.id.btncal);
        btnopen=(ImageButton)findViewById(R.id.btnabrir);
        listView = (ListView) findViewById(R.id.list_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(band==1){
                    mMap.clear();
                    contorno();
                    sitioscercas();
                    radio();
                }else{
                    if(band==2){
                        mMap.clear();
                        contorno();

                        restaurantescercas();

                        radio();
                    }
                }


            }
        });

        btnopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });


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
        contorno();
        ubicacion ubi =new ubicacion(this);
        if(mMap!=null) {
            //Activamos la capa o layer MyLocation
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                LatLng actual = new LatLng(ubi.lat, ubi.lo);
               // Toast.makeText(getApplicationContext(),String.valueOf(actual),Toast.LENGTH_LONG).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actual,14));
            } else {
                // Show rationale and request permission.
            }



        }
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String n=marker.getTitle();

                // Toast.makeText(getApplicationContext(),n,Toast.LENGTH_LONG).show();
                 obtiene(n);



            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
               // Toast.makeText(getApplicationContext(),"tocaste",Toast.LENGTH_LONG).show();
                marker.showInfoWindow();
                  latlon(marker.getTitle());

                return true;

            }
        });
        drawerLayout.openDrawer(Gravity.LEFT);


        // final  String[] opciones = { "Hoteles cercas de mi", "Todos los hoteles", "Restaurantes cercas de mi", "Todos los restaurantes","Busqueda Personalizada","Calificar Sitio" };
        // listView.setAdapter(new ArrayAdapter(this,
        //   R.layout.list_white_color, R.id.list_content,
        //    opciones));




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
               // Toast.makeText(MapsActivity.this, "Item: " +itemname[arg2], Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                if(arg2==1){
                    band=1;
                    mMap.clear();
                    contorno();
                    sitioscercas();
                    radio();

                }
                else{
                    if(arg2==0){
                        mMap.clear();
                        contorno();

                        todoslossitios();

                    }else{

                            if(arg2==2){
                                mMap.clear();
                                contorno();

                                todoslosrestaurantes();

                            }else{
                                if(arg2==3){
                                    band=2;
                                    mMap.clear();
                                    contorno();
                                    restaurantescercas();




                                    radio();

                                }
                            }


                    }

                }
            }
        });


    }


    public void sitioscercas(){


        final ubicacion ubi =new ubicacion(this);
        final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String name=sitios.getString("nombre");
                                String lat=sitios.getString("latitud");
                                String lon=sitios.getString("longitud");
                                String distancia=sitios.getString("distancia");
                                Double lati=Double.valueOf(lat);
                                Double longi=Double.valueOf(lon);
                                //  Toast.makeText(getApplicationContext(),"nombre:"+name+" lat:"+lat+" long:"+lon+ "\n",Toast.LENGTH_LONG).show();
                                LatLng pos = new LatLng(lati, longi);
                                mMap.addMarker(new MarkerOptions().position(pos).title(name).snippet("Distancia: "+distancia+" Km").icon(BitmapDescriptorFactory.fromResource(R.drawable.markhotel)));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                String la=String.valueOf(ubi.lat);
                String lo=String.valueOf(ubi.lo);
                params.put("latitud",la);
                params.put("longitud",lo);
//




                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void todoslossitios(){
//prueba

        final ubicacion ubi =new ubicacion(this);
        final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url2,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String name=sitios.getString("nombre");
                                String lat=sitios.getString("latitud");
                                String lon=sitios.getString("longitud");
                                String distancia=sitios.getString("distancia");
                                Double lati=Double.valueOf(lat);
                                Double longi=Double.valueOf(lon);
                                //  Toast.makeText(getApplicationContext(),"nombre:"+name+" lat:"+lat+" long:"+lon+ "\n",Toast.LENGTH_LONG).show();
                                LatLng pos = new LatLng(lati, longi);
                                mMap.addMarker(new MarkerOptions().position(pos).title(name).snippet("Distancia: "+distancia+" Km").icon(BitmapDescriptorFactory.fromResource(R.drawable.markhotel)));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                String la=String.valueOf(ubi.lat);
                String lo=String.valueOf(ubi.lo);
                params.put("latitud",la);
                params.put("longitud",lo);





                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    public void obtiene(final String nombre){
//prueba



        final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url3,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String name=sitios.getString("nombre");
                                String imagen=sitios.getString("imagen");
                                String descripcion=sitios.getString("descripcion");
                                String lat=sitios.getString("latitud");
                                String lon=sitios.getString("longitud");
                                String direccion=sitios.getString("direccion");
                                String horario=sitios.getString("horario");
                                String contacto=sitios.getString("contacto");
                                String id=sitios.getString("id");


                               // Toast.makeText(getApplicationContext(),imagen,Toast.LENGTH_LONG).show();
                                Intent nextScreen = new Intent(MapsActivity.this,InformacionActivity.class);
                                Bundle bundle=new Bundle();
                                bundle.putString("imagen",imagen);
                                bundle.putString("nombre",name);
                                bundle.putString("descripcion",descripcion);
                                bundle.putString("latitud",lat);
                                bundle.putString("longitud",lon);
                                bundle.putString("direccion",direccion);
                                bundle.putString("horario",horario);
                                bundle.putString("contacto",contacto);
                                bundle.putString("id",id);
                                nextScreen.putExtras(bundle);
                                startActivityForResult(nextScreen, 0);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("nombre",nombre);





                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }
    public void  todoslosrestaurantes(){

        final ubicacion ubi =new ubicacion(this);
        final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url4,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String name=sitios.getString("nombre");
                                String lat=sitios.getString("latitud");
                                String lon=sitios.getString("longitud");
                                String distancia=sitios.getString("distancia");
                                Double lati=Double.valueOf(lat);
                                Double longi=Double.valueOf(lon);
                                //  Toast.makeText(getApplicationContext(),"nombre:"+name+" lat:"+lat+" long:"+lon+ "\n",Toast.LENGTH_LONG).show();
                                LatLng pos = new LatLng(lati, longi);
                                mMap.addMarker(new MarkerOptions().position(pos).title(name).snippet("Distancia: "+distancia+" km").icon(BitmapDescriptorFactory.fromResource(R.drawable.restmarker)));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                String la=String.valueOf(ubi.lat);
                String lo=String.valueOf(ubi.lo);
                params.put("latitud",la);
                params.put("longitud",lo);





                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    public void restaurantescercas(){
        final ubicacion ubi =new ubicacion(this);
        final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url5,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String name=sitios.getString("nombre");
                                String lat=sitios.getString("latitud");
                                String lon=sitios.getString("longitud");
                                String distancia=sitios.getString("distancia");
                                Double lati=Double.valueOf(lat);
                                Double longi=Double.valueOf(lon);
                                //  Toast.makeText(getApplicationContext(),"nombre:"+name+" lat:"+lat+" long:"+lon+ "\n",Toast.LENGTH_LONG).show();
                                LatLng pos = new LatLng(lati, longi);
                                mMap.addMarker(new MarkerOptions().position(pos).title(name).snippet("Distancia: "+distancia+" km").icon(BitmapDescriptorFactory.fromResource(R.drawable.restmarker)));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                String la=String.valueOf(ubi.lat);
                String lo=String.valueOf(ubi.lo);
                params.put("latitud",la);
                params.put("longitud",lo);
//




                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }




    public void latlon(final String nombre){
//prueba

        //final  ubicacion ubi=new ubicacion(this);

        final RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url3,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String name=sitios.getString("nombre");
                                String lat=sitios.getString("latitud");
                                String lon=sitios.getString("longitud");
                                if(bandera==true){
                                    ruta.setVisible(false);
                                }
                                ubicacion ubi=new ubicacion(MapsActivity.this);
                                LatLng destino=new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                                LatLng origen=new LatLng(ubi.lat,ubi.lo);

                                // Toast.makeText(getApplicationContext(),"laa: "+latituddestino+" loo: "+longituddestino,Toast.LENGTH_LONG).show();
                                String url = getUrl(origen, destino);
                                Log.d("onMapClick", url.toString());
                                FetchUrl FetchUrl = new FetchUrl();
                                FetchUrl.execute(url);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("nombre",nombre);





                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"&key=AIzaSyCfYUMjL9bQOc58LIUXD2nmGVyOXdjDJvU";
        return url;
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {

                ruta=mMap.addPolyline(lineOptions);
                bandera=true;

            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
    public void radio(){
        ubicacion ubi=new ubicacion(this);

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(ubi.lat, ubi.lo))
                .fillColor(Color.argb(100,195,192,192))
                .strokeColor(0x10000000)
                .strokeWidth(5)
                .radius(1000);

// Get back the mutable Circle
        Circle circle = mMap.addCircle(circleOptions);
    }

    public void contorno(){
        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(18.87300800190553,-103.57229198043252),
                        new LatLng(18.871789753785148,-103.57254947250148),
                        new LatLng(18.871627320033994,-103.57332194869939),
                        new LatLng(18.871627320033994,-103.57409442488829),
                        new LatLng(18.871627320033994,-103.57478107040252),
                        new LatLng(18.87195218737975,-103.57512439315515),
                        new LatLng(18.87252070371992,-103.5758110386604),
                        new LatLng(18.873332866576057,-103.57684100692727),
                        new LatLng(18.87430745680778,-103.57752765243252),
                        new LatLng(18.874957180479804,-103.5776134831162),
                        new LatLng(18.875525686627583,-103.57855762069039),
                        new LatLng(18.875606901633756,-103.57933009688828),
                        new LatLng(18.876175405577385,-103.58018840376985),
                        new LatLng(18.876175405577385,-103.58087504928409),
                        new LatLng(18.87674390759151,-103.58018840376985),
                        new LatLng(18.877880905834413,-103.57907260481933),
                        new LatLng(18.878855469615946,-103.5787292820667),
                        new LatLng(18.881454278653163,-103.578900943443),
                        new LatLng(18.882510033308687,-103.5795017582646),
                        new LatLng(18.88405304737471,-103.58036006514617),
                        new LatLng(18.88470273325579,-103.5804458958388),
                        new LatLng(18.886245727123242,-103.58061755721513),
                        new LatLng(18.888194751688328,-103.58199084823461),
                        new LatLng(18.889494088796027,-103.5831924778688),
                        new LatLng(18.890955830990933,-103.58439410751197),
                        new LatLng(18.891605490092772,-103.58499492232457),
                        new LatLng(18.891930318697973,-103.58645404402772),
                        new LatLng(18.893310833240548,-103.58705485884931),
                        new LatLng(18.89485374778726,-103.58739818160193),
                        new LatLng(18.896234238220888,-103.58765567367091),
                        new LatLng(18.897533512935443,-103.58791316573087),
                        new LatLng(18.898995184932733,-103.58817065779982),
                        new LatLng(18.90029443821298,-103.5890289646814),
                        new LatLng(18.901593681406816,-103.58997310225561),
                        new LatLng(18.903623728700428,-103.59100307052246),
                        new LatLng(18.90419213752953,-103.59606708113718),
                        new LatLng(18.9047605444267,-103.59529460493927),
                        new LatLng(18.907115352428903,-103.58945811812667),
                        new LatLng(18.90857694072313,-103.58885730330508),
                        new LatLng(18.9092265314214,-103.5884281498688),
                        new LatLng(18.910038516245898,-103.58782733504721),
                        new LatLng(18.911175288378153,-103.58731235091827),
                        new LatLng(18.91263684119408,-103.58636821334406),
                        new LatLng(18.916371862577225,-103.58653987472037),
                        new LatLng(18.917346202259672,-103.5851665837009),
                        new LatLng(18.917427396976606,-103.58387912337405),
                        new LatLng(18.91726500750244,-103.5801025730862),
                        new LatLng(18.917833369971483,-103.5801025730862),
                        new LatLng(18.920918732511524,-103.58130420272039),
                        new LatLng(18.92238020015312,-103.58130420272039),
                        new LatLng(18.927576426042375,-103.58087504928409),
                        new LatLng(18.928388321742016,-103.57967341964091),
                        new LatLng(18.92920021349548,-103.58482326094826),
                        new LatLng(18.92708928672924,-103.58799899642351),
                        new LatLng(18.934396227285575,-103.58954394881931),
                        new LatLng(18.935776391279454,-103.5890289646814),
                        new LatLng(18.938455500589093,-103.58817065779982),
                        new LatLng(18.940160365951876,-103.58765567367091),
                        new LatLng(18.941702848184512,-103.58765567367091),
                        new LatLng(18.943164133997133,-103.58765567367091),
                        new LatLng(18.944706588476734,-103.58817065779982),
                        new LatLng(18.94689847295589,-103.58997310225561),
                        new LatLng(18.947710274718066,-103.58971561019563),
                        new LatLng(18.949820940817226,-103.58825648849249),
                        new LatLng(18.95055154978588,-103.58825648849249),
                        new LatLng(18.95201275812729,-103.58937228743402),
                        new LatLng(18.960211523113955,-103.58782733504721),
                        new LatLng(18.9644325122839,-103.58739818160193),
                        new LatLng(18.96946509048636,-103.58645404402772),
                        new LatLng(18.973117673058027,-103.5861107212751),
                        new LatLng(18.975633850038225,-103.5856815678388),
                        new LatLng(18.97733833543819,-103.5856815678388),
                        new LatLng(18.97855581433393,-103.58765567367091),
                        new LatLng(18.97944862653661,-103.58962977950297),
                        new LatLng(18.980909581637775,-103.592118869473),
                        new LatLng(18.98123423658748,-103.5913463932751),
                        new LatLng(18.981883544586957,-103.58997310225561),
                        new LatLng(18.983750290988294,-103.58945811812667),
                        new LatLng(18.98545469334277,-103.58911479537404),
                        new LatLng(18.987321399716826,-103.58877147262142),
                        new LatLng(18.988863445721073,-103.5890289646814),
                        new LatLng(18.990567795741402,-103.58962977950297),
                        new LatLng(18.994544544602018,-103.59040225570088),
                        new LatLng(18.9953561143301,-103.59100307052246),
                        new LatLng(18.998521198455357,-103.59246219222563),
                        new LatLng(19.00038775831145,-103.59306300703821),
                        new LatLng(19.002091990328395,-103.59349216048348),
                        new LatLng(19.00387735752759,-103.59263385360194),
                        new LatLng(19.00468888173958,-103.59237636153297),
                        new LatLng(19.006555372402183,-103.59649623458245),
                        new LatLng(19.00842184212006,-103.59975780074137),
                        new LatLng(19.010369440365185,-103.6031910282766),
                        new LatLng(19.01345309093724,-103.60465014998873),
                        new LatLng(19.015806364742062,-103.60619510237555),
                        new LatLng(19.017753876493337,-103.60774005477136),
                        new LatLng(19.01913335017969,-103.60859836165291),
                        new LatLng(19.021811119358937,-103.60902751509819),
                        new LatLng(19.023190559371624,-103.60816920820764),
                        new LatLng(19.027734516070932,-103.61108745162292),
                        new LatLng(19.028708204910128,-103.61194575850446),
                        new LatLng(19.029925307933883,-103.61469234053446),
                        new LatLng(19.03041214664656,-103.61615146223762),
                        new LatLng(19.029681888042518,-103.61795390669339),
                        new LatLng(19.02935732763277,-103.61915553633656),
                        new LatLng(19.028545923833796,-103.62052882734707),
                        new LatLng(19.028545923833796,-103.62233127181183),
                        new LatLng(19.028789345389697,-103.62430537764392),
                        new LatLng(19.029113906909117,-103.62602199140704),
                        new LatLng(19.029681888042518,-103.62748111311917),
                        new LatLng(19.029438467794435,-103.62885440412967),
                        new LatLng(19.029925307933883,-103.6301418644565),
                        new LatLng(19.02935732763277,-103.63177264753595),
                        new LatLng(19.029438467794435,-103.6326309544265),
                        new LatLng(19.028708204910128,-103.63297427717914),
                        new LatLng(19.028708204910128,-103.63460506025861),
                        new LatLng(19.028140220447728,-103.63692248884331),
                        new LatLng(19.026598538545812,-103.6390682560607),
                        new LatLng(19.02416427483933,-103.64353145185382),
                        new LatLng(19.022297981847228,-103.64653552595277),
                        new LatLng(19.02099967870927,-103.64679301802171),
                        new LatLng(19.01872762381306,-103.64696467939802),
                        new LatLng(19.017835022321744,-103.64713634077434),
                        new LatLng(19.01726700069445,-103.64747966352697),
                        new LatLng(19.01686126977333,-103.64808047834855),
                        new LatLng(19.016698977127092,-103.64833797040853),
                        new LatLng(19.018078459567604,-103.6487671238538),
                        new LatLng(19.018646478421225,-103.64893878523013),
                        new LatLng(19.018646478421225,-103.64962543073538),
                        new LatLng(19.01945793056028,-103.64988292280434),
                        new LatLng(19.020837390103594,-103.65039790693328),
                        new LatLng(19.021729975472514,-103.65039790693328),
                        new LatLng(19.022135694510176,-103.64962543073538),
                        new LatLng(19.023190559371624,-103.64911044660643),
                        new LatLng(19.02448884539455,-103.64911044660643),
                        new LatLng(19.02570597932813,-103.64902461592277),
                        new LatLng(19.026760821525162,-103.64893878523013),
                        new LatLng(19.027572234042633,-103.64859546247749),
                        new LatLng(19.028221361203865,-103.64825213972487),
                        new LatLng(19.02984416801001,-103.64859546247749),
                        new LatLng(19.030655565467836,-103.64868129316116),
                        new LatLng(19.03227834849396,-103.64825213972487),
                        new LatLng(19.032765180310076,-103.64825213972487),
                        new LatLng(19.03333314895928,-103.6479946476559),
                        new LatLng(19.033657701600433,-103.6479946476559),
                        new LatLng(19.034144529373428,-103.64773715559593),
                        new LatLng(19.034712493304674,-103.64730800215067),
                        new LatLng(19.03528045529434,-103.64730800215067),
                        new LatLng(19.03584841534069,-103.64730800215067),
                        new LatLng(19.03665978346589,-103.64713634077434),
                        new LatLng(19.037957964221103,-103.64713634077434),
                        new LatLng(19.038769322038405,-103.6470505100817),
                        new LatLng(19.03990521632234,-103.64670718732907),
                        new LatLng(19.041122237287986,-103.64670718732907),
                        new LatLng(19.0414467747051,-103.64584888044752),
                        new LatLng(19.04185244558493,-103.64533389631858),
                        new LatLng(19.04266378437054,-103.64524806562594),
                        new LatLng(19.04315058573811,-103.64516223494226),
                        new LatLng(19.043475119189758,-103.64464725080434),
                        new LatLng(19.04420531713667,-103.64456142012068),
                        new LatLng(19.044854379281915,-103.64456142012068),
                        new LatLng(19.046233627912574,-103.6441322666754),
                        new LatLng(19.04745060247945,-103.64370311323013),
                        new LatLng(19.04834304482269,-103.64327395979383),
                        new LatLng(19.048910960179512,-103.64387477461543),
                        new LatLng(19.049965654969895,-103.6441322666754),
                        new LatLng(19.05118260216726,-103.64353145185382),
                        new LatLng(19.051669378546993,-103.64318812910119),
                        new LatLng(19.052805184545967,-103.64447558942803),
                        new LatLng(19.053940982769,-103.64481891218065),
                        new LatLng(19.055806920110403,-103.64516223494226),
                        new LatLng(19.056212555884823,-103.64541972700223),
                        new LatLng(19.056212555884823,-103.64644969526908),
                        new LatLng(19.056374809917333,-103.64747966352697),
                        new LatLng(19.057186077697455,-103.64945376935906),
                        new LatLng(19.05742945725781,-103.65125621381483),
                        new LatLng(19.057591710099913,-103.65305865827959),
                        new LatLng(19.057672836461027,-103.65460361066641),
                        new LatLng(19.058078467673027,-103.6569210392601),
                        new LatLng(19.05832184592387,-103.65760768476535),
                        new LatLng(19.058889727119983,-103.6581226688943),
                        new LatLng(19.06002548367948,-103.65846599164692),
                        new LatLng(19.06286484104621,-103.65863765302322),
                        new LatLng(19.06359495365329,-103.66112674299322),
                        new LatLng(19.06456843212818,-103.66335834089428),
                        new LatLng(19.06554190488713,-103.66627658430058),
                        new LatLng(19.067083225065566,-103.66962398115217),
                        new LatLng(19.068218925499455,-103.67262805524213),
                        new LatLng(19.0698413411936,-103.67734874311317),
                        new LatLng(19.070571423074625,-103.6792370182616),
                        new LatLng(19.071950457853173,-103.68138278546999),
                        new LatLng(19.072680530443048,-103.68352855267841),
                        new LatLng(19.074870728914018,-103.68258441510419),
                        new LatLng(19.075195200300055,-103.68318522992577),
                        new LatLng(19.076087493333198,-103.68541682782681),
                        new LatLng(19.07957550181,-103.69485820355993),
                        new LatLng(19.080711116629054,-103.69751895489729),
                        new LatLng(19.082090067012967,-103.70129550519414),
                        new LatLng(19.082657866776167,-103.70301211895726),
                        new LatLng(19.083793460461948,-103.70575870098725),
                        new LatLng(19.08460459404115,-103.7087627750862),
                        new LatLng(19.085659061754622,-103.7112518650562),
                        new LatLng(19.08655129842204,-103.71331180157196),
                        new LatLng(19.088092423160074,-103.71657336773987),
                        new LatLng(19.08874131349444,-103.71837581219565),
                        new LatLng(19.08874131349444,-103.7194057804535),
                        new LatLng(19.087849088628854,-103.7194057804535),
                        new LatLng(19.08638907393078,-103.71906245770089),
                        new LatLng(19.085577949092222,-103.71906245770089),
                        new LatLng(19.084685707180313,-103.7200065952751),
                        new LatLng(19.083712346884937,-103.7200065952751),
                        new LatLng(19.082090067012967,-103.72017825665141),
                        new LatLng(19.080711116629054,-103.72017825665141),
                        new LatLng(19.079818848498,-103.72069324078035),
                        new LatLng(19.079494386168186,-103.7217232090472),
                        new LatLng(19.078196530492043,-103.72258151592878),
                        new LatLng(19.076979781561747,-103.72258151592878),
                        new LatLng(19.0758441411642,-103.72292483868141),
                        new LatLng(19.074870728914018,-103.72395480694824),
                        new LatLng(19.07397842932833,-103.72455562176086),
                        new LatLng(19.07341059981654,-103.72584308208769),
                        new LatLng(19.073329481156392,-103.72687305035456),
                        new LatLng(19.072112696484712,-103.72721637310718),
                        new LatLng(19.070814782986965,-103.72747386517615),
                        new LatLng(19.06959797985192,-103.72695888103821),
                        new LatLng(19.068218925499455,-103.72635806622561),
                        new LatLng(19.066920981510886,-103.72592891278035),
                        new LatLng(19.065866394537096,-103.7254139286514),
                        new LatLng(19.065055169222163,-103.72524226727509),
                        new LatLng(19.064892923683512,-103.7256714207114),
                        new LatLng(19.066353127820314,-103.73039210858244),
                        new LatLng(19.067164346782928,-103.73288119854345),
                        new LatLng(19.068056683057215,-103.7357136112661),
                        new LatLng(19.06919237682232,-103.73828853191976),
                        new LatLng(19.0698413411936,-103.74052012982082),
                        new LatLng(19.065866394537096,-103.74223674358393),
                        new LatLng(19.064243939938596,-103.75030482830658),
                        new LatLng(19.063108212272457,-103.75021899761393),
                        new LatLng(19.062215849362843,-103.75021899761393),
                        new LatLng(19.06205360104574,-103.75124896587181),
                        new LatLng(19.061161232460503,-103.75124896587181),
                        new LatLng(19.061323481651623,-103.75236476482232),
                        new LatLng(19.06302708857034,-103.75287974896024),
                        new LatLng(19.0644061861132,-103.75227893413866),
                        new LatLng(19.065460782375826,-103.75193561138605),
                        new LatLng(19.06683985967442,-103.752193103446),
                        new LatLng(19.068462288865923,-103.75210727276236),
                        new LatLng(19.07008470217795,-103.75245059551496),
                        new LatLng(19.07122038204703,-103.75245059551496),
                        new LatLng(19.07186933847825,-103.75330890239654),
                        new LatLng(19.07186933847825,-103.75614131511917),
                        new LatLng(19.07130150173959,-103.7589737278418),
                        new LatLng(19.071139262314755,-103.76129115642652),
                        new LatLng(19.070977022730272,-103.76386607708018),
                        new LatLng(19.071058142541947,-103.76661265911018),
                        new LatLng(19.07073366305617,-103.76738513530807),
                        new LatLng(19.069516859325574,-103.76764262736805),
                        new LatLng(19.068462288865923,-103.76790011943702),
                        new LatLng(19.068056683057215,-103.76867259563491),
                        new LatLng(19.06894901452778,-103.7700458866454),
                        new LatLng(19.070246942635944,-103.77287829936805),
                        new LatLng(19.071058142541947,-103.77450908244751),
                        new LatLng(19.071788219062796,-103.77605403484331),
                        new LatLng(19.078640618355795,-103.7732748614048),
                        new LatLng(19.07831615371821,-103.78469034297001),
                        new LatLng(19.08618424201592,-103.78314539058321),
                        new LatLng(19.088860929104364,-103.78280206783059),
                        new LatLng(19.09997277331584,-103.79232927424738),
                        new LatLng(19.105731172762766,-103.79722162348577),
                        new LatLng(19.1071910168278,-103.79885240657421),
                        new LatLng(19.11749066150931,-103.81172700984257),
                        new LatLng(19.12186984356421,-103.81722017390256),
                        new LatLng(19.13379036229256,-103.82442995173463),
                        new LatLng(19.13597975180969,-103.82588907343778),
                        new LatLng(19.137277154122533,-103.82503076654723),
                        new LatLng(19.145385687582973,-103.8089804278126),
                        new LatLng(19.145791103799517,-103.8082079516147),
                        new LatLng(19.14627760194541,-103.80803629023839),
                        new LatLng(19.14643976767562,-103.80674882991154),
                        new LatLng(19.151466826258847,-103.80031152827736),
                        new LatLng(19.149926292324224,-103.79868074518893),
                        new LatLng(19.149683048809084,-103.79799409968368),
                        new LatLng(19.149034397681966,-103.79722162348577),
                        new LatLng(19.14862898943252,-103.79696413142578),
                        new LatLng(19.14976413002035,-103.79619165522789),
                        new LatLng(19.16419595078633,-103.78863855464319),
                        new LatLng(19.160385424188416,-103.78108545405848),
                        new LatLng(19.168168533679008,-103.78125711543478),
                        new LatLng(19.169060324843322,-103.78125711543478),
                        new LatLng(19.17603234396128,-103.7829737292069),
                        new LatLng(19.18113956609435,-103.78271623713795),
                        new LatLng(19.190380803575835,-103.78202959163269),
                        new LatLng(19.19483910984832,-103.78031297786058),
                        new LatLng(19.200469507632786,-103.77990261477254),
                        new LatLng(19.204684378260225,-103.77955929201993),
                        new LatLng(19.206301817426045,-103.77921753512965),
                        new LatLng(19.2072744549772,-103.78127747165438),
                        new LatLng(19.20597760363106,-103.78196411715963),
                        new LatLng(19.20597760363106,-103.7828224240412),
                        new LatLng(19.207598666215734,-103.78333740817912),
                        new LatLng(19.209219712822865,-103.7829940854265),
                        new LatLng(19.21019233311688,-103.7829940854265),
                        new LatLng(19.211002845634574,-103.78127747165438),
                        new LatLng(19.212785959108714,-103.78024750338751),
                        new LatLng(19.213920657615027,-103.78059082614914),
                        new LatLng(19.215379544186543,-103.77921753512965),
                        new LatLng(19.214569053245437,-103.7771575986049),
                        new LatLng(19.215865836833323,-103.77612763034703),
                        new LatLng(19.21764889756318,-103.77492600070387),
                        new LatLng(19.219269845078912,-103.77475433932756),
                        new LatLng(19.221701236376873,-103.7740676938223),
                        new LatLng(19.222025419165906,-103.77578430759442),
                        new LatLng(19.2247809470452,-103.77595596897073),
                        new LatLng(19.226563911048768,-103.77595596897073),
                        new LatLng(19.22850894061979,-103.7754409848418),
                        new LatLng(19.229481446769167,-103.77441101657493),
                        new LatLng(19.230453947160772,-103.77887421237703),
                        new LatLng(19.231588523673768,-103.7799041806349),
                        new LatLng(19.233371413786596,-103.78230743991226),
                        new LatLng(19.233857653185524,-103.7829940854265),
                        new LatLng(19.235640518664322,-103.77938919650596),
                        new LatLng(19.236612982584433,-103.77492600070387),
                        new LatLng(19.237585440744976,-103.7725227414265),
                        new LatLng(19.238395818145303,-103.76977615939653),
                        new LatLng(19.238557893145714,-103.76788788425708),
                        new LatLng(19.239368265746013,-103.76548462497972),
                        new LatLng(19.24066485358709,-103.76239472018813),
                        new LatLng(19.241637287746812,-103.76033478367238),
                        new LatLng(19.242447645145678,-103.75861816990026),
                        new LatLng(19.2426097161457,-103.75707321750446),
                        new LatLng(19.241961431186265,-103.75466995822711),
                        new LatLng(19.241961431186265,-103.75278168308768),
                        new LatLng(19.24342006874368,-103.75003510105768),
                        new LatLng(19.244392486580413,-103.74728851901871),
                        new LatLng(19.244388851257494,-103.74385685734583),
                        new LatLng(19.244388851257494,-103.74282688907896),
                        new LatLng(19.24503712662912,-103.74248356632634),
                        new LatLng(19.24592850108376,-103.74256939701898),
                        new LatLng(19.24730607026215,-103.74153942875215),
                        new LatLng(19.247954334109828,-103.7408527832469),
                        new LatLng(19.249007757400058,-103.74008030704898),
                        new LatLng(19.250304269087298,-103.73956532292004),
                        new LatLng(19.25119561492968,-103.73930783085109),
                        new LatLng(19.251924894289356,-103.73930783085109),
                        new LatLng(19.252735200886466,-103.7390503387911),
                        new LatLng(19.253059322405292,-103.73870701603848),
                        new LatLng(19.25354550348189,-103.73836369328585),
                        new LatLng(19.254436831712574,-103.73802037052424),
                        new LatLng(19.254841979307766,-103.73767704777163),
                        new LatLng(19.25605741608808,-103.73741955571164),
                        new LatLng(19.25646255968025,-103.73707623295903),
                        new LatLng(19.254841979307766,-103.73269886784058),
                        new LatLng(19.256138444886236,-103.73226971440428),
                        new LatLng(19.256543588278262,-103.73192639165168),
                        new LatLng(19.257110787345074,-103.73029560856322),
                        new LatLng(19.257272843861383,-103.73003811650325),
                        new LatLng(19.257921068324393,-103.73038143925588),
                        new LatLng(19.258650317782337,-103.73046726993954),
                        new LatLng(19.25986572633911,-103.73046726993954),
                        new LatLng(19.260919073134506,-103.73012394718693),
                        new LatLng(19.260108806969463,-103.72832150273113),
                        new LatLng(19.25978469938252,-103.72686238102798),
                        new LatLng(19.259460591154152,-103.7256607513938),
                        new LatLng(19.25873134529943,-103.724115798998),
                        new LatLng(19.258326207312773,-103.72351498417642),
                        new LatLng(19.260675993705313,-103.72265667729486),
                        new LatLng(19.2616483092599,-103.72231335454224),
                        new LatLng(19.263673948146664,-103.721626709028),
                        new LatLng(19.26553751381775,-103.72128338627537),
                        new LatLng(19.26634775315352,-103.72128338627537),
                        new LatLng(19.26788719685892,-103.72128338627537),
                        new LatLng(19.268535379355882,-103.7210258942154),
                        new LatLng(19.268940492115423,-103.72033924870117),
                        new LatLng(19.27015582438503,-103.71999592594854),
                        new LatLng(19.270803997910665,-103.71956677251224),
                        new LatLng(19.271614211211926,-103.71905178837432),
                        new LatLng(19.272424420508198,-103.7187084656217),
                        new LatLng(19.273477686603368,-103.7177643280475),
                        new LatLng(19.274125846989534,-103.71690602116594),
                        new LatLng(19.274125846989534,-103.71630520634434),
                        new LatLng(19.274125846989534,-103.71570439153173),
                        new LatLng(19.274530945928117,-103.71570439153173),
                        new LatLng(19.275017063333,-103.71587605290804),
                        new LatLng(19.275584198482964,-103.71587605290804),
                        new LatLng(19.277933737473322,-103.71939511113594),
                        new LatLng(19.280850359682706,-103.7175068359875),
                        new LatLng(19.27963510674017,-103.71493191533384),
                        new LatLng(19.2803642595877,-103.71467442326488),
                        new LatLng(19.28141747463658,-103.71407360844329),
                        new LatLng(19.281660523301504,-103.7144169312049),
                        new LatLng(19.28198458762757,-103.71373028569066),
                        new LatLng(19.283442869157042,-103.71295780950175),
                        new LatLng(19.284901137700853,-103.7119278412349),
                        new LatLng(19.28595432357246,-103.71141285710596),
                        new LatLng(19.286278379401562,-103.71184201055122),
                        new LatLng(19.28684547555795,-103.71201367192754),
                        new LatLng(19.287574596301827,-103.71201367192754),
                        new LatLng(19.287817635828226,-103.71167034917492),
                        new LatLng(19.288141687968608,-103.71124119572964),
                        new LatLng(19.28911384054301,-103.7107262116007),
                        new LatLng(19.290491046806267,-103.71021122746278),
                        new LatLng(19.291382174097343,-103.70986790471015),
                        new LatLng(19.292759361277504,-103.70909542852122),
                        new LatLng(19.294298556765582,-103.70858044438332),
                        new LatLng(19.29486562513929,-103.70806546025439),
                        new LatLng(19.295675719406475,-103.70652050785858),
                        new LatLng(19.29648580966359,-103.70514721684808),
                        new LatLng(19.29705287045828,-103.70454640202648),
                        new LatLng(19.2981869861516,-103.70300144963969),
                        new LatLng(19.29932109398349,-103.70162815862021),
                        new LatLng(19.299807137791,-103.70111317449125),
                        new LatLng(19.300617207595444,-103.70068402104597),
                        new LatLng(19.301589286065646,-103.7000832062244),
                        new LatLng(19.30256135875987,-103.69939656071915),
                        new LatLng(19.30361443099335,-103.69862408452126),
                        new LatLng(19.30499151522389,-103.6977657776397),
                        new LatLng(19.306206579916648,-103.69690747075812),
                        new LatLng(19.30701661803087,-103.6962208252439),
                        new LatLng(19.308474676524988,-103.69476170354073),
                        new LatLng(19.30928470340426,-103.6939033966592),
                        new LatLng(19.310337732348763,-103.69270176702499),
                        new LatLng(19.31122875154412,-103.69167179875814),
                        new LatLng(19.312200766946543,-103.6905559998076),
                        new LatLng(19.313091775989317,-103.68909687810445),
                        new LatLng(19.313577778874922,-103.68849606328288),
                        new LatLng(19.314063780315763,-103.68772358709396),
                        new LatLng(19.314225780475265,-103.68677944951973),
                        new LatLng(19.314630780170415,-103.6860928040055),
                        new LatLng(19.31552177596946,-103.68566365056921),
                        new LatLng(19.317060757274646,-103.68480534367868),
                        new LatLng(19.318761719753272,-103.68420452886608),
                        new LatLng(19.320462664529664,-103.68377537542078),
                        new LatLng(19.321353628538038,-103.68360371404448),
                        new LatLng(19.32200159931046,-103.68360371404448),
                        new LatLng(19.32232558373401,-103.68368954472814),
                        new LatLng(19.32224458768792,-103.68257374578661),
                        new LatLng(19.32289255492825,-103.68154377751974),
                        new LatLng(19.32305454633708,-103.68094296269817),
                        new LatLng(19.32345952415524,-103.68051380926187),
                        new LatLng(19.324107486577716,-103.68034214788555),
                        new LatLng(19.32499843071279,-103.67982716374763),
                        new LatLng(19.325970364227924,-103.6793121796187),
                        new LatLng(19.325403403713718,-103.67811054998451),
                        new LatLng(19.324755446430572,-103.67596478276712),
                        new LatLng(19.323702510364694,-103.67313237005347),
                        new LatLng(19.32321653758445,-103.67107243352874),
                        new LatLng(19.322406579739106,-103.66866917425139),
                        new LatLng(19.321758610571678,-103.66652340704297),
                        new LatLng(19.321029642187465,-103.66437763982559),
                        new LatLng(19.320543661458117,-103.6631760101914),
                        new LatLng(19.32078665200344,-103.6622318726172),
                        new LatLng(19.318923715256023,-103.66060108953774),
                        new LatLng(19.317384751492167,-103.66103024298302),
                        new LatLng(19.316007771630982,-103.66120190435932),
                        new LatLng(19.314630780170415,-103.66128773504298),
                        new LatLng(19.31390177999573,-103.66060108953774),
                        new LatLng(19.312686772480774,-103.66094441229036),
                        new LatLng(19.312119765883995,-103.66163105779562),
                        new LatLng(19.312119765883995,-103.6634335022514),
                        new LatLng(19.31203876478133,-103.66506428533984),
                        new LatLng(19.31203876478133,-103.66557926946878),
                        new LatLng(19.311066748414973,-103.66489262396352),
                        new LatLng(19.310337732348763,-103.6639484863893),
                        new LatLng(19.309527710685714,-103.66386265569666),
                        new LatLng(19.30887969046574,-103.66386265569666),
                        new LatLng(19.3080696615803,-103.66437763982559),
                        new LatLng(19.307340632152695,-103.66472096257823),
                        new LatLng(19.30612557588491,-103.66557926946878),
                        new LatLng(19.30555854653686,-103.66652340704297),
                        new LatLng(19.305072519817276,-103.66695256047926),
                        new LatLng(19.304424481945173,-103.66781086736982),
                        new LatLng(19.30361443099335,-103.66832585149875),
                        new LatLng(19.30223733516969,-103.66926998907297),
                        new LatLng(19.30094123439365,-103.67004246527085),
                        new LatLng(19.299159079060694,-103.67081494145978),
                        new LatLng(19.298105978147934,-103.67141575628136),
                        new LatLng(19.29721488746653,-103.67167324835033),
                        new LatLng(19.29664782723414,-103.67167324835033),
                        new LatLng(19.295513700873492,-103.67141575628136),
                        new LatLng(19.29421754683787,-103.67141575628136),
                        new LatLng(19.292840371926957,-103.67107243352874),
                        new LatLng(19.291382174097343,-103.67124409490505),
                        new LatLng(19.289680926878038,-103.67184490972664),
                        new LatLng(19.286602434588467,-103.67321820073715),
                        new LatLng(19.28595432357246,-103.67364735418242),
                        new LatLng(19.284820123122728,-103.6740765076277),
                        new LatLng(19.28319982313722,-103.67502064520191),
                        new LatLng(19.281741539443548,-103.67596478276712),
                        new LatLng(19.28076934310069,-103.67665142828136),
                        new LatLng(19.2803642595877,-103.67742390447027),
                        new LatLng(19.280202225901803,-103.67819638066818),
                        new LatLng(19.279716123883233,-103.67853970342081),
                        new LatLng(19.27890595064678,-103.67922634893502),
                        new LatLng(19.27760966513269,-103.67965550237132),
                        new LatLng(19.276799481478015,-103.67999882513293),
                        new LatLng(19.275584198482964,-103.68051380926187),
                        new LatLng(19.27461196559631,-103.68120045476712),
                        new LatLng(19.273963807133146,-103.68188710027238),
                        new LatLng(19.273396666375127,-103.68291706853923),
                        new LatLng(19.272829523653254,-103.68368954472814),
                        new LatLng(19.272262378969245,-103.68446202092603),
                        new LatLng(19.271533190062318,-103.68497700505498),
                        new LatLng(19.270885019420795,-103.68540615850024),
                        new LatLng(19.270641954769374,-103.6859211426292),
                        new LatLng(19.270479911467874,-103.68652195745078),
                        new LatLng(19.270236846215607,-103.68652195745078),
                        new LatLng(19.269588670447693,-103.68652195745078),
                        new LatLng(19.269183559290322,-103.68695111089605),
                        new LatLng(19.268616401988417,-103.68772358709396),
                        new LatLng(19.26788719685892,-103.68772358709396),
                        new LatLng(19.267239011797766,-103.68660778814343),
                        new LatLng(19.26634775315352,-103.68557781987657),
                        new LatLng(19.26521341696142,-103.68317456059921),
                        new LatLng(19.26432214730477,-103.68248791509396),
                        new LatLng(19.265375465469678,-103.67999882513293),
                        new LatLng(19.267239011797766,-103.67742390447027),
                        new LatLng(19.26845435668416,-103.67519230657821),
                        new LatLng(19.26942662610474,-103.67356152348977),
                        new LatLng(19.2700748025144,-103.67278904730085),
                        new LatLng(19.26975071462957,-103.67167324835033),
                        new LatLng(19.269183559290322,-103.67012829595454),
                        new LatLng(19.26837333397239,-103.66875500494403),
                        new LatLng(19.27056093313907,-103.6672958832319),
                        new LatLng(19.26983173666129,-103.6649784546472),
                        new LatLng(19.269507648295825,-103.66291851812245),
                        new LatLng(19.267482081495977,-103.6622318726172),
                        new LatLng(19.26578058603907,-103.66145939641929),
                        new LatLng(19.263754973181268,-103.66085858159772),
                        new LatLng(19.262134464875853,-103.66042942816144),
                        new LatLng(19.261000099531383,-103.65982861333983),
                        new LatLng(19.260432913915775,-103.66017193609247),
                        new LatLng(19.259622645348415,-103.66060108953774),
                        new LatLng(19.259298536800593,-103.66145939641929),
                        new LatLng(19.258326207312773,-103.66171688848826),
                        new LatLng(19.257434900217547,-103.66188854986457),
                        new LatLng(19.256624616836234,-103.66188854986457),
                        new LatLng(19.256138444886236,-103.66206021124088),
                        new LatLng(19.25549021337839,-103.65871281438932),
                        new LatLng(19.2543558020738,-103.66008610540878),
                        new LatLng(19.252005925128934,-103.66240353399351),
                        new LatLng(19.249412918402392,-103.66515011602348),
                        new LatLng(19.24609056864563,-103.66875500494403),
                        new LatLng(19.243578503442645,-103.67184490972664),
                        new LatLng(19.24163365236285,-103.674248169004),
                        new LatLng(19.240904327266964,-103.67527813726187),
                        new LatLng(19.240742254583104,-103.6756214600145),
                        new LatLng(19.240661218181604,-103.67587895208347),
                        new LatLng(19.240174998930726,-103.67587895208347),
                        new LatLng(19.239769815121296,-103.67587895208347),
                        new LatLng(19.239445667354218,-103.67536396795451),
                        new LatLng(19.239364630311822,-103.67502064520191),
                        new LatLng(19.23725765318333,-103.6746773224403),
                        new LatLng(19.23482649212949,-103.67441983038032),
                        new LatLng(19.23434025559927,-103.67502064520191),
                        new LatLng(19.233772977827954,-103.67356152348977),
                        new LatLng(19.23296257761151,-103.67115826422139),
                        new LatLng(19.231909051352577,-103.66841168218242),
                        new LatLng(19.231179683060383,-103.66583676152874),
                        new LatLng(19.230369270047458,-103.66377682501299),
                        new LatLng(19.229639894917554,-103.66480679327088),
                        new LatLng(19.2317469697895,-103.65613789373563),
                        new LatLng(19.2326384164057,-103.6519321900025),
                        new LatLng(19.232557376004472,-103.65081639105198),
                        new LatLng(19.231260724141407,-103.65055889898301),
                        new LatLng(19.230531352970107,-103.65055889898301),
                        new LatLng(19.22972093675915,-103.65098805242829),
                        new LatLng(19.228910516548865,-103.65107388312096),
                        new LatLng(19.228100092341077,-103.65107388312096),
                        new LatLng(19.227562407374453,-103.6510356653482),
                        new LatLng(19.226833019781417,-103.6503919351848),
                        new LatLng(19.2263872813251,-103.64966237433771),
                        new LatLng(19.2260225853247,-103.64906155951613),
                        new LatLng(19.22549580078353,-103.64893281348165),
                        new LatLng(19.225171624842194,-103.64893281348165),
                        new LatLng(19.22488797036829,-103.6491903055506),
                        new LatLng(19.22444222663492,-103.64923322089244),
                        new LatLng(19.224158570901782,-103.64910447485795),
                        new LatLng(19.22375334757652,-103.64906155951613),
                        new LatLng(19.223429168197296,-103.64936196692692),
                        new LatLng(19.2232670782674,-103.64966237433771),
                        new LatLng(19.22314551071527,-103.65034901984296),
                        new LatLng(19.223023943073198,-103.65069234259558),
                        new LatLng(19.22278080751922,-103.65094983466453),
                        new LatLng(19.222172967060413,-103.65107858069004),
                        new LatLng(19.22160564727056,-103.65120732672452),
                        new LatLng(19.220876233233945,-103.651336072759),
                        new LatLng(19.22038995541093,-103.651336072759),
                        new LatLng(19.220146815959847,-103.6510356653482),
                        new LatLng(19.220046937299138,-103.65088702750516),
                        new LatLng(19.219945629026505,-103.65084411215436),
                        new LatLng(19.219824059017245,-103.65084411215436),
                        new LatLng(19.219651834683813,-103.65083338332337),
                        new LatLng(19.21947961016905,-103.65087629866521),
                        new LatLng(19.219287123734293,-103.650929942847),
                        new LatLng(19.21921620762206,-103.65085484099428),
                        new LatLng(19.21910476795604,-103.65075828147069),
                        new LatLng(19.218912281081415,-103.65062953543621),
                        new LatLng(19.21871979398138,-103.65053297591258),
                        new LatLng(19.218567830322332,-103.65043641638897),
                        new LatLng(19.218375342819737,-103.65036131453628),
                        new LatLng(19.218152462271,-103.65028621268358),
                        new LatLng(19.21793971237464,-103.6503291280254),
                        new LatLng(19.217726962202892,-103.65033985686537),
                        new LatLng(19.21761552152714,-103.65036131453628),
                        new LatLng(19.217412901922,-103.65054370475254),
                        new LatLng(19.217220413066855,-103.65073682379978),
                        new LatLng(19.217220413066855,-103.65081192565246),
                        new LatLng(19.217331854010514,-103.65098358702876),
                        new LatLng(19.217392639948077,-103.65106941771245),
                        new LatLng(19.217666176388967,-103.65123035025779),
                        new LatLng(19.217858664722918,-103.65139128279415),
                        new LatLng(19.21793971237464,-103.65150929999767),
                        new LatLng(19.21804102188373,-103.65167023253402),
                        new LatLng(19.218132200388272,-103.65182043623943),
                        new LatLng(19.21820311696736,-103.65193845343396),
                        new LatLng(19.218314557245584,-103.65233542036836),
                        new LatLng(19.218344950035466,-103.65250708174469),
                        new LatLng(19.218385473746014,-103.65263582777916),
                        new LatLng(19.218344950035466,-103.65274311614272),
                        new LatLng(19.218314557245584,-103.65285040449731),
                        new LatLng(19.218182855090866,-103.65291477751903),
                        new LatLng(19.218000498087413,-103.6529469640299),
                        new LatLng(19.217909319509822,-103.65292550635),
                        new LatLng(19.217777617030396,-103.6528718621772),
                        new LatLng(19.21764591444629,-103.6527967603245),
                        new LatLng(19.217483818812298,-103.65270020079191),
                        new LatLng(19.21736224698211,-103.6526465566191),
                        new LatLng(19.217129234056546,-103.65261437010824),
                        new LatLng(19.216997530952064,-103.65256072592645),
                        new LatLng(19.216906351818103,-103.65270020079191),
                        new LatLng(19.216815172633563,-103.65278603148455),
                        new LatLng(19.216784779560815,-103.65288259100817),
                        new LatLng(19.216754386482446,-103.6530220658736),
                        new LatLng(19.21674425545479,-103.65311862540622),
                        new LatLng(19.21662268307763,-103.65321518492982),
                        new LatLng(19.216460586435428,-103.65335465979527),
                        new LatLng(19.21630862068767,-103.65338684630615),
                        new LatLng(19.21617691692569,-103.6533975751371),
                        new LatLng(19.216035081987673,-103.6534512193189),
                        new LatLng(19.215872984765188,-103.65347267698981),
                        new LatLng(19.215761542831647,-103.65347267698981),
                        new LatLng(19.21566023191848,-103.65338684630615),
                        new LatLng(19.215569052042753,-103.65333320212436),
                        new LatLng(19.21548800322129,-103.6532473714317),
                        new LatLng(19.215427216579396,-103.65306498122443),
                        new LatLng(19.215346167688864,-103.65295769286087),
                        new LatLng(19.215214463155778,-103.65286113333724),
                        new LatLng(19.21509288964723,-103.6527753026446),
                        new LatLng(19.214981447185686,-103.65268947196093),
                        new LatLng(19.21483961121619,-103.65268947196093),
                        new LatLng(19.214576201232834,-103.65268947196093),
                        new LatLng(19.214434364913703,-103.65268947196093),
                        new LatLng(19.21445462725227,-103.6528182179954),
                        new LatLng(19.21449515192272,-103.65296842170082),
                        new LatLng(19.214414102572658,-103.65315081190809),
                        new LatLng(19.214353315533682,-103.65336538863524),
                        new LatLng(19.214363446707836,-103.65351559234063),
                        new LatLng(19.214292528472214,-103.65385891509324),
                        new LatLng(19.214252003751817,-103.65423442435673),
                        new LatLng(19.2142013478379,-103.65443827223493),
                        new LatLng(19.21401898641752,-103.65460993361124),
                        new LatLng(19.21379609996207,-103.65478159498757),
                        new LatLng(19.213613738092178,-103.65491034102205),
                        new LatLng(19.213390851087333,-103.65499617171469),
                        new LatLng(19.213259144988626,-103.65499617171469),
                        new LatLng(19.213076782523142,-103.65501762938561),
                        new LatLng(19.21279310717449,-103.6543738992222),
                        new LatLng(19.212529693913016,-103.6538481862533),
                        new LatLng(19.21176984560121,-103.65218521666296),
                        new LatLng(19.21110117618123,-103.65079046798155),
                        new LatLng(19.210412241208626,-103.64927770209663),
                        new LatLng(19.209621988714762,-103.64758254599543),
                        new LatLng(19.209348438889744,-103.64685298514836),
                        new LatLng(19.20912554610301,-103.64634872985036),
                        new LatLng(19.208791206356874,-103.6454904229688),
                        new LatLng(19.208477129007008,-103.64473940444182),
                        new LatLng(19.20759568291647,-103.64259363723343),
                        new LatLng(19.206825680166457,-103.64073754859588),
                        new LatLng(19.20656225734497,-103.64005090308164),
                        new LatLng(19.206430545776854,-103.63975049567082),
                        new LatLng(19.206288702430506,-103.63941790175815),
                        new LatLng(19.206197517357918,-103.63936425757639),
                        new LatLng(19.205680800991672,-103.63984705520343),
                        new LatLng(19.20543763978702,-103.64009381843246),
                        new LatLng(19.20482973520272,-103.64060880256137),
                        new LatLng(19.203076931073785,-103.64218594145906),
                        new LatLng(19.202033345178524,-103.64309789252242),
                        new LatLng(19.200949224665592,-103.64403130126566),
                        new LatLng(19.198466865743605,-103.64619852614497),
                        new LatLng(19.196643067977586,-103.64781858039348),
                        new LatLng(19.19571089686946,-103.64862324309324),
                        new LatLng(19.194292365486064,-103.64985705923831),
                        new LatLng(19.194130246835797,-103.64999653411273),
                        new LatLng(19.194211306180893,-103.65003944945457),
                        new LatLng(19.19365402237492,-103.64992143226004),
                        new LatLng(19.192904219368476,-103.64979268622554),
                        new LatLng(19.192387461254,-103.64962102484925),
                        new LatLng(19.191678182768804,-103.6494064481311),
                        new LatLng(19.1909081055245,-103.64911676955127),
                        new LatLng(19.190604126673335,-103.64899875235673),
                        new LatLng(19.190675055122423,-103.64863397193321),
                        new LatLng(19.191049962130187,-103.64836575103327),
                        new LatLng(19.19124248161244,-103.6480868012934),
                        new LatLng(19.19171871305055,-103.64773274970082),
                        new LatLng(19.191931496864537,-103.64755035948455),
                        new LatLng(19.192022689843462,-103.64736796927728),
                        new LatLng(19.191820038709995,-103.64689590049018),
                        new LatLng(19.191556591864508,-103.64688517165023),
                        new LatLng(19.190857442421905,-103.64721776557188),
                        new LatLng(19.190543330835737,-103.64733578276642),
                        new LatLng(19.190411606444503,-103.64740015578815),
                        new LatLng(19.190330545227358,-103.64747525763185),
                        new LatLng(19.19027988194701,-103.64758254599543),
                        new LatLng(19.19020895332765,-103.64764691901716),
                        new LatLng(19.19009749400765,-103.64766837668809),
                        new LatLng(19.18998603461217,-103.64763619017721),
                        new LatLng(19.18988470782298,-103.64757181716446),
                        new LatLng(19.18995563658204,-103.64696027350293),
                        new LatLng(19.18998603461217,-103.64670278144294),
                        new LatLng(19.1899759019358,-103.64605905127954),
                        new LatLng(19.18995563658204,-103.64562989783425),
                        new LatLng(19.19001643263667,-103.64535094809439),
                        new LatLng(19.189752982903887,-103.64509345603439),
                        new LatLng(19.18941860379046,-103.64498616767082),
                        new LatLng(19.189104489459755,-103.64490033697818),
                        new LatLng(19.188972763916368,-103.64470721793096),
                        new LatLng(19.188790374528864,-103.64455701422557),
                        new LatLng(19.188648515976094,-103.64452482772367),
                        new LatLng(19.188587719416304,-103.64451409888375),
                        new LatLng(19.18844586068976,-103.64446045470196),
                        new LatLng(19.18834453295264,-103.64427806449466),
                        new LatLng(19.188263470718546,-103.64416004729117),
                        new LatLng(19.18831413461942,-103.64384891104939),
                        new LatLng(19.18836479850555,-103.64368797850405),
                        new LatLng(19.188537055599614,-103.64348413062585),
                        new LatLng(19.1886687814916,-103.64328028273867),
                        new LatLng(19.188820772774147,-103.64316226554415),
                        new LatLng(19.189013294863365,-103.64313007903327),
                        new LatLng(19.18916528582871,-103.64313007903327),
                        new LatLng(19.189570594380754,-103.6431729943751),
                        new LatLng(19.18967192136328,-103.64319445204602),
                        new LatLng(19.18984417708968,-103.64313007903327),
                        new LatLng(19.18998603461217,-103.64313007903327),
                        new LatLng(19.1899759019358,-103.64290477347517),
                        new LatLng(19.18993537122494,-103.64258290839348),
                        new LatLng(19.189945503903814,-103.64236833167533),
                        new LatLng(19.18999616728706,-103.64219667029901),
                        new LatLng(19.190077228668855,-103.64216448378815),
                        new LatLng(19.19025961663067,-103.64218594145906),
                        new LatLng(19.190431871742128,-103.64229322982263),
                        new LatLng(19.190745983540975,-103.6425292642117),
                        new LatLng(19.19083717717667,-103.64257217955353),
                        new LatLng(19.190958768612347,-103.64263655257527),
                        new LatLng(19.191131022992213,-103.6426258237353),
                        new LatLng(19.19121208381433,-103.64260436606439),
                        new LatLng(19.191424868283217,-103.6424970777098),
                        new LatLng(19.191455266042045,-103.64225031447182),
                        new LatLng(19.191516061542835,-103.6422288568009),
                        new LatLng(19.19164778505115,-103.6421322972773),
                        new LatLng(19.191769375888065,-103.6420035512428),
                        new LatLng(19.19187070151718,-103.64178897452466),
                        new LatLng(19.192022689843462,-103.64163877081927),
                        new LatLng(19.192124015315798,-103.64171387267196),
                        new LatLng(19.192225340726612,-103.64178897452466),
                        new LatLng(19.192387461254,-103.64192844939011),
                        new LatLng(19.192549581620852,-103.64205719542458),
                        new LatLng(19.19269143681194,-103.64223958564085),
                        new LatLng(19.192731966843343,-103.64233614516446),
                        new LatLng(19.192772496865626,-103.6424219758571),
                        new LatLng(19.192965014334106,-103.6424970777098),
                        new LatLng(19.193056206740454,-103.64255072188261),
                        new LatLng(19.193238591401556,-103.64261509490434),
                        new LatLng(19.19341084339565,-103.64261509490434),
                        new LatLng(19.193593227663783,-103.64241124701717),
                        new LatLng(19.193633757473986,-103.64226104331178),
                        new LatLng(19.193694552170147,-103.64214302611722),
                        new LatLng(19.1937148170636,-103.64187480521731),
                        new LatLng(19.193795876614146,-103.64163877081927),
                        new LatLng(19.193876936123917,-103.64141346526117),
                        new LatLng(19.193876936123917,-103.64125253272479),
                        new LatLng(19.193876936123917,-103.64107014250855),
                        new LatLng(19.19381614149512,-103.64086629462136),
                        new LatLng(19.193795876614146,-103.64069463324505),
                        new LatLng(19.1938364063736,-103.64037276816335),
                        new LatLng(19.19388706856014,-103.64019037795606),
                        new LatLng(19.19406945230057,-103.64005090308164),
                        new LatLng(19.194089717147868,-103.63994361472706),
                        new LatLng(19.193968128025002,-103.63964320731625),
                        new LatLng(19.19391746586425,-103.6395359189527),
                        new LatLng(19.19391746586425,-103.63941790175815),
                        new LatLng(19.19391746586425,-103.6391604096892),
                        new LatLng(19.19397826045563,-103.63904239249466),
                        new LatLng(19.194089717147868,-103.63889218878927),
                        new LatLng(19.19418090893106,-103.63875271392384),
                        new LatLng(19.194241703425114,-103.63873125625291),
                        new LatLng(19.194424086772568,-103.63870979857302),
                        new LatLng(19.194616602309207,-103.63866688323118),
                        new LatLng(19.194758455717952,-103.63864542556026),
                        new LatLng(19.194950970863484,-103.63873125625291),
                        new LatLng(19.195062426896992,-103.63875271392384),
                        new LatLng(19.19519414756717,-103.63863469672032),
                        new LatLng(19.195244809334923,-103.63852740836573),
                        new LatLng(19.195295471087093,-103.63839866233127),
                        new LatLng(19.195295471087093,-103.63811971259138),
                        new LatLng(19.19531573578428,-103.63790513587324),
                        new LatLng(19.195244809334923,-103.63767983031514),
                        new LatLng(19.195143485782967,-103.63743306708612),
                        new LatLng(19.19509282398402,-103.63717557501717),
                        new LatLng(19.194971235602225,-103.6368429811045),
                        new LatLng(19.19499150033848,-103.63652111602279),
                        new LatLng(19.195427191570747,-103.63667131972818),
                        new LatLng(19.19559944127427,-103.63667131972818),
                        new LatLng(19.195822352388316,-103.63670350623005),
                        new LatLng(19.1960047339841,-103.6366927773991),
                        new LatLng(19.196318835147743,-103.63674642158087),
                        new LatLng(19.196572142100297,-103.63681079459363),
                        new LatLng(19.196622803444704,-103.63681079459363),
                        new LatLng(19.196825448663795,-103.6366927773991),
                        new LatLng(19.196916638931103,-103.63652111602279),
                        new LatLng(19.197068622597648,-103.63632799697555),
                        new LatLng(19.197200341662064,-103.63616706443021),
                        new LatLng(19.197352325066582,-103.63597394538297),
                        new LatLng(19.197565101597085,-103.63577009749581),
                        new LatLng(19.197727216863676,-103.63559843611948),
                        new LatLng(19.197929860723573,-103.63544823241409),
                        new LatLng(19.198091975631648,-103.63538385940133),
                        new LatLng(19.198162900853003,-103.63535167289048),
                        new LatLng(19.198345279854752,-103.63528729987775),
                        new LatLng(19.198507394353527,-103.63531948637961),
                        new LatLng(19.198689772973456,-103.63531948637961),
                        new LatLng(19.198973472647356,-103.63531948637961),
                        new LatLng(19.19909505807203,-103.635222926856),
                        new LatLng(19.199409153335996,-103.6349225194452),
                        new LatLng(19.199571266786617,-103.6347937734197),
                        new LatLng(19.19958139887223,-103.63465429854527),
                        new LatLng(19.19958139887223,-103.63448263716896),
                        new LatLng(19.19945981380695,-103.63433243346357),
                        new LatLng(19.199378757046272,-103.63420368742909),
                        new LatLng(19.199368624949017,-103.6340963990745),
                        new LatLng(19.199378757046272,-103.63387109351642),
                        new LatLng(19.199348360750946,-103.63355995726567),
                        new LatLng(19.19928756814343,-103.633227363353),
                        new LatLng(19.199196379190045,-103.63296987128403),
                        new LatLng(19.199105190186128,-103.6326050908605),
                        new LatLng(19.199247039726504,-103.63224031043696),
                        new LatLng(19.19931796445,-103.63218666625517),
                        new LatLng(19.19950034217146,-103.63218666625517),
                        new LatLng(19.199702983847665,-103.63212229323344),
                        new LatLng(19.19990562527347,-103.63216520858425),
                        new LatLng(19.20005760617923,-103.63217593741523),
                        new LatLng(19.20018490969425,-103.63216074319374),
                        new LatLng(19.201301513761287,-103.63191108042756),
                        new LatLng(19.201787848039277,-103.63156775767493),
                        new LatLng(19.201787848039277,-103.63105277354599),
                        new LatLng(19.20138256957384,-103.63070945079338),
                        new LatLng(19.20101781810158,-103.63040904338257),
                        new LatLng(19.201139402015524,-103.63015155131362),
                        new LatLng(19.201257349562333,-103.6297668790815),
                        new LatLng(19.201581572660835,-103.62942355632887),
                        new LatLng(19.201824739565602,-103.62935918330714),
                        new LatLng(19.202108433833832,-103.6292948102944),
                        new LatLng(19.202311072296837,-103.62918752193085),
                        new LatLng(19.202452919072357,-103.62890857219995),
                        new LatLng(19.202452919072357,-103.62830775737835),
                        new LatLng(19.202817667362794,-103.62811463833113),
                        new LatLng(19.20308109618127,-103.62805026530941),
                        new LatLng(19.203364788282066,-103.62802880763849),
                        new LatLng(19.20370927089055,-103.62802880763849),
                        new LatLng(19.203992961908853,-103.62783568859123),
                        new LatLng(19.20425638884436,-103.62770694255676),
                        new LatLng(19.204519815357997,-103.62751382350955),
                        new LatLng(19.204681923772526,-103.62712758541508),
                        new LatLng(19.204823768503676,-103.62684863567522),
                        new LatLng(19.20496561311335,-103.62650531292259),
                        new LatLng(19.2050466671205,-103.62614053249006),
                        new LatLng(19.204884559065423,-103.62568992138286),
                        new LatLng(19.204580606032057,-103.62551826000653),
                        new LatLng(19.20441849751851,-103.6254538869848),
                        new LatLng(19.20425638884436,-103.62500327586862),
                        new LatLng(19.20415507084177,-103.62461703777416),
                        new LatLng(19.20407401639528,-103.6243595457052),
                        new LatLng(19.204377970364746,-103.62414496898705),
                        new LatLng(19.204580606032057,-103.62412351131614),
                        new LatLng(19.20498587661867,-103.6240805959743),
                        new LatLng(19.20543167311089,-103.62405913829441),
                        new LatLng(19.205857204998537,-103.62403768062349),
                        new LatLng(19.20624220909081,-103.62405913829441),
                        new LatLng(19.206566422364187,-103.62380164623443),
                        new LatLng(19.206789318619396,-103.6235870695073),
                        new LatLng(19.20711353081427,-103.62320083141283),
                        new LatLng(19.207336426327988,-103.62260001659126),
                        new LatLng(19.20753905835051,-103.6222137784968),
                        new LatLng(19.2076809006185,-103.6217631673806),
                        new LatLng(19.207822742763284,-103.62131255626443),
                        new LatLng(19.208005111055783,-103.62090486049904),
                        new LatLng(19.208045637315813,-103.62056153774643),
                        new LatLng(19.208106426686722,-103.62026113033563),
                        new LatLng(19.207984847922436,-103.61991780758301),
                        new LatLng(19.20794432164742,-103.61963885784313),
                        new LatLng(19.208065900441667,-103.61935990810325),
                        new LatLng(19.20828879466463,-103.61910241604328),
                        new LatLng(19.208450899363545,-103.61884492397432),
                        new LatLng(19.208572477783285,-103.61839431285811),
                        new LatLng(19.208592740845056,-103.6180509901055),
                        new LatLng(19.208450899363545,-103.61770766735287),
                        new LatLng(19.208268531565388,-103.61745017528392),
                        new LatLng(19.20788353221659,-103.61712831020219),
                        new LatLng(19.207782216448333,-103.6168493604713),
                        new LatLng(19.207701163789284,-103.61654895306052),
                        new LatLng(19.2076809006185,-103.61620563030789),
                        new LatLng(19.20786326906827,-103.61611979961523),
                        new LatLng(19.20816721603516,-103.61618417262798),
                        new LatLng(19.208369847034053,-103.6163772916842),
                        new LatLng(19.208592740845056,-103.6165274953896),
                        new LatLng(19.20885616041342,-103.61661332607326),
                        new LatLng(19.20916010554522,-103.61648458003879),
                        new LatLng(19.209504576015743,-103.61642020702604),
                        new LatLng(19.209767994123478,-103.61611979961523),
                        new LatLng(19.20986930866833,-103.61564773082814),
                        new LatLng(19.20990983446895,-103.61541169643908),
                        new LatLng(19.209788257037953,-103.61496108532289),
                        new LatLng(19.209686942443167,-103.61461776257025),
                        new LatLng(19.20974773120735,-103.61444610119395),
                        new LatLng(19.20997062315076,-103.61433881283038),
                        new LatLng(19.210355617613345,-103.61414569378317),
                        new LatLng(19.21070008557951,-103.61393111705601),
                        new LatLng(19.210355617613345,-103.61307281017447),
                        new LatLng(19.210193514791197,-103.61270802975092),
                        new LatLng(19.210071937569914,-103.61245053768195),
                        new LatLng(19.20997062315076,-103.61219304562198),
                        new LatLng(19.20997062315076,-103.61191409588211),
                        new LatLng(19.210173251927497,-103.61167806148406),
                        new LatLng(19.210456931796134,-103.61157077312947),
                        new LatLng(19.210760873969168,-103.61167806148406),
                        new LatLng(19.2109635017721,-103.61191409588211),
                        new LatLng(19.211287705737604,-103.61204284191659),
                        new LatLng(19.211530858292434,-103.61215013027116),
                        new LatLng(19.211733485147356,-103.61219304562198),
                        new LatLng(19.212057687594655,-103.61212867260025),
                        new LatLng(19.212260313799472,-103.61202138424568),
                        new LatLng(19.212422414584054,-103.61182826518946),
                        new LatLng(19.212625040339393,-103.61159223080038),
                        new LatLng(19.21264530290178,-103.61135619640235),
                        new LatLng(19.212584515208807,-103.61116307735513),
                        new LatLng(19.21238188940268,-103.61105578900053),
                        new LatLng(19.212138738106372,-103.61105578900053),
                        new LatLng(19.2118145358189,-103.61105578900053),
                        new LatLng(19.21140928205996,-103.61120599269695),
                        new LatLng(19.211166129325342,-103.61120599269695),
                        new LatLng(19.2109635017721,-103.61107724667147),
                        new LatLng(19.210882450681066,-103.61084121227341),
                        new LatLng(19.21080139954922,-103.61064809322619),
                        new LatLng(19.21090271345735,-103.61039060115723),
                        new LatLng(19.21114586658151,-103.61011165142632),
                        new LatLng(19.211328231188652,-103.60963958263922),
                        new LatLng(19.21142954477219,-103.60921042919395),
                        new LatLng(19.211632171751113,-103.6087812757487),
                        new LatLng(19.211875323796544,-103.60792296886711),
                        new LatLng(19.212077950226124,-103.60751527310174),
                        new LatLng(19.21211847548241,-103.60727923870371),
                        new LatLng(19.212260313799472,-103.60717195034013),
                        new LatLng(19.212503464915976,-103.60706466198555),
                        new LatLng(19.21278714076438,-103.60704320431462),
                        new LatLng(19.213293703561195,-103.60700028896382),
                        new LatLng(19.213800264798074,-103.6069788312929),
                        new LatLng(19.21408393840809,-103.60695737362198),
                        new LatLng(19.2144486609033,-103.60685008525843),
                        new LatLng(19.214894431741737,-103.60665696621119),
                        new LatLng(19.21519836571147,-103.60648530483489),
                        new LatLng(19.215218627955853,-103.606206355095),
                        new LatLng(19.215218627955853,-103.60594886303502),
                        new LatLng(19.21517810346458,-103.60562699795332),
                        new LatLng(19.21495521858063,-103.6052836752007),
                        new LatLng(19.214712071090176,-103.60509055614448),
                        new LatLng(19.21459049721009,-103.60487597942634),
                        new LatLng(19.21448918557439,-103.6046828603791),
                        new LatLng(19.214509447907027,-103.60431807995558),
                        new LatLng(19.214509447907027,-103.60403913021568),
                        new LatLng(19.214529710236327,-103.60386746883937),
                        new LatLng(19.214772857996508,-103.60371726513398),
                        new LatLng(19.21523889019858,-103.60388892651028),
                        new LatLng(19.21550229911926,-103.60388892651028),
                        new LatLng(19.215867018466867,-103.60384601116846),
                        new LatLng(19.216130426381028,-103.6037601804758),
                        new LatLng(19.21637357177359,-103.6035885190995),
                        new LatLng(19.216474882247958,-103.60322373867595),
                        new LatLng(19.216555930581997,-103.60277312755977),
                        new LatLng(19.216555930581997,-103.60242980480714),
                        new LatLng(19.21659645473445,-103.60210793972543),
                        new LatLng(19.2167585512435,-103.6018719053274),
                        new LatLng(19.217103005793337,-103.6017646169728),
                        new LatLng(19.217467721589358,-103.60165732860924),
                        new LatLng(19.217832436576117,-103.60178607464373),
                        new LatLng(19.21831872196626,-103.6018719053274),
                        new LatLng(19.21874422050327,-103.6018719053274),
                        new LatLng(19.21902788558153,-103.60185044765649),
                        new LatLng(19.219392597106307,-103.60165732860924),
                        new LatLng(19.21963573767322,-103.60137837886936),
                        new LatLng(19.219716784449485,-103.6010779714586),
                        new LatLng(19.219812126708053,-103.60055718829125),
                        new LatLng(19.219690556600042,-103.60004220415334),
                        new LatLng(19.219487939553797,-103.5996988814007),
                        new LatLng(19.21960950981169,-103.59939847398991),
                        new LatLng(19.219812126708053,-103.59922681261361),
                        new LatLng(19.22009578994398,-103.59922681261361),
                        new LatLng(19.220582068637437,-103.59901223589544),
                        new LatLng(19.220784684334756,-103.59909806657912),
                        new LatLng(19.221311483979694,-103.59888348986097),
                        new LatLng(19.22135200695963,-103.59828267503939),
                        new LatLng(19.221433052888646,-103.59759602953415),
                        new LatLng(19.221108868931896,-103.59686646868705),
                        new LatLng(19.220987299783115,-103.59609399248916),
                        new LatLng(19.220865730544414,-103.59566483904389),
                        new LatLng(19.220865730544414,-103.5949352781968),
                        new LatLng(19.220987299783115,-103.59424863268256),
                        new LatLng(19.22147357583861,-103.59347615648466),
                        new LatLng(19.22179775907591,-103.59274659563759),
                        new LatLng(19.222324555473257,-103.59180245806338),
                        new LatLng(19.222648737031754,-103.590901235831),
                        new LatLng(19.223053963081142,-103.59021459032574),
                        new LatLng(19.22337814320063,-103.58952794482049),
                        new LatLng(19.22358075545042,-103.58884129931525),
                        new LatLng(19.22358075545042,-103.58815465380101),
                        new LatLng(19.22358075545042,-103.5875109236376),
                        new LatLng(19.223702322680538,-103.58699593950865),
                        new LatLng(19.22390493453142,-103.58660970141419),
                        new LatLng(19.224472246382888,-103.58622346331076),
                        new LatLng(19.224796423705005,-103.5855368178055),
                        new LatLng(19.224755901574508,-103.58476434160761),
                        new LatLng(19.224634335123906,-103.58403478076053),
                        new LatLng(19.224431724173296,-103.58343396593894),
                        new LatLng(19.224431724173296,-103.58304772784447),
                        new LatLng(19.22471537943402,-103.58266148974104),
                        new LatLng(19.225201644456895,-103.58231816698841),
                        new LatLng(19.225768951831885,-103.58188901354315),
                        new LatLng(19.226174170185132,-103.5815456907905),
                        new LatLng(19.226336257247095,-103.58115945269606),
                        new LatLng(19.22637677898781,-103.58077321459263),
                        new LatLng(19.226336257247095,-103.58034406115632),
                        new LatLng(19.226052604783934,-103.57957158495842),
                        new LatLng(19.226052604783934,-103.57905660082947),
                        new LatLng(19.226174170185132,-103.57841287066607),
                        new LatLng(19.22641730071767,-103.57789788653714),
                        new LatLng(19.226822517472637,-103.57742581775003),
                        new LatLng(19.22730877625889,-103.57648168017582),
                        new LatLng(19.227632947983178,-103.57613835742318),
                        new LatLng(19.228281289513497,-103.57566628863609),
                        new LatLng(19.229091712828172,-103.57502255847265),
                        new LatLng(19.22949692298528,-103.57455048968555),
                        new LatLng(19.229821090392285,-103.57420716693294),
                        new LatLng(19.226903560703338,-103.57124600817582),
                        new LatLng(19.228686501670616,-103.56995854784897),
                        new LatLng(19.225404254458912,-103.56897149493294),
                        new LatLng(19.220582068637437,-103.56751237322979),
                        new LatLng(19.22017683649253,-103.56751237322979),
                        new LatLng(19.219285322256916,-103.56708321978452),
                        new LatLng(19.218434326884694,-103.5669115584082),
                        new LatLng(19.217664374894678,-103.5669115584082),
                        new LatLng(19.21717808756862,-103.56652532031374),
                        new LatLng(19.216691798803897,-103.56639657427927),
                        new LatLng(19.21620550860055,-103.56609616686846),
                        new LatLng(19.216408129693157,-103.56523785998691),
                        new LatLng(19.216002887257336,-103.56420789172006),
                        new LatLng(19.2158002656652,-103.56317792346218),
                        new LatLng(19.215678692589886,-103.56253419329876),
                        new LatLng(19.215881314331863,-103.56201920916982),
                        new LatLng(19.216408129693157,-103.56133256366456),
                        new LatLng(19.216853895218094,-103.55747018267508),
                        new LatLng(19.21705651551232,-103.5553673308085),
                        new LatLng(19.216894419297088,-103.5515478651788),
                        new LatLng(19.216570226387937,-103.5481146376346),
                        new LatLng(19.216489178060108,-103.54725633075303),
                        new LatLng(19.215597643823344,-103.54296479632724),
                        new LatLng(19.215962362959406,-103.54219232012935),
                        new LatLng(19.216164984351725,-103.54146275928225),
                        new LatLng(19.216732322922002,-103.53978906086097),
                        new LatLng(19.21652970222901,-103.53850160052515),
                        new LatLng(19.21636760549428,-103.53575501849517),
                        new LatLng(19.216164984351725,-103.53296552112334),
                        new LatLng(19.21636760549428,-103.53253636767808),
                        new LatLng(19.216570226387937,-103.53219304492544),
                        new LatLng(19.2161244600929,-103.53060517718781),
                        new LatLng(19.215840790003945,-103.52936063221179),
                        new LatLng(19.215516595015973,-103.52657113483099),
                        new LatLng(19.215273448355845,-103.52451119830626),
                        new LatLng(19.215232923878087,-103.52300916126127),
                        new LatLng(19.21511135038318,-103.52155003955812),
                        new LatLng(19.2151923993895,-103.52069173267657),
                        new LatLng(19.215597643823344,-103.51987634113684),
                        new LatLng(19.21604341154612,-103.5190609495971),
                        new LatLng(19.21652970222901,-103.51764474323578),
                        new LatLng(19.217015991473286,-103.51644311359263),
                        new LatLng(19.217299659535016,-103.51451192310236),
                        new LatLng(19.21717808756862,-103.51403985431526),
                        new LatLng(19.216853895218094,-103.51386819293893),
                        new LatLng(19.216570226387937,-103.51326737812633),
                        new LatLng(19.216732322922002,-103.51275239398842),
                        new LatLng(19.216813371129952,-103.51206574848317),
                        new LatLng(19.216732322922002,-103.5115078490124),
                        new LatLng(19.216408129693157,-103.51099286487448),
                        new LatLng(19.216083935824944,-103.5107353728145),
                        new LatLng(19.215678692589886,-103.51030621936923),
                        new LatLng(19.21507082586462,-103.5099628966166),
                        new LatLng(19.214422432213265,-103.5099628966166),
                        new LatLng(19.21361193655349,-103.5099628966166),
                        new LatLng(19.212760911810115,-103.51052079608738),
                        new LatLng(19.212679861604233,-103.5110357802253),
                        new LatLng(19.21247723591646,-103.51159367969605),
                        new LatLng(19.212396185571585,-103.51249490192843),
                        new LatLng(19.21223408476115,-103.51343903950266),
                        new LatLng(19.211909882661686,-103.51455483845318),
                        new LatLng(19.211504629137753,-103.51524148395842),
                        new LatLng(19.211058849108383,-103.5153273146421),
                        new LatLng(19.211018323590938,-103.51472649982948),
                        new LatLng(19.210815695855548,-103.51429734638421),
                        new LatLng(19.210410439634835,-103.51386819293893),
                        new LatLng(19.209964656638235,-103.51339612415184),
                        new LatLng(19.20923519094706,-103.51275239398842),
                        new LatLng(19.20842466971757,-103.51202283314133),
                        new LatLng(19.20753309175061,-103.51163659503788),
                        new LatLng(19.206803615273664,-103.51137910297793),
                        new LatLng(19.205912028518767,-103.51112161090896),
                        new LatLng(19.205223071807477,-103.51107869556714),
                        new LatLng(19.204250422125366,-103.51099286487448),
                        new LatLng(19.203156184358345,-103.51094994953263),
                        new LatLng(19.20234563317176,-103.5110357802253),
                        new LatLng(19.201575605844837,-103.51125035694344),
                        new LatLng(19.200967686988434,-103.51120744160161),
                        new LatLng(19.200440822162264,-103.51094994953263),
                        new LatLng(19.19987342738627,-103.51086411884897),
                        new LatLng(19.19946814420496,-103.51034913471106),
                        new LatLng(19.199306030652743,-103.50974831989845),
                        new LatLng(19.198941274576892,-103.50858960559711),
                        new LatLng(19.19873863196343,-103.50790296009187),
                        new LatLng(19.19825228867219,-103.50713048389396),
                        new LatLng(19.1978064727261,-103.50648675373056),
                        new LatLng(19.19715801101127,-103.50610051563609),
                        new LatLng(19.19671219209964,-103.50558553150715),
                        new LatLng(19.196023196861745,-103.50485597065109),
                        new LatLng(19.195455786852836,-103.5042551558295),
                        new LatLng(19.19476678635396,-103.50339684894793),
                        new LatLng(19.194118312662344,-103.50288186481899),
                        new LatLng(19.193469836414845,-103.50240979603188),
                        new LatLng(19.19241605706336,-103.50202355793743),
                        new LatLng(19.191564922662568,-103.50206647327927),
                        new LatLng(19.190794844889275,-103.50189481190296),
                        new LatLng(19.189984232813856,-103.50112233570506),
                        new LatLng(19.189376271138283,-103.5005644362253),
                        new LatLng(19.188687245192117,-103.49987779072005),
                        new LatLng(19.187998216361404,-103.49914822987296),
                        new LatLng(19.186822807584466,-103.49850449970954),
                        new LatLng(19.186174302597998,-103.49824700764059),
                        new LatLng(19.185323135927526,-103.49824700764059),
                        new LatLng(19.184350368631023,-103.49820409229875),
                        new LatLng(19.183539724827995,-103.49820409229875),
                        new LatLng(19.18276960951847,-103.49820409229875),
                        new LatLng(19.18167522893687,-103.4985903303932),
                        new LatLng(19.18086457196617,-103.49867616108585),
                        new LatLng(19.179932311516477,-103.498890737804),
                        new LatLng(19.179283779396954,-103.49876199176953),
                        new LatLng(19.17855417770895,-103.49846158435872),
                        new LatLng(19.178027241147976,-103.49820409229875),
                        new LatLng(19.177297633895567,-103.49764619281899),
                        new LatLng(19.176486955380295,-103.49760327747717),
                        new LatLng(19.175676272876736,-103.49747453144269),
                        new LatLng(19.17462237965768,-103.49756036213533),
                        new LatLng(19.17401436127292,-103.49786076954612),
                        new LatLng(19.173690083883937,-103.49824700764059),
                        new LatLng(19.172960457424733,-103.49901948383848),
                        new LatLng(19.172352432907502,-103.49914822987296),
                        new LatLng(19.17219029265706,-103.49936280659111),
                        new LatLng(19.1718254765109,-103.50112233570506),
                        new LatLng(19.17142012429074,-103.50185189656113),
                        new LatLng(19.170974235695766,-103.50253854206636),
                        new LatLng(19.1705688813818,-103.50292478016082),
                        new LatLng(19.17020406164627,-103.5030535261953),
                        new LatLng(19.169879776758382,-103.50279603412635),
                        new LatLng(19.169433883996742,-103.5024527113737),
                        new LatLng(19.16858263082873,-103.50219521931373),
                        new LatLng(19.167528692243184,-103.50202355793743),
                        new LatLng(19.166312600886688,-103.50202355793743),
                        new LatLng(19.165542405053674,-103.50180898121032),
                        new LatLng(19.16431175067205,-103.50168649861627),
                        new LatLng(19.163663157074655,-103.50168649861627),
                        new LatLng(19.162933486227946,-103.50172941395809),
                        new LatLng(19.16252811213968,-103.50202982136888),
                        new LatLng(19.162365962225984,-103.50254480549782),
                        new LatLng(19.1620416619185,-103.50284521290861),
                        new LatLng(19.16127144613291,-103.50348894307203),
                        new LatLng(19.160379612834735,-103.5042185039281),
                        new LatLng(19.159406698280495,-103.50529138753679),
                        new LatLng(19.158960777190902,-103.50602094838386),
                        new LatLng(19.15863647018727,-103.50657884786364),
                        new LatLng(19.157947315686695,-103.50756590077967),
                        new LatLng(19.1571365419982,-103.50829546162674),
                        new LatLng(19.156528459116622,-103.5088533611065),
                        new LatLng(19.155069051053967,-103.50936834523546),
                        new LatLng(19.154177184214273,-103.50941126057728),
                        new LatLng(19.15271775534849,-103.50954000661176),
                        new LatLng(19.151055612303903,-103.50962583729543),
                        new LatLng(19.1492312896863,-103.50975458332991),
                        new LatLng(19.148420473158847,-103.50988332936438),
                        new LatLng(19.147731275978693,-103.51035539815149),
                        new LatLng(19.147163699668184,-103.51091329762227),
                        new LatLng(19.14683936947201,-103.51155702778568),
                        new LatLng(19.145866375058176,-103.5122007579491),
                        new LatLng(19.145217708928346,-103.5122007579491),
                        new LatLng(19.143879826984318,-103.5125011653599),
                        new LatLng(19.142258137361086,-103.5126728267362),
                        new LatLng(19.14079860307821,-103.5130161494978),
                        new LatLng(19.139460685316084,-103.51331655689962),
                        new LatLng(19.13889308056577,-103.51353113362674),
                        new LatLng(19.138568734117303,-103.51396028706304),
                        new LatLng(19.13763623453051,-103.5140461177557),
                        new LatLng(19.136784817264502,-103.5140461177557),
                        new LatLng(19.136541554382124,-103.51353113362674),
                        new LatLng(19.13601448357387,-103.51314489552331),
                        new LatLng(19.135446866976384,-103.51262991139437),
                        new LatLng(19.13483870417076,-103.51215784260725),
                        new LatLng(19.13353764492767,-103.51142984762254),
                        new LatLng(19.13272675131379,-103.51082903280096),
                        new LatLng(19.132078033557082,-103.51108652486991),
                        new LatLng(19.131591493566972,-103.5115156783062),
                        new LatLng(19.131591493566972,-103.51211649312778),
                        new LatLng(19.1310238617665,-103.51297480000935),
                        new LatLng(19.13037513732135,-103.51374727620725),
                        new LatLng(19.129645319274463,-103.51434809102884),
                        new LatLng(19.128996589414395,-103.51512056722675),
                        new LatLng(19.128023489847823,-103.51589304342464),
                        new LatLng(19.1269692921817,-103.51718050375146),
                        new LatLng(19.12591508778953,-103.51821047200934),
                        new LatLng(19.12518525003895,-103.51898294820725),
                        new LatLng(19.12396884662387,-103.51992708578146),
                        new LatLng(19.123157906038376,-103.52027040853407),
                        new LatLng(19.121860392824985,-103.52061373128672),
                        new LatLng(19.120725060405768,-103.52138620748461),
                        new LatLng(19.119589720187115,-103.52190119161355),
                        new LatLng(19.118778758113628,-103.52215868368252),
                        new LatLng(19.117967792061176,-103.52224451436616),
                        new LatLng(19.116589140639935,-103.5233603133167),
                        new LatLng(19.115615968008264,-103.52361780538565),
                        new LatLng(19.114886084774902,-103.52361780538565),
                        new LatLng(19.11415619831915,-103.5233603133167),
                        new LatLng(19.113426308641095,-103.52250200643512),
                        new LatLng(19.112858614441013,-103.52164369954457),
                        new LatLng(19.11237201786148,-103.52087122335567),
                        new LatLng(19.111885419849916,-103.52052790059406),
                        new LatLng(19.11050671770715,-103.52052790059406),
                        new LatLng(19.108398092197,-103.52078539266303),
                        new LatLng(19.106938258786148,-103.52095705403931),
                        new LatLng(19.10515400045195,-103.5212145461083),
                        new LatLng(19.10320751486471,-103.52130037679197),
                        new LatLng(19.10207205436169,-103.52112871541566),
                        new LatLng(19.1011799013565,-103.51958376302883),
                        new LatLng(19.10028774354137,-103.5188971175146),
                        new LatLng(19.099882215670945,-103.51803881063303),
                        new LatLng(19.098908944727476,-103.5176954878804),
                        new LatLng(19.09785456141222,-103.51786714925672),
                        new LatLng(19.0972057068016,-103.51872545613828),
                        new LatLng(19.09598909755012,-103.51949793233618),
                        new LatLng(19.094934695630528,-103.5204420699104),
                        new LatLng(19.093636960970613,-103.52155786886092),
                        new LatLng(19.092825871640496,-103.5223303450588),
                        new LatLng(19.09152812044685,-103.52258783711879),
                        new LatLng(19.090311469462623,-103.52293115987142),
                        new LatLng(19.088689254241345,-103.52310282124773),
                        new LatLng(19.086985911152716,-103.52387529744563),
                        new LatLng(19.085444776116915,-103.52430445089091),
                        new LatLng(19.08349805872739,-103.52473360433618),
                        new LatLng(19.081875776757272,-103.52499109639616),
                        new LatLng(19.080983514900012,-103.5250769270888),
                        new LatLng(19.0803345941666,-103.52567774190142),
                        new LatLng(19.07936120830033,-103.52627855672299),
                        new LatLng(19.07838781671436,-103.52627855672299),
                        new LatLng(19.077738885812618,-103.52542024984143),
                        new LatLng(19.077576652690652,-103.52421862019827),
                        new LatLng(19.077414419408957,-103.5233603133167),
                        new LatLng(19.077333302708958,-103.52250200643512),
                        new LatLng(19.076035430102586,-103.52215868368252),
                        new LatLng(19.07473754733023,-103.52181536092091),
                        new LatLng(19.073601891564348,-103.52190119161355),
                        new LatLng(19.072790704109718,-103.52224451436616),
                        new LatLng(19.07092495789399,-103.52318865194037),
                        new LatLng(19.069545914582434,-103.52310282124773),
                        new LatLng(19.067842374650716,-103.52284532918776),
                        new LatLng(19.066057694992207,-103.52215868368252),
                        new LatLng(19.065003102525814,-103.52069956197037),
                        new LatLng(19.06394850335186,-103.51949793233618),
                        new LatLng(19.063056144964676,-103.51863962545461),
                        new LatLng(19.061514787347562,-103.51743799581145),
                        new LatLng(19.060784665580996,-103.51640802755355),
                        new LatLng(19.06037904098886,-103.51443392171251),
                        new LatLng(19.059973415403714,-103.51263147725673),
                        new LatLng(19.058837658487633,-103.51117235555358),
                        new LatLng(19.058026398785977,-103.50971323385043),
                        new LatLng(19.056890628536383,-103.50928408040515),
                        new LatLng(19.05494357572919,-103.50868326559254),
                        new LatLng(19.052428598695013,-103.50730997457308),
                        new LatLng(19.051049401555,-103.50705248251309),
                        new LatLng(19.050075843729598,-103.5067091597515),
                        new LatLng(19.04942680200557,-103.50619417562254),
                        new LatLng(19.049102280191953,-103.50430590047412),
                        new LatLng(19.048858888415026,-103.50190264120575),
                        new LatLng(19.048290972880004,-103.50121599569152),
                        new LatLng(19.04723626744555,-103.50078684225522),
                        new LatLng(19.04618155530715,-103.4996710433047),
                        new LatLng(19.04464004089513,-103.49872690573048),
                        new LatLng(19.043747578638687,-103.49778276815627),
                        new LatLng(19.042043773734108,-103.49726778402731),
                        new LatLng(19.04042108613789,-103.49649530782943),
                        new LatLng(19.039122924641788,-103.49632364645312),
                        new LatLng(19.037500208497445,-103.49632364645312),
                        new LatLng(19.03652657119887,-103.49632364645312),
                        new LatLng(19.035228379254058,-103.49701029195838),
                        new LatLng(19.03449814182433,-103.49632364645312),
                        new LatLng(19.03449814182433,-103.49495035543364),
                        new LatLng(19.033767901184518,-103.49400621785942),
                        new LatLng(19.032713103481104,-103.49160295858206),
                        new LatLng(19.032713103481104,-103.49083048239315),
                        new LatLng(19.031414881727088,-103.49091631307681),
                        new LatLng(19.030684627531258,-103.49125963582945),
                        new LatLng(19.030035509995123,-103.49108797445314),
                        new LatLng(19.02922410950778,-103.49005800619526),
                        new LatLng(19.028818407778772,-103.48937136069),
                        new LatLng(19.027925860485915,-103.48911386862103),
                        new LatLng(19.027195590952633,-103.48885637655208),
                        new LatLng(19.026303034941485,-103.48885637655208),
                        new LatLng(19.02565390028618,-103.48945719137366),
                        new LatLng(19.024842478399506,-103.49048715964052),
                        new LatLng(19.024112195314174,-103.49203211202735),
                        new LatLng(19.02338190901941,-103.4928904189089),
                        new LatLng(19.02208361434928,-103.49374872579945),
                        new LatLng(19.020785309535842,-103.49383455648311),
                        new LatLng(19.018837833300754,-103.49417787923575),
                        new LatLng(19.017377211150535,-103.49417787923575),
                        new LatLng(19.01599772289088,-103.49434954062103),
                        new LatLng(19.014942812380955,-103.49434954062103),
                        new LatLng(19.014374780871663,-103.49537950887891),
                        new LatLng(19.014050190567065,-103.49649530782943),
                        new LatLng(19.013238712032855,-103.49795442953257),
                        new LatLng(19.01185918943503,-103.49889856710679),
                        new LatLng(19.011453445316214,-103.49864107503782),
                        new LatLng(19.011291147391308,-103.49795442953257),
                        new LatLng(19.011372296373978,-103.49761110677997),
                        new LatLng(19.011291147391308,-103.49709612265103),
                        new LatLng(19.01072310340806,-103.49649530782943),
                        new LatLng(19.009749309207738,-103.49563700094787),
                        new LatLng(19.008613208767105,-103.49486452474997),
                        new LatLng(19.00723364779884,-103.49409204855206),
                        new LatLng(19.003906424272866,-103.49297624960154),
                        new LatLng(19.001958750353758,-103.4928904189089),
                        new LatLng(19.000741442576594,-103.49246126547261),
                        new LatLng(18.999118351687606,-103.4922896040963),
                        new LatLng(18.99790102313021,-103.4915171278984),
                        new LatLng(18.996277904537145,-103.49108797445314),
                        new LatLng(18.994979398264512,-103.49083048239315),
                        new LatLng(18.99359972425049,-103.49083048239315),
                        new LatLng(18.992706987913262,-103.49117380514578),
                        new LatLng(18.990678023885632,-103.49186045065102),
                        new LatLng(18.989135994687704,-103.49186045065102),
                        new LatLng(18.98767511174727,-103.49203211202735),
                        new LatLng(18.986538860598927,-103.49203211202735),
                        new LatLng(18.984509821407794,-103.4915171278984),
                        new LatLng(18.98256192052793,-103.49125963582945),
                        new LatLng(18.981831451824856,-103.49083048239315),
                        new LatLng(18.98126330728584,-103.49022966757155),
                        new LatLng(18.98085748857108,-103.4899721755026),
                        new LatLng(18.980289340710165,-103.49022966757155),
                        new LatLng(18.979802355286886,-103.49100214376946),
                        new LatLng(18.978828380169574,-103.49160295858206),
                        new LatLng(18.97761090326564,-103.49186045065102),
                        new LatLng(18.97663691533692,-103.49263292684891),
                        new LatLng(18.975175922767683,-103.49383455648311),
                        new LatLng(18.97379608469171,-103.49495035543364),
                        new LatLng(18.972903242200733,-103.49555117025523),
                        new LatLng(18.97217273115047,-103.49555117025523),
                        new LatLng(18.971279879962623,-103.4952078475026),
                        new LatLng(18.970630530639877,-103.49434954062103),
                        new LatLng(18.97014351698785,-103.49331957235417),
                        new LatLng(18.969656501912855,-103.49194628134369),
                        new LatLng(18.969656501912855,-103.49100214376946),
                        new LatLng(18.969575332595642,-103.49040132894788),
                        new LatLng(18.96868246749408,-103.48962885274999),
                        new LatLng(18.967221405194366,-103.48902803792839),
                        new LatLng(18.96600384349623,-103.48902803792839),
                        new LatLng(18.965029787735514,-103.48885637655208),
                        new LatLng(18.9637310378695,-103.48868471517576),
                        new LatLng(18.962351105052132,-103.48894220724473),
                        new LatLng(18.961133507789146,-103.48962885274999),
                        new LatLng(18.959834727575977,-103.4899721755026),
                        new LatLng(18.958860635779025,-103.4905729903242),
                        new LatLng(18.95821123808685,-103.49211794272),
                        new LatLng(18.95788653829303,-103.49417787923575),
                        new LatLng(18.958373587747136,-103.49529367818627),
                        new LatLng(18.958860635779025,-103.49598032370051),
                        new LatLng(18.95942885668587,-103.49649530782943),
                        new LatLng(18.960808813683677,-103.49821192160155),
                        new LatLng(18.96129585460397,-103.50001436605731),
                        new LatLng(18.96194524028533,-103.50164514913678),
                        new LatLng(18.96226993217729,-103.50370508566152),
                        new LatLng(18.962513450681588,-103.50525003804833),
                        new LatLng(18.96381221003223,-103.50653749837518),
                        new LatLng(18.963649865666394,-103.50791078939464),
                        new LatLng(18.96316283162139,-103.50902658834518),
                        new LatLng(18.961377027952956,-103.50894075765252),
                        new LatLng(18.960402945164127,-103.50816828146363),
                        new LatLng(18.959591205160105,-103.50688082113678),
                        new LatLng(18.95918533367736,-103.50585085286993),
                        new LatLng(18.95918533367736,-103.5049067152957),
                        new LatLng(18.95894181031329,-103.50404840841414),
                        new LatLng(18.95829241293717,-103.50353342428521),
                        new LatLng(18.957480662661265,-103.50344759359255),
                        new LatLng(18.95666890843575,-103.50353342428521),
                        new LatLng(18.95545126969023,-103.5039625777215),
                        new LatLng(18.95399009146384,-103.50413423909781),
                        new LatLng(18.951635944069853,-103.50473505391939),
                        new LatLng(18.948875866895772,-103.5056791914936),
                        new LatLng(18.947658171277126,-103.50722414388939),
                        new LatLng(18.946684008385237,-103.50842577352358),
                        new LatLng(18.946115744072777,-103.51005655660305),
                        new LatLng(18.946521647351016,-103.51117235555358),
                        new LatLng(18.94733345094522,-103.5113440169299),
                        new LatLng(18.948145250590763,-103.51160150899885),
                        new LatLng(18.949119404953304,-103.51211649312778),
                        new LatLng(18.949525300926346,-103.51348978414727),
                        new LatLng(18.949687659039302,-103.51486307515778),
                        new LatLng(18.949931195912118,-103.51615053548461),
                        new LatLng(18.949931195912118,-103.5170946730588),
                        new LatLng(18.95001237479047,-103.51803881063303),
                        new LatLng(18.95009355363016,-103.52035623921776),
                        new LatLng(18.94976883803611,-103.52104288473198),
                        new LatLng(18.949038225640574,-103.52181536092091),
                        new LatLng(18.948064070804154,-103.5223303450588),
                        new LatLng(18.94676518884397,-103.52241617574248),
                        new LatLng(18.946278105502696,-103.52198702229721),
                        new LatLng(18.945141572178304,-103.52147203816827),
                        new LatLng(18.94408621287612,-103.52087122335567),
                        new LatLng(18.943193211333046,-103.52001291646512),
                        new LatLng(18.942300205012106,-103.5188971175146),
                        new LatLng(18.942300205012106,-103.51795297994039),
                        new LatLng(18.94156955992403,-103.51743799581145),
                        new LatLng(18.94067654491814,-103.51640802755355),
                        new LatLng(18.93945878948564,-103.51537805928672),
                        new LatLng(18.938159840568712,-103.51451975240515),
                        new LatLng(18.93718562224973,-103.51451975240515),
                        new LatLng(18.93580546990191,-103.51434809102884),
                        new LatLng(18.934993610251276,-103.51434809102884),
                        new LatLng(18.93434411969031,-103.51503473653409),
                        new LatLng(18.93410056007859,-103.51623636617727),
                        new LatLng(18.933694626602826,-103.51743799581145),
                        new LatLng(18.933288692140493,-103.51924044026723),
                        new LatLng(18.932395632851946,-103.51992708578146),
                        new LatLng(18.931259005032754,-103.52078539266303),
                        new LatLng(18.9302035579897,-103.52112871541566),
                        new LatLng(18.92890453709154,-103.52138620748461),
                        new LatLng(18.928092643901227,-103.52241617574248),
                        new LatLng(18.927605506093133,-103.52344614400934),
                        new LatLng(18.92695598680611,-103.52447611226722),
                        new LatLng(18.9275243163198,-103.52550608052508),
                        new LatLng(18.92776788551975,-103.52705103292088),
                        new LatLng(18.92776788551975,-103.52859598531668),
                        new LatLng(18.92768669582616,-103.52979761495088),
                        new LatLng(18.92768669582616,-103.53082758320875),
                        new LatLng(18.92768669582616,-103.53194338215927),
                        new LatLng(18.927199556834527,-103.5332308424861),
                        new LatLng(18.92508860479256,-103.53434664143664),
                        new LatLng(18.924033118771973,-103.53434664143664),
                        new LatLng(18.923302393775465,-103.53417498006033),
                        new LatLng(18.92208451168058,-103.53391748800034),
                        new LatLng(18.921110199615715,-103.53365999593139),
                        new LatLng(18.91997349502918,-103.53305918110979),
                        new LatLng(18.91851200634475,-103.53237253560454),
                        new LatLng(18.917212894564834,-103.53168589009928),
                        new LatLng(18.9162385541055,-103.53108507527773),
                        new LatLng(18.915020620546457,-103.53039842977248),
                        new LatLng(18.91412746363111,-103.52954012288193),
                        new LatLng(18.91388387455355,-103.52876764669301),
                        new LatLng(18.913153105191814,-103.52851015462403),
                        new LatLng(18.912584726814003,-103.52842432394037),
                        new LatLng(18.912178741075174,-103.52859598531668),
                        new LatLng(18.911772754350732,-103.52919680012928),
                        new LatLng(18.910879580088885,-103.52962595357457),
                        new LatLng(18.909580409009703,-103.53005510701983),
                        new LatLng(18.908443626037453,-103.53039842977248),
                        new LatLng(18.907306835340172,-103.53099924459404),
                        new LatLng(18.906738437093576,-103.53151422872298),
                        new LatLng(18.905845235948384,-103.53211504353558),
                        new LatLng(18.905764035607163,-103.53288751973349),
                        new LatLng(18.905682835227392,-103.53400331868401),
                        new LatLng(18.90633243716455,-103.53511911763454),
                        new LatLng(18.90665723718678,-103.53589159383245),
                        new LatLng(18.907794032299524,-103.53735071553558),
                        new LatLng(18.909012018489765,-103.5384665144861),
                        new LatLng(18.90933681330898,-103.54044062031821),
                        new LatLng(18.90933681330898,-103.541985572714),
                        new LatLng(18.90933681330898,-103.5433588637245),
                        new LatLng(18.908768421961245,-103.54533296956556),
                        new LatLng(18.90852482507794,-103.54722124470501),
                        new LatLng(18.90828122783989,-103.54859453572449),
                        new LatLng(18.90803763024708,-103.54996782674398),
                        new LatLng(18.907225635708407,-103.55116945637816),
                        new LatLng(18.906251237059823,-103.55262857808131),
                        new LatLng(18.906251237059823,-103.55383020772449),
                        new LatLng(18.906413637229026,-103.55546099080394),
                        new LatLng(18.906900836789802,-103.55752092732868),
                        new LatLng(18.906251237059823,-103.55837923421025),
                        new LatLng(18.90478962844528,-103.55872255696288),
                        new LatLng(18.90300320057579,-103.55880838765552),
                        new LatLng(18.902272383676973,-103.5590658797155),
                        new LatLng(18.901054348419144,-103.5590658797155),
                        new LatLng(18.89991750751238,-103.55880838765552),
                        new LatLng(18.898455843570446,-103.55880838765552),
                        new LatLng(18.89740018945041,-103.55898004903183),
                        new LatLng(18.895857298376054,-103.55992418659704),
                        new LatLng(18.895126450271544,-103.56095415486391),
                        new LatLng(18.893827156873087,-103.56292826069601),
                        new LatLng(18.893827156873087,-103.5643015517155),
                        new LatLng(18.893827156873087,-103.5658465041023),
                        new LatLng(18.893339919248984,-103.56687647236916),
                        new LatLng(18.892771473562185,-103.56790644062704),
                        new LatLng(18.892284232865627,-103.56833559407231),
                        new LatLng(18.891390954572124,-103.56850725544862),
                        new LatLng(18.890010424199048,-103.56876474750861),
                        new LatLng(18.88798021179769,-103.56902223957756),
                        new LatLng(18.88659965330495,-103.56902223957756),
                        new LatLng(18.885787554758153,-103.5689364088939),
                        new LatLng(18.884569399554277,-103.56876474750861),
                        new LatLng(18.88375729116251,-103.56910807027022),
                        new LatLng(18.882863967385063,-103.5697088850828),
                        new LatLng(18.882133062568617,-103.57048136128071),
                        new LatLng(18.880914880786932,-103.5718546523002),
                        new LatLng(18.880102754678852,-103.57254129780546),
                        new LatLng(18.87807242218523,-103.57228380573649),
                        new LatLng(18.876448138475954,-103.57211214436018),
                        new LatLng(18.873199523825335,-103.57219797505282),
                        new LatLng(18.871737626688805,-103.57262712848912),
                        new LatLng(18.87149397592641,-103.57374292743964),
                        new LatLng(18.87149397592641,-103.57382875813228),
                        new LatLng(18.87300800190553,-103.57229198043252))
                .strokeColor(Color.argb(255,51,122,183))
                .fillColor(Color.argb(70,238,238,238));

        Polygon polygon = mMap.addPolygon(rectOptions);
    }



}
