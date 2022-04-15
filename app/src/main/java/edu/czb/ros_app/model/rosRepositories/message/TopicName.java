package edu.czb.ros_app.model.rosRepositories.message;

public interface TopicName {
    String RPY="/sensor/imu/rpy";
    String BATTERY="/sensor/m_VI";
    String JOY="joy";
    String MAP="navSatFix";
    String MAP_PATH="waypoints";
    String TEMPERATURE="temperature";
    String TOPIC_KEY="topic_key";
    String IMU="/sensor/data_raw";
    String DEST_YAW="control/heading";
}
