package ua.org.cofriends.reades.ui.read;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.utils.BusUtils;
import ua.org.cofriends.reades.utils.PageSplitter;

public class PageView extends TextView {

    @InjectView(R.id.text_page)
    TextView textPage;

    private final int touchSlop;

    /**
     * Text of the page.
     */
    private String text;
    /**
     * Touch start coordinates.
     */
    private float downX;
    private float downY;
    /**
     * Indicates that move event happened if some may ask.
     */
    private boolean moved;

    public PageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setText(String textValue) {
        this.text = textValue;

        parseWords();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        textPage.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = downX - event.getX();
                float dy = downY - event.getY();
                if (Math.sqrt(dx * dx + dy * dy) > touchSlop) {
                    moved = true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void parseWords() {
        textPage.setText(PageSplitter.splitWords(text));
    }

    /**
     * @return if before this call was move event
     */
    public boolean hasMoved() {
        boolean result = moved;
        moved = false;
        return result;
    }

    public static class WordRequestEvent extends BusUtils.Event<CharSequence> {

        public WordRequestEvent(CharSequence object) {
            super(object);
        }
    }
}