package dota2drafter;

import javax.swing.JPanel;

public abstract class Screen {
    JPanel Eve = new JPanel();
    
    abstract void Refresh();
    abstract void Switch();
    abstract void Return();
}
