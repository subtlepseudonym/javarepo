
public class Posting {
	
	private String sceneId;
	private int sceneNum; //docId
	private int[] positions;
	
	public Posting (String scene, int id, int[] pos) {
		sceneId = scene;
		sceneNum = id;
		positions = pos;
	}
	
	public String getScene () {
		return sceneId;
	}
	
	public int getId () {
		return sceneNum;
	}
	
	public int[] getPos () {
		return positions;
	}

}
