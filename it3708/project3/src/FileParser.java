import java.io.*;

public class FileParser {

    int jobCount, machineCount;
    Job[] jobs;


    public FileParser(String fileName) {
        try {
            final BufferedReader br = new BufferedReader(new FileReader(new File("./test_data/"+fileName + ".txt")));
            String line = br.readLine().trim();
            String[] split = line.split("\\s+");
            this.jobCount = Integer.parseInt(split[0]);
            this.machineCount = Integer.parseInt(split[1]);
            this.jobs = new Job[jobCount];
            for (int i= 0; i < this.jobCount;i++){
                line = br.readLine().trim();
                split = line.split("\\s+");

                final int[][] requirements = new int[this.machineCount][2];
                for (int j = 0; j < this.machineCount; j++) {
                    final int index = j*2;
                    requirements[j][0] = Integer.parseInt(split[index]);
                    requirements[j][1] = Integer.parseInt(split[index+1]);
                }
                this.jobs[i] = new Job(i,requirements);
            } 

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}