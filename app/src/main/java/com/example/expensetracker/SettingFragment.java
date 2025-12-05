package com.example.expensetracker;

import android.content.Context;
import android.content.SharedPreferences;import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class SettingFragment extends Fragment {

    // UI Components
    private RadioGroup radioGroupTheme, radioGroupLang;
    private RadioButton rbLight, rbDark, rbEnglish, rbKhmer;
    private SharedPreferences sharedPreferences;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // 1. Initialize UI Views (Bind Java variables to XML IDs)
        radioGroupTheme = view.findViewById(R.id.radioGroupTheme);
        radioGroupLang = view.findViewById(R.id.radioGroupLang);
        rbLight = view.findViewById(R.id.rbLight);
        rbDark = view.findViewById(R.id.rbDark);
        rbEnglish = view.findViewById(R.id.rbEnglish);
        rbKhmer = view.findViewById(R.id.rbKhmer);

        // 2. Initialize SharedPreferences (File name: "AppSettings")
        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);

        // 3. Load previously saved settings to update UI state
        loadPreferences();

        // 4. Set Listener for Theme Changes
        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (checkedId == R.id.rbLight) {
                editor.putBoolean("isDarkMode", false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.rbDark) {
                editor.putBoolean("isDarkMode", true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            editor.apply(); // Save changes
        });

        // 5. Set Listener for Language Changes
        radioGroupLang.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedLang = "en"; // Default to English

            if (checkedId == R.id.rbKhmer) {
                selectedLang = "km";
            }

            // Save language to storage
            sharedPreferences.edit().putString("My_Lang", selectedLang).apply();

            // Apply new language and restart the activity to refresh text
            setLocale(selectedLang);
            requireActivity().recreate();
        });

        return view;
    }

    // Helper method to check checkboxes based on saved data
    private void loadPreferences() {
        // Check Saved Theme
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        if (isDarkMode) {
            rbDark.setChecked(true);
        } else {
            rbLight.setChecked(true);
        }

        // Check Saved Language
        String lang = sharedPreferences.getString("My_Lang", "en");
        if (lang.equals("km")) {
            rbKhmer.setChecked(true);
        } else {
            rbEnglish.setChecked(true);
        }
    }

    // Helper method to force the app to change language
    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);

        // Update configuration
        requireActivity().getResources().updateConfiguration(
                config,
                requireActivity().getResources().getDisplayMetrics()
        );
    }
}
