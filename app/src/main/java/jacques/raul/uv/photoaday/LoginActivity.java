package jacques.raul.uv.photoaday;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    EditText username;
    EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView greetingTextView = findViewById(R.id.greetingTextView);
        TextView txtRegistrarse = findViewById(R.id.id_lbl_registrarse);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.btnLogin);

        //Obtener la hora actual
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Establece el texto de saludo según la hora del día
        if (hour >= 6 && hour < 13) {
            greetingTextView.setText("¡Buenos días!");
        } else if (hour >= 13 && hour < 19) {
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
            attemptLogin();

        });

    }

    private void checkFieldsAndUpdateButton() {
        boolean isEmailEmpty = TextUtils.isEmpty(username.getText().toString().trim());
        boolean isPasswordEmpty = TextUtils.isEmpty(password.getText().toString().trim());

        if (isEmailEmpty || isPasswordEmpty) {
            loginButton.setEnabled(false);
            loginButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_disabled));
        } else {
            loginButton.setEnabled(true);
            loginButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_enabled));
        }
    }

    private void attemptLogin() {
        boolean isEmailEmpty = TextUtils.isEmpty(username.getText().toString().trim());
        boolean isPasswordEmpty = TextUtils.isEmpty(password.getText().toString().trim());

        if (!isEmailEmpty && !isPasswordEmpty) {
            Intent menu = new Intent(this, ListaFotos.class);
            startActivity(menu);
        } else {
            if (isEmailEmpty) {
                Toast.makeText(LoginActivity.this, "El campo email no puede estar vacío", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "El campo contraseña no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        }
    }

}