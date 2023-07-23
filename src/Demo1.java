import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Demo1 extends JFrame{
	
    public static int goal_x = staticBlocks.size;				//终点x坐标
    public static int goal_y = staticBlocks.size;				//终点y坐标

    static class Plot extends JPanel {

        DStarLite alg;
        int[][] block;
        List<State> path;
        int numStep;
        boolean flag = true;	//判断无人机电量是否剩余
        int shine;
        int temperature;
        int windSpeed;
        int mark;	// 表示风向的标志
        String windDirection;


        public Plot(DStarLite alg,int[][] block,List<State> path,int shine,int temperature,int windSpeed,int mark){
            this.alg = alg;
            this.block = block;
            this.path = path;
            this.shine = shine;
            this.temperature = temperature;
            this.windSpeed = windSpeed;
            this.mark = mark;
        }

        public void setNumstep(int i) {
            this.numStep = i;
        }

        public void paint(Graphics gp) {
            super.paint(gp);

            Graphics2D gp2d = (Graphics2D) gp;

            Uav test0 = new Uav(110,15,5);                                                 //新建了一个无人机类（芜湖起飞）
            
            naturalEnvironment testE0 = new naturalEnvironment(shine, temperature, windSpeed, windDirection);            //自然环境类
            
            staticBlocks testS0 = new staticBlocks(block);                                      //静态障碍物类

            testE0.createEnvironment(path,numStep,gp2d,test0,flag);                             //创建自然环境
            
            HashMap<Integer, String> mark0 = new HashMap<>();
            
            mark0.put(0, "东");
            mark0.put(1, "西");
            mark0.put(2, "南");
            mark0.put(3, "北");
            mark0.put(4, "东南");
            mark0.put(5, "东北");
            mark0.put(6, "西南");
            mark0.put(7, "西北");
            
            this.windDirection = mark0.get(mark);

            this.flag = testE0.isFlag();                                                        //用于判断无人机电量是否充足

            testS0.createEnvironment(gp2d);                                                     //创建环境

            alg.replan();

            gp2d.setColor(Color.RED);

            if(numStep<path.size()) {
                State i = path.get(numStep);                                                          //无人机每次移动一格
                // System.out.println("x: " + i.x + "," + " y: " + i.y);
                gp2d.fillOval(i.x * staticBlocks.sizeRow, i.y * staticBlocks.sizeCol, staticBlocks.sizeRow, staticBlocks.sizeCol);
            }

            gp2d.setColor(Color.BLUE);
            
            gp2d.fillOval(0,0,staticBlocks.sizeRow,staticBlocks.sizeCol);	//起点
            
            gp2d.fillOval(goal_x*staticBlocks.sizeRow,goal_y*staticBlocks.sizeCol,staticBlocks.sizeRow,staticBlocks.sizeCol);	//终点

        }

    }


    //初始化窗体

    private void initialize(){

        this.setSize(staticBlocks.size*staticBlocks.sizeRow+20, staticBlocks.size*staticBlocks.sizeCol+120);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);						//设置窗体关闭模式

        this.setTitle("芜湖起飞");

        this.setLocationRelativeTo(null);									//窗体居中

        this.setVisible(true);												//设置窗体的可见性

        DStarLite alg = new DStarLite();

 		alg.init(0,1,goal_x, goal_y);					//起点和终点

        int[][] block = new int[staticBlocks.numBlock][2];                                    //记录障碍物的坐标

        int n = staticBlocks.size + 1;

        Random shine = new Random();												//随机光照
        Random temperature = new Random();											//随机温度
        Random wind = new Random();													//随机风速
        Random mark = new Random();													//随机风向
        
        int shine0 = shine.nextInt(30)-10;
        int temperature0 = temperature.nextInt(20)+10;
        int wind0 = wind.nextInt(5);
        int mark0 = mark.nextInt(7);

        for (int i = 0; i < staticBlocks.numBlock; i++) {                                     //设置障碍物
            Random cellX = new Random();
            Random cellY = new Random();
            int cellX0 = cellX.nextInt(n - 2) + 1;                    //避免障碍物被放到起点和终点
            int cellY0 = cellY.nextInt(n - 2) + 1;
            alg.updateCell(cellX0, cellY0, -1);
            block[i][0] = cellX0;
            block[i][1] = cellY0;
        }
//----------------------------------------------------------------------------"围墙"-------防止无人机从环境外寻路
        for (int j = -1; j <= n; j++) {
            alg.updateCell(-1, j, -1);             //(-1,-1)----------(-1,n)
        }
        for (int j = -1; j <= n; j++) {
            alg.updateCell(n, j, -1);             //(n,-1)----------(n,n)
        }
        for (int j = 0; j < n; j++) {
            alg.updateCell(j, -1, -1);             //(0,-1)-----------(n-1,-1)
        }
        for (int j = 0; j < n; j++) {
            alg.updateCell(j, n, -1);             //(0,n)------------(n-1,n)
        }
//----------------------------------------------------------------------------------------

        alg.replan();

        List<State> path = alg.getPath();                                        //这里存放无人机的飞行路径

        this.setVisible(true);                                                  //设置窗体的可见性

        Plot test = new Plot(alg, block, path, shine0, temperature0, wind0, mark0);
        
        for (int i = 0; i < path.size(); i++) {
            if (test.flag) {
                test.setNumstep(i);
                this.getContentPane().add(test);
                this.setVisible(true);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.repaint();
            }
        }
        this.setVisible(true);
        this.getContentPane().add(test);
    }

    public Demo1(){

        super();
        initialize();

    }

    public static void main(String[] args){
        new Demo1();
    }

}
