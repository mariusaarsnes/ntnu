public class Job{

    int id;

    //requirements[operation][0] = machine
    //requirements[operation][1] = time
    int[][] requirements;
    public Job(int id, int[][] requirements) {
        this.id = id;
        this.requirements = requirements;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (int[] requirement : requirements) {
            sb.append(String.format("%7s", requirement[0] + ":" + requirement[1]));
        }

        return sb.toString();
    }
}