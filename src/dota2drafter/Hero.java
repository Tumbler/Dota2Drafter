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
    
    void addCounters(String[] counters){
        for (String counter : counters) {
            commonCounters[currentCounters] = counter;
            currentCounters++;
        }
    }
    
    void addCombos(String[] combos){
        for (String combo : combos) {
            commonCombos[currentCombos] = combo;
            currentCombos++;
        }
    }
    
    void addCharacters(int[] characters){
        for (int character : characters) {
            characteristics[characteristicsNum] = character;
            characteristicsNum++;
        }
    }
    
    String[] getCounters() {
        return commonCounters;
    }
    
    String[] getCombos() {
        return commonCombos;
    }
    
    boolean hasCharacteristic(int num) {
        for(int characteristic: characteristics) {
            if (characteristic == num) {
                return true;
            }
        }
        return false;
    }
}
