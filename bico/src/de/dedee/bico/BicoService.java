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
 */

package de.dedee.bico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.apps.mytracks.content.Waypoint.WaypointType;
import com.google.android.apps.mytracks.content.WaypointCreationRequest;

import de.dedee.bico.csm.StateContext;
import de.dedee.bico.csm.states.Event;
import de.dedee.bico.ui.Units;
import de.dedee.bico.ui.WidgetVariants;

/**
 * Main background service which itself connects to the MyTracks service and captures all the statistics data whenever a
 * track is recorded in MyTracks. The MyTracks app itself is opensource and its API is documented basically.
 * http://code.google.com/p/mytracks/wiki/MyTracksApi
 * 
 * @author dedee
 */
public class BicoService extends Service {

	private final Messenger messenger = new Messenger(new IncomingHandler());
	private final MyTracksServiceStatusReceiver serviceStatusReceiver = new MyTracksServiceStatusReceiver();
	private WidgetVariants widgetVariants;
	private StateContext ctx;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(C.TAG, "Service created");

		widgetVariants = new WidgetVariants(this);

		ctx = new StateContext(this, widgetVariants);
		ctx.start();
		ctx.sendEvent(Event.Connect);

		// Register to MyTracks service to receiver notifications.
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(IntentConstants.COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_STARTED);
		intentFilter.addAction(IntentConstants.COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_PAUSED);
		intentFilter.addAction(IntentConstants.COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_RESUMED);
		intentFilter.addAction(IntentConstants.COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_STOPPED);
		intentFilter.addAction(IntentConstants.ORG_METAWATCH_MANAGER_REFRESH_WIDGET_REQUEST);
		intentFilter.addAction(IntentConstants.ORG_METAWATCH_MANAGER_APPLICATION_DISCOVERY);
		intentFilter.addAction(IntentConstants.ORG_METAWATCH_MANAGER_BUTTON_PRESS_RIGHT);
		intentFilter.addAction(IntentConstants.ORG_METAWATCH_MANAGER_BUTTON_PRESS_RIGHT_LONG);
		intentFilter.addAction(IntentConstants.ORG_METAWATCH_MANAGER_BUTTON_PRESS_LEFT);
		registerReceiver(serviceStatusReceiver, intentFilter);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		widgetVariants.clearScreen();
		Log.d(C.TAG, "Service started");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// http://stackoverflow.com/questions/9755271/android-service-keeps-getting-killed?rq=1
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(serviceStatusReceiver);

		ctx.sendEvent(Event.Disconnect);
		ctx.waitFor(Event.Disconnected, 500);
		ctx.sendEvent(Event.Terminate);

		Log.d(C.TAG, "Service destroyed");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return messenger.getBinder();
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Log.d(C.TAG, "Got message " + msg);
			switch (msg.what) {
			default:
				super.handleMessage(msg);
			}
		}
	}

	class MyTracksServiceStatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (action.equals(IntentConstants.COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_STARTED)) {
				Log.i(C.TAG, "The track was started");
				ctx.sendEvent(Event.Connect);
				ctx.vibrate();

			} else if (action.equals(IntentConstants.COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_STOPPED)) {
				Log.i(C.TAG, "The track was stopped");
				ctx.sendEvent(Event.Disconnect);
				ctx.vibrate();

			} else if (action.equals(IntentConstants.COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_PAUSED)) {
				Log.i(C.TAG, "The track was paused");
				ctx.sendEvent(Event.Pausing);
				ctx.vibrate();

			} else if (action.equals(IntentConstants.COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_RESUMED)) {
				Log.i(C.TAG, "The track was resumed");
				ctx.sendEvent(Event.Resuming);
				ctx.vibrate();

			} else if (action.equals(IntentConstants.ORG_METAWATCH_MANAGER_REFRESH_WIDGET_REQUEST)) {
				Log.d(C.TAG, "Widget update requested we resend the previous stats or clear the screen");
				Bundle bundle = intent.getExtras();

				if (bundle.containsKey(IntentConstants.ORG_METAWATCH_MANAGER_WIDGETS_DESIRED)) {
					boolean widgetEnabled = widgetVariants.activatePerIdList(new ArrayList<String>(Arrays.asList(bundle
							.getStringArray(IntentConstants.ORG_METAWATCH_MANAGER_WIDGETS_DESIRED))));
					if (widgetEnabled) {
						Log.i(C.TAG, "Got REFRESH_WIDGET_REQUEST and we are activated");
					} else {
						Log.i(C.TAG, "Got REFRESH_WIDGET_REQUEST and we are NOT activated");
					}
				}

				// Send an UI update for all possible variants we provide.
				boolean previewRequested = bundle.containsKey(IntentConstants.ORG_METAWATCH_MANAGER_GET_PREVIEWS);
				if (previewRequested) {
					Log.d(C.TAG, "A preview picture is requested, so sending some nice previews for all widgets");
					widgetVariants.sendDemoStatistics();
				} else if (widgetVariants.isActive()) {
					widgetVariants.repaint();
				}

			} else if (action.equals(IntentConstants.ORG_METAWATCH_MANAGER_APPLICATION_DISCOVERY)) {
				// Just for apps. Not for widgets

			} else if (action.equals(IntentConstants.ORG_METAWATCH_MANAGER_BUTTON_PRESS_RIGHT)) {
				/*
				 * Control from metawatch manager: toggles Pause/Resume
				 */
				Log.d(C.TAG, "Received Right Button press from Metawatch Manager, pausing/resuming mytracks");

				try {
					if (ctx.getData().getMyTracksServiceStatus()) {
						if (ctx.getData().getMyTracksService().isPaused()) {
							ctx.getData().getMyTracksService().resumeCurrentTrack();
						} else {
							if (ctx.getData().getMyTracksService().getTotalTime() > 15 * 1000)
								ctx.getData()
										.getMyTracksService()
										.insertWaypoint(
												new WaypointCreationRequest(WaypointType.WAYPOINT, false, "Pause",
														Calendar.getInstance().getTime().toString(), Units
																.durationToString(
																		ctx.getData().getMyTracksService()
																				.getTotalTime()).getValue(), ""));

							ctx.getData().getMyTracksService().pauseCurrentTrack();
						}
					}
				} catch (RemoteException e) {
					Log.e(C.TAG, "Exception raised MyTracks services not active", e);
				}

			} else if (action.equals(IntentConstants.ORG_METAWATCH_MANAGER_BUTTON_PRESS_LEFT)
					|| action.equals(IntentConstants.ORG_METAWATCH_MANAGER_BUTTON_PRESS_RIGHT_LONG)) {
				/*
				 * Control from metawatch manager: Add waypoint
				 */
				Log.d(C.TAG,
						"Received Left button press or long Right button press from Metawatch Manager, adding waypoint");

				try {
					if (ctx.getData().getMyTracksServiceStatus()) {

						ctx.getData()
								.getMyTracksService()
								.insertWaypoint(
										new WaypointCreationRequest(WaypointType.WAYPOINT, false, "Point of Interest",
												Calendar.getInstance().getTime().toString(), Units.durationToString(
														ctx.getData().getMyTracksService().getTotalTime()).getValue(),
												""));

						ctx.vibrate();
					}
				} catch (RemoteException e) {
					Log.e(C.TAG, "Exception raised MyTracks services not active", e);
				}

			} else {
				Log.w(C.TAG, "Ignored action in receiver " + action);
			}
		}
	}

}
