package tamara.valenzuela.verduritassa;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AgregarCultivoActivity extends AppCompatActivity {

    private Spinner vegetableSpinner;
    private EditText selectDate;
    private Button registerButton;
    private EditText aliasPlanta;
    private ArrayList<String> cultivoList;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser currentUser;


    private int selectedYear, selectedMonth, selectedDay;
    private Map<String, Integer> harvestDaysMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cultivo);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Vistas
        vegetableSpinner = findViewById(R.id.spinnerVerduras);
        registerButton = findViewById(R.id.buttonGuardar);
        selectDate = findViewById(R.id.fechaSiembra);
        aliasPlanta = findViewById(R.id.alias);


        // Lista verduritas
        harvestDaysMap = new HashMap<>();
        harvestDaysMap.put("Tomate", 80);
        harvestDaysMap.put("Cebolla", 120);
        harvestDaysMap.put("Lechuga", 85);
        harvestDaysMap.put("Apio", 150);
        harvestDaysMap.put("Choclo", 90);


        // Spinner de verduras
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.Verduras, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vegetableSpinner.setAdapter(spinnerAdapter);

        // Botón para seleccionar la fecha
        selectDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AgregarCultivoActivity.this,
                    (view, yearSelected, monthSelected, daySelected) -> {
                        selectedYear = yearSelected;
                        selectedMonth = monthSelected;
                        selectedDay = daySelected;

                        // Mostrar la fecha seleccionada en el botón
                        String dateStr = daySelected + "/" + (monthSelected + 1) + "/" + yearSelected;
                        selectDate.setText(dateStr);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Botón para registrar el cultivo
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedVegetable = vegetableSpinner.getSelectedItem().toString();

                if (!harvestDaysMap.containsKey(selectedVegetable)) {
                    Toast.makeText(AgregarCultivoActivity.this, "Por favor, selecciona una verdura válida", Toast.LENGTH_LONG).show();
                    return;
                }

                //Calendario con la fecha seleccionada
                Calendar plantingDate = Calendar.getInstance();
                plantingDate.set(selectedYear, selectedMonth, selectedDay);

                int daysToHarvest = harvestDaysMap.get(selectedVegetable);
                Calendar harvestDate = Calendar.getInstance();
                harvestDate.setTime(plantingDate.getTime());
                harvestDate.add(Calendar.DAY_OF_MONTH, daysToHarvest);

                String harvestDateStr = String.format("%02d/%02d/%d",
                        harvestDate.get(Calendar.DAY_OF_MONTH),
                        harvestDate.get(Calendar.MONTH) + 1,
                        harvestDate.get(Calendar.YEAR));

                //logica de guardar el firebase
                registrarCultivo(harvestDateStr);
                Intent registrar = new Intent(AgregarCultivoActivity.this, ListaCultivoActivity.class);
                Toast.makeText(AgregarCultivoActivity.this, "Cultivo registrado exitosamente", Toast.LENGTH_LONG).show();
                startActivity(registrar);
            }
        });

    }

    private void registrarCultivo(String fechaCosecha) {

            String alias = aliasPlanta.getText().toString();
            String fechaSiembra = selectDate.getText().toString();
            String email = currentUser.getEmail();
            String planta = vegetableSpinner.getSelectedItem().toString();

            // Crear un objeto para almacenar datos
            Map<String, Object> datosPlanta = new HashMap<>();
            datosPlanta.put("alias", alias);
            datosPlanta.put("fechaSiembra", fechaSiembra);
            datosPlanta.put("fechaCosecha", fechaCosecha);
            datosPlanta.put("planta", planta);
            datosPlanta.put("correo", email);


            // Guardar los datos en Firestore
            db.collection("cultivos")
                    .add(datosPlanta)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "Guardado con éxito", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                    });

    }

}