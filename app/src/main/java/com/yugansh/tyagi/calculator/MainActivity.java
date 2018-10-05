package com.yugansh.tyagi.calculator;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yugansh.tyagi.calculator.databinding.ActivityMainBinding;
import com.yugansh.tyagi.calculator.databinding.CalcItemFormulasBinding;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;


public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    private String textToFormat = "";
    private ActionListener actionListener;
    private NumberListener numberListener;
    FrameLayout container;

    private boolean isOperator = false;

    //Button Id's
    int numButtons[] = {R.id.btn_one, R.id.btn_two, R.id.btn_three, R.id.btn_four,
            R.id.btn_five, R.id.btn_six, R.id.btn_seven, R.id.btn_eight,
            R.id.btn_nine, R.id.btn_zero};
    int actionButtons[] = {R.id.btn_clear, R.id.btn_deg_rad, R.id.brackets,
            R.id.btn_divide, R.id.btn_multiply, R.id.btn_minus,
            R.id.btn_plus, R.id.btn_equals};

    CalcItemFormulasBinding formulasBinding;
    ActivityMainBinding mainBinding;

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        init();

    }

    private void init() {
        actionListener = new ActionListener();
        numberListener = new NumberListener();

        viewPager = findViewById(R.id.viewpager);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(24, 24, 24, 24);
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setPageTransformer(true, new CardTransformer());
    }

    private void evaluate() throws Exception {
        Expression expression = new ExpressionBuilder(textToFormat).build();
        try {
            double result = expression.evaluate();
            mainBinding.answerTv.setText(String.valueOf((int) result));

            //lastDot = true; // Result contains a dot
        } catch (ArithmeticException e) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        private int resId;

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) container.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch (position) {
                case 0:
                    resId = R.layout.calc_item_main;
                    break;
                case 1:
                    resId = R.layout.calc_item_formulas;
                    break;
            }

            if (resId == R.layout.calc_item_main) {
                View view = inflater.inflate(resId, container, false);
                container.addView(view);

                setUpNumberButtonsListener();
                setUpActionButtonsListener();

                return view;
            } else if (resId == R.layout.calc_item_formulas) {
                formulasBinding = DataBindingUtil.inflate(inflater, resId, container, false);

                formulasBinding.btnXSquare
                        .setText(Html.fromHtml("x<sup><small>2</small></sup>"));
                formulasBinding.btnXPower
                        .setText(Html.fromHtml("x<sup><small>y</small></sup>"));
                formulasBinding.btnEPower
                        .setText(Html.fromHtml("e<sup><small>x</small></sup>"));
                formulasBinding.btnTenPower
                        .setText(Html.fromHtml("10<sup><small>x</small></sup>"));
                formulasBinding.btnLogBaseTen
                        .setText(Html.fromHtml("log<sub><small>10</small></sub>"));
                formulasBinding.btnSquareRoot
                        .setText(Html.fromHtml("&#8730;<small>2</small>"));
                formulasBinding.btnCubeRoot
                        .setText(Html.fromHtml("&#8730;<small>3</small>"));
                formulasBinding.btnShowMore
                        .setText(Html.fromHtml("&#8226;&#8226;&#8226;"));
                formulasBinding.btnShowMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int x = mainBinding.viewpager.getLeft() + 130;
                        int y = mainBinding.viewpager.getTop() + 130;

                        int startRadius = 0;
                        int endRadius = (int) Math.hypot(mainBinding.viewpager.getWidth() - 200,
                                mainBinding.viewpager.getHeight() - 200);

                        Animator anim = ViewAnimationUtils.createCircularReveal
                                (formulasBinding.moreFormulas, x, y, startRadius, endRadius);

                        formulasBinding.moreFormulas.setVisibility(View.VISIBLE);
                        anim.start();
                    }
                });

                formulasBinding.btnShowMoreM.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int x = mainBinding.viewpager.getLeft() + 130;
                        int y = mainBinding.viewpager.getTop() + 130;

                        int startRadius = 0;
                        int endRadius = (int) Math.hypot(mainBinding.viewpager.getWidth() - 200,
                                mainBinding.viewpager.getHeight() - 200);

                        Animator anim = ViewAnimationUtils.createCircularReveal
                                (formulasBinding.moreFormulas, x, y, endRadius, startRadius);

                        anim.start();
                        anim.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                formulasBinding.moreFormulas.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                    }
                });


                container.addView(formulasBinding.getRoot());
                return formulasBinding.getRoot();
            }
            return null;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        private void setUpNumberButtonsListener() {
            for (int numButton : numButtons) {
                TextView textView = findViewById(numButton);
                textView.setOnClickListener(numberListener);
            }
        }

        private void setUpActionButtonsListener() {
            for (int actionButton : actionButtons) {
                TextView textView = findViewById(actionButton);
                textView.setOnClickListener(actionListener);
                textView.setOnLongClickListener(actionListener);
            }
        }
    }

    private class ActionListener implements View.OnClickListener, View.OnLongClickListener {

        @Override
        public void onClick(View v) {
            int actionId = v.getId();
            switch (actionId) {
                case R.id.btn_clear:
                    if (textToFormat.length() > 1) {
                        textToFormat = textToFormat.substring(0, textToFormat.length() - 1);
                        mainBinding.expressionTv.setText(textToFormat);

                    } else {
                        textToFormat = "";
                        mainBinding.expressionTv.setText(textToFormat);
                    }
                    break;
                case R.id.btn_plus:
                    textToFormat = textToFormat + "+";
                    mainBinding.expressionTv.setText(textToFormat);
                    isOperator = true;
                    break;
                case R.id.btn_minus:
                    textToFormat = textToFormat + "-";
                    mainBinding.expressionTv.setText(textToFormat);
                    isOperator = true;
                    break;
                case R.id.btn_multiply:
                    textToFormat = textToFormat + "*";
                    mainBinding.expressionTv.setText(textToFormat);
                    isOperator = true;
                    break;
                case R.id.btn_divide:
                    textToFormat = textToFormat + "/";
                    mainBinding.expressionTv.setText(textToFormat);
                    isOperator = true;
                    break;
                case R.id.btn_equals:
                    //region Expression Animate Sequence
                    mainBinding.expressionTv.animate()
                            .translationY(-200f)
                            .translationX(80f)
                            .scaleY(0.7f)
                            .scaleX(0.7f)
                            .alpha(0.9f)
                            .setDuration(300)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    mainBinding.answerTv.setVisibility(View.VISIBLE);
                                    mainBinding.answerTv.animate()
                                            .alpha(1f)
                                            .setDuration(700)
                                            .setInterpolator(new LinearInterpolator());
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            })
                            .setInterpolator(new AccelerateDecelerateInterpolator());
                    //endregion
                    try {
                        evaluate();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (v.getId() == R.id.btn_clear) {
                textToFormat = "";
                mainBinding.expressionTv.setText(textToFormat);
                mainBinding.answerTv.setText("");
                mainBinding.expressionTv.animate()
                        .translationY(0f)
                        .translationX(0f)
                        .scaleY(1f)
                        .scaleX(1f)
                        .alpha(1f)
                        .setDuration(100);
                mainBinding.answerTv.setVisibility(View.INVISIBLE);
                return true;
            }
            return false;
        }
    }

    private class NumberListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String btnText = ((TextView) view).getText().toString();
            try {
                if (!isOperator) {
                    textToFormat = textToFormat + btnText;
                    Log.d("text to format ", textToFormat);
                    isOperator = false;
                    mainBinding.expressionTv.setText(textToFormat);
                } else {
                    textToFormat = textToFormat + btnText;
                    mainBinding.expressionTv.setText(textToFormat);
                }

            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), "Expression too long", Toast.LENGTH_SHORT).show();
                textToFormat = textToFormat.substring(0, textToFormat.length() - 1);
            }
            mainBinding.horizontalScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mainBinding.horizontalScrollView.fullScroll(View.FOCUS_RIGHT);
                }
            });
        }
    }
}
