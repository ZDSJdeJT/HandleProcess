import java.util.LinkedList;
import java.util.Scanner;

public class HandleProcess {
    private Integer cpuTime = 0;//初始cpu运行时间
    private LinkedList<PCB> readyList = new LinkedList<>();//就绪队列
    private LinkedList<PCB> blockedList = new LinkedList<>();//阻塞队列
    private LinkedList<PCB> runningList = new LinkedList<>();//执行队列

    public Integer getCpuTime() {
        return cpuTime;
    }

    //创建原语√
    public boolean create() {
        Scanner input = new Scanner(System.in);
        System.out.println("输入进程名(一个字符串)【请勿输入相同的进程名】：");
        String name = input.nextLine();
        if (!haveSameName(name)) {
            System.out.println("输入的进程名已存在");
            return false;
        }
        System.out.println("优先级(一个0-9的整数)：");
        int priority = 0;
        int a = 0;
        while (a == 0) {
            try {
                priority = Integer.parseInt(input.nextLine().trim());
                while (priority > 9 || priority < 0) {
                    System.out.println("输入不合法，请重新输入优先级(一个0-9的整数)：");
                    priority = Integer.parseInt(input.nextLine().trim());
                }
                a = 1;
            } catch (Exception e) {
                System.out.println("输入不合法，请重新输入优先级(一个0-9的整数)：");
            }
        }
        System.out.println("所需时间(一个大于0的整数)：");
        int needTime = 0;
        while (a == 1) {
            try {
                needTime = Integer.parseInt(input.nextLine().trim());
                while (needTime <= 0) {
                    System.out.println("输入不合法，请重新输入所需时间(一个大于0的整数)：");
                    needTime = Integer.parseInt(input.nextLine().trim());
                }
                a = 2;
            } catch (Exception e) {
                System.out.println("输入不合法，请重新输入所需时间(一个大于0的整数)：");
            }
        }
        readyList.offer(new PCB(name, priority, needTime));//向就绪队列添加元素
        return dispatch();
    }

    //处理器调度√
    public boolean dispatch() {
        if (runningList.isEmpty()) {
            if (!readyList.isEmpty()) {
                int i = getMinIndex(readyList);
                runningList.offer(new PCB(readyList.get(i).getName(), readyList.get(i).getPriority(), readyList.get(i).getNeedTime()));
                readyList.remove(i);
                System.out.println("进程" + runningList.peek().getName() + "送往CPU执行");
                return true;
            } else {
                System.out.println("就绪队列为空，无法调度");
                return false;
            }
        } else {
            System.out.println("CPU忙，无法调度");
            return false;
        }
    }

