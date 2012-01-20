package com.maishoku.android;

import static java.lang.Math.abs;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

// Taken from http://code.google.com/p/android/issues/detail?id=17465

/**
 * The <code>SegmentedRadioButton</code> class is a subclass of
 * {@link RadioButton} that emulates the look of a segmented control in iOS. The
 * initial code for this came from
 * http://blog.bookworm.at/2010/10/segmented-controls-in-android.html. The
 * rendering in {@link @onDraw} is substantially improved from the original
 * version and aims to replicate the look of the segmented control found in the
 * Google Maps Android app.
 */
public class SegmentedRadioButton extends RadioButton {

	private static final int[] GRADIENT_COLORS = new int[] { 0xfffcfcfc,
			0xffe2e2e2, 0xffbebebe, 0xff929292 };
	private static final int[] OUTLINE_GRADIENT_COLORS;

	private float textScaleFactor = 2.0f;

	static {
		// OUTLINE_GRADIENT_COLORS is a darker version of GRADIENT_COLORS; here
		// we convert the colors
		// from GRADIENT_COLORS to HSV so we can modify (darken) the V
		// component.
		OUTLINE_GRADIENT_COLORS = new int[GRADIENT_COLORS.length];
		for (int i = 0; i < GRADIENT_COLORS.length; ++i) {
			float[] hsv = new float[3];
			Color.colorToHSV(GRADIENT_COLORS[i], hsv);
			hsv[2] *= 0.5;
			OUTLINE_GRADIENT_COLORS[i] = Color.HSVToColor(255, hsv);

		}
	}

	public SegmentedRadioButton(Context context) {
		super(context);
	}

	public SegmentedRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public float getTextScaleFactor() {
		return textScaleFactor;
	}

	public void setTextScaleFactor(float textScaleFactor) {
		this.textScaleFactor = textScaleFactor;
	}

	private Shader checkedShader() {
		return new LinearGradient(0f, 0f, 0f, getHeight(), new int[] {
				0xff404040, 0xff828282, 0xffd3d3d3, 0xfff6f6f6 }, new float[] {
				0.0f, 0.19f, 0.66f, 1.0f }, Shader.TileMode.CLAMP);
	}

	private Shader uncheckedShader() {
		return new LinearGradient(0f, 0f, 0f, getHeight(), GRADIENT_COLORS,
				new float[] { 0.0f, 0.40f, 0.60f, 1.0f }, Shader.TileMode.CLAMP);
	}

	private Shader outlineShader() {
		return new LinearGradient(0f, 0f, 0f, getHeight(),
				OUTLINE_GRADIENT_COLORS,
				new float[] { 0.0f, 0.40f, 0.60f, 1.0f }, Shader.TileMode.CLAMP);
	}

	private Shader leftEdgeHilightShader() {
		return new LinearGradient(0f, 0f, 0f, getHeight(), new int[] {
				0xfffcfcfc, 0xfff4f4f4, 0xffefefef, 0xffa2a2a2 }, new float[] {
				0.0f, 0.30f, 0.50f, 1.0f }, Shader.TileMode.CLAMP);
	}

