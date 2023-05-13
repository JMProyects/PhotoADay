package jacques.raul.uv.photoaday;

import android.graphics.Canvas;
import android.util.AttributeSet;

import android.graphics.Path;

import android.content.Context;

public class CircleImageView extends androidx.appcompat.widget.AppCompatImageView {
    private Path path;

    public CircleImageView(Context context) {
        super(context);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float radius = Math.min(getWidth() / 2, getHeight() / 2);
        path.addCircle(getWidth() / 2, getHeight() / 2, radius, Path.Direction.CCW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
