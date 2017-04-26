package com.hjx.v2ex.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by shaxiboy on 2017/4/24 0024.
 */

public class LoginoutDialogFragment extends DialogFragment {

    LoginoutDialogFragmentListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (LoginoutDialogFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确定要登出账户吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.loginout();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    public interface LoginoutDialogFragmentListener {
        public void loginout();
    }
}
