package dota2drafter;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ResourceRetriever {
    
    public static ImageIcon GetImage(String image, int x, int y) {
        try {
            URL ImageToURL = Dota2Drafter.class.getResource(Global.PICPATH + image);
            Image rescale = ImageIO.read(ImageToURL);
            rescale = rescale.getScaledInstance(x, y, Image.SCALE_SMOOTH);
            return (new ImageIcon(rescale));
        } catch (IOException ex) {
            Logger.getLogger(ResourceRetriever.class.getName()).log(Level.SEVERE, null, ex);
            return (null);
        }

    }
    
    public static Hero GetHero(String hero) throws IOException{
        InputStream stream = ResourceRetriever.class.getResourceAsStream(Global.RESOURCE_PATH + "info/" + hero + ".txt");
        BufferedReader text = new BufferedReader(new InputStreamReader(stream));
        
        Pattern known = Pattern.compile("CommonName: (.*)");
        Pattern name = Pattern.compile("Name: (.*)");
        Pattern title = Pattern.compile("Title: (.*)");
        Pattern abrv = Pattern.compile("Abbrv: (.*)");
        Pattern side = Pattern.compile("Side: (.*)");
        Pattern attr = Pattern.compile("Attr: (.*)");
        Pattern combo = Pattern.compile("Combos: (.*)");
        Pattern counter = Pattern.compile("Counters: (.*)");
        Pattern character = Pattern.compile("Characteristics: (.*)");
        
        String[] heroInfo = new String[9];
        
        String line;
        while((line = text.readLine()) != null){
            Matcher Known = known.matcher(line);
            Matcher Name = name.matcher(line);
            Matcher Title = title.matcher(line);
            Matcher Abrv = abrv.matcher(line);
            Matcher Side = side.matcher(line);
            Matcher Attr = attr.matcher(line);
            Matcher Combo = combo.matcher(line);
            Matcher Counter = counter.matcher(line);
            Matcher Character = character.matcher(line);
            
            if (Known.matches()) {
                heroInfo[0] = Known.group(1);
            } else if (Name.matches()) {
                heroInfo[1] = Name.group(1);
            } else if (Title.matches()) {
                heroInfo[2] = Title.group(1);
            } else if (Abrv.matches()) {
                heroInfo[3] = Abrv.group(1);
            } else if (Side.matches()) {
                heroInfo[4] = Side.group(1);
            } else if (Attr.matches()) {
                heroInfo[5] = Attr.group(1);
            } else if (Combo.matches()) {
                heroInfo[6] = Combo.group(1);
            } else if (Counter.matches()) {
                heroInfo[7] = Counter.group(1);
            } else if (Character.matches()) {
                heroInfo[8] = Character.group(1);
            }
        }
        
        String[] combos = heroInfo[6].split(",");
        String[] counters = heroInfo[7].split(",");
        String[] charactersString = heroInfo[8].split(",");
        int[] characters = new int[charactersString.length];
        for(int i = 0; i < characters.length; i++) {
            characters[i] = Integer.parseInt(charactersString[i]);
        }
        
        ImageIcon[] portraits = new ImageIcon[3];
        URL ImageToURL1 = Dota2Drafter.class.getResource(Global.SMALLPATH + hero + ".png");
        URL ImageToURL2 = Dota2Drafter.class.getResource(Global.MEDIUMPATH + hero + ".png");
        URL ImageToURL3 = Dota2Drafter.class.getResource(Global.LARGEPATH + hero + ".png");
        Image rescale1 = ImageIO.read(ImageToURL1);
        Image rescale2 = ImageIO.read(ImageToURL2);
        Image rescale3 = ImageIO.read(ImageToURL3);
        rescale1 = rescale1.getScaledInstance(59,  33, Image.SCALE_SMOOTH);
        rescale2 = rescale2.getScaledInstance(205, 115, Image.SCALE_SMOOTH);
        rescale3 = rescale3.getScaledInstance(256, 144, Image.SCALE_SMOOTH);
        portraits[0] = new ImageIcon(rescale1);
        portraits[1] = new ImageIcon(rescale2);
        portraits[2] = new ImageIcon(rescale3);
        
        Hero returnHero = new Hero(
                heroInfo[0],
                heroInfo[1],
                heroInfo[2],
                heroInfo[3],
                heroInfo[4],
                heroInfo[5],
                portraits
        );
        
        returnHero.AddCombos(combos);
        returnHero.AddCounters(counters);
        returnHero.AddCharacters(characters);
        
        return returnHero;
    }
}