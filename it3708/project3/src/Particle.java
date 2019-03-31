import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Particle {
    FileParser fp;
    ParticleElement[] local;
    Solution solution;
    ParticleElement[] localBest;
    Solution solutionBest;
    ParticleElementPositionComparator particleElementPositionComparator;
    Random random;

    int[] positionBounds;
    int[] velocityBounds;


    public Particle(FileParser fp, int[] positionBounds, int[] velocityBounds) {
        this.fp = fp;
        this.positionBounds = positionBounds;
        this.velocityBounds = velocityBounds;
        this.local = new ParticleElement[fp.jobCount * fp.machineCount];
        this.random = new Random();
        this.particleElementPositionComparator = new ParticleElementPositionComparator();

        initializeParticle();
        this.solution = decode();
        this.solutionBest = this.solution;
    }

    private void initializeParticle() {
        for (int i = 0; i < fp.jobCount; i++) {
            for (int j = 0; j < fp.machineCount; j++) {
                this.local[i * fp.machineCount + j] = new ParticleElement(i,
                        (Math.random() * (this.positionBounds[1] - this.positionBounds[0] + 1)) + this.positionBounds[0],
                        (Math.random() * (this.velocityBounds[1] - this.velocityBounds[0] + 1)) + this.velocityBounds[0]);
            }
        }
        this.localBest = this.local.clone();
    }

    public Solution decode() {
        int[] jobOperationCounter = new int[fp.jobCount];
        int[] prevOperationfinishedAt = new int[fp.jobCount];
        int[] availableAt = new int[fp.machineCount];
        int[][][] schedule = new int[fp.machineCount][fp.jobCount][2];

        ParticleElement[] sortedRepresentation = this.local.clone();
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
        return new Solution(schedule);
    }

    public void update(Particle globalBest, double omega, double c1, double c2) {
        for (int i = 0; i < this.local.length; i++) {
            this.local[i].velocity = (omega * this.local[i].velocity)
                        + (c1 * Math.random() * (this.localBest[i].position - this.local[i].position))
                        + (c2 * Math.random() * (globalBest.localBest[i].position - this.local[i].position));
            if (this.local[i].velocity < this.velocityBounds[0]) {
                this.local[i].velocity = this.velocityBounds[0];
            } else if (this.local[i].velocity > this.velocityBounds[1]) {
                this.local[i].velocity = this.velocityBounds[1];
            }

            this.local[i].position = this.local[i].position + this.local[i].velocity;
        }

        this.solution = decode();
        if (this.solution.makespan < this.solutionBest.makespan) {
            this.solutionBest = this.solution;
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
        return Double.compare(p1.solution.makespan, p2.solution.makespan);
    }
}
