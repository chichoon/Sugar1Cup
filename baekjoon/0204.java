//백준은 package main; 이 default
//3052
import java.io.*;

public class Main {
    public static void main(String args[]) throws IOException {
    	int[] o = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int cnt = 0;
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        
        for(int i = 0; i < 10; i++){
        	o[i] = Integer.parseInt(bf.readLine()) % 42;
        }
        bf.close();

        for(int i = 0; i < 42; i++) {
        	for(int j = 0; j < 10; j++) {
        		if(o[j] == i) {
        			cnt++;
        			break;
        		}
        	}
        }
    	System.out.println(cnt);
    }
}

//1546
import java.io.*;
import java.util.StringTokenizer;

class Main {
    public static void main(String args[]) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        int max = Integer.MIN_VALUE;
        float cnt = 0;
        int N = Integer.parseInt(bf.readLine());
        StringTokenizer st = new StringTokenizer(bf.readLine(), " ");
        bf.close();
        
        for(int i = 0; i < N; i++){
        	int a = Integer.parseInt(st.nextToken());
        	cnt += a;
        	max = (a > max) ? a : max;
        }
    	System.out.println((((cnt / N) / max) * 100));
    }
}


