package dota2drafter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class Team {
    String name;
    private String[] heroPool = new String[Global.NUMBER_OF_HEROES];
    int poolNumber;
    private int[] players = {-1, -1, -1, -1, -1, -1};
    private int numOfPlayers;
    int uniqueID;
    
    public Team(String n) {
        name = n;
        poolNumber = 0;
        numOfPlayers = 0;
        uniqueID = -1;
    }
    
    Hero[] GetHeroPool() {
        Hero[] returnList = new Hero[numOfPlayers];
        for (int i=0; i < poolNumber; i++) {
            returnList[i] = Global.AllHeroes.get(heroPool[i]);
        }
        return returnList;
    }
    
    Player[] GetPlayers(){
        Player[] returnList = new Player[numOfPlayers];
        for (int i=0; i < numOfPlayers; i++) {
            returnList[i] = Global.Players.get(players[i]);
        }
        return returnList;
    }
    
    int AddPlayer(Player player) {
        if (numOfPlayers < 5) {
            players[numOfPlayers] = player.uniqueID;
            numOfPlayers++;
            if (uniqueID != -1) {
                player.teams.add(uniqueID);
            }
            return 0;
        } else {
            return 1;
        }
    }
    
    int RemovePlayer(Player player) {
        int returnVal = 0;
        for (int i=0; i < numOfPlayers; i++) {
            if (player.uniqueID == players[i]) {
                returnVal = 1;
                numOfPlayers--;
                if (uniqueID != -1) {
                    player.teams.remove(uniqueID);
                }
            }
            if (returnVal == 1) {
                players[i] = players[i+1];
            }
        }
        return returnVal;
    }
    
    // Desn't actually delete, just adjusts for the deletion.
    void DeletePlayer(Player player) {
        boolean found = false;
        for (int i=0; i < numOfPlayers; i++) {
            if (player.uniqueID == players[i]) {
                found = true;
                numOfPlayers--;
            }
            if (found) {
                players[i] = players[i+1];
            }
            if (players[i] > player.uniqueID) {
                players[i]--;
            }
        }
    }
    
    void AddToPool(Hero hero) {
        // Don't want duplicates
        if (!Global.ExsistsInPool(hero.abbrv, GetHeroPool()) && poolNumber < Global.NUMBER_OF_HEROES) {
           AddHero(hero); 
        }
    }
    
    void AddHero(Hero hero) {
        // Inserstion sort
        // MIGHT BE WRONG!!! PLEASE CHECK THIS!!
        int i = poolNumber;
        while(hero.abbrv.compareTo(heroPool[i]) < 0 && i > 0) {
            heroPool[i-1] = heroPool[i];
            i--;
        }
        heroPool[i] = hero.abbrv;
    }
    
    void saveTeam() {
        uniqueID = Global.Teams.size();
        Global.Teams.add(this);
        for (int i=0; i < numOfPlayers; i++) {
            Global.Players.get(players[i]).teams.add(uniqueID);          
        }
    }
    
    JPanel TeamPreview() {
        JPanel panel = new JPanel(new MigLayout("", "grow, fill"));
        JPanel S = new JPanel(new MigLayout("", "grow, fill"));
        for(Player player: GetPlayers()) {
            S.add(new JLabel(player.name));
        }
        panel.add(S, "south, growx");
        panel.add(new JLabel(" " + this.name), "west");
        return panel;
    }
    
    void Delete(){
        Global.Teams.remove(this);
        for(int i=uniqueID; i < Global.Teams.size(); i++) {
            Global.Teams.get(i).uniqueID--;
        } 
        for (Player player: GetPlayers()) {
            Global.Players.get(player.uniqueID).DeleteTeam(this);
        }
    }
}
