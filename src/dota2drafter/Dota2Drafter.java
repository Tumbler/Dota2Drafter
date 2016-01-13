// Author:         Tumbler41
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
        
        String currentLine;
        // Iterate through all the heroes to add them.
        while ((currentLine = HeroList.readLine()) != null) {
            // Skip comments
            if (currentLine.substring(0, 2).compareTo("//") != 0) {
                String currentHero = currentLine;
                Global.AllHeroes.put(currentHero, ResourceRetriever.GetHero(currentHero));
            }
        }
        
        // Load all the current players
        ResourceRetriever.ReadPlayers();
        ResourceRetriever.ReadTeams();

        // Start up the Window Manager
        WindowManager God = new WindowManager();        
    } 
}
