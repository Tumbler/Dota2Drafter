package dota2drafter;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class Player {
    String name;
    private Hero[] playList = new Hero[Global.NUMBER_OF_HEROES + 1];
    // Number of heros they can play
    private int playNumber;
    // Tracks which teams this player is a part of. (Mostsly for deleting puroses)
    List<Integer> teams = new ArrayList<>();
    String DotaBuffLink;
    int uniqueID;
    
    public Player(String n) {
        name = n;
        playNumber = 0;
        uniqueID = -1;
    }
    
    void AddHero(Hero hero) {
        // Inserstion sort
        int i = playNumber;
        if (playNumber == 0){
            playList[0] = hero;
        } else {
            while(i > 0 && (hero.commonName.compareTo(playList[i-1].commonName)) < 0) {
                playList[i] = playList[i-1];
                i--;
            }
            playList[i] = hero;
        }
        playNumber++;
    }    
    
    void RemoveHero(Hero hero) {
        boolean found = false;
        for (int i=0; i < playNumber; i++) {
            if (hero.abbrv.matches(playList[i].abbrv)) {
                found = true;
                playNumber--;
            }
            if (found) {
                playList[i] = playList[i+1];
            }
        }
    }
    
    void savePlayer() {
        uniqueID = Global.Players.size();
        Global.Players.add(this);
    }
    
    void Delete() {
        Global.Players.remove(this);        
        for(int i=uniqueID; i < Global.Players.size(); i++) {
            Global.Players.get(i).uniqueID--;
        }
        for(int team: teams) {
            Global.Teams.get(team).DeletePlayer(this);
        }
    }
    
    JPanel PlayerPreview() {
        JPanel panel = new JPanel(new MigLayout("", "grow, fill"));
        JPanel S = new JPanel(new MigLayout("wrap 8"));
        for(int i=0; i < this.playNumber; i++) {
            S.add(new JLabel(playList[i].portraitTiny));
        }
        panel.add(S, "south, growx, width min:pref:300");
        panel.add(new JLabel(" " + this.name), "west");
        return panel;
    }
    
    Hero[] GetPlayList() {
        Hero[] returnList = new Hero[playNumber];
        for (int i=0; i < playNumber; i++) {
            returnList[i] = playList[i];
        }
        return returnList;
    }
    
    void SortPlayList() {
        
    }
    
    void DeleteTeam(Team teamPassed) {
        boolean found = false;
        for (int i=0; i < teams.size(); i++) {
            if (teamPassed.uniqueID == teams.get(i)) {
                found = true;
                teams.remove(i);
            }
        }
        for (int i=0; i < teams.size(); i++) {        
            if (teams.get(i) > teamPassed.uniqueID) {
                teams.set(i, (teams.get(i) - 1));
            }
        }
    }
}
