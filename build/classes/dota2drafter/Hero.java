package dota2drafter;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Hero {
    ImageIcon portraitTiny;
    ImageIcon portraitSmall;
    ImageIcon portraitMedium;
    ImageIcon portraitLarge;
    String[] commonCounters = new String[5];
    int currentCounters;
    String[] commonCombos = new String[5];
    int currentCombos;
    int[] characteristics = new int[13];
    int characteristicsNum;
    String wikiLink;
    String commonName;
    String name;
    String title;
    String abbrv;
    String side;
    String attribute;
    
    public Hero(String cn, String n, String t, String a, String s, String att, ImageIcon[] pictures) {
        commonName = cn;
        name = n;
        title = t;
        abbrv = a;
        side = s;
        attribute = att;
        portraitSmall = pictures[0];
        portraitMedium = pictures[1];
        portraitLarge = pictures[2];
        portraitTiny = new ImageIcon(portraitSmall.getImage().getScaledInstance(27, 15, Image.SCALE_SMOOTH));
        currentCounters = 0;
        currentCombos = 0;
        characteristicsNum = 0;
    }
    
    void AddCounters(String[] counters){
        for (String counter : counters) {
            commonCounters[currentCounters] = counter;
            currentCounters++;
        }
    }
    
    void AddCombos(String[] combos){
        for (String combo : combos) {
            commonCombos[currentCombos] = combo;
            currentCombos++;
        }
    }
    
    void AddCharacters(int[] characters){
        for (int character : characters) {
            characteristics[characteristicsNum] = character;
            characteristicsNum++;
        }
    }
    
    String[] GetCounters() {
        return commonCounters;
    }
    
    String[] GetCombos() {
        return commonCombos;
    }
}
