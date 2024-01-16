package com.nl.clientachatmobile.Models.Data;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageSelector {

    public static void selectLanguage(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

    }
}
