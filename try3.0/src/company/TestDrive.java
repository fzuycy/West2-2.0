package company;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;

abstract class Drink//饮料类(抽象类)
{
    protected String DrinkName;//饮料名
    protected double cost;//成本
    protected LocalDate PDate;//生产日期
    protected int EDay;//保质期
    Drink() {

    }
    Drink(String name,double money,LocalDate date,int day){
        DrinkName=name;
        cost=money;
        PDate=date;
        EDay=day;
    }
    boolean isOverdue(){//判断饮料是否过期
        LocalDate temp=LocalDate.now();
        //对象名.toEpochDay()方法返回 该对象储存的日期至java中规定的过去某一天(固定)所相隔的天数,可利用该方法计算两个日期相隔的天数
        if(temp.toEpochDay()-PDate.toEpochDay()>EDay) return true;
        else return false;
    }
    abstract public String toString();

    public String getDrinkName() {
        return DrinkName;
    }

    public double getCost() {
        return cost;
    }

    public LocalDate getPDate() {
        return PDate;
    }

    public int getEDay() {
        return EDay;
    }
}
class Beer extends Drink{//啤酒类
    private float degree;//酒精度数
    Beer(String name,double money,LocalDate date,int day,float Degree)
    {
        super(name,money,date,day);//直接使用super调用父类的构造函数(同时附上相应参数)
        degree=Degree;
    }
    Beer(){

    }
    @Override
    public String toString() {
        return "Beer{" +
                "DrinkName= " + DrinkName +
                ", cost=" + cost +
                ", PDate=" + PDate +
                ", EDay=" + EDay +
                ", degree=" + degree +
                '}';
    }

    public float getDegree() {
        return degree;
    }
}
class Juice extends Drink{//果汁类
    Juice(){

    }
    Juice(String name,double money,LocalDate date,int day){
        super(name,money,date,day);
    }
    @Override
    public String toString() {
        return "Juice{" +
                "DrinkName= " + DrinkName +
                ", cost=" + cost +
                ", PDate=" + PDate +
                ", EDay=" + EDay +
                '}';
    }
}
class SetMeal{//套餐类
    private String MealName;//套餐名
    private String ChickenName;//炸鸡名
    private double Price;//套餐价格
    Drink MyDrink;//饮料(成员对象)
    SetMeal(){

    }
    public SetMeal(String mealName, String chickenName, double price, Drink myDrink) {
        MealName = mealName;
        ChickenName = chickenName;
        Price = price;
        MyDrink = myDrink;
    }
    @Override
    public String toString() {
        return "SetMeal{" +
                "MealName='" + MealName +
                ", ChickenName='" + ChickenName +
                ", Price=" + Price +
                ", MyDrink=" + MyDrink +
                '}';
    }
    public String getMealName() {
        return MealName;
    }

    public String getChickenName() {
        return ChickenName;
    }

    public double getPrice() {
        return Price;
    }

    public Drink getMyDrink() {
        return MyDrink;
    }
}
interface FriedChickenRestaurant{//炸鸡店接口
   void SaleMeal(String MyMeal);//出售套餐的方法
   void Stock(ArrayList<Drink> L);//批量进货的方法
}
class IngredientSortOutException extends RuntimeException{//用于果汁或啤酒售完时抛的异常
    IngredientSortOutException(){

    }
    IngredientSortOutException(String msg){
        super(msg);
    }//利用super()方法调用父类构造函数，将msg传进父类，方便输出异常提示
}
class OverdraftBalanceException extends  RuntimeException{//用于进货费用超过拥有余额时抛出的异常

    OverdraftBalanceException(){

    }
    OverdraftBalanceException(String msg){
        super(msg);
    }
}
class West2FriedChickenRestaurant implements FriedChickenRestaurant {//西二炸鸡店类
    private double balance;//餐厅账目余额
    //果汁和啤酒列表很可能需要进行多次的添加和删除操作，因此我选用LinkedList，进行插入删除速度较快
    //由于套餐列表不需要进行添加和删除，因此我将套餐列表声明为static，并选用ArrayList
    private LinkedList<Beer> BeerList=new LinkedList<>();//啤酒列表
    private LinkedList<Juice> JuiceList=new LinkedList<>();//果汁列表
    private static ArrayList<SetMeal> Menu=new ArrayList<>();//套餐列表
    static {//初始化套餐列表
        Menu.add(new SetMeal("Meal_one","FriedChicken_1",100,new Beer("百威",80,LocalDate.now(),100,10f)));
        Menu.add(new SetMeal("Meal_two","FriedChicken_1",70,new Beer("雪花",40,LocalDate.now(),90,5f)));
        Menu.add(new SetMeal("Meal_three","FriedChicken_2",50,new Juice("橙汁",20,LocalDate.now(),2)));
        Menu.add(new SetMeal("Meal_four","FriedChicken_2",45,new Juice("苹果汁",15,LocalDate.now(),1)));
    }
    public West2FriedChickenRestaurant() {

    }

