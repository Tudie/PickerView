package com.bigkoo.pickerview.view;

import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;

import java.util.List;

public class WheelOptions<T> {
    private View view;
    private WheelView wv_option1;
    private WheelView wv_option2;
    private WheelView wv_option3;
    private WheelView wv_option4;
    private TextView text1tv;
    private TextView text2tv;
    private TextView text3tv;

    private List<T> mOptions1Items;
    private List<List<T>> mOptions2Items;
    private List<List<List<T>>> mOptions3Items;
    private List<List<List<List<T>>>> mOptions4Items;
    private List<T> mOptions1Item;
    private List<T> mOptions2Item;
    private List<T> mOptions3Item;
    private List<T> mOptions4Item;

    private boolean linkage = true;//默认联动
    private boolean isRestoreItem; //切换时，还原第一项
    private OnItemSelectedListener wheelListener_option1;
    private OnItemSelectedListener wheelListener_option2;

    private OnOptionsSelectChangeListener optionsSelectChangeListener;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public WheelOptions(View view, boolean isRestoreItem) {
        super();
        this.isRestoreItem = isRestoreItem;
        this.view = view;
        wv_option1 = (WheelView) view.findViewById(R.id.options1);// 初始化时显示的数据
        wv_option2 = (WheelView) view.findViewById(R.id.options2);
        wv_option3 = (WheelView) view.findViewById(R.id.options3);
        wv_option4 = (WheelView) view.findViewById(R.id.options4);
        text1tv = (TextView) view.findViewById(R.id.text1tv);
        text2tv = (TextView) view.findViewById(R.id.text2tv);
        text3tv = (TextView) view.findViewById(R.id.text3tv);
    }

    public void setPicker(List<T> options1Items) {
        setPicker(options1Items, null);
    }

    public void setPicker(List<T> options1Items, List<List<T>> options2Items) {
        setPicker(options1Items, options2Items, null);
    }

