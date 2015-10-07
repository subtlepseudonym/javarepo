
public class Scene implements Comparable<Scene> {
	
	private String sceneId;
	private int sceneNum;
	private int wordCount;
	
	public Scene (String id, int num, int wc) {
		sceneId = id;
		sceneNum = num;
		wordCount = wc;
	}
	
	public String getSceneId () {
		return sceneId;
	}
	
	public String getPlayId () {
		return sceneId.substring(0, sceneId.indexOf(':'));
	}
	
	public int getSceneNum () {
		return sceneNum;
	}
	
	public int size () {
		return wordCount;
	}
	
	public int compareTo (Scene other) {
		return sceneId.compareTo(other.getSceneId());
	}

}
