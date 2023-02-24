package com.lucaplugin.lucaplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class LucaPlugin extends JavaPlugin implements Listener
{
    public Plugin plugin = this;
    public static boolean gameStarted = false;
    public static int userId = 7746914;
    player selectedUser = new player();
    eventHandler eventHandlerObj = new eventHandler();
    spawnSystem spawnSystemObj = new spawnSystem();
    static String[] modNames = {"Luca"};
    private Map<UUID, String> questions = new HashMap<>();


    @Override
    public void onEnable()
    {
        /*setupWebsocket();*/
        getServer().getPluginManager().registerEvents(new ListenerClass(eventHandlerObj, this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new onDeathHandler(), this);
    }

    class PlayerJoinListener implements Listener
    {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event)
        {
            //TODO Remove later
            spawnSystemObj.emptyPlayerList();
            Player player = event.getPlayer();

            //Check if already registered to tournament, if not, we
            if (!spawnSystemObj.checkIfPlayerInList(player.getName()))
            {
                McHelperClass.teleportPlayer(player, -22, 78, -8,plugin);
                player.sendMessage("Welcome to Event!");
                askQuestion(player, "What is your team name? ___");
            } else
            {
                player.sendMessage("Welcome Back, good luck!");
            }
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
        testEventCommands(label, sender);
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
            if (questions.containsKey(playerUUID))
            {
                checkForNewPlayerAndConnect(event.getMessage(), player);
            }
        }
    }

    public void askQuestion(Player player, String question)
    {
        questions.put(player.getUniqueId(), question);
        Bukkit.broadcastMessage(ChatColor.AQUA + question);
    }

    public void checkForNewPlayerAndConnect(String message, Player player)
    {
        if (!spawnSystemObj.checkIfPlayerInList(player.getName()))
        {
            //If online, we add the broadcaster player.
            //Check message if its 3 letters
            if (message.length() != 3)
            {
                questions.remove(player.getUniqueId());
                askQuestion(player, "The answer must be 3 chars long! What is your team name? ___");
                return;
            }

            player.sendMessage("You are registered for the Event, good luck!");
            spawnSystemObj.addPlayerToArrayLists(message.toUpperCase(), player.getName());
            questions.remove(player.getUniqueId());
            McHelperClass.teleportPlayer(player, 100, 100, 100,plugin);

        } else
        {
            System.out.println("Player is already in list with broadcast name, not able to finish registering");
        }

    }

    public void testEventCommands(String label, CommandSender sender)
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


        if (!gameStarted)
            System.out.println("Game not started yet!");


        if (label.equalsIgnoreCase("test"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                eventHandlerObj.test(player, "TestName", this);
            }
        }

        if (label.equalsIgnoreCase("raid"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                eventHandlerObj.createVillagerCircle(player, "TestName", 10);
            }
        }

        if (label.equalsIgnoreCase("tntRain"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                eventHandlerObj.tntRain(player, "TestName", this);
            }
        }
    }
}
