package jacques.raul.uv.photoaday;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListaFotos extends AppCompatActivity implements ListaFotosInterface {
    AlertDialog progressDialog;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
    private FusedLocationProviderClient fusedLocationClient;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    AlarmManager alarmManager;
    private FirebaseAuth auth;
    private Uri photoUri;
    private ListaFotosAdapter listaFotosAdapter;
    FotoModel currentFoto;
    RecyclerView recyclerView;
    TextView noResultsTextView;
    ArrayList<FotoModel> listaFotos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_fotos);
        auth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerview_fotos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        noResultsTextView = findViewById(R.id.no_results);

        // Configura el Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Photo A Day");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, toolbar.getMenu());

        //SETTEAR LOS DATOS
        listaFotos = new ArrayList<>();
        listaFotosAdapter = new ListaFotosAdapter(this, getApplicationContext(), listaFotos);
        recyclerView.setAdapter(listaFotosAdapter);
        FloatingActionButton fabCamara = findViewById(R.id.id_btn_camara);

        fabCamara.setOnClickListener(view -> {
            if (allPermissionsGranted()) {
                getLastLocation();
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("canal_notificaciones", "Mi Canal", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        //Programar alarma
        programarAlarmaDiaria();

        loadImagesFromFirebase();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fecha_menu_item:
                sortReservationsByDate();
                return true;

            case R.id.ubicacion_menu_item:
                sortReservationsByUbicacion();
                return true;

            case R.id.cerrar_sesion:
                new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Cerrar sesión").setMessage("¿Estás seguro que quieres cerrar la sesión?").setPositiveButton("Sí", (dialog, which) -> {
                    // Aquí puedes manejar el cierre de sesión. Por ejemplo,
                    // puedes navegar de regreso a la actividad de inicio de sesión:
                    // Eliminar la clave de sesión del usuario
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(ListaFotos.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                }).setNegativeButton("No", null).show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortReservationsByDate() {
        if (listaFotosAdapter != null) {
            listaFotosAdapter.sortBy(Comparator.comparing(FotoModel::getFechaFoto).reversed());
            listaFotosAdapter.notifyDataSetChanged();
        }
    }

    private void sortReservationsByUbicacion() {
        if (listaFotosAdapter != null) {
            listaFotosAdapter.sortBy(Comparator.comparing(FotoModel::getUbicacion));
            listaFotosAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Cambiar el color del ícono de mapa a negro
        MenuItem mapItem = menu.findItem(R.id.cerrar_sesion);
        if (mapItem != null) {
            Drawable mapIcon = mapItem.getIcon();
            if (mapIcon != null) {
                mapIcon = DrawableCompat.wrap(mapIcon);
                mapItem.setIcon(mapIcon);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void openCamera(FotoModel newFoto) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Crear el archivo de la foto
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error creando el archivo de la foto: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Continue only if the File was successfully created
            Uri photoURI = FileProvider.getUriForFile(this, "jacques.raul.uv.photoaday.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        photoUri = Uri.fromFile(image);
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Subir a la base de datos la foto
            uploadPhotoToFirebase(photoUri);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Permisos no concedidos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadImagesFromFirebase() {
        FirebaseUser usuario = auth.getCurrentUser();
        if (usuario != null) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            // Aquí cambiamos la consulta para buscar en la colección de fotos
            Query query = database.collection("fotos").whereEqualTo("email", usuario.getEmail());
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    listaFotos.clear();  // Limpiamos la lista antes de agregar los nuevos elementos
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        FotoModel fotoModel = document.toObject(FotoModel.class);
                        listaFotos.add(fotoModel);
                    }
                    if (listaFotos.size() > 0) {
                        // Hay fotos cargadas, mostrar el RecyclerView
                        recyclerView.setVisibility(View.VISIBLE);
                        noResultsTextView.setVisibility(View.GONE);
                        listaFotosAdapter.updateData(listaFotos);
                    } else {
                        // No hay fotos cargadas, mostrar el TextView
                        recyclerView.setVisibility(View.GONE);
                        noResultsTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
        }
    }

    private void uploadPhotoToFirebase(Uri photoFile) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String timeStamp = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES")).format(new Date());

        // Aquí cambiamos la referencia para apuntar al directorio de fotos del usuario específico
        FirebaseUser usuario = auth.getCurrentUser();
        if (usuario != null) {
            showProgressDialog();
            // Codificar el correo electrónico para hacerlo válido para la ruta de almacenamiento de Firebase
            String encodedEmail = usuario.getEmail().replace("@", "_at_").replace(".", "_dot_");

            StorageReference storageRef = storage.getReference().child("fotos/" + encodedEmail + "/" + timeStamp + ".jpg");
            UploadTask uploadTask = storageRef.putFile(photoUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(this, "Foto subida correctamente.", Toast.LENGTH_SHORT).show();

                // Get the download URL
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    // Creamos la clase con la URL de descarga
                    String fechaFormateada = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES")).format(new Date());
                    currentFoto.setFechaFoto(fechaFormateada);
                    currentFoto.setFoto(downloadUri.toString());
                    currentFoto.setEmail(usuario.getEmail());

                    // Aquí es donde realmente guardamos los datos en Firestore
                    database.collection("fotos").add(currentFoto).addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        listaFotos.add(currentFoto);
                        listaFotosAdapter.notifyDataSetChanged();

                        // Verificar si ahora la lista tiene elementos y mostrar el RecyclerView si es necesario
                        if (listaFotos.size() > 0) {
                            // Hay fotos cargadas, mostrar el RecyclerView
                            recyclerView.setVisibility(View.VISIBLE);
                            noResultsTextView.setVisibility(View.GONE);
                        }
                        progressDialog.dismiss();
                    }).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                });
            });

        }
    }

    public String getCityName(double latitude, double longitude) {
        String cityName = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                cityName = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }


    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String cityName = getCityName(latitude, longitude);

                // Necesitas obtener el email del usuario actual de Firebase
                FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
                if (usuario != null) {
                    String emailUsuario = usuario.getEmail();
                    currentFoto = new FotoModel(emailUsuario, cityName, "", "");
                    openCamera(currentFoto);
                }
            }
        });
    }
    private void programarAlarmaDiaria() {

        if(alarmManager == null) {
            // Obtén una referencia al sistema de servicios de alarma
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            // Crea un intento para el BroadcastReceiver
            Intent intent = new Intent(this, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, PendingIntent.FLAG_IMMUTABLE);

            // Configura la hora de la alarma (9 AM)
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    public void onItemClick(int position) {
        // Aquí pasas los datos de la foto seleccionada a la nueva actividad
        Intent intent = new Intent(this, ListaFotosDetalles.class);

        intent.putExtra("fechaFoto", listaFotos.get(position).getFechaFoto());
        intent.putExtra("ubicacion", listaFotos.get(position).getUbicacion());
        intent.putExtra("foto", listaFotos.get(position).getFoto());

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Cerrar sesión").setMessage("¿Estás seguro que quieres cerrar la sesión?").setPositiveButton("Sí", (dialog, which) -> {
            // Aquí puedes manejar el cierre de sesión. Por ejemplo,
            // puedes navegar de regreso a la actividad de inicio de sesión:
            // Eliminar la clave de sesión del usuario
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(ListaFotos.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        }).setNegativeButton("No", null).show();
    }

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.progress_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();
    }
}
