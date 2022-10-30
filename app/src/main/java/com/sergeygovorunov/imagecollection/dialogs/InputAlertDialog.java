package com.sergeygovorunov.imagecollection.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sergeygovorunov.imagecollection.R;

public class InputAlertDialog extends AlertDialog {

    //private Context context;
    private EditText input;
    private TextView error;
    private InputAlertDialogActions iada;

    public InputAlertDialog(Context context) {
        super(context);
        //this.context = context;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.input_alert_dialog, null);
        setView(view);
        input = view.findViewById(R.id.input_alert_dialog_input);
        error = view.findViewById(R.id.input_alert_dialog_error);
        error.setVisibility(View.GONE);
        setButton(DialogInterface.BUTTON_POSITIVE, "Ок", (dialogInterface, id) -> {
        });
        setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена", (dialogInterface, id) -> {
        });
        super.onCreate(savedInstanceState);
        if (iada != null) {
            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
                String inputText = input.getText().toString().trim();
                String validatedText = iada.OnValidation(inputText);
                if (validatedText != null && !"".equals(validatedText)) {
                    error.setVisibility(View.VISIBLE);
                    error.setText(validatedText);
                } else {
                    iada.OnSuccess(inputText);
                    dismiss();
                }
            });
        }
    }

    public void setInputAlertDialogActions(InputAlertDialogActions iada) {
        this.iada = iada;
    }

    public interface InputAlertDialogActions {
        String OnValidation(String text);

        void OnSuccess(String text);
    }
}