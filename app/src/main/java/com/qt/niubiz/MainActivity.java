package com.qt.niubiz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.niubiz.sdk.NiubizApp;
import com.niubiz.sdk.presentation.custom.NiubizViewAuthorizationCustom;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading = findViewById(R.id.progress);
    }

    public void payment(View view) {

        final Map<String, Object> data = new HashMap<>();

        data.put(NiubizApp.NIUBIZ_CHANNEL, NiubizApp.Channel.MOBILE);
        data.put(NiubizApp.NIUBIZ_COUNTABLE, true);
        data.put(NiubizApp.NIUBIZ_MERCHANT, "341198214");
        data.put(NiubizApp.NIUBIZ_PURCHASE_NUMBER, "2021");
        data.put(NiubizApp.NIUBIZ_NAME, "farid");
        data.put(NiubizApp.NIUBIZ_LASTNAME, "gamarra floreano");
        data.put(NiubizApp.NIUBIZ_EMAIL, "farid@hotmail.com");
        data.put(NiubizApp.NIUBIZ_USER_TOKEN, "farid@hotmail.com");
        data.put(NiubizApp.NIUBIZ_AMOUNT, 25.50);//para traer tarjetas recordadas
        data.put(NiubizApp.NIUBIZ_ENDPOINT, "https://apitestenv.vnforapps.com/");

        // MDDS
        HashMap<String, String> MDDData = new HashMap<String, String>();
        MDDData.put("5", "TempMDD5");
        MDDData.put("8", "TempMDD8");
        MDDData.put("20", "TempMDD20");
        MDDData.put("30", "TempMDD30");
        MDDData.put("40", "TempMDD40");
        MDDData.put("50", "TempMDD50");
        data.put(NiubizApp.NIUBIZ_MDD, MDDData);


        final NiubizViewAuthorizationCustom customView = new NiubizViewAuthorizationCustom();
        customView.setInputTextColor(R.color.teal_800);
        customView.setInputTextFont(R.font.dm_sans_bold);
        customView.setButtonPayColor(R.color.amber_800);
        customView.setButtonPayText("Pagar...");
        customView.setInputTextSize(70);

        loading.setVisibility(View.VISIBLE);

        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("https://apitestenv.vnforapps.com/api.security/v1/security")
                .addHeaders("Authorization","Basic Z2lhbmNhZ2FsbGFyZG9AZ21haWwuY29tOkF2MyR0cnV6")
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        loading.setVisibility(View.GONE);

                        try {
                            data.put(NiubizApp.NIUBIZ_SECURITY_TOKEN, response);

                            NiubizApp.authorization(MainActivity.this, data, customView);
                        } catch (Exception e) {
                            Log.e(TAG, "onCreate: ", e);
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        loading.setVisibility(View.GONE);
                        dialogResult("Error al generar token");
                    }
                });

    }


    private void dialogResult(String msg){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("VISANET")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        dialog1.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
        TextView textView = dialog.findViewById(android.R.id.message);
        textView.setScroller(new Scroller(this));
        textView.setVerticalScrollBarEnabled(true);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NiubizApp.NIUBIZ_AUTHORIZATION) {

            if (data != null) {
                if (resultCode == RESULT_OK) {
                    String returnString = data.getExtras().getString(NiubizApp.keySuccess);
                    Toast toast1 = Toast.makeText(getApplicationContext(), returnString, Toast.LENGTH_SHORT);
                    toast1.show();
                    Log.i(TAG, "onActivityResult: " + returnString);
                } else {
                    String returnString = data.getExtras().getString(NiubizApp.keyError);
                    returnString = returnString != null ? returnString : "";
                    Toast toast1 = Toast.makeText(getApplicationContext(), returnString, Toast.LENGTH_SHORT);
                    toast1.show();
                    Log.i(TAG, "onActivityResult: " + returnString);
                }
            } else {
                Toast toast1 = Toast.makeText(getApplicationContext(), "Cancelado....", Toast.LENGTH_SHORT);
                toast1.show();
            }
        }

    }

}