public class vars {
    public static void main(String args[]) {               
        int r1=0,r2;
        parallel{
            r1=compute(0,1000); //check assignment
            r2=compute(1001,2000);
        }
        System.out.println(r1+r2);
    }

    static int compute(int r1,int r2){
        int r = 0;
        for (int i=r1; i<r2;i++){
            r+=i;
        }
        return r;
    }
}
