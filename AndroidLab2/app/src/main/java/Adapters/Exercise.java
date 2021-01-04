package Adapters;

public class Exercise {
    public int id;
    public int color;
    public String title;
    public int prepare;
    public int work;
    public int chill;
    public int cycles;
    public int sets;
    public int setChill;
    public Exercise(){

    }
    public Exercise(int id, int color, String title, int prepare, int work, int chill, int cycles, int sets, int setChill)
    {
        this.id = id;
        this.color = color;
        this.title = title;
        this.prepare = prepare;
        this.work = work;
        this.chill = chill;
        this.cycles = cycles;
        this.sets = sets;
        this.setChill = setChill;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
