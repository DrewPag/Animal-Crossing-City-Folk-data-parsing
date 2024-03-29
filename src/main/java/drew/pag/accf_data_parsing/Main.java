package drew.pag.accf_data_parsing;

import drew.pag.accf_data_parsing.fish.Fish;
import drew.pag.accf_data_parsing.fish.FishSpawnWeight;
import drew.pag.accf_data_parsing.ui.LoadDolDialog;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    
    // fins size 7, eel size 8 for distinguishing purposes
    final static Fish[] fish = new Fish[]{
        new Fish(0, "Bitterling", 1),
        new Fish(1, "Pale Chub", 2),
        new Fish(2, "Crucian Carp", 2),
        new Fish(3, "Dace", 3),
        new Fish(4, "Barbel Steed", 3),
        new Fish(5, "Carp", 4),
        new Fish(6, "Koi", 4),
        new Fish(7, "Goldfish", 1),
        new Fish(8, "Popeyed Goldfish", 1),
        new Fish(9, "Killifish", 1),
        new Fish(10, "Crawfish", 2),
        new Fish(11, "Frog", 2),
        new Fish(12, "Freshwater Goby", 3),
        new Fish(13, "Loach", 2),
        new Fish(14, "Catfish", 4),
        new Fish(15, "Eel", 8),
        new Fish(16, "Giant Snakehead", 5),
        new Fish(17, "Bluegill", 2),
        new Fish(18, "Yellow Perch", 3),
        new Fish(19, "Black Bass", 4),
        new Fish(20, "Pike", 5),
        new Fish(21, "Pond Smelt", 2),
        new Fish(22, "Sweetfish", 3),
        new Fish(23, "Cherry Salmon", 3),
        new Fish(24, "Char", 3),
        new Fish(25, "Rainbow Trout", 4),
        new Fish(26, "Stringfish", 6),
        new Fish(27, "Salmon", 4),
        new Fish(28, "King Salmon", 6),
        new Fish(29, "Guppy", 1),
        new Fish(30, "Angelfish", 1),
        new Fish(31, "Neon Tetra", 1),
        new Fish(32, "Piranha", 2),
        new Fish(33, "Arowana", 4),
        new Fish(34, "Dorado", 5), 
        new Fish(35, "Gar", 6), 
        new Fish(36, "Arapaima", 6),
        new Fish(37, "Sea Butterfly", 1),
        new Fish(38, "Jellyfish", 2),
        new Fish(39, "Seahorse", 1), 
        new Fish(40, "Clownfish", 1), 
        new Fish(41, "Surgeonfish", 2), 
        new Fish(42, "Butterflyfish", 2),
        new Fish(43, "Napoleonfish", 6), 
        new Fish(44, "Zebra Turkeyfish", 3), 
        new Fish(45, "Puffer Fish", 3),
        new Fish(46, "Horse Mackerel", 2),
        new Fish(47, "Barred Knifejaw", 3),
        new Fish(48, "Sea Bass", 5),
        new Fish(49, "Red Snapper", 3),
        new Fish(50, "Dab", 3),
        new Fish(51, "Olive Flounder", 4),
        new Fish(52, "Squid", 3),
        new Fish(53, "Octopus", 3),
        new Fish(54, "Lobster", 5),
        new Fish(55, "Moray Eel", 5),
        new Fish(56, "Football Fish", 4),
        new Fish(57, "Tuna", 6),
        new Fish(58, "Blue Marlin", 6),
        new Fish(59, "Ray", 6),
        new Fish(60, "Ocean Sunfish", 7),
        new Fish(61, "Hammerhead Shark", 7),
        new Fish(62, "Shark", 7),
        new Fish(63, "Coelacanth", 6),
        new Fish(64, "Can", 2), 
        new Fish(65, "Boot", 3), 
        new Fish(66, "Tire", 4), 
        new Fish(67, "Key", 2)};
    
    // the index of each time range corresponds to its internal ID
    final static String[] bugTimes = new String[]{"11PM - 4AM", "4AM - 8AM", "8AM - 4PM", "4PM - 5PM", "5PM - 7PM", "7PM - 11PM"};
    final static String[] fishTimes = new String[]{"4AM - 9AM, 4PM - 9PM", "9AM - 4PM", "9PM - 4AM"};
    
    /**
        * Ghidra addresses... but Ghidra has some sort of offset compared to the actual .dol file. The January data starts
        * at 4e8638... offset of 3f20 compare to Ghidra... so subtract 80003f20 from all below
        * 
        * Bugs:
        * 
        * January - 804ec558        4e8638
        * February - 804ec5c0       4E86A0
        * March - 804ec628          4E8708 
        * April - 804ec6d8          4E87B8
        * May - 804ec7b0            4E8890
        * June - 804ec8b8           4E8998
        * July - 804eca34           4E8B14
        * August - 804ecc68         4E8D48
        * September - 804ecea8      4E8F88
        * October - 804ed050        4E9130
        * November - 804ed138       4E9218
        * December - 804ed208       4E92E8
        * 
        * Fish (River) - each address is the start of the memory region containing the fish spawn data, per month:
        * 
        * January - 804e4c88                4E0D68
        * February - 804e4d14               4E0DF4
        * March - 804e4da0                  4E0E80
        * April - 804e4e20                  4E0F00
        * May - 804e4eb4                    4E0F94   
        * June - 804e4f58                   4E1038
        * July - 804e5018                   4E10F8
        * August (1 - 14) - 804e50dc        4E11BC
        * August (15 - 31) - 804e51a0       4E1280
        * September (1 - 14) - 804e5268     4E1348
        * September (15 - 30) - 804e532c    4E140C
        * October - 804e53e0                4E14C0
        * November - 804e547c               4E155C
        * December - 804e5518               4E15F8
        * 
        * end: 4E1685
        * 
        * Fish (Ocean)
        * January - 804e55a8                4E1688
        * February - 804e5630               4E1710
        * March - 804e56ac                  4E178C
        * April - 804e5728                  4E1808
        * May - 804e57c8                    4E18A8   
        * June - 804e5858                   4E1938
        * July - 804e5900                   4E19E0
        * August (1 - 14) - 804e59bc        4E1A9C
        * August (15 - 31) - 804e5a7c       4E1B5C
        * September (1 - 14) - 804e5b3c     4E1C1C
        * September (15 - 30) - 804e5c00    4E1CE0
        * October - 804e5cbc                4E1D9C
        * November - 804e5d44               4E1E24
        * December - 804e5dd8               4E1EB8
    */

    static MonthPair[] bugAddrs = new MonthPair[]{new MonthPair("January", "4e8638"), new MonthPair("February", "4E86A0"),
        new MonthPair("March", "4E8708"), new MonthPair("April", "4E87B8"), new MonthPair("May", "4E8890"),
        new MonthPair("June", "4E8998"), new MonthPair("July", "4E8B14"), new MonthPair("August", "4E8D48"),
        new MonthPair("September", "4E8F88"), new MonthPair("October", "4E9130"),
        new MonthPair("November", "4E9218"), new MonthPair("December", "4E92E8")};
    
    static MonthPair[] fishRiverAddrs = new MonthPair[]{new MonthPair("January", "4E0D68"), new MonthPair("February", "4E0DF4"),
        new MonthPair("March", "4E0E80"), new MonthPair("April", "4E0F00"), new MonthPair("May", "4E0F94"),
        new MonthPair("June", "4E1038"), new MonthPair("July", "4E10F8"), new MonthPair("August (1 - 14)", "4E11BC"),
        new MonthPair("August (15 - 31)", "4E1280"), new MonthPair("September (1 - 14)", "4E1348"),
        new MonthPair("September (15 - 30)", "4E140C"), new MonthPair("October", "4E14C0"),
        new MonthPair("November", "4E155C"), new MonthPair("December", "4E15F8")};
    
    static MonthPair[] fishOceanAddrs = new MonthPair[]{new MonthPair("January", "4E1688"), new MonthPair("February", "4E1710"),
        new MonthPair("March", "4E178C"), new MonthPair("April", "4E1808"), new MonthPair("May", "4E18A8"),
        new MonthPair("June", "4E1938"), new MonthPair("July", "4E19E0"), new MonthPair("August (1 - 14)", "4E1A9C"),
        new MonthPair("August (15 - 31)", "4E1B5C"), new MonthPair("September (1 - 14)", "4E1C1C"),
        new MonthPair("September (15 - 30)", "4E1CE0"), new MonthPair("October", "4E1D9C"),
        new MonthPair("November", "4E1E24"), new MonthPair("December", "4E1EB8")};
    
    final static String riverFishEndAddr = "4E1685";
    final static String oceanFishEndAddr = "4E1F3D";
    
    static String[] fishAcreIds = new String[] {"River", "Lake", "Waterfall", "Pond", "River Mouth", "Ocean (rain/snow)", "Ocean"};
    
    static Map<Integer, Map<Integer, List<FishSpawnWeight>>> riverFishSpawnWeightMap = new HashMap<>();
    static Map<Integer, Map<Integer, List<FishSpawnWeight>>> oceanFishSpawnWeightMap = new HashMap<>();
    
    static Double[][] fishSpawnWeightArray = new Double[68][42];
    static Double[][] fishShadowBasedArray = new Double[68][42];
    static Double[][] bugBasePercentagesArray = new Double[64][72];
    
    static String fishPercentagesCsv = "C:/Users/drewp/Desktop/cf_fish_percentages.csv";
    static String fishShadowPercentagesCsv = "C:/Users/drewp/Desktop/cf_fish_shadow_percentages.csv";
    static String bugPercentagesCsv = "C:/Users/drewp/Desktop/cf_bug_percentages.csv";

    public static void main(String[] args) {
        
        for(int i=0; i < 68; i++){
            for(int j = 0; j < 42; j++){
                fishSpawnWeightArray[i][j] = 0.0;
                fishShadowBasedArray[i][j] = 0.0;
            }
        }
        
        for(int i=0; i < 64; i++){
            for(int j = 0; j < 72; j++){
                bugBasePercentagesArray[i][j] = 0.0;
            }
        }
        
        // display the pane, and get the file path + options (?) back
//        String dolPathStr = LoadDolDialog.display();
        
        // hard code for now lol
        String dolPathStr = "C:/Users/drewp/Desktop/games/ACCF_JP_full_disc/DATA/sys/main.dol";
        
        if(dolPathStr.equals("")){
            // user canceled... gg
            System.exit(0);
        }
        
        // lol
        boolean parseBugs = false;
        String result = parseBugData(dolPathStr);

        boolean isOcean = true;

        result = parseFishData(dolPathStr, !isOcean);
        result = parseFishData(dolPathStr, isOcean);
//        System.out.println(result);
        
        // process all of the fish spawn weights
        String riverSpawnWeights = processRiverFishSpawnWeights();
        String oceanSpawnWeights = processOceanFishSpawnWeights();
//        System.out.println(riverSpawnWeights);
//        System.out.println(oceanSpawnWeights);
        
        writeFishToCsv();
        writeBugsToCsv();
    }
    
    private static String parseBugData(String dolPathStr){
        StringBuilder result = new StringBuilder();
        
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
            int monthId = 0;
            
            for(MonthPair m: bugAddrs){
                StringBuilder sb = new StringBuilder();
                sb.append("\n").append(m.getMonth()).append(":\n");
                int startOffset = Integer.parseInt(String.valueOf(m.getAddress()), 16);
                int endOffset = (monthId < 11) ? Integer.parseInt(bugAddrs[monthId+1].getAddress(), 16) : Integer.parseInt("4E9350", 16);
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
                            sb.append(bugTimes[timeOfDayId]).append("\n");
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
                
//                System.out.println(sb);
                result.append(sb);
                
                monthId++;
            }
            
        } catch(Exception ex){
            System.out.println("Exception " + ex);
        }
        
        return result.toString();
    }
    
    private static String parseFishData(String dolPathStr, boolean isOcean){
        
        StringBuilder result = new StringBuilder();
        
        String endAddr = (isOcean ? oceanFishEndAddr : riverFishEndAddr);
        MonthPair[] fishAddrs = (isOcean ? fishOceanAddrs : fishRiverAddrs);
        
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
            int monthId = 0;
            
            for(MonthPair m: fishAddrs){
                StringBuilder sb = new StringBuilder();
                sb.append("\n").append(m.getMonth()).append(":\n");
                
                // one map for each month, containing the 3 spawn weight lists for each time of day
                Map<Integer, List<FishSpawnWeight>> monthlySpawnWeightsMap = new HashMap<>();
                
                ArrayList<FishSpawnWeight> eveningWeights = new ArrayList<>();
                ArrayList<FishSpawnWeight> dayWeights = new ArrayList<>();
                ArrayList<FishSpawnWeight> nightWeights = new ArrayList<>();
                
                monthlySpawnWeightsMap.put(0, eveningWeights);
                monthlySpawnWeightsMap.put(1, dayWeights);
                monthlySpawnWeightsMap.put(2, nightWeights);
                
                int lastId = 999;
                int lastSpawnRange = 0;
                int timeOfDayId = -1;
                int j = -1;
                
                int startOffset = Integer.parseInt(String.valueOf(m.getAddress()), 16);
                int endOffset = (monthId < 13) ? Integer.parseInt(fishAddrs[monthId+1].getAddress(), 16) : Integer.parseInt(endAddr, 16);
//                System.out.println("startOffset " + startOffset);
                
                while(startOffset < endOffset){

                    // no unsigned ints... this is necessary to read an int from 2 bytes
                    byte[] bytes = new byte[3];
                    bb.get(startOffset, bytes, 0, 3);

                    // first byte is the fish ID
                    int fishId = Byte.toUnsignedInt(bytes[0]);

                    // second byte is the acre type (?)
                    int acreId = Byte.toUnsignedInt(bytes[1]);

                    // final byte is the spawn range upper value
                    int upperSpawnRange = Byte.toUnsignedInt(bytes[2]);
                    
                    // the fish data is not perfectly lined up, presumably because each fish takes up 3 bytes and not 4?
                    // so sometimes there is an extra blank byte at the end of a data segment...
                    // if the spawn weight is 0, increase the starting byte by 1 and try again.
                    if(upperSpawnRange <= 0 || acreId > 6){
                        startOffset++;
                        continue;
                    } 
                    
                    // sometimes there are 2 padding bytes...
                    // for river, < 10 checks for something (?)
                    // for ocean, >= 27 checks if the offset ID is at least Salmon
                    else if((!isOcean && fishId == 0 && acreId == 0 && upperSpawnRange < 10) 
                            || (isOcean && fishId == 0 && acreId == 0 && upperSpawnRange >= 27)){
                        startOffset += 2;
                        continue;
                    }
                    
//                    System.out.println("fishId: " + fishId + ", acreId " + acreId + ", upperSpawnRange " + upperSpawnRange);

                    if(fishId <= lastId){
                        sb.append("\n");
                        j++;
                        lastId = -1;
                        lastSpawnRange = 0;
                        timeOfDayId++;
                        if(timeOfDayId < 3){
                            sb.append(fishTimes[timeOfDayId]).append("\n");
//                            System.out.println(m.getMonth() + " " + fishTimes[timeOfDayId]);
                        }
                    }

                    String fishName = fish[fishId].getName();
                    int spawnWeight = upperSpawnRange - lastSpawnRange;
                    
                    // add the spawn weight for this month and time of day to this fish's list
                    FishSpawnWeight weight = new FishSpawnWeight(fishId, monthId, timeOfDayId, acreId, spawnWeight);
                    
                    // add this spawn weight to the specific fish (not currently used)
                    fish[fishId].addSpawnWeight(weight);
                    
                    // add this spawn weight to the corresponding spawn weight list
                    monthlySpawnWeightsMap.get(timeOfDayId).add(weight);
                    
                    if(spawnWeight != 0){
                        sb.append(String.format("%1$18s", fishName)).append("\t").append(spawnWeight);
                        if(( !isOcean && acreId != 0) || (isOcean && acreId != 6)){
                            sb.append("\t").append(fishAcreIds[acreId]);
                        }
                        sb.append("\n");
                    }

                    lastId = fishId;
                    lastSpawnRange = upperSpawnRange;

                    startOffset += 3;
                }
                
                result.append(sb);
                
                // add the spawn weight map for this month to the master map
                if(isOcean){
                    oceanFishSpawnWeightMap.put(monthId, monthlySpawnWeightsMap);
                } else{
                    riverFishSpawnWeightMap.put(monthId, monthlySpawnWeightsMap);
                }

                monthId++;
            }
            
//            System.out.println(result);
            
            if(isOcean){
                return processOceanFishSpawnWeights();
            } else{
                return processRiverFishSpawnWeights();
            }
            
        } catch(Exception ex){
            System.out.println("Exception " + ex);
            ex.printStackTrace();
            
            return result.toString();
        }
        
        
    }
    
    private static String processRiverFishSpawnWeights(){
        
        StringBuilder sb = new StringBuilder();
        
        for(int monthId = 0; monthId < 14; monthId++){
            
            sb.append("\n").append(fishRiverAddrs[monthId].getMonth()).append(":\n");
            
            Map<Integer, List<FishSpawnWeight>> monthMap = riverFishSpawnWeightMap.get(monthId);
            
            for(int timeOfDayId = 0; timeOfDayId < 3; timeOfDayId++){
                
                sb.append("\n").append(String.format("%-20s", fishTimes[timeOfDayId]))
                        .append("\tBase %\t\tShadow Based %\n");                
                
                ArrayList<FishSpawnWeight> weights = (ArrayList) monthMap.get(timeOfDayId);
                
                // first, get the total spawn weights
                double totalRiverSpawnWeight = 0;
                double riverSpawnWeight = 0;
                double lakeSpawnWeight = 0;
                double waterfallSpawnWeight = 0;
                double pondSpawnWeight = 0;
                
                // also the shadow-based spawn weights
                double SSRiverSpawnWeight = 0;
                double SSPondSpawnWeight = 0;
                double SRiverSpawnWeight = 0;
                double SPondSpawnWeight = 0;
                double MRiverSpawnWeight = 0;
                double MWaterfallSpawnWeight = 0;
                double LRiverSpawnWeight = 0;
                double LLakeSpawnWeight = 0;
                double LLRiverSpawnWeight = 0;
                double LLLakeSpawnWeight = 0;
                double LLLRiverSpawnWeight = 0;
                double LLLLakeSpawnWeight = 0;
                
                for(FishSpawnWeight fsw: weights){
                    int w = fsw.getSpawnWeight();
//                    System.out.println("weight " + w + " for fish ID " + fsw.getFishId());
                    
                    switch(fsw.getAcreId()){
                        
                        // river
                        case 0:
                            riverSpawnWeight += w;
                            break;
                        
                        // Lake
                        case 1:
                            lakeSpawnWeight += w;
                            break;
                            
                        // Waterfall
                        case 2:
                            waterfallSpawnWeight += w;
                            break;
                            
                        // Pond
                        case 3:
                            pondSpawnWeight += w;
                            break;
                    }
                    
                    switch(fish[fsw.getFishId()].getSize()){
                        // Tiny (SS)
                        case 1:
                            if(fsw.getAcreId() == 0){
                                SSRiverSpawnWeight += w;
                            } else if(fsw.getAcreId() == 3){
                                SSPondSpawnWeight += w;
                            }
                            break;
                            
                        // Small (S)
                        case 2:
                            if(fsw.getAcreId() == 0){
                                SRiverSpawnWeight += w;
                            } else if(fsw.getAcreId() == 3){
                                SPondSpawnWeight += w;
                            }
                            break;
                            
                        // Medium (M)
                        case 3:
                            if(fsw.getAcreId() == 0){
                                MRiverSpawnWeight += w;
                            } else if(fsw.getAcreId() == 2){
                                MWaterfallSpawnWeight += w;
                            }
                            break;
                            
                        // Large (L)
                        case 4:
                            if(fsw.getAcreId() == 0){
                                LRiverSpawnWeight += w;
                            } else if(fsw.getAcreId() == 1){
                                LLakeSpawnWeight += w;
                            }
                            break;
                            
                        // Extra Large (LL)
                        case 5:
                            if(fsw.getAcreId() == 0){
                                LLRiverSpawnWeight += w;
                            } else if(fsw.getAcreId() == 1){
                                LLLakeSpawnWeight += w;
                            }
                            break;
                            
                        // Huge (LLL)
                        case 6:
                            if(fsw.getAcreId() == 0){
                                LLLRiverSpawnWeight += w;
                            } else if(fsw.getAcreId() == 1){
                                LLLLakeSpawnWeight += w;
                            }
                            break;
                    }
                }
                
                // seems like all spawn weights matter when determining river fish... so why did I separate them...
                totalRiverSpawnWeight = riverSpawnWeight + lakeSpawnWeight + waterfallSpawnWeight + pondSpawnWeight;
                
                // then, calculate the percentage for each individual fish
                for(FishSpawnWeight fsw: weights){
                    sb.append(String.format("%1$20s", fish[fsw.getFishId()].getName())).append("\t")
                            .append(String.format("%.1f", (100.0 * (fsw.getSpawnWeight() / totalRiverSpawnWeight))));
//                            .append("%");
                    
                    // handle the shadow-based %
                    double totalWeightToUse = 0;
                    
                    switch(fish[fsw.getFishId()].getSize()){
                        // Tiny (SS)
                        case 1:
                            if(fsw.getAcreId() == 0){
                                totalWeightToUse = SSRiverSpawnWeight;
                            } else if(fsw.getAcreId() == 3){
                                totalWeightToUse = SSPondSpawnWeight;
                            }
                            break;
                            
                        // Small (S)
                        case 2:
                            if(fsw.getAcreId() == 0){
                                totalWeightToUse = SRiverSpawnWeight;
                            } else if(fsw.getAcreId() == 3){
                                totalWeightToUse = SPondSpawnWeight;
                            }
                            break;
                            
                        // Medium (M)
                        case 3:
                            if(fsw.getAcreId() == 0){
                                totalWeightToUse = MRiverSpawnWeight;
                            } else if(fsw.getAcreId() == 2){
                                totalWeightToUse = MRiverSpawnWeight + MWaterfallSpawnWeight;
                            }
                            break;
                            
                        // Large (L)
                        case 4:
                            if(fsw.getAcreId() == 0){
                                totalWeightToUse = LRiverSpawnWeight;
                            } else if(fsw.getAcreId() == 1){
                                totalWeightToUse = LRiverSpawnWeight + LLakeSpawnWeight;
                            }
                            break;
                            
                        // Extra Large (LL)
                        case 5:
                            if(fsw.getAcreId() == 0){
                                totalWeightToUse = LLRiverSpawnWeight;
                            } else if(fsw.getAcreId() == 1){
                                totalWeightToUse = LLRiverSpawnWeight + LLLakeSpawnWeight;
                            }
                            break;
                            
                        // Huge (LLL)
                        case 6:
                            if(fsw.getAcreId() == 0){
                                totalWeightToUse = LLLRiverSpawnWeight;
                            } else if(fsw.getAcreId() == 1){
                                totalWeightToUse = LLLRiverSpawnWeight + LLLLakeSpawnWeight;
                            }
                            break;
                            
                        // Eel (lol)
                        case 8:
                            totalWeightToUse = fsw.getSpawnWeight();
                            break;
                    }
                    
                    double shadowBasedPercent = 100.0 * (fsw.getSpawnWeight() / totalWeightToUse);
                    
                    sb.append("\t\t").append(String.format("%.1f", shadowBasedPercent));
                            
                    if(fsw.getAcreId() != 0){
                        sb.append("\t").append(fishAcreIds[fsw.getAcreId()]);
                    }
                    sb.append("\n");
                    
                    // add the fish spawn weight and shadow based % to the appropriate entry in the 2d arrays
                    int colIndex = (monthId * 3) + timeOfDayId;
                    
                    fishSpawnWeightArray[fsw.getFishId()][colIndex] = 1.0*fsw.getSpawnWeight();
                    fishShadowBasedArray[fsw.getFishId()][colIndex] = shadowBasedPercent;
                }
            }
        }
        
        return sb.toString();
    }
    
    private static String processOceanFishSpawnWeights(){
        
        StringBuilder sb = new StringBuilder();
        
        for(int monthId = 0; monthId < 14; monthId++){
            
            sb.append("\n").append(fishOceanAddrs[monthId].getMonth()).append(":\n");
            
            Map<Integer, List<FishSpawnWeight>> monthMap = oceanFishSpawnWeightMap.get(monthId);
            
            for(int timeOfDayId = 0; timeOfDayId < 3; timeOfDayId++){
                
                sb.append("\n").append(String.format("%-20s", fishTimes[timeOfDayId]))
                        .append("\tBase %\t\tShadow Based %\n");                
                
                ArrayList<FishSpawnWeight> weights = (ArrayList) monthMap.get(timeOfDayId);
                
                // first, get the total spawn weights
                double totalOceanSpawnWeight = 0;
                double oceanSpawnWeight = 0;
                double oceanRainSpawnWeight = 0;
                double riverMouthSpawnWeight = 0;
                
                // also the shadow-based spawn weights
                double SSSpawnWeight = 0;
                double SSpawnWeight = 0;
                double MSpawnWeight = 0;
                double LOceanSpawnWeight = 0;
                double LRiverMouthSpawnWeight = 0;
                double LLSpawnWeight = 0;
                double LLLOceanSpawnWeight = 0;
                double LLLOceanRainSpawnWeight = 0;
                double LLLRiverMouthSpawnWeight = 0;
                double finSpawnWeight = 0;
                
                for(FishSpawnWeight fsw: weights){
                    int w = fsw.getSpawnWeight();
//                    System.out.println("weight " + w + " for fish ID " + fsw.getFishId());
                    
                    switch(fsw.getAcreId()){
                        
                        // River Mouth
                        case 4:
                            riverMouthSpawnWeight += w;
                            break;
                            
                        case 5:
                            oceanRainSpawnWeight += w;
                            break;
                        
                        // Ocean
                        case 6:
                            oceanSpawnWeight += w;
                            break;
                    }
                    
                    switch(fish[fsw.getFishId()].getSize()){
                        // Tiny (SS)
                        case 1:
                            SSSpawnWeight += w;
                            break;
                            
                        // Small (S)
                        case 2:
                            SSpawnWeight += w;
                            break;
                            
                        // Medium (M)
                        case 3:
                            MSpawnWeight += w;
                            break;
                            
                        // Large (L)
                        case 4:
                            if(fsw.getAcreId() == 4){
                                LRiverMouthSpawnWeight += w;
                            } else{
                                LOceanSpawnWeight += w;
                            }
                            break;
                            
                        // Extra Large (LL)
                        case 5:
                            LLSpawnWeight += w;
                            break;
                            
                        // Huge (LLL)
                        case 6:
                            if(fsw.getAcreId() == 4){
                                LLLRiverMouthSpawnWeight += w;
                            } else if(fsw.getAcreId() == 5){
                                LLLOceanRainSpawnWeight += w;
                            } else{
                                LLLOceanSpawnWeight += w;
                            }
                            break;
                            
                        // Sharks (finned)
                        case 7:
                            finSpawnWeight += w;
                            break;
                    }
                }
                
                // combine the spawn weights
                totalOceanSpawnWeight = oceanSpawnWeight + riverMouthSpawnWeight + oceanRainSpawnWeight;
                
                // then, calculate the percentage for each individual fish
                for(FishSpawnWeight fsw: weights){
                    sb.append(String.format("%1$20s", fish[fsw.getFishId()].getName())).append("\t")
                            .append(String.format("%.1f", (100.0 * (fsw.getSpawnWeight() / totalOceanSpawnWeight))));
//                            .append("%");
                    
                    // handle the shadow-based %
                    double totalWeightToUse = 0;
                    
                    switch(fish[fsw.getFishId()].getSize()){
                        // Tiny (SS)
                        case 1:
                            totalWeightToUse = SSSpawnWeight;
                            break;
                            
                        // Small (S)
                        case 2:
                            totalWeightToUse = SSpawnWeight;
                            break;
                            
                        // Medium (M)
                        case 3:
                            totalWeightToUse = MSpawnWeight;
                            break;
                            
                        // Large (L)
                        case 4:
                            if(fsw.getAcreId() == 4){
                                totalWeightToUse = LRiverMouthSpawnWeight + LOceanSpawnWeight;
                            } else{
                                totalWeightToUse = LOceanSpawnWeight;
                            }
                            break;
                            
                        // Extra Large (LL)
                        case 5:
                            totalWeightToUse = LLSpawnWeight;
                            break;
                            
                        // Huge (LLL)
                        case 6:
                            if(fsw.getAcreId() == 4){
                                totalWeightToUse = LLLRiverMouthSpawnWeight + LLLOceanSpawnWeight;
                            } else if(fsw.getAcreId() == 5){
                                totalWeightToUse = LLLOceanRainSpawnWeight + LLLOceanSpawnWeight;
                            }else{
                                totalWeightToUse = LLLOceanSpawnWeight;
                            }
                            break;
                            
                        // Sharks (finned)
                        case 7:
                            totalWeightToUse = finSpawnWeight;
                            break;
                    }
                    
                    double shadowBasedPercent = 100.0 * (fsw.getSpawnWeight() / totalWeightToUse);
                    
                    sb.append("\t\t").append(String.format("%.1f", shadowBasedPercent));
                            
                    if(fsw.getAcreId() == 4){
                        sb.append("\t").append(fishAcreIds[fsw.getAcreId()]);
                    } else if(fsw.getAcreId() == 5){
                        sb.append("\t").append("Rain/Snow");
                    }
                    sb.append("\n");
                    
                    // add the fish spawn weight and shadow based % to the appropriate entry in the 2d arrays
                    int colIndex = (monthId * 3) + timeOfDayId;
                    
                    fishSpawnWeightArray[fsw.getFishId()][colIndex] = 1.0*fsw.getSpawnWeight();
                    fishShadowBasedArray[fsw.getFishId()][colIndex] = shadowBasedPercent;
                }
            }
        }
        
        return sb.toString();
    }
    
    private static void writeFishToCsv(){
        
        // Regular percentages
        try (PrintWriter pw = new PrintWriter(fishPercentagesCsv)) {
            for(int fishIndex = 0; fishIndex < fishSpawnWeightArray.length; fishIndex++){
                String csvLine = getCsvLineFromDoubles(fishSpawnWeightArray[fishIndex]);
                pw.println(csvLine);
            }
        } catch(Exception ex){
            System.out.println("Exception writing bug percentages to .csv");
            ex.printStackTrace();
        }
        
        // Shadow-based percentages
        try (PrintWriter pw = new PrintWriter(fishShadowPercentagesCsv)) {
            for(int fishIndex = 0; fishIndex < fishShadowBasedArray.length; fishIndex++){
                String csvLine = getCsvLineFromDoubles(fishShadowBasedArray[fishIndex]);
                pw.println(csvLine);
            }
        } catch(Exception ex){
            System.out.println("Exception writing bug percentages to .csv");
            ex.printStackTrace();
        }
    }
    
    private static void writeBugsToCsv(){
        try (PrintWriter pw = new PrintWriter(bugPercentagesCsv)) {
            for(int bugIndex = 0; bugIndex < bugBasePercentagesArray.length; bugIndex++){
                String csvLine = getCsvLineFromDoubles(bugBasePercentagesArray[bugIndex]);
                pw.println(csvLine);
            }
        } catch(Exception ex){
            System.out.println("Exception writing bug percentages to .csv");
            ex.printStackTrace();
        }
    }
    
    private static String getCsvLineFromDoubles(Double[] data){
        return Stream.of(data)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
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
