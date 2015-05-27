package dota2drafter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import static javax.swing.ScrollPaneConstants.*;
import net.miginfocom.swing.MigLayout;

public class WindowManager {
    int activeWindow = 0;
    JFrame Adam = new JFrame();
    MainScreen main;
    DraftScreen draft;
    ManageScreen manage;
    BrowseScreen browse;
    
    public WindowManager(){
        Adam.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main = new MainScreen();
        int cat = 41;
    }
    
    
    class MainScreen extends Screen {
        JPanel teamDraft = new JPanel(new MigLayout("wrap 2","[grow 50][grow 50]","[grow 10][grow 90, fill]"));
        JLabel teamDraftL = new JLabel("Team Draft");
        JPanel teams = new JPanel(new MigLayout("flowy", "grow, fill"));        
        JPanel players = new JPanel(new MigLayout("flowy", "grow, fill"));
        JButton quick = new JButton("Quick Draft");
        JButton viewAll = new JButton("View all heroes");
        JLabel myTeams = new JLabel("My Teams");
        JButton manageTeams = new JButton("Manage Teams");
        JLabel myPlayers = new JLabel("Players");
        JButton managePlayers = new JButton("Manage Players");
        JPanel teamsInner = new JPanel(new MigLayout("flowy", "grow, fill"));
        JPanel playersInner = new JPanel(new MigLayout("flowy", "grow, fill"));
        JScrollPane teamScroll = new JScrollPane(teamsInner);        
        JScrollPane playerScroll = new JScrollPane(playersInner);
        Dimension windowSize = new Dimension(500, 500);
        Screen called;
        
        public MainScreen() {
            
            Eve.setLayout(new MigLayout("wrap 2","[0:0, grow 50, fill][0:0, grow 50, fill]","[pref!][grow, fill]"));
            
            quick.addActionListener((ActionEvent e) -> {
                DraftScreen draft = new DraftScreen(null, this);
                draft.Switch();
            });
            
            viewAll.addActionListener((ActionEvent e) -> {
                called = new BrowseScreen(this);
                called.Switch();
            });
            
            manageTeams.addActionListener((ActionEvent e) -> {
                called = new ManageScreen("team", this);
                called.Switch();
            });
            
            managePlayers.addActionListener((ActionEvent e) -> {
                called = new ManageScreen("player", this);
                called.Switch();
            });
            
            teams.setBorder(BorderFactory.createLineBorder(Color.black));
            players.setBorder(BorderFactory.createLineBorder(Color.black));
            
            teamScroll.setBorder(BorderFactory.createEmptyBorder());
            teamScroll.getVerticalScrollBar().setUnitIncrement(16);
            teamScroll.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
            playerScroll.setBorder(BorderFactory.createEmptyBorder());
            playerScroll.getVerticalScrollBar().setUnitIncrement(16);
            playerScroll.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
            
            this.DrawScreen();          
            
            Adam.setMinimumSize(windowSize);
            Adam.setLocationRelativeTo(null);
            Adam.setVisible(true);
        }
        
        void DrawScreen () {
            
            teams.add(myTeams);
            teams.add(manageTeams, "growx");
            teams.add(new JSeparator(), "growx");
            
            for (Team team: Global.Teams) {
                JPanel thisTeam = team.TeamPreview();
                JButton startTeam = new JButton("Draft!");
                startTeam.addActionListener((ActionEvent e) -> {
                    DraftScreen called = new DraftScreen(team, this);
                    called.Switch();
                });
                thisTeam.add(startTeam, "east");
                thisTeam.setBorder(BorderFactory.createLineBorder(Color.black));
                teamsInner.add(thisTeam, "growx");
            }
            teams.add(teamScroll);
            
            players.add(myPlayers);
            players.add(managePlayers, "growx");
            players.add(new JSeparator());
            
            for (Player player: Global.Players) {
                JPanel thisPlayer = player.PlayerPreview();
                JButton modify = new JButton(ResourceRetriever.GetImage("Edit.png", 16, 16));
                modify.setMargin(new Insets(0,0,0,0));
                modify.addActionListener((ActionEvent e) -> {
                    new ModifyPlayerPopup(player, this, null);
                });
                thisPlayer.add(modify, "east");
                thisPlayer.setBorder(BorderFactory.createLineBorder(Color.black));
                playersInner.add(thisPlayer, "growx");
            }
            players.add(playerScroll);
            
            teamDraft.add(teamDraftL, "span, center");
            teamDraft.add(teams, "grow");
            teamDraft.add(players, "grow");
            
            Eve.add(quick);
            Eve.add(viewAll);
            Eve.add(teamDraft, "span");
            
            Adam.add(Eve);
            Adam.setSize(windowSize);
        }
        
