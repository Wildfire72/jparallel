public class minMax {
        public static void main(String args[]) {               
            int[] vals = new int[100];
            for(int i=0;i<vals.length;i++){
                vals[i] = i;
            }

            int mi = 1000;
            int ma = -1;

            parallelfor(int i=0;i<vals.length;i++) threads(4) 
                reduction(min:mi){
                    if (vals[i]<mi){
                        mi=vals[i];
                    }
                }

            parallelfor(int i=0;i<vals.length;i++) threads(4)
                reduction(max:ma){
                    if (vals[i]>ma){
                        ma = vals[i];
                    }
                }

            System.out.println("Max = "+ma);
            System.out.println("Min = "+mi);
        }
}
