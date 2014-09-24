package ua.org.cofriends.reades.ui.tools.swipetoremove;

import android.view.animation.Animation;

/**
 * Utility, to avoid having to implement every method in AnimationListener in
 * every implementation class
 */
public class AnimationListenerAdapter implements Animation.AnimationListener {

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }
}