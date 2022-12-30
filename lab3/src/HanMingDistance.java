public class HanMingDistance {
    public static void main(String[]args){
        System.out.println(hammingDistance(3,1));
    }
    public static int hammingDistance(int x, int y) {
        // TODO
        int hmNum=x^y;
        String hmNum10=parseTo01(hmNum);
        int count=0;
        for(int i=0;i<hmNum10.length();i++) {
            if(hmNum10.charAt(i)=='1') {
                count++;
            }
        }
        return count;
    }

    private static String parseTo01(int i) {
        StringBuilder sb=new StringBuilder();
        while(i!=0) {
            sb.append(i%2);
            i/=2;
        }
        sb.reverse();
        return sb.toString();
    }
}
