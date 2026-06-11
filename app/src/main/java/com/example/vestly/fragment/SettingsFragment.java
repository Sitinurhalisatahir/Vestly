package com.example.vestly.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import com.example.vestly.R;
import com.example.vestly.helper.SharedPrefManager;
import com.example.vestly.helper.ThemeManager;

public class SettingsFragment extends Fragment {

    private SwitchCompat switchTheme;
    private TextView tvDarkModeLabel;
    private SharedPrefManager pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchTheme = view.findViewById(R.id.switch_theme);
        tvDarkModeLabel = view.findViewById(R.id.tv_dark_mode_label);
        pref = SharedPrefManager.getInstance(requireContext());

        switchTheme.setChecked(pref.isDarkTheme());

        if (pref.isDarkTheme()) {
            tvDarkModeLabel.setText("Light Mode");
        } else {
            tvDarkModeLabel.setText("Dark Mode");
        }

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            pref.setDarkTheme(isChecked);
            ThemeManager.applyTheme(isChecked);

            if (isChecked) {
                tvDarkModeLabel.setText("Light Mode");
            } else {
                tvDarkModeLabel.setText("Dark Mode");
            }

            requireActivity().recreate();
        });
    }
}