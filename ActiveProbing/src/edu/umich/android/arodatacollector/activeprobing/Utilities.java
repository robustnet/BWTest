/**
 * @author Yuanyuan Zhou
 * @date Oct 30, 2012
 * @organization University of Michigan, Ann Arbor
 */

package edu.umich.android.arodatacollector.activeprobing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

import android.os.Environment;

public class Utilities {
	public static String genRandomString(int len){
		StringBuilder sb = new StringBuilder("");
		Random ran = new Random();
		for(int i = 1; i <= len; i++){
			sb.append((char)('a' + ran.nextInt(26)));
		}
		return sb.toString();
	}
	
	public static double getMax(double[] a){  
		if(a == null || a.length == 0){
			System.err.println("getMax invalid array");
			return Double.MIN_VALUE;
		}
		double max = Double.MIN_VALUE;  
		for(int i = 0; i < a.length;i++){  
			if(a[i] > max){  
				max = a[i];  
			}  
		}  
		return max;  
	}  

	public static double getMin(double[] a){  
		if(a == null || a.length == 0){
			System.err.println("getMax invalid array");
			return Double.MIN_VALUE;
		}
		double min = Double.MAX_VALUE;  
		for(int i = 0; i < a.length; i++){  
			if(a[i] < min){  
				min = a[i];  
			}  
		}  
		return min;  
	}  

	public static double getMedian(double[] a){
		if(a == null || a.length == 0){
			System.err.println("getMedian invalid array");
			return Double.MIN_VALUE;
		}
		double[] tmp = a.clone();
		double median = 0;
		Arrays.sort(tmp);
		int len = tmp.length;
		if(len % 2 == 0){
			//len is even, e.g., len = 4, => (2 + 1) / 2
			median = (tmp[len / 2] + tmp[len / 2 - 1]) / 2;
		}else{
			//len is odd, e.g. len = 3, => 1
			median = tmp[(len - 1) / 2];
		}
		return median;
	}
	
	public static double getAverage(double[] a){
		if(a == null || a.length == 0){
			System.err.println("getAverage invalid array");
			return Double.MIN_VALUE;
		}
		double total = 0;
		for(int i = 0 ; i < a.length ; i++){
			total += a[i];
		}
		return total / (double)a.length;
	}
	
	public static double getStandardDeviation(double[] a){
		if(a == null || a.length == 0){
			System.err.println("getStandardDeviation invalid array");
			return Double.MIN_VALUE;
		}
		double std = 0;
		double avg = Utilities.getAverage(a);
		for(int i = 0 ; i < a.length ; i++){
			std += (a[i] - avg) * (a[i] - avg);
		}
		return Math.sqrt(std / (double)(a.length - 1));
	}
	
	public static boolean writeToSDCard(String str, String filename) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        	return false;

        File f = new File(Environment.getExternalStorageDirectory(), filename);
        if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        BufferedWriter out = null;  
        try {  
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true)));  
            out.write(str);
            out.flush();
        } catch (Exception e) { 
            e.printStackTrace();
        } finally {  
            try {  
                out.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
        
        return true;
	}
	
	public static double[] pushResult(double[] results, double res){
		double[] tmp = results.clone();
		results = new double[tmp.length + 1];
		for(int i = 0; i < tmp.length; i++)
			results[i] = tmp[i];
		results[tmp.length] = res;
		return results.clone();
	}
	
	public static double roundDouble(double src) {
		DecimalFormat df = new DecimalFormat("#0.00");
		return Double.valueOf(df.format(src));
	}
}
