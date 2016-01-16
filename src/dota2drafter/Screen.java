package dota2drafter;

import javax.swing.JPanel;

public abstract class Screen {
    JPanel Eve = new JPanel();
    
    abstract void refresh();
    abstract void Switch(); // Since switch is a reserved keyword, this can't be lowercase; rename to swap() maybe?
    abstract void Return(); // Since return is a reserved keyword, this can't be lowercase; rename to result() maybe?
}
