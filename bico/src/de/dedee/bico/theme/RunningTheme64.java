/*  
 * bico - (C)opyright 2012 - dedee
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Running theme by Nicolas Oct 2013
 * 
 * Based on original bic DefaultTheme64
 * 
 * New: different displays in "Running" - while mytracks is recording - and "Last track"
 * "Running" display shows:
 * - time elapsed since track start 
 * - total distance since track start
 * - average speed over last minute
 * - average speed trend: comparison of last minute average and track average
 * 
 * "Last track" display is same as original
 * 
 * Coolvetica font is copyright by Typodermic Fonts Inc.
 */

package de.dedee.bico.theme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import de.dedee.bico.C;
import de.dedee.bico.R;
import de.dedee.bico.ui.Resolution;

public class RunningTheme64 implements Theme {

	private Typeface typefaceLarge;
	private Typeface typefaceSmall;
	private Typeface typefaceXLarge;

	private Bitmap bitmap;
	private Canvas canvas;
	private Context context;
	private Paint paintSmall;
	private Paint paintLarge;
	private Paint paintXLarge;

	private Paint paintLine;

	public RunningTheme64(Context context, Resolution resolution) {
		this.context = context;

		typefaceSmall = Typeface.createFromAsset(context.getAssets(), "metawatch_8pt_5pxl_CAPS.ttf");
		typefaceLarge = Typeface.createFromAsset(context.getAssets(), "coolvetica_rg.otf");
		typefaceXLarge = Typeface.createFromAsset(context.getAssets(), "coolvetica_rg.otf");

		bitmap = Bitmap.createBitmap(resolution.getWidth(), resolution.getHeight(), Bitmap.Config.RGB_565);
		canvas = new Canvas(bitmap);

		// Initialize bitmap
		createTextBitmap(UpdateMode.Idle, null);

		paintSmall = new Paint();
		paintSmall.setColor(Color.BLACK);
		paintSmall.setTextSize(8);
		paintSmall.setTypeface(typefaceSmall);

		paintLarge = new Paint();
		paintLarge.setColor(Color.BLACK);
		paintLarge.setTextSize(16);
		paintLarge.setTypeface(typefaceLarge);

		paintXLarge = new Paint();
		paintXLarge.setColor(Color.BLACK);
		paintXLarge.setTextSize(26);
		paintXLarge.setTypeface(typefaceXLarge);

		paintLine = new Paint();
		paintLine.setColor(Color.BLACK);
	}

	@Override
	public Bitmap createTextBitmap(UpdateMode mode, UpdateData data) {

		canvas.drawColor(Color.WHITE);

		int yoffset = 0;
		int rowHeight = 0;

		if (data != null) {
			// TITLE
			if (data.getTitle() == "Running") {
				yoffset += 1;
			} else {
				yoffset += 6;
			}
			rowHeight = drawHeader(data.getTitle(), 5, yoffset);
			yoffset += rowHeight + 2;

			switch (mode) {
			case Recording:
			case Demo: {
				// Log.d(C.TAG, "Updating bitmap data mode: " + mode + " data: " + data);

				if (data.getTitle() == "Running") {
					// Total time
					yoffset += 1;
					rowHeight = drawRowXLarge(R.string.time, data.getTotalTimeRounded(), yoffset);
					yoffset += rowHeight;

					// Total distance to date
					yoffset += 3;
					rowHeight = drawRowXLarge(R.string.totaldistance, data.getTotalDistance(), yoffset);
					yoffset += rowHeight;

					// Average speed over last minute
					yoffset += 5;
					rowHeight = drawRowRunning(R.string.avgspeedoveroneminute, data.getAverageSpeedOverOneMinute(),
							yoffset);

					// Trend on the speed
					canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), data.getAverageTrend()), 82,
							yoffset - 1, null);

				} else {
					// Average speed
					canvas.drawLine(0, yoffset, 96, yoffset, paintLine);
					yoffset += 2;
					rowHeight = drawRow(R.string.avgspeed, data.getAverageSpeed(), yoffset);
					yoffset += rowHeight;

					// Total time
					yoffset += 2;
					canvas.drawLine(0, yoffset, 96, yoffset, paintLine);
					yoffset += 2;
					rowHeight = drawRow(R.string.time, data.getTotalTime(), yoffset);
					yoffset += rowHeight;

					// Total distance to date
					yoffset += 2;
					canvas.drawLine(0, yoffset, 96, yoffset, paintLine);
					yoffset += 2;
					rowHeight = drawRow(R.string.totaldistance, data.getTotalDistance(), yoffset);
					yoffset += rowHeight;
					yoffset += 2;
					canvas.drawLine(0, yoffset, 96, yoffset, paintLine);
				}

				break;
			}
			case Idle: {
				Log.d(C.TAG, "Updating idle mode: " + mode);
				break;
			}
			}
		} else {
			Log.d(C.TAG, "Data is null. This is the case when the screen is cleared");
		}

		return bitmap;
	}

	private int drawRow(int textId, UpdateValue v, int yoffset) {
		int headerHeight = drawHeader(context.getString(textId), 5, yoffset);
		int valueHeight = drawValue(v.getValue(), 96 - 5, yoffset);
		drawUnit(v.getUnit(), 5, yoffset, headerHeight);
		return valueHeight;
	}

	private int drawRowRunning(int textId, UpdateValue v, int yoffset) {
		int headerHeight = drawHeader(context.getString(textId), 2, yoffset);
		int valueHeight = drawValue(v.getValue(), 80, yoffset);
		drawUnit(v.getUnit(), 2, yoffset, headerHeight);
		return valueHeight;
	}

	private int drawRowXLarge(int textId, UpdateValue v, int yoffset) {
		Rect bounds = new Rect();
		paintXLarge.getTextBounds(v.toString(), 0, v.toString().length(), bounds);
		int height = Math.abs(bounds.top);
		int x = 96 - 5 - bounds.right;
		int y = yoffset + height;
		// Log.d(C.TAG, "drawValue x=" + x + " y=" + y);
		canvas.drawText(v.toString(), x, y, paintXLarge);
		return height;
	}

	private int drawHeader(String s, int xoffset, int yoffset) {
		Rect bounds = new Rect();
		paintSmall.getTextBounds(s, 0, s.length(), bounds);
		int height = Math.abs(bounds.top);
		int x = xoffset;
		int y = yoffset + height;
		// Log.d(C.TAG, "drawHeader x=" + x + " y=" + y);
		canvas.drawText(s, x, y, paintSmall);
		return height;
	}

	private int drawValue(String s, int xoffset, int yoffset) {
		Rect bounds = new Rect();
		paintLarge.getTextBounds(s, 0, s.length(), bounds);
		int height = Math.abs(bounds.top);
		int x = xoffset - bounds.right;
		int y = yoffset + height;
		// Log.d(C.TAG, "drawValue x=" + x + " y=" + y);
		canvas.drawText(s, x, y, paintLarge);
		return height;
	}

	private int drawUnit(String s, int xoffset, int yoffset, int heiderHeight) {
		Rect bounds = new Rect();
		paintSmall.getTextBounds(s, 0, s.length(), bounds);
		int height = Math.abs(bounds.top);
		int x = xoffset;
		int y = yoffset + heiderHeight + height + 2;
		// Log.d(C.TAG, "drawUnit x=" + x + " y=" + y);
		canvas.drawText(s, x, y, paintSmall);
		return height;
	}

	@Override
	public Bitmap getLastBitmap() {
		Log.d(C.TAG, "Get last bitmap");
		return bitmap;
	}

}
