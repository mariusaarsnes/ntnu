public class BeeVertex{
    int machineNumber,jobNumber,timeRequired;

    BeeVertex[] edges;
    
    public BeeVertex(int machineNumber, int jobNumber, int timeRequired){
        this.machineNumber = machineNumber;
        this.jobNumber = jobNumber;
        this.timeRequired = timeRequired;
    }
}