        @Override
        void Switch() {
            // Nothing here to see
        }
        
        @Override
        void Return() {
            Adam.remove(called.Eve);
            Adam.add(Eve);            
            Adam.revalidate();
            Adam.setMinimumSize(windowSize);
            Adam.setSize(windowSize);
            Adam.repaint();
        }

        @Override
        void Refresh() {
            teamDraft.removeAll();
            teams.removeAll();
            players.removeAll();
            teamsInner.removeAll();;
            playersInner.removeAll();
            Eve.removeAll();
            this.DrawScreen();
            Eve.revalidate();
            Eve.repaint();
        }
    }
    
    class DraftScreen extends Screen{
        JPanel matchup = new JPanel(new MigLayout("inset 0","[0:0, grow, center][center][0:0, grow, center]",""));
        JPanel myPicks = new JPanel(new MigLayout("inset 0", "grow, center"));
        JPanel myPicksPortraits = new JPanel(new MigLayout(""));
        JPanel theirPicks = new JPanel(new MigLayout("inset 0", "grow, center"));
        JPanel theirPicksPortraits = new JPanel(new MigLayout(""));
        JPanel myBans = new JPanel(new MigLayout("inset 0", "grow, center"));
        JPanel myBansPortraits = new JPanel(new MigLayout(""));
        JPanel theirBans = new JPanel(new MigLayout("inset 0", "grow, center"));
        JPanel theirBansPortraits = new JPanel(new MigLayout(""));
        JPanel players = new JPanel(new MigLayout("inset 0, flowy", "[grow, fill]", ""));
        JPanel theirPool = new JPanel(new MigLayout("inset 0, flowy", "[grow, fill]", ""));
        JLabel teamName = new JLabel("Team #1");
        JLabel enemyName = new JLabel("Team #2");
        JLabel myPick1 = new JLabel(Global.QUESTIONPIC);
        JLabel myPick2 = new JLabel(Global.QUESTIONPIC);
        JLabel myPick3 = new JLabel(Global.QUESTIONPIC);
        JLabel myPick4 = new JLabel(Global.QUESTIONPIC);
        JLabel myPick5 = new JLabel(Global.QUESTIONPIC);
        JLabel myBan1 = new JLabel(Global.QUESTIONPIC);
        JLabel myBan2 = new JLabel(Global.QUESTIONPIC);
        JLabel myBan3 = new JLabel(Global.QUESTIONPIC);
        JLabel myBan4 = new JLabel(Global.QUESTIONPIC);
        JLabel myBan5 = new JLabel(Global.QUESTIONPIC);
        JLabel enemyPick1 = new JLabel(Global.QUESTIONPIC);
        JLabel enemyPick2 = new JLabel(Global.QUESTIONPIC);
        JLabel enemyPick3 = new JLabel(Global.QUESTIONPIC);
        JLabel enemyPick4 = new JLabel(Global.QUESTIONPIC);
        JLabel enemyPick5 = new JLabel(Global.QUESTIONPIC);
        JLabel enemyBan1 = new JLabel(Global.QUESTIONPIC);
        JLabel enemyBan2 = new JLabel(Global.QUESTIONPIC);
        JLabel enemyBan3 = new JLabel(Global.QUESTIONPIC);
        JLabel enemyBan4 = new JLabel(Global.QUESTIONPIC);
        JLabel enemyBan5 = new JLabel(Global.QUESTIONPIC);
        JLabel player1 = new JLabel("Player #1");
        JLabel player2 = new JLabel("Player #2");
        JLabel player3 = new JLabel("Player #3");
        JLabel player4 = new JLabel("Player #4");
        JLabel player5 = new JLabel("Player #5");
        Dimension windowSize = new Dimension(700, 1000);
        Team team;
        Screen caller;
        
