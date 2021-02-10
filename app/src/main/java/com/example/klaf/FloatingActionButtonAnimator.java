package com.example.klaf;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FloatingActionButtonAnimator implements View.OnClickListener {
    private final FloatingActionButton buttonAdd;
    private final FloatingActionButton buttonEdit;
    private final FloatingActionButton button3;
    private boolean clicked;
    private OnClickHelper onClickHelper;

    public FloatingActionButtonAnimator(FloatingActionButton buttonAdd,
                                        FloatingActionButton buttonEdit,
                                        FloatingActionButton button3) {
        this.buttonAdd = buttonAdd;
        this.buttonEdit = buttonEdit;
        this.button3 = button3;
        clicked = false;
    }

    public interface OnClickHelper {
        void onClick();
    }

    public void setOnClickHelper(OnClickHelper onClickHelper) {
        this.onClickHelper = onClickHelper;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    @Override
    public void onClick(View v) {
        clicked = !clicked;
        setVisibility();
        setAnimation(v);
        setClickable();
        if (onClickHelper != null) {
            onClickHelper.onClick();
        }
    }
    private void setAnimation(View v) {
        Animation click = AnimationUtils.loadAnimation(v.getContext(), R.anim.click_anim);
        Animation openButtonAdd = AnimationUtils.loadAnimation(v.getContext(), R.anim.open_button_add_anim);
        Animation closeButtonAdd = AnimationUtils.loadAnimation(v.getContext(), R.anim.close_button_add_anim);
        Animation openButtonEdit = AnimationUtils.loadAnimation(v.getContext(), R.anim.open_button_edit_anim);
        Animation closeButtonEdit = AnimationUtils.loadAnimation(v.getContext(), R.anim.close_button_edit_anim);
        Animation open3 = AnimationUtils.loadAnimation(v.getContext(), R.anim.open_button_delete_anim);
        Animation close3 = AnimationUtils.loadAnimation(v.getContext(), R.anim.close_button_delete_anim);
        v.startAnimation(click);
        if (clicked) {
            buttonAdd.startAnimation(openButtonAdd);
            buttonEdit.startAnimation(openButtonEdit);
            button3.startAnimation(open3);
        } else {
            buttonAdd.startAnimation(closeButtonAdd);
            buttonEdit.startAnimation(closeButtonEdit);
            button3.startAnimation(close3);
        }
    }

    private void setVisibility() {
        if (clicked) {
            buttonAdd.setVisibility(View.VISIBLE);
            buttonEdit.setVisibility(View.VISIBLE);
            button3.setVisibility(View.VISIBLE);
        } else {
            buttonAdd.setVisibility(View.INVISIBLE);
            buttonEdit.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
        }
    }

    private void setClickable() {
        if (clicked) {
            buttonAdd.setClickable(true);
            buttonEdit.setClickable(true);
            button3.setClickable(true);
        } else {
            buttonAdd.setClickable(false);
            buttonEdit.setClickable(false);
            button3.setClickable(false);
        }
    }
}
