package xifopen.noisemap.client.android.UI;

import xifopen.noisemap.client.android.R;
import xifopen.noisemap.client.android.data.LocalService;
import xifopen.noisemap.client.android.data.LocatorAndNoiseMeterImpl;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

public class NoisemapLayout extends RelativeLayout {
	private Activity context;
	private Button clickBtn;
	private boolean isStart = false;
	private static final int MENUITEM = Menu.FIRST;
	
	public NoisemapLayout(Activity context) {
		super(context);
		this.context = context;
	}
	public NoisemapLayout addViewButton(){
		Button btn1 = new Button(context);
		btn1.setText("View Noisemap");
	    btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, ViewActivity.class);
				context.startActivity(intent);
			}
	    });
	    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    params.addRule(RelativeLayout.CENTER_HORIZONTAL, -1);
	    params.addRule(RelativeLayout.CENTER_VERTICAL, -1);
	    btn1.setLayoutParams(params);
	    this.addView(btn1); 
	    return this;
	}
	public Menu alter(Menu menu) {
		menu.clear();
		MenuItem item = menu.add(0, MENUITEM, 0, isStart?"Start measuring noise":"Stop measuring noise");
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				if(isStart)
					context.startService(new Intent(context, LocalService.class));
				else
					context.stopService(new Intent(context, LocalService.class));
				isStart = !isStart;
				return true;
			}
	    });
		return menu;
	}
	
	public NoisemapLayout addImage(){
		WebView webView = new WebView(context);
		/*
		String page = "<html><body><center><img src=\"file:///android_asset/floorplan.png\"/></center></body></html>";
	    webView.loadDataWithBaseURL("fake",page, "text/html", "UTF-8","");
	    */
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setInitialScale(50);
		webView.setWebViewClient(new NoURLwebview());
		webView.loadUrl(LocatorAndNoiseMeterImpl.url+"?isMobile=true");
	    /*
		ImageView i = new ImageView(context);
        i.setImageResource(R.drawable.floorplan);	// currently the image is also in all res/drawable* folders
        i.setAdjustViewBounds(true); // matches the Drawable's dimensions
        i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        */
        this.addView(webView);        
        return this;
	}
    /**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    final class NoURLwebview extends WebViewClient  {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        /*
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d(getClass().getSimpleName(), message);
            result.confirm();
            return true;
        }*/
    }
    public NoisemapLayout addExitButton(){
		Button btn1 = new Button(context);
	    btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				context.finish();
	            System.exit(0);
			}
	    });
	    this.addView(btn1); 
	    return this;
	}
	/**
	public MapView addWorldMap(){
		CloudmadeUtil.retrieveCloudmadeKey(context.getApplicationContext());
		MapView map = new MapView(context, 256);
		GeoPoint RLC = new GeoPoint(46.518473, 6.568338);
		map.getController().setZoom(17);
		map.getController().setCenter(RLC);
		this.addView(map, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		return map;
	}
	public NoisemapLayout addOverlays(MapView map){
		ResourceProxy mResourceProxy = new DefaultResourceProxyImpl(context.getApplicationContext());
		// Create a static ItemizedOverlay showing a some Markers on some cities. 
		final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		items.add(new OverlayItem("Hannover", "SampleDescription", new GeoPoint(52370816,
				9735936))); // Hannover
		items.add(new OverlayItem("Berlin", "SampleDescription", new GeoPoint(52518333,
				13408333))); // Berlin
		items.add(new OverlayItem("Washington", "SampleDescription", new GeoPoint(38895000,
				-77036667))); // Washington
		items.add(new OverlayItem("San Francisco", "SampleDescription", new GeoPoint(37779300,
				-122419200))); // San Francisco

		// OnTapListener for the Markers, shows a simple Toast. 
		ItemizedOverlay<OverlayItem> mMyLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
					@Override
					public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
						Toast.makeText(
								context,
								"Item '" + item.mTitle + "' (index=" + index
										+ ") got single tapped up", Toast.LENGTH_LONG).show();
						return true; // We 'handled' this event.
					}

					@Override
					public boolean onItemLongPress(final int index, final OverlayItem item) {
						Toast.makeText(
								context,
								"Item '" + item.mTitle + "' (index=" + index
										+ ") got long pressed", Toast.LENGTH_LONG).show();
						return false;
					}
				}, mResourceProxy);
		map.getOverlays().add(mMyLocationOverlay);
		return this;
	}*/
}
