package broot.ingress.sim;

import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

public class FakePrefs implements SharedPreferences {

    @Override
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(String key, String def) {
        switch (key) {
            case "itemsTab":
                return "AT_END";
        }
        return def;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> def) {
        return def;
    }

    @Override
    public int getInt(String key, int def) {
        return def;
    }

    @Override
    public long getLong(String key, long def) {
        return def;
    }

    @Override
    public float getFloat(String key, float def) {
        return def;
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return def;
    }

    @Override
    public boolean contains(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Editor edit() {
        return new Editor() {
            @Override
            public Editor putString(String key, String value) {
                return this;
            }

            @Override
            public Editor putStringSet(String key, Set<String> values) {
                return this;
            }

            @Override
            public Editor putInt(String key, int value) {
                return this;
            }

            @Override
            public Editor putLong(String key, long value) {
                return this;
            }

            @Override
            public Editor putFloat(String key, float value) {
                return this;
            }

            @Override
            public Editor putBoolean(String key, boolean value) {
                return this;
            }

            @Override
            public Editor remove(String key) {
                return this;
            }

            @Override
            public Editor clear() {
                return this;
            }

            @Override
            public boolean commit() {
                return true;
            }

            @Override
            public void apply() {
            }
        };
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        throw new UnsupportedOperationException();
    }
}
