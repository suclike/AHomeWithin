package org.ahomewithin.ahomewithin.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.ahomewithin.ahomewithin.BuyClient;
import org.ahomewithin.ahomewithin.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chezlui on 07/03/16.
 */
public class BuyDialog extends DialogFragment {
    AsyncHttpResponseHandler responseHandler;

    public static final String LOG_TAG = BuyDialog.class.getSimpleName();
    @Bind(R.id.etBuyerName) EditText etBuyerName;
    @Bind(R.id.etBuyerCardNumber) EditText etBuyerCardNumber;
    @Bind(R.id.etBuyerDate) EditText etBuyerDate;
    @Bind(R.id.etBuyerCVC) EditText etBuyerCVC;
    @Bind(R.id.btBuy) Button btBuy;

    public BuyDialog() {
    }

    public static BuyDialog newInstance(AsyncHttpResponseHandler responseHandler) {
        BuyDialog buyDialog = new BuyDialog();
        buyDialog.responseHandler = responseHandler;
        return buyDialog;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Translucent);
        View view = inflater.inflate(R.layout.dialog_buy, container);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setListeners();
    }

    // For making full screen
    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setListeners() {

        btBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy();
            }
        });

        etBuyerCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ((s.length() % 4 == 0) && (s.length() < 15)) {
                    s.append(" ");
                }
            }
        });

    }

    private void buy() {

        BuyClient client = BuyClient.getRestClient();
        client.buy(responseHandler,
                etBuyerName.getText().toString(),
                etBuyerCardNumber.getText().toString(),
                etBuyerDate.getText().toString(),
                etBuyerCVC.getText().toString()
        );
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}
