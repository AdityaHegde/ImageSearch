package adityash.imagesearch;

import org.json.JSONException;
import org.json.JSONObject;

public class Image {
	public String title;
	public String url;
	public int height;
	public int width;
	public String tbUrl;
	public int tbHeight;
	public int tbWidth;
	
	public Image() {
	}
	
	public Image(JSONObject json) {
		try {
			this.title = json.getString("titleNoFormatting");
			this.url = json.getString("url");
			this.height = json.getInt("height");
			this.width = json.getInt("width");
			this.tbUrl = json.getString("tbUrl");
			this.tbHeight = json.getInt("tbHeight");
			this.tbWidth = json.getInt("tbWidth");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
