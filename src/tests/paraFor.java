public class paraFor {
    public static void main(String args[]) {               
        parallelfor (int i=0;i<10;i++) threads(2){
            System.out.println(i);
        }
    }
}
