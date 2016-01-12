// Authour:         Tumbler41
// Last updated:    5/12/2015

package dota2drafter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Dota2Drafter {
    public static void main(String[] args) throws IOException {
        
        InputStream stream = Dota2Drafter.class.getResourceAsStream( Global.RESOURCE_PATH + "info/HeroList.txt");        
        BufferedReader HeroList = new BufferedReader(new InputStreamReader(stream));
        
        String currentHero;
        // Iterate through all the heroes to add them.
        while ((currentHero = HeroList.readLine()) != null) {
            // Skip comments
            if (currentHero.substring(0, 2).compareTo("//") != 0) {
                Global.AllHeroes.put(currentHero, ResourceRetriever.GetHero(currentHero));
            }
        }

        // Start up the Window Manager
        WindowManager God = new WindowManager();        
    } 
}
