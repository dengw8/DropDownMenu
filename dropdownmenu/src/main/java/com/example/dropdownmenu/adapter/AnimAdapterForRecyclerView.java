package com.example.dropdownmenu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.example.dropdownmenu.R;
import com.example.dropdownmenu.bean.SecondMenuModel;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Classic custom adapter.
 */
public class AnimAdapterForRecyclerView extends RecyclerView.Adapter<AnimAdapterForRecyclerView.ViewHolder> {
    private Context context;
    // The items to display in your RecyclerView
    private List<SecondMenuModel> itemList;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;
    // The first item that to show
    private int startPosition;
    // itemView的点击事件
    private AnimAdapterForRecyclerView.OnItemClickListener click;
    // 根据点击位置进行区分
    private final static int LEFT = 1;
    private final static int RIGHT = 2;
    private final static int MIDDLE = 3;
    // 弹性移动的振幅
    private final static int MAX_SPRING_LENGTH = 18;
    // 动画的持续时间
    private final static int ANIMATATE_DURATION = 300;

    private List<ViewHolder> viewHolders = new ArrayList<>();
    /**
     * Classic ViewHolder.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.name);
        }
    }

    public AnimAdapterForRecyclerView(Context context, List<SecondMenuModel> itemList, int startPosition) {
        this.context = context;
        this.itemList = itemList;
        this.startPosition = startPosition;
    }

    @Override
    public AnimAdapterForRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_adapter_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        viewHolders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTextView.setText(itemList.get(position).getName());
        if(position == 0) {
            holder.mTextView.setBackgroundColor(context.getResources().getColor(R.color.menuBackground));
        }
        if(getItemViewType(position) == LEFT) {
            setAnimationForLeft(holder.itemView, position);
        } else if(getItemViewType(position) == RIGHT){
            setAnimationForRight(holder.itemView, position);
        } else {
            setAnimationForMiddle(holder.itemView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position > startPosition) {
            return RIGHT;
        } else if(position < startPosition){
            return LEFT;
        } else {
            return MIDDLE;
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener click) {
        this.click = click;
    }

    // 从中间向左边播放动画
    private void setAnimationForLeft(View viewToAnimate, int position)
    {
        if(lastPosition < position) {
            lastPosition = position;
            float fromXDelta = 0;
            while(position < startPosition) {
                fromXDelta += fromDpToPx();
                position++;
            }
            // 添加一个Animaton集
            AnimationSet set = new AnimationSet(true);
            // 添加平移动画
            set.addAnimation(getTranslateAnimation(fromXDelta));
            // 添加透明度动画
            set.addAnimation(getAlphaAnimation());
            // 添加伸缩动画
            set.addAnimation(getScaleAnimation());
            // 启动动画
            viewToAnimate.startAnimation(set);
            // 添加一个结尾的具有Spring效果的动画
            addSpringEffect(viewToAnimate, true);
            //更新播放了动画的最大下标
        }
    }
    // 从中间向右边播放的动画
    private void setAnimationForRight(View viewToAnimate, int position)
    {
         if(lastPosition < position && position < 6) {
            lastPosition = position;
            float fromXDelta = 0;
            while(position > startPosition) {
                fromXDelta -= fromDpToPx();
                position--;
            }
            // 添加一个Animaton集
            AnimationSet set = new AnimationSet(true);
            // 添加平移动画
            set.addAnimation(getTranslateAnimation(fromXDelta));
            // 添加透明度动画
            set.addAnimation(getAlphaAnimation());
            // 添加伸缩动画
            set.addAnimation(getScaleAnimation());
            // 启动动画
            viewToAnimate.startAnimation(set);
            // 添加一个结尾的具有Spring效果的动画
            addSpringEffect(viewToAnimate, false);
            //更新播放了动画的最大下标
        }
    }
    // 位于startPosition位置的item的动画
    private void setAnimationForMiddle(View viewToAnimate) {
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(getAlphaAnimation());
        set.addAnimation(getScaleAnimation());
        viewToAnimate.startAnimation(set);
    }
    /*
     * 将dp = 60的值转化为对应px值
     */
    private float fromDpToPx() {
        final float scale = context.getResources().getDisplayMetrics().density;
        return 60 * scale + 0.5f;
    }
    /*
     * 可以自己指定fromXDelta参数的平移动画
     *  @param fromXDelta
     */
    private TranslateAnimation getTranslateAnimation(float fromXDelta) {
        TranslateAnimation animation = new TranslateAnimation(fromXDelta, 0.0f, 0.0f, 0.0f);
        animation.setDuration(ANIMATATE_DURATION);
        return animation;
    }
    /*
     * 特定参数的透明度动画
     */
    private AlphaAnimation getAlphaAnimation() {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(ANIMATATE_DURATION);
        return animation;
    }
    /*
     * 特定参数的伸缩动画
     */
    private ScaleAnimation getScaleAnimation() {
        ScaleAnimation animation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, 0.5f, 0.5f);
        animation.setDuration(ANIMATATE_DURATION);
        return animation;
    }
    /*
     * item平移动画结束后的弹簧效果
     * @param 要作用的view
     * @param 标记位，用于判断左右方向（true: 向左， false: 向右）
     */
    private void addSpringEffect(final View viewToAnim, final boolean isLeft) {
        Runnable startSpringAnimation = new Runnable() {
            @Override
            public void run() {
                SpringConfig config = new SpringConfig(250, 25);
                Spring spring = SpringSystem.create().createSpring();
                spring.setSpringConfig(config);
                spring.addListener(new SimpleSpringListener() {
                    @Override
                    public void onSpringUpdate(Spring spring) {
                        float val;
                        if(isLeft) {
                            val = (float) (-MAX_SPRING_LENGTH - spring.getCurrentValue());
                        } else {
                            val = (float) (MAX_SPRING_LENGTH - spring.getCurrentValue());
                        }
                        viewToAnim.setTranslationX(val);
                    }
                });
                if(isLeft) {
                    spring.setEndValue(-MAX_SPRING_LENGTH);
                } else {
                    spring.setEndValue(MAX_SPRING_LENGTH);
                }
            }
        };
        viewToAnim.postDelayed(startSpringAnimation, ANIMATATE_DURATION - 20);
    }

    public void closeCurrentMenu(int firstVisiblePosition, int lastVisiblePosition, int duration) {
        for(int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            setCloseAnimationForItem(i, firstVisiblePosition + startPosition, duration);
        }
    }

    private void setCloseAnimationForItem(int pos, int des, int duration) {
        float toXDelta = 0;
        ViewHolder holder = viewHolders.get(pos);
        if(pos > des) {
            while(pos > des) {
                toXDelta -= fromDpToPx();
                pos--;
            }
        } else {
            while(pos < des) {
                toXDelta += fromDpToPx();
                pos++;
            }
        }
        TranslateAnimation animation = new TranslateAnimation(0, toXDelta, 0.0f, 0.0f);
        animation.setDuration(duration);
        holder.itemView.startAnimation(animation);
    }
}