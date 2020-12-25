package com.bigkoo.pickerview.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.configure.PickerOptions;

import java.util.List;

/**
 * 条件选择器
 */

public class OptionsPickerView<T> extends BasePickerView implements View.OnClickListener {

    private WheelOptions<T> wheelOptions;

    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";


    public OptionsPickerView(PickerOptions pickerOptions) {
        super(pickerOptions.context);
        mPickerOptions = pickerOptions;
        initView(pickerOptions.context);
    }

    private void initView(Context context) {
        setDialogOutSideCancelable();
        initViews();
        initAnim();
        initEvents();
        if (mPickerOptions.customListener == null) {
            LayoutInflater.from(context).inflate(mPickerOptions.layoutRes, contentContainer);

            //顶部标题
            TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
            RelativeLayout rv_top_bar = (RelativeLayout) findViewById(R.id.rv_topbar);

            //确定和取消按钮
            Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
            Button btnCancel = (Button) findViewById(R.id.btnCancel);

            btnSubmit.setTag(TAG_SUBMIT);
            btnCancel.setTag(TAG_CANCEL);
            btnSubmit.setOnClickListener(this);
            btnCancel.setOnClickListener(this);

            //设置文字
            btnSubmit.setText(TextUtils.isEmpty(mPickerOptions.textContentConfirm) ? context.getResources().getString(R.string.pickerview_submit) : mPickerOptions.textContentConfirm);
            btnCancel.setText(TextUtils.isEmpty(mPickerOptions.textContentCancel) ? context.getResources().getString(R.string.pickerview_cancel) : mPickerOptions.textContentCancel);
            tvTitle.setText(TextUtils.isEmpty(mPickerOptions.textContentTitle) ? "" : mPickerOptions.textContentTitle);//默认为空

            //设置color
            btnSubmit.setTextColor(mPickerOptions.textColorConfirm);
            btnCancel.setTextColor(mPickerOptions.textColorCancel);
            tvTitle.setTextColor(mPickerOptions.textColorTitle);
            rv_top_bar.setBackgroundColor(mPickerOptions.bgColorTitle);

            //设置文字大小
            btnSubmit.setTextSize(mPickerOptions.textSizeSubmitCancel);
            btnCancel.setTextSize(mPickerOptions.textSizeSubmitCancel);
            tvTitle.setTextSize(mPickerOptions.textSizeTitle);
        } else {
            mPickerOptions.customListener.customLayout(LayoutInflater.from(context).inflate(mPickerOptions.layoutRes, contentContainer));
        }

        // ----滚轮布局
        final LinearLayout optionsPicker = (LinearLayout) findViewById(R.id.optionspicker);
        optionsPicker.setBackgroundColor(mPickerOptions.bgColorWheel);

        wheelOptions = new WheelOptions<>(optionsPicker, mPickerOptions.isRestoreItem);
        if (mPickerOptions.optionsSelectChangeListener != null) {
            wheelOptions.setOptionsSelectChangeListener(mPickerOptions.optionsSelectChangeListener);
        }

        wheelOptions.setTextContentSize(mPickerOptions.textSizeContent);
        wheelOptions.setItemsVisible(mPickerOptions.itemsVisibleCount);
        wheelOptions.setAlphaGradient(mPickerOptions.isAlphaGradient);
        wheelOptions.setLabels(mPickerOptions.label1, mPickerOptions.label2, mPickerOptions.label3, mPickerOptions.label4);
        wheelOptions.setTextXOffset(mPickerOptions.x_offset_one, mPickerOptions.x_offset_two, mPickerOptions.x_offset_three, mPickerOptions.x_offset_fourth);
        wheelOptions.setCyclic(mPickerOptions.cyclic1, mPickerOptions.cyclic2, mPickerOptions.cyclic3, mPickerOptions.cyclic4);
        wheelOptions.setTypeface(mPickerOptions.font);

        setOutSideCancelable(mPickerOptions.cancelable);

        wheelOptions.setDividerColor(mPickerOptions.dividerColor);
        wheelOptions.setDividerType(mPickerOptions.dividerType);
        wheelOptions.setLineSpacingMultiplier(mPickerOptions.lineSpacingMultiplier);
        wheelOptions.setTextColorOut(mPickerOptions.textColorOut);
        wheelOptions.setTextColorCenter(mPickerOptions.textColorCenter);
        wheelOptions.isCenterLabel(mPickerOptions.isCenterLabel);
    }

