package net.oldervoll.flightschedule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

public class CompassView extends View {

    private static final float STROKE_WIDTH = 3;

    private Paint paint;
    private Path path;
    private RectF largeCircle;
    private RectF smallCircle;

    private float degrees;

    public CompassView(Context context) {
        this(context, null, 0);
    }

    public CompassView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        degrees = 0;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(STROKE_WIDTH * getResources().getDisplayMetrics().density);
        path = new Path();
        largeCircle = new RectF();
        smallCircle = new RectF();
        setWillNotDraw(false);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.getText().add(String.format(getContext().getString(R.string.format_direction), (int) degrees));
        return true;
    }

    public void setDegrees(float degrees) {
        this.degrees = degrees;
        AccessibilityManager accessibilityManager =
                (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager.isEnabled()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final float halfWidth = w * .5f;
        final float halfHeight = h * .5f;
        largeCircle.set(w * .30f, h * .30f, w * .70f, h * .70f);
        smallCircle.set(w * .47f, h * .47f, w * .53f, h * .53f);
        path.moveTo(halfWidth, 0);
        path.lineTo(w * .7f, halfHeight);
        path.lineTo(w * .3f, halfHeight);
        path.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Rotate to degrees entered.
        canvas.rotate(degrees, getWidth() * .5f, getHeight() * .5f);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.flight_secondary));
        // Draw the red triangle.
        canvas.drawPath(path, paint);
        canvas.save();
        // Rotate 180 degrees to draw the blue triangle.
        canvas.rotate(180, getWidth() * .5f, getHeight() * .5f);
        paint.setColor(getResources().getColor(R.color.flight_background));
        canvas.drawPath(path, paint);
        // Restore canvas transformation before save() method. 
        canvas.restore();
        paint.setColor(Color.WHITE);
        // Draw the background of white large circle.
        canvas.drawOval(largeCircle, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        // Draw the contour of white large circle.
        canvas.drawOval(largeCircle, paint);
        // Draw the small circle.
        canvas.drawOval(smallCircle, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}