package jacques.raul.uv.photoaday;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrarUsuario extends AppCompatActivity {
    AlertDialog progressDialog;
    EditText id_inputnombre;
    EditText id_inputapellidos;
    EditText id_inputcorreo;
    EditText id_inputcontrasena;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        Button btnCancelar = findViewById(R.id.id_btn_volver_registro);

        id_inputnombre = findViewById(R.id.id_inputnombre);
        id_inputapellidos = findViewById(R.id.id_inputapellidos);
        id_inputcorreo = findViewById(R.id.id_inputcorreo);
        id_inputcontrasena = findViewById(R.id.id_inputcontrasena);

        btnCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void guardarDatosPersonales(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Convierte el correo electrónico a minúsculas
        String correoEnMinusculas = id_inputcorreo.getText().toString().toLowerCase();

        // Crea un nuevo mapa con los datos del usuario
        Map<String, Object> userData = new HashMap<String, Object>() {{
            put("nombre", id_inputnombre.getText().toString());
            put("apellidos", id_inputapellidos.getText().toString());
            put("contraseña", id_inputcontrasena.getText().toString());
            put("correo", correoEnMinusculas);
        }};

        db.collection("usuarios").document(correoEnMinusculas).set(userData).addOnSuccessListener(aVoid -> {
            // Continúa con la siguiente actividad o muestra un mensaje de éxito
            Toast.makeText(RegistrarUsuario.this, "¡Usuario creado correctamente!", Toast.LENGTH_SHORT).show();
            Intent principal = new Intent(RegistrarUsuario.this, ListaFotos.class);
            startActivity(principal);
            finish();

        }).addOnFailureListener(e -> {
            // Muestra un mensaje de error
            Toast.makeText(RegistrarUsuario.this, "Error al guardar datos personales: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Firestore", "Error al guardar el documento", e);
        });
    }


    private void registrarUsuario(String email, String password) {
        Log.d("Auth", "Iniciando registrarUsuario");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("Auth", "Usuario registrado exitosamente");
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // Cargar la imagen de perfil en Firebase Storage y guardar datos en Firestore
                    guardarDatosPersonales(user);
                    progressDialog.dismiss();
                } else {
                    Log.e("Auth", "El usuario es nulo después del registro");
                }
            } else {
                Log.e("Auth", "Error en el registro", task.getException());
                Toast.makeText(RegistrarUsuario.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar registro");
        builder.setMessage("Está a punto de darse de alta en el sistema. ¿Está seguro?");

        builder.setPositiveButton("Confirmar", (dialog, id) -> registrarUsuario(id_inputcorreo.getText().toString(), id_inputcontrasena.getText().toString()));

        builder.setNegativeButton("Atrás", (dialog, id) -> {
            // No es necesario realizar ninguna acción, simplemente se cierra el diálogo
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Obtener los botones del AlertDialog
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        // Establecer el estilo de fuente en negrita
        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
        positiveButton.setTypeface(boldTypeface);
        negativeButton.setTypeface(boldTypeface);

        // Establecer el color hexadecimal del texto en los botones
        int greenColor = Color.parseColor("#66BB00");
        int redColor = Color.parseColor("#FF0000");
        positiveButton.setTextColor(greenColor);
        negativeButton.setTextColor(redColor);
    }


    public void verificarCampos(View view) {
        EditText inputNombre = findViewById(R.id.id_inputnombre);
        EditText inputApellidos = findViewById(R.id.id_inputapellidos);
        EditText inputContrasena = findViewById(R.id.id_inputcontrasena);
        EditText inputCorreo = findViewById(R.id.id_inputcorreo);

        if (campoVacio(inputContrasena) || campoVacio(inputNombre) || campoVacio(inputApellidos) || campoVacio(inputCorreo)) {
            Toast.makeText(this, "Por favor, complete todos los campos antes de continuar", Toast.LENGTH_SHORT).show();
        } else if (!validarContrasenya(inputContrasena, 6)) {
            Toast.makeText(this, "La longitud mínima de la contraseña debe ser de 6 caracteres", Toast.LENGTH_SHORT).show();
        } else {
            // Muestra el diálogo de confirmación cuando todos los campos estén completados y validados
            mostrarDialogoConfirmacion();
        }
    }

    private boolean campoVacio(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private boolean validarContrasenya(EditText editText, int longitud) {
        return editText.getText().toString().trim().length() >= longitud;
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
