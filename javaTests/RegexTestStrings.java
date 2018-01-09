import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegexTestStrings {
	public static final String EXAMPLE_TEST = "IF _1(FIRE_MISSION_TYPE)= 8(QUICK SMOKE MISSION) AND ( _3.3.1.9.1.2.1(FIRE_FOR_EFFECT_PROJECTILE)= 20(SMA - 60MM (M302A1 W/M527), 81MM (M375 W/M524), 120MM (M929 W/MO M734A1)) OR _3.3.1.9.1.2.1(FIRE_FOR_EFFECT_PROJECTILE)= 3(M60A2/WP - SMA - 105MM) OR _3.3.1.9.1.2.1(FIRE_FOR_EFFECT_PROJECTILE)= 12(M60/WP - SMA - 105MM) OR _3.3.1.9.1.2.1(FIRE_FOR_EFFECT_PROJECTILE)= 60(M110/WP - SMA - 155MM) OR _3.3.1.9.1.2.1(FIRE_FOR_EFFECT_PROJECTILE)= 61(M110A1/WP - SMA - 155MM) OR _3.3.1.9.1.2.1(FIRE_FOR_EFFECT_PROJECTILE)= 62(M110A2/WP - SMA - 155MM) OR _3.3.1.9.1.2.1(FIRE_FOR_EFFECT_PROJECTILE)= 72(M110E2/WP - SMA - 155MM) OR _3.3.1.9.1.2.1(FIRE_FOR_EFFECT_PROJECTILE)= 73(M110C1/WP - SMA - 155MM) OR _3.3.1.9.1.2.1(FIRE_FOR_EFFECT_PROJECTILE)= 97(M60A1/WP - SMA - 105MM) ) THEN ( _3.6.3(SMOKE_SCREEN_LENGTH_SMKSCRL)= 0(100 METERS) OR _3.6.3(SMOKE_SCREEN_LENGTH_SMKSCRL)= 1(200 METERS) OR _3.6.3(SMOKE_SCREEN_LENGTH_SMKSCRL)= 3(300 METERS) OR _3.6.3(SMOKE_SCREEN_LENGTH_SMKSCRL)= 4(400 METERS) OR _3.6.3(SMOKE_SCREEN_LENGTH_SMKSCRL)= 6(600 METERS) ) END IF ";

	public static void main(String[] args) {
		StringBuffer lBuf = new StringBuffer();
		String cleanString = EXAMPLE_TEST.replaceAll("\\([A-Z0-9 _/-]+\\)", "");
                cleanString = logic.replaceAll("\\([A-Z0-9][A-Z0-9\u0020\u002C_/-]+\\)", "");
                cleanString = cleanString.replace(" END IF ", "");
                cleanString = cleanString.replace("IF ", "");
                System.out.println("\n");
		System.out.println("cleanString = "+cleanString);


		String firstCondition = cleanString.substring(0, identifyEndOfFirstCondition(cleanString));
		System.out.println("\nfirstCondition = "+firstCondition);

		Pattern duiReplacePattern = Pattern.compile("_[1-9.]+=");
		Matcher matchObj = duiReplacePattern.matcher(cleanString);
		while(matchObj.find()) {
			matchObj.appendReplacement(lBuf, "EXPRESSION !=");
		}
		System.out.println("\nreplaced string = "+lBuf.toString());
	}

	private static int identifyEndOfFirstCondition(String cleanedString) {
		char OPEN_PAREN = '(';
		char CLOSED_PAREN = ')';
		int index = 0;
		if(cleanedString.indexOf("( ") ==0) {
			int openCount = 1;
			int closedCount = 0;
			char[] cleanedChars = cleanedString.toCharArray();
			for(int x = 1; x < cleanedChars.length; x++ ) {
				if(cleanedChars[x] == OPEN_PAREN)
					openCount++;
				else if(cleanedChars[x] == CLOSED_PAREN)
					closedCount++;

				if(openCount == closedCount) {
					index =  x+1 ;
					break;
				}
			}
		} else {
			index =  (cleanedString.indexOf(" AND "));
		}
		return index;
	}
}
