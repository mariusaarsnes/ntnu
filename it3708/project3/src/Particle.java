import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Particle {
    FileParser fp;
    ParticleElement[] X; // current particle
    ParticleElement[] P; // Best local particle
    Solution solution;
    Solution solutionBest;
    ParticleElementPositionComparator particleElementPositionComparator;
    Random random;

    int[] positionBounds;
    int[] velocityBounds;

    public Particle(FileParser fp, int[] positionBounds, int[] velocityBounds) {
        this.fp = fp;
        this.positionBounds = positionBounds;
        this.velocityBounds = velocityBounds;
        this.random = new Random();
        this.particleElementPositionComparator = new ParticleElementPositionComparator();

        initializeParticle();
        int[][][] schedule = decode(this.X);
        this.solution = new Solution(schedule);
        this.solutionBest = new Solution(schedule);
    }

    private void initializeParticle() {
        ParticleElement[] X = new ParticleElement[this.fp.jobCount*this.fp.machineCount];
        for (int i = 0; i < fp.jobCount; i++) {
            for (int j = 0; j < fp.machineCount; j++) {
                X[i * fp.machineCount + j] = new ParticleElement(i,
                        (Math.random() * (this.positionBounds[1] - this.positionBounds[0] + 1)) + this.positionBounds[0],
                        (Math.random() * (this.velocityBounds[1] - this.velocityBounds[0] + 1)) + this.velocityBounds[0]);
            }
        }
        this.X = X;
        this.P = copy(X);
    }

    /**
     * Copys an array containing ParticleElement objects
     * @param X array of ParticleElement objects
     * @return new array containing copies of the elements in X
     */
    private ParticleElement[] copy(ParticleElement[] X) {
        ParticleElement[] copy = new ParticleElement[this.fp.jobCount*this.fp.machineCount];
        for(int i = 0; i < fp.jobCount;i++) {
            for(int j = 0; j< fp.machineCount;j++) {
                ParticleElement elem = X[i*fp.machineCount+j];
                copy[i*fp.machineCount+j] = new ParticleElement(elem.operation,elem.position,elem.velocity);
            }
        }
        return copy;
    }

    public int[][][] decode(ParticleElement[] X) {
        int[] jobOperationCounter = new int[fp.jobCount];
        int[] prevOperationfinishedAt = new int[fp.jobCount];
        int[] availableAt = new int[fp.machineCount];
        int[][][] schedule = new int[fp.machineCount][fp.jobCount][2];

        ParticleElement[] sortedRepresentation = X.clone();
        Arrays.sort(sortedRepresentation, this.particleElementPositionComparator);
        for (ParticleElement elem : sortedRepresentation) {
            int machine = this.fp.jobs[elem.operation].requirements[jobOperationCounter[elem.operation]][0];
            int requiredTime = this.fp.jobs[elem.operation].requirements[jobOperationCounter[elem.operation]][1];
            int startTime = (availableAt[machine] > prevOperationfinishedAt[elem.operation]) ? availableAt[machine] : prevOperationfinishedAt[elem.operation];
            schedule[machine][elem.operation][0] = startTime;
            schedule[machine][elem.operation][1] = requiredTime;
            availableAt[machine] = startTime +requiredTime;
            prevOperationfinishedAt[elem.operation] = startTime + requiredTime;
            jobOperationCounter[elem.operation]++;
        }
        return schedule;
    }

    public void update(Particle G, double omega, double c1, double c2) {

        for (int i = 0; i < this.X.length; i++) {
            this.X[i].velocity = (omega * this.X[i].velocity)
                        + (c1 * Math.random() * (this.P[i].position - this.X[i].position))
                        + (c2 * Math.random() * (G.P[i].position - this.X[i].position));

            if (this.X[i].velocity < this.velocityBounds[0]) {
                this.X[i].velocity = this.velocityBounds[0];
            } else if (this.X[i].velocity > this.velocityBounds[1]) {
                this.X[i].velocity = this.velocityBounds[1];
            }

            this.X[i].position = this.X[i].position + this.X[i].velocity;
        }

        int[][][] schedule = decode(this.X);
        this.solution = new Solution(schedule);
        if (this.solution.makespan < this.solutionBest.makespan) {
            this.solutionBest = new Solution(schedule);
            this.P = copy(this.X);
        }
    }

    public class ParticleElementPositionComparator implements Comparator<ParticleElement> {

        @Override
        public int compare(ParticleElement pe1, ParticleElement pe2) {
            return Double.compare(pe1.position, pe2.position);
        }
    }
}

class ParticleMakespanComparator implements Comparator<Particle> {

    @Override
    public int compare(Particle p1, Particle p2) {
        return Double.compare(p1.solutionBest.makespan, p2.solutionBest.makespan);
    }
}
