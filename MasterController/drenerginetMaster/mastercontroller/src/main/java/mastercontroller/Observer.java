package mastercontroller;

import mastercontroller.Models.Workload;

public interface Observer {
    public void update(Workload workload);
}
