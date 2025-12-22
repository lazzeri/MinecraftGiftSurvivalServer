package com.lucaplugin.lucaplugin;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.ls.LSOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Array;
import java.util.*;

public final class LucaPlugin extends JavaPlugin implements Listener {

    public static double xBorderCenter = 0, yBorderCenter = 0;

    private List<ChatColor> validColors = new ArrayList<>(Arrays.asList(
            ChatColor.AQUA,
            ChatColor.DARK_GRAY,
            ChatColor.DARK_RED,
            ChatColor.GOLD,
            ChatColor.GRAY,
            ChatColor.GREEN,
            ChatColor.LIGHT_PURPLE,
            ChatColor.RED,
            ChatColor.WHITE,
            ChatColor.YELLOW
    ));

    public Plugin plugin = this;
    public static boolean gameStarted = false;
    player selectedUser = new player();
    eventHandler eventHandlerObj = new eventHandler();
    spawnSystem spawnSystemObj = new spawnSystem();

    public static ArrayList<YouNowPlayer> playersList = new ArrayList<YouNowPlayer>();

    private final Map<UUID, String> questions = new HashMap<>();
    public Scoreboard scoreboard;
    public Pusher pusher;

    private final String API_KEY = "AIzaSyAVcO_Za8I4tpIb6AQPei3y-q2mz4MoZfw";
    private final String CHANNEL_ID = "UCXbboag48Qlr78zzz6SkzkQ";
    private String liveChatId;

