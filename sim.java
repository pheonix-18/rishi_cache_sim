import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
public class sim {
	public static void main(String args[]){
		System.out.println("This is corrected line");
		if (args[0].equals("smith")){
			smith_predictor(args, Integer.parseInt(args[1]));
		}
		else if(args[0].equals("bimodal")) {
			int array[] = new int[(int) Math.pow(2, Integer.parseInt(args[1]))];
			Arrays.fill(array, 4);
			bimodal_predictor(args, Integer.parseInt(args[1]), array);
		}		
		else if(args[0].equals("gshare")) {
			int array[] = new int[(int) Math.pow(2, Integer.parseInt(args[1]))];
			Arrays.fill(array, 4);
			gshare_predictor(args, Integer.parseInt(args[1]), array);
			}
		else if(args[0].equals("hybrid")) {
			hybrid_predictor(args);
		}		
		else {
			System.out.println("Kindly enter the correct model name");
		}
	}	
	 public static String convert_hex_to_bin(String hex_command){
	        String binary_str = "";
	        Map<Character, String> map = new HashMap<>();
	        map.put('7', "0111");
	        map.put('6', "0110");
	        map.put('0', "0000");
	        map.put('1', "0001");
	        map.put('2', "0010");
	        map.put('8', "1000");
	        map.put('c', "1100");
	        map.put('5', "0101");
	        map.put('9', "1001");
	        map.put('3', "0011");
	        map.put('4', "0100");
	        map.put('a', "1010");
	        map.put('f', "1111");
	        map.put('b', "1011");
	        map.put('d', "1101");
	        map.put('e', "1110");
	        
	        hex_command = hex_command.toLowerCase();
	        for(char ch: hex_command.toCharArray()){
	            binary_str = binary_str + (map.get(ch));
	        }
			while(binary_str.length() !=24) {
				binary_str = "0" + binary_str ;
			}
	        return binary_str.toString();
	    }
	public static int no_of_lines = 0;
	public static int no_of_misPre = 0;
	public static String processed_string;
	public static String true_prediction;
	
