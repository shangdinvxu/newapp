package com.linkloving.rtring_new.logic.UI.customerservice.serviceItem;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.linkloving.rtring_new.R;

/**
 * Created by Linkloving on 2016/3/24.
 */
public class ServiceHotline extends Fragment{
    Button mButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflateAndSetupView(inflater, container, savedInstanceState, R.layout.phone_layout);
    }
    private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState, int layoutResourceId) {
        View layout = inflater.inflate(layoutResourceId, container, false);
        mButton = (Button) layout.findViewById(R.id.call_hotline);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())  //
                        .setTitle(getString(R.string.main_more_rexian))
                        .setMessage(getString(R.string.main_more_rexian_title))
                        .setPositiveButton(getString(R.string.general_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4009983282"));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(getString(R.string.general_cancel),null)
                        .show();

            }
        });
        return layout;
    }
}
