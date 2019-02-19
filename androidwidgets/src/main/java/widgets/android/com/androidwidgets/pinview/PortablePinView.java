package widgets.android.com.androidwidgets.pinview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import widgets.android.com.androidwidgets.R;
import widgets.android.com.androidwidgets.pinview.util.UiUtil;

/**
 * Created by Rehman Murad Ali on 12/27/2018.
 */
public class PortablePinView extends LinearLayout {

    private static final float DEFAULT_PIN_TEXT_SIZE = 40;
    private static final int DEFAULT_PIN_VIEW_DIGITS = 4;
    public static final int DEFAULT_MARGIN_BETWEEN_DIGIT = 15;
    public static final int NUMBER_ZERO = 0;
    public static final int NUMBER_ONE = 1;
    private int currentFocusIndex = 0;
    private int numberOfDigits;
    private int marginBetweenDigitDps;
    private float textSizeInPx;
    private ValidatePinListener validatePinListener;
    private List<PinViewEditText> editTextList = new ArrayList<>();


    public PortablePinView(Context context) {
        this(context, null);
    }

    public PortablePinView(Context context, AttributeSet attrs) {
        this(context, attrs, NUMBER_ZERO);
    }

    public PortablePinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PortablePinView);
            try {

                numberOfDigits = typedArray.getInt(R.styleable.PortablePinView_m_digit, DEFAULT_PIN_VIEW_DIGITS);
                marginBetweenDigitDps = typedArray.getInt(R.styleable.PortablePinView_m_margin_between, DEFAULT_MARGIN_BETWEEN_DIGIT);
                textSizeInPx = typedArray.getDimension(R.styleable.PortablePinView_m_text_size, DEFAULT_PIN_TEXT_SIZE);

            } finally {

                typedArray.recycle();
            }

        }
        init();
    }

    // Initialize Pin View and Edit Text Properties
    private void init() {
        // Linear Layout Properties
        setGravity(Gravity.CENTER);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UiUtil.showKeyboard(PortablePinView.this);
            }
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No need for this functionality
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // For shifting forward the focus of edit Text
                if (charSequence.toString().length() == NUMBER_ONE && currentFocusIndex < numberOfDigits - NUMBER_ONE) {
                    currentFocusIndex++;
                    editTextList.get(currentFocusIndex % editTextList.size()).requestFocus();
                }

                // For shifting backwards the focus of edit Text
                if (charSequence.toString().length() == NUMBER_ZERO && currentFocusIndex > NUMBER_ZERO) {
                    currentFocusIndex--;
                    editTextList.get(currentFocusIndex % editTextList.size()).requestFocus();
                }

                //To check if code is completed or not
                boolean isFinish = true;
                StringBuilder pin = new StringBuilder();
                for (PinViewEditText editText : editTextList) {

                    if (Objects.requireNonNull(editText.getText()).toString().length() == NUMBER_ZERO) {

                        isFinish = false;

                    } else {

                        pin.append(editText.getText().toString());

                    }
                }

                // Trigger listener if code is completed
                if (isFinish && validatePinListener != null)
                    validatePinListener.validatePin(pin.toString());


            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No need of this functionality
            }
        };

        // Pin View Edit Text Properties
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(NUMBER_ZERO, NUMBER_ZERO, dpToPx(marginBetweenDigitDps), NUMBER_ZERO);
        InputFilter[] filters = new InputFilter[NUMBER_ONE];
        filters[NUMBER_ZERO] = new InputFilter.LengthFilter(NUMBER_ONE);

        for (int i = NUMBER_ZERO; i < numberOfDigits; i++) {
            final PinViewEditText editText = new PinViewEditText(getContext(), R.drawable.verification_pin_drawable);
            editText.setEms(1);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setFilters(filters);
            editText.setGravity(Gravity.CENTER);
            editText.setTextSize(textSizeInPx);
            editText.setCursorVisible(false);
            editText.setLayoutParams(layoutParams);
            editText.addTextChangedListener(textWatcher);
            final Integer iObject = i;
            editText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        currentFocusIndex = iObject;
                    }
                    editText.setSelection(Objects.requireNonNull(editText.getText()).length());
                }
            });
            editTextList.add(editText);
            addView(editText);
        }
    }


    /**
     * Get Current content of Pin View
     */
    public String getText() {
        StringBuilder pin = new StringBuilder();

        for (PinViewEditText et : editTextList) {
            pin.append(et.getText());
        }

        return pin.toString();
    }


    /**
     * The functional interface will be trigger when user filled all the pins
     *
     * @param validatePinListener callback interface
     */
    public void addOnValidatePinListener(ValidatePinListener validatePinListener) {
        this.validatePinListener = validatePinListener;
    }


    // Dp to Pixels
    private int dpToPx(int dp) {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }


    // For Setting Edit Text Cursor at End
    private class PinViewEditText extends android.support.v7.widget.AppCompatEditText {

        public PinViewEditText(Context context) {
            super(context);
        }

        public PinViewEditText(Context context, @DrawableRes int layout) {
            super(context);
            setBackground(ResourcesCompat.getDrawable(getResources(), layout, null));
        }

        @Override
        protected void onSelectionChanged(int selStart, int selEnd) {
            CharSequence text = getText();
            if (text != null && (selStart != text.length() || selEnd != Objects.requireNonNull(text).length())) {
                setSelection(text.length(), text.length());
            } else super.onSelectionChanged(selStart, selEnd);

        }
    }


    public interface ValidatePinListener {

        void validatePin(String pin);
    }
}