    //获取优先级数最小的进程的索引值√
    public static int getMinIndex(LinkedList<PCB> list) {
        int index = 0;
        int first = list.get(0).getPriority();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPriority() < first) {
                index = i;
                first = list.get(i).getPriority();
            }
        }
        return index;
    }

    //通过进程名查询索引值√
    public static int getNameIndex(LinkedList<PCB> list, String name) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals(name)) {
                index = i;
            }
        }
        return index;
    }

    //查询是否存在相同名称的进程√
    public boolean haveSameName(String name) {
        int runningIndex = -1;
        int readyIndex = -1;
        int blockedIndex = -1;
        if (runningList.size() != 0) {
            for (int i = 0; i < runningList.size(); i++) {
                if (runningList.get(i).getName().equals(name)) {
                    runningIndex = i;
                }
            }
        }
        if (readyList.size() != 0) {
            for (int i = 0; i < readyList.size(); i++) {
                if (readyList.get(i).getName().equals(name)) {
                    readyIndex = i;
                }
            }
        }
        if (blockedList.size() != 0) {
            for (int i = 0; i < blockedList.size(); i++) {
                if (blockedList.get(i).getName().equals(name)) {
                    blockedIndex = i;
                }
            }
        }
        if (runningIndex < 0 && readyIndex < 0 && blockedIndex < 0) {
            return true;
        } else {
            return false;
        }
    }

    //时间片推移√
    public boolean timeout() {
        if (!runningList.isEmpty()) {
            cpuTime++;
            runningList.peek().setNeedTime(runningList.peek().getNeedTime() - 1);
            if (runningList.peek().getNeedTime() == 0) {
                System.out.println("进程" + runningList.poll().getName() + "时间片用完，并且执行完毕，已被释放");
            }
        } else {
            System.out.println("当前没有进程在CPU中执行");
        }
        return dispatch();
    }

    //阻塞原语√
    public boolean eventWait() {
        if (!runningList.isEmpty()) {
            PCB cpu = runningList.poll();
            blockedList.offer(new PCB(cpu.getName(), cpu.getPriority(), cpu.getNeedTime()));
            System.out.println("进程" + cpu.getName() + "被阻塞");
            return dispatch();
        } else {
            System.out.println("当前没有进程在CPU中执行，无法阻塞");
            return false;
        }
    }

    //唤醒原语√
    public boolean eventOccur() {
        if (blockedList.size() != 0) {
            Scanner eoName = new Scanner(System.in);
            System.out.println("输入要唤醒的进程名(一个字符串)：");
            String PCBName = eoName.nextLine();
            if (getNameIndex(blockedList, PCBName) >= 0) {
                PCB blockedToReady = blockedList.get(getNameIndex(blockedList, PCBName));
                readyList.offer(new PCB(blockedToReady.getName(), blockedToReady.getPriority(), blockedToReady.getNeedTime()));
                blockedList.remove(getNameIndex(blockedList, PCBName));
                return dispatch();
            } else {
                System.out.println("要唤醒的阻塞进程不存在");
                return false;
            }
        } else {
            System.out.println("阻塞队列为空，无法执行唤醒原语");
            return false;
        }
    }

    //撤销原语√
    public boolean destroy() {
        if (!runningList.isEmpty()) {
            System.out.println("进程" + runningList.poll().getName() + "已被撤销");
            return dispatch();
        } else {
            System.out.println("当前没有进程在CPU中执行，无法撤销");
            return false;
        }
    }



    public static void main(String[] args) {
        System.out.println("进程状态及其转换（单处理器）模拟程序");
        HandleProcess handleProcess = new HandleProcess();
        int select = 0;
        while (true) {
            System.out.println("10001：创建原语\t\t10002：时间片推移");
            System.out.println("10003：阻塞原语\t\t10004：唤醒原语");
            System.out.println("10005：撤销原语\t\t10000：退出");
            System.out.println("输入数字以实现相应的功能：");
            Scanner input = new Scanner(System.in);
            int a = 0;
            while (a == 0) {
                try {
                    select = Integer.parseInt(input.nextLine().trim());
                    while (select < 10000 || select > 10005) {
                        System.out.println("输入不合法，请重新输入数字以实现相应的功能：");
                        select = Integer.parseInt(input.nextLine().trim());
                    }
                    a = 1;
                } catch (Exception e) {
                    System.out.println("输入不合法，请重新输入数字以实现相应的功能：");
                }
            }
            switch (select) {
                case 10001:
                    handleProcess.create();
                    break;
                case 10002:
                    handleProcess.timeout();
                    break;
                case 10003:
                    handleProcess.eventWait();
                    break;
                case 10004:
                    handleProcess.eventOccur();
                    break;
                case 10005:
                    handleProcess.destroy();
                    break;
                case 10000:
                    System.exit(0);//正常退出程序
            }
            System.out.println("**************************** CPU执行时间：" + handleProcess.getCpuTime() + " ***************************");
            System.out.println("状态\t\t进程名\t\t需要时间\t\t优先级");
            if (!handleProcess.runningList.isEmpty()) {
                System.out.print("执行状态：" + "\t" + handleProcess.runningList.peek().getName() + "\t\t\t");
                System.out.print(handleProcess.runningList.peek().getNeedTime() + "\t\t\t");
                System.out.println(handleProcess.runningList.peek().getPriority());
            }
            if (handleProcess.readyList.size() != 0) {
                for (int i = 0; i < handleProcess.readyList.size(); i++) {
                    System.out.print("就绪状态：" + "\t" + handleProcess.readyList.get(i).getName() + "\t\t\t");
                    System.out.print(handleProcess.readyList.get(i).getNeedTime() + "\t\t\t");
                    System.out.println(handleProcess.readyList.get(i).getPriority());
                }
            }
            if (handleProcess.blockedList.size() != 0) {
                for (int i = 0; i < handleProcess.blockedList.size(); i++) {
                    System.out.print("阻塞状态：" + "\t" + handleProcess.blockedList.get(i).getName() + "\t\t\t");
                    System.out.print(handleProcess.blockedList.get(i).getNeedTime() + "\t\t\t");
                    System.out.println(handleProcess.blockedList.get(i).getPriority());
                }
            }
            System.out.println("**********************************************************************");
        }
    }
}
