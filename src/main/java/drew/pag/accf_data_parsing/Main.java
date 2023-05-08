package drew.pag.accf_data_parsing;

import drew.pag.accf_data_parsing.ui.LoadDolDialog;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author drewpag
 */
public class Main {
    
    final static String[] bugs = new String[]{"Common Butterfly", "Yellow Butterfly", "Tiger Butterfly", "Peacock Butterfly",
        "Monarch Butterfly", "Emperor Butterfly", "Agrias Butterfly", "Raja Brooke", "Birdwing", "Moth", "Oak Silk Moth", "Honeybee",
        "Bee", "Long Locust", "Migratory Locust", "Mantis", "Orchid Mantis", "Brown Cicada", "Robust Cicada", "Walker Cicada",
        "Evening Cicada", "Lantern Fly", "Red Dragonfly", "Darner Dragonfly", "Banded Dragonfly", "Giant Petaltail", "Ant",
        "Pondskater", "Diving Beetle", "Snail", "Cricket", "Bell Cricket", "Grasshopper", "Mole Cricket", "Walking Leaf",
        "Walkingstick", "Bagworm", "Ladybug", "Violin Beetle", "Longhorn Beetle", "Dung Beetle", "Firefly", "Fruit Beetle",
        "Scarab Beetle", "Jewel Beetle", "Miyama Stag", "Saw Stag", "Giant Stag", "Rainbow Stag", "Cyclommatus", "Golden Stag",
        "Dynastid Beetle", "Atlas Beetle", "Elephant Beetle", "Hercules Beetle", "Goliath Beetle", "Flea", "Pill Bug", "Mosquito",
        "Fly", "Centipede", "Spider", "Tarantula", "Scorpion"};
    
    // the index of each time range corresponds to its internal ID
    final static String[] times = new String[]{"11PM - 4AM", "4AM - 8AM", "8AM - 4PM", "4PM - 5PM", "5PM - 7PM", "7PM - 11PM"};
    
    /**
        * Ghidra addresses... but Ghidra has some sort of offset compared to the actual .dol file. The January data starts
        * at 4e8638... offset of 3f20 compare to Ghidra... so subtract from all below
        * 
        * January - 804ec558       4e8638
        * February - 804ec5c0      4E86A0
        * March - 804ec628         4E8708 
        * April - 804ec6d8         4E87B8
        * May - 804ec7b0           4E8890
        * June - 804ec8b8          4E8998
        * July - 804eca34          4E8B14
        * August - 804ecc68        4E8D48
        * September - 804ecea8     4E8F88
        * October - 804ed050       4E9130
        * November - 804ed138      4E9218
        * December - 804ed208      4E92E8
    */

    static MonthPair[] bugAddrs = new MonthPair[]{new MonthPair("January", "4e8638"), new MonthPair("February", "4E86A0"),
        new MonthPair("March", "4E8708"), new MonthPair("April", "4E87B8"), new MonthPair("May", "4E8890"),
        new MonthPair("June", "4E8998"), new MonthPair("July", "4E8B14"), new MonthPair("August", "4E8D48"),
        new MonthPair("September", "4E8F88"), new MonthPair("October", "4E9130"),
        new MonthPair("November", "4E9218"), new MonthPair("December", "4E92E8")};

    public static void main(String[] args) {
        
        // display the pane, and get the file path + options (?) back
//        String dolPathStr = LoadDolDialog.display();
        
        // hard code for now lol
        String dolPathStr = "C:/Users/drewp/Desktop/games/ACCF_JP_full_disc/DATA/sys/main.dol";
        
        if(dolPathStr.equals("")){
            // user canceled... gg
            System.exit(0);
        }
        
        // parse main.dol!
        try{
            Path dolPath = Paths.get(dolPathStr);
            
            byte[] data = null;
            try {
                data = Files.readAllBytes(dolPath);
            } catch (IOException ex) {
                System.out.println("Exception while reading main.dol byte array:\n" + ex);
                System.exit(0);
            }
            
            ByteBuffer bb = ByteBuffer.wrap(data);
            // BIG
            bb.order(ByteOrder.BIG_ENDIAN);
            
            // month counter
            int i = 0;
            
            for(MonthPair m: bugAddrs){
                StringBuilder sb = new StringBuilder();
                sb.append("\n").append(m.getMonth()).append(":\n");
                int startOffset = Integer.parseInt(String.valueOf(m.getAddress()), 16);
                int endOffset = (i < 11) ? Integer.parseInt(bugAddrs[i+1].getAddress(), 16) : Integer.parseInt("4E9350", 16);
                int lastId = 999;
                int lastSpawnRange = 0;
                int timeOfDayId = -1;
                while(startOffset < endOffset){
                    
                    // no unsigned ints... this is necessary to read an int from 2 bytes
                    byte[] bytes = new byte[4];
                    bb.get(startOffset, bytes, 0, 4);
                  
                    // first two bytes are the bug ID
                    int bugId = ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff);
                    
                    // second two bytes are the spawn range upper value
                    int upperSpawnRange = ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff);
                    
                    if(bugId <= lastId){
                        sb.append("\n");
                        lastId = -1;
                        lastSpawnRange = 0;
                        timeOfDayId++;
                        if(timeOfDayId < 6){
                            sb.append(times[timeOfDayId]).append("\n");
                        }
                    }
                    
                    String bugName = bugs[bugId];
                    int spawnWeight = upperSpawnRange - lastSpawnRange;

                    // sometimes, there are 4 bytes of 0x0 that this is picking up as common butterfly... so ignore anything with weight 0
                    if(spawnWeight != 0){
                        sb.append(String.format("%1$18s", bugName)).append("\t").append(spawnWeight).append("\n");
                    }
                    
                    lastId = bugId;
                    lastSpawnRange = upperSpawnRange;
                    
                    startOffset += 4;
                }
                
                System.out.println(sb);
                
                i++;
            }
            
        } catch(Exception ex){
            System.out.println("Exception " + ex);
        }
    }
    
    static class MonthPair{
        String month;
        String address;
        
        public MonthPair(String m, String addr){
            this.month = m;
            this.address = addr;
        }
        
        public String getMonth(){
            return month;
        }
        
        public String getAddress(){
            return address;
        }
    }
}
