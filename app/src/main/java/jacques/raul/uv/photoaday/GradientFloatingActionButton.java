package jacques.raul.uv.photoaday;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GradientFloatingActionButton extends FloatingActionButton {

    private Paint mBackgroundPaint;

    public GradientFloatingActionButton(@NonNull Context context) {
        super(context);
        init();
    }

    public GradientFloatingActionButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GradientFloatingActionButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(),
                0xFFFFFFFF, 0xFFEEF3D2, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mBackgroundPaint);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBackgroundPaint.setShader(new LinearGradient(0, 0, getWidth(), getHeight(),
                0xFFFFFFFF, 0xFFEEF3D2, Shader.TileMode.CLAMP));
    }
}

