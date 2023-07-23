import java.awt.*;
import java.util.HashMap;
import java.util.List;

interface Environment{
    void createEnvironment();
}

class naturalEnvironment implements Environment {
    int windSpeed;                                                      //设定风力分为0~5级
    String windDirection;												//设定风向，分别为东、南、西、北、东北、东南、西北、西南
    int temperature;                                                    //设定温度一项会对无人机的电量使用率造成影响（越接近最适温度效率越高，反之越低）
    int shine;                                                          //设定光照一项会对无人机电量造成影响（太阳能发电~）
    boolean flag;                                                       //判断无人机电量是否剩余
    int k;                                                              //温度/无人机电量=常数

    public naturalEnvironment(int shine,int temperature,int windSpeed,String windDirection){
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.temperature = temperature;
        this.shine = shine;
    }



    public void createEnvironment(List<State> path,int numStep, Graphics2D gp2d, Uav nva,boolean flag){

        this.flag = flag;

        if(this.shine<=0)this.shine = 0;                                   //接收光照提供太阳能效率有限
        else if(this.shine>0&&this.shine<10)this.shine = 20;
        else if(this.shine>=10) this.shine = 40;


        if(this.temperature>=10&&this.temperature<20){                         //温度影响电池效率
            this.k = 8;
        }
        else if(this.temperature>=20&&this.temperature<30){
            this.k = 10;
        }
        else if(this.temperature>=30&&this.temperature<=40){
            this.k = 9;
        }
        

        switch (this.shine){                                                  // 根据环境光照设置不同背景色
            case 0:
                gp2d.setBackground(Color.GRAY);
                gp2d.clearRect(0, 0, staticBlocks.size*staticBlocks.sizeRow+50, staticBlocks.size*staticBlocks.sizeCol+150);
                gp2d.setColor(Color.BLACK);
                gp2d.drawString("环境光照：昏暗",0,staticBlocks.size*staticBlocks.sizeCol+80);
                break;
            case 20:
                gp2d.setBackground(Color.LIGHT_GRAY);
                gp2d.clearRect(0, 0, staticBlocks.size*staticBlocks.sizeRow+50, staticBlocks.size*staticBlocks.sizeCol+150);
                gp2d.setColor(Color.BLACK);
                gp2d.drawString("环境光照：正常",0,staticBlocks.size*staticBlocks.sizeCol+80);
                break;
            case 40:
                gp2d.setBackground(Color.WHITE);
                gp2d.clearRect(0, 0, staticBlocks.size*staticBlocks.sizeRow+50, staticBlocks.size*staticBlocks.sizeCol+150);
                gp2d.setColor(Color.BLACK);
                gp2d.drawString("环境光照：明亮",0,staticBlocks.size*staticBlocks.sizeCol+80);
                break;
        }

        gp2d.setColor(Color.BLACK);

        gp2d.drawLine(0,staticBlocks.size*staticBlocks.sizeCol+6,staticBlocks.size*staticBlocks.sizeRow+20,staticBlocks.size*staticBlocks.sizeCol+6);
        
        double powerLeft = ((nva.maxPower+this.shine)*this.k-numStep * 9)/((nva.maxPower+this.shine)*this.k*1.0);     //剩余电量

        if(powerLeft>=0){                                                               //判断无人机能否到达终点
            gp2d.setColor(Color.BLACK);
            gp2d.drawString("电量情况：电量充足,无人机正常运行",0,staticBlocks.size*staticBlocks.sizeCol+40);
            powerLeft = (double)Math.round(powerLeft*100)/100;
            String power  = "";
            power = power+powerLeft*100;
            gp2d.drawString("电量剩余："+power+"%",0,staticBlocks.size*staticBlocks.sizeCol+20);
        }
        else{
            this.flag = false;
            gp2d.setColor(Color.RED);
            gp2d.drawString("电量情况：警告!无人机电量不足!",0,staticBlocks.size*staticBlocks.sizeCol+40);
            gp2d.drawString("电量剩余：电量不足！",0,staticBlocks.size*staticBlocks.sizeCol+20);
        }
        
        switch (this.k){                                                  // 显示环境温度情况
        case 8:
            gp2d.setColor(Color.cyan);
            gp2d.drawString("环境温度："+this.temperature+"°C(低温)",0,staticBlocks.size*staticBlocks.sizeCol+60);
            break;
        case 10:
            gp2d.setColor(Color.BLACK);
            gp2d.drawString("环境温度："+this.temperature+"°C(适宜)",0,staticBlocks.size*staticBlocks.sizeCol+60);
            break;
        case 9:
            gp2d.setColor(Color.ORANGE);
            gp2d.drawString("环境温度："+this.temperature+"°C(高温)",0,staticBlocks.size*staticBlocks.sizeCol+60);
            break;
    }

        gp2d.setColor(Color.BLACK);

        double lambda = getLambda(windDirection, path, numStep);			//lambda为衡量风向与运动方向夹角的因子
        
        nva.flyingSpeed += lambda*windSpeed*0.1;

        gp2d.drawString("(初始速度为10.0step/s)", (staticBlocks.size*staticBlocks.sizeRow+10)/2+130, staticBlocks.size*staticBlocks.sizeCol+20);

        if(numStep!=path.size()-1&&powerLeft>0) {
        	int x = (staticBlocks.size*staticBlocks.sizeRow+10)/2, y = staticBlocks.size*staticBlocks.sizeCol+20;
        	if(nva.flyingSpeed<=nva.maxFlyingSpeed&&nva.flyingSpeed>=nva.minFlyingSpeed)
        		gp2d.drawString("当前速度：" + nva.flyingSpeed + "step/s", x, y);
        	else if(nva.flyingSpeed>nva.maxFlyingSpeed)
        		gp2d.drawString("当前速度：" + nva.maxFlyingSpeed + "step/s", x, y);
        	else if(nva.flyingSpeed<nva.minFlyingSpeed)
        		gp2d.drawString("当前速度：" + nva.minFlyingSpeed + "step/s", x, y);
        }
        else {
            gp2d.drawString("当前速度：0 ", (staticBlocks.size*staticBlocks.sizeRow+10)/2, staticBlocks.size*staticBlocks.sizeCol+20);
        }       
        
        gp2d.setColor(Color.BLACK);
        
        gp2d.drawString("风力等级：" + windSpeed, (staticBlocks.size*staticBlocks.sizeRow+10)/2, staticBlocks.size*staticBlocks.sizeCol+40);
        
        if(windSpeed!=0)
        	gp2d.drawString("风向：" + windDirection + "风", (staticBlocks.size*staticBlocks.sizeRow+10)/2, staticBlocks.size*staticBlocks.sizeCol+60);
        else
            gp2d.drawString("风向：无风", (staticBlocks.size*staticBlocks.sizeRow+10)/2, staticBlocks.size*staticBlocks.sizeCol+60);
    
        if(numStep<path.size()-1&&powerLeft>=0){
            gp2d.setColor(Color.blue);
            gp2d.drawString("无人机运行中......",(staticBlocks.size*staticBlocks.sizeRow+10)/2,staticBlocks.size*staticBlocks.sizeCol+80);
        }
        else if(numStep==path.size()-1&&powerLeft>=0){
            gp2d.setColor(Color.GREEN);
            gp2d.drawString("无人机成功抵达终点！",(staticBlocks.size*staticBlocks.sizeRow+10)/2,staticBlocks.size*staticBlocks.sizeCol+80);
        }
        else if(powerLeft<0)
        {
            gp2d.setColor(Color.RED);
            gp2d.drawString("无人机未能抵达终点！",(staticBlocks.size*staticBlocks.sizeRow+10)/2,staticBlocks.size*staticBlocks.sizeCol+80);
        }

    }

