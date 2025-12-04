package me.elpomoika.holykits.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import javax.annotation.Nullable;
import java.util.Map;

@UtilityClass
public class FormatUtil {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacyAmpersand = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer legacySection = LegacyComponentSerializer.legacySection();

    public Component parseAndFormatMessage(String message, @Nullable Map<String, Component> placeholders) {
        Component parsed = FormatUtil.format(message, placeholders);

        return FormatUtil.formatComponent(parsed, placeholders == null ? Map.of() : placeholders);
    }

    private static Component format(String raw, Map<String, Component> placeholders) {
        if (raw == null) {
            return Component.empty();
        }

        Component result = Component.text(raw);

        for (Map.Entry<String, Component> entry : placeholders.entrySet()) {
            String key = entry.getKey();
            Component value = entry.getValue();

            result = result.replaceText(
                    TextReplacementConfig.builder()
                            .matchLiteral(key)
                            .replacement(value)
                            .build()
            );
        }

        return result;
    }

    public static Component formatComponent(Component raw, Map<String, Component> placeholders) {
        if (raw == null) {
            return Component.empty();
        }

        String serialized = LegacyComponentSerializer.legacySection().serialize(raw);

        serialized = serialized.replace("&", "§");

        Component result = parseToComponent(serialized);

        for (Map.Entry<String, Component> entry : placeholders.entrySet()) {
            String key = entry.getKey();
            Component value = entry.getValue();

            result = result.replaceText(TextReplacementConfig.builder()
                    .matchLiteral(key)
                    .replacement(value)
                    .build()
            );
        }

        return result;
    }

    public static String formatTime(long totalSeconds) {
        if (totalSeconds <= 0) return "0 сек.";

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder builder = new StringBuilder();

        if (hours > 0) builder.append(hours).append(" ч. ");
        if (minutes > 0 || hours > 0) builder.append(minutes).append(" мин. ");
        builder.append(seconds).append(" сек.");

        return builder.toString().trim();
    }

    private static Component parseToComponent(String text) {
        if (text.contains("§")) {
            return legacySection.deserialize(text);
        } else if (text.contains("&")) {
            return legacyAmpersand.deserialize(text);
        } else {
            return mm.deserialize(text);
        }
    }
}
