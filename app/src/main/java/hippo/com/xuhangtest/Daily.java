package hippo.com.xuhangtest;

public class Daily {

    public String reverseStr(String str){
        String newstr = null;
        for(int i = str.length()-1;i>=0;i--)
        newstr+=str.charAt(i);
        return newstr;
    }

    public String reverseBuildin(String str){
        StringBuilder builder = new StringBuilder(str);
        return builder.reverse().toString();

    }

    public void strexe(){
        String str = "acds 123 jkl 456";
        String str2 = str.trim();
        str.concat("ddd");
        //String.
    }

    public static void main(String args[]){
        Daily daily = new Daily();
        String s = daily.reverseStr("123456");
        System.out.print(s);
        String s2 = daily.reverseStr("78910");
        System.out.print(s2);
    }

}
