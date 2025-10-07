package com.example.lab5_starter;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CityDialogFragment.CityDialogListener {

    private ListView listView;
    private Button addButton;
    private final ArrayList<City> cityArrayList = new ArrayList<>();
    private CityArrayAdapter cityArrayAdapter;

    // Firestore
    private FirebaseFirestore db;
    private CollectionReference citiesRef;
    private ListenerRegistration citiesRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.buttonAddCity);
        listView = findViewById(R.id.listviewCities);

        // Adapter setup
        cityArrayAdapter = new CityArrayAdapter(this, cityArrayList);
        listView.setAdapter(cityArrayAdapter);

        // Firestore connection
        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        // Real-time updates
        citiesRegistration = citiesRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", "Listener error", error);
                return;
            }
            if (value == null) return;

            cityArrayList.clear();
            for (QueryDocumentSnapshot snapshot : value) {
                City c = snapshot.toObject(City.class);
                if (c.getCityName() != null) cityArrayList.add(c);
            }
            cityArrayAdapter.notifyDataSetChanged();
        });

        // Add City button
        addButton.setOnClickListener(v ->
                CityDialogFragment.newInstance(null)
                        .show(getSupportFragmentManager(), "Add City")
        );

        // Click to edit
        listView.setOnItemClickListener((parent, view, position, id) -> {
            City selected = cityArrayList.get(position);
            CityDialogFragment.newInstance(selected)
                    .show(getSupportFragmentManager(), "City Details");
        });

    }

    @Override
    public void addCity(City city) {
        if (city == null || city.getCityName() == null || city.getCityName().isEmpty()) return;
        citiesRef.document(city.getCityName()).set(city)
                .addOnFailureListener(e -> Log.e("Firestore", "addCity failed", e));
    }

    @Override
    public void updateCity(City original, String newName, String newProvince) {
        if (original == null) return;
        String oldName = original.getCityName();
        boolean nameChanged = oldName != null && !oldName.equals(newName);
        City updated = new City(newName, newProvince);

        if (nameChanged) {
            citiesRef.document(newName).set(updated)
                    .addOnSuccessListener(aVoid ->
                            citiesRef.document(oldName).delete()
                                    .addOnFailureListener(e -> Log.e("Firestore", "delete old failed", e)))
                    .addOnFailureListener(e -> Log.e("Firestore", "update create new failed", e));
        } else {
            citiesRef.document(oldName).set(updated)
                    .addOnFailureListener(e -> Log.e("Firestore", "update failed", e));
        }
    }

    @Override
    public void deleteCity(City city) {
        if (city == null || city.getCityName() == null) return;

        String name = city.getCityName();

        citiesRef.document(name)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Deleted city: " + name))
                .addOnFailureListener(e -> Log.e("Firestore", "deleteCity failed", e));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (citiesRegistration != null) citiesRegistration.remove();
    }
}