        public DraftScreen(Team team, Screen caller) {
            Eve = new JPanel(new MigLayout("wrap 2", "[grow, center, fill][grow, center, fill]"));
            this.team = team;
            this.caller = caller;
            DrawScreen();
        }
        
        void DrawScreen() {
            matchup.add(teamName);
            matchup.add(new JLabel("vs."));
            matchup.add(enemyName);
            
            myPicks.add(new JLabel("picks"), "span, center, wrap");
            myPicksPortraits.add(myPick1);
            myPicksPortraits.add(myPick2);
            myPicksPortraits.add(myPick3);
            myPicksPortraits.add(myPick4);
            myPicksPortraits.add(myPick5);
            myPicks.add(myPicksPortraits);
            
            theirPicks.add(new JLabel("picks"), "span, center, wrap");
            theirPicksPortraits.add(enemyPick1);
            theirPicksPortraits.add(enemyPick2);
            theirPicksPortraits.add(enemyPick3);
            theirPicksPortraits.add(enemyPick4);
            theirPicksPortraits.add(enemyPick5);
            theirPicks.add(theirPicksPortraits);
            
            myBans.add(new JLabel("bans"), "span, center, wrap");
            myBansPortraits.add(myBan1);
            myBansPortraits.add(myBan2);
            myBansPortraits.add(myBan3);
            myBansPortraits.add(myBan4);
            myBansPortraits.add(myBan5);
            myBans.add(myBansPortraits);
            
            theirBans.add(new JLabel("bans"), "span, center, wrap");
            theirBansPortraits.add(enemyBan1);
            theirBansPortraits.add(enemyBan2);
            theirBansPortraits.add(enemyBan3);
            theirBansPortraits.add(enemyBan4);
            theirBansPortraits.add(enemyBan5);
            theirBans.add(theirBansPortraits);
            
            if (team == null) {                
                players.add(new PoolBuilder(false, "small", null, null).pool);
            } else {
                for (Player player: team.GetPlayers()) {
                    players.add(new JLabel(player.name));
                    JPanel heroes = new JPanel(new WrapLayout(FlowLayout.LEADING));
                    for (Hero hero: player.GetPlayList()) {
                        heroes.add(new JLabel(hero.portraitSmall));
                    }
                    heroes.setBorder(BorderFactory.createLineBorder(Color.black));
                    players.add(heroes);
                }
            }
            theirPool.add(new PoolBuilder(false, "small", null, null).pool);
            
            Eve.add(matchup, "span, grow");
            Eve.add(myPicks, "c");
            Eve.add(theirPicks, "c");
            Eve.add(myBans, "c");
            Eve.add(theirBans, "c");
            Eve.add(players);
            Eve.add(theirPool);
            
            Adam.setSize(windowSize);
        }
        
        @Override
        void Refresh() {
        }

        @Override
        void Switch() {
            Adam.remove(caller.Eve);
            Adam.add(Eve);            
            Adam.revalidate();
            Adam.setMinimumSize(windowSize);
            Adam.repaint();
        }

        @Override
        void Return() {
        }
    }
    
    class ManageScreen extends Screen {
        JPanel info = new JPanel(new MigLayout());
        JPanel view = new JPanel(new MigLayout("flowy", "grow, fill"));
        JScrollPane viewScroll = new JScrollPane(view);
        JButton back = new JButton("Back");
        JButton addB = new JButton(ResourceRetriever.GetImage("plus.png", 16, 16));
        JLabel label = new JLabel();
        String type;
        Dimension windowSize = new Dimension(400, 400);
        Screen caller;
        Screen called;
        
