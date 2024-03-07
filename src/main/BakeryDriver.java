import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BakeryDriver {

    public BakeryDriver() {
    }

    public static void main(String[] args)  
    {
        System.out.println("sugar".substring(0,"sugar".length()-1));
        ArrayList<String> list1 = (ArrayList) Arrays.asList("1","2","3");
        ArrayList<String>list2 = (ArrayList) List.copyOf(list1);
        list1.remove("1");
        System.out.println(list1.toString());
        System.out.println(list2.toString());

    }

}