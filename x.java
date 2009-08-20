
	 public static int getSignificantFigures(String number, boolean removeTrailingZeros){
	    
	 int sigfigs = 0;
	 //remove any leading zeros from the number
	 while(number.charAt(0) == '0'){
	 number = number.substring(1);
	 }
	 //remove any trailing zeros from the number
	 if(removeTrailingZeros){
	 while(number.charAt(number.length() - 1) == '0'){
	 number = number.substring(0, number.length() - 1);
	 }
	 }
	 //find decimal place in number
	 int decPointPos = number.lastIndexOf(".");
	 System.out.println("dec point found at: " + decPointPos);
	
	 for(int i = 0; i < decPointPos; i++){
	 number = number.substring(1);
	 sigfigs++;
	 }
	 if(number.charAt(0) == '.'){
	 number = number.substring(1);
	 }
	 while(number.length() > 0){
	 number = number.substring(1);
	 sigfigs++;
	 }
	 return sigfigs;
	 }
	
	 public static String roundSignificantFigures(String number, int numFigs){
	 //outputs a numerical string 
	 //e.g., 12345 to 2 significant figures is 12000, not 1.2*10^4
	 //although the latter is more correct, the former is more intuitive.
	 boolean debug = false;
	 //check if number is negative. Remove and remember.
	 boolean isNegative = false;
	 if(number.charAt(0) == '-'){
	 isNegative = true;
	 number = number.substring(1);
	 }
	 //remove any leading zeros from the number
	 while(number.charAt(0) == '0'){
	 number = number.substring(1);
	 }
	
	 if(debug)
	 System.out.println("number is " + number);
	
	 int order = (int) Math.floor(Math.log10(Double.parseDouble(number)));
	
	 if(debug)
	 System.out.println("Number is order " + order);
	 //find decimal place in number
	 int decPointPos = number.lastIndexOf(".");
	 //we want to remove the decimal point, to make things easier
	 number = number.replaceFirst("\\.", "");
	 //next we want to round off the insignificant digits
	 String significant = number.substring(0, numFigs);
	 String insignificant = number.substring(numFigs);
	 String forRounding = significant + "." + insignificant;
	 int roundedSignificant = (int) Math.round(Double.parseDouble(forRounding));
	
	 if(debug)
	 System.out.println("chopped number down to " + roundedSignificant);
	 String roundedSignificantStr = "" + roundedSignificant;
	 String outputStr = "";
	 //restore number to its original order
	 int currentOrder = (int) Math.floor(Math.log10(roundedSignificant));
	 if(debug)
	 System.out.println("order was " + order + " and is now " + currentOrder);
	
	 if(currentOrder > order){
	 //we need to make this a decimal.
	 //number was sth like 1.20 and now it's 12
	 if(order >= 0){
	 for(int i = 0; i <= order; i++){
	 outputStr += roundedSignificantStr.charAt(0);
	 roundedSignificantStr = roundedSignificantStr.substring(1);
	 }
	 outputStr += ".";
	 while(! roundedSignificantStr.equals("")){
	 outputStr += roundedSignificantStr.charAt(0);
	 roundedSignificantStr = roundedSignificantStr.substring(1);
	 }
	 }
	 else{
	 outputStr = "0.";
	 for(int i = 0; i < ((int) Math.abs(order) - 1); i++){
	 outputStr += "0";
	 }
	 while(! roundedSignificantStr.equals("")){
	 outputStr += roundedSignificantStr.charAt(0);
	 roundedSignificantStr = roundedSignificantStr.substring(1);
	 }
	 }
	 }
	 else{
	 //number was sth like 123456 and now it's 12
	 for(int i = 0; i < roundedSignificantStr.length(); i++){
	 outputStr += roundedSignificantStr.charAt(i);
	 }
	 while(outputStr.length() <= order){
	 outputStr += "0";
	 }
	 }
	
	 if(debug)
	 System.out.println("restored number to " + outputStr);
	 //remove trailing zeros if number is a decimal
	 /*if(outputStr.contains(".")){
	 while(outputStr.charAt(outputStr.length() - 1) == '0'){
	 outputStr = outputStr.substring(0, outputStr.length() - 1);
	 }
	 //remove dec. point if no zeros after it
	 if(outputStr.charAt(outputStr.length() - 1) == '.'){
	 outputStr = outputStr.substring(0, outputStr.length() - 1);
	 }
	 }*/
	 if(isNegative){
	 outputStr = "-" + outputStr;
	 }
	 return outputStr;