        public ManageScreen(String type, Screen callerScreen){
            caller = callerScreen;
            
            Eve.setLayout(new MigLayout("wrap 3", "[][grow 25][grow 75]", "[][][grow]"));
            this.type = type;
            
            back.addActionListener((ActionEvent e) -> {
                caller.Refresh();
                caller.Return();
            });
            addB.setMargin(new Insets(0,0,0,0));
            addB.addActionListener((ActionEvent e) -> {
                if (type.matches("player")) {
                    new ModifyPlayerPopup(null, this, null);
                } else if (type.matches("team")) {
                    called = new ModifyTeamScreen(null, this);
                    called.Switch();
                }
            });
            
            view.setBorder(BorderFactory.createLineBorder(Color.black));
            
            viewScroll.getVerticalScrollBar().setUnitIncrement(16);
            
            this.DrawScreen();
        }
        
        void DrawScreen() {
            
            
            Eve.add(back, "span 2");
            Eve.add(info, "spany 2, grow");
            Eve.add(addB);
            Eve.add(label);
            Eve.add(viewScroll, "span, grow");
            
            switch(type) {
                case "team":
                    label.setText("Teams:");
                    if (Global.Teams.isEmpty()) {
                        view.add(new JLabel("No teams to display"));
                    } else {
                        for (Team team: Global.Teams) {
                            JPanel thisTeam = team.TeamPreview();
                            JButton modify = new JButton(ResourceRetriever.GetImage("edit.png", 16, 16));
                            modify.setMargin(new Insets(0,0,0,0));
                            modify.addActionListener((ActionEvent e) -> {
                                called = new ModifyTeamScreen(team, this);
                                called.Switch();
                            });
                            JButton delete = new JButton(ResourceRetriever.GetImage("X.png", 16, 16));
                            delete.setMargin(new Insets(0,0,0,0));
                            delete.addActionListener((ActionEvent e) -> {
                                Object[] options = {"Yes", "No"};
                                JFrame frame = new JFrame();
                                int answer = JOptionPane.showOptionDialog(frame,
                                        "Are you sure you would like to delete team \"" + team.name + "?\"",
                                        "Confirm Delete",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE,
                                        Global.QUESTIONPIC,
                                        options,
                                        options[1]);
                                if (answer == 0) {
                                    team.Delete();
                                    Refresh();
                                }
                            });
                            thisTeam.add(delete, "east");
                            thisTeam.add(modify, "east");
                            thisTeam.setBorder(BorderFactory.createLineBorder(Color.black));
                            view.add(thisTeam, "growx");
                        }
                    }
                    break;
                case "player":                    
                    label.setText("Players:");
                    if (Global.Players.isEmpty()) {
                        view.add(new JLabel("No players to display"));
                    } else {
                        for (Player player: Global.Players) {                            
                            JPanel thisPlayer = player.PlayerPreview();
                            JButton modify = new JButton(ResourceRetriever.GetImage("Edit.png", 16, 16));
                            modify.setMargin(new Insets(0,0,0,0));
                            modify.addActionListener((ActionEvent e) -> {
                                new ModifyPlayerPopup(player, this, null);
                            });
                            JButton delete = new JButton(ResourceRetriever.GetImage("X.png", 16, 16));
                            delete.setMargin(new Insets(0,0,0,0));
                            delete.addActionListener((ActionEvent e) -> {
                                Object[] options = {"I didn't like him anyways...", "No"};
                                JFrame frame = new JFrame();
                                int answer = JOptionPane.showOptionDialog(frame,
                                        "Are you sure you would like to delete \"" + player.name + "?\"",
                                        "Confirm Delete",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE,
                                        Global.QUESTIONPIC,
                                        options,
                                        options[1]);
                                if (answer == 0) {
                                    player.Delete();
                                    Refresh();
                                }
                            });
                            thisPlayer.add(delete, "east");
                            thisPlayer.add(modify, "east");
                            thisPlayer.setBorder(BorderFactory.createLineBorder(Color.black));
                            view.add(thisPlayer, "growx");
                        }
                    }
                    break;
            }
        }
        
        @Override
        void Refresh() {
            info.removeAll();
            view.removeAll();
            Eve.removeAll();
            this.DrawScreen();
            Eve.revalidate();
            Eve.repaint();
        }
        
        
        void Switch(){
            Adam.remove(caller.Eve);
            Adam.add(Eve);            
            Adam.revalidate();
            Adam.setMinimumSize(windowSize);
            Adam.repaint();
        }

