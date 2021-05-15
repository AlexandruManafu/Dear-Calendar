package com.example.dearcalendar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class HelpFragment extends Fragment {

    private String link;

    public HelpFragment() {
        link = "https://github.com/AlexandruManafu/Dear-Calendar/tree/main/documentation";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.help, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTitle();

        listenGithubButton();
    }

    private void setupTitle()
    {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        String title = "About and Help";

        toolbar.setTitle(title);
    }

    private void listenGithubButton()
    {
        Button button = getActivity().findViewById(R.id.helpLink);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browserActivity();
            }
        });
    }

    private void browserActivity()
    {
        Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        getActivity().startActivity(intent);
    }
}
