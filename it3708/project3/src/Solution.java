public class Solution {

    protected final int[][][]schedule;
    protected final int makespan;
    
    public Solution(int[][][] schedule) {
        this.schedule = schedule;

        int maxMakespan = Integer.MIN_VALUE;
        for(int i = 0; i < schedule.length; i++) {
            for(int j = 0; j < schedule[i].length; j++) {
                int value = schedule[i][j][0] + schedule[i][j][1];
                if (value > maxMakespan){
                    maxMakespan = value;
                }
            }
        }
        this.makespan = maxMakespan;
    }

    int getMakespan() {
        return this.makespan;
    }

    int[][][] getSchedule() {
        return this.schedule;
    }
}