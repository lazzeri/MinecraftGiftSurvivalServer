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

    @Override
    public void onEnable()
    {
        setupWebsocket();
    }

    @Override
    public void onDisable()
    {

    }

    player joinedPlayer = new player();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (label.equalsIgnoreCase("startgame"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                player.sendMessage("Starting Game!");
                joinedPlayer.setPlayer(player);
                return true;
            }
        }
        return false;
    }

    public void triggerData(String jsonString)
    {
        JSONObject obj = new JSONObject(jsonString.replace("{event=onGift, data=", "").replace(", channel=public-channel_14443322}", ""));

        JSONArray jsonArray = obj.getJSONObject("message").getJSONArray("stageGifts");

        for (int i = 0; i < jsonArray.length(); i++)
        {
            //Trigger here objects for gifts
            String skuName = jsonArray.getJSONObject(i).getString("SKU");
            System.out.println(skuName);
            if (joinedPlayer.getPlayer() != null)
            {
                System.out.println("Sending data to " + joinedPlayer.getPlayer().getName());
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
        String userId = "14443322";
        Channel channel = pusher.subscribe("public-channel_" + userId);

        // Bind to listen for events called "my-event" sent to "my-channel"
        channel.bind("onGift", new SubscriptionEventListener()
        {
            @Override
            public void onEvent(PusherEvent event)
            {
                System.out.println("Received event with data: " + event.toString());
                triggerData(event.toString());

            }
        });

        // Disconnect from the service
        pusher.disconnect();

        // Reconnect, with all channel subscriptions and event bindings automatically recreated
        pusher.connect();
    }

    //Helper Classes:
    public boolean checkIfOnline(String userName)
    {
        return Bukkit.getServer().getPlayer(userName) != null;
    }


    public void triggerMessages(String jsonString)
    {
        JSONObject obj = new JSONObject(jsonString);
        String skuName = "";
        JSONArray jsonArray = obj.getJSONObject("data").getJSONArray("stageGifts");
        for (int i = 0; i < jsonArray.length(); i++)
        {
            //Trigger here objects for gifts
            skuName = jsonArray.getJSONObject(i).getString("SKU");
            System.out.println(skuName);
        }
    }


}
