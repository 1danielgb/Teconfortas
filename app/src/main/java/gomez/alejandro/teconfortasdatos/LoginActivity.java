package gomez.alejandro.teconfortasdatos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
Button btn;
    String ser="http://teconfortascolima.esy.es/webservice/servicios/login.php";
    EditText usuario,password;
    String us,ps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn = (Button) findViewById(R.id.btnlogin);
        usuario = (EditText) findViewById(R.id.txtuser);
        password = (EditText) findViewById(R.id.txtpass);
        SharedPreferences prefe=getSharedPreferences("datos",Context.MODE_PRIVATE);
            usuario.setText(prefe.getString("mail",""));
            password.setText(prefe.getString("pass",""));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                us=usuario.getText().toString().trim();
                ps=password.getText().toString();

                if(us.equals("memo")&&(ps.equals("123"))){
                    SharedPreferences preferencias=getSharedPreferences("datos", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferencias.edit();
                    editor.putString("mail", "memo");
                    editor.putString("pass", "123");
                    editor.commit();
                    Intent intent=new Intent(LoginActivity.this,MapsActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginActivity.this, "Usuario incorrecto", Toast.LENGTH_SHORT).show();
                }
                */
                verifica();
            }

        });

    }
    public void  verifica(){


        final RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ser,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                           if(jsonArray.length()>0){
                               SharedPreferences preferencias=getSharedPreferences("datos", Context.MODE_PRIVATE);
                               SharedPreferences.Editor editor=preferencias.edit();
                               editor.putString("mail", usuario.getText().toString());
                               editor.putString("pass", password.getText().toString());
                               editor.commit();
                               Intent intent=new Intent(LoginActivity.this,MapsActivity.class);
                               startActivity(intent);
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

                params.put("correo",usuario.getText().toString());
                params.put("password",password.getText().toString());





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