	public static void gshare_predictor(String[] args, int gshare_bit,  int[] gshare_array){
		try{
		int i = 0;
		int n= Integer.parseInt(args[2]);
		String history_register = "";
		String file = args[3];
		File fp = new File(file);
		BufferedReader ba = new BufferedReader(new FileReader(fp));
		while (i < n) {
			history_register  += "0";
			i++;
		}
		while ((processed_string = ba.readLine()) != null) {
			
			String hex_address = processed_string.substring(0, 6);
			true_prediction = processed_string.substring(7, 8);
			
			String binary = convert_hex_to_bin(hex_address);
			String initial_mn_bits = binary.substring(binary.length()-gshare_bit-2, binary.length()-n-2);
			String final_n_bits = binary.substring(binary.length()-n-2, binary.length()-2);	
			
			int x1 = Integer.parseInt(history_register, 2);
			int x2 = Integer.parseInt(final_n_bits, 2);
			
			int xor_result = x1^x2;
			
			String bin_xor_result = Integer.toBinaryString(xor_result);
			while(bin_xor_result.length()<n) {
				bin_xor_result = "0" + bin_xor_result;
			}			
			String result_bit_tag  = initial_mn_bits + bin_xor_result ;
			int indexValue = Integer.parseInt(result_bit_tag,2);	
			int mid_point = 4;
			String predict_value = "";
			if (gshare_array[indexValue] >= mid_point) {
				predict_value = "t";
			}
			else{
				predict_value = "n";
			}
			if(true_prediction.equals("n")) {
				if(gshare_array[indexValue] > 0) {
					gshare_array[indexValue]--;
				}
				history_register = history_register.substring(0, history_register.length()-1);
				history_register = "0" + history_register;
			}

			if(true_prediction.equals("n") && predict_value.equals("t")) {
				no_of_misPre++;
			}

			if(true_prediction.equals("t")) 
			{
				if(gshare_array[indexValue] < 7) {
					gshare_array[indexValue]++;
				}
				history_register = history_register.substring(0, history_register.length()-1);
				history_register = "1" + history_register;
			}
			if(true_prediction.equals("t") && predict_value.equals("n")) {
				no_of_misPre++;
			}
			no_of_lines++;
		}
		System.out.println("COMMAND");
		System.out.println("./sim "+ args[0]+" "+args[1]+" "+args[2]+" "+args[3]);
		System.out.println("OUTPUT");
		System.out.println("number of predictions:" + no_of_lines);
		System.out.println("number of mispredictions:" + no_of_misPre);
		System.out.println("misprediction rate:	" + String.format("%.2f", (((float)no_of_misPre/(float)no_of_lines)*100))+"%");
		System.out.println("FINAL GSHARE CONTENTS");
		for (i = 0;i < gshare_array.length; i++ ) {
			System.out.println(i + "	" + gshare_array[i]);
		}
		ba.close();
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	}

	public static void bimodal_predictor(String[] args, int bimodal_bit, int[] bimodal_array){
		try{
		String file = args[2];
		File fp = new File(file);
		BufferedReader br = new BufferedReader(new FileReader(fp));
		
		while ((processed_string = br.readLine()) != null) {
			String hex_address = processed_string.substring(0, 6);
			true_prediction = processed_string.substring(7, 8);			
			String binAddr = convert_hex_to_bin(hex_address);
			binAddr = binAddr.substring(binAddr.length()-bimodal_bit-2, binAddr.length()-2);
			int result_bit_tag = Integer.parseInt(binAddr,2);		
			int mid_point = 4;
			String predict_value = "";
			if (bimodal_array[result_bit_tag] >= mid_point) {
				predict_value = "t";
			}
			else{
				predict_value = "n";
			}
			if(true_prediction.equals("n")) {
				if(bimodal_array[result_bit_tag] > 0) {
					bimodal_array[result_bit_tag]--;
				}
			}

			if(true_prediction.equals("n") && predict_value.equals("t")) {
				no_of_misPre++;
			}

			if(true_prediction.equals("t")) 
			{
				if(bimodal_array[result_bit_tag] < 7) {
					bimodal_array[result_bit_tag]++;
				}
			}
			if(true_prediction.equals("t") && predict_value.equals("n")) {
				no_of_misPre++;
			}
			no_of_lines++;
		}
		System.out.println("COMMAND");
		System.out.println("./sim "+args[0]+" "+args[1]+" "+args[2]);
		System.out.println("OUTPUT");
		System.out.println("number of predictions:" + no_of_lines);
		System.out.println("number of mispredictions:" + no_of_misPre);
		Double miss =Double.valueOf(no_of_misPre) ;
		Double lines = Double.valueOf(no_of_lines);
		Double x = (miss/lines)*100;
		Double y = Math.round(x*100.0)/100.0;
		System.out.println("misprediction rate:" + String.format("%.2f", y) + "%");
		System.out.println("FINAL BIMODAL CONTENTS");
		for (int i = 0;i < bimodal_array.length; i++ ) {
			System.out.println(i + "	" + bimodal_array[i]);
		}
		br.close();
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	}

	public static void smith_predictor(String[] args, int smith_bit) {
		try{
		String file = args[2];
		Double temp_variable = Math.pow(2, smith_bit-1);
		int smith_bit_counter = (int)Math.round(temp_variable);
		File fp = new File(file);
		BufferedReader br = new BufferedReader(new FileReader(fp));
		while ((processed_string = br.readLine())  != null) {
			String true_prediction = processed_string.substring(7, 8);
			Double temp_Midpoint = Math.pow(2, smith_bit)/2;
			int mid_point = (int)Math.round(temp_Midpoint);
			String predict_value = "";
			if (smith_bit_counter >= mid_point) {
				predict_value = "t";
			}
			if (smith_bit_counter < mid_point) {
				predict_value = "n";
			}
			if(true_prediction.equals("n")) {
				if(smith_bit_counter > 0) {
					smith_bit_counter--;
				}
			}

			if(true_prediction.equals("n") && predict_value.equals("t")) {
				no_of_misPre++;
			}

			if(true_prediction.equals("t")) {
				Double temp_value = Math.pow(2, smith_bit) - 1;
				int temp = (int)Math.round(temp_value);
				if(smith_bit_counter < temp) {
					smith_bit_counter++;
				}
			}
			if(true_prediction.equals("t") && predict_value.equals("n")) {
				no_of_misPre++;
			}
			no_of_lines++;
		}
		System.out.println("COMMAND");
		System.out.println("./sim "+args[0]+" "+args[1]+" "+args[2]);
		System.out.println("OUTPUT");
		System.out.println("number of predictions:	" + no_of_lines);
		System.out.println("number of mispredictions:	" + no_of_misPre);
		System.out.println("misprediction rate:	" + String.format("%.2f", ((float)no_of_misPre/(float)no_of_lines)*100) + "%");
		System.out.println("FINAL COUNTER CONTENT:	" + smith_bit_counter);
		br.close();
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	public static void update_array(String true_prediction, int[] bimodal_array, char model_prediction, int hex_address) {
		if(true_prediction.equals("n")) {
			if(bimodal_array[hex_address] > 0) {
				bimodal_array[hex_address]--;
			}
		}

		if(true_prediction.equals("n") && model_prediction == 't') {
			no_of_misPre++;
		}

		if(true_prediction.equals("t")) 
		{
			if(bimodal_array[hex_address] < 7) {
				bimodal_array[hex_address]++;
			}
		}
		if(true_prediction.equals("t") && model_prediction == 'n') {
			no_of_misPre++;
		}
	}
	public static void hybrid_predictor(String[] args){
		try{
		String history_register = "";
		
		int k = Integer.parseInt(args[1]);
		int hybrid_bit_m1 = Integer.parseInt(args[2]);
		int hybrid_bit_n = Integer.parseInt(args[3]);
		int hybrid_bit_m2 = Integer.parseInt(args[4]);
		for (int i = 0; i < hybrid_bit_n; i++) {
			history_register  += "0";
		}
		String file = args[5];
		int chooser_array[] = new int[(int) Math.pow(2, k)];
		Arrays.fill(chooser_array, 1);
		int bimodal_array[] = new int[(int) Math.pow(2, hybrid_bit_m2)];
		Arrays.fill(bimodal_array, 4);
		int gshare_array[] = new int[(int) Math.pow(2, hybrid_bit_m1)];
		Arrays.fill(gshare_array, 4);
		File fp = new File(file);
		BufferedReader br = new BufferedReader(new FileReader(fp));
		while ((processed_string = br.readLine()) != null) {
			String hex_address = processed_string.substring(0, 6);
			true_prediction = processed_string.substring(7, 8);
			String hex_to_bin = convert_hex_to_bin(hex_address);
			String k_length = hex_to_bin.substring(hex_to_bin.length()-k-2, hex_to_bin.length()-2);
			int  index = Integer.parseInt(k_length,2);
			String binaryValue = hex_to_bin.substring(hex_to_bin.length()-hybrid_bit_m2-2, hex_to_bin.length()-2);
			int hex_addVal = Integer.parseInt(binaryValue,2);
			int temp = bimodal_array[hex_addVal];
			char model_predictionn = 'n';
			if (temp >= 4) {
				model_predictionn = 't';
			}
			else {
				model_predictionn = 'n';
			}
			String bimodal_res =Character.toString(model_predictionn);
			int x1 = Integer.parseInt(history_register, 2);
			int x2 = Integer.parseInt(hex_to_bin.substring(hex_to_bin.length()-hybrid_bit_n-2, hex_to_bin.length()-2), 2);
			int xor_result = x1^x2;
			String bin_xor_result = Integer.toBinaryString(xor_result);
			while(bin_xor_result.length()<hybrid_bit_n) {
				bin_xor_result = "0" + bin_xor_result;
			}
			String result_bit_tag  = hex_to_bin.substring(hex_to_bin.length()-hybrid_bit_m1-2, hex_to_bin.length()-hybrid_bit_n-2) + bin_xor_result;		
			int indexT = Integer.parseInt(result_bit_tag,2);
			String model_pred = "n";
			if (gshare_array[indexT] >= 4) {
				model_pred = "t";
			}
			else{
				model_pred = "n";
			}
			no_of_lines++;
			if(chooser_array[index] >=0 && chooser_array[index] < 2) {
				hex_to_bin = hex_to_bin.substring(hex_to_bin.length()-hybrid_bit_m2-2, hex_to_bin.length()-2);
				int hex_add_val = Integer.parseInt(hex_to_bin,2);
				char model_prediction =0;
				if (bimodal_array[hex_add_val] >= 4) {
					model_prediction = 't';
					}
				else {
					model_prediction = 'n';
					}
		
				update_array(true_prediction, bimodal_array, model_prediction, hex_add_val);
			}
			else if(chooser_array[index] >= 2 && chooser_array[index] < 4){
				String initial_mn_bits = hex_to_bin.substring(hex_to_bin.length()-hybrid_bit_m1-2, hex_to_bin.length()-hybrid_bit_n-2);
				String final_n_bits = hex_to_bin.substring(hex_to_bin.length()-hybrid_bit_n-2, hex_to_bin.length()-2);	
				int x11 = Integer.parseInt(history_register, 2);
				int x22 = Integer.parseInt(final_n_bits, 2);
				int xor_result1 = x11^x22;
				String bin_xor_result1 = Integer.toBinaryString(xor_result1);
				while(bin_xor_result1.length()<hybrid_bit_n) {
					bin_xor_result1 = "0" + bin_xor_result1;	
				}	
				String result_bit_tag1  = initial_mn_bits + bin_xor_result1;	
				int indexVal = Integer.parseInt(result_bit_tag1,2);	
				char model_prediction = 0;
				if (gshare_array[indexVal] >= 4) {
					model_prediction = 't';
				}
				else{
					model_prediction = 'n';
				}
				update_array(true_prediction, gshare_array, model_prediction, indexVal);
			}
			if(true_prediction.equals("n")) {
				history_register = "0" + history_register;
				history_register = history_register.substring(0, history_register.length()-1);
			}
			
			else {
				history_register = "1" + history_register;
				history_register = history_register.substring(0, history_register.length()-1);
			}
			if(bimodal_res.equals(true_prediction) && !model_pred.equals(true_prediction)) {
				if(chooser_array[index] > 0 ) {
					chooser_array[index]--;
				}
			}
			else if(!bimodal_res.equals(true_prediction) && model_pred.equals(true_prediction)) {
				if(chooser_array[index] < 3 ) {
					chooser_array[index]++;
				}
			}
		}
		System.out.println("COMMAND");
		System.out.println("./sim "+args[0] + " " + args[1] + " " + args[2] + " " + args[3] + " " + args[4] + " " + args[5]);
		System.out.println("OUTPUT");
		System.out.println("number of predictions:" + no_of_lines);
		System.out.println("number of mispredictions:" + no_of_misPre);
		System.out.println("misprediction rate:	" + String.format("%.2f", ((float)no_of_misPre/(float)no_of_lines)*100) + "%");
		System.out.println("FINAL CHOOSER CONTENTS");
		
		for (int i = 0; i < chooser_array.length; i++) {
			System.out.println(i + "	" + chooser_array[i]);					
		}
		System.out.println("FINAL GSHARE CONTENTS");
		for (int i = 0; i < gshare_array.length; i++) {
			System.out.println(i + "	" + gshare_array[i]);					 
		}

		System.out.println("FINAL BIMODAL CONTENTS");
		for (int i = 0; i < bimodal_array.length; i++) {
			System.out.println(i + "	" + bimodal_array[i]);					
		}
		br.close();
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	}
}