        @Override
        void Return() {
            Adam.remove(called.Eve);
            Adam.add(Eve);            
            Adam.revalidate();
            Adam.setMinimumSize(windowSize);
            Adam.repaint();
        }
    }
    
    class BrowseScreen extends Screen {        
        PoolBuilder pool = new PoolBuilder(true, "small", null, null);
        JButton back = new JButton("Back");
        Dimension windowSize = new Dimension(650, 700);
        Screen caller;
        
        public BrowseScreen(Screen caller) {      
            Eve.setLayout(new MigLayout("flowy", "grow, fill", "[][grow, fill]"));
            this.caller = caller;
            back.addActionListener((ActionEvent e) -> {
                caller.Return();
            });
            Eve.add(back);
            Eve.add(pool.pool);
        }
        
        @Override
        void Switch() {
            Adam.remove(caller.Eve);
            Adam.add(Eve);            
            Adam.revalidate();
            Adam.setMinimumSize(windowSize);
            Adam.repaint();
        }

        @Override
        void Refresh() {            
        }

        @Override
        void Return() {
        }
    }
    
    class ModifyPlayerPopup {
        JFrame Cain = new JFrame();
        JPanel Enoch = new JPanel(new MigLayout("flowy", "[grow, fill]","[][grow, fill]"));
        JPanel south = new JPanel(new MigLayout("wrap 2", "[][grow]", "[][grow][]"));
        JPanel heroes = new JPanel(new WrapLayout(FlowLayout.LEADING));
        JTextField name = new JTextField("Player Name");
        JButton addHeroes = new JButton(ResourceRetriever.GetImage("plus.png", 16, 16));
        JButton cancel = new JButton("Cancel");
        JButton save = new JButton("Save");
        Player player;
        JWindow popup = new JWindow();
        JPanel inner = new JPanel(new MigLayout("", "[grow, fill]", "[grow, fill]"));
        JButton back;
        
