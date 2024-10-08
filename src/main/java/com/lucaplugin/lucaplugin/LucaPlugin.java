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
import org.w3c.dom.ls.LSOutput;

import java.sql.Array;
import java.util.*;


public final class LucaPlugin extends JavaPlugin implements Listener
{
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


    @Override
    public void onEnable()
    {
        System.out.println("Started Server");
        McHelperClass mcClass = new McHelperClass();
        /*setupWebsocket();*/
        getServer().getPluginManager().registerEvents(new ListenerClass(eventHandlerObj, this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new onDeathHandler(this), this);
        startWebsocket();
        onChatDistributor.setPlayerList(playersList);
        onGiftDistributor.setPlugin(this);
    }

    class PlayerJoinListener implements Listener
    {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event)
        {
            Player player = event.getPlayer();

            boolean foundPlayer = false;
            for (YouNowPlayer playerItem : playersList)
            {
                if (playerItem.getUsername().equals(player.getName()))
                {
                    foundPlayer = true;
                }
            }

            //Already added player:
            if (foundPlayer)
            {
                player.sendMessage("Welcome back to Server!");
                return;
            }

            //New player
            askQuestion(player, "What is your userId?, type 0 for nothing.");
        }
    }

    @Override
    public void onDisable()
    {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        //Check For Event Commands
        testEventCommands(label, sender, args);
        return false;
    }

    class PlayerChatListener implements Listener
    {
        @EventHandler
        public void onPlayerChat(AsyncPlayerChatEvent event)
        {
            Player player = event.getPlayer();

            UUID playerUUID = player.getUniqueId();
            System.out.println("On Chat triggered");
            System.out.println(questions);

            if (event.getMessage().equals("removeMe"))
            {
                // Remove the player from the playerList
                playersList.removeIf(playerItem -> playerItem.getUsername().equals(player.getName()));
                return;
            }

            if (questions.containsKey(playerUUID))
                connectNewPlayerToWebsocket(event.getMessage(), player);
        }
    }


    public void connectNewPlayerToWebsocket(String message, Player player)
    {
        if (message.trim().isEmpty())
        {
            questions.remove(player.getUniqueId());
            player.sendMessage("Didnt enter anything, have fun!");
            return;
        }

        //Check if message is an integer, if not, return, if yes, parse to int
        try
        {
            int userId = Integer.parseInt(message);

            // Setup Websocket for the player
            connectWebsocketForUserId(userId);
            // Add player to the players map
            playersList.add(new YouNowPlayer(player.getName(), player, userId));
            player.sendMessage("Connected to userId :)");
            questions.remove(player.getUniqueId());
        } catch (NumberFormatException e)
        {
            player.sendMessage("You didn't insert a valid userId sorry. Try again");
        }
    }


    public void askQuestion(Player player, String question)
    {
        questions.put(player.getUniqueId(), question);
        Bukkit.broadcastMessage(ChatColor.AQUA + question);
    }

    class EntityDamageListener implements Listener
    {
        //This removes team dmg
        @EventHandler
        public void onEntityDamage(EntityDamageByEntityEvent event)
        {
            if (event.getDamager() instanceof Player && event.getEntity() instanceof Player)
            {
                Player damager = (Player) event.getDamager();
                Player player = (Player) event.getEntity();
                System.out.println(damager.getName() + player.getName());
                // Check if both players are on the same team
                System.out.println(damager.getScoreboard().getEntryTeam(damager.getName()));
                System.out.println(damager.getScoreboard().getEntryTeam(player.getName()));

                if (damager.getScoreboard().getEntryTeam(damager.getName()).equals(damager.getScoreboard().getEntryTeam(player.getName())))
                {
                    event.setCancelled(true); // Cancel the event to prevent team damage
                }
            }
        }
    }


    private ChatColor getRandomColor()
    {
        Random random = new Random();
        int index = random.nextInt(validColors.size());
        ChatColor color = validColors.get(index);
        validColors.remove(index);
        return color;
    }

    public void testEventCommands(String label, CommandSender sender, String[] args)
    {
        //Register Commands
        if (label.equalsIgnoreCase("startgame"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                player.sendMessage("Starting Game!");
                selectedUser.setPlayer(player);
                gameStarted = true;
            }
        }

        if (label.equalsIgnoreCase("cancelTasks"))
        {

            McHelperClass.stopTasks(this);
        }


        if (label.equalsIgnoreCase("test"))
        {
            if (sender instanceof Player)
            {
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

        if (label.equalsIgnoreCase("raid"))
        {
            long seed = System.currentTimeMillis();

            if (sender instanceof Player)
            {
                for (Player player : Bukkit.getServer().getOnlinePlayers())
                {
                    eventHandler.startLava(player, seed, this,3,3);
                }
            }

            /*
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                try
                {
                    int num = Integer.parseInt(args[0]);
                    onGiftDistributor.triggerEvent(num, player, "DonorName", 123);
                } catch (NumberFormatException e)
                {
                    // Handle the case where input cannot be converted to an integer
                    // You might want to log an error or take some other action here
                }
            }
             */
        }

        if (label.equalsIgnoreCase("tntRain"))
        {
            long seed = System.currentTimeMillis();

            if (sender instanceof Player)
            {
                for (Player player : Bukkit.getServer().getOnlinePlayers())
                {
                    eventHandler.startLava(player, seed, this,1,2);
                }
            }
        }

    }


    public void startWebsocket()
    {
        PusherOptions options = new PusherOptions().setCluster("mt1");
        pusher = new Pusher("42a54e2785b3c81ee7b3", options);
        pusher.connect(new ConnectionEventListener()
                       {
                           @Override
                           public void onConnectionStateChange(ConnectionStateChange change)
                           {
                               System.out.println("State changed to " + change.getCurrentState() + " from " + change.getPreviousState());
                           }

                           @Override
                           public void onError(String message, String code, Exception e)
                           {
                               System.out.println(message);
                               System.out.println(code);
                               System.out.println("There was a problem connecting!");
                           }
                       },
                ConnectionState.ALL);
    }

    private static ArrayList<Integer> connectedList = new ArrayList<>();

    public void connectWebsocketForUserId(int userId)
    {
        //Check if we already connected to the player
        if (connectedList.contains(userId))
        {
            System.out.println(": Someone is already connected to this userId, which is fine, have fun!");
            return;
        }

        connectedList.add(userId);
        System.out.println(": New connection to userId: " + userId);
        Channel channel = pusher.subscribe("public-channel_" + userId);

        // Bind to listen for events called "my-event" sent to "my-channel"
        channel.bind("onGift", new SubscriptionEventListener()
        {
            @Override
            public void onEvent(PusherEvent event)
            {
                System.out.println("Received event with data: " + event.toString());
                onGiftDistributor.triggerEventForGift(event.toString(), userId);
            }
        });

        //Bind chat for invite moment and fan events
        channel.bind("onChat", new SubscriptionEventListener()
        {
            @Override
            public void onEvent(PusherEvent event)
            {
                System.out.println("Received event with data: " + event.toString());
                onChatDistributor.triggerEventForChat(event.toString(), userId);
            }
        });


        // Reconnect, with all channel subscriptions and event bindings automatically recreated
        pusher.connect();
    }
}
