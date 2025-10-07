package com.example.lab5_starter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CityDialogFragment extends DialogFragment {

    public interface CityDialogListener {
        void updateCity(City city, String name, String province);
        void addCity(City city);
        void deleteCity(City city);
    }

    private CityDialogListener listener;
    private City city;

    public static CityDialogFragment newInstance(@Nullable City city) {
        CityDialogFragment fragment = new CityDialogFragment();
        Bundle args = new Bundle();
        if (city != null) {
            args.putString("cityName", city.getCityName());
            args.putString("province", city.getProvince());
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CityDialogListener)
            listener = (CityDialogListener) context;
        else
            throw new IllegalStateException("Host must implement CityDialogListener");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_city_details, null);

        EditText editName = view.findViewById(R.id.edit_city_name);
        EditText editProvince = view.findViewById(R.id.edit_province);

        Bundle args = getArguments();
        String name = null, province = null;
        if (args != null) {
            name = args.getString("cityName");
            province = args.getString("province");
        }
        boolean isEdit = name != null;

        if (isEdit) {
            editName.setText(name);
            editProvince.setText(province);
            city = new City(name, province);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(isEdit ? "City Details" : "Add City")
                .setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(isEdit ? "Update" : "Add", (dialog, which) -> {
                    String newName = editName.getText().toString().trim();
                    String newProv = editProvince.getText().toString().trim();

                    if (isEdit)
                        listener.updateCity(city, newName, newProv);
                    else
                        listener.addCity(new City(newName, newProv));
                });

        if (isEdit) {
            builder.setNeutralButton("Delete", (dialog, which) -> {
                listener.deleteCity(city);
            });
        }

        return builder.create();
    }
}
