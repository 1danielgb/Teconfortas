package gomez.alejandro.teconfortasdatos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class InformacionActivity extends AppCompatActivity{
    private NetworkImageView mNetworkImageView;
    private ImageLoader mImageLoader;
    private Button btn,btncal;
    private TextView nomb,descr,dire,hor,cont;
    String nombre,url,descripcion,lat,lon,direccion,horario,contacto,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);
        mNetworkImageView=(NetworkImageView)findViewById(R.id.imgsitio);
        btn=(Button)findViewById(R.id.btncoment);
        btncal=(Button)findViewById(R.id.btncalifica);
        nomb=(TextView)findViewById(R.id.tv_nombre);
        descr=(TextView)findViewById(R.id.tv_desc);
        dire=(TextView)findViewById(R.id.tv_direccion);
        hor=(TextView)findViewById(R.id.tv_horario);
        cont=(TextView)findViewById(R.id.tv_contacto);


        Bundle recibir=getIntent().getExtras();
        nombre=recibir.getString("nombre");
        url=recibir.getString("imagen");
        descripcion=recibir.getString("descripcion");
        lat=recibir.getString("latitud");
        lon=recibir.getString("longitud");
        direccion=recibir.getString("direccion");
        horario=recibir.getString("horario");
        contacto=recibir.getString("contacto");
        id=recibir.getString("id");


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(InformacionActivity.this,"hola",Toast.LENGTH_LONG).show();
                // Intent i=new Intent(InformacionActivity.this,ImageTextListViewActivity.class);
                // InformacionActivity.this.startActivity(i);

                Intent nextScreen = new Intent(InformacionActivity.this,ImageTextListViewActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("id",id);
                nextScreen.putExtras(bundle);
                InformacionActivity.this.startActivityForResult(nextScreen, 0);

            }
        });
        btncal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(InformacionActivity.this,QRActivity.class);
                InformacionActivity.this.startActivity(i);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        // Instantiate the RequestQueue.
        mImageLoader = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                .getImageLoader();
        //Image URL - This can point to any image file supported by Android

        mImageLoader.get(url, ImageLoader.getImageListener(mNetworkImageView,
                R.mipmap.ic_launcher, android.R.drawable
                        .ic_dialog_alert));
        mNetworkImageView.setImageUrl(url, mImageLoader);

        nomb.setText(nombre);
        descr.setText(descripcion);
        dire.setText(direccion);
        cont.setText(contacto);
        hor.setText(horario);
       // Toast.makeText(getApplicationContext(),"id:"+id,Toast.LENGTH_LONG).show();

    }




}
