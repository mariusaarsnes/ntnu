import java.util.Arrays;

public class PSO {

    FileParser fp;
    Particle[] particles;
    Particle globalBest;
    int[] positionBounds, velocityBounds;

    Particle[] population;
    ParticleMakespanComparator particleMakespanComparator = new ParticleMakespanComparator();

    int populationSize, generations;
    double omega,omegaThreshold, omegaStep, c1, c2;
    public PSO(FileParser fp, int populationSize,int generations,double omega,double omegaThreshold, double c1, double c2, int[] positionBounds, int[] velocityBounds) {
        this.fp = fp;
        this.particles = new Particle[fp.jobCount];
        this.populationSize = populationSize;
        this.generations = generations;
        this.omega = omega;
        this.omegaThreshold = omegaThreshold;
        this.c1 = c1;
        this.c2 = c2;
        this.positionBounds = positionBounds;
        this.velocityBounds = velocityBounds;

        this.omegaStep = (this.omega - this.omegaThreshold) / this.generations;

    }




    public void run() {
        population = initializeParticles();
        this.globalBest = population[0];
        evaluate(population);
        for(int i = 0; i< this.generations; i++) {
            this.omega -= this.omegaStep;
            updateParticles(population);
            evaluate(population);
        }
    }



    private Particle[] initializeParticles() {
        Particle[] particles = new Particle[this.populationSize];
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle(this.fp,this.positionBounds, this.velocityBounds);
        }
        return particles;
    }

    /**
     * Find the global best solution by iterating through the whole population of particles,
     * and selecting the one with the lowest makespan
     * @param population
     */
    private void evaluate(Particle[] population) {
        Particle globalBest = population[0];
        for (Particle contender : population) {
            if (contender.solution.makespan < globalBest.solution.makespan) {
                globalBest = contender;
            }
        }
        if (this.globalBest.solution.makespan > globalBest.solution.makespan) {
            this.globalBest = globalBest;
        }
    }

    private void updateParticles(Particle[] particles) {
        for(int i = 0; i < particles.length; i++) {
            particles[i].update(this.globalBest,this.omega, this.c1, this.c2);
        }
    }
}