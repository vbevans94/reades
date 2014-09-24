package ua.org.cofriends.reades.ui.tools.swipetoremove;

import android.annotation.SuppressLint;
import android.support.v4.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.WrapperListAdapter;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.nineoldandroids.view.animation.AnimatorProxy;

import ua.org.cofriends.reades.ui.tools.UiUtils;
import ua.org.cofriends.reades.utils.EventBusUtils;

public class SwipeToRemoveTouchListener implements View.OnTouchListener {

    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;

    // dynamically changing indicators
    private boolean mSwiping = false;
    private boolean mAnimating = false;
    private boolean mItemPressed = false;
    private float mDownX;

    // containers on which operations are done
    private final LongSparseArray<Integer> mItemIdTopMap = new LongSparseArray<Integer>();
    private final int mSwipeSlop;
    private final ListView mListView;
    private final BackgroundContainer mBackgroundContainer;

    SwipeToRemoveTouchListener(ListView listView, BackgroundContainer backgroundContainer) {
        mListView = listView;
        mBackgroundContainer = backgroundContainer;
        mSwipeSlop = ViewConfiguration.get(listView.getContext()).getScaledTouchSlop();
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mItemPressed || mAnimating) {
                    // Multi-item swipes not handled
                    return false;
                }
                mItemPressed = true;
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_CANCEL: {
                ViewHelper.setTranslationX(v, 0);
                ViewHelper.setAlpha(v, 1);
                mItemPressed = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mAnimating) {
                    return true;
                }
                float x = event.getX();
                if (!AnimatorProxy.NEEDS_PROXY) {
                    x += v.getTranslationX();
                }
                float deltaX = x - mDownX;
                float deltaXAbs = Math.abs(deltaX);
                if (!mSwiping) {
                    if (deltaXAbs > mSwipeSlop) {
                        mSwiping = true;
                        mListView.requestDisallowInterceptTouchEvent(true);
                        mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                    }
                }
                if (mSwiping) {
                    float fraction = Math.abs(deltaX) / v.getWidth();
                    ViewHelper.setTranslationX(v, deltaX);
                    ViewHelper.setAlpha(v, 1 - fraction);
                }
            }
            break;
            case MotionEvent.ACTION_UP: {
                if (mAnimating) {
                    return true;
                }
                // User let go - figure out whether to animate the view out, or back into place
                if (mSwiping) {
                    float x = event.getX();
                    if (!AnimatorProxy.NEEDS_PROXY) {
                        x += v.getTranslationX();
                    }
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    float fractionCovered;
                    float endX;
                    final boolean remove;
                    if (deltaXAbs > v.getWidth() / 4) {
                        // Greater than a quarter of the width - animate it out
                        fractionCovered = deltaXAbs / v.getWidth();
                        endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                        remove = true;
                    } else {
                        // Not far enough - animate it back
                        fractionCovered = 1 - (deltaXAbs / v.getWidth());
                        endX = 0;
                        remove = false;
                    }
                    // Animate position and alpha
                    long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                    animateSwipe(v, endX, duration, remove);
                } else {
                    int position = mListView.getPositionForView(v);
                    mListView.performItemClick(v, position, mListView.getAdapter().getItemId(position));
                }
                mItemPressed = false;
            }
            break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Animates a swipe of the item either back into place or out of the listview container.
     * NOTE: This is a simplified version of swipe behavior, for the purposes of this demo
     * about animation. A real version should use velocity (via the VelocityTracker class)
     * to send the item off or back at an appropriate speed.
     */
    @SuppressLint("NewApi")
    private void animateSwipe(final View view, float endX, long duration, final boolean remove) {
        mAnimating = true;
        mListView.setEnabled(false);
        ViewPropertyAnimator.animate(view).setDuration(duration)
                .alpha(remove ? 0 : 1)
                .translationX(endX)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Restore animated values
                        view.setAlpha(1);
                        view.setTranslationX(0);
                        if (remove) {
                            animateOtherViews(mListView, view);
                        } else {
                            mBackgroundContainer.hideBackground();
                            mSwiping = false;
                            mAnimating = false;
                            mListView.setEnabled(true);
                        }
                    }
                })
                .start();
    }

    /**
     * This method animates all other views in the ListView container (not including ignoreView)
     * into their final positions. It is called after ignoreView has been removed from the
     * adapter, but before layout has been run. The approach here is to figure out where
     * everything is now, then allow layout to run, then figure out where everything is after
     * layout, and then to run animations between all of those start/end positions.
     */
    private void animateOtherViews(final ListView listview, View viewToRemove) {
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        for (int i = 0; i < listview.getChildCount(); ++i) {
            View child = listview.getChildAt(i);
            int position = firstVisiblePosition + i;
            long itemId = mListView.getAdapter().getItemId(position);
            if (child != viewToRemove) {
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }

        int position = mListView.getPositionForView(viewToRemove);
        // tell the world about it
        EventBusUtils.getBus().post(new RemoveEvent(mListView.getItemAtPosition(position)));

        // Delete the item from the adapter
        ListAdapter listAdapter = mListView.getAdapter();
        ArrayAdapter adapter;
        if (listAdapter instanceof WrapperListAdapter) {
            adapter = (ArrayAdapter) ((WrapperListAdapter) listAdapter).getWrappedAdapter();
        } else {
            adapter = (ArrayAdapter) listAdapter;
        }
        adapter.remove(mListView.getAdapter().getItem(position));

        // After layout runs, capture position of all itemIDs, compare to pre-layout
        // positions, and animate changes
        final ViewTreeObserver observer = listview.getViewTreeObserver();
        if (observer != null) {
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);
                    boolean firstAnimation = true;
                    int firstVisiblePosition = listview.getFirstVisiblePosition();
                    for (int i = 0; i < listview.getChildCount(); ++i) {
                        final View child = listview.getChildAt(i);
                        int position = firstVisiblePosition + i;
                        long itemId = mListView.getAdapter().getItemId(position);
                        Integer startTop = mItemIdTopMap.get(itemId);
                        int top = child.getTop();
                        if (startTop == null) {
                            // Animate new views along with the others. The catch is that they did not
                            // exist in the start state, so we must calculate their starting position
                            // based on whether they're coming in from the bottom (i > 0) or top.
                            int childHeight = child.getHeight() + listview.getDividerHeight();
                            startTop = top + (i > 0 ? childHeight : -childHeight);
                        }
                        int delta = startTop - top;
                        if (delta != 0) {
                            Runnable endAction = firstAnimation ?
                                    new Runnable() {
                                        public void run() {
                                            mBackgroundContainer.hideBackground();
                                            mSwiping = false;
                                            mAnimating = false;
                                            mListView.setEnabled(true);
                                        }
                                    } :
                                    null;
                            firstAnimation = false;
                            moveView(child, 0, 0, delta, 0, endAction);
                        }
                    }
                    if (firstAnimation) {
                        mBackgroundContainer.hideBackground();
                        mSwiping = false;
                        mAnimating = false;
                        mListView.setEnabled(true);
                    }
                    mItemIdTopMap.clear();
                    return true;
                }
            });
        }
    }

    /**
     * Animate a view between start and end X/Y locations, using either old (pre-3.0) or
     * new animation APIs.
     */
    @SuppressLint("NewApi")
    private void moveView(View view, float startX, float endX, float startY, float endY,
                          final Runnable endAction) {
        if (startX != endX) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(view, UiUtils.TRANSLATION_X, startX, endX)
                    .setDuration(MOVE_DURATION);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (endAction != null) {
                        endAction.run();
                    }
                }
            });
            anim.start();
        }
        if (startY != endY) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(view, UiUtils.TRANSLATION_Y, startY, endY)
                    .setDuration(MOVE_DURATION);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (endAction != null) {
                        endAction.run();
                    }
                }
            });
            anim.start();
        }
    }

    public static class RemoveEvent extends EventBusUtils.Event<Object> {

        public RemoveEvent(Object object) {
            super(object);
        }
    }
}