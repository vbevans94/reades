package ua.org.cofriends.reades.ui.dictionaries;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Dictionary;
import ua.org.cofriends.reades.ui.basic.tools.SemiCircleTarget;

public class DictionaryItemView extends RelativeLayout {

    @InjectView(R.id.text_name)
    TextView textName;

    @InjectView(R.id.text_details)
    TextView textDetails;

    @InjectView(R.id.image_from)
    ImageView imageFrom;

    @InjectView(R.id.image_to)
    ImageView imageTo;

    Target targetTo;

    Target targetFrom;

    public DictionaryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        targetFrom = new SemiCircleTarget(imageFrom, SemiCircleTarget.Side.LEFT);
        targetTo = new SemiCircleTarget(imageTo, SemiCircleTarget.Side.RIGHT);
    }


    public void bind(Dictionary item, Picasso picasso, int detailsRes, int size) {
        textDetails.setText(detailsRes);
        textName.setText(item.getName());
        picasso.load(item.getFromLanguage().getImageUrl())
                .resize(size, size)
                .centerCrop()
                .into(targetFrom);

        picasso.load(item.getToLanguage().getImageUrl())
                .resize(size, size)
                .centerCrop()
                .into(targetTo);
    }
}
