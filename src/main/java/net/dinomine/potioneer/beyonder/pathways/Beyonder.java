package net.dinomine.potioneer.beyonder.pathways;

public class Beyonder {
    protected int sequence;
    protected String name;
    protected int color;
    public int[] maxSpirituality = new int[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100};

    public Beyonder(int sequence){
        this(sequence, "Beyonderless");
    }

    public static int getX(){
        return 64;
    }

    public static int getY(){
        return 64;
    }

    public Beyonder(int sequence, String path){
        this.sequence = sequence;
        this.name = path;
        this.color = 0x404040;
    }

    public int getMaxSpirituality(int seq){
        return maxSpirituality[seq%10];
    }

    public int getId(){
        return -1;
    }

    public int getColor(){
        return this.color;
    }

    public int getSequence(){
        return this.sequence;
    }

    /**
     * returns the id string of the sequence. it should be written like "Duke_of_Entropy", with capitalized words and underscores for spaces
     * this way the names can be procedurally acquired
     * @return
     */

    public static String getPathwayName(int id, boolean capitalize){
        if(capitalize){
            return switch(Math.floorDiv(id, 10)){
                case 0 -> "Wheel_of_Fortune";
                case 1 -> "Tyrant";
                case 2 -> "Mystery";
                case 3 -> "Red_Priest";
                case 4 -> "Paragon";
                case 5 -> "Dev";
                default -> "None";
            };
        } else {
            return switch(Math.floorDiv(id, 10)){
                case 0 -> "wheel_of_fortune";
                case 1 -> "tyrant";
                case 2 -> "mystery";
                case 3 -> "red_priest";
                case 4 -> "paragon";
                case 5 -> "dev";
                default -> "none";
            };
        }
    }

    public static int getSequenceColorFromId(int id){
        int seq = id%10;
        int color;
        int pathway = id == -1 ? -1 : (Math.floorDiv(id, 10));
        color = switch (pathway) {
            case 0 -> WheelOfFortunePathway.getSequenceColor(seq);
            case 1 -> TyrantPathway.getSequenceColor(seq);
            case 2 -> MysteryPathway.getSequenceColor(seq);
            case 3 -> RedPriestPathway.getSequenceColor(seq);
            case 4 -> ParagonPathway.getSequenceColor(seq);
            default -> 0;
        };
        if(color == 0) color = 16742143;
        return color;
    }

    public static String getSequenceNameFromId(int id, boolean show){
        int seq = id % 10;
        return switch(Math.floorDiv(id, 10)){
            case 0 -> WheelOfFortunePathway.getSequenceName(seq, show);
            case 1 -> TyrantPathway.getSequenceName(seq, show);
            case 2 -> MysteryPathway.getSequenceName(seq, show);
            case 3 -> RedPriestPathway.getSequenceName(seq, show);
            case 4 -> ParagonPathway.getSequenceName(seq, show);
            case 5 -> DevPathway.getSequenceName(seq, show);
            default -> show ? "None" : "none";
        };
    }

    public static float[] getStatsFor(int sequence){
        return new float[]{0, 0, 0, 0, 0};
    }
}
