import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;

import java.util.ArrayList;

public class Plotter {
    JavaPlot p = new JavaPlot(true);

    public Plotter(){

    }

    public void plotFront(ArrayList<Individual> front) {
        PointDataSet<Double> d = new PointDataSet<>();
        p.setTitle("Pareto front");
        p.set("xlabel", "'Deviation' offset -2,0,0");
        p.set("ylabel", "'Connectivity' offset -2,0,0");
        p.set("zlabel", "'Edge value' offset -2,0,0");
        for (Individual individual : front) {
            d.add(new Point<>(individual.overallDeviation,individual.connectivityMeasure,individual.edgeValue));
        }
        p.addPlot(d);
        p.plot();
    }

}