    /**
     * 动态设置标题
     *
     * @param text 标题文本内容
     */
    public void setTitleText(String text) {
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText(text);
        }
    }

    /**
     * 设置默认选中项
     *
     * @param option1
     */
    public void setSelectOptions(int option1) {
        setSelectOptions(option1, 0, 0);
    }


    public void setSelectOptions(int option1, int option2) {
        setSelectOptions(option1, option2, 0);
    }

    public void setSelectOptions(int option1, int option2, int option3) {
        setSelectOptions(option1, option2, option3, 0);
    }

    public void setSelectOptions(int option1, int option2, int option3, int option4) {
        mPickerOptions.option1 = option1;
        mPickerOptions.option2 = option2;
        mPickerOptions.option3 = option3;
        mPickerOptions.option4 = option4;
        reSetCurrentItems();
    }

    public void setSelectOptions(String options1) {
        setSelectOptions(options1, null, null, null);
    }

    public void setSelectOptions(String options1, String options2) {
        setSelectOptions(options1, options2, null, null);
    }

    public void setSelectOptions(String options1, String options2, String options3) {
        setSelectOptions(options1, options2, options3, null);
    }

    public void setSelectOptions(String options1, String options2, String options3, String options4) {
        mPickerOptions.options1 = options1;
        mPickerOptions.options2 = options2;
        mPickerOptions.options3 = options3;
        mPickerOptions.options4 = options4;
        reSetCurrentItemsstr();
    }


    private void reSetCurrentItems() {
        if (wheelOptions != null) {
            wheelOptions.setCurrentItems(mPickerOptions.option1, mPickerOptions.option2, mPickerOptions.option3, mPickerOptions.option4);
        }
    }

    private void reSetCurrentItemsstr() {

        if (wheelOptions != null) {
            wheelOptions.setCurrentItems(mPickerOptions.options1, mPickerOptions.options2, mPickerOptions.options3, mPickerOptions.options4);

        }
    }


    public void setPicker(List<T> optionsItems) {
        this.setPicker(optionsItems, null, null);
    }

    public void setPicker(List<T> options1Items, List<List<T>> options2Items) {
        this.setPicker(options1Items, options2Items, null);
    }

    public void setPicker(List<T> options1Items,
                          List<List<T>> options2Items,
                          List<List<List<T>>> options3Items) {

        wheelOptions.setPicker(options1Items, options2Items, options3Items);
        reSetCurrentItems();
    }


    public void setPicker(List<T> options1Items,
                          List<List<T>> options2Items,
                          List<List<List<T>>> options3Items,
                          List<List<List<T>>> options4Items) {

        wheelOptions.setPicker(options1Items, options2Items, options3Items);
        reSetCurrentItems();
    }

    //不联动情况下调用
    public void setNPicker(List<T> options1Items) {
        setNPicker(options1Items, null, null, null, null);
    }

    public void setNPicker(List<T> options1Items, List<T> options2Items) {
        setNPicker(options1Items, options2Items, null, null, null);
    }

    public void setNPicker(List<T> options1Items, List<T> options2Items, List<T> options3Items) {
        setNPicker(options1Items, options2Items, options3Items, null, null);
    }

    public void setNPicker(List<T> options1Items,
                           List<T> options2Items,
                           String textstr) {
        setNPicker(options1Items, options2Items, null, null, textstr, null, null);
    }

    public void setNPicker(List<T> options1Items,
                           List<T> options2Items,
                           List<T> options3Items,
                           List<T> options4Items,
                           String textstr) {

        wheelOptions.setLinkage(false);
        wheelOptions.setNPicker(options1Items, options2Items, options3Items, options4Items, null, textstr, null);
        reSetCurrentItems();
    }

    public void setNPicker(List<T> options1Items,
                           List<T> options2Items,
                           List<T> options3Items,
                           List<T> options4Items,
                           String text1str,
                           String text2str,
                           String text3str) {

        wheelOptions.setLinkage(false);
        wheelOptions.setNPicker(options1Items, options2Items, options3Items, options4Items, text1str, text2str, text3str);
        reSetCurrentItems();
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_SUBMIT)) {
            returnData();
        } else if (tag.equals(TAG_CANCEL)) {
            if (mPickerOptions.cancelListener != null) {
                mPickerOptions.cancelListener.onClick(v);
            }
        }
        dismiss();
    }

    //抽离接口回调的方法
    public void returnData() {
        if (mPickerOptions.optionsSelectListener != null) {
            int[] optionsCurrentItems = wheelOptions.getCurrentItems();
            mPickerOptions.optionsSelectListener.onOptionsSelect(optionsCurrentItems[0], optionsCurrentItems[1], optionsCurrentItems[2], optionsCurrentItems[3], clickView);
        }
    }


    @Override
    public boolean isDialog() {
        return mPickerOptions.isDialog;
    }
}
