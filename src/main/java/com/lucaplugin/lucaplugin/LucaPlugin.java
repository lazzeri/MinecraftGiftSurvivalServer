package com.lucaplugin.lucaplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;


public final class LucaPlugin extends JavaPlugin implements Listener
{
    private List<ChatColor> validColors = new ArrayList<>(Arrays.asList(
            ChatColor.AQUA,
            ChatColor.DARK_BLUE,
            ChatColor.DARK_GRAY,
            ChatColor.DARK_GREEN,
            ChatColor.DARK_PURPLE,
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
    public static int userId = 7746914;
    player selectedUser = new player();
    eventHandler eventHandlerObj = new eventHandler();
    spawnSystem spawnSystemObj = new spawnSystem();
    static String[] modNames = {"Luca"};
    private Map<UUID, String> questions = new HashMap<>();
    public Scoreboard scoreboard;

    @Override
    public void onEnable()
    {
        /*setupWebsocket();*/
        getServer().getPluginManager().registerEvents(new ListenerClass(eventHandlerObj, this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new onDeathHandler(), this);
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    }

    class PlayerJoinListener implements Listener
    {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event)
        {
            //TODO Remove later
            //spawnSystemObj.emptyPlayerList();
            Player player = event.getPlayer();

            for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            {
                onlinePlayer.setScoreboard(scoreboard);
            }

            //Check if already registered to tournament, if not, we
            if (!spawnSystemObj.checkIfPlayerInList(player.getName()))
            {
                McHelperClass.teleportPlayer(player, -22, 78, -8, plugin);
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
            System.out.println(player.getName());

            UUID playerUUID = player.getUniqueId();
            System.out.println("On Chat triggered");
            System.out.println(questions);
            if (questions.containsKey(playerUUID))
            {
                checkForNewPlayerAndConnect(event.getMessage(), player);
                return;
            }

            Team team = scoreboard.getEntryTeam(player.getName());

            if (team != null)
            {
                String teamPrefix = ChatColor.GREEN + team.getPrefix();
                String message = teamPrefix + "" + player.getName() + ": " + ChatColor.WHITE + event.getMessage();
                event.setCancelled(true);
                for (Player recipient : event.getRecipients())
                {
                    recipient.sendMessage(message);
                }
            }

            //Removes User
            if (player.getName().equals("Bruzzelpia"))
            {
                //remove asd
                checkForRemovingTeam(event);
            }
        }
    }

    public void checkForRemovingTeam(AsyncPlayerChatEvent event)
    {
        String[] words = event.getMessage().split(" ");
        if (!Objects.equals(words[0], "remove") && words.length != 2)
        {
            System.out.println(words.length);
            return;
        }

        Team team2 = scoreboard.getTeam(words[1].toUpperCase());
        //remove the entries
        assert team2 != null;
        for (String entry : team2.getEntries())
        {
            Bukkit.getScheduler().runTask(plugin, new Runnable()
            {
                public void run()
                {
                    Player p = Bukkit.getPlayer(entry);
                    spawnSystemObj.removePlayerFromList(p);
                    System.out.println("Player" + p.getName());
                    Bukkit.getPlayer(entry).kickPlayer("You can now register with a new Team Name!");
                    scoreboard.resetScores(entry);
                }
            });
        }
        team2.unregister();
        // Check if the team has no entries
        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
        {
            onlinePlayer.setScoreboard(scoreboard);
        }
        event.setCancelled(true);
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
            addToScoreBoard(player, message.toUpperCase());
            McHelperClass.teleportPlayer(player, 100, 100, 100, plugin);

        } else
        {
            System.out.println("Player is already in list with broadcast name, not able to finish registering");
        }

    }

    public void addToScoreBoard(Player player, String teamName)
    {
        System.out.println("Trying to add" + player.getName());
        Team team = scoreboard.getTeam(teamName);

        if (team == null)
        {
            team = scoreboard.registerNewTeam(teamName);
            team.setColor(getRandomColor());
        }
        team.addEntry(player.getName());
        team.setPrefix(team.getColor() + "[" + teamName + "] ");

        player.setPlayerListHeader(team.getPrefix()); // set player list header
        player.setPlayerListFooter(""); // clear player list footer
        player.setCustomName(team.getPrefix() + player.getName()); // set custom name
        player.setCustomNameVisible(true); // show custom name
        System.out.println(teamName);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
        {
            onlinePlayer.setScoreboard(scoreboard);
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
