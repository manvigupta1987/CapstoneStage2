package com.example.manvi.walkmore.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.manvi.walkmore.R;
import com.example.manvi.walkmore.WalkMore;
import com.example.manvi.walkmore.ui.adapter.SpinnerAdapter;
import com.example.manvi.walkmore.data.WalkMorePreferences;
import com.example.manvi.walkmore.utils.ConstantUtils;
import com.example.manvi.walkmore.utils.DialogueUtill;
import com.example.manvi.walkmore.utils.HeightUtils;
import com.example.manvi.walkmore.utils.WeightUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class EditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    @BindView(R.id.toolbar1)
    Toolbar mToolBar;
    @BindView(R.id.edit_text1)
    EditText mEditText1;
    @BindView(R.id.edit_text2)
    EditText mEditText2;
    @BindView(R.id.weight_edit_Text)
    EditText mWeightEditText;
    @BindView(R.id.HeightSpinner)
    Spinner HeightSpinner;
    @BindView(R.id.WeightSpinner)
    Spinner WeightSpinner;

    private static String heightInFeet = "";
    private static String heighInInch = "";
    private static boolean WRONG_VALUE = false;
    private static boolean mFirstTimeInstallation = false;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        setupToolBar();

        WalkMore application = (WalkMore) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.edit_screen));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Intent intent = getIntent();
        if(intent!=null)
        {
            if(intent.getAction()!=null){
                if(intent.getAction().equals(ConstantUtils.FIRST_TIME)){
                    mFirstTimeInstallation = true;
                }
            } else {
                mFirstTimeInstallation = false;
            }
        }

        HeightSpinner.setOnItemSelectedListener(this);
        WeightSpinner.setOnItemSelectedListener(this);

        setHeightAdapter();
        setWeightAdapter();
        // Creating adapter for spinner

        addOnFocusChangeListenerOnHeight();
        addOnFocusChangeListenerOnWeight();

        fetchPreviousEditTextData();
    }

    private void fetchPreviousEditTextData(){
        float heightInInch = WalkMorePreferences.getUserHeight(this);
        if(heightInInch != 0) {
            if (WalkMorePreferences.isFeetNInch(this)) {
                int feet = (int)(heightInInch/ 12);
                float inch = heightInInch % 12;
                mEditText1.setText(String.valueOf(feet));
                mEditText2.setText(String.valueOf(inch));
                HeightSpinner.setSelection(0);
            }else {
                float cm = HeightUtils.convertInchToCentimeter(heightInInch);
                mEditText1.setText(String.valueOf(cm));
                HeightSpinner.setSelection(1);
            }
        }

        float weightInPound = WalkMorePreferences.getUserWeight(this);
        if(weightInPound!=0){
            if (WalkMorePreferences.isPound(this)) {
                mWeightEditText.setText(String.format(Locale.getDefault(),"%.1f", weightInPound));
                WeightSpinner.setSelection(0);
            }else {
                float weight = WeightUtils.convertPoundsToKilo(weightInPound);
                mWeightEditText.setText(String.format(Locale.getDefault(),"%.1f",weight));
                WeightSpinner.setSelection(1);
            }
        }
    }

    private void addOnFocusChangeListenerOnWeight(){

        mWeightEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    String message;
                    if(WalkMorePreferences.isPound(EditActivity.this)) {
                        message = getString(R.string.weight_message);
                    } else {
                        message = getString(R.string.kg_message);
                    }
                    if(mEditText1.getText().toString().equals("0")){
                        DialogueUtill.showInvalidDialogue(EditActivity.this, message);
                    }
                }

            }
        });
    }


    private void addOnFocusChangeListenerOnHeight(){
        mEditText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    String message;
                    if(WalkMorePreferences.isFeetNInch(EditActivity.this)) {
                        message = getString(R.string.feet_message);
                    }else {
                        message = getString(R.string.centimeter_message);
                    }
                    if(mEditText1.getText().toString().equals("0")){
                        DialogueUtill.showInvalidDialogue(EditActivity.this, message);
                    }
                }else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }

    private void setHeightAdapter(){
        // Creating adapter for spinner
        SpinnerAdapter dataAdapter = new SpinnerAdapter(this, getResources().getStringArray(R.array.pref_height_options));
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        HeightSpinner.setAdapter(dataAdapter);
    }

    private void setWeightAdapter(){
        SpinnerAdapter dataAdapter1 = new SpinnerAdapter(this, getResources().getStringArray(R.array.pref_weight_options));
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        WeightSpinner.setAdapter(dataAdapter1);
    }

    private void setupToolBar(){
        setSupportActionBar(mToolBar);

            mToolBar.setTitle(getString(R.string.edit_profile));

            if(getSupportActionBar()!=null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
                mToolBar.setTitleTextColor(android.graphics.Color.WHITE);
                getSupportActionBar().setHomeActionContentDescription(getString(R.string.a11y_previous_screen));
            }

    }

    private void updateUserHeight(){
        float height;
        String message;
        if(!heightInFeet.equals("") && !heightInFeet.equals("."))
        {
            if (WalkMorePreferences.isFeetNInch(this)) {
                if(heighInInch.equals("") && (mEditText2.isEnabled()) || (heighInInch.equals("."))){
                    heighInInch = "0";
                }
                height = HeightUtils.convertFeetToInch(Float.parseFloat(heightInFeet), Float.parseFloat(heighInInch));
                message = getString(R.string.feet_message);
                mEditText2.setContentDescription(getString(R.string.a11_height_in_Inch,heighInInch));

            } else {
                height =  HeightUtils.convertCentimeterToInch(Float.parseFloat(heightInFeet));
                message = getString(R.string.centimeter_message);
            }
            if(heightInFeet.equals("0")){
                DialogueUtill.showInvalidDialogue(EditActivity.this, message);
                WRONG_VALUE = true;
            } else {
                WalkMorePreferences.setUserHeight(this, height);
                WRONG_VALUE = false;
            }
            mEditText1.setContentDescription(getString(R.string.a11_height_in_Feet,heightInFeet));
        }
    }

    private void updateUserWeight() {
        String weightInPounds = mWeightEditText.getText().toString();
        String message = getString(R.string.weight_message);
        if(!weightInPounds.equals("") && !weightInPounds.equals(".")) {
            float weight = (Float.parseFloat(weightInPounds));
            mWeightEditText.setContentDescription(getString(R.string.a11_weight, Float.parseFloat(weightInPounds)));
            if (!(WalkMorePreferences.isPound(this))) {
                weight = WeightUtils.convertKiloToPounds(Float.parseFloat(weightInPounds));
                message = getString(R.string.kg_message);
                mWeightEditText.setContentDescription(getString(R.string.a11_weight, weight));
            }
            if(weightInPounds.equals("0")){
                DialogueUtill.showInvalidDialogue(EditActivity.this, message);
                WRONG_VALUE = true;
            }else {
                WalkMorePreferences.setUserWeight(this, weight);
                WRONG_VALUE = false;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if(item.equals(getString(R.string.pref_units_label_centi))){
            mEditText2.setVisibility(View.GONE);
            mEditText2.setEnabled(false);
            mEditText1.setWidth(getResources().getInteger(R.integer.centimeter_text));
            WalkMorePreferences.setHeightUnit(this,getString(R.string.pref_units_Centimeter));
        } else if(item.equals(getString(R.string.pref_units_label_feet))){
            mEditText1.setWidth(getResources().getInteger(R.integer.inch_text_width));
            mEditText2.setVisibility(View.VISIBLE);
            mEditText2.setEnabled(true);
            WalkMorePreferences.setHeightUnit(this,getString(R.string.pref_units_feet));
        } else if(item.equals(getString(R.string.pref_units_label_kg))){
            WalkMorePreferences.setWeightUnit(this,getString(R.string.pref_units_kg));
        }else if(item.equals(getString(R.string.pref_units_label_pound))) {
            WalkMorePreferences.setWeightUnit(this, getString(R.string.pref_units_pound));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_save:
                heightInFeet = mEditText1.getText().toString();
                heighInInch = mEditText2.getText().toString();
                updateUserHeight();
                updateUserWeight();
                if(!WRONG_VALUE) {
                    finish();
                }
                return true;
            case android.R.id.home:
                if(mFirstTimeInstallation) {
                    DialogueUtill.showHeightDialogue(this);
                }else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
