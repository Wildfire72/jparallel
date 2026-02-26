public class paraFor {
    public static void main(String args[]) {               
        parallelfor (int i=0;i<100;i++) threads(3){
            System.out.println(i);
        }
    }
}
