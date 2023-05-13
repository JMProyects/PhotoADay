package jacques.raul.uv.photoaday;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.cardview.widget.CardView;

public class GradientItemList extends CardView {

    private Paint mBackgroundPaint;

    public GradientItemList(Context context) {
        super(context);
        init();
    }

    public GradientItemList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GradientItemList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(),
                0xFFEEF3D2, 0xFF8DE8FF, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(getPaddingLeft(), getPaddingTop(),
                getWidth() - getPaddingRight(), getHeight() - getPaddingBottom(), mBackgroundPaint);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBackgroundPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(),
                0xFFEEF3D2, 0xFF8DE8FF, Shader.TileMode.CLAMP));
    }
}





