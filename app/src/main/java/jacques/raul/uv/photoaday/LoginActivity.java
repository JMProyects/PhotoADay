package jacques.raul.uv.photoaday;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    EditText username;
    EditText password;
    private FirebaseAuth auth;
    AlertDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Obtención de FirebaseAnalytics.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Drawable visibilityOff = getResources().getDrawable(R.drawable.ic_visibility_off);
        Drawable visibilityOn = getResources().getDrawable(R.drawable.ic_visibility);
        Drawable lockIcon = getResources().getDrawable(R.drawable.ic_lock);
        ConstraintLayout linearLayout = findViewById(R.id.container);

        auth = FirebaseAuth.getInstance();
        TextView greetingTextView = findViewById(R.id.greetingTextView);
        TextView txtRegistrarse = findViewById(R.id.id_lbl_registrarse);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.btnLogin);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim);
        linearLayout.startAnimation(anim);

        //Obtener la hora actual
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Establece el texto de saludo según la hora del día
        if (hour >= 6 && hour < 13) {
            greetingTextView.setText("¡Buenos días!");
        } else if (hour >= 13 && hour < 20) {
            greetingTextView.setText("¡Buenas tardes!");
        } else {
            greetingTextView.setText("¡Buenas noches!");
        }

        String texto = "¿No tiene cuenta? Regístrese.";
        SpannableString spannableString = new SpannableString(texto);
        int inicio = texto.indexOf("Regístrese.");
        int fin = inicio + "Regístrese.".length();
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan, inicio, fin, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtRegistrarse.setText(spannableString);

        txtRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistrarUsuario.class);
            startActivity(intent);
        });

        password.setCompoundDrawablesWithIntrinsicBounds(lockIcon, null, visibilityOn, null);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        password.setOnTouchListener((view, motionEvent) -> {
            final int DRAWABLE_RIGHT = 2;
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (motionEvent.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (password.getTransformationMethod() instanceof PasswordTransformationMethod) {
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        password.setCompoundDrawablesWithIntrinsicBounds(lockIcon, null, visibilityOff, null);

                    } else {
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        password.setCompoundDrawablesWithIntrinsicBounds(lockIcon, null, visibilityOn, null);
                    }
                    return true;
                }
            }
            return false;
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        username.addTextChangedListener(afterTextChangedListener);
        password.addTextChangedListener(afterTextChangedListener);

        loginButton.setOnClickListener(v -> {
            String email = username.getText().toString();
            String contrasena = password.getText().toString();
            attemptLogin(email, contrasena);
        });
    }

    private void attemptLogin(String email, String password) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            showProgressDialog();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("usuarios").document(user.getEmail()).get().addOnSuccessListener(documentSnapshot -> {
                        // Aquí tienes el documento del usuario si la recuperación fue exitosa.
                        // Puedes procesarlo como quieras.
                        // Luego puedes ir a la actividad ListaFotos
                        Intent menu = new Intent(this, ListaFotos.class);
                        startActivity(menu);
                    }).addOnFailureListener(e -> {
                        // Aquí puedes manejar cualquier error al obtener el documento del usuario
                        Toast.makeText(LoginActivity.this, "Error al obtener los datos del usuario.", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Aquí puedes manejar los errores en la autenticación
                    Toast.makeText(LoginActivity.this, "Autenticación fallida: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginActivity.this, "El campo email no puede estar vacío", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "El campo contraseña no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        }
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