        public ModifyPlayerPopup(Player playerPassed, Screen callerScreen, Team team) {
           
            cancel.addActionListener((ActionEvent e) -> {
                Cain.dispose();
            });
            
            save.addActionListener((ActionEvent e) -> {                
                player.name = name.getText();
                
                if (player.globalIndex == -1) {
                    player.savePlayer();
                } else {
                    Global.Players.set(player.globalIndex, player);
                }
                if (team != null) {
                    team.AddPlayer(player);
                }
                
                callerScreen.Refresh();
                Cain.dispose();
            });
            
            if (playerPassed != null) {
                // We're edditing a current player
                player = playerPassed;
                for(Hero hero: player.GetPlayList()) {
                    JLabel heroLabel = new JLabel(hero.portraitSmall);
                    heroLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            player.RemoveHero(hero);
                            Refresh();
                        }
                    });
                    heroes.add(heroLabel);
                }
                name.setText(player.name);
            } else {
                // This is a new Player
                Cain.setTitle("Add a player");    
                player = new Player("New Player");
            }
            addHeroes.setMargin(new Insets(0,0,0,0));
            addHeroes.addActionListener((ActionEvent e) -> {
                PoolBuilder pool = new PoolBuilder(false, "small", new returner(), player.GetPlayList());
                back = new JButton("Cancel");
                back.addActionListener((ActionEvent p) -> {
                    inner.removeAll();
                    popup.dispose();
                });
                JLabel note = new JLabel("Right click to add multiple heroes.");
                inner.add(back, "wrap");
                inner.add(note, "wrap");
                inner.add(pool.pool, "growx");
                popup.add(inner);
                popup.setSize(700, 500); 
                popup.setLocation(MouseInfo.getPointerInfo().getLocation());
                popup.setVisible(true);
            });

            name.setFont(new Font("Ariel", Font.PLAIN, 20));
            name.select(0, name.getText().length());

            heroes.setBorder(BorderFactory.createLineBorder(Color.black));

            south.add(addHeroes);
            south.add(new JLabel("Heroes"));
            south.add(heroes, "span, grow");
            south.add(cancel, "span, right, split 2");
            south.add(save, "span, right");

            Enoch.add(name);
            Enoch.add(south);

            Cain.add(Enoch);
            Cain.setMinimumSize(new Dimension(400, 350));
            Cain.setVisible(true);
        }
        public class returner extends HeroReturner{
            @Override
            public Void call() throws Exception {
                player.AddHero(this.hero);
                JLabel heroLabel = new JLabel(this.hero.portraitSmall);
                heroLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            player.RemoveHero(hero);
                            Refresh();
                        }
                    });
                
                heroes.add(heroLabel);
                heroes.revalidate();
                heroes.repaint();
                if (e.getButton() == 3) {
                    back.setText("Done");
                } else {
                    // Button 1 (Or three I suppose??)
                    inner.removeAll();
                    popup.dispose();
                }
                return null;
            }
        }
        
        void Refresh(){
            heroes.removeAll();
            for(Hero hero: player.GetPlayList()) {
                JLabel heroLabel = new JLabel(hero.portraitSmall);
                heroLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        player.RemoveHero(hero);
                        Refresh();
                    }
                });
                heroes.add(heroLabel);
            }
            heroes.revalidate();
            heroes.repaint();
        }
    }
    
    class ModifyTeamScreen extends Screen{
        JPanel south = new JPanel(new MigLayout("wrap 2", "[][grow]", "[][grow][]"));
        JPanel players = new JPanel(new MigLayout("flowy", "grow, fill"));
        JTextField name = new JTextField("Team Name");
        JButton addPlayers = new JButton(ResourceRetriever.GetImage("plus.png", 16, 16));
        JButton cancel = new JButton("Cancel");
        JButton save = new JButton("Save");       
        Team team;
        JWindow popup = new JWindow();
        JPanel inner = new JPanel(new MigLayout("flowy", "[grow, fill]", ""));
        JScrollPane innerScroll = new JScrollPane(inner);
        Dimension windowSize = new Dimension(400, 400);
        Screen caller;
        
        public ModifyTeamScreen(Team teamPassed, Screen callerScreen) {
            
            Eve.setLayout(new MigLayout("flowy", "[grow, fill]","[][grow, fill]"));
            
            cancel.addActionListener((ActionEvent e) -> {
                caller.Return();
            });
            
            save.addActionListener((ActionEvent e) -> {
                team.name = name.getText();
                if (team.globalIndex == -1) {
                    team.saveTeam();
                } else {
                    Global.Teams.set(team.globalIndex, team);
                }
                callerScreen.Refresh();
                caller.Return();
            });
            addPlayers.setMargin(new Insets(0,0,0,0));
            addPlayers.addActionListener((ActionEvent e) -> {                
                if (team.GetPlayers().length < 5) {
                    JButton cancelB = new JButton("Cancel");
                    cancelB.addActionListener((ActionEvent f) -> {
                        inner.removeAll();
                        popup.dispose();
                    });
                    inner.add(cancelB);
                    JButton newPlayer = new JButton("New Player");
                    newPlayer.addActionListener((ActionEvent f) -> {
                        inner.removeAll();
                        popup.dispose();
                        ModifyPlayerPopup addNewPlayer = new ModifyPlayerPopup(null, this, team);
                    });
                    inner.add(newPlayer);                
                    inner.add(new JSeparator(), "growx");
                    for (Player player: Global.Players) {
                        boolean playerInCurrentTeam = false;
                        for (Player teamPlayer: team.GetPlayers()) {
                            if (teamPlayer.globalIndex == player.globalIndex) {
                                playerInCurrentTeam = true;
                                break;
                            }
                        }
                        if (!playerInCurrentTeam) {                        
                            JPanel oldPlayer = player.PlayerPreview();
                            JButton choose = new JButton("Add!");
                            choose.addActionListener((ActionEvent f) -> {
                                team.AddPlayer(player);
                                inner.removeAll();
                                popup.dispose();
                                Refresh();
                            });
                            oldPlayer.add(choose, "east");
                            oldPlayer.setBorder(BorderFactory.createLineBorder(Color.black));
                            inner.add(oldPlayer);
                        }
                    }
                    innerScroll.getVerticalScrollBar().setUnitIncrement(16);
                    innerScroll.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
                    popup.add(innerScroll);
                    popup.setSize(350, 400); 
                    popup.setLocation(MouseInfo.getPointerInfo().getLocation());
                    popup.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "You cannot add more than 5 players per team.\nPlease remove one before adding another.");
                }
            });
            
            caller = callerScreen;
            
            DrawScreen(teamPassed);
        }
        
        void DrawScreen(Team teamPassed) {
            
            if (teamPassed != null) {
                // We're edditing a current team
                team = teamPassed;
                for (Player player: team.GetPlayers()) {                          
                            JPanel thisPlayer = player.PlayerPreview();
                            JButton modify = new JButton(ResourceRetriever.GetImage("edit.png", 16, 16));
                            modify.setMargin(new Insets(0,0,0,0));
                            modify.addActionListener((ActionEvent e) -> {
                                new ModifyPlayerPopup(player, this, null);
                            });
                            JButton delete = new JButton(ResourceRetriever.GetImage("X.png", 16, 16));
                            delete.setMargin(new Insets(0,0,0,0));
                            delete.addActionListener((ActionEvent e) -> {
                                Object[] options = {"Remove Payer from team", "Delete Player", "Cancel"};
                                JFrame frame = new JFrame();
                                int answer = JOptionPane.showOptionDialog(frame,
                                        "Are you sure you would like to delete \"" + player.name + "?\"",
                                        "Confirm Delete",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE,
                                        Global.QUESTIONPIC,
                                        options,
                                        options[2]);
                                if (answer == 0) {
                                    team.RemovePlayer(player);
                                    Refresh();
                                } else if (answer == 1) {
                                    // Teams without uniqueID's don't get added to players yet,
                                    //   so the player needs to be manually removed.
                                    if (team.globalIndex == -1) {
                                        team.RemovePlayer(player);
                                    }
                                    player.Delete();
                                    Refresh();
                                }
                            });
                            thisPlayer.add(delete, "east");
                            thisPlayer.add(modify, "east");
                            thisPlayer.setBorder(BorderFactory.createLineBorder(Color.black));
                            players.add(thisPlayer, "growx");
                }
                name.setText(team.name);
            } else {
                // This is a new Team                
                team = new Team("New Team");
                name.setText(team.name);
                name.setFont(new Font("Ariel", Font.PLAIN, 20));
                name.select(0, name.getText().length());
            }

            players.setBorder(BorderFactory.createLineBorder(Color.black));

            south.add(addPlayers);
            south.add(new JLabel("Players"));
            south.add(players, "span, grow");
            south.add(cancel, "span, right, split 2");
            south.add(save, "span, right");

            Eve.add(name);
            Eve.add(south);            
        }

        @Override
        void Refresh() {
            team.name = name.getText();
            south.removeAll();
            players.removeAll();
            Eve.removeAll();
            DrawScreen(team);
            Eve.revalidate();
            Eve.repaint();
        }

        @Override
        void Switch() {
            Adam.remove(caller.Eve);
            Adam.add(Eve);            
            Adam.revalidate();
            Adam.setMinimumSize(windowSize);
            Adam.repaint();
        }

        @Override
        void Return() {
        }
    }

    class PoolBuilder {
        JPanel pool = new JPanel(new MigLayout("flowy, insets 0,", "grow, fill", ""));
        JPanel preview = new JPanel(new MigLayout("insets 0"));
        JPanel heroes = new JPanel(new MigLayout("wrap 2", "grow, fill", ""));
        Map<String, JPanel> heroPanels = new HashMap<>();
        JPanel Str = new JPanel(new MigLayout("", "[grow 50][grow 50]", ""));
        JPanel Agi = new JPanel(new MigLayout("", "[grow 50][grow 50]", ""));
        JPanel Int = new JPanel(new MigLayout("", "[grow 50][grow 50]", ""));
        JLabel portrait = new JLabel(ResourceRetriever.GetImage("Transparent.png", 256, 144));
        JScrollPane scrollPane = new JScrollPane(heroes);
        JTextArea info = new JTextArea();
        
        /**
        * Creates a panel with a custom built hero pool display.
        * WARNING: DELETE THIS FRAME WHEN YOU ARE DONE OR YOU WILL CAUSE A MEMORY LEAK.
        * @param preview Do you want a hero preview at the top?
        */
        public PoolBuilder(boolean previewFlag, String size, HeroReturner function, Hero[] disableList) {
            heroPanels.put("RadiantStrength", new JPanel(new WrapLayout(FlowLayout.LEADING)));
            heroPanels.put("DireStrength", new JPanel(new WrapLayout(FlowLayout.LEADING)));
            heroPanels.put("RadiantAgility", new JPanel(new WrapLayout(FlowLayout.LEADING)));
            heroPanels.put("DireAgility", new JPanel(new WrapLayout(FlowLayout.LEADING)));
            heroPanels.put("RadiantIntelligence", new JPanel(new WrapLayout(FlowLayout.LEADING)));
            heroPanels.put("DireIntelligence", new JPanel(new WrapLayout(FlowLayout.LEADING)));
                        
            preview.add(portrait);
            preview.add(info);
            
            Str.setBorder(BorderFactory.createLineBorder(Color.black));
            Agi.setBorder(BorderFactory.createLineBorder(Color.black));
            Int.setBorder(BorderFactory.createLineBorder(Color.black));
            
            info.setEditable(false);
            
            for (Hero hero: Global.AllHeroes.values()){
                JLabel label = new JLabel(hero.portraitSmall);
                heroPanels.get(hero.side + hero.attribute).add(label);
                // Disable the button if the hero it contains matches the disableList.
                if (Global.ExsistsInPool(hero.abbrv, disableList)) {
                    label.setEnabled(false);
                } else {
                    label.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            if (previewFlag) {
                                portrait.setIcon(hero.portraitLarge);
                                System.out.println("info text set!");
                                info.setText(hero.name + " the " + hero.title + "\n");
                            } else {
                                try {
                                    scrollPane.getVerticalScrollBar().setValue(0);
                                    function.HeroEquals(hero);
                                    function.MouseEventEquals(evt);
                                    function.call();
                                } catch (Exception ex) {
                                    Logger.getLogger(WindowManager.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    });
                }
            }
            
            heroes.add(new JLabel(ResourceRetriever.GetImage("Radiant.png", 24, 24)), "span, split 3, center, grow");
            heroes.add(new JLabel(ResourceRetriever.GetImage("STR.png", 24, 24)), "shrink, center");
            heroes.add(new JLabel(ResourceRetriever.GetImage("Dire.png", 24, 24)), "span, center, grow");
            Str.add(heroPanels.get("RadiantStrength"), "grow, width 100:pref:260, center");
            Str.add(heroPanels.get("DireStrength"), "grow, width 100:pref:260, center");
            heroes.add(Str, "span, height min:0:pref");
            
            heroes.add(new JLabel(ResourceRetriever.GetImage("Radiant.png", 24, 24)), "span, split 3, center, grow");
            heroes.add(new JLabel(ResourceRetriever.GetImage("AGI.png", 24, 24)), "shrink, center");
            heroes.add(new JLabel(ResourceRetriever.GetImage("Dire.png", 24, 24)), "span, center, grow");
            Agi.add(heroPanels.get("RadiantAgility"), "grow, width 100:pref:260, center");
            Agi.add(heroPanels.get("DireAgility"), "grow, width 100:pref:260, center");
            heroes.add(Agi, "span, height pref!");
            
            heroes.add(new JLabel(ResourceRetriever.GetImage("Radiant.png", 24, 24)), "span, split 3, center, grow");
            heroes.add(new JLabel(ResourceRetriever.GetImage("INT.png", 24, 24)), "shrink, center");
            heroes.add(new JLabel(ResourceRetriever.GetImage("Dire.png", 24, 24)), "span, center, grow");
            Int.add(heroPanels.get("RadiantIntelligence"), "grow, width 100:pref:260, center");
            Int.add(heroPanels.get("DireIntelligence"), "grow, width 100:pref:260, center");
            heroes.add(Int, "span, height pref!");
            
            if (previewFlag) {
                pool.add(preview);
            }
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
            pool.add(scrollPane);
        }
    }
}