    private void startPollingChat() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                pollLiveChat(API_KEY, liveChatId);
            } catch (IOException e) {\
                e.printStackTrace();
            }
        }, 0L, 100L); // Every 5 seconds (100 ticks)
    }

    public void handleLiveChatMessage(String author, String message, Player player) {
        player.sendMessage(ChatColor.YELLOW + "[YouTube] " + author + ": " + ChatColor.WHITE + message);
        System.out.println("Received message from " + author + ": " + message);
    }


    

    @Override
    public void onEnable() {
        System.out.println("Started Server Test 234");

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                String videoId = getLiveVideoId(API_KEY, CHANNEL_ID);
                if (videoId != null) {
                    liveChatId = getLiveChatId(API_KEY, videoId);
                }

                if (liveChatId != null) {
                    startPollingChat();
                } else {
                    getLogger().warning("No live stream found.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        McHelperClass mcClass = new McHelperClass();
        /*setupWebsocket();*/
        getServer().getPluginManager().registerEvents(new ListenerClass(eventHandlerObj, this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new onDeathHandler(this), this);
        // No Younow Websocket for now startWebsocket();
        onChatDistributor.setPlayerList(playersList);
        onGiftDistributor.setPlayerList(playersList);
        onGiftDistributor.setPlugin(this);
    }

    class PlayerJoinListener implements Listener {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();

            boolean foundPlayer = false;
            for (YouNowPlayer playerItem : playersList) {
                if (playerItem.getUsername().equals(player.getName())) {
                    foundPlayer = true;
                }
            }

            //Already added player:
            if (foundPlayer) {
                player.sendMessage("Welcome back to Server!");
                return;
            }

            //New player
            askQuestion(player, "What is your userId?, type 0 for nothing.");
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //Check For Event Commands
        testEventCommands(label, sender, args);
        return false;
    }

    class PlayerChatListener implements Listener {

        @EventHandler
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            Player player = event.getPlayer();

            UUID playerUUID = player.getUniqueId();
            System.out.println("On Chat triggered");
            System.out.println(questions);

            if (event.getMessage().equals("removeMe")) {
                // Remove the player from the playerList
                playersList.removeIf(playerItem -> playerItem.getUsername().equals(player.getName()));
                return;
            }
        }
    }

    public void askQuestion(Player player, String question) {
        questions.put(player.getUniqueId(), question);
        Bukkit.broadcastMessage(ChatColor.AQUA + question);
    }

    class EntityDamageListener implements Listener {

        //This removes team dmg
        @EventHandler
        public void onEntityDamage(EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
                Player damager = (Player) event.getDamager();
                Player player = (Player) event.getEntity();
                System.out.println(damager.getName() + player.getName());
                // Check if both players are on the same team
                System.out.println(damager.getScoreboard().getEntryTeam(damager.getName()));
                System.out.println(damager.getScoreboard().getEntryTeam(player.getName()));

                if (damager.getScoreboard().getEntryTeam(damager.getName()).equals(damager.getScoreboard().getEntryTeam(player.getName()))) {
                    event.setCancelled(true); // Cancel the event to prevent team damage
                }
            }
        }
    }

    public void testEventCommands(String label, CommandSender sender, String[] args) {
        //Register Commands
        if (label.equalsIgnoreCase("startgame")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage("Starting Game!");
                selectedUser.setPlayer(player);
                gameStarted = true;
            }
        }

        if (label.equalsIgnoreCase("cancelTasks")) {

            McHelperClass.stopTasks(this);
        }

        if (label.equalsIgnoreCase("test")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                // Give full Netherite armor
                ItemStack netheriteHelmet = new ItemStack(Material.NETHERITE_HELMET);
                ItemStack netheriteChestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
                ItemStack netheriteLeggings = new ItemStack(Material.NETHERITE_LEGGINGS);
                ItemStack netheriteBoots = new ItemStack(Material.NETHERITE_BOOTS);
                ItemStack elytra = new ItemStack(Material.ELYTRA);

                player.getInventory().setHelmet(netheriteHelmet);
                player.getInventory().setChestplate(elytra);
                player.getInventory().setLeggings(netheriteLeggings);
                player.getInventory().setBoots(netheriteBoots);

                // Enchant a Netherite sword
                ItemStack netheriteSword = new ItemStack(Material.NETHERITE_SWORD);
                ItemMeta swordMeta = netheriteSword.getItemMeta();
                swordMeta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
                netheriteSword.setItemMeta(swordMeta);

                player.getInventory().addItem(netheriteSword);

                // Give Elytra and rockets
                ItemStack rockets = new ItemStack(Material.FIREWORK_ROCKET, 64); // You can adjust the quantity

                player.getInventory().addItem(rockets);

                // Give steak
                ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64); // You can adjust the quantity

                player.getInventory().addItem(steak);
                player.getInventory().addItem(steak);
                AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                attribute.setBaseValue(40.0D);
            }
        }

        if (label.equalsIgnoreCase("raid")) {
            long seed = System.currentTimeMillis();

            /* if (sender instanceof Player)
            {
                for (Player player : Bukkit.getServer().getOnlinePlayers())
                {
                    eventHandler.startLava(player, seed, this,3,3);
                }
            }*/
            if (sender instanceof Player) {
                Player player = (Player) sender;
                try {
                    int num = Integer.parseInt(args[0]);
                    onGiftDistributor.triggerEvent(num, player, "DonorName", 123);
                } catch (NumberFormatException e) {
                    // Handle the case where input cannot be converted to an integer
                    // You might want to log an error or take some other action here
                }
            }

        }

        if (label.equalsIgnoreCase("tntRain")) {
            long seed = System.currentTimeMillis();

            if (sender instanceof Player) {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    eventHandler.startLava(player, seed, this, 1, 2);
                }
            }
        }

    }

    /**
     * Establishes a WebSocket connection to a YouNow channel, to receive gift
     * and chat events. This method is called by the plugin's onEnable method,
     * and is not intended to be called manually.
     *
     * @throws URISyntaxException If there is a problem with the WebSocket
     * connection.
     */
    public void startWebsocket() {
        try {
            YouNowWebSocketClient client = new YouNowWebSocketClient("61651035");
            client.connect();
            System.out.println("Connected to YouNow WebSocket!");
        } catch (URISyntaxException e) {
            System.out.println("Failed to create WebSocket connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * *********** ✨ Windsurf Command ⭐ ************
     */
    /**
     * Retrieves the ID of the current live video on a YouTube channel, using
     * the YouTube Data API v3. This method is intended to be used by the
     * plugin's onEnable method, and is not intended to be called manually.
     *
     * @param apiKey The API key to use for the request.
     * @param channelId The ID of the YouTube channel to retrieve the live video
     * for.
     * @return The ID of the current live video, or null if no live video is
     * found.
     * @throws IOException If there is a problem with the request.
     */
    /**
     * ***** 5b67c62d-c9d1-4254-be61-5711090ccc6c ******
     */
    public String getLiveVideoId(String apiKey, String channelId) throws IOException {
        String urlStr = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId="
                + channelId + "&order=date&type=video&key=" + apiKey;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject json = new JSONObject(response.toString());
        JSONArray items = json.getJSONArray("items");

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            JSONObject snippet = item.getJSONObject("snippet");
            if (snippet.getString("liveBroadcastContent").equals("live")) {
                return item.getJSONObject("id").getString("videoId");
            }
        }

        return null; // No live video found
    }

    public String getLiveChatId(String apiKey, String videoId) throws IOException {
        String urlStr = "https://www.googleapis.com/youtube/v3/videos?part=liveStreamingDetails&id="
                + videoId + "&key=" + apiKey;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject json = new JSONObject(response.toString());
        JSONArray items = json.getJSONArray("items");
        if (items.length() > 0) {
            JSONObject details = items.getJSONObject(0)
                    .getJSONObject("liveStreamingDetails");
            return details.getString("activeLiveChatId");
        }

        return null;
    }

    public void pollLiveChat(String apiKey, String liveChatId) throws IOException {
        String urlStr = "https://www.googleapis.com/youtube/v3/liveChat/messages?liveChatId="
                + liveChatId + "&part=snippet,authorDetails&key=" + apiKey;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject json = new JSONObject(response.toString());
        JSONArray items = json.getJSONArray("items");

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            JSONObject snippet = item.getJSONObject("snippet");
            JSONObject author = item.getJSONObject("authorDetails");
            String message = snippet.getString("displayMessage");
            String authorName = author.getString("displayName");

            // Send message to all online players in Minecraft
            for (Player player : Bukkit.getOnlinePlayers()) {
                handleLiveChatMessage(authorName, message, player);
            }
        }
    }

}
