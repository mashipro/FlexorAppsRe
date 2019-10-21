package com.flexor.storage.flexorstoragesolution;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flexor.storage.flexorstoragesolution.Models.Transaction;
import com.flexor.storage.flexorstoragesolution.Models.User;
import com.flexor.storage.flexorstoragesolution.Utility.Constants;
import com.flexor.storage.flexorstoragesolution.Utility.ManPaymentManager;
import com.flexor.storage.flexorstoragesolution.Utility.TransactionManager;
import com.flexor.storage.flexorstoragesolution.Utility.UserManager;

import java.util.HashMap;

public class TopUpFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "TopUpFragment";
    private View view;
    private TextView rekening;
    private Button prosesTrans;
    private UserManager userManager;
    private User user;
    private ManPaymentManager manPaymentManager;
    private String transREF = "";
    private Integer transValue;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_top_up, container, false);

        userManager = new UserManager();
        userManager.getInstance();

        user = userManager.getUser();
        manPaymentManager = new ManPaymentManager();

        Spinner spinner = view.findViewById(R.id.spinner_topup);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.nominal_topup, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = (String) adapterView.getItemAtPosition(i);
                if (i == 0){
                    transValue = 25000;
                }else if (i == 1){
                    transValue = 50000;
                }else if (i == 2){
                    transValue = 100000;
                }else if (i == 3){
                    transValue = 250000;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner spinner2 = view.findViewById(R.id.spinner_pembayaran);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.pembayaran_topup, android.R.layout.simple_spinner_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);



        rekening = view.findViewById(R.id.rekening_pembayaran);
        prosesTrans = view.findViewById(R.id.proses_transaksi);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = (String) adapterView.getItemAtPosition(i);
                if (i == 0){
                    rekening.setText("Rekening BCA a.n njun - 8123663072");
                    transREF = String.valueOf(Constants.BANK_BCA);
                }else if (i == 1) {
                    rekening.setText("Rekening BRI a.n njun - 111019119233");
                    transREF = String.valueOf(Constants.BANK_BRI);
                }else if (i == 2) {
                    rekening.setText("Rekening Mandiri a.n njun - 00031239213");
                    transREF = String.valueOf(Constants.BANK_MANDIRI);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prosesTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proses_transaksi(user.getUserID(), transREF, transValue );
                startActivity(new Intent(getActivity(), KonfirmTransaksiActivity.class));
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String text = adapterView.getItemAtPosition(position).toString();
        Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void proses_transaksi(String idSource, final String transREF, final Integer transValue) {

        manPaymentManager.requestTopUp(idSource, transREF, transValue, new TransactionManager() {
            @Override
            public void onTransactionSuccess(Boolean success, String transactionID) {
                if (success){
                    Log.d(TAG, "onTransactionSuccess: yoyo"+user.getUserID() + transREF + transValue);
                }else{
                    Log.d(TAG, "onTransactionFailed: argh! ");
                }
            }
        });
    }
}