    public void setPicker(List<T> options1Items,
                          List<List<T>> options2Items,
                          List<List<List<T>>> options3Items) {
        this.mOptions1Items = options1Items;
        this.mOptions2Items = options2Items;
        this.mOptions3Items = options3Items;

        // 选项1
        wv_option1.setAdapter(new ArrayWheelAdapter(mOptions1Items));// 设置显示数据
        wv_option1.setCurrentItem(0);// 初始化时显示的数据
        // 选项2
        if (mOptions2Items != null) {
            wv_option2.setAdapter(new ArrayWheelAdapter(mOptions2Items.get(0)));// 设置显示数据
        }
        wv_option2.setCurrentItem(wv_option2.getCurrentItem());// 初始化时显示的数据
        // 选项3
        if (mOptions3Items != null) {
            wv_option3.setAdapter(new ArrayWheelAdapter(mOptions3Items.get(0).get(0)));// 设置显示数据
        }
        wv_option3.setCurrentItem(wv_option3.getCurrentItem());
        wv_option1.setIsOptions(true);
        wv_option2.setIsOptions(true);
        wv_option3.setIsOptions(true);

        if (this.mOptions2Items == null) {
            wv_option2.setVisibility(View.GONE);
        } else {
            wv_option2.setVisibility(View.VISIBLE);
        }
        if (this.mOptions3Items == null) {
            wv_option3.setVisibility(View.GONE);
        } else {
            wv_option3.setVisibility(View.VISIBLE);
        }

        // 联动监听器
        wheelListener_option1 = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(int index) {
                int opt2Select = 0;
                if (mOptions2Items == null) {//只有1级联动数据
                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), 0, 0, 0);
                    }
                } else {
                    if (!isRestoreItem) {
                        opt2Select = wv_option2.getCurrentItem();//上一个opt2的选中位置
                        //新opt2的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                        opt2Select = opt2Select >= mOptions2Items.get(index).size() - 1 ? mOptions2Items.get(index).size() - 1 : opt2Select;
                    }
                    wv_option2.setAdapter(new ArrayWheelAdapter(mOptions2Items.get(index)));
                    wv_option2.setCurrentItem(opt2Select);

                    if (mOptions3Items != null) {
                        wheelListener_option2.onItemSelected(opt2Select);
                    } else {//只有2级联动数据，滑动第1项回调
                        if (optionsSelectChangeListener != null) {
                            optionsSelectChangeListener.onOptionsSelectChanged(index, opt2Select, 0, 0);
                        }
                    }
                }
            }
        };

        wheelListener_option2 = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(int index) {
                if (mOptions3Items != null) {
                    int opt1Select = wv_option1.getCurrentItem();
                    opt1Select = opt1Select >= mOptions3Items.size() - 1 ? mOptions3Items.size() - 1 : opt1Select;
                    index = index >= mOptions2Items.get(opt1Select).size() - 1 ? mOptions2Items.get(opt1Select).size() - 1 : index;
                    int opt3 = 0;
                    if (!isRestoreItem) {
                        // wv_option3.getCurrentItem() 上一个opt3的选中位置
                        //新opt3的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                        opt3 = wv_option3.getCurrentItem() >= mOptions3Items.get(opt1Select).get(index).size() - 1 ?
                                mOptions3Items.get(opt1Select).get(index).size() - 1 : wv_option3.getCurrentItem();
                    }
                    wv_option3.setAdapter(new ArrayWheelAdapter(mOptions3Items.get(wv_option1.getCurrentItem()).get(index)));
                    wv_option3.setCurrentItem(opt3);

                    //3级联动数据实时回调
                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), index, opt3, 0);
                    }
                } else {//只有2级联动数据，滑动第2项回调
                    if (optionsSelectChangeListener != null) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), index, 0, 0);
                    }
                }
            }
        };

        // 添加联动监听
        if (options1Items != null && linkage) {
            wv_option1.setOnItemSelectedListener(wheelListener_option1);
        }
        if (options2Items != null && linkage) {
            wv_option2.setOnItemSelectedListener(wheelListener_option2);
        }
        if (options3Items != null && linkage && optionsSelectChangeListener != null) {
            wv_option3.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), wv_option2.getCurrentItem(), index, 0);
                }
            });
        }
    }


    //不联动情况下
    public void setNPicker(List<T> options1Items) {
        setNPicker(options1Items, null, null, null, null, null, null);
    }

    public void setNPicker(List<T> options1Items, List<T> options2Items) {
        setNPicker(options1Items, options2Items, null, null, null, null, null);
    }

    public void setNPicker(List<T> options1Items, List<T> options2Items, String text1str) {
        setNPicker(options1Items, options2Items, null, null, text1str, null, null);
    }

    public void setNPicker(List<T> options1Items, List<T> options2Items, List<T> options3Items, List<T> options4Items, String text1str, String text2str, String text3str) {
        this.mOptions1Item = options1Items;
        this.mOptions2Item = options2Items;
        this.mOptions3Item = options3Items;
        this.mOptions4Item = options4Items;
        // 选项1
        wv_option1.setAdapter(new ArrayWheelAdapter<>(options1Items));// 设置显示数据
        wv_option1.setCurrentItem(0);// 初始化时显示的数据
        // 选项2
        if (options2Items != null) {
            wv_option2.setAdapter(new ArrayWheelAdapter<>(options2Items));// 设置显示数据
        }
        wv_option2.setCurrentItem(wv_option2.getCurrentItem());// 初始化时显示的数据
        // 选项3
        if (options3Items != null) {
            wv_option3.setAdapter(new ArrayWheelAdapter<>(options3Items));// 设置显示数据
        }
        // 选项4
        if (options4Items != null) {
            wv_option4.setAdapter(new ArrayWheelAdapter<>(options4Items));// 设置显示数据
        }
        wv_option3.setCurrentItem(wv_option3.getCurrentItem());
        wv_option1.setIsOptions(true);
        wv_option2.setIsOptions(true);
        wv_option3.setIsOptions(true);
        wv_option4.setIsOptions(true);

        if (optionsSelectChangeListener != null) {
            wv_option1.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    optionsSelectChangeListener.onOptionsSelectChanged(index, wv_option2.getCurrentItem(), wv_option3.getCurrentItem(), wv_option4.getCurrentItem());
                }
            });
        }

        if (options2Items == null) {
            wv_option2.setVisibility(View.GONE);
        } else {
            wv_option2.setVisibility(View.VISIBLE);
            if (optionsSelectChangeListener != null) {
                wv_option2.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), index, wv_option3.getCurrentItem(), wv_option4.getCurrentItem());
                    }
                });
            }
        }
        if (options3Items == null) {
            wv_option3.setVisibility(View.GONE);
        } else {
            wv_option3.setVisibility(View.VISIBLE);
            if (optionsSelectChangeListener != null) {
                wv_option3.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), wv_option2.getCurrentItem(), index, wv_option4.getCurrentItem());
                    }
                });
            }
        }
        if (options3Items == null) {
            wv_option4.setVisibility(View.GONE);
        } else {
            wv_option4.setVisibility(View.VISIBLE);
            if (optionsSelectChangeListener != null) {
                wv_option4.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        optionsSelectChangeListener.onOptionsSelectChanged(wv_option1.getCurrentItem(), wv_option2.getCurrentItem(), wv_option3.getCurrentItem(), index);
                    }
                });
            }
        }
        if (text1str == null || text1tv == null) {
            text1tv.setVisibility(View.GONE);
        } else {
            text1tv.setText(text1str);
            text1tv.setVisibility(View.VISIBLE);
        }
        if (text2str == null || text2tv == null) {
            text2tv.setVisibility(View.GONE);
        } else {
            text2tv.setText(text2str);
            text2tv.setVisibility(View.VISIBLE);
        }
        if (text3str == null || text3tv == null) {
            text3tv.setVisibility(View.GONE);
        } else {
            text3tv.setText(text3str);
            text3tv.setVisibility(View.VISIBLE);
        }
    }

    public void setTextContentSize(int textSize) {
        wv_option1.setTextSize(textSize);
        wv_option2.setTextSize(textSize);
        wv_option3.setTextSize(textSize);
        if (wv_option4 != null)
            wv_option4.setTextSize(textSize);
    }

    private void setLineSpacingMultiplier() {

    }

    /**
     * 设置选项的单位
     *
     * @param label1 单位
     * @param label2 单位
     * @param label3 单位
     */
    public void setLabels(String label1, String label2, String label3, String label4) {
        if (label1 != null) {
            wv_option1.setLabel(label1);
        }
        if (label2 != null) {
            wv_option2.setLabel(label2);
        }
        if (label3 != null) {
            wv_option3.setLabel(label3);
        }
        if (label4 != null) {
            wv_option4.setLabel(label4);
        }
    }

    /**
     * 设置x轴偏移量
     */
    public void setTextXOffset(int x_offset_one, int x_offset_two, int x_offset_three, int x_offset_fourth) {
        wv_option1.setTextXOffset(x_offset_one);
        wv_option2.setTextXOffset(x_offset_two);
        wv_option3.setTextXOffset(x_offset_three);
        if (wv_option4 != null)
            wv_option4.setTextXOffset(x_offset_fourth);
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic 是否循环
     */
    public void setCyclic(boolean cyclic) {
        wv_option1.setCyclic(cyclic);
        wv_option2.setCyclic(cyclic);
        wv_option3.setCyclic(cyclic);
        if (wv_option4 != null)
            wv_option4.setCyclic(cyclic);
    }

    /**
     * 设置字体样式
     *
     * @param font 系统提供的几种样式
     */
    public void setTypeface(Typeface font) {
        wv_option1.setTypeface(font);
        wv_option2.setTypeface(font);
        wv_option3.setTypeface(font);
        if (wv_option4 != null)
            wv_option4.setTypeface(font);
    }

    /**
     * 分别设置第一二三级是否循环滚动
     *
     * @param cyclic1,cyclic2,cyclic3 是否循环
     */
    public void setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3, boolean cyclic4) {
        wv_option1.setCyclic(cyclic1);
        wv_option2.setCyclic(cyclic2);
        wv_option3.setCyclic(cyclic3);
        if (wv_option4 != null)
            wv_option4.setCyclic(cyclic4);
    }

    /**
     * 返回当前选中的结果对应的位置数组 因为支持三级联动效果，分三个级别索引，0，1，2。
     * 在快速滑动未停止时，点击确定按钮，会进行判断，如果匹配数据越界，则设为0，防止index出错导致崩溃。
     *
     * @return 索引数组
     */
    public int[] getCurrentItems() {
        int[] currentItems = new int[4];
        currentItems[0] = wv_option1.getCurrentItem();

        if (mOptions2Items != null && mOptions2Items.size() > 0) {//非空判断
            currentItems[1] = wv_option2.getCurrentItem() > (mOptions2Items.get(currentItems[0]).size() - 1) ? 0 : wv_option2.getCurrentItem();
        } else {
            currentItems[1] = wv_option2.getCurrentItem();
        }

        if (mOptions3Items != null && mOptions3Items.size() > 0) {//非空判断
            currentItems[2] = wv_option3.getCurrentItem() > (mOptions3Items.get(currentItems[0]).get(currentItems[1]).size() - 1) ? 0 : wv_option3.getCurrentItem();
        } else {
            currentItems[2] = wv_option3.getCurrentItem();
        }
        if (mOptions4Items != null && mOptions3Items.size() > 0) {//非空判断
            currentItems[3] = wv_option4.getCurrentItem() > (mOptions4Items.get(currentItems[0]).get(currentItems[2]).size() - 1) ? 0 : wv_option4.getCurrentItem();
        } else {
            currentItems[3] = wv_option4.getCurrentItem();
        }
        return currentItems;
    }

    public void setCurrentItems(int option1, int option2, int option3, int option4) {
        if (linkage) {
            itemSelected(option1, option2, option3, option4);
        } else {
            wv_option1.setCurrentItem(option1);
            wv_option2.setCurrentItem(option2);
            wv_option3.setCurrentItem(option3);
            wv_option4.setCurrentItem(option4);
        }
    }

    public void setCurrentItems(String options1, String options2, String options3, String options4) {
        if (linkage) {
            int option1 = 0;
            int option2 = 0;
            int option3 = 0;
            int option4 = 0;
            try {
                for (int i = 0; i < Legth(mOptions1Items); i++) {
                    if (("" + options1).equals(mOptions1Items.get(i) + "")) {
                        option1 = i;
                        if (options2 != null && Legth(mOptions2Items) > i) {
                            for (int j = 0; j < Legth(mOptions2Items.get(i)); j++) {
                                if (("" + options2).equals(mOptions2Items.get(i).get(j) + "")) {
                                    option2 = j;
                                    if (options3 != null && Legth(mOptions3Items) > 0) {
                                        for (int k = 0; k < Legth(mOptions3Items.get(i).get(j)); k++) {
                                            if (("" + options3).equals(mOptions3Items.get(i).get(j).get(k) + "")) {
                                                option3 = k;
                                                if (options4 != null && Legth(mOptions4Items) > 0) {
                                                    for (int h = 0; h < Legth(mOptions4Items.get(i).get(j).get(k)); h++) {
                                                        if (("" + options4).equals(mOptions4Items.get(i).get(j).get(k).get(h) + "")) {
                                                            option4 = h;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
            itemSelected(option1, option2, option3, option4);
        } else {
            if (options1 != null) {
                for (int i = 0; i < Legth(mOptions1Item); i++) {
                    if (("" + options1).equals(mOptions1Item.get(i) + "")) {
                        wv_option1.setCurrentItem(i);
                        break;
                    }
                }
            }

            if (options2 != null) {
                for (int i = 0; i < Legth(mOptions2Item); i++) {
                    if (("" + options2).equals(mOptions2Item.get(i) + "")) {
                        wv_option2.setCurrentItem(i);
                        break;
                    }

                }
            }

            if (options3 != null) {
                for (int i = 0; i < Legth(mOptions3Item); i++) {
                    if (("" + options3).equals(mOptions3Item.get(i) + "")) {
                        wv_option3.setCurrentItem(i);
                        break;
                    }

                }
            }

            if (options4 != null) {
                for (int i = 0; i < Legth(mOptions4Item); i++) {
                    if (("" + options4).equals(mOptions4Item.get(i) + "")) {
                        wv_option4.setCurrentItem(i);
                        break;
                    }

                }
            }

        }
    }

    public int Legth(Object list) {
        if (list == null)
            return 0;
        else
            return ((List) list).size();
    }

    private void itemSelected(int opt1Select, int opt2Select, int opt3Select) {
        if (mOptions1Items != null) {
            wv_option1.setCurrentItem(opt1Select);
        }
        if (mOptions2Items != null) {
            wv_option2.setAdapter(new ArrayWheelAdapter(mOptions2Items.get(opt1Select)));
            wv_option2.setCurrentItem(opt2Select);
        }
        if (mOptions3Items != null) {
            wv_option3.setAdapter(new ArrayWheelAdapter(mOptions3Items.get(opt1Select).get(opt2Select)));
            wv_option3.setCurrentItem(opt3Select);
        }
    }

    private void itemSelected(int opt1Select, int opt2Select, int opt3Select, int opt4Select) {
        if (mOptions1Items != null) {
            wv_option1.setCurrentItem(opt1Select);
        }
        if (mOptions2Items != null) {
            wv_option2.setAdapter(new ArrayWheelAdapter(mOptions2Items.get(opt1Select)));
            wv_option2.setCurrentItem(opt2Select);
        }
        if (mOptions3Items != null) {
            wv_option3.setAdapter(new ArrayWheelAdapter(mOptions3Items.get(opt1Select).get(opt2Select)));
            wv_option3.setCurrentItem(opt3Select);
        }
        if (mOptions4Items != null) {
            wv_option4.setAdapter(new ArrayWheelAdapter(mOptions4Items.get(opt1Select).get(opt2Select).get(opt3Select)));
            wv_option4.setCurrentItem(opt4Select);
        }
    }

    /**
     * 设置间距倍数,但是只能在1.2-4.0f之间
     *
     * @param lineSpacingMultiplier
     */
    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        wv_option1.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_option2.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_option3.setLineSpacingMultiplier(lineSpacingMultiplier);
        if (wv_option4 != null)
            wv_option4.setLineSpacingMultiplier(lineSpacingMultiplier);
    }

    /**
     * 设置分割线的颜色
     *
     * @param dividerColor
     */
    public void setDividerColor(int dividerColor) {
        wv_option1.setDividerColor(dividerColor);
        wv_option2.setDividerColor(dividerColor);
        wv_option3.setDividerColor(dividerColor);
        if (wv_option4 != null)
            wv_option4.setDividerColor(dividerColor);
    }

    /**
     * 设置分割线的类型
     *
     * @param dividerType
     */
    public void setDividerType(WheelView.DividerType dividerType) {
        wv_option1.setDividerType(dividerType);
        wv_option2.setDividerType(dividerType);
        wv_option3.setDividerType(dividerType);
        if (wv_option4 != null)
            wv_option4.setDividerType(dividerType);
    }

    /**
     * 设置分割线之间的文字的颜色
     *
     * @param textColorCenter
     */
    public void setTextColorCenter(int textColorCenter) {
        wv_option1.setTextColorCenter(textColorCenter);
        wv_option2.setTextColorCenter(textColorCenter);
        wv_option3.setTextColorCenter(textColorCenter);
        if (wv_option4 != null)
            wv_option4.setTextColorCenter(textColorCenter);
    }

    /**
     * 设置分割线以外文字的颜色
     *
     * @param textColorOut
     */
    public void setTextColorOut(int textColorOut) {
        wv_option1.setTextColorOut(textColorOut);
        wv_option2.setTextColorOut(textColorOut);
        wv_option3.setTextColorOut(textColorOut);
        if (wv_option4 != null)
            wv_option4.setTextColorOut(textColorOut);
    }

    /**
     * Label 是否只显示中间选中项的
     *
     * @param isCenterLabel
     */
    public void isCenterLabel(boolean isCenterLabel) {
        wv_option1.isCenterLabel(isCenterLabel);
        wv_option2.isCenterLabel(isCenterLabel);
        wv_option3.isCenterLabel(isCenterLabel);
        if (wv_option4 != null)
            wv_option4.isCenterLabel(isCenterLabel);
    }

    public void setOptionsSelectChangeListener(OnOptionsSelectChangeListener optionsSelectChangeListener) {
        this.optionsSelectChangeListener = optionsSelectChangeListener;
    }

    public void setLinkage(boolean linkage) {
        this.linkage = linkage;
    }

    /**
     * 设置最大可见数目
     *
     * @param itemsVisible 建议设置为 3 ~ 9之间。
     */
    public void setItemsVisible(int itemsVisible) {
        wv_option1.setItemsVisibleCount(itemsVisible);
        wv_option2.setItemsVisibleCount(itemsVisible);
        wv_option3.setItemsVisibleCount(itemsVisible);
        if (wv_option4 != null)
            wv_option4.setItemsVisibleCount(itemsVisible);
    }

    public void setAlphaGradient(boolean isAlphaGradient) {
        wv_option1.setAlphaGradient(isAlphaGradient);
        wv_option2.setAlphaGradient(isAlphaGradient);
        wv_option3.setAlphaGradient(isAlphaGradient);
        if (wv_option4 != null)
            wv_option4.setAlphaGradient(isAlphaGradient);
    }
}
