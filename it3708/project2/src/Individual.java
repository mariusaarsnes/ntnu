import java.util.Comparator;

public class Individual {


    ImageParser imageParser;
    PixelMatrix pixelMatrix;
    Direction[] genotype;
    double score, edgeValue, overallDeviation, crowdingDistance;


    public Individual(ImageParser image, PixelMatrix pixelMatrix) {
        this.imageParser = image;
        this.pixelMatrix = pixelMatrix;
        this.genotype = createGenotype();
    }


    private Direction[] createGenotype() {
        Direction[] genotype = new Direction[this.imageParser.getNumPixels()];

        // SECTION
        // step 1: Select random Pixel,

    }


}


class CrowdingDistanceComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual o1, Individual o2) {
        return Double.compare(o2.crowdingDistance, o1.crowdingDistance);
    }
}

class EdgeValueComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual o1, Individual o2) {
        return Double.compare(o2.edgeValue, o1.edgeValue);
    }
}

class OverallDeviationComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual o1, Individual o2) {
        return Double.compare(o1.overallDeviation, o2.overallDeviation);
    }
}
