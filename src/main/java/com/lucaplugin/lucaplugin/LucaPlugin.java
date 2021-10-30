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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLOutput;


public final class LucaPlugin extends JavaPlugin implements Listener
{
    public  static int userId = 54816052;
    @Override
    public void onEnable()
    {
        setupWebsocket();
    }

    @Override
    public void onDisable()
    {

    }

    player selectedUser = new player();
    eventHandler eventHandlerObj = new eventHandler();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (label.equalsIgnoreCase("startgame"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                player.sendMessage("Starting Game!");
                selectedUser.setPlayer(player);
                return true;
            }
        }

        if (label.equalsIgnoreCase("test"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                eventHandlerObj.itemSnack(player,"TestName");
                return true;
            }
        }

        return false;
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
                onGiftDistributor.triggerEventForGift(event.toString(), selectedUser.getPlayer(),userId);

            }
        });

        //Bind chat for invite moment and fan events
        channel.bind("onChat", new SubscriptionEventListener()
        {
            @Override
            public void onEvent(PusherEvent event)
            {
                System.out.println("Received event with data: " + event.toString());
                onChatDistributor.triggerEventForChat(event.toString(), selectedUser.getPlayer(),"TestUser",userId);
            }
        });
        
        
        
        // Disconnect from the service
        pusher.disconnect();

        // Reconnect, with all channel subscriptions and event bindings automatically recreated
        pusher.connect();
    }
}
