package nrw.helpers.localization;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.StringBuilder;
import nrw.helpers.FontHelper;
import nrw.helpers.Settings;
import nrw.helpers.masters.AssetLoader;
import nrw.helpers.masters.AssetMaster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocalizedStrings {

    private final StringMaster<UIStrings> uiMaster;

    public LocalizedStrings() {
        uiMaster = new StringMaster<>(UIStrings.class, buildName("ui.json"));
    }

    public String buildName(String name) {
        return "localization/" + Settings.usingLang.name() + "/" + name;
    }

    public UIStrings getUIString(String key) {
        return uiMaster.getString(key);
    }

    public void buildUnicodeSet() {
        Set<String> unicodeSet = new HashSet<>();
        // default ascii
        addChar(unicodeSet, FreeTypeFontGenerator.DEFAULT_CHARS);
        for (UIStrings ui: uiMaster.strings.values()) {
            if (ui.TEXT != null) {
                for (String text : ui.TEXT) {
                    addChar(unicodeSet, text);
                }
            }
            if (ui.TEXT_MAP != null) {
                // 不考虑 map key
                for (String text : ui.TEXT_MAP.values()) {
                    addChar(unicodeSet, text);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String c: unicodeSet) {
            sb.append(c);
        }
        FontHelper.usingUnicode = sb.toString();
    }

    private void addChar(Set<String> set, String unicodeText) {
        int i = 0;
        while (i < unicodeText.length()) {
            int codePoint = unicodeText.codePointAt(i);
            set.add(new String(Character.toChars(codePoint)));
            i += Character.charCount(codePoint);
        }
    }

    private class StringMaster<T> implements AssetMaster {

        /**
         * filename 应当包含语言配置
         */
        private final String filename;
        protected Map<String, T> strings;

        private StringMaster(Class<T> type, String filename) {
            this.filename = filename;
            AssetLoader.registerMaster(this, type);
        }

        public T getString(String key) {
            return strings.get(key);
        }

        @Override
        public List<String> getAssetPath() {
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(filename);
            return tmp;
        }

        @Override
        public void postLoad(AssetManager manager) {
            strings = manager.get(filename);
        }
    }

}
