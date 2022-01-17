package edu.czb.ros_app.model.rosRepositories.node;

public interface NodeMainExecutorServiceListener {
    /**
     * @param nodeMainExecutorService the {@link NodeMainExecutorService} that was shut down
     */
    void onShutdown(NodeMainExecutorService nodeMainExecutorService);
}
