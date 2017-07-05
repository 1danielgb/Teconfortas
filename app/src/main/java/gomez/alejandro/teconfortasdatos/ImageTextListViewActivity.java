package gomez.alejandro.teconfortasdatos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ALEJANDRO on 13/10/2016.
 */
public class ImageTextListViewActivity extends Activity  {
    String id_sitio;

    String url_comentarios="http://teconfortascolima.esy.es/webservice/servicios/obtenercomentarios.php";

    ListView listView;
    List<RowItem> rowItems;





    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_view);
        rowItems = new ArrayList<RowItem>();


        Bundle recibir=getIntent().getExtras();

        id_sitio=recibir.getString("id");
       // Toast.makeText(getApplicationContext(),"id segunda p:"+id_sitio,Toast.LENGTH_LONG).show();
        getcomentarios(id_sitio);





        listView = (ListView) findViewById(R.id.lista);



    }

    public void getcomentarios(final String sitio){

        final RequestQueue requestQueue = Volley.newRequestQueue(ImageTextListViewActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_comentarios,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                String usuario=sitios.getString("usuario");
                                String comentario=sitios.getString("comentario");
                               // Toast.makeText(getApplicationContext(),usuario,Toast.LENGTH_LONG).show();
                                RowItem item = new RowItem(usuario,comentario);
                                rowItems.add(item);

                            }
                           // Toast.makeText(getApplicationContext(),"Se obtuvieron "+String.valueOf(rowItems.size())+" comentarios",Toast.LENGTH_LONG).show();
                          /*  for( int i = 0 ; i < rowItems.size() ; i++ ){
                                System.out.println( rowItems.get(i));
                            }*/
                            if(rowItems.size()>0){
                                CustomListViewAdapter adapter = new CustomListViewAdapter(ImageTextListViewActivity.this,
                                        R.layout.list_item, rowItems);
                                listView.setAdapter(adapter);
                            }
                            else{
                                RowItem item = new RowItem("0 comentarios","No existen comentarios acerca de este lugar");
                                rowItems.add(item);
                                CustomListViewAdapter adapter = new CustomListViewAdapter(ImageTextListViewActivity.this,
                                        R.layout.list_item, rowItems);
                                listView.setAdapter(adapter);
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
                params.put("id", sitio);
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

}