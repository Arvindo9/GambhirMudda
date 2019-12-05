package com.jithvar.gambhirmudda.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jithvar.gambhirmudda.R;

/**
 * Created by Arvindo Mondal on 2/8/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class CommentDialog extends DialogFragment  implements TextView.OnEditorActionListener{

    private EditText nameEt;
    private EditText commentEt;
    private EditText phoneEt;


    public interface UserNameListener {
        void onFinishUserDialog(String name, String phone, String comment);
    }

    // Empty constructor required for DialogFragment
    public CommentDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_dialog, container);
        nameEt = (EditText) view.findViewById(R.id.name_et);
        commentEt = (EditText) view.findViewById(R.id.comment_et);
        phoneEt = (EditText) view.findViewById(R.id.phone_et);

        // set this instance as callback for editor action
        phoneEt.setOnEditorActionListener(this);
        phoneEt.requestFocus();

        nameEt.setOnEditorActionListener(this);
        nameEt.requestFocus();

        commentEt.setOnEditorActionListener(this);
        commentEt.requestFocus();


        getDialog().getWindow().setLayout(200, 200);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle("Please enter username");

        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // Return input text to activity
        UserNameListener activity = (UserNameListener) getActivity();
        activity.onFinishUserDialog(nameEt.getText().toString(), phoneEt.getText().toString(),
                commentEt.getText().toString());
        this.dismiss();
        return true;
    }
}
