package com.example.dearcalendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsFragment extends Fragment {

    private EditText passwordField;
    private EditText passwordAgain;
    private EditText passwordHint;
    private TextView passwordError;
    private Button confirm;
    private Button cancel;
    private AlertDialog dialog;

    public SettingsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.settings, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTitle();

        handleSetPassword();
        handleChangePassword();
        handleResetPassword();

    }

    private void setupTitle()
    {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        String title = "Settings";
        toolbar.setTitle(title);
    }

    private void handleSetPassword() {
        Button button = getActivity().findViewById(R.id.settingsSetPw);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.databaseHandler.getSetPassword().equals(""))
                    setPasswordDialog("set");
            }
        });
    }

    private void handleChangePassword()
    {
        Button button = getActivity().findViewById(R.id.settingsChangePw);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.databaseHandler.getSetPassword().equals("") && !MainActivity.rawPassword.equals(""))
                    setPasswordDialog("change");
            }
        });
    }

    private void handleResetPassword() {
        Button button = getActivity().findViewById(R.id.settingsResetPw);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPasswordDialog();
            }
        });
    }

    private void setPasswordDialog(String task)
    {
        AlertDialog.Builder alertDialog  = new AlertDialog.Builder(getContext());
        View result = getLayoutInflater().inflate(R.layout.setpw_popup,null);
        alertDialog.setView(result);
        dialog = alertDialog.create();

        dialog.show();

        passwordField = dialog.findViewById(R.id.setPassword);
        passwordAgain = dialog.findViewById(R.id.setPasswordAgain);
        passwordHint = dialog.findViewById(R.id.setPasswordHint);
        passwordError = dialog.findViewById(R.id.setPasswordError);

        confirm = dialog.findViewById(R.id.setPasswordConfirm);
        cancel = dialog.findViewById(R.id.setPasswordCancel);

        handlePopupButtons(task);
    }

    private void handlePopupButtons(String task) {


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePasswordIfValid(task);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void updatePasswordIfValid(String task)
    {
        if(validPassword() && validHint())
        {
            DatabaseHandler databaseHandler = MainActivity.databaseHandler;

            String password = passwordField.getText().toString();
            if(task.equals("change"))
                databaseHandler.reEncryptEntries(MainActivity.rawPassword,password);

            MainActivity.rawPassword = password;

            EncryptionHandler encryptionHandler = new EncryptionHandler();
            String hashedPassword = encryptionHandler.hash(password);


            databaseHandler.createPassword(hashedPassword,passwordHint.getText().toString());
            if(task.equals("set"))
                databaseHandler.encryptEntries(password);

            dialog.cancel();
            MainActivity.displayLock();
        }
        else if(!validHint())
        {
            String error = "The password hint can't contain the password and it can't be empty.";
            passwordError.setText(error);
        }
        else
        {
            String error = "The password should have a length of at least 8.";
            passwordError.setText(error);
        }
    }

    private void resetPasswordDialog()
    {
        AlertDialog.Builder alertDialog  = new AlertDialog.Builder(getContext());
        View result = getLayoutInflater().inflate(R.layout.reset_confirmation,null);
        alertDialog.setView(result);
        dialog = alertDialog.create();

        dialog.show();

        confirm = dialog.findViewById(R.id.resetConfirm);
        cancel = dialog.findViewById(R.id.resetCancel);

        handleConfirmButtons();
    }

    private boolean validPw(String pw)
    {
        //Any String of minimum 8 letters
        String PASSWORD_PATTERN = "^.*(?=.{8,})(?=.*[a-zA-z0-9#?!@$%^&*-]).*$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(pw);
        return matcher.matches();
    }

    private boolean validPassword() {
        String password = passwordField.getText().toString();
        String passwordAgain = this.passwordAgain.getText().toString();

        return validPw(password) && password.equals(passwordAgain);
    }

    private boolean validHint()
    {
        String hint = passwordHint.getText().toString();
        return !hint.contains(passwordField.getText().toString()) && !hint.equals("") && hint.length() <= 32;
    }

    private void handleConfirmButtons() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void resetPassword()
    {
        DatabaseHandler databaseHandler = MainActivity.databaseHandler;
        databaseHandler.editPassword("");
        databaseHandler.deleteAllEntries();
        databaseHandler.removeImages();
        dialog.cancel();
        MainActivity.eventsLocked=false;
        MainActivity.displayLock();
    }


}
