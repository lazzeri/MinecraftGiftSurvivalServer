package com.lucaplugin.lucaplugin;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.xml.stream.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class LucaPlugin extends JavaPlugin implements Listener
{
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
    }

    class PlayerJoinListener implements Listener
    {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event)
        {
            Player player = event.getPlayer();

            //Check if already registered to tournament, if not, we
            if (!spawnSystemObj.checkIfPlayerInList(player.getName()))
            {
                // Freeze Player for registration
                player.setWalkSpeed(0f);
                player.setFlySpeed(0f);

                //Remove the Player data to create a new one:
                spawnSystemObj.removePlayerFromList(player);

                //Welcome and ask first Question
                player.sendMessage("Welcome to Event!");
                player.sendMessage("Will you be a leader? type /yes or /no in the chat.");
            }


            // Handle the player's response...
            //


        }
    }

    @Override
    public void onDisable()
    {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        //Check for Register Commands
        checkForRegisterEvents(label, sender);
        //Check For Event Commands
        testEventCommands(label, sender);
        return false;
    }


    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        //TODO Here we check if we have no broadcaster set and a question missing:
        if (questions.containsKey(playerUUID))
        {
            checkForConnectingToBroadcasterAndConnect(event.getMessage(), player);
            questions.remove(playerUUID);
        }
    }

    public void askQuestion(Player player, String question)
    {
        questions.put(player.getUniqueId(), question);
        Bukkit.broadcastMessage(ChatColor.AQUA + question);
    }

    public void checkForConnectingToBroadcasterAndConnect(String message, Player player)
    {
        //Here we check the state of the broadcaster.

        //If not online: we ask again:
        askQuestion(player, "This user is not live. Wait till he is live and join!");
        //If not exisitng we ask again:
        askQuestion(player, "This user does not exist.. Please enter the Broadcaster Name again.");

        //If online, we add the broadcaster player.
    }

    public void checkForRegisterEvents(String label, CommandSender sender)
    {
        //If the answer is yes no, its the first register question.
        if (label.equalsIgnoreCase("yes") || label.equalsIgnoreCase("no"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;

                //If not registered, which is only in the first phase, we register

                if (!spawnSystemObj.checkIfPlayerInList(player.getName()))
                {
                    if (label.equalsIgnoreCase("yes"))
                    {
                        spawnSystemObj.addPlayerToArrayLists(new YouNowPlayer(player.getName(), "", player.getName()));
                    }

                    if (label.equalsIgnoreCase("no"))
                    {
                        spawnSystemObj.addPlayerToArrayLists(new YouNowPlayer(player.getName(), "", ""));
                    }
                    askQuestion(player, "Awesome? What is the broadcasters name where your leader is live while the tournament?");
                }
            }
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

    public void setupWebsocket()
    {
        PusherOptions options = new PusherOptions().setCluster("younow");
        Pusher pusher = new Pusher("d5b7447226fc2cd78dbb", options);

        pusher.connect(new ConnectionEventListener()
                       {
                           @Override
                           public void onConnectionStateChange(ConnectionStateChange change)
                           {
                               System.out.println("State changed to " + change.getCurrentState() +
                                       " from " + change.getPreviousState());
                           }

                           @Override
                           public void onError(String message, String code, Exception e)
                           {
                               System.out.println("There was a problem connecting!");
                           }
                       },
                ConnectionState.ALL);

        // Subscribe to a channel
        Channel channel = pusher.subscribe("public-channel_" + userId);

        // Bind to listen for events called "my-event" sent to "my-channel"
        channel.bind("onGift", new SubscriptionEventListener()
        {
            @Override
            public void onEvent(PusherEvent event)
            {
                System.out.println("Received event with data: " + event.toString());
                onGiftDistributor.triggerEventForGift(event.toString(), selectedUser.getPlayer(), userId);

            }
        });

        //Bind chat for invite moment and fan events
        channel.bind("onChat", new SubscriptionEventListener()
        {
            @Override
            public void onEvent(PusherEvent event)
            {
                System.out.println("Received event with data: " + event.toString());
                onChatDistributor.triggerEventForChat(event.toString(), selectedUser.getPlayer(), "TestUser", userId);
            }
        });

        // Disconnect from the service
        pusher.disconnect();

        // Reconnect, with all channel subscriptions and event bindings automatically recreated
        pusher.connect();
    }
}
