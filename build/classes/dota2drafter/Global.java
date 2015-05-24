package dota2drafter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

public class Global {
    public final static int NUMBER_OF_HEROES = 110;
    public final static String RESOURCE_PATH = "/dota2drafter/Resources/";
    public final class Characteristics {
        // push, nuke, stun, rstun, teamfight, invisible, tank, heal, minus armor, mobile, phisical damage, silence, squishy
        final static int PUSH       = 1;
        final static int NUKE       = 2;
        final static int STUN       = 3;
        final static int RSTUN      = 4;
        final static int TEAMFIGHT  = 5;
        final static int INVIS      = 6;
        final static int TANK       = 7;
        final static int HEAL       = 8;
        final static int NEGARMOR   = 9;
        final static int MOBILE     = 10;
        final static int PHDAMAGE   = 11;
        final static int SILENCE    = 12;
        final static int SQUISHY    = 13;
        final static int SPELLPIERCE= 14;
        final static int ROSH       = 15;
    }
    // This is where we store all the hero data.
    public static Map<String, Hero> AllHeroes = new HashMap<>();
    public static List<Team> Teams = new ArrayList<Team>();    
    public static List<Player> Players = new ArrayList<Player>();    
    public static String LARGEPATH = RESOURCE_PATH + "pics/large/";
    public static String MEDIUMPATH = RESOURCE_PATH + "pics/medium/";
    public static String SMALLPATH = RESOURCE_PATH + "pics/small/";    
    public static String PICPATH = RESOURCE_PATH + "pics/";
    public static String INFOPATH = RESOURCE_PATH + "info/";
    public final static ImageIcon QUESTIONPIC = ResourceRetriever.GetImage("Question.png", 59, 33);
    static int i;
    static int j;
    public static boolean ExsistsInPool(String find, Hero[] heroes) {
        // Null check everything before moving on
        if (heroes == null) {
            System.out.println("found null:" + i++);
            return false;
        }
        
        System.out.println("moving on!" + j++);
        for(Hero hero: heroes) {
            if (hero == null){
                return false;
            }
            if (find.matches(hero.abbrv)) {
                return true;
            }
        }
        return false;
    }
}
