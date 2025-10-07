package com.example.lab5_starter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CityArrayAdapter extends ArrayAdapter<City> {
    private final ArrayList<City> cities;

    public CityArrayAdapter(@NonNull Context context, @NonNull ArrayList<City> cities) {
        super(context, 0, cities);
        this.cities = cities;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_city, parent, false);

        City city = cities.get(position);
        ((TextView) view.findViewById(R.id.textCityName)).setText(city.getCityName());
        ((TextView) view.findViewById(R.id.textCityProvince)).setText(city.getProvince());

        return view;
    }
}
