package tamara.valenzuela.verduritassa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListaCultivoActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ImageView agregarCultivo;
    private ListView lista;
    private TextView mensaje;
    FirebaseUser currentUser;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_lista_cultivos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Obtener una instancia de Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String email = currentUser.getEmail();
        lista = findViewById(R.id.lista);
        mensaje = findViewById(R.id.mensaje);
        agregarCultivo = findViewById(R.id.agregarCultivo);

        db.collection("cultivos")
                .whereEqualTo("correo", email)  // Filtrar los cultivos por el correo
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Cultivo> listaCultivos = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();  // Obtener el ID del documento
                            String nombre = document.getString("alias");
                            String fechaCosecha = document.getString("fechaCosecha");

                            Cultivo cultivo = new Cultivo(id, nombre, fechaCosecha);
                            listaCultivos.add(cultivo);
                        }

                        AdapterCultivo listaDatos = new AdapterCultivo(ListaCultivoActivity.this, listaCultivos);
                        lista.setAdapter(listaDatos);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al leer los datos", Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("usuarios")
                .whereEqualTo("correo", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String usuario = document.getString("nombre");
                            mensaje.setText("bienvenido, " + usuario);
                        }
                    }
                });
        //boton para agregar cultivo nuevo
        agregarCultivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirige a la actividad de registro
                Intent registro = new Intent(ListaCultivoActivity.this, AgregarCultivoActivity.class);
                startActivity(registro);
            }
        });

    }
}
