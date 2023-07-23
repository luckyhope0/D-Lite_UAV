public class Uav {
    int maxPower;                             //最大电量---限制无人机的最大路程（会受环境光照影响）
    double flyingSpeed = 10;                  //无人机实时飞行速度（初始为10）
    double maxFlyingSpeed;                    //最大飞行速度
    double minFlyingSpeed;                    //最小飞行速度
    
    public Uav(int maxPower,double maxFlyingSpeed,double minFlyingSpeed){
        this.maxPower = maxPower;
        this.maxFlyingSpeed = maxFlyingSpeed;
        this.minFlyingSpeed = minFlyingSpeed;
    }
}
