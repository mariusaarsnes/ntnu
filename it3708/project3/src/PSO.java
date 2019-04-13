import java.util.Arrays;

public class PSO {

    FileParser fp;
    int[] bestMakespanPerGeneration;
    Particle[] particles;
    Particle globalBest;
    int[] positionBounds, velocityBounds;

    Particle[] population;
    ParticleMakespanComparator particleMakespanComparator = new ParticleMakespanComparator();

    int populationSize, generations;
    double omegaMax,omegaMin,omega, c1, c2;
    public PSO(FileParser fp, int populationSize,int generations,double omegaMax,double omegaMin, double c1, double c2, int[] positionBounds, int[] velocityBounds) {
        this.fp = fp;
        this.particles = new Particle[fp.jobCount];
        this.populationSize = populationSize;
        this.generations = generations;
        this.omegaMax = omegaMax;
        this.omegaMin = omegaMin;
        this.c1 = c1;
        this.c2 = c2;
        this.positionBounds = positionBounds;
        this.velocityBounds = velocityBounds;

        this.bestMakespanPerGeneration = new int[generations];
        this.particleMakespanComparator = new ParticleMakespanComparator();
    }



    public void run() {
        population = initializeParticles();
        this.globalBest = population[0];
        evaluate(population);
        for(int i = 0; i< this.generations; i++) {
            this.bestMakespanPerGeneration[i] = population[0].solution.makespan;
            this.omega = this.omegaMax- ((this.omegaMax-this.omegaMin)/this.generations)*i;
            updateParticles(population);
            evaluate(population);
        }
    }


    private Particle[] initializeParticles() {
        Particle[] particles = new Particle[this.populationSize];
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle(this.fp,this.positionBounds, this.velocityBounds);
        }
        Arrays.sort(particles,this.particleMakespanComparator);
        return particles;
    }

    /**
     * Find the global best solution by iterating through the whole population of particles,
     * and selecting the one with the lowest makespan
     * @param population
     */
    private void evaluate(Particle[] population) {
        Arrays.sort(population,this.particleMakespanComparator);
        if (this.globalBest.solutionBest.makespan > population[0].solutionBest.makespan) {
            this.globalBest = population[0];
        }
    }

    private void updateParticles(Particle[] particles) {
        for(int i = 0; i < particles.length; i++) {
            particles[i].update(this.globalBest,this.omega, this.c1, this.c2);
        }
    }
}