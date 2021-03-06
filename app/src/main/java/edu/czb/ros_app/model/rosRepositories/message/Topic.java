package edu.czb.ros_app.model.rosRepositories.message;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.model.rosRepositories.message
 * @ClassName: Topic
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/17 17:36
 * @Version: 1.0
 */
public class Topic {
    /**
     * Topic name e.g. '/map'
     */
    public String name = "";

    /**
     * Type of the topic e.g. 'nav_msgs.OccupancyGrid'
     */
    public String type = "";


    public Topic(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Topic(Topic other) {
        if (other == null) {
            return;
        }

        this.name = other.name;
        this.type = other.type;
    }


    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;

        } else if (object.getClass() != this.getClass()) {
            return false;
        }

        Topic other = (Topic) object;

        return other.name.equals(name) && other.type.equals(type);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + 31 * type.hashCode();
    }
}
