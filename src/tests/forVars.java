public class forVars {
    public static void main(String args[]) {               
        int r1,r2;

        parallel{
            for (int i=0; i<1000;i++){
                r1+=i;
            }
            for (int i=1001;i<2000;i++){
                r2+=i;
            }
        }
        System.out.println(r1+r2);
    }
}
