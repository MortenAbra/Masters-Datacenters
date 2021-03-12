package drenerginet;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import drenerginet.Pinger.DockerPinger;

public class ExecutionManager  {
    private FileHandler fh;
    private ExecutorService es;
    private EnergiDataFetcher edf;
    private DockerPinger dp;
    private ArrayList<Runnable> runnableList = new ArrayList<>();

    public ExecutionManager(){
        fh = new FileHandler();
        edf = new EnergiDataFetcher();
        dp = new DockerPinger();
    }

    public void addToWorklist(Runnable task){

    }



    public Future<Runnable> ExecuteJSONTask(ArrayList<Runnable> worklist){
        es = Executors.newCachedThreadPool();
        
        return (Future<Runnable>) es.submit(() -> {
            for (int i = 0; i < worklist.size(); i++) {
                worklist.get(i).run();
            }
        });
    }



    /**
     * @return the runnableList
     */
    public ArrayList<Runnable> getRunnableList() {
        return runnableList;
    }



    /**
     * @param runnableList the runnableList to set
     */
    public void setRunnableList(ArrayList<Runnable> runnableList) {
        this.runnableList = runnableList;
    }
}
