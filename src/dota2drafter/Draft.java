package dota2drafter;
import static dota2drafter.Global.FIRST_BAN;
import static dota2drafter.Global.SECOND_BAN;
import static dota2drafter.Global.FIRST_PICK;
import static dota2drafter.Global.SECOND_PICK;
import javax.swing.JLabel;
public class Draft {
    int firstPick;
    int currentpick;
    int[] draftOrder = {
        FIRST_BAN, SECOND_BAN, FIRST_BAN, SECOND_BAN,
        FIRST_PICK, SECOND_PICK, SECOND_PICK, FIRST_PICK,
        FIRST_BAN, SECOND_BAN, FIRST_BAN, SECOND_BAN,
        SECOND_PICK, FIRST_PICK, SECOND_PICK, FIRST_PICK,
        SECOND_BAN, FIRST_BAN,
        SECOND_PICK, FIRST_PICK};
    int draftPosition;
    JLabel[] myPicks;
    int MPNext = 0;
    JLabel[] myBans;
    int MBNext = 0;
    JLabel[] enemyPicks;
    int EPNext = 0;
    JLabel[] enemyBans;
    int EBNext = 0;
    
    public Draft(int firstPick, JLabel[] a, JLabel[] b, JLabel[] c, JLabel[] d) {
        this.firstPick = firstPick;
        currentpick = firstPick;
        draftPosition = 0;
        myPicks = a;
        myBans = b;
        enemyPicks = c;
        enemyBans = d;
    }
    
    public void next(Hero hero) {        
        System.out.println("NEXT!!");
        switch (draftOrder[draftPosition]) {
            case FIRST_BAN:
                if (firstPick == 0) {
                    myBans[MBNext++].setIcon(hero.portraitSmall);
                } else {
                    enemyBans[EBNext++].setIcon(hero.portraitSmall);
                }
                draftPosition++;
                break;
            case SECOND_BAN:
                if (firstPick == 0) {
                    enemyBans[EBNext++].setIcon(hero.portraitSmall);
                } else {
                    myBans[MBNext++].setIcon(hero.portraitSmall);
                }
                draftPosition++;
                break;
            case FIRST_PICK:
                if (firstPick == 0) {
                    myPicks[MPNext++].setIcon(hero.portraitSmall);
                } else {
                    enemyPicks[EPNext++].setIcon(hero.portraitSmall);
                }
                draftPosition++;
                break;
            case SECOND_PICK:
                if (firstPick == 0) {
                    enemyPicks[EPNext++].setIcon(hero.portraitSmall);
                } else {
                    myPicks[MPNext++].setIcon(hero.portraitSmall);
                }
                draftPosition++;
                break;
        }
    }
}