    public West2FriedChickenRestaurant(double balance) {
        this.balance = balance;
    }


    public double getBalance(){
        return balance;
    }

    private void use(Beer MyBeer) throws IngredientSortOutException{//售卖套餐，同时移除过期和已售卖的饮料
        boolean flag=false;//flag用于标记，若for循环后flag仍为false，则说明顾客想要的饮料已经售完了，抛出异常
        for(int i=0;i<BeerList.size();i++)
        {
            if(BeerList.get(i).isOverdue()) {//如果饮料过期，则移除该饮料
                BeerList.remove(i);
                i--;//同时要记得将i--
            }
            else if(BeerList.get(i).getDrinkName()==MyBeer.getDrinkName()){//如果找到所要的饮料
                BeerList.remove(i);
                flag=true;
                break;
            }
        }
        if(flag==false) throw new IngredientSortOutException("The beer "+MyBeer.getDrinkName()+" is sold out");
    }
    private void use(Juice MyJuice) throws IngredientSortOutException{
        boolean flag=false;
        for(int i=0;i<JuiceList.size();i++)
        {
            if(JuiceList.get(i).isOverdue()) {
                JuiceList.remove(i);
                i--;
            }
            else if(JuiceList.get(i).getDrinkName()==MyJuice.getDrinkName()){
                JuiceList.remove(i);
                flag=true;
                break;
            }
        }

        //if(!flag)抛出异常
        if(flag==false) throw new IngredientSortOutException("The juice "+MyJuice.getDrinkName()+" is sold out");
    }
    @Override
    public void SaleMeal(String MyMeal) {//实现炸鸡店接口中用于售卖套餐的方法
         int MealNumber,i;
         for(i=0;i<Menu.size();i++)
         {
             if(MyMeal==Menu.get(i).getMealName()) {//找到顾客所点的套餐
                 MealNumber=i;//记录所点套餐的编号
                 break;
             }
         }
         //利用instanceof 来判断所点套餐中饮料是啤酒还是果汁
         if(Menu.get(i).getMyDrink() instanceof Beer) {//判断顾客点的套餐中饮料是啤酒

             try {
                 use((Beer)Menu.get(i).getMyDrink());
                 System.out.println("Congratulation！It's your Meal");
             } catch (IngredientSortOutException e) {
                 e.printStackTrace();//输出异常中所带的信息(msg)
             }

         }
         else{
             try{
                 use((Juice)Menu.get(i).getMyDrink());
                 System.out.println("Congratulation！It's your Meal");
             }catch (IngredientSortOutException e){
                 e.printStackTrace();
             }

         }
    }