	@Override
	public void onDraw(Canvas canvas) {
		RadioGroup radioGroup = (RadioGroup) getParent();
		int index = (radioGroup != null) ? radioGroup.indexOfChild(this) : 0;

		final float width = getMeasuredWidth();
		final float height = getMeasuredHeight();
		final float radius = 15.0f;
		final float halfStrokeWidth = 0.8f;
		Path outline = new Path();

		if (radioGroup != null && radioGroup.getChildCount() == 1) {
			// Need to draw rounded corners on the left and right.

			// Draw outline, clockwise, starting at top-right corner straight
			// line segment
			outline.moveTo(width - halfStrokeWidth, halfStrokeWidth + radius);
			outline.lineTo(width - halfStrokeWidth, height - radius
					- halfStrokeWidth);
			outline.arcTo(new RectF(width - radius - halfStrokeWidth, height
					- radius - halfStrokeWidth, width - halfStrokeWidth, height
					- halfStrokeWidth), 0, 90);
			outline.lineTo(halfStrokeWidth, height - halfStrokeWidth);
			outline.arcTo(new RectF(halfStrokeWidth, height - halfStrokeWidth
					- radius, halfStrokeWidth + radius, height
					- halfStrokeWidth), 90, 90);
			outline.lineTo(halfStrokeWidth, radius + halfStrokeWidth);
			outline.arcTo(new RectF(halfStrokeWidth, halfStrokeWidth, radius,
					radius), 180, 90);
			outline.lineTo(width - radius - halfStrokeWidth, halfStrokeWidth);
			outline.arcTo(new RectF(width - radius - halfStrokeWidth,
					halfStrokeWidth, width - halfStrokeWidth, halfStrokeWidth
							+ radius), 270, 90);
			outline.close();

		} else if (index == 0) {
			// Draw outline of left-most button, clockwise, starting at
			// top-right corner
			outline.moveTo(width - halfStrokeWidth, halfStrokeWidth);
			outline.lineTo(width - halfStrokeWidth, height - halfStrokeWidth);
			outline.lineTo(radius + halfStrokeWidth, height - halfStrokeWidth);
			outline.arcTo(new RectF(halfStrokeWidth, height - halfStrokeWidth
					- radius, halfStrokeWidth + radius, height
					- halfStrokeWidth), 90, 90);
			outline.lineTo(halfStrokeWidth, radius + halfStrokeWidth);
			outline.arcTo(new RectF(halfStrokeWidth, halfStrokeWidth, radius,
					radius), 180, 90);
			outline.close();
		} else if (radioGroup != null
				&& (index == (radioGroup.getChildCount() - 1))) {
			// Draw outline of right-most button, clockwise, starting at
			// top-left corner
			outline.moveTo(halfStrokeWidth, halfStrokeWidth);
			outline.lineTo(width - radius - halfStrokeWidth, halfStrokeWidth);
			outline.arcTo(new RectF(width - radius - halfStrokeWidth,
					halfStrokeWidth, width - halfStrokeWidth, halfStrokeWidth
							+ radius), 270, 90);
			outline.lineTo(width - halfStrokeWidth, height - radius
					- halfStrokeWidth);
			outline.arcTo(new RectF(width - radius - halfStrokeWidth, height
					- radius - halfStrokeWidth, width - halfStrokeWidth, height
					- halfStrokeWidth), 0, 90);
			outline.lineTo(halfStrokeWidth, height - halfStrokeWidth);
			outline.close();
		} else {
			// Draw outline of "inside" button.
			outline.addRect(new RectF(halfStrokeWidth, halfStrokeWidth, width
					- halfStrokeWidth, height - halfStrokeWidth),
					Path.Direction.CW);
		}

		// Draw background
		Shader gradient = isChecked() ? checkedShader() : uncheckedShader();
		Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		backgroundPaint.setStyle(Paint.Style.FILL);
		backgroundPaint.setShader(gradient);
		canvas.drawPath(outline, backgroundPaint);

		// Draw text label
		String text = this.getText().toString();
		Paint textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);
		textPaint.setTextSize(getHeight() / textScaleFactor);
		textPaint.setColor(0xff505050);
		textPaint.setShadowLayer(2f, 1f, 1f, 0xaaffffff);
		Paint.FontMetrics metrics = textPaint.getFontMetrics();

		float x = (width - textPaint.measureText(text)) / 2;
		float y = (height + abs(metrics.ascent) - metrics.descent) / 2.0f;
		canvas.drawText(text, x, y, textPaint);

		// Draw border
		Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		borderPaint.setShader(outlineShader());
		borderPaint.setStrokeWidth(2f * halfStrokeWidth);
		borderPaint.setStyle(Style.STROKE);
		canvas.drawPath(outline, borderPaint);

		if (index > 0 && !isChecked()) {
			// Draw highlight on left border edge
			Paint edgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			edgePaint.setShader(leftEdgeHilightShader());
			edgePaint.setStrokeWidth(3f * halfStrokeWidth);
			edgePaint.setStyle(Style.STROKE);
			canvas.drawLine(halfStrokeWidth, 2 * halfStrokeWidth,
					halfStrokeWidth, height - 2 * halfStrokeWidth, edgePaint);
		}

	}

}