package com.fitness.manvi.walkmore.ui.activity;

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

import com.fitness.manvi.walkmore.R;
import com.fitness.manvi.walkmore.WalkMore;
import com.fitness.manvi.walkmore.ui.adapter.SpinnerAdapter;
import com.fitness.manvi.walkmore.data.WalkMorePreferences;
import com.fitness.manvi.walkmore.utils.ConstantUtils;
import com.fitness.manvi.walkmore.utils.DialogueUtill;
import com.fitness.manvi.walkmore.utils.HeightUtils;
import com.fitness.manvi.walkmore.utils.WeightUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.common.base.Preconditions;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import com.google.common.base.Strings;
import com.google.common.collect.Range;

public final class EditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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
    private static boolean WRONG_HEIGHT_VALUE = false;
    private static boolean WRONG_WEIGHT_VALUE = false;
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
        try {
            Preconditions.checkNotNull(intent, "intent can't be null");
            if (intent.getAction() != null) {
                if (intent.getAction().equals(ConstantUtils.FIRST_TIME)) {
                    mFirstTimeInstallation = true;
                }
            } else {
                mFirstTimeInstallation = false;
            }
        } catch (NullPointerException e) {
            Timber.e(e.getMessage(), "intent can't be null");
        }

        HeightSpinner.setOnItemSelectedListener(this);
        WeightSpinner.setOnItemSelectedListener(this);

        setHeightAdapter();
        setWeightAdapter();
        // Creating adapter for spinner

        addOnFocusChangeListenerOnHeight();
        addOnFocusChangeListenerOnWeight();
        if(!mFirstTimeInstallation) {
            fetchPreviousEditTextData();
        }
    }

    private void fetchPreviousEditTextData() {
        float heightInInch = WalkMorePreferences.getUserHeight(this);
        Preconditions.checkArgument(heightInInch > 0, "height should be more than 0");

        if (WalkMorePreferences.isFeetNInch(this)) {
            int feet = (int) (heightInInch / 12);
            float inch = heightInInch % 12;
            mEditText1.setText(String.valueOf(feet));
            mEditText2.setText(String.valueOf(inch));
            HeightSpinner.setSelection(0);
        } else {
            float cm = HeightUtils.convertInchToCentimeter(heightInInch);
            mEditText1.setText(String.valueOf(cm));
            HeightSpinner.setSelection(1);
        }
        float weightInPound = WalkMorePreferences.getUserWeight(this);
        Preconditions.checkArgument(weightInPound > 0, "weight should be more than 0");
        if (WalkMorePreferences.isPound(this)) {
            mWeightEditText.setText(String.format(Locale.getDefault(), "%.1f", weightInPound));
            WeightSpinner.setSelection(0);
        } else {
            float weight = WeightUtils.convertPoundsToKilo(weightInPound);
            mWeightEditText.setText(String.format(Locale.getDefault(), "%.1f", weight));
            WeightSpinner.setSelection(1);
        }

    }

    private void addOnFocusChangeListenerOnWeight() {

        mWeightEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String message;
                    if (WalkMorePreferences.isPound(EditActivity.this)) {
                        message = getString(R.string.weight_message);
                    } else {
                        message = getString(R.string.kg_message);
                    }
                    if (mWeightEditText.getText().toString().equals("0")) {
                        mWeightEditText.setError(message);
                    }
                }

            }
        });
    }

    private void addOnFocusChangeListenerOnHeight() {
        mEditText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String message;
                    if (WalkMorePreferences.isFeetNInch(EditActivity.this)) {
                        message = getString(R.string.feet_message);
                    } else {
                        message = getString(R.string.centimeter_message);
                    }
                    if (mEditText1.getText().toString().equals("0")) {
                        mEditText1.setError(message);
                    }
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }

    private void setHeightAdapter() {
        // Creating adapter for spinner
        SpinnerAdapter dataAdapter = new SpinnerAdapter(this, getResources().getStringArray(R.array.pref_height_options));
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        HeightSpinner.setAdapter(dataAdapter);
    }

    private void setWeightAdapter() {
        SpinnerAdapter dataAdapter1 = new SpinnerAdapter(this, getResources().getStringArray(R.array.pref_weight_options));
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        WeightSpinner.setAdapter(dataAdapter1);
    }

    private void setupToolBar() {
        setSupportActionBar(mToolBar);

        mToolBar.setTitle(getString(R.string.edit_profile));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
            mToolBar.setTitleTextColor(android.graphics.Color.WHITE);
            getSupportActionBar().setHomeActionContentDescription(getString(R.string.a11y_previous_screen));
        }

    }

    private void updateUserHeight() {
        float height = 0;
        String message = "";
        Range<Float> feetRange = Range.closed(1f, 8f);
        Range<Float> centimeterRange = Range.closed(30f, 272f);

        if(!Strings.isNullOrEmpty(heightInFeet) && !heightInFeet.equals(".")) {
            float heightFeet = Float.parseFloat(heightInFeet);
            if (WalkMorePreferences.isFeetNInch(this)) {
                //This check avoids the crash if user fills the height in inch as empty or .
                if (heighInInch.equals("") && (mEditText2.isEnabled()) || (heighInInch.equals("."))) {
                    heighInInch = "0";
                }
                if (feetRange.contains(heightFeet)) {
                    height = HeightUtils.convertFeetToInch(heightFeet, Float.parseFloat(heighInInch));
                    mEditText2.setContentDescription(getString(R.string.a11_height_in_Inch, heighInInch));
                    WRONG_HEIGHT_VALUE = false;
                } else {
                    message = getString(R.string.feet_message);
                    WRONG_HEIGHT_VALUE = true;
                }

            } else {
                if (centimeterRange.contains(heightFeet)) {
                    height = HeightUtils.convertCentimeterToInch(heightFeet);
                    WRONG_HEIGHT_VALUE = false;
                } else {
                    message = getString(R.string.centimeter_message);
                    WRONG_HEIGHT_VALUE = true;
                }
            }
            if (WRONG_HEIGHT_VALUE) {
                mEditText1.setError(message);
            } else {
                WalkMorePreferences.setUserHeight(this, height);
            }
            mEditText1.setContentDescription(getString(R.string.a11_height_in_Feet, heightInFeet));
        } else {
            mEditText1.setError("enter a valid value");
            WRONG_HEIGHT_VALUE = true;
        }
    }

    private void updateUserWeight() {
        String message = "";
        float weight = 0;

        Range<Float> weightRange = Range.closed(1f, 1000f);
        Range<Float> kiloRange = Range.closed(1f, 450f);

        String weightInPounds = mWeightEditText.getText().toString();
        if (!Strings.isNullOrEmpty(weightInPounds) && !weightInPounds.equals(".")) {
            Float weightFloat = Float.parseFloat(weightInPounds);
            if (WalkMorePreferences.isPound(this)) {
                if (weightRange.contains(weightFloat)) {
                    weight = weightFloat;
                    WRONG_WEIGHT_VALUE = false;
                } else {
                    message = getString(R.string.weight_message);
                    WRONG_WEIGHT_VALUE = true;
                }
                mWeightEditText.setContentDescription(getString(R.string.a11_weight, weight));
            } else {
                if (kiloRange.contains(weightFloat)) {
                    weight = WeightUtils.convertKiloToPounds(weightFloat);
                    WRONG_WEIGHT_VALUE = false;
                } else {
                    message = getString(R.string.kg_message);
                    WRONG_WEIGHT_VALUE = true;
                }
                mWeightEditText.setContentDescription(getString(R.string.a11_weight, weight));
            }
            if (WRONG_WEIGHT_VALUE) {
                mWeightEditText.setError(message);
            } else {
                WalkMorePreferences.setUserWeight(this, weight);
            }
        } else {
            mWeightEditText.setError("enter a valid value");
            WRONG_WEIGHT_VALUE = true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if (item.equals(getString(R.string.pref_units_label_centi))) {
            mEditText2.setVisibility(View.GONE);
            mEditText2.setEnabled(false);
            mEditText1.setWidth(getResources().getInteger(R.integer.centimeter_text));
            WalkMorePreferences.setHeightUnit(this, getString(R.string.pref_units_Centimeter));
        } else if (item.equals(getString(R.string.pref_units_label_feet))) {
            mEditText1.setWidth(getResources().getInteger(R.integer.inch_text_width));
            mEditText2.setVisibility(View.VISIBLE);
            mEditText2.setEnabled(true);
            WalkMorePreferences.setHeightUnit(this, getString(R.string.pref_units_feet));
        } else if (item.equals(getString(R.string.pref_units_label_kg))) {
            WalkMorePreferences.setWeightUnit(this, getString(R.string.pref_units_kg));
        } else if (item.equals(getString(R.string.pref_units_label_pound))) {
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
        switch (id) {
            case R.id.action_save:
                heightInFeet = mEditText1.getText().toString();
                heighInInch = mEditText2.getText().toString();
                updateUserHeight();
                updateUserWeight();
                if (!WRONG_WEIGHT_VALUE && !WRONG_HEIGHT_VALUE) {
                    finish();
                }
                return true;
            case android.R.id.home:
                if (mFirstTimeInstallation) {
                    DialogueUtill.showHeightDialogue(this);
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