    @Override
    public void Stock(ArrayList<Drink> L){
        int SumPrice=0;
        for(int i=0;i<L.size();i++)//计算进货费用
        {
            SumPrice+=L.get(i).cost;
        }
        try {
            TryStock(SumPrice);
        }catch (OverdraftBalanceException e)
        {
            e.printStackTrace();
            System.out.println(" ");
            return ;
        }
        //如果未抛出异常，就正常执行进货，并扣除费用
        balance-=SumPrice;
        for(int i=0;i<L.size();i++)
        {
            if(L.get(i) instanceof Beer) BeerList.add((Beer)L.get(i));
            else JuiceList.add((Juice)L.get(i));
        }
    }
    public void TryStock(int SumPrice) throws OverdraftBalanceException
    {
        if(SumPrice>balance) {
            throw new OverdraftBalanceException("The balance is not enough , you still need "+(SumPrice-balance)+" yuan");
        }
    }
}
public class TestDrive{
    public static void main(String[] args) {

        //测试啤酒类
        //通过LocalDate.of(int year,int month,int dayOfMonth) 来创建一个LocalDate类对象，并设置其状态(参数由用户自定义)
        //通过LocalDate.now()以当前的时间（年月日等）作为参数创建一个LocalDate类的对象
        Beer b1=new Beer("百威",80,LocalDate.of(1990,1,1),100,10);//b1已过期
        Beer b2=new Beer("雪花",40,LocalDate.now(),90,5);//b2未过期
        System.out.println("b1的信息 : "+b1.toString());
        System.out.println("b2的信息 : "+b2.toString());
        //饮料过期测试
        if(b1.isOverdue()) System.out.println("b1 已过期");
        else System.out.println("b1 未过期");
        if(b2.isOverdue()) System.out.println("b2 已过期");
        else System.out.println("b2 未过期");

        West2FriedChickenRestaurant w=new West2FriedChickenRestaurant(1000);//创建西二炸鸡店的实例，最初的资金赋为1000元
        //商店进货测试
            //初始化商店进货列表
        ArrayList<Drink> L=new ArrayList<>();//用L来储存商店进货时的物品清单
        L.add(new Beer("百威",80,LocalDate.of(2000,12,30),100,10));//放入一个已经过期的百威啤酒
        L.add(new Beer("百威",80,LocalDate.now(),100,10));
        L.add(new Beer("百威",80,LocalDate.now(),100,10));
        L.add(new Beer("雪花",40,LocalDate.now(),90,5));
        L.add(new Beer("雪花",40,LocalDate.now(),90,5));
        L.add(new Juice("橙汁",20,LocalDate.now(),2));
        L.add(new Juice("橙汁",20,LocalDate.now(),2));
        L.add(new Juice("苹果汁",15,LocalDate.now(),1));
        L.add(new Juice("苹果汁",15,LocalDate.now(),1));
        //费用一共是3*80+2*40+2*20+2*15=390元
        double tmp;
        //初始化商店饮品库存
        //同时开始测试进货用的Stock()方法
        tmp=w.getBalance();
        w.Stock(L);//即最初的一次进货
        if(w.getBalance()!=tmp) System.out.println("进货成功！");
        else System.out.println("进货失败！");
        System.out.println("balance = "+w.getBalance());
        //售卖套餐测试
           //饮料售空测试
        //连续五名顾客都点了套餐一(套餐一的饮料是“百威”，此时商店里只有三瓶百威的库存，而且有一瓶已经过期了)
        for(int i=1;i<=5;i++)//因此这五名顾客中只有前两位顾客能成功拿到他们想要的套餐一
        {
            System.out.println("第 "+i+" 位顾客 点了套餐一");
            w.SaleMeal("Meal_one");
        }
        //第二次进货
        //此时炸鸡店里饮料库存：百威3瓶(1瓶过期) 雪花4瓶 橙汁4瓶 苹果汁4瓶
        tmp=w.getBalance();
        w.Stock(L);
        if(w.getBalance()!=tmp) System.out.println("进货成功！");
        else System.out.println("进货失败！");
        System.out.println("balance = "+w.getBalance());
        //第一位顾客点了套餐二(配有“雪花”啤酒)
        System.out.println("第 1 位顾客 点了套餐二");
        w.SaleMeal("Meal_two");
        //第二位顾客点了套餐三（配有“橙汁”果汁）
        System.out.println("第 2位顾客 点了套餐三");
        w.SaleMeal("Meal_three");
        //第三位顾客点了套餐四（配有“苹果汁”果汁）
        System.out.println("第 3 位顾客 点了套餐四");
        w.SaleMeal("Meal_four");
        //第四位顾客点了套餐四（配有“苹果汁”果汁）
        System.out.println("第 4 位顾客 点了套餐四");
        w.SaleMeal("Meal_four");
        //第五位顾客点了套餐一（配有“百威”啤酒）
        System.out.println("第 5 位顾客 点了套餐一");
        w.SaleMeal("Meal_one");
        //第三次进货(这次钱不够了，无法进货)
        tmp=w.getBalance();
        w.Stock(L);
        if(w.getBalance()!=tmp) System.out.println("进货成功！");
        else System.out.println("进货失败！");
        System.out.println("balance = "+w.getBalance());
        //本次进货不成功，此时炸鸡店里的饮料库存为：百威1瓶 雪花3瓶 橙汁3瓶 苹果汁2瓶
        //第一位顾客点了套餐一(配有“百威”啤酒)
        System.out.println("第 1 位顾客 点了套餐一");
        w.SaleMeal("Meal_one");
        //第二位顾客点了套餐四(配有“苹果汁”果汁)
        System.out.println("第 2 位顾客 点了套餐四");
        w.SaleMeal("Meal_four");
        //第三位顾客点了套餐二(配有“雪花”啤酒)
        System.out.println("第 3 位顾客 点了套餐二");
        w.SaleMeal("Meal_two");
        //第四位顾客点了套餐一(配有“百威”啤酒)
        System.out.println("第 4 位顾客 点了套餐一");
        w.SaleMeal("Meal_one");
        //第五位顾客点了套餐四(配有“苹果汁”果汁)
        System.out.println("第 5 位顾客 点了套餐四");
        w.SaleMeal("Meal_four");
        //第六位顾客点了套餐四(配有“苹果汁”果汁)
        System.out.println("第 6 位顾客 点了套餐四");
        w.SaleMeal("Meal_four");
    }
}