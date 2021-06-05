package dr.achim.sleep_timer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import io.flutter.plugins.googlemobileads.GoogleMobileAdsPlugin

class ListTileNativeAdFactory(private val layoutInflater: LayoutInflater) : GoogleMobileAdsPlugin.NativeAdFactory {

    override fun createNativeAd(nativeAd: NativeAd, customOptions: MutableMap<String, Any>?): NativeAdView {

        val nativeAdView = layoutInflater.inflate(R.layout.list_tile_native_ad, null) as NativeAdView

        nativeAdView.setNativeAd(nativeAd)

        // Colors
        var titleTextColor = Color.BLACK
        var subtitleTextColor = Color.DKGRAY
        (customOptions?.get("titleTextColor") as? String)?.let {
            titleTextColor = Color.parseColor(it)
        }
        (customOptions?.get("subtitleTextColor") as? String)?.let {
            subtitleTextColor = Color.parseColor(it)
        }

        // Attribution
        val attributionViewSmall = nativeAdView
            .findViewById(R.id.tv_list_tile_native_ad_attribution_small) as TextView
        val attributionViewLarge = nativeAdView
            .findViewById(R.id.tv_list_tile_native_ad_attribution_large) as TextView

        // Icon
        val iconView = nativeAdView.findViewById(R.id.iv_list_tile_native_ad_icon) as ImageView
        val icon: NativeAd.Image? = nativeAd.icon
        if (icon != null) {
            attributionViewSmall.visibility = View.VISIBLE
            attributionViewLarge.visibility = View.INVISIBLE
            iconView.setImageDrawable(icon.drawable)
        } else {
            attributionViewSmall.visibility = View.INVISIBLE
            attributionViewLarge.visibility = View.VISIBLE
        }
        nativeAdView.iconView = iconView

        // Text
        nativeAdView.headlineView =
            (nativeAdView.findViewById(R.id.tv_list_tile_native_ad_headline) as TextView).apply {
                text = nativeAd.headline
                setTextColor(titleTextColor)
            }
        nativeAdView.bodyView =
            (nativeAdView.findViewById(R.id.tv_list_tile_native_ad_body) as TextView).apply {
                text = nativeAd.body
                setTextColor(subtitleTextColor)
                visibility = if (nativeAd.body != null) View.VISIBLE else View.INVISIBLE
            }

        return nativeAdView
    }

}