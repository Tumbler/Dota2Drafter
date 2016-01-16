package dota2drafter;

import java.awt.event.MouseEvent;
import java.util.concurrent.Callable;

public abstract class HeroReturner implements Callable<Void> {
    Hero hero;
    MouseEvent e;
    void heroEquals(Hero hero){
        this.hero = hero;
    }
    void mouseEventEquals(MouseEvent e) {
        this.e = e;
    }
}