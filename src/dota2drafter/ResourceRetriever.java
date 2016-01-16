package dota2drafter;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ResourceRetriever {
    
    public static ImageIcon getImage(String image, int x, int y) {
        try {
            URL ImageToURL = Dota2Drafter.class.getResource(Global.PICPATH + image);
            Image rescale = ImageIO.read(ImageToURL);
            rescale = rescale.getScaledInstance(x, y, Image.SCALE_SMOOTH);
            return (new ImageIcon(rescale));
        } catch (IOException ex) {
            Logger.getLogger(ResourceRetriever.class.getName()).log(Level.SEVERE, null, ex);
            return (null);
        }

    }
    
    public static Hero getHero(String hero) throws IOException{
        InputStream stream = ResourceRetriever.class.getResourceAsStream(Global.RESOURCE_PATH + "info/" + hero + ".txt");
        BufferedReader text = new BufferedReader(new InputStreamReader(stream));
        
        Pattern known = Pattern.compile("CommonName: (.*)");
        Pattern name = Pattern.compile("Name: (.*)");
        Pattern title = Pattern.compile("Title: (.*)");
        Pattern abrv = Pattern.compile("Abbrv: (.*)");
        Pattern side = Pattern.compile("Side: (.*)");
        Pattern attr = Pattern.compile("Attr: (.*)");
        Pattern combo = Pattern.compile("Combos: (.*)");
        Pattern counter = Pattern.compile("Counters: (.*)");
        Pattern character = Pattern.compile("Characteristics: (.*)");
        
        String[] heroInfo = new String[9];
        
        String line;
        while((line = text.readLine()) != null){
            Matcher Known = known.matcher(line);
            Matcher Name = name.matcher(line);
            Matcher Title = title.matcher(line);
            Matcher Abrv = abrv.matcher(line);
            Matcher Side = side.matcher(line);
            Matcher Attr = attr.matcher(line);
            Matcher Combo = combo.matcher(line);
            Matcher Counter = counter.matcher(line);
            Matcher Character = character.matcher(line);
            
            if (Known.matches()) {
                heroInfo[0] = Known.group(1);
            } else if (Name.matches()) {
                heroInfo[1] = Name.group(1);
            } else if (Title.matches()) {
                heroInfo[2] = Title.group(1);
            } else if (Abrv.matches()) {
                heroInfo[3] = Abrv.group(1);
            } else if (Side.matches()) {
                heroInfo[4] = Side.group(1);
            } else if (Attr.matches()) {
                heroInfo[5] = Attr.group(1);
            } else if (Combo.matches()) {
                heroInfo[6] = Combo.group(1);
            } else if (Counter.matches()) {
                heroInfo[7] = Counter.group(1);
            } else if (Character.matches()) {
                heroInfo[8] = Character.group(1);
            }
        }
        
        String[] combos = heroInfo[6].split(",");
        String[] counters = heroInfo[7].split(",");
        String[] charactersString = heroInfo[8].split(",");
        int[] characters = new int[charactersString.length];
        for(int i = 0; i < characters.length; i++) {
            characters[i] = Integer.parseInt(charactersString[i]);
        }
        
        ImageIcon[] portraits = new ImageIcon[3];
        URL ImageToURL1 = Dota2Drafter.class.getResource(Global.SMALLPATH + hero + ".png");
        URL ImageToURL2 = Dota2Drafter.class.getResource(Global.MEDIUMPATH + hero + ".png");
        URL ImageToURL3 = Dota2Drafter.class.getResource(Global.LARGEPATH + hero + ".png");
        Image rescale1 = ImageIO.read(ImageToURL1);
        Image rescale2 = ImageIO.read(ImageToURL2);
        Image rescale3 = ImageIO.read(ImageToURL3);
        rescale1 = rescale1.getScaledInstance(59,  33, Image.SCALE_SMOOTH);
        rescale2 = rescale2.getScaledInstance(205, 115, Image.SCALE_SMOOTH);
        rescale3 = rescale3.getScaledInstance(256, 144, Image.SCALE_SMOOTH);
        portraits[0] = new ImageIcon(rescale1);
        portraits[1] = new ImageIcon(rescale2);
        portraits[2] = new ImageIcon(rescale3);
        
        Hero returnHero = new Hero(
                heroInfo[0],
                heroInfo[1],
                heroInfo[2],
                heroInfo[3],
                heroInfo[4],
                heroInfo[5],
                portraits
        );
        
        returnHero.addCombos(combos);
        returnHero.addCounters(counters);
        returnHero.addCharacters(characters);
        
        return returnHero;
    }
    
    public static void readPlayers() throws FileNotFoundException, IOException {
        File dir = new File(Global.EMERGENCY_PLAYER_PATH);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            
            Pattern nameP = Pattern.compile("Name: (.*)");
            Pattern heroesP = Pattern.compile("PlayList: (.*)");
            Pattern indexP = Pattern.compile("GlobalIndex: (.*)");
            Pattern teamsP = Pattern.compile("Teams: (.*)");
            
            for (File child: directoryListing) {
                BufferedReader text = new BufferedReader(new FileReader(child));

                String[] playerInfo = new String[4];

                String line;
                while((line = text.readLine()) != null){
                    Matcher NameM = nameP.matcher(line);
                    Matcher HeroesM = heroesP.matcher(line);
                    Matcher IndexM = indexP.matcher(line);
                    Matcher TeamsM = teamsP.matcher(line);

                    if (NameM.matches()) {                        
                        playerInfo[0] = NameM.group(1);
                    } else if (HeroesM.matches()) {
                        playerInfo[1] = HeroesM.group(1);
                    } else if (IndexM.matches()) {
                        playerInfo[2] = IndexM.group(1);
                    } else if (TeamsM.matches()) {
                        playerInfo[3] = TeamsM.group(1);
                    }
                }

                String[] playList = playerInfo[1].split(",");
                String[] teamsString = playerInfo[3].split(",");
                int[] teams = new int[teamsString.length];
                for(int i = 0; i < teams.length; i++) {
                    if (!teamsString[i].equals(""))  {
                        teams[i] = Integer.parseInt(teamsString[i]);
                    }
                }
                
                Player player = new Player(playerInfo[0]);
                player.uniqueID = Integer.parseInt(child.getName().replaceAll("\\..*", ""));
                
                for (String hero: playList) {
                    if (!hero.isEmpty()) {
                        player.addHero(Global.AllHeroes.get(hero));
                    }
                }
                
                int gIndex = Integer.parseInt(playerInfo[2]);
                player.globalIndex = gIndex;
                
                while (gIndex > Global.Players.size()){
                    Global.Players.add(new Player("Fake Player"));
                }
                
                if (gIndex == Global.Players.size()) {
                    Global.Players.add(player);
                } else {
                    Global.Players.set(gIndex, player);
                }
                
                text.close();
            }
        }
    }
    
    public static void readTeams() throws FileNotFoundException, IOException {
        File dir = new File(Global.EMERGENCY_TEAM_PATH);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            
            Pattern nameP = Pattern.compile("Name: (.*)");
            Pattern heroesP = Pattern.compile("Hero Pool: (.*)");
            Pattern indexP = Pattern.compile("GlobalIndex: (.*)");
            Pattern playersP = Pattern.compile("Players: (.*)");
            
            for (File child: directoryListing) {
                BufferedReader text = new BufferedReader(new FileReader(child));

                String[] TeamInfo = new String[4];

                String line;
                while((line = text.readLine()) != null){
                    Matcher NameM = nameP.matcher(line);
                    Matcher HeroesM = heroesP.matcher(line);
                    Matcher IndexM = indexP.matcher(line);
                    Matcher PlayersM = playersP.matcher(line);

                    if (NameM.matches()) {                        
                        TeamInfo[0] = NameM.group(1);
                    } else if (HeroesM.matches()) {
                        TeamInfo[1] = HeroesM.group(1);
                    } else if (IndexM.matches()) {
                        TeamInfo[2] = IndexM.group(1);
                    } else if (PlayersM.matches()) {
                        TeamInfo[3] = PlayersM.group(1);
                    }
                }

                String[] playList = TeamInfo[1].split(",");
                String[] playersString = TeamInfo[3].split(",");
                int[] players = new int[playersString.length];
                for(int i = 0; i < players.length; i++) {
                    if (!playersString[i].equals(""))  {
                        players[i] = Integer.parseInt(playersString[i]);
                    }
                }
                
                Team team = new Team(TeamInfo[0]);
                team.uniqueID = Integer.parseInt(child.getName().replaceAll("\\..*", ""));
                
                for(int i=0; i < players.length; i++) {
                    team.addPlayer(Global.Players.get(players[i]));
                }
                
                /*for (String hero: playList) {
                    team.addToPool(Global.AllHeroes.get(hero));
                }*/
                
                int gIndex = Integer.parseInt(TeamInfo[2]);
                team.globalIndex = gIndex;
                
                while (gIndex > Global.Teams.size()){
                    Global.Teams.add(new Team("Fake Team"));
                }
                
                if (gIndex == Global.Teams.size()) {
                    Global.Teams.add(team);
                } else {
                    Global.Teams.set(gIndex, team);
                }
                
                text.close();
            }
        }
    }
}