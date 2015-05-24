package dota2drafter;

import java.util.concurrent.Callable;

public abstract class HeroReturner implements Callable<Void> {
    Hero hero;    
    void HeroEquals(Hero hero){
        this.hero = hero;
    }            
}