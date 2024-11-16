package tamara.valenzuela.verduritassa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextNombre, editTextPais, editTextGenero, editTextContrasena;
    private Button buttonRegistrarse;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar los campos de entrada
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextPais = findViewById(R.id.editTextPais);
        editTextGenero = findViewById(R.id.editTextGenero);
        editTextContrasena = findViewById(R.id.editTextContrasena);
        buttonRegistrarse = findViewById(R.id.buttonRegistrarse);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //botón de registro
        buttonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String contrasena = editTextContrasena.getText().toString();
                registrar(email, contrasena);
            }

        });
    }

    private void registrar(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Sign in success, update UI with the signed-in user's information

                    String nombre = editTextNombre.getText().toString();
                    String pais = editTextPais.getText().toString();
                    String genero = editTextGenero.getText().toString();

                    // Crear un objeto para almacenar datos
                    Map<String, Object> usuario = new HashMap<>();
                    usuario.put("correo", email);
                    usuario.put("nombre", nombre);
                    usuario.put("pais", pais);
                    usuario.put("genero", genero);

                    // Guardar los datos en Firestore
                    db.collection("usuarios")
                            .add(usuario)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(getApplicationContext(), "Usuario guardado con éxito", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                            });
                    Intent ListaCultivos = new Intent(RegistroActivity.this, ListaCultivoActivity.class);
                    startActivity(ListaCultivos);
                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(RegistroActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}