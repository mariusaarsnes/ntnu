import javafx.application.Application;
import javafx.stage.Stage;

import java.util.HashMap;

public class Main extends Application {


    private Stage stage;
    private final static HashMap<String, Integer> MAKESPAN_VALUES = new HashMap<>();
    static {
        MAKESPAN_VALUES.put("1", 56);
        MAKESPAN_VALUES.put("2", 1059);
        MAKESPAN_VALUES.put("3", 1276);
        MAKESPAN_VALUES.put("4", 1130);
        MAKESPAN_VALUES.put("5", 1451);
        MAKESPAN_VALUES.put("6", 979);}

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        GUI gui = new GUI(stage);
        String fileName = "6";
        boolean runPso = false;
        FileParser fp = new FileParser(fileName);

        for (Job job : fp.jobs) {
            System.out.println(job);
        }

        if (runPso) {
            PSO pso = new PSO(fp, 200, 2000, 0.9, 0.2, 5, 5, new int[]{0, 2}, new int[]{-2, 2});
            pso.run();
            System.out.println(pso.globalBest.solution);
            gui.drawGantt(pso.globalBest.solutionBest, this.MAKESPAN_VALUES.get(fileName));
        } else {
            BA ba = new BA(fp,200,1000,50,10,MAKESPAN_VALUES.get(fileName),true);
            ba.run();
            System.out.println(ba.bestGlobalBeeSolution);
            gui.drawGantt(ba.bestGlobalBeeSolution,this.MAKESPAN_VALUES.get(fileName));
        }


    }
}