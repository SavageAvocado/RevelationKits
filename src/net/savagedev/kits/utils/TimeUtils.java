package net.savagedev.kits.utils;

public class TimeUtils {
    /**
     * Creates an array of times from the inputted {@link long}
     * and returns the formatted version.
     *
     * @param time       The time {@link long}
     * @param timeFormat Preferred {@link net.savagedev.kits.utils.TimeUtils.TimeLengthFormat TimeLengthFormat}
     * @return {@link java.lang.String}
     * @see #formatTimes(long[], TimeLengthFormat)
     */
    public static String formatTime(long time, TimeLengthFormat timeFormat) {
        long[] times = new long[6];

        times[0] = time / 1000 % 60; // Seconds
        times[1] = time / (1000 * 60) % 60; // Minutes
        times[2] = time / (1000 * 3600) % 24; // Hours
        times[3] = time / (1000 * 86400) % 30; // Days
        times[4] = time / (1000 * 86400 * 30L) % 12; // Months
        times[5] = time / (1000 * 86400 * 365L); // Years

        return formatTimes(times, timeFormat);
    }

    /**
     * Returns a nicely formatted {@link java.lang.String} based on the specified {@link net.savagedev.kits.utils.TimeUtils.TimeLengthFormat TimeLengthFormat}
     *
     * @param times  {@link long[]} array of times in seconds, minutes, hours, days, months, years.
     * @param format Preferred {@link net.savagedev.kits.utils.TimeUtils.TimeLengthFormat TimeLengthFormat}
     * @return {@link java.lang.String}
     */
    private static String formatTimes(long[] times, TimeLengthFormat format) {
        StringBuilder builder = new StringBuilder();
        String[] names = format.getTimeFormat();

        for (int i = times.length - 1; i >= 0; i--) {
            long time = times[i];

            if (time <= 0) {
                continue;
            }

            String name = names[i];

            if (time > 1 && format == TimeLengthFormat.LONG) {
                name = name + "s";
            }

            builder.append(" ").append(time).append(format == TimeLengthFormat.LONG ? " " : "").append(name);
        }

        return builder.toString().trim();
    }

    /**
     * Represents the time format that should be used when formatting time.
     */
    public enum TimeLengthFormat {
        LONG("second", "minute", "hour", "day", "month", "year"),
        SHORT("s", "m", "h", "d", "mo", "y");

        private String[] timeFormat;

        TimeLengthFormat(String... timeFormat) {
            this.timeFormat = timeFormat;
        }

        public String[] getTimeFormat() {
            return this.timeFormat;
        }
    }
}
