public class paraFor {
    public static void main(String args[]) {               
        for (int i=0;i<10;i++){
            System.out.println(i);
        }
        parallelfor (int i;i<10;i++) threads(2){
            System.out.println(i);
        }
    }
}
