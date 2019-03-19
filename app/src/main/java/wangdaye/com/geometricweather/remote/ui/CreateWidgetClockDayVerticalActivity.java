package wangdaye.com.geometricweather.remote.ui;

import android.annotation.SuppressLint;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.basic.GeoWidgetConfigActivity;
import wangdaye.com.geometricweather.basic.model.weather.Weather;
import wangdaye.com.geometricweather.utils.LanguageUtils;
import wangdaye.com.geometricweather.background.BackgroundManager;
import wangdaye.com.geometricweather.utils.manager.TimeManager;
import wangdaye.com.geometricweather.remote.utils.WidgetClockDayVerticalUtils;

/**
 * Create widget clock day center activity.
 * */

public class CreateWidgetClockDayVerticalActivity extends GeoWidgetConfigActivity
        implements View.OnClickListener {

    private View[] widgetViews;
    private ImageView widgetCard;
    private ImageView widgetIcon;
    private RelativeLayout[] widgetClockContainers;
    private TextClock[] widgetClocks;
    private TextClock[] widgetClockAAs;
    private TextClock[] widgetClockVerticals;
    private TextView widgetTitle;
    private TextView widgetSubtitle;
    private TextView widgetTime;

    private CoordinatorLayout container;

    private Switch showCardSwitch;
    private Switch hideSubtitleSwitch;
    private Switch blackTextSwitch;

    // data
    private String viewTypeValueNow = "rectangle";
    private String[] viewTypes;
    private String[] viewTypeValues;

    private String subtitleDataValueNow = "time";
    private String[] subtitleData;
    private String[] subtitleDataValues;

    private String clockFontValueNow = "light";
    private String[] clockFonts;
    private String[] clockFontValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_widget_clock_day_vertical);
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    @Override
    public void initData() {
        super.initData();
        this.viewTypes = new String[] {
                getResources().getStringArray(R.array.widget_styles)[0],
                getResources().getStringArray(R.array.widget_styles)[1],
                getResources().getStringArray(R.array.widget_styles)[2],
                getResources().getStringArray(R.array.widget_styles)[3],
                getResources().getStringArray(R.array.widget_styles)[6]};
        this.viewTypeValues = new String[] {
                getResources().getStringArray(R.array.widget_style_values)[0],
                getResources().getStringArray(R.array.widget_style_values)[1],
                getResources().getStringArray(R.array.widget_style_values)[2],
                getResources().getStringArray(R.array.widget_style_values)[3],
                getResources().getStringArray(R.array.widget_style_values)[6]};
        int length = LanguageUtils.getLanguageCode(this).startsWith("zh") ? 5 : 4;
        this.subtitleData = new String[length];
        this.subtitleDataValues = new String[length];
        String[] data = getResources().getStringArray(R.array.subtitle_data);
        String[] dataValues = getResources().getStringArray(R.array.subtitle_data_values);
        for (int i = 0; i < length; i ++) {
            subtitleData[i] = data[i];
            subtitleDataValues[i] = dataValues[i];
        }
        this.clockFonts = getResources().getStringArray(R.array.clock_font);
        this.clockFontValues = getResources().getStringArray(R.array.clock_font_values);
    }

    @Override
    public void initWidget() {
        setWidgetView(true);

        ImageView wallpaper = findViewById(R.id.activity_create_widget_clock_day_vertical_wall);
        bindWallpaper(wallpaper);

        this.container = findViewById(R.id.activity_create_widget_clock_day_vertical_container);

        AppCompatSpinner viewTypeSpinner = findViewById(R.id.activity_create_widget_clock_day_vertical_styleSpinner);
        viewTypeSpinner.setOnItemSelectedListener(new ViewTypeSpinnerSelectedListener());
        viewTypeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, viewTypes));

        this.showCardSwitch = findViewById(R.id.activity_create_widget_clock_day_vertical_showCardSwitch);
        showCardSwitch.setOnCheckedChangeListener(new ShowCardSwitchCheckListener());

        this.hideSubtitleSwitch = findViewById(R.id.activity_create_widget_clock_day_vertical_hideSubtitleSwitch);
        hideSubtitleSwitch.setOnCheckedChangeListener(new HideSubtitleSwitchCheckListener());

        AppCompatSpinner subtitleDataSpinner = findViewById(R.id.activity_create_widget_clock_day_vertical_subtitleDataSpinner);
        subtitleDataSpinner.setOnItemSelectedListener(new SubtitleDataSpinnerSelectedListener());
        subtitleDataSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subtitleData));

        this.blackTextSwitch = findViewById(R.id.activity_create_widget_clock_day_vertical_blackTextSwitch);
        blackTextSwitch.setOnCheckedChangeListener(new BlackTextSwitchCheckListener());

        AppCompatSpinner clockFontSpinner = findViewById(R.id.activity_create_widget_clock_day_vertical_clockFontSpinner);
        clockFontSpinner.setOnItemSelectedListener(new ClockFontSpinnerSelectedListener());
        clockFontSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, clockFonts));

        Button doneButton = findViewById(R.id.activity_create_widget_clock_day_vertical_doneButton);
        doneButton.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshWidgetView(Weather weather) {
        if (weather == null) {
            return;
        }

        int imageId = WidgetClockDayVerticalUtils.getWeatherIconId(
                weather,
                TimeManager.getInstance(this).getDayTime(
                        this, weather, false).isDayTime(),
                isMinimalIcon(),
                blackTextSwitch.isChecked() || showCardSwitch.isChecked());
        Glide.with(this)
                .load(imageId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(widgetIcon);

        widgetTitle.setText(
                WidgetClockDayVerticalUtils.getTitleText(
                        weather, viewTypeValueNow, isFahrenheit()));

        if (widgetSubtitle != null) {
            widgetSubtitle.setText(
                    WidgetClockDayVerticalUtils.getSubtitleText(
                            weather, viewTypeValueNow, isFahrenheit()));
        }
        if (widgetTime != null) {
            widgetTime.setText(
                    WidgetClockDayVerticalUtils.getTimeText(
                            this, weather, viewTypeValueNow, subtitleDataValueNow));
        }

        for (int i = 0; i < clockFontValues.length; i ++) {
            if (clockFontValueNow.equals(clockFontValues[i])) {
                widgetClockContainers[i].setVisibility(View.VISIBLE);
            } else {
                widgetClockContainers[i].setVisibility(View.GONE);
            }
        }

        if (showCardSwitch.isChecked() || blackTextSwitch.isChecked()) {
            if (showCardSwitch.isChecked()) {
                widgetCard.setVisibility(View.VISIBLE);
            } else {
                widgetCard.setVisibility(View.GONE);
            }
            if (widgetClocks != null) {
                for (TextClock c : widgetClocks) {
                    c.setTextColor(ContextCompat.getColor(this, R.color.colorTextDark));
                }
            }
            if (widgetClockAAs != null) {
                for (TextClock c : widgetClockAAs) {
                    c.setTextColor(ContextCompat.getColor(this, R.color.colorTextDark));
                }
            }
            if (widgetClockVerticals != null) {
                for (TextClock c : widgetClockVerticals) {
                    c.setTextColor(ContextCompat.getColor(this, R.color.colorTextDark));
                }
            }
            widgetTitle.setTextColor(ContextCompat.getColor(this, R.color.colorTextDark));
            if (widgetSubtitle != null) {
                widgetSubtitle.setTextColor(ContextCompat.getColor(this, R.color.colorTextDark));
            }
            if (widgetTime != null) {
                widgetTime.setTextColor(ContextCompat.getColor(this, R.color.colorTextDark));
            }
        } else {
            widgetCard.setVisibility(View.GONE);

            if (widgetClocks != null) {
                for (TextClock c : widgetClocks) {
                    c.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight));
                }
            }
            if (widgetClockAAs != null) {
                for (TextClock c : widgetClockAAs) {
                    c.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight));
                }
            }
            if (widgetClockVerticals != null) {
                for (TextClock c : widgetClockVerticals) {
                    c.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight));
                }
            }
            widgetTitle.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight));
            if (widgetSubtitle != null) {
                widgetSubtitle.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight));
            }
            if (widgetTime != null) {
                widgetTime.setTextColor(ContextCompat.getColor(this, R.color.colorTextLight));
            }
        }

        if (widgetTime != null) {
            widgetTime.setVisibility(hideSubtitleSwitch.isChecked() ? View.GONE : View.VISIBLE);
        }
    }

    /** <br> UI. */

    @SuppressLint("InflateParams")
    private void setWidgetView(boolean init) {
        if (init) {
            this.widgetViews = new View[] {
                    LayoutInflater.from(this).inflate(R.layout.widget_clock_day_rectangle, null),
                    LayoutInflater.from(this).inflate(R.layout.widget_clock_day_symmetry, null),
                    LayoutInflater.from(this).inflate(R.layout.widget_clock_day_tile, null),
                    LayoutInflater.from(this).inflate(R.layout.widget_clock_day_mini, null),
                    LayoutInflater.from(this).inflate(R.layout.widget_clock_day_vertical, null)};
            for (View widgetView : widgetViews) {
                ((ViewGroup) findViewById(R.id.activity_create_widget_clock_day_vertical_widgetContainer)).addView(widgetView);
            }
        }
        for (View widgetView : widgetViews) {
            widgetView.setVisibility(View.GONE);
        }

        switch (viewTypeValueNow) {
            case "rectangle":
                this.widgetViews[0].setVisibility(View.VISIBLE);

                this.widgetCard = widgetViews[0].findViewById(R.id.widget_clock_day_card);
                widgetCard.setVisibility(View.GONE);

                this.widgetIcon = widgetViews[0].findViewById(R.id.widget_clock_day_icon);
                this.widgetClockContainers = new RelativeLayout[] {
                        widgetViews[0].findViewById(R.id.widget_clock_day_clock_lightContainer),
                        widgetViews[0].findViewById(R.id.widget_clock_day_clock_normalContainer),
                        widgetViews[0].findViewById(R.id.widget_clock_day_clock_blackContainer)};
                this.widgetClocks = new TextClock[] {
                        widgetViews[0].findViewById(R.id.widget_clock_day_clock_light),
                        widgetViews[0].findViewById(R.id.widget_clock_day_clock_normal),
                        widgetViews[0].findViewById(R.id.widget_clock_day_clock_black)};
                this.widgetClockAAs = new TextClock[] {
                        widgetViews[0].findViewById(R.id.widget_clock_day_clock_aa_light),
                        widgetViews[0].findViewById(R.id.widget_clock_day_clock_aa_normal),
                        widgetViews[0].findViewById(R.id.widget_clock_day_clock_aa_black)};
                this.widgetClockVerticals = null;
                this.widgetTitle = widgetViews[0].findViewById(R.id.widget_clock_day_title);
                this.widgetSubtitle = widgetViews[0].findViewById(R.id.widget_clock_day_subtitle);
                this.widgetTime = widgetViews[0].findViewById(R.id.widget_clock_day_time);
                break;

            case "symmetry":
                widgetViews[1].setVisibility(View.VISIBLE);

                this.widgetCard = widgetViews[1].findViewById(R.id.widget_clock_day_card);
                widgetCard.setVisibility(View.GONE);

                this.widgetIcon = widgetViews[1].findViewById(R.id.widget_clock_day_icon);
                this.widgetClockContainers = new RelativeLayout[] {
                        widgetViews[1].findViewById(R.id.widget_clock_day_clock_lightContainer),
                        widgetViews[1].findViewById(R.id.widget_clock_day_clock_normalContainer),
                        widgetViews[1].findViewById(R.id.widget_clock_day_clock_blackContainer)};
                this.widgetClocks = new TextClock[] {
                        widgetViews[1].findViewById(R.id.widget_clock_day_clock_light),
                        widgetViews[1].findViewById(R.id.widget_clock_day_clock_normal),
                        widgetViews[1].findViewById(R.id.widget_clock_day_clock_black)};
                this.widgetClockAAs = new TextClock[] {
                        widgetViews[1].findViewById(R.id.widget_clock_day_clock_aa_light),
                        widgetViews[1].findViewById(R.id.widget_clock_day_clock_aa_normal),
                        widgetViews[1].findViewById(R.id.widget_clock_day_clock_aa_black)};
                this.widgetClockVerticals = null;
                this.widgetTitle = widgetViews[1].findViewById(R.id.widget_clock_day_title);
                this.widgetSubtitle = widgetViews[1].findViewById(R.id.widget_clock_day_subtitle);
                this.widgetTime = widgetViews[1].findViewById(R.id.widget_clock_day_time);
                break;

            case "tile":
                widgetViews[2].setVisibility(View.VISIBLE);

                this.widgetCard = widgetViews[2].findViewById(R.id.widget_clock_day_card);
                widgetCard.setVisibility(View.GONE);

                this.widgetIcon = widgetViews[2].findViewById(R.id.widget_clock_day_icon);
                this.widgetClockContainers = new RelativeLayout[] {
                        widgetViews[2].findViewById(R.id.widget_clock_day_clock_lightContainer),
                        widgetViews[2].findViewById(R.id.widget_clock_day_clock_normalContainer),
                        widgetViews[2].findViewById(R.id.widget_clock_day_clock_blackContainer)};
                this.widgetClocks = new TextClock[] {
                        widgetViews[2].findViewById(R.id.widget_clock_day_clock_light),
                        widgetViews[2].findViewById(R.id.widget_clock_day_clock_normal),
                        widgetViews[2].findViewById(R.id.widget_clock_day_clock_black)};
                this.widgetClockAAs = new TextClock[] {
                        widgetViews[2].findViewById(R.id.widget_clock_day_clock_aa_light),
                        widgetViews[2].findViewById(R.id.widget_clock_day_clock_aa_normal),
                        widgetViews[2].findViewById(R.id.widget_clock_day_clock_aa_black)};
                this.widgetClockVerticals = null;
                this.widgetTitle = widgetViews[2].findViewById(R.id.widget_clock_day_title);
                this.widgetSubtitle = widgetViews[2].findViewById(R.id.widget_clock_day_subtitle);
                this.widgetTime = widgetViews[2].findViewById(R.id.widget_clock_day_time);
                break;

            case "mini":
                widgetViews[3].setVisibility(View.VISIBLE);

                this.widgetCard = widgetViews[3].findViewById(R.id.widget_clock_day_card);
                widgetCard.setVisibility(View.GONE);

                this.widgetIcon = widgetViews[3].findViewById(R.id.widget_clock_day_icon);
                this.widgetClockContainers = new RelativeLayout[] {
                        widgetViews[3].findViewById(R.id.widget_clock_day_clock_lightContainer),
                        widgetViews[3].findViewById(R.id.widget_clock_day_clock_normalContainer),
                        widgetViews[3].findViewById(R.id.widget_clock_day_clock_blackContainer)};
                this.widgetClocks = new TextClock[] {
                        widgetViews[3].findViewById(R.id.widget_clock_day_clock_light),
                        widgetViews[3].findViewById(R.id.widget_clock_day_clock_normal),
                        widgetViews[3].findViewById(R.id.widget_clock_day_clock_black)};
                this.widgetClockAAs = new TextClock[] {
                        widgetViews[3].findViewById(R.id.widget_clock_day_clock_aa_light),
                        widgetViews[3].findViewById(R.id.widget_clock_day_clock_aa_normal),
                        widgetViews[3].findViewById(R.id.widget_clock_day_clock_aa_black)};
                this.widgetClockVerticals = null;
                this.widgetTitle = widgetViews[3].findViewById(R.id.widget_clock_day_title);
                this.widgetSubtitle = widgetViews[3].findViewById(R.id.widget_clock_day_subtitle);
                this.widgetTime = null;
                break;

            case "vertical":
                widgetViews[4].setVisibility(View.VISIBLE);

                this.widgetCard = widgetViews[4].findViewById(R.id.widget_clock_day_card);
                widgetCard.setVisibility(View.GONE);

                this.widgetIcon = widgetViews[4].findViewById(R.id.widget_clock_day_icon);

                this.widgetClockContainers = new RelativeLayout[] {
                        widgetViews[4].findViewById(R.id.widget_clock_day_clock_lightContainer),
                        widgetViews[4].findViewById(R.id.widget_clock_day_clock_normalContainer),
                        widgetViews[4].findViewById(R.id.widget_clock_day_clock_blackContainer)};
                this.widgetClocks = null;
                this.widgetClockAAs = null;
                this.widgetClockVerticals = new TextClock[] {
                        widgetViews[4].findViewById(R.id.widget_clock_day_clock_1_light),
                        widgetViews[4].findViewById(R.id.widget_clock_day_clock_2_light),
                        widgetViews[4].findViewById(R.id.widget_clock_day_clock_1_normal),
                        widgetViews[4].findViewById(R.id.widget_clock_day_clock_2_normal),
                        widgetViews[4].findViewById(R.id.widget_clock_day_clock_1_black),
                        widgetViews[4].findViewById(R.id.widget_clock_day_clock_2_black)};
                this.widgetTitle = widgetViews[4].findViewById(R.id.widget_clock_day_title);
                this.widgetSubtitle = null;
                this.widgetTime = widgetViews[4].findViewById(R.id.widget_clock_day_time);
                break;
        }
    }

    // interface.

    // on click listener.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_create_widget_clock_day_vertical_doneButton:
                SharedPreferences.Editor editor = getSharedPreferences(
                        getString(R.string.sp_widget_clock_day_vertical_setting),
                        MODE_PRIVATE)
                        .edit();
                editor.putString(getString(R.string.key_view_type), viewTypeValueNow);
                editor.putBoolean(getString(R.string.key_show_card), showCardSwitch.isChecked());
                editor.putBoolean(getString(R.string.key_hide_subtitle), hideSubtitleSwitch.isChecked());
                editor.putString(getString(R.string.key_subtitle_data), subtitleDataValueNow);
                editor.putBoolean(getString(R.string.key_black_text), blackTextSwitch.isChecked());
                editor.putString(getString(R.string.key_clock_font), clockFontValueNow);
                editor.apply();

                Intent intent = getIntent();
                Bundle extras = intent.getExtras();
                int appWidgetId = 0;
                if (extras != null) {
                    appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID);
                }

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);

                BackgroundManager.resetNormalBackgroundTask(this, true);
                finish();
                break;
        }
    }

    // on item selected listener.

    private class ViewTypeSpinnerSelectedListener implements AppCompatSpinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (!viewTypeValueNow.equals(viewTypeValues[i])) {
                viewTypeValueNow = viewTypeValues[i];
                setWidgetView(false);
                refreshWidgetView(getLocationNow().weather);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            // do nothing.
        }
    }

    private class SubtitleDataSpinnerSelectedListener implements AppCompatSpinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (!subtitleDataValueNow.equals(subtitleDataValues[i])) {
                subtitleDataValueNow = subtitleDataValues[i];
                refreshWidgetView(getLocationNow().weather);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            // do nothing.
        }
    }

    private class ClockFontSpinnerSelectedListener implements AppCompatSpinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (!clockFontValueNow.equals(clockFontValues[i])) {
                clockFontValueNow = clockFontValues[i];
                refreshWidgetView(getLocationNow().weather);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            // do nothing.
        }
    }

    // on check changed listener(switch).

    private class ShowCardSwitchCheckListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            refreshWidgetView(getLocationNow().weather);
        }
    }

    private class HideSubtitleSwitchCheckListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (widgetTime != null) {
                widgetTime.setVisibility(hideSubtitleSwitch.isChecked() ? View.GONE : View.VISIBLE);
            }
        }
    }

    private class BlackTextSwitchCheckListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            refreshWidgetView(getLocationNow().weather);
        }
    }
}