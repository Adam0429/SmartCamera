package helper;

public class DataHelper {
	public static String SplitResult(String result,String attribute){
		if(!result.contains("value"))
			return "no result";
		switch (attribute) {
			case "sadness":
				return "sadness" + result.split("sadness\":")[1].split(",")[0];
			case "nautral":
				return "neutral" + result.split("neutral\":")[1].split(",")[0];		
			case "disgust":
				return "disgust" + result.split("disgust\":")[1].split(",")[0];
			case "anger":
				return "anger" + result.split("anger\":")[1].split(",")[0];
			case "surprise":
				return "surprise" + result.split("surprise\":")[1].split(",")[0];
			case "fear":
				return "fear" + result.split("fear\":")[1].split(",")[0];
			case "happiness":
				String s = result.split("happiness\":")[1].split(",")[0]; 
				s = s.substring(0,s.length()-1);
				return "happiness" + s;
			case "beau":
				String s2 = result.split("male_score\":")[1].split("beau")[0];
				s2 = s2.substring(0,s2.length()-3);
				return s2;
			case "smile":
				float threshold = Float.parseFloat(result.split("threshold\":")[1].split(",")[0]);//代表笑容的阈值,超过就是笑了
				String s1 =(result.split("threshold\":")[1].split("value\":")[1]).split(",")[0];
				s1 =s1.substring(0,s1.length()-1);
				float smile = Float.parseFloat(s1);
				if(smile>threshold)
					return "smiling";
				else 
					return "not smile";
			case "gender":
				if(result.contains("gender")){
					if(result.contains("Male"))
						return "Male";
					else 
						return "Female";
				}
				else 
					return "Unknown gender";
			case "age":
				String s3 = result.split("age")[1];
				s3 = s3.substring(12,15);
				return s3;
			case "all":
				return "Gender:"+SplitResult(result, "gender")+'\n'+"Age:"+SplitResult(result, "age")+'\n'+"Emotion:"+emotion(result)+'\n'+"beau:"+
				SplitResult(result, "beau");
			}
			return "";
	}
	
	public static String emotion(String result){
		String emotion = "";
		float max = 0;
		int index = -1;
		float[] a = new float[7];
		a[0] = Float.parseFloat(result.split("sadness\":")[1].split(",")[0]);
		a[1] = Float.parseFloat(result.split("neutral\":")[1].split(",")[0]);
		a[2] = Float.parseFloat(result.split("disgust\":")[1].split(",")[0]);
		a[3] = Float.parseFloat(result.split("anger\":")[1].split(",")[0]);
		a[4] = Float.parseFloat(result.split("surprise\":")[1].split(",")[0]);
		String s = result.split("happiness\":")[1].split(",")[0]; 
		s = s.substring(0,s.length()-1);
		a[5] = Float.parseFloat(s);
		a[6] = Float.parseFloat(result.split("fear\":")[1].split(",")[0]);
		for(int i = 0 ; i <= 6 ; i++){
			if(a[i]>max){
				index = i;
				max = a[i];
			}
		}
		switch (index) {
		case 0:
			return "sadness";
		case 1:
			return "neutral";
		case 2:
			return "disgust";
		case 3:
			return "anger";
		case 4:
			return "surprise";
		case 5:
			return "happiness";
		case 6:
			return "fear";
		default:
			break;
		}
		return "";
		
	}
}
