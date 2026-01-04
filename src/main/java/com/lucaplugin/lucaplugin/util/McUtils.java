package com.lucaplugin.lucaplugin.util;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

public class McUtils {

    private static final Random random = new Random();

    public static void sendConsoleCommand(String command) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static void spawnParticle(Player player, int amount, int interval, Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < amount; i++) {
                    new ParticleBuilder(ParticleEffect.DRIP_LAVA, player.getLocation()).display();
                }
            }
        }.runTaskTimer(plugin, 0, interval);
    }

    public static World getWorld() {
        return Bukkit.getWorld("world");
    }

    public static ChatColor randomColor() {
        int randomInt = generateRandomInt(0, 21);
        int i = 0;
        for (ChatColor chatcolor : ChatColor.values()) {
            if (randomInt == i) {
                return chatcolor;
            }
            i++;
        }
        return ChatColor.WHITE;
    }

    public static DyeColor randomDyeColor() {
        int randomInt = generateRandomInt(0, 15);
        int i = 0;
        for (DyeColor dyeColor : DyeColor.values()) {
            if (randomInt == i) {
                return dyeColor;
            }
            i++;
        }
        return DyeColor.WHITE;
    }

    public static void showBroadcasterMessage(String platformType, String username, String message, boolean isModerator, boolean isSubscriber) {
        String prefix;
        ChatColor usernameColor;

        switch (platformType.toUpperCase()) {
            case "YOUTUBE":
                prefix = ChatColor.RED + "[YT]";
                usernameColor = ChatColor.WHITE;
                break;
            case "YOUNOW":
                prefix = ChatColor.AQUA + "[YN]";
                usernameColor = ChatColor.WHITE;
                break;
            default:
                prefix = ChatColor.GRAY + "[???]";
                usernameColor = ChatColor.WHITE;
                break;
        }

        // Build badges for moderator and subscriber
        StringBuilder badges = new StringBuilder();
        if (isModerator) {
            badges.append(ChatColor.BLUE + "[M]" + ChatColor.RESET);
        }
        if (isSubscriber) {
            badges.append(ChatColor.GOLD + "[S]" + ChatColor.RESET);
        }

        String badgeStr = badges.length() > 0 ? badges.toString() + " " : "";
        Bukkit.broadcastMessage(prefix + " " + badgeStr + usernameColor + username + ": " + ChatColor.RESET + message);
    }

    public static void playSound(Player player, Sound sound, float volume) {
        player.getWorld().playSound(player.getLocation(), sound, volume, 0.5F);
    }

    public static void stopTasks(Plugin plugin) {
        Bukkit.getServer().getScheduler().cancelTasks(plugin);
    }

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static int generateRandomInt(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    public static double generateRandomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    public static void spawnEntityWithParticle(Player player, Particle particle,
            Particle.DustOptions dustOptions, EntityType entityType, int x, int z) {
        Location spawnLocation = findNonBlockY(player.getLocation().add(x, 1, z), player);
        double randomDouble = generateRandomDouble(0.0, 2.5);
        Location location = new Location(player.getWorld(), spawnLocation.getX(),
                spawnLocation.getY() + randomDouble, spawnLocation.getZ());
        player.spawnParticle(particle, location, 30, dustOptions);
        player.getWorld().spawnEntity(location, entityType);
    }

    public static EntityType getRandomEntityType(EntityType[] entityTypes) {
        if (entityTypes.length == 0) {
            return EntityType.BAT;
        }
        return entityTypes[random.nextInt(entityTypes.length)];
    }

    public static void playSoundXTimes(Player player, Sound sound, Float volume, int amount) {
        for (int i = 0; i < amount; i++) {
            player.getWorld().playSound(player.getLocation(), sound, volume, 0.5F);
            wait(generateRandomInt(0, 140));
        }
    }

    public static Location findNonBlockY(Location location, Player player) {
        int x = (int) Math.round(location.getX());
        int y = (int) Math.round(location.getY());
        int z = (int) Math.round(location.getZ());
        while (player.getWorld().getBlockAt(x, y, z).getType() == Material.AIR
                || player.getWorld().getBlockAt(x, y, z).getType() == Material.LAVA) {
            if (player.getWorld().getBlockAt(x, y, z).getType() == Material.LAVA) {
                x += 10;
                z += 10;
                y = 100;
            }
            y--;
        }
        return new Location(player.getWorld(), Math.round(x), y + 1, Math.round(z));
    }

    public static Location findNonBlockYFromTop(Location location, Player player) {
        int x = (int) Math.round(location.getX());
        int y = 155;
        int z = (int) Math.round(location.getZ());
        while (player.getWorld().getBlockAt(x, y, z).getType() == Material.AIR
                || player.getWorld().getBlockAt(x, y, z).getType() == Material.LAVA) {
            if (player.getWorld().getBlockAt(x, y, z).getType() == Material.LAVA) {
                x += 10;
                z += 10;
                y = 100;
            }
            y--;
        }
        return new Location(player.getWorld(), Math.round(x), y + 1, Math.round(z));
    }

    public static void circleEffect(final Player player, Plugin plugin, int timeInSeconds,
            int interval, ParticleEffect particleEffect) {
        new BukkitRunnable() {
            double time = 0;
            double phi = 0;

            public void run() {
                phi = phi + Math.PI / 8;
                Location location1 = player.getLocation();
                for (double t = 0; t <= 2 * Math.PI; t = t + Math.PI / 16) {
                    for (double i = 0; i <= 1; i = i + 1) {
                        double x = 0.4 * (2 * Math.PI - t) * 0.5 * Math.cos(t + phi + i * Math.PI);
                        double y = 0.5 * t;
                        double z = 0.4 * (2 * Math.PI - t) * 0.5 * Math.sin(t + phi + i * Math.PI);
                        location1.add(x, y, z);
                        particleEffect.display(location1);
                        location1.subtract(x, y, z);
                    }
                }
                time += (double) interval / 20;
                if (time > timeInSeconds) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, interval);
    }

    public static void coneEffect(final Player player, Plugin plugin, int timeInSeconds,
            int interval, ParticleEffect particleEffect) {
        new BukkitRunnable() {
            double time = 0;
            double phi = 0;

            public void run() {
                phi = phi + Math.PI / 8;
                Location location1 = player.getLocation();
                for (double t = 0; t <= 2 * Math.PI; t = t + Math.PI / 16) {
                    for (double i = 0; i <= 1; i = i + 1) {
                        double x = 0.4 * (2 * Math.PI - t) * 0.5 * Math.cos(t + phi + i * Math.PI);
                        double y = 0.5 * t;
                        double z = 0.4 * (2 * Math.PI - t) * 0.5 * Math.sin(t + phi + i * Math.PI);
                        location1.add(x, y, z);
                        particleEffect.display(location1);
                        location1.subtract(x, y, z);
                    }
                }
                time += (double) interval / 20;
                if (time > timeInSeconds) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, interval);
    }

    public static void teleportPlayer(Player player, double x, double y, double z, Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(new Location(player.getWorld(), x, y, z));
            }
        }.runTask(plugin);
    }

    public static void sendBigText(String title, String subtitle, String titleColor, String subtitleColor) {
        try {
            sendConsoleCommand(String.format(
                    "title @a title {\"text\":\"%s\", \"bold\":true, \"italic\":true, \"color\":\"%s\"}",
                    title, titleColor));
            sendConsoleCommand(String.format(
                    "title @a subtitle {\"text\":\"%s\", \"bold\":true, \"italic\":true, \"color\":\"%s\"}",
                    subtitle, subtitleColor));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Represents a text segment with customizable styling.
     */
    public static class TextSegment {

        private final String text;
        private final String color;
        private boolean bold = false;
        private boolean italic = false;
        private boolean underlined = false;
        private boolean strikethrough = false;
        private boolean obfuscated = false;

        public TextSegment(String text, String color) {
            this.text = text;
            this.color = color;
        }

        public TextSegment bold() {
            this.bold = true;
            return this;
        }

        public TextSegment italic() {
            this.italic = true;
            return this;
        }

        public TextSegment underlined() {
            this.underlined = true;
            return this;
        }

        public TextSegment strikethrough() {
            this.strikethrough = true;
            return this;
        }

        public TextSegment obfuscated() {
            this.obfuscated = true;
            return this;
        }

        public TextSegment bold(boolean value) {
            this.bold = value;
            return this;
        }

        public TextSegment italic(boolean value) {
            this.italic = value;
            return this;
        }

        public TextSegment underlined(boolean value) {
            this.underlined = value;
            return this;
        }

        public TextSegment strikethrough(boolean value) {
            this.strikethrough = value;
            return this;
        }

        public TextSegment obfuscated(boolean value) {
            this.obfuscated = value;
            return this;
        }

        public String toJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"text\":\"").append(text).append("\"");
            sb.append(",\"color\":\"").append(color).append("\"");
            if (bold) {
                sb.append(",\"bold\":true");
            }
            if (italic) {
                sb.append(",\"italic\":true");
            }
            if (underlined) {
                sb.append(",\"underlined\":true");
            }
            if (strikethrough) {
                sb.append(",\"strikethrough\":true");
            }
            if (obfuscated) {
                sb.append(",\"obfuscated\":true");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    /**
     * Builder for creating styled title/subtitle displays. Supports chaining up
     * to 5 text segments for both title and subtitle.
     */
    public static class TitleBuilder {

        private final java.util.List<TextSegment> titleSegments = new java.util.ArrayList<>();
        private final java.util.List<TextSegment> subtitleSegments = new java.util.ArrayList<>();
        private int fadeIn = 10;
        private int stay = 70;
        private int fadeOut = 20;

        public TitleBuilder addTitle(String text, String color) {
            if (titleSegments.size() < 5) {
                titleSegments.add(new TextSegment(text, color));
            }
            return this;
        }

        public TitleBuilder addTitle(TextSegment segment) {
            if (titleSegments.size() < 5) {
                titleSegments.add(segment);
            }
            return this;
        }

        public TitleBuilder addSubtitle(String text, String color) {
            if (subtitleSegments.size() < 5) {
                subtitleSegments.add(new TextSegment(text, color));
            }
            return this;
        }

        public TitleBuilder addSubtitle(TextSegment segment) {
            if (subtitleSegments.size() < 5) {
                subtitleSegments.add(segment);
            }
            return this;
        }

        public TitleBuilder times(int fadeIn, int stay, int fadeOut) {
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
            return this;
        }

        private String buildJsonArray(java.util.List<TextSegment> segments) {
            if (segments.isEmpty()) {
                return "{\"text\":\"\"}";
            }
            if (segments.size() == 1) {
                return segments.get(0).toJson();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("[{\"text\":\"\"}");
            for (TextSegment seg : segments) {
                sb.append(",").append(seg.toJson());
            }
            sb.append("]");
            return sb.toString();
        }

        /**
         * Send the title to a specific player by username.
         */
        public void sendTo(String playerName) {
            try {
                sendConsoleCommand("title " + playerName + " times " + fadeIn + " " + stay + " " + fadeOut);
                if (!subtitleSegments.isEmpty()) {
                    sendConsoleCommand("title " + playerName + " subtitle " + buildJsonArray(subtitleSegments));
                }
                if (!titleSegments.isEmpty()) {
                    sendConsoleCommand("title " + playerName + " title " + buildJsonArray(titleSegments));
                }
            } catch (Exception e) {
                System.out.println("Error sending title: " + e);
            }
        }

        /**
         * Send the title to a specific player.
         */
        public void sendTo(Player player) {
            sendTo(player.getName());
        }

        /**
         * Send the title to all players.
         */
        public void sendToAll() {
            sendTo("@a");
        }
    }

    /**
     * Create a new TextSegment for use with TitleBuilder.
     *
     * @param text The text content
     * @param color The color (minecraft color name like "red", "gold", "aqua"
     * or hex like "#FF5555")
     */
    public static TextSegment text(String text, String color) {
        return new TextSegment(text, color);
    }

    /**
     * Create a new TitleBuilder for building styled titles.
     */
    public static TitleBuilder titleBuilder() {
        return new TitleBuilder();
    }

    /**
     * Send a styled chat message to a player using tellraw command.
     *
     * @param playerName The player name or selector (e.g., "@a" for all
     * players)
     * @param segments List of text segments to send
     */
    public static void sendStyledChat(String playerName, java.util.List<TextSegment> segments) {
        if (segments == null || segments.isEmpty()) {
            return;
        }
        String json = buildJsonArray(segments);
        sendConsoleCommand("tellraw " + playerName + " " + json);
    }

    /**
     * Send a styled chat message to a specific player.
     *
     * @param player The player to send the message to
     * @param segments List of text segments to send
     */
    public static void sendStyledChat(Player player, java.util.List<TextSegment> segments) {
        sendStyledChat(player.getName(), segments);
    }

    /**
     * Send a styled chat message to all players.
     *
     * @param segments List of text segments to send
     */
    public static void sendStyledChatToAll(java.util.List<TextSegment> segments) {
        sendStyledChat("@a", segments);
    }

    /**
     * Build a JSON array string from a list of text segments.
     */
    private static String buildJsonArray(java.util.List<TextSegment> segments) {
        if (segments.isEmpty()) {
            return "{\"text\":\"\"}";
        }
        if (segments.size() == 1) {
            return segments.get(0).toJson();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[{\"text\":\"\"}");
        for (TextSegment seg : segments) {
            sb.append(",").append(seg.toJson());
        }
        sb.append("]");
        return sb.toString();
    }

}
