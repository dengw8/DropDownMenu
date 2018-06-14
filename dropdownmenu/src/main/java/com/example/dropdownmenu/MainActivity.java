package com.example.dropdownmenu;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dropdownmenu.adapter.AdapterForRecylerView;
import com.example.dropdownmenu.adapter.AnimAdapterForRecyclerView;
import com.example.dropdownmenu.bean.AnimationModel;
import com.example.dropdownmenu.bean.FirstMenuModel;
import com.example.dropdownmenu.bean.SecondMenuModel;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringChain;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView switcher;
    private ImageButton fixedButton;
    private RecyclerView firstMenu;
    private RecyclerView secondMenu;
    private AdapterForRecylerView firstAdapter;
    private AnimAdapterForRecyclerView secondAdapter;
    private List<FirstMenuModel> firstMenuModelList;
    /*
     * 判断当前浮动菜单是隐藏还是显示
     * true: 隐藏  false: 显示
     */
    private boolean isHidden = true;

    private final static int ANIMATION_DURATION = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * 初始化view
         */
        initViews();
        /*
         * 添加数据
         */
        initData();
        /*
         * 为view 添加相关的clickListener
         */
        setClickListenner();
    }

    private void initViews() {
        switcher = findViewById(R.id.switcher);
        fixedButton = findViewById(R.id.fixedButton);
        firstMenu = findViewById(R.id.menu1);
        secondMenu = findViewById(R.id.menu2);
    }

    private void initData() {
        firstMenuModelList = new ArrayList<>();
        firstMenuModelList.add(new FirstMenuModel("原图", null));
        for (int i = 1; i <= 10; i++) {
            List<SecondMenuModel> secondMenuModelList = new ArrayList<>();
            for (int j = 1; j <= 10; j++) {
                secondMenuModelList.add(new SecondMenuModel("滤镜" + i + "-" + j));
            }
            firstMenuModelList.add(new FirstMenuModel("滤镜" + i, secondMenuModelList));
        }
        firstAdapter = new AdapterForRecylerView(firstMenuModelList);
        setParamsForRecyclerView(firstMenu, firstAdapter);
    }

    private void setClickListenner() {
        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHidden) {
                    isHidden = false;
                    if (firstMenu.getVisibility() == View.VISIBLE) {
                        moveViewSmooth(firstMenu, false);
                    } else {
                        moveViewSmooth(secondMenu, false);
                        moveViewSmooth(fixedButton, false);
                    }
                } else {
                    isHidden = true;
                    if (firstMenu.getVisibility() == View.VISIBLE) {
                        moveViewSmooth(firstMenu, true);
                    } else {
                        moveViewSmooth(secondMenu, true);
                        moveViewSmooth(fixedButton, true);
                    }
                }
            }
        });
        fixedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstMenu.setVisibility(View.VISIBLE);
                firstMenu.startAnimation(getAlphaAnimation(0, 1));
                fixedButton.startAnimation(getAlphaAnimation(1, 0));
                secondAdapter.closeCurrentMenu(getFirstVisibleItemPosition(secondMenu), getLastVisibleItemPosition(secondMenu), ANIMATION_DURATION - 100);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        moveView(secondMenu, true);
                        moveView(fixedButton, true);
                    }
                };
                fixedButton.postDelayed(runnable, ANIMATION_DURATION);
            }
        });
        firstAdapter.setOnItemClickListener(new AdapterForRecylerView.OnItemClickListener() {
            @Override
            public void onItemClick(final int pos) {
                if (pos > 0) {
                    int startPosition = getStartPosition(firstAdapter.getRawX());
                    List<SecondMenuModel> list = new ArrayList<>();
                    list.add(new SecondMenuModel(""));
                    list.addAll(firstMenuModelList.get(pos).getmList());
                    secondAdapter = new AnimAdapterForRecyclerView(getApplicationContext(), list, startPosition);
                    secondAdapter.setOnItemClickListener(new AnimAdapterForRecyclerView.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            if (position > 0) {
                                Toast.makeText(getApplicationContext(), firstMenuModelList.get(pos).getmList().get(position - 1).getName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    setParamsForRecyclerView(secondMenu, secondAdapter);
                    firstMenu.setVisibility(View.INVISIBLE);
                    moveView(secondMenu, false);
                    moveView(fixedButton, false);
                } else {
                    // to do something
                }
            }
        });
    }
    /*
     * 为RecyclerView设置布局水平的方式，以及setAdapter
     * @param1 RecyclerView 对象
     * @param2 继承自RecyclerView.Adapter类的adapter，是自定义Adapter的父类
     */
    private void setParamsForRecyclerView(RecyclerView view, RecyclerView.Adapter adapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        view.setLayoutManager(linearLayoutManager);
        view.setAdapter(adapter);
    }
    /*
     * 使用动画平滑移动view指定的距离
     * @param1 要移动的view
     * @param2 标志位，值为true的时候下移，值为false的时候上移
     */
    private void moveViewSmooth(View view, boolean sign) {
        float from = view.getTranslationY(), to;
        if (sign) {
            to = from + fromDpToPx();
        } else {
            to = from - fromDpToPx();
        }
        ObjectAnimator.ofFloat(view, "translationY", from, to).setDuration(400).start();
    }
    /*
     * 修改位置参数瞬移 view 指定的距离
     * @param 要移动的view
     * @param 标志位，值为true的时候下移，值为false的时候上移
     */
    private void moveView(View view, boolean sign) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (sign) {
            params.topMargin += fromDpToPx();
        } else {
            params.topMargin -= fromDpToPx();
        }
        view.requestLayout();
    }
    /*
     * 将dp = 60的值转化为对应px值
     */
    private float fromDpToPx() {
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        return 60 * scale + 0.5f;
    }
    /*
     * 获取二级菜单动画开始的下标
     * @param 所点击一级菜单item的横坐标
     */
    private int getStartPosition(float rawX) {
        int n = 0;
        while ((rawX - fromDpToPx()) >= 0.0001) {
            n++;
            rawX -= fromDpToPx();
        }
        return n;
    }
    /*
     * 获取secondMenu可见item范围的下值
     */
    private int getFirstVisibleItemPosition(RecyclerView view) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) view.getLayoutManager();
        return layoutManager.findFirstVisibleItemPosition();
    }
    /*
     * 获取secondMenu可见item范围的上值
     */
    private int getLastVisibleItemPosition(RecyclerView view) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) view.getLayoutManager();
        return layoutManager.findLastVisibleItemPosition();
    }

    private AlphaAnimation getAlphaAnimation(float from, float to) {
        AlphaAnimation animation = new AlphaAnimation(from,to);
        animation.setDuration(ANIMATION_DURATION);
        return animation;
    }
}

