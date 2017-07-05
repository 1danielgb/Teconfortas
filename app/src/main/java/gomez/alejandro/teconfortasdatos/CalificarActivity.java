package gomez.alejandro.teconfortasdatos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CalificarActivity extends AppCompatActivity {
    TextView msj;
    EditText nom,comen;
    public static String  mensaje,seleccion,no,com="";
    String codigo,total,nombre,id;
    Button btnca, btnguarda;
    boolean bandera=false;
    String url_insertar="http://teconfortascolima.esy.es/webservice/servicios/insertarcomentario.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);

        btnca=(Button)findViewById(R.id.btncalifica);
        nom=(EditText)findViewById(R.id.txtnombre);
        comen=(EditText)findViewById(R.id.txtcomentario);
        btnguarda=(Button)findViewById(R.id.btnguardar);

        Bundle recibir = getIntent().getExtras();
        total = recibir.getString("total");
        codigo = recibir.getString("codigo");
        nombre = recibir.getString("nombre");
        id=recibir.getString("id");




        btnca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSingleListDialog();

            }
        });
        btnguarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtienedatos();
                if(!no.equals("")){
                    if (!com.equals("")){
                        if (bandera==true){

                             inserta(id,no,com,seleccion);



                        }else{
                            Toast.makeText(
                                    CalificarActivity.this,
                                    "Selecciona una calificacion",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                    else{
                        Toast.makeText(
                                CalificarActivity.this,
                                "Ingresa un comentario",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }else{
                    Toast.makeText(
                            CalificarActivity.this,
                            "Ingresa tu nombre",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    public AlertDialog createSingleListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final CharSequence[] items = new CharSequence[6];

        items[0] = "5";
        items[1] = "6";
        items[2] = "7";
        items[3] = "8";
        items[4] = "9";
        items[5] = "10";

        builder.setTitle("Calificación")
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seleccion=items[which].toString();
                        bandera=true;
                        Toast.makeText(
                                CalificarActivity.this,
                                "Seleccionaste: " + items[which],
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        return builder.show();

    }
    private void obtienedatos(){
        no=nom.getText().toString().trim();
        com=comen.getText().toString();

    }
    public void inserta(final String id,final String nombre, final String comentario, final String calificacion  ){
//prueba



        final RequestQueue requestQueue = Volley.newRequestQueue(CalificarActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_insertar,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject sitios=jsonArray.getJSONObject(i);
                                mensaje=sitios.getString("mensaje");
                            }
                            if(!mensaje.equals("")){
                                Toast.makeText(CalificarActivity.this,"Comentario guardado",Toast.LENGTH_SHORT).show();
                                nom.setText("");
                                comen.setText("");
                                seleccion="";
                                Intent i=new Intent(CalificarActivity.this,QRActivity.class);
                                startActivity(i);
                            }
                            else{
                                Toast.makeText(CalificarActivity.this,"Error, Intentalo de nuevo",Toast.LENGTH_SHORT).show();
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
                Intent i=new Intent(CalificarActivity.this,QRActivity.class);
                startActivity(i);
            }


        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("nombre",nombre);
                params.put("comentario",comentario);
                params.put("calificacion",calificacion);
                params.put("id",id);

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

