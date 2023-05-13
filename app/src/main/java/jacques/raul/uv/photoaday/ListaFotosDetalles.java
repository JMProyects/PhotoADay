package jacques.raul.uv.photoaday;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class ListaFotosDetalles extends AppCompatActivity {

    TextView fechaTextView, ubicacionTextView;
    ImageView imageView;
    RelativeLayout relativeLayout;
    ImageButton imageButton;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_fotos);

        fechaTextView = findViewById(R.id.fechaTextView2);
        ubicacionTextView = findViewById(R.id.ubicacionTextView2);
        imageView = findViewById(R.id.imageView);
        relativeLayout = findViewById(R.id.relativeLayout);
        imageButton = findViewById(R.id.imageButton);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        fechaTextView.startAnimation(fadeIn);
        ubicacionTextView.startAnimation(fadeIn);
        imageView.startAnimation(fadeIn);
        relativeLayout.startAnimation(fadeIn);
        imageButton.startAnimation(fadeIn);

        // Aquí obtienes los datos que pasaste a través del Intent
        Intent intent = getIntent();
        String fechaFoto = intent.getStringExtra("fechaFoto");
        String ubicacion = intent.getStringExtra("ubicacion");
        String foto = intent.getStringExtra("foto");
        System.out.println(foto);
        // Aquí asignas los datos a las vistas
        fechaTextView.setText(fechaFoto);
        ubicacionTextView.setText(ubicacion);

        // Para cargar la imagen, necesitas usar Glide o una librería similar
        Glide.with(this).load(foto).into(imageView);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permiso no concedido, solicitarlo al usuario
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        imageButton.setOnClickListener(v -> {
            new Thread(() -> {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "nombre-imagen.jpg"); // esto asignará el nombre al archivo
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // esto asignará el tipo de MIME
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Download/" + "CustomFolder");
                values.put(MediaStore.Images.Media.IS_PENDING, true);

                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                if (uri != null) {
                    try {
                        OutputStream out = getContentResolver().openOutputStream(uri);
                        if (out != null) {
                            InputStream inputStream = new URL(foto).openStream();
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                            }
                            out.close();
                            inputStream.close();
                        }
                        values.put(MediaStore.Images.Media.IS_PENDING, false);
                        getContentResolver().update(uri, values, null, null);
                    } catch (IOException e) {
                        getContentResolver().delete(uri, null, null);
                    }
                }
            }).start();
            Toast.makeText(this, "Descargando imagen...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Si la solicitud es cancelada, el resultado de los arrays estará vacío.
        // Permiso concedido, puedes proceder con la descarga de la imagen
        // Permiso denegado, desactivar la funcionalidad que depende de este permiso.
    }
}
