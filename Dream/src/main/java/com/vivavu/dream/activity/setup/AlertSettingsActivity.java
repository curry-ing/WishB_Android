package com.vivavu.dream.activity.setup;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.vivavu.dream.R;
import com.vivavu.dream.broadcastReceiver.AlarmManagerBroadcastReceiver;
import com.vivavu.dream.common.DreamApp;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class AlertSettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(DreamApp.getInstance()).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(DreamApp.getInstance()).reportActivityStop(this);
        super.onStop();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
//        addPreferencesFromResource(R.xml.pref_general);
        addPreferencesFromResource(R.xml.pref_notification);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
//        bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        bindPreferenceSummaryToValue(findPreference("notifications_time_morning"));
        bindPreferenceSummaryToValue(findPreference("notifications_time_night"));

        final Preference good_morning_alarm = (Preference) findPreference("notification_good_morning_alarm");
        final Preference good_night_alarm = (Preference) findPreference("notification_good_night_alarm");
        final Preference alarm_time_morning = (Preference) findPreference("notifications_time_morning");
        final Preference alarm_time_night = (Preference) findPreference("notifications_time_night");
        final AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();


        alarm_time_morning.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value){
                String v = (String)value;
                alarm.setEverydayAlarm(preference.getContext(), Integer.parseInt(v.split(":")[0]), Integer.parseInt(v.split(":")[1]), 1);
                preference.setSummary(v);

	            Tracker tracker = DreamApp.getInstance().getTracker();
	            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_alert_settings_activity)).setAction(getString(R.string.ga_event_action_change_morning_alert));
	            tracker.send(eventBuilder.build());

                return true;
            }
        });

        alarm_time_night.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value){
                String v = (String)value;
                alarm.setEverydayAlarm(preference.getContext(), Integer.parseInt(v.split(":")[0]), Integer.parseInt(v.split(":")[1]), 2);
                preference.setSummary(v);

	            Tracker tracker = DreamApp.getInstance().getTracker();
	            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_alert_settings_activity)).setAction(getString(R.string.ga_event_action_change_night_alert));
	            tracker.send(eventBuilder.build());

	            return true;
            }
        });

        good_morning_alarm.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {

                if ((Boolean)value) {
	                Tracker tracker = DreamApp.getInstance().getTracker();
	                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_alert_settings_activity)).setAction(getString(R.string.ga_event_action_morning_alert_on));
	                tracker.send(eventBuilder.build());
                } else {
	                Tracker tracker = DreamApp.getInstance().getTracker();
	                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_alert_settings_activity)).setAction(getString(R.string.ga_event_action_morning_alert_off));
	                tracker.send(eventBuilder.build());
                }
                alarm.setEverydayAlarm(getBaseContext(), true, 1);
                return true;
            }
        });

        good_night_alarm.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {

	            if ((Boolean)value) {
		            Tracker tracker = DreamApp.getInstance().getTracker();
		            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_alert_settings_activity)).setAction(getString(R.string.ga_event_action_night_alert_on));
		            tracker.send(eventBuilder.build());
	            } else {
		            Tracker tracker = DreamApp.getInstance().getTracker();
		            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder().setCategory(getString(R.string.ga_event_category_alert_settings_activity)).setAction(getString(R.string.ga_event_action_night_alert_off));
		            tracker.send(eventBuilder.build());
	            }

                alarm.setEverydayAlarm(getBaseContext(), true, 2);
                return true;
            }
        });

    }

    /** {@inheritDoc} */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /** {@inheritDoc} */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);

//                if (preference.getKey().equals("notifications_time_morning")) {
//                    AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();
//                    alarm.setEverydayAlarm(preference.getContext(), Integer.parseInt(stringValue.split(":")[0]), Integer.parseInt(stringValue.split(":")[1]), 1);
//                } else if (preference.getKey().equals("notifications_time_night")) {
//                    AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();
//                    alarm.setEverydayAlarm(preference.getContext(), Integer.parseInt(stringValue.split(":")[0]), Integer.parseInt(stringValue.split(":")[1]), 2);
//                }
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        if(preference == null){
            return ;
        }
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);

            final Preference morning_alarm = (Preference) findPreference("notification_good_morning_alarm");
            morning_alarm.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getActivity().getBaseContext(), "Some Text", Toast.LENGTH_LONG).show();
                    return false;
                }
            });

//            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
            bindPreferenceSummaryToValue(findPreference("notifications_time_morning"));
            bindPreferenceSummaryToValue(findPreference("notifications_time_night"));
        }
    }

}
