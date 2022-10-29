package com.sergeygovorunov.imagecollection.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sergeygovorunov.imagecollection.R;

public class InputAlertDialog extends AlertDialog {

    private EditText input;
    private TextView error;
    private InputAlertDialogActions iada;

    public InputAlertDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View view = findViewById(R.id.input_alert_dialog);
        setView(view);
        input = view.findViewById(R.id.input_alert_dialog_input);
        error = view.findViewById(R.id.input_alert_dialog_error);
        error.setVisibility(View.INVISIBLE); //GONE
        setButton(DialogInterface.BUTTON_POSITIVE, "Ок", (dialogInterface, id) -> {
            if (iada != null) {
                String inputText = input.getText().toString().trim();
                String validatedText = iada.OnValidation(inputText);
                if (validatedText != null && !"".equals(validatedText)) {
                    error.setVisibility(View.VISIBLE);
                    error.setText(validatedText);
                } else {
                    iada.OnSuccess(inputText);
                    dismiss();
                }
            } else {
                dismiss();
            }
        });
        setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена", (dialogInterface, id) -> {
            dismiss();
        });
        super.onCreate(savedInstanceState);
    }

    public void setInputAlertDialogActions(InputAlertDialogActions iada) {
        this.iada = iada;
    }

    public interface InputAlertDialogActions {
        String OnValidation(String text);

        void OnSuccess(String text);
    }
}