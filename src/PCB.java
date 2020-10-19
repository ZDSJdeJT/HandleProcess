public class PCB {//Processing Control Block：进程控制块
    //属性
    private Integer priority,needTime;//优先级，执行所需时间

    private String name;//名称

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getNeedTime() {
        return needTime;
    }

    public void setNeedTime(int needTime) {
        this.needTime = needTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //构造方法
    PCB(String name, int priority, int needTime){
        this.name = name;
        this.priority = priority;
        this.needTime = needTime;
    }
}
