package adityash.imagesearch;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import adityash.imagesearch.AdvancedSettingsDialog.AdvancedSettingsDialogListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ImageSearchActivity extends FragmentActivity implements AdvancedSettingsDialogListener, OnScrollListener {
	private static String API = "https://ajax.googleapis.com/ajax/services/search/images";
	private static int SIZE = 8;
	private static String VERSION = "1.0";
	private static int VISIBLE_THRESHOLD = 12;
	
	private EditText etQuery;
	private GridView gvImageResult;
	private ArrayList<Image> images;
	private ImageResultsAdapter imageResultsAdapter;
	private AdvancedSettings settings;
	
	private RequestParams params;
	private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);
        setupAdapter();
    }

    private void setupAdapter() {
    	etQuery = (EditText) findViewById(R.id.etQuery);
    	gvImageResult = (GridView) findViewById(R.id.gvImageResults);
    	images = new ArrayList<Image>();
    	imageResultsAdapter = new ImageResultsAdapter(getBaseContext(), images);
    	gvImageResult.setAdapter(imageResultsAdapter);
    	gvImageResult.setOnScrollListener(this);
    	
    	Button btnSearch = (Button) findViewById(R.id.btnSearch);
    	btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fetchImages(etQuery.getText().toString());
			}
		});
    	
    	this.settings = new AdvancedSettings();
    	
    	initUIL();
    }
    
    private void initUIL() {
    	ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getBaseContext())
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.diskCacheFileNameGenerator(new Md5FileNameGenerator())
		.diskCacheSize(50 * 1024 * 1024)
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.writeDebugLogs()
		.build();
    	ImageLoader.getInstance().init(config);
	}

	private void fetchImages(String query) {
    	params = new RequestParams();
    	params.put("v", VERSION);
    	params.put("q", query);
    	params.put("rsz", SIZE);
    	params.put("start", 0);
    	if(settings.imageSize != null) {
    		params.put("imgsz", settings.imageSize);
    	}
    	if(settings.imageColor != null) {
    		params.put("imgcolor", settings.imageColor);
    	}
    	if(settings.imageType != null) {
    		params.put("imgtype", settings.imageType);
    	}
    	if(settings.site != null) {
    		params.put("as_sitesearch", settings.site);
    	}
    	System.out.println(params.toString());
    	fetchImagesForUrl(API+"?"+params.toString(), 0, true);
    }
    
    private void fetchImagesForUrl(String url, final int pos, final boolean clearList) {
    	AsyncHttpClient client = new AsyncHttpClient();
    	System.out.println(url);
    	client.get(url, new JsonHttpResponseHandler() {
    		@Override
    		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
    			if(clearList) {
    				//clear images only on success
    				images.clear();
    			}
    			try {
					JSONArray results = response.getJSONObject("responseData").getJSONArray("results");
					for(int i = 0; i < results.length(); i++) {
						images.add(i + pos * SIZE, new Image(results.getJSONObject(i)));
					}
					imageResultsAdapter.notifyDataSetChanged();
					loading = false;
				} catch (JSONException e) {
					e.printStackTrace();
					loading = false;
				}
    		}
    	});
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	showAdvancedSettingsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showAdvancedSettingsDialog() {
      	FragmentManager fm = getSupportFragmentManager();
      	AdvancedSettingsDialog alertDialog = AdvancedSettingsDialog.newInstance(settings);
      	alertDialog.show(fm, "fragment_advanced_settings");
      }

	@Override
	public void onFinishEditDialog(AdvancedSettings settings) {
		this.settings = settings;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(!loading && totalItemCount - firstVisibleItem < VISIBLE_THRESHOLD && params != null) {
			loading = true;
			int pos = (int) (totalItemCount / SIZE);
			params.put("start", pos * SIZE);
			fetchImagesForUrl(API+"?"+params.toString(), pos, false);
		}
	}
}
