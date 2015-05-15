package no.bekk.wearworkshop.todoapp;

import android.content.Context;
import android.support.wearable.view.CircledImageView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WearableListItemLayout extends LinearLayout {

    private TextView itemContentView;
    private CircledImageView imageView;


    public WearableListItemLayout(Context context) {
        this(context, null);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imageView = (CircledImageView) findViewById(R.id.image);
        itemContentView = (TextView) findViewById(R.id.text);
    }
}
