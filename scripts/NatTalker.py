import rospy
from sensor_msgs.msg import BatteryState
from sensor_msgs.msg import NavSatFix
import random
import std_msgs

def talker():
    pub = rospy.Publisher('navSatFix', NavSatFix, queue_size=10)
    rospy.init_node('MapTalker', anonymous=True)
    rate = rospy.Rate(0.5) # 10hz
    h=std_msgs.msg.Header();
    #h=sensor_msgs.msg.Header()
    navSatFix = NavSatFix()
    navSatFix.latitude=23.255354
    navSatFix.longitude=116.259489
    navSatFix.altitude=random.random()*60
    a=random.uniform(-0.0001,0.0001);
    C=6;
    while not rospy.is_shutdown():
        navSatFix.header.seq+=1
        navSatFix.header.stamp=rospy.Time.now()
        navSatFix.latitude=navSatFix.latitude+round(a,C)
        navSatFix.longitude=navSatFix.longitude+round(a,C)
        navSatFix.altitude=navSatFix.altitude+round(a,C)
        rospy.loginfo(navSatFix)
        pub.publish(navSatFix)
        rate.sleep()

if __name__ == '__main__':
    try:
        talker()
    except rospy.ROSInterruptException:
        pass


