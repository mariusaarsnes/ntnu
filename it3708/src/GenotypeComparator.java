import java.util.Comparator;

public class GenotypeComparator implements Comparator<Genotype> {

    @Override
    public int compare(Genotype g1, Genotype g2) {
        return g1.compareTo(g2);
    }
}