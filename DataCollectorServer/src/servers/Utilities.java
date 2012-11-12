package servers;

import java.util.Random;
import java.text.DecimalFormat;

public class Utilities {
	public static String genRandomString(int len){
		StringBuilder sb = new StringBuilder("");
		Random ran = new Random();
		for(int i = 1; i <= len; i++){
			sb.append((char)('a' + ran.nextInt(26)));
		}
		return sb.toString();
	}
	
	public static String[] parsePrefix(String prefix){
		String[] prefix_array = prefix.trim().split("<");
		//<aaa><aaa><aaa>
		//"" "aaa>" "aaa>"  "aaa>"
		
		String[] result = new String[prefix_array.length - 1];
		for(int i = 0; i < prefix_array.length - 1; i++){
			result[i] = prefix_array[i + 1].split(">")[0];
		}
		
		if(result.length >= 3)
			return result;
		else
			return null;
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
