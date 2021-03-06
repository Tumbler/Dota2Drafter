package dota2drafter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;

public class WindowManager {
    int activeWindow = 0;
    JFrame Adam = new JFrame();
    MainScreen main;
    DraftScreen draft;
    ManageScreen manage;
    BrowseScreen browse;
    
    public WindowManager(){
        WindowListener exitListener = new WindowAdapter() {            
            @Override
            public void windowClosing(WindowEvent e) {                
                for(Team team: Global.Teams) {
                    team.writeTeam();
                }
                for(Player player: Global.Players){
                    player.writePlayer();
                }
                System.exit(0);
            }
        };
        Adam.addWindowListener(exitListener);
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
        Dimension windowSize = new Dimension(630, 500);
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
            
            this.drawScreen();
            
            Adam.setMinimumSize(windowSize);
            Adam.setLocationRelativeTo(null);
            Adam.setVisible(true);
        }
        
        void drawScreen() {
            
            teams.add(myTeams);
            teams.add(manageTeams, "growx");
            teams.add(new JSeparator(), "growx");
            
            for (Team team: Global.Teams) {
                JPanel thisTeam = team.teamPreview();
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
                JPanel thisPlayer = player.playerPreview();
                JButton modify = new JButton(ResourceRetriever.getImage("edit.png", 16, 16));
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
        void refresh() {
            teamDraft.removeAll();
            teams.removeAll();
            players.removeAll();
            teamsInner.removeAll();
            playersInner.removeAll();
            Eve.removeAll();
            this.drawScreen();
            Eve.revalidate();
            Eve.repaint();
        }
    }
    
    class DraftScreen extends Screen{
        JPanel matchup = new JPanel(new MigLayout("inset 0","[0:0, grow, center][center][0:0, grow, center]",""));
        JPanel myPicksFrame = new JPanel(new MigLayout("inset 0", "grow, center"));
        JPanel myPicksPortraits = new JPanel(new MigLayout(""));
        JPanel theirPicks = new JPanel(new MigLayout("inset 0", "grow, center"));
        JPanel theirPicksPortraits = new JPanel(new MigLayout(""));
        JPanel myBansFrame = new JPanel(new MigLayout("inset 0", "grow, center"));
        JPanel myBansPortraits = new JPanel(new MigLayout(""));
        JPanel theirBans = new JPanel(new MigLayout("inset 0", "grow, center"));
        JPanel theirBansPortraits = new JPanel(new MigLayout(""));
        JPanel players = new JPanel(new MigLayout("inset 0, flowy", "[grow, fill]", ""));
        JPanel theirPool = new JPanel(new MigLayout("inset 0, flowy", "[grow, fill]", ""));
        JLabel teamName = new JLabel("Team #1");
        JLabel enemyName = new JLabel("Team #2");
        JLabel[] myPicks = {
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC)
        };
        JLabel[] myBans = {
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC)
        };
        JLabel[] enemyPicks = {
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC)
        };
        JLabel[] enemyBans = {
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC),
            new JLabel(Global.QUESTIONPIC)
        };
        JLabel player1 = new JLabel("Player #1");
        JLabel player2 = new JLabel("Player #2");
        JLabel player3 = new JLabel("Player #3");
        JLabel player4 = new JLabel("Player #4");
        JLabel player5 = new JLabel("Player #5");
        JButton back = new JButton("Back");
        Dimension windowSize = new Dimension(700, 700);
        Team team;
        Screen caller;
        int firstPick;
        Draft draft;
        PoolBuilder allyPool;
        PoolBuilder enemyPool;
        
        public DraftScreen(Team team, Screen caller) {
            Eve = new JPanel(new MigLayout("wrap 2", "[grow, center, fill][grow, fill]"));
            this.team = team;
            this.caller = caller;
            back.addActionListener((ActionEvent e) -> {
                caller.refresh();
                caller.Return();
            });
            Object[] options = {"Us", "Them"};
            JFrame frame = new JFrame();
            firstPick = JOptionPane.showOptionDialog(frame,
                    "Who has first pick?",
                    "",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
            draft = new Draft(firstPick, myPicks, myBans, enemyPicks, enemyBans);
            drawScreen();
        }
        
        void drawScreen() {
            matchup.add(teamName);
            matchup.add(new JLabel("vs."));
            matchup.add(enemyName);
            
            myPicksFrame.add(new JLabel("picks"), "span, center, wrap");
            for (JLabel portait: myPicks) {
                myPicksPortraits.add(portait);
            }
            myPicksFrame.add(myPicksPortraits);
            
            theirPicks.add(new JLabel("picks"), "span, center, wrap");
            for (JLabel portait: enemyPicks) {
                theirPicksPortraits.add(portait);
            }
            theirPicks.add(theirPicksPortraits);
            
            myBansFrame.add(new JLabel("bans"), "span, center, wrap");
            for (JLabel portait: myBans) {
                myBansPortraits.add(portait);
            }
            myBansFrame.add(myBansPortraits);
            
            theirBans.add(new JLabel("bans"), "span, center, wrap");
            for (JLabel portait: enemyBans) {
                theirBansPortraits.add(portait);
            }
            theirBans.add(theirBansPortraits);
            
            if (team == null) {
                allyPool = new PoolBuilder(false, "small", null, null);
                players.add(allyPool.pool);
            } else {
                for (Player player: team.getPlayers()) {
                    players.add(new JLabel(player.name));
                    JPanel heroes = new JPanel(new MigLayout("","[grow 49, fill][grow 1][grow 49, fill]"));
                    JPanel regHeroes = new JPanel(new WrapLayout(FlowLayout.LEADING));
                    JPanel stunHeroes = new JPanel(new WrapLayout(FlowLayout.LEADING));
                    int numOfStuns = 0;
                    for (Hero hero: player.getPlayList()) {
                        if (hero.hasCharacteristic(Global.Characteristics.RSTUN)) {
                            stunHeroes.add(new JLabel(hero.portraitSmall));
                            numOfStuns++;
                        } else {
                            regHeroes.add(new JLabel(hero.portraitSmall));
                        }
                    }
                    heroes.add(regHeroes);
                    if (numOfStuns > 0) {
                        heroes.add(new JSeparator(SwingConstants.VERTICAL), "growy");
                        heroes.add(stunHeroes);
                    }
                    heroes.setBorder(BorderFactory.createLineBorder(Color.black));
                    players.add(heroes);
                }
            }
            enemyPool = new PoolBuilder(false, "small", new Returner(), null);
            theirPool.add(enemyPool.pool);
            
            Eve.add(matchup, "span, grow");
            Eve.add(myPicksFrame, "c");
            Eve.add(theirPicks, "c");
            Eve.add(myBansFrame, "c");
            Eve.add(theirBans, "c");
            //Eve.add(players, "top, span");
            Eve.add(theirPool);
            //Eve.add(back);
            
            Adam.setSize(windowSize);
        }
        
        @Override
        void refresh() {
            Adam.revalidate();
            Adam.setMinimumSize(windowSize);
            Adam.setSize(windowSize);
            Adam.repaint();
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
        
        public class Returner extends HeroReturner{

            @Override
            public Void call() {
                draft.next(this.hero);
                enemyPool.disableHero(this.hero);
                //Eve.remove(theirPool);
                //Eve.add(players, "top, span");
                //refresh();
                return null;
            }
            
        }
    }
    
    class ManageScreen extends Screen {
        JPanel info = new JPanel(new MigLayout());
        JPanel view = new JPanel(new MigLayout("flowy", "grow, fill"));
        JScrollPane viewScroll = new JScrollPane(view);
        JButton back = new JButton("Back");
        JButton addB = new JButton(ResourceRetriever.getImage("plus.png", 16, 16));
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
                caller.refresh();
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
            
            this.drawScreen();
        }
        
        void drawScreen() {
            
            
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
                            JPanel thisTeam = team.teamPreview();
                            JButton modify = new JButton(ResourceRetriever.getImage("edit.png", 16, 16));
                            modify.setMargin(new Insets(0,0,0,0));
                            modify.addActionListener((ActionEvent e) -> {
                                called = new ModifyTeamScreen(team, this);
                                called.Switch();
                            });
                            JButton delete = new JButton(ResourceRetriever.getImage("X.png", 16, 16));
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
                                    team.delete();
                                    refresh();
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
                            JPanel thisPlayer = player.playerPreview();
                            JButton modify = new JButton(ResourceRetriever.getImage("edit.png", 16, 16));
                            modify.setMargin(new Insets(0,0,0,0));
                            modify.addActionListener((ActionEvent e) -> {
                                new ModifyPlayerPopup(player, this, null);
                            });
                            JButton delete = new JButton(ResourceRetriever.getImage("X.png", 16, 16));
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
                                    player.delete();
                                    refresh();
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
        void refresh() {
            info.removeAll();
            view.removeAll();
            Eve.removeAll();
            this.drawScreen();
            Eve.revalidate();
            Eve.repaint();
        }
        
        
        void Switch(){
            Adam.remove(caller.Eve);
            Adam.add(Eve);            
            Adam.revalidate();
            Adam.setMinimumSize(windowSize);
            Adam.setSize(windowSize);
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
        void refresh() {
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
        JButton addHeroes = new JButton(ResourceRetriever.getImage("plus.png", 16, 16));
        JButton cancel = new JButton("Cancel");
        JButton save = new JButton("Save");
        Player player;
        JWindow popup = new JWindow();
        JPanel inner = new JPanel(new MigLayout("", "[grow, fill]", "[grow, fill]"));
        JButton back;
        PoolBuilder pool;
        
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
                    player.writePlayer();
                }
                if (team != null) {
                    team.addPlayer(player);
                }
                
                callerScreen.refresh();
                Cain.dispose();
            });
            
            if (playerPassed != null) {
                // We're editing a current player
                player = playerPassed;
                for(Hero hero: player.getPlayList()) {
                    JLabel heroLabel = new JLabel(hero.portraitSmall);
                    heroLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            player.removeHero(hero);
                            refresh();
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
                pool = new PoolBuilder(false, "small", new Returner(), player.getPlayList());
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
        public class Returner extends HeroReturner{
            @Override
            public Void call() throws Exception {
                player.addHero(this.hero);
                JLabel heroLabel = new JLabel(this.hero.portraitSmall);
                heroLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            player.removeHero(hero);
                            refresh();
                        }
                    });
                
                heroes.add(heroLabel);
                heroes.revalidate();
                heroes.repaint();
                if (e.getButton() == 3) {
                    back.setText("Done");
                    pool.disableHero(this.hero);
                } else {
                    // Button 1 (Or three I suppose??)
                    inner.removeAll();
                    popup.dispose();
                }
                return null;
            }
        }
        
        void refresh(){
            heroes.removeAll();
            for(Hero hero: player.getPlayList()) {
                JLabel heroLabel = new JLabel(hero.portraitSmall);
                heroLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        player.removeHero(hero);
                        refresh();
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
        JButton addPlayers = new JButton(ResourceRetriever.getImage("plus.png", 16, 16));
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
                    team.saveTeam(true);
                } else {
                    team.saveTeam(false);
                }
                callerScreen.refresh();
                caller.Return();
            });
            addPlayers.setMargin(new Insets(0,0,0,0));
            addPlayers.addActionListener((ActionEvent e) -> {                
                if (team.getPlayers().length < 5) {
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
                        for (Player teamPlayer: team.getPlayers()) {
                            if (teamPlayer.globalIndex == player.globalIndex) {
                                playerInCurrentTeam = true;
                                break;
                            }
                        }
                        if (!playerInCurrentTeam) {                        
                            JPanel oldPlayer = player.playerPreview();
                            JButton choose = new JButton("Add!");
                            choose.addActionListener((ActionEvent f) -> {
                                team.addPlayer(player);
                                inner.removeAll();
                                popup.dispose();
                                refresh();
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
            
            drawScreen(teamPassed);
        }
        
        void drawScreen(Team teamPassed) {
            
            if (teamPassed != null) {
                // We're editing a current team
                team = teamPassed;
                for (Player player: team.getPlayers()) {
                            JPanel thisPlayer = player.playerPreview();
                            JButton modify = new JButton(ResourceRetriever.getImage("edit.png", 16, 16));
                            modify.setMargin(new Insets(0,0,0,0));
                            modify.addActionListener((ActionEvent e) -> {
                                new ModifyPlayerPopup(player, this, null);
                            });
                            JButton delete = new JButton(ResourceRetriever.getImage("X.png", 16, 16));
                            delete.setMargin(new Insets(0,0,0,0));
                            delete.addActionListener((ActionEvent e) -> {
                                Object[] options = {"Remove Player from team", "Delete Player", "Cancel"};
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
                                    team.removePlayer(player);
                                    refresh();
                                } else if (answer == 1) {
                                    // Teams without uniqueID's don't get added to players yet,
                                    //   so the player needs to be manually removed.
                                    if (team.globalIndex == -1) {
                                        team.removePlayer(player);
                                    }
                                    player.delete();
                                    refresh();
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
        void refresh() {
            team.name = name.getText();
            south.removeAll();
            players.removeAll();
            Eve.removeAll();
            drawScreen(team);
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
        Map<String, JPanel> heroFactions = new HashMap<>();
        Map<String, JLabel> heroPanels = new HashMap<>();
        JPanel Str = new JPanel(new MigLayout("", "[grow 50][grow 50]", ""));
        JPanel Agi = new JPanel(new MigLayout("", "[grow 50][grow 50]", ""));
        JPanel Int = new JPanel(new MigLayout("", "[grow 50][grow 50]", ""));
        JLabel portrait = new JLabel(ResourceRetriever.getImage("Transparent.png", 256, 144));
        JScrollPane scrollPane = new JScrollPane(heroes);
        JTextArea info = new JTextArea();
        
        /**
        * Creates a panel with a custom built hero pool display.
        * WARNING: DELETE THIS FRAME WHEN YOU ARE DONE OR YOU WILL CAUSE A MEMORY LEAK.
        * @param preview Do you want a hero preview at the top?
        */
        public PoolBuilder(boolean previewFlag, String size, HeroReturner function, Hero[] disableList) {
            heroFactions.put("RadiantStrength", new JPanel(new WrapLayout(FlowLayout.LEADING)));
            heroFactions.put("DireStrength", new JPanel(new WrapLayout(FlowLayout.LEADING)));
            heroFactions.put("RadiantAgility", new JPanel(new WrapLayout(FlowLayout.LEADING)));
            heroFactions.put("DireAgility", new JPanel(new WrapLayout(FlowLayout.LEADING)));
            heroFactions.put("RadiantIntelligence", new JPanel(new WrapLayout(FlowLayout.LEADING)));
            heroFactions.put("DireIntelligence", new JPanel(new WrapLayout(FlowLayout.LEADING)));
                        
            preview.add(portrait);
            preview.add(info);
            
            Str.setBorder(BorderFactory.createLineBorder(Color.black));
            Agi.setBorder(BorderFactory.createLineBorder(Color.black));
            Int.setBorder(BorderFactory.createLineBorder(Color.black));
            
            info.setEditable(false);
            
            for (Hero hero: Global.AllHeroes.values()){
                JLabel label = new JLabel(hero.portraitSmall);
                heroFactions.get(hero.side + hero.attribute).add(label);
                // Disable the button if the hero it contains matches the disableList.
                if (Global.existsInPool(hero.abbrv, disableList)) {
                    label.setEnabled(false);
                } else {
                    label.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            if (previewFlag) {
                                portrait.setIcon(hero.portraitLarge);
                                
                                info.setText(hero.name + " the " + hero.title + "\n");
                            } else {
                                if (label.isEnabled()) {
                                    try {
                                        scrollPane.getVerticalScrollBar().setValue(0);
                                        function.heroEquals(hero);
                                        function.mouseEventEquals(evt);
                                        function.call();
                                    } catch (Exception ex) {
                                        Logger.getLogger(WindowManager.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                    });
                }
                heroPanels.put(hero.abbrv, label);
            }
            
            heroes.add(new JLabel(ResourceRetriever.getImage("Radiant.png", 24, 24)), "span, split 3, center, grow");
            heroes.add(new JLabel(ResourceRetriever.getImage("STR.png", 24, 24)), "shrink, center");
            heroes.add(new JLabel(ResourceRetriever.getImage("Dire.png", 24, 24)), "span, center, grow");
            Str.add(heroFactions.get("RadiantStrength"), "grow, width 100:pref:260, center");
            Str.add(heroFactions.get("DireStrength"), "grow, width 100:pref:260, center");
            heroes.add(Str, "span, height min:0:pref");
            
            heroes.add(new JLabel(ResourceRetriever.getImage("Radiant.png", 24, 24)), "span, split 3, center, grow");
            heroes.add(new JLabel(ResourceRetriever.getImage("AGI.png", 24, 24)), "shrink, center");
            heroes.add(new JLabel(ResourceRetriever.getImage("Dire.png", 24, 24)), "span, center, grow");
            Agi.add(heroFactions.get("RadiantAgility"), "grow, width 100:pref:260, center");
            Agi.add(heroFactions.get("DireAgility"), "grow, width 100:pref:260, center");
            heroes.add(Agi, "span, height pref!");
            
            heroes.add(new JLabel(ResourceRetriever.getImage("Radiant.png", 24, 24)), "span, split 3, center, grow");
            heroes.add(new JLabel(ResourceRetriever.getImage("INT.png", 24, 24)), "shrink, center");
            heroes.add(new JLabel(ResourceRetriever.getImage("Dire.png", 24, 24)), "span, center, grow");
            Int.add(heroFactions.get("RadiantIntelligence"), "grow, width 100:pref:260, center");
            Int.add(heroFactions.get("DireIntelligence"), "grow, width 100:pref:260, center");
            heroes.add(Int, "span, height pref!");
            
            if (previewFlag) {
                pool.add(preview);
            }
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
            pool.add(scrollPane);
        }
        public void disableHero(Hero hero) {
            heroPanels.get(hero.abbrv).setEnabled(false);
        }
    }
}
