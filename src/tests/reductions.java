public class reductions {
    public static void main(String args[]) {               
        int sum=0;
        int prod=1;
        int mi=100;
        int ma=0;
        boolean and = false;
        boolean or = false;
        int bit_and = 1;
        int bit_or = 1;
        int bit_xor = 1;

        int[] nums = {1,2,3,4};
        
        parallelfor (int i=0;i<nums.length;i++) threads(2) 
            reduction(+:sum){
                sum+=nums[i];
            }
        
        parallelfor (int i=0;i<nums.length;i++) threads(2) 
            reduction(*:prod){
                prod*=nums[i];
            }
        parallelfor (int i=0;i<nums.length;i++) threads(2) 
            reduction(min:mi){
                if (mi>nums[i]){
                    mi = nums[i];
                }
            }
        parallelfor (int i=0;i<nums.length;i++) threads(2) 
            reduction(max:ma){
                if (ma<nums[i]){
                    ma = nums[i];
                }
            }
        parallelfor (int i=0;i<nums.length;i++) threads(2) 
            reduction(&&:and){
                and = and && nums[i];
            }
        parallelfor (int i=0;i<nums.length;i++) threads(2) 
            reduction(||:or){
                or = or || nums[i];
            }
        parallelfor (int i=0;i<nums.length;i++) threads(2) 
            reduction(&:bit_and){
                bit_and = bit_and & nums[i];
            }
        parallelfor (int i=0;i<nums.length;i++) threads(2) 
            reduction(|:bit_or){
                bit_or = bit_or | nums[i];
            }
        parallelfor (int i=0;i<nums.length;i++) threads(2) 
            reduction(^:bit_xor){
                bit_xor = bit_xor ^ nums[i];
            }
    }
}
