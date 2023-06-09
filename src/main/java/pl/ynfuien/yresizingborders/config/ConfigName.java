package pl.ynfuien.yresizingborders.config;

public enum ConfigName {
    LANG,
    PROFILES;

    String getFileName() {
        return name().toLowerCase().replace('_', '-') + ".yml";
    }
}
