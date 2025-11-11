public class vars {
    public static void main(String args[]) {               
        int r1=0,r2;
        int r3;
        int r4 = 1;
        byte b=0,b1;
        byte b2;
        byte b3 = 0;
        double d=0,d1;
        double d2;
        double d2 = 3.2;
        short h,h1=0;
        short h2 = 1;
        short h3;
        long l1=0,l;
        long l2;
        long l3 = 1;
        float f,f1;
        float f2;
        float f3 = 1.32;
        char c,c1;
        char c2;
        char c3 = 4;
        boolean o,o1;
        boolean o2 = true;
        boolean o3;
        String s,s1;
        String s2;
        String s3 = "Hello";

        parallel{
            r1=compute(0,1000);
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
