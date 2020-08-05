//백준은 package main; 이 default
//10818
import java.io.*;
import java.util.StringTokenizer;

class Main {
    public static void main(String args[]) throws IOException {
    	int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        
        int N = Integer.parseInt(bf.readLine());
        StringTokenizer st = new StringTokenizer(bf.readLine(), " ");
        bf.close();
        
        for(int i = 0; i < N; i++){
        	int a = Integer.parseInt(st.nextToken());
            min = (a < min) ? a : min;
            max = (a > max) ? a : max;
        }
        System.out.println(min + " " + max);
    }
}

//2562
import java.io.*;

public class Main {
    public static void main(String args[]) throws IOException {
    	int max = Integer.MIN_VALUE;
    	int o = 0;
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        
        for(int i = 0; i < 9; i++){
        	int a = Integer.parseInt(bf.readLine());
            max = (a > max) ? a : max;
            o = (a == max) ? i + 1 : o;
        }
        bf.close();
        System.out.println(max);
        System.out.println(o);
    }
}

//2577
import java.io.*;

class Main {
    public static void main(String args[]) throws IOException {
    	int o = 1;
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        
        for(int i = 0; i < 3; i++){
        	o *= Integer.parseInt(bf.readLine());
        }
        bf.close();
        String a = Integer.toString(o);
        
        for(int i = 0; i < 10; i++) {
            int cnt = 0;
        	for(int j = 0; j < a.length(); j++) {
        		o = Integer.parseInt(a.substring(j, j+1));
        		cnt = (o == i) ? cnt + 1 : cnt;
        	}
        	System.out.println(cnt);
        }
    }
}