    public boolean isFlag(){
        return flag;
    }

    public double getLambda(String direction, List<State> path, int numStep) {
        double lambda;
        int[] flyingDirection = new int[2];
        int[] windDirection = new int[2];
        if (numStep == 0) {
            return 0;
        } else {
            State i = path.get(numStep - 1);
            State j = path.get(numStep);
            flyingDirection[0] = j.x - i.x;
            flyingDirection[1] = j.y - i.y;

            HashMap<String, int[]> direction0 = new HashMap<>();

            direction0.put("北", new int[]{0, 1});
            direction0.put("南", new int[]{0, -1});
            direction0.put("东", new int[]{-1, 0});
            direction0.put("西", new int[]{1, 0});
            direction0.put("东北", new int[]{-1, -1});
            direction0.put("东南", new int[]{-1, -1});
            direction0.put("西北", new int[]{1, 1});
            direction0.put("西南", new int[]{1, -1});

            windDirection = direction0.get(direction);

            lambda = flyingDirection[0] * windDirection[0] + flyingDirection[1] * windDirection[1];

            return lambda;
        }
    }

    public void createEnvironment() {
    }
}

class staticBlocks implements Environment {
	int[][] position;											//障碍物坐标
	public static int sizeRow = 6;								//障碍物x坐标像素值
	public static int sizeCol = 6;								//障碍物y坐标像素值
	public static int size = 90;								//地图范围0-edge
	public static int numBlock = 3000;							//障碍物个数

    public staticBlocks(int[][] block) {
        this.position = block;
    }

    public void createEnvironment(Graphics2D gp2d){
        gp2d.setColor(Color.BLACK);                                                               //画障碍物
        for (int i = 0;i<numBlock;i++){
            gp2d.fillRect(position[i][0]*sizeRow, position[i][1]*sizeCol, sizeRow, sizeCol);
        }
    }

    public void createEnvironment() {
    }
}
