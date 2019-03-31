public class Solution {

    // [machineNumber][jobNumber][2]
    // [X][Y][0] = Start Time
    // [X][Y][1] = Time Required

    protected final int[][][] schedule;
    protected final int makespan;

    public Solution(int[][][] schedule) {
        this.schedule = schedule;

        int maxMakespan = Integer.MIN_VALUE;
        for (int i = 0; i < schedule.length; i++) {
            for (int j = 0; j < schedule[i].length; j++) {
                int value = schedule[i][j][0] + schedule[i][j][1];
                if (value > maxMakespan) {
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

    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (int m = 0; m < this.schedule.length; m++) {
            StringBuilder tempSb = new StringBuilder();
            tempSb.append("m" + m + "\t");
            for (int j = 0; j < this.schedule[m].length; j++) {
                tempSb.append("j" + j + "\t" + " [" + this.schedule[m][j][0] + "," + this.schedule[m][j][1] + "]\t");
            }
            sb.append(tempSb + "\n");

        }
        return sb.toString();

    }
}
