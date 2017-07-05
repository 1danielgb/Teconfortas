package gomez.alejandro.teconfortasdatos;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QRActivity extends AppCompatActivity  implements View.OnClickListener  {
    String server_verifica="http://teconfortascolima.esy.es/webservice/servicios/verifica.php";
    private Button scanBtn;
    private ImageButton ib;
    private TextView formatTxt, contentTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        //Se Instancia el botón de Scan
        // scanBtn = (Button)findViewById(R.id.scan_button);
        //Se Instancia el Campo de Texto para el nombre del formato de código de barra
        // formatTxt = (TextView)findViewById(R.id.scan_format);
        //Se Instancia el Campo de Texto para el contenido  del código de barra
        // contentTxt = (TextView)findViewById(R.id.scan_content);
        //Se agrega la clase MainActivity.java como Listener del evento click del botón de Scan
        // scanBtn.setOnClickListener(this);
        ib=(ImageButton)findViewById(R.id.ibleerqr);
        ib.setOnClickListener(this);

    }
    //holaaaaa
    @Override
    public void onClick(View view) {
        //Se responde al evento click
        if(view.getId()==R.id.ibleerqr){
            //Se instancia un objeto de la clase IntentIntegrator
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            //Se procede con el proceso de scaneo
            scanIntegrator.initiateScan();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //Se obtiene el resultado del proceso de scaneo y se parsea
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //Quiere decir que se obtuvo resultado pro lo tanto:
            //Desplegamos en pantalla el contenido del código de barra scaneado
            String scanContent = scanningResult.getContents();
            //contentTxt.setText("Contenido: " + scanContent);
            //Desplegamos en pantalla el nombre del formato del código de barra scaneado
            // String scanFormat = scanningResult.getFormatName();
            // formatTxt.setText("Formato: " + scanFormat);
            Toast toast = Toast.makeText(getApplicationContext(),
                    scanContent, Toast.LENGTH_SHORT);
            toast.show();
            if(scanContent!=null){



                 verifica(scanContent);


            }


        }else{
            //Quiere decir que NO se obtuvo resultado
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No se ha recibido datos del scaneo!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void verifica(final String codigo) {
//prueba


        final RequestQueue requestQueue = Volley.newRequestQueue(QRActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_verifica,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject sitios = jsonArray.getJSONObject(i);
                                String total = sitios.getString("total");
                                String codigo = sitios.getString("codigo");
                                String nombre = sitios.getString("nombre");
                                String id=sitios.getString("id");
                                Toast.makeText(getApplicationContext(), codigo, Toast.LENGTH_LONG).show();


                                if(total.equals("1")){
                                    Intent nextScreen = new Intent(QRActivity.this, CalificarActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("total", total);
                                    bundle.putString("codigo", codigo);
                                    bundle.putString("nombre", nombre);
                                    bundle.putString("id",id);
                                    nextScreen.putExtras(bundle);
                                    startActivityForResult(nextScreen, 0);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Codigo QR invalido", Toast.LENGTH_LONG).show();
                                }



                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        requestQueue.stop();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                error.printStackTrace();
                requestQueue.stop();
            }


        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("codigo", codigo);


                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}