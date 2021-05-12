package mastercontroller;

import java.util.ArrayList;
import java.util.List;

import mastercontroller.Models.Workload;

public class WorkloadManager implements Subject {

    private List<Observer> observerList;
    private ArrayList<Workload> workloadList;
    private Workload workload;

    public WorkloadManager(){
        this.observerList = new ArrayList<>();
        this.workloadList = new ArrayList<>();
    }

    public ArrayList<Workload> getWorkloadList(){
        return this.workloadList;
    }

    @Override
    public void registerObserver(Observer observer) {
        // TODO Auto-generated method stub
        System.out.println("Adding observer!");
        observerList.add(observer);

    }

    @Override
    public void removeObserver(Observer observer) {
        // TODO Auto-generated method stub
        int index = observerList.indexOf(observer);
        if(index >= 0){
            observerList.remove(index);
            System.out.println("Observer at index: " + index +  ", have been removed!");
        }
    }

    @Override
    public void notifyObservers() {
        // TODO Auto-generated method stub
        System.out.println("Beginning notification of observers!");
        observerList.forEach(o -> o.update(workload));
        System.out.println("Observers notified!");
    }

    public void workloadAddedToList(Workload workload){
        this.workload = workload;
        workloadList.add(workload);
        System.out.println("Workload added to list!");
        notifyObservers();
    }
    
}
