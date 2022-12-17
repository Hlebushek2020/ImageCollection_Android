package com.sergeygovorunov.imagecollection.dialogs;

import android.annotation.SuppressLint;
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
    private EditText et_input;
    private TextView tv_error;
    private InputAlertDialogActions inputAlertDialogActions;

    public InputAlertDialog(Context context) {
        super(context);
        //this.context = context;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.input_alert_dialog, null);
        setView(view);
        et_input = view.findViewById(R.id.input_alert_dialog_input);
        tv_error = view.findViewById(R.id.input_alert_dialog_error);
        tv_error.setVisibility(View.GONE);
        setButton(DialogInterface.BUTTON_POSITIVE, "Ок", (dialogInterface, id) -> {
        });
        setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена", (dialogInterface, id) -> {
        });
        super.onCreate(savedInstanceState);
        if (inputAlertDialogActions != null) {
            getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
                String inputText = et_input.getText().toString().trim();
                String validatedText = inputAlertDialogActions.OnValidation(inputText);
                if (validatedText != null && !"".equals(validatedText)) {
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(validatedText);
                } else {
                    inputAlertDialogActions.OnSuccess(inputText);
                    dismiss();
                }
            });
        }
    }

    public void setInputAlertDialogActions(InputAlertDialogActions iada) {
        this.inputAlertDialogActions = iada;
    }

    public interface InputAlertDialogActions {
        String OnValidation(String text);

        void OnSuccess(String text);
